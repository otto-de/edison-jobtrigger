package de.otto.edison.jobtrigger.trigger;

import org.asynchttpclient.Response;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
interface TriggerResponseConsumer {

    public void consume(final Response response);

    public void consume(final Throwable throwable);

}
