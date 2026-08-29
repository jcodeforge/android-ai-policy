package io.github.jcodeforge.aipolicy;

import java.util.Objects;

public final class PolicyResult {

    private final Decision decision;
    private final String reason;

    public PolicyResult(Decision decision, String reason) {
        this.decision = Objects.requireNonNull(decision, "decision must not be null");

        if (decision == Decision.ALLOW && reason != null) {
            throw new IllegalArgumentException("ALLOW must not have a reason");
        }

        if (decision != Decision.ALLOW && reason == null) {
            throw new IllegalArgumentException("A reason is required for " + decision);
        }

        this.reason = reason;
    }

    public boolean isAllowed() {
        return decision == Decision.ALLOW;
    }

    public boolean isDenied() {
        return decision == Decision.DENY;
    }

    public boolean requiresConfirmation() {
        return decision == Decision.REQUIRE_CONFIRMATION;
    }

    public String getReason() {
        return reason;
    }
}