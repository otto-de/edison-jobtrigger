package de.otto.edison.jobtrigger.trigger;

import com.google.common.collect.ImmutableList;
import de.otto.edison.jobtrigger.definition.JobDefinition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


import static org.mockito.Mockito.*;

public class JobSchedulerTest {

    @Mock
    private ThreadPoolTaskScheduler scheduler;

    @InjectMocks
    private JobScheduler testee;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShouldScheduleAllTriggers() {
        Runnable myRunnable = () -> {};
        Trigger myTrigger = mock(Trigger.class);
        JobDefinition definition = mock(JobDefinition.class);
        JobTrigger parameter = new JobTrigger(definition, myTrigger, myRunnable);

        testee.updateTriggers(ImmutableList.of(parameter, parameter));

        verify(scheduler, times(2)).schedule(myRunnable, myTrigger);
    }
}