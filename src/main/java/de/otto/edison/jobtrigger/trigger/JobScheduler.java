package de.otto.edison.jobtrigger.trigger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import static com.google.common.collect.ImmutableSet.copyOf;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * A service that is using all discovered job definitions to schedule triggering of jobs.
 *
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Service
public class JobScheduler {

    @Autowired
    private ThreadPoolTaskScheduler scheduler;
    
    private Map<JobTrigger, ScheduledFuture> taskMap = new HashMap<JobTrigger, ScheduledFuture>();

    private static final Logger LOG = getLogger(JobScheduler.class);

    public void updateTriggers(final List<JobTrigger> jobTriggers) {
    	Set<JobTrigger> deletedTasks = ImmutableSet.copyOf(Sets.difference(taskMap.keySet(), copyOf(jobTriggers)));
    	Set<JobTrigger> newTasks = ImmutableSet.copyOf(Sets.difference(copyOf(jobTriggers), taskMap.keySet()));
    	
    	deletedTasks.forEach(e->stopTrigger(e)); 
    	newTasks.forEach(e->startTrigger(e));
    }

	private void startTrigger(JobTrigger jobTrigger) {
        LOG.info("Start JobTrigger " + jobTrigger.getDefinition());
		ScheduledFuture<?> task = scheduler.schedule(jobTrigger.getRunnable(), jobTrigger.getTrigger());
		taskMap.put(jobTrigger, task);
	}

	private void stopTrigger(JobTrigger jobTrigger) {
        LOG.info("Stop JobTrigger " + jobTrigger.getDefinition());
		taskMap.get(jobTrigger).cancel(false);
		taskMap.remove(jobTrigger);
	}

	public void stopAllTriggers() {
		taskMap.values().stream().forEach(e->e.cancel(false));
		taskMap.clear();		
	}
}
