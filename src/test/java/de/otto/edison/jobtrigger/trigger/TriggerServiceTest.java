package de.otto.edison.jobtrigger.trigger;

import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableList;
import de.otto.edison.jobtrigger.configuration.JobTriggerProperties;
import de.otto.edison.jobtrigger.definition.JobDefinition;
import de.otto.edison.jobtrigger.definition.JobDefinitionBuilder;
import de.otto.edison.jobtrigger.discovery.DiscoveryService;
import de.otto.edison.jobtrigger.security.BasicAuthCredentials;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static de.otto.edison.jobtrigger.trigger.Triggers.cronTrigger;
import static de.otto.edison.jobtrigger.trigger.Triggers.periodicTrigger;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TriggerServiceTest {

    Logger log = LoggerFactory.getLogger(TriggerService.class);

    @Mock
    private DiscoveryService discoveryService;

    @Mock
    private JobScheduler scheduler;

    @Mock
    private AsyncHttpClient httpClient;

    @Mock
    private BasicAuthCredentials basicAuthCredentials;

    @Mock
    private TriggerRunnablesService triggerRunnablesService;

    private TriggerService testee;

    @Captor
    ArgumentCaptor<List<JobTrigger>> listArgumentCaptor;


    @BeforeEach
    public void setUp() {
        testee = new TriggerService(discoveryService, scheduler, httpClient, new JobTriggerProperties(), basicAuthCredentials, triggerRunnablesService);
        reset(discoveryService, scheduler, httpClient);
        testee.postConstruct();
    }

    @Test
    public void shouldStartTriggering() {
        testee.startTriggering();

        assertThat(testee.isStarted(), is(true));
    }

    @Test
    public void shouldNotStopTriggeringIfAlreadyStarted() {
        testee.startTriggering();

        testee.startTriggering();

        verify(scheduler, never()).stopAllTriggers();
    }

    @Test
    public void shouldUpdateTriggersForAllJobDefinitions() {
        final JobDefinition fixedDelayDefinition = new JobDefinitionBuilder().setFixedDelay(Optional.of(Duration.ofDays(2))).createJobDefinition();
        final JobDefinition cronDefinition = new JobDefinitionBuilder().setCron(Optional.of("* * * * * *")).createJobDefinition();
        final JobDefinition noDelayDefinition = new JobDefinitionBuilder().createJobDefinition();
        when(discoveryService.allJobDefinitions()).thenReturn(ImmutableList.of(fixedDelayDefinition, cronDefinition, noDelayDefinition));
        final Runnable expectedRunnable = () -> {
        };
        when(triggerRunnablesService.httpTriggerRunnable(any(JobDefinition.class), any(TriggerResponseConsumer.class))).thenReturn(expectedRunnable);

        testee.startTriggering();

        verify(scheduler).updateTriggers(listArgumentCaptor.capture());
        assertJobTriggerEquality(listArgumentCaptor.getValue(),
                ImmutableList.of(
                        new JobTrigger(
                                fixedDelayDefinition,
                                periodicTrigger(fixedDelayDefinition.getFixedDelay().get()),
                                expectedRunnable),
                        new JobTrigger(
                                cronDefinition,
                                cronTrigger(cronDefinition.getCron().get()),
                                expectedRunnable)
                ));
    }

    @Test
    public void shouldNotFailIfSingleCronExpressionIsBroken() {
        final JobDefinition fixedDelayDefinition = new JobDefinitionBuilder().setFixedDelay(Optional.of(Duration.ofDays(2))).createJobDefinition();
        final JobDefinition brokenCronDefinition = new JobDefinitionBuilder().setCron(Optional.of("BÄM!")).createJobDefinition();
        final JobDefinition noDelayDefinition = new JobDefinitionBuilder().createJobDefinition();
        when(discoveryService.allJobDefinitions()).thenReturn(ImmutableList.of(fixedDelayDefinition, brokenCronDefinition, noDelayDefinition));

        testee.startTriggering();

        verify(scheduler).updateTriggers(listArgumentCaptor.capture());
        assertThat(listArgumentCaptor.getValue(), hasSize(2));
    }

    @Test
    public void shouldThrowExceptionDuringExecutionIfCronExpressionIsBroken() {
        final JobDefinition brokenCronDefinition = new JobDefinitionBuilder().setCron(Optional.of("BÄM!")).createJobDefinition();
        when(discoveryService.allJobDefinitions()).thenReturn(ImmutableList.of(brokenCronDefinition));

        testee.startTriggering();
        verify(scheduler).updateTriggers(listArgumentCaptor.capture());
        listArgumentCaptor.getValue().get(0).getRunnable().run();
        assertThat(testee.getLastResults().get(0).failed(), is(true));
    }

    @Test
    public void shouldAddSuccessfulResponseAsLastResult() throws Exception {
        final Response responseMock = mock(Response.class);
        final JobDefinition jobDefinition = mock(JobDefinition.class);
        final TriggerService.DefaultTriggerResponseConsumer responseConsumer = testee.new DefaultTriggerResponseConsumer(jobDefinition);

        when(responseMock.getStatusCode()).thenReturn(200);

        responseConsumer.consume(responseMock);

        final TriggerResult triggerResult = testee.getLastResults().get(0);
        assertThat(testee.getLastResults(), hasSize(1));
        assertThat(triggerResult.getJobDefinition(), is(jobDefinition));
        assertThat(triggerResult.getTriggerStatus().getState(), is(TriggerStatus.State.OK));
    }

    @Test
    public void shouldRemoveLastResultsIfMoreThanMaxResults() throws Exception {
        for (int i = 0; i < testee.getMaxJobResults() + 10; i++) {
            final Response responseMock = mock(Response.class);
            final JobDefinition jobDefinition = mock(JobDefinition.class);
            final TriggerService.DefaultTriggerResponseConsumer responseConsumer = testee.new DefaultTriggerResponseConsumer(jobDefinition);

            when(responseMock.getStatusCode()).thenReturn(200);

            responseConsumer.consume(responseMock);
        }

        assertThat(testee.getLastResults(), hasSize(testee.getMaxJobResults()));
    }

    @Test
    public void shouldAddThrowableAsLastResult() throws Exception {
        final Exception throwable = new Exception("some message");
        final JobDefinition jobDefinition = mock(JobDefinition.class);
        final TriggerService.DefaultTriggerResponseConsumer responseConsumer = testee.new DefaultTriggerResponseConsumer(jobDefinition);

        responseConsumer.consume(throwable);

        final TriggerResult triggerResult = testee.getLastResults().get(0);
        assertThat(testee.getLastResults(), hasSize(1));
        assertThat(triggerResult.getJobDefinition(), is(jobDefinition));
        assertThat(triggerResult.getTriggerStatus().getMessage(), is("some message"));
        assertThat(triggerResult.getTriggerStatus().getState(), is(TriggerStatus.State.FAILED));
    }

    @Test
    public void shouldAddConnectExceptionAsLastResult() throws Exception {
        final Exception throwable = new ConnectException("some connect exception");
        final JobDefinition jobDefinition = mock(JobDefinition.class);
        final TriggerService.DefaultTriggerResponseConsumer responseConsumer = testee.new DefaultTriggerResponseConsumer(jobDefinition);


        responseConsumer.consume(throwable);

        final TriggerResult triggerResult = testee.getLastResults().get(0);
        assertThat(testee.getLastResults(), hasSize(1));
        assertThat(triggerResult.getJobDefinition(), is(jobDefinition));
        assertThat(triggerResult.getTriggerStatus().getMessage(), is("Connection Refused"));
        assertThat(triggerResult.getTriggerStatus().getState(), is(TriggerStatus.State.FAILED));
    }

    private void assertJobTriggerEquality(final List<JobTrigger> actual, final List<JobTrigger> expected) {
        assertThat(actual.size(), is(expected.size()));
        final Iterator<JobTrigger> actualTriggerIterator = actual.iterator();
        final Iterator<JobTrigger> expectedTriggerIterator = expected.iterator();
        while (expectedTriggerIterator.hasNext()) {
            assertThat(jobTriggerEquivalence.equivalent(expectedTriggerIterator.next(), actualTriggerIterator.next()), is(true));
        }
    }

    private final Equivalence<JobTrigger> jobTriggerEquivalence = new Equivalence<JobTrigger>() {
        @Override
        protected boolean doEquivalent(final JobTrigger a, final JobTrigger b) {
            final JobTrigger that = (JobTrigger) b;

            if (a.getDefinition() != null ? !a.getDefinition().equals(that.getDefinition()) : that.getDefinition() != null)
                return false;
            if (a.getTrigger() != null ? !a.getTrigger().equals(that.getTrigger()) : that.getTrigger() != null)
                return false;
            return !(a.getRunnable() != null ? !a.getRunnable().equals(that.getRunnable()) : that.getRunnable() != null);
        }

        @Override
        protected int doHash(final JobTrigger jobTrigger) {
            int result = jobTrigger.getDefinition() != null ? jobTrigger.getDefinition().hashCode() : 0;
            result = 31 * result + (jobTrigger.getTrigger() != null ? jobTrigger.getTrigger().hashCode() : 0);
            result = 31 * result + (jobTrigger.getRunnable() != null ? jobTrigger.getRunnable().hashCode() : 0);
            return result;
        }
    };

}
