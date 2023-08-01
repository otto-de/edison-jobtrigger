package de.otto.edison.jobtrigger.trigger;

import static jakarta.servlet.http.HttpServletResponse.*;

/**
 * @author Guido Steinacker
 * @since 06.09.15
 */
public class TriggerStatus {

    public enum State {
        OK,
        FAILED,
        BLOCKED
    }
    private static final String TRIGGERED = "triggered";
    private static final String BLOCKED = "blocked";
    private static final String NOT_FOUND = "not found";
    private static final String FAILED = "failed with http ";

    private final String message;
    private final State state;

    private TriggerStatus(final State state, final String message) {
        this.state = state;
        this.message = message;
    }

    public static TriggerStatus fromHttpStatus(final int statusCode) {
        final String message;
        final State state;
        switch (statusCode) {
            case SC_OK:
            case SC_CREATED:
            case SC_ACCEPTED:
            case SC_NO_CONTENT:
                message = TRIGGERED;
                state = State.OK;
                break;
            case SC_CONFLICT:
                message = BLOCKED;
                state = State.BLOCKED;
                break;
            case SC_NOT_FOUND:
                message = NOT_FOUND;
                state = State.FAILED;
                break;
            default:
                message = FAILED + statusCode;
                state = State.FAILED;
        }
        return new TriggerStatus(state, message);
    }

    public static TriggerStatus fromMessage(final String message) {
        return new TriggerStatus(State.FAILED, message);
    }
    public String getMessage() {
        return message;
    }

    public State getState() {
        return state;
    }
}
