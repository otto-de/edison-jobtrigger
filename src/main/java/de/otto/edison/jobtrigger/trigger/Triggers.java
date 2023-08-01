package de.otto.edison.jobtrigger.trigger;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.time.Duration;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
class Triggers {

    private Triggers() {}

    public static Trigger cronTrigger(final String cron) {
        return new CronTrigger(cron);
    }

    public static Trigger periodicTrigger(final Duration delay) {
        return new PeriodicTrigger(delay.getSeconds() * 1000L);
    }
}
