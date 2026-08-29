package io.github.jcodeforge.aipolicy;

import java.util.Objects;

public final class ActionContext {

    private final String capability;
    private final String caller;
    private final boolean userInitiated;

    public ActionContext(String capability, String caller, boolean userInitiated) {
        this.capability = Objects.requireNonNull(capability, "capability must not be null");
        this.caller = Objects.requireNonNull(caller, "caller must not be null");

        if (capability.trim().isEmpty()) {
            throw new IllegalArgumentException("capability must not be blank");
        }

        if (caller.trim().isEmpty()) {
            throw new IllegalArgumentException("caller must not be blank");
        }

        this.userInitiated = userInitiated;
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
}