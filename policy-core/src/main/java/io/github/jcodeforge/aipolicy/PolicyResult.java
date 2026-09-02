package io.github.jcodeforge.aipolicy;

import java.util.Objects;

/**
 * <p>A policy result contains the outcome of the evaluation and, for
 * non-allowed decisions, a reason describing why the action was not
 * immediately allowed.</p>
 *
 * <p>The result is immutable and provides convenience methods for checking
 * the evaluation outcome without requiring callers to work directly with
 * {@link Decision}.</p>
 */
public final class PolicyResult {

    private final Decision decision;
    private final String reason;

    /**
     * Creates a policy result.
     *
     * @param decision the outcome of the policy evaluation
     * @param reason the reason for a denied or confirmation-required action;
     *               must be {@code null} for an allowed action
     * @throws NullPointerException if {@code decision} is {@code null}
     * @throws IllegalArgumentException if an allowed result has a reason or
     *                                  a non-allowed result has no reason
     */
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