package de.otto.edison.jobtrigger.trigger;

import com.google.common.collect.ImmutableList;
import de.otto.edison.jobtrigger.definition.JobDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobSchedulerTest {

    @Mock
    private ThreadPoolTaskScheduler scheduler;

    @InjectMocks
    private JobScheduler testee;

    @Test
    public void testShouldScheduleAllTriggers() {
        Runnable myRunnable = () -> {};
        Trigger myTrigger1 = mock(Trigger.class);
        JobDefinition definition1 = mock(JobDefinition.class);
        Trigger myTrigger2 = mock(Trigger.class);
        JobDefinition definition2 = mock(JobDefinition.class);
        JobTrigger trigger1 = new JobTrigger(definition1, myTrigger1, myRunnable);
        JobTrigger trigger2 = new JobTrigger(definition2, myTrigger2, myRunnable);

        testee.updateTriggers(ImmutableList.of(trigger1, trigger2));

        verify(scheduler, times(1)).schedule(myRunnable, myTrigger1);
        verify(scheduler, times(1)).schedule(myRunnable, myTrigger2);
    }
    
    @Test
    public void testShouldAddTriggers() {
        Runnable myRunnable = () -> {};
        Trigger myTrigger1 = mock(Trigger.class);
        JobDefinition definition1 = mock(JobDefinition.class);
        Trigger myTrigger2 = mock(Trigger.class);
        JobDefinition definition2 = mock(JobDefinition.class);
        JobTrigger trigger1 = new JobTrigger(definition1, myTrigger1, myRunnable);
        JobTrigger trigger2 = new JobTrigger(definition2, myTrigger2, myRunnable);

        testee.updateTriggers(ImmutableList.of(trigger1));

        verify(scheduler, times(1)).schedule(myRunnable, myTrigger1);
        
        testee.updateTriggers(ImmutableList.of(trigger1, trigger2));
     
        verify(scheduler, times(1)).schedule(myRunnable, myTrigger2);
        verify(scheduler, times(1)).schedule(myRunnable, myTrigger2);
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void testShouldRemoveTriggers() {
        Runnable myRunnable = () -> {};
        Trigger myTrigger1 = mock(Trigger.class);
        JobDefinition definition1 = mock(JobDefinition.class);
        Trigger myTrigger2 = mock(Trigger.class);
        JobDefinition definition2 = mock(JobDefinition.class);
        JobTrigger trigger1 = new JobTrigger(definition1, myTrigger1, myRunnable);
        JobTrigger trigger2 = new JobTrigger(definition2, myTrigger2, myRunnable);

        ScheduledFuture task1 = mock(ScheduledFuture.class);
        ScheduledFuture task2 = mock(ScheduledFuture.class);
        
        when(scheduler.schedule(myRunnable, myTrigger1)).thenReturn(task1);
        when(scheduler.schedule(myRunnable, myTrigger2)).thenReturn(task2);
        testee.updateTriggers(ImmutableList.of(trigger1, trigger2));

        verify(scheduler, times(1)).schedule(myRunnable, myTrigger1);
        verify(scheduler, times(1)).schedule(myRunnable, myTrigger2);

        testee.updateTriggers(ImmutableList.of(trigger2));
        
        verify(task1, times(1)).cancel(false);
        verify(task2, never()).cancel(false);
        verifyNoMoreInteractions(scheduler);
    }

}
