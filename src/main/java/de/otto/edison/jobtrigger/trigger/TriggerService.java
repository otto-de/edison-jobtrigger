package de.otto.edison.jobtrigger.trigger;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import de.otto.edison.jobtrigger.definition.JobDefinition;
import de.otto.edison.jobtrigger.discovery.DiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static de.otto.edison.jobtrigger.trigger.TriggerRunnables.httpTriggerRunnable;
import static de.otto.edison.jobtrigger.trigger.Triggers.periodicTrigger;
import static java.lang.String.valueOf;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Service
public class TriggerService {

    private static final Logger LOG = getLogger(TriggerService.class);

    @Autowired
    private DiscoveryService discoveryService;
    @Autowired
    private JobScheduler scheduler;
    @Autowired
    private AsyncHttpClient httpClient;
    private final List<TriggerResult> lastResult = new CopyOnWriteArrayList<>();
    private AtomicBoolean isStarted = new AtomicBoolean(false);
    private AtomicLong currentIndex = new AtomicLong(0);

    public void startTriggering() {
        if (isStarted()) {
            stopTriggering();
        }
        final List<JobDefinition> jobDefinitions = discoveryService.dicoveredJobDefinitions();
        scheduler.updateTriggers(jobDefinitions
                .stream()
                .map(def -> new JobTrigger(def, triggerFor(def), runnableFor(def)))
                .collect(toList()));
        isStarted.set(true);
    }

    public void stopTriggering() {
        scheduler.stopAllTriggers();
        isStarted.set(false);
    }

    public boolean isStarted() {
        return isStarted.get();
    }

    public List<TriggerResult> getLastResults() {
        return unmodifiableList(lastResult);
    }

    private Runnable runnableFor(final JobDefinition jobDefinition) {
        return httpTriggerRunnable(httpClient, jobDefinition, response -> {
            int statusCode = response.getStatusCode();
            String location = response.getHeader("Location");
            lastResult.add(0, new TriggerResult(valueOf(currentIndex.addAndGet(1)), statusCode, location, jobDefinition));
            if (lastResult.size() > 1000) {
                lastResult.remove(1000);
            }
            LOG.info("Triggered {}: status = {}, location = {}", jobDefinition.getTriggerUrl(), statusCode, location);
        });
    }

    private Trigger triggerFor(final JobDefinition jobDefinition) {
        if (jobDefinition.getFixedDelay().isPresent()) {
            return periodicTrigger(jobDefinition.getFixedDelay().get());
        } else if (jobDefinition.getCron().isPresent()) {
            return Triggers.cronTrigger(jobDefinition.getCron().get());
        } else {
            LOG.warn("No Trigger found for job definition " + jobDefinition.getTriggerUrl());
            return triggerContext -> null;
        }
    }
}
