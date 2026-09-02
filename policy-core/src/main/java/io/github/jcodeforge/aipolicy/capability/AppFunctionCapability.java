package io.github.jcodeforge.aipolicy.capability;

import java.util.Objects;

public final class AppFunctionCapability {

    private final String functionId;
    private final Capability capability;

    public AppFunctionCapability(String functionId, Capability capability) {
        this.functionId = Objects.requireNonNull(
                functionId, "functionId must not be null");
        this.capability = Objects.requireNonNull(
                capability, "capability must not be null");

        if (functionId.trim().isEmpty()) {
            throw new IllegalArgumentException("functionId must not be blank");
        }
    }

    public String getFunctionId() {
        return functionId;
    }

    public Capability getCapability() {
        return capability;
    }
}