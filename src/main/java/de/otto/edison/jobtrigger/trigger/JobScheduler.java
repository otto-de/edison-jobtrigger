package de.otto.edison.jobtrigger.trigger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void updateTriggers(final List<JobTrigger> jobTriggers) {
        stopAllTriggers();
        scheduler.initialize();
        for (final JobTrigger jobTrigger : jobTriggers) {
            scheduler.schedule(
                    jobTrigger.getRunnable(),
                    jobTrigger.getTrigger());
        }
    }

    public void stopAllTriggers() {
        scheduler.destroy();
    }
}
