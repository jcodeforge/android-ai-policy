package io.github.jcodeforge.aipolicy;

import java.util.Objects;

public final class ActionContext {

    private final String capability;
    private final CallerIdentity callerIdentity;
    private final boolean userInitiated;
    private final ApplicationState applicationState;

    /**
     * Creates an action context.
     *
     * @param capability the capability being requested
     * @param callerIdentity the caller requesting the capability
     * @param userInitiated whether the action was explicitly initiated by the user
     * @param applicationState the application state when the action was requested
     * @throws NullPointerException if {@code capability}, {@code caller} is {@code null}
     * @throws IllegalArgumentException if {@code capability} or {@code caller}
     *                                  is blank
     */
    public ActionContext(String capability, CallerIdentity callerIdentity, boolean userInitiated,
                         ApplicationState applicationState) {
        this.capability = Objects.requireNonNull(capability, "capability must not be null");
        this.callerIdentity = Objects.requireNonNull(callerIdentity,
                "callerIdentity  must not be null");

        if (capability.trim().isEmpty()) {
            throw new IllegalArgumentException("capability must not be blank");
        }

        this.userInitiated = userInitiated;
        this.applicationState = applicationState;
    }

    public ActionContext(String capability, CallerIdentity callerIdentity, boolean userInitiated) {
        this(capability, callerIdentity, userInitiated, null);
    }

    public String getCapability() {
        return capability;
    }

    public CallerIdentity getCallerIdentity() {
        return callerIdentity;
    }

    public boolean isUserInitiated() {
        return userInitiated;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }
}