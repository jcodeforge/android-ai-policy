package io.github.jcodeforge.aipolicy;

import java.util.Objects;
import io.github.jcodeforge.aipolicy.condition.AlwaysCondition;
import io.github.jcodeforge.aipolicy.condition.PolicyCondition;

/**
 * Defines a single rule within an {@link AiPolicy}.
 *
 * <p>A policy rule specifies a decision, an optional condition, and a reason
 * for decisions that do not immediately allow an action.</p>
 *
 * <p>A rule matches an {@link ActionContext} only when its
 * {@link PolicyCondition} matches the context.</p>
 *
 * <p>Rules are evaluated in the order in which they are registered with
 * {@link AiPolicy.Builder}. When multiple rules apply to the same capability,
 * the first matching rule determines the result.</p>
 */
public final class PolicyRule {

    private final Decision decision;
    private final String reason;
    private final PolicyCondition condition;

    /**
     * Creates a conditional policy rule.
     *
     * @param decision the decision to return when the rule matches
     * @param reason the reason for a denied or confirmation-required decision;
     *               must be {@code null} for {@link Decision#ALLOW}
     * @param condition the condition that determines whether this rule matches
     * @throws NullPointerException if {@code decision} is {@code null}
     * @throws IllegalArgumentException if an allowed rule has a reason or
     *                                  a non-allowed rule has no reason
     */
    public PolicyRule(Decision decision, String reason, PolicyCondition condition) {
        this.decision = Objects.requireNonNull(decision, "decision must not be null");

        if (decision == Decision.ALLOW && reason != null) {
            throw new IllegalArgumentException("ALLOW must not have a reason");
        }

        if (decision != Decision.ALLOW && reason == null) {
            throw new IllegalArgumentException("A reason is required for " + decision);
        }

        this.reason = reason;
        this.condition = condition;
    }

    public PolicyRule(Decision decision, String reason) {
        this(decision, reason, new AlwaysCondition());
    }

    public Decision getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }

    public PolicyCondition getCondition() {
        return condition;
    }

    /**
     * Determines whether this rule applies to the supplied action context.
     *
     * @param context the context to evaluate
     * @return {@code true} if the rule's condition matches the context
     * @throws NullPointerException if {@code context} is {@code null}
     */
    public boolean matches(ActionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        return condition.matches(context);
    }
}