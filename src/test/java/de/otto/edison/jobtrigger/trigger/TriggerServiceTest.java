package de.otto.edison.jobtrigger.trigger;

import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableList;
import com.ning.http.client.AsyncHttpClient;
import de.otto.edison.jobtrigger.definition.JobDefinition;
import de.otto.edison.jobtrigger.definition.JobDefinitionBuilder;
import de.otto.edison.jobtrigger.discovery.DiscoveryService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static de.otto.edison.jobtrigger.trigger.Triggers.cronTrigger;
import static de.otto.edison.jobtrigger.trigger.Triggers.periodicTrigger;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest(TriggerRunnables.class)
@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
public class TriggerServiceTest {

    Logger log = LoggerFactory.getLogger(TriggerService.class);

    @Mock
    private DiscoveryService discoveryService;

    @Mock
    private JobScheduler scheduler;

    @Mock
    private AsyncHttpClient httpClient;

    @InjectMocks
    private TriggerService testee;

    @Captor
    ArgumentCaptor<List<JobTrigger>> listArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        reset(discoveryService, scheduler, httpClient);
        testee.postConstruct();
        PowerMockito.mockStatic(TriggerRunnables.class);
    }

    @Test
    public void shouldStartTriggering() {
        testee.startTriggering();

        assertThat(testee.isStarted(), is(true));
    }

    @Test
    public void shouldStopTriggeringIfAlreadyStarted() {
        testee.startTriggering();

        testee.startTriggering();

        verify(scheduler).stopAllTriggers();
    }

    @Test
    public void shouldUpdateTriggersForAllJobDefinitions() {
        JobDefinition fixedDelayDefinition = new JobDefinitionBuilder().setFixedDelay(Optional.of(Duration.ofDays(2))).createJobDefinition();
        JobDefinition cronDefinition = new JobDefinitionBuilder().setCron(Optional.of("* * * * * *")).createJobDefinition();
        JobDefinition noDelayDefinition = new JobDefinitionBuilder().createJobDefinition();
        when(discoveryService.allJobDefinitions()).thenReturn(ImmutableList.of(fixedDelayDefinition, cronDefinition, noDelayDefinition));
        Runnable expectedRunnable = () -> {};
        when(TriggerRunnables.httpTriggerRunnable(eq(httpClient), any(JobDefinition.class), any(TriggerResponseConsumer.class))).thenReturn(expectedRunnable);

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

    private void assertJobTriggerEquality(List<JobTrigger> actual, List<JobTrigger> expected) {
        assertThat(actual.size(), is(expected.size()));
        Iterator<JobTrigger> actualTriggerIterator = actual.iterator();
        Iterator<JobTrigger> expectedTriggerIterator = expected.iterator();
        while(expectedTriggerIterator.hasNext()) {
            assertThat(jobTriggerEquivalence.equivalent(expectedTriggerIterator.next(), actualTriggerIterator.next()), is(true));
        }
    }

    private Equivalence<JobTrigger> jobTriggerEquivalence = new Equivalence<JobTrigger>() {
        @Override
        protected boolean doEquivalent(JobTrigger a, JobTrigger b) {
            JobTrigger that = (JobTrigger) b;

            if (a.getDefinition() != null ? !a.getDefinition().equals(that.getDefinition()) : that.getDefinition() != null)
                return false;
            if (a.getTrigger() != null ? !a.getTrigger().equals(that.getTrigger()) : that.getTrigger() != null)
                return false;
            return !(a.getRunnable() != null ? !a.getRunnable().equals(that.getRunnable()) : that.getRunnable() != null);
        }

        @Override
        protected int doHash(JobTrigger jobTrigger) {
            int result = jobTrigger.getDefinition() != null ? jobTrigger.getDefinition().hashCode() : 0;
            result = 31 * result + (jobTrigger.getTrigger() != null ? jobTrigger.getTrigger().hashCode() : 0);
            result = 31 * result + (jobTrigger.getRunnable() != null ? jobTrigger.getRunnable().hashCode() : 0);
            return result;
        }
    };
}