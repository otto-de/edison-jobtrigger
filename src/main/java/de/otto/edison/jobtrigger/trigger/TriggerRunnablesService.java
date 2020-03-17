package de.otto.edison.jobtrigger.trigger;

import de.otto.edison.jobtrigger.definition.JobDefinition;
import de.otto.edison.jobtrigger.security.AuthProvider;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static de.otto.edison.jobtrigger.security.BasicAuthCredentials.AUTHORIZATION_HEADER;
import static java.lang.Thread.sleep;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Service
class TriggerRunnablesService {

    private final AuthProvider authHeaderProvider;
    private final AsyncHttpClient httpClient;

    TriggerRunnablesService(AuthProvider authHeaderProvider, AsyncHttpClient httpClient) {
        this.authHeaderProvider = authHeaderProvider;
        this.httpClient = httpClient;
    }
    public Runnable httpTriggerRunnable(
            final JobDefinition jobDefinition,
            final TriggerResponseConsumer consumer) {
        return () -> {
            final Logger LOG = LoggerFactory.getLogger("de.otto.edison.jobtrigger.trigger.HttpTriggerRunnable");
            final String triggerUrl = jobDefinition.getTriggerUrl();
            try {
                for (int i = 0, n = jobDefinition.getRetries() + 1; i < n; ++i) {
                    BoundRequestBuilder boundRequestBuilder = httpClient.preparePost(triggerUrl);
                    authHeaderProvider.setAuthHeader(boundRequestBuilder);
                    final ListenableFuture<Response> futureResponse = boundRequestBuilder.execute(new AsyncCompletionHandler<Response>() {
                        @Override
                        public Response onCompleted(final Response response) throws Exception {
                            final String location = response.getHeader("Location");
                            final int status = response.getStatusCode();
                            consumer.consume(response);
                            return response;
                        }

                        @Override
                        public void onThrowable(Throwable t) {
                            consumer.consume(t);
                        }
                    });

                    if (futureResponse.get().getStatusCode() < 300) {
                        return;
                    } else {
                        if (i < (jobDefinition).getRetries()) {
                            if (jobDefinition.getRetryDelay().isPresent()) {
                                final Duration duration = jobDefinition.getRetryDelay().get();
                                LOG.info("Retrying trigger in " + duration.getSeconds() + "s");
                                sleep(duration.toMillis());
                            }
                            LOG.info("Trigger failed. Retry " + jobDefinition.getJobType() + "[" + (i + 1) + "/" + jobDefinition.getRetries() + "]");
                        } else {
                            LOG.info("Trigger failed. No more retries.");
                        }
                    }
                }
            } catch (final Exception e) {
                LOG.error("Exception caught when trying to trigger '{}': {}", triggerUrl, e.getMessage());
            }
        };
    }
}

