package de.otto.edison.jobtrigger.trigger;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Guido Steinacker
 * @since 06.09.15
 */
public class TriggerStatus {
    public static final String TRIGGERED = "triggered";
    public static final String BLOCKED = "blocked";
    public static final String NOT_FOUND = "not found";
    public static final String FAILED = "failed with http ";

    private final String message;
    private final int statusCode;

    public TriggerStatus(final int statusCode) {
        this.statusCode = statusCode;
        switch (statusCode) {
            case HttpServletResponse.SC_OK:
            case HttpServletResponse.SC_CREATED:
            case HttpServletResponse.SC_ACCEPTED:
            case HttpServletResponse.SC_NO_CONTENT:
                this.message = TRIGGERED;
                break;
            case HttpServletResponse.SC_CONFLICT:
                this.message = BLOCKED;
                break;
            case HttpServletResponse.SC_NOT_FOUND:
                this.message = NOT_FOUND;
                break;
            default:
                this.message = FAILED + statusCode;
        }
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
