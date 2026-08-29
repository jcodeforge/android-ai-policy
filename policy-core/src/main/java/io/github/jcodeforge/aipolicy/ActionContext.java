package io.github.jcodeforge.aipolicy;

import java.util.Objects;

/**
 * Describes the context in which an action is requested.
 *
 * <p>An action context is supplied to {@link AiPolicy} during policy
 * evaluation and provides the information that policy conditions can
 * inspect.</p>
 *
 * <p>An {@code ActionContext} is immutable after construction.</p>
 */
public final class ActionContext {

    private final String capability;
    private final String caller;
    private final boolean userInitiated;
    private final ApplicationState applicationState;

    /**
     * Creates an action context.
     *
     * @param capability the capability being requested
     * @param caller the caller requesting the capability
     * @param userInitiated whether the action was explicitly initiated by the user
     * @param applicationState the application state when the action was requested
     * @throws NullPointerException if {@code capability}, {@code caller},
     *                              or {@code applicationState} is {@code null}
     * @throws IllegalArgumentException if {@code capability} or {@code caller}
     *                                  is blank
     */
    public ActionContext(String capability, String caller, boolean userInitiated,
                         ApplicationState applicationState) {
        this.capability = Objects.requireNonNull(capability, "capability must not be null");
        this.caller = Objects.requireNonNull(caller, "caller must not be null");
        this.applicationState = Objects.requireNonNull(applicationState,
                "applicationState must not be null");

        if (capability.trim().isEmpty()) {
            throw new IllegalArgumentException("capability must not be blank");
        }

        if (caller.trim().isEmpty()) {
            throw new IllegalArgumentException("caller must not be blank");
        }

        this.userInitiated = userInitiated;
    }

    public ActionContext(String capability, String caller, boolean userInitiated) {
        this(capability, caller, userInitiated, ApplicationState.FOREGROUND);
    }

    public String getCapability() {
        return capability;
    }

    public String getCaller() {
        return caller;
    }

    public boolean isUserInitiated() {
        return userInitiated;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }
}