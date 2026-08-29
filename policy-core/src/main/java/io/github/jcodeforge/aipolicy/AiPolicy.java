package io.github.jcodeforge.aipolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Defines and evaluates policies for AI-initiated actions.
 *
 * <p>Policies consist of one or more {@link PolicyRule} instances associated
 * with a capability. Multiple rules may be registered for the same capability.
 * Rules are evaluated in registration order, and the first matching rule
 * determines the result.</p>
 *
 * <p>If no rule exists for a capability, or if none of the rules for a
 * capability match the supplied {@link ActionContext}, evaluation fails
 * closed and returns a denied {@link PolicyResult}.</p>
 *
 * <p>An {@code AiPolicy} is immutable after construction and can safely be
 * reused for multiple evaluations.</p>
 */
public final class AiPolicy {

    private final Map<String, List<PolicyRule>> rules;

    private AiPolicy(Map<String, List<PolicyRule>> rules) {
        Map<String, List<PolicyRule>> copiedRules = new HashMap<>();

        for (Map.Entry<String, List<PolicyRule>> entry : rules.entrySet()) {
            copiedRules.put(entry.getKey(), List.copyOf(entry.getValue()));
        }

        this.rules = Map.copyOf(copiedRules);
    }

    /**
     * Evaluates the policy for the supplied action context.
     *
     * <p>Rules associated with the context's capability are evaluated in
     * registration order. The first rule whose condition matches determines
     * the result.</p>
     *
     * <p>If no rule exists for the capability, the result is
     * {@link Decision#DENY}. If rules exist but none of their conditions
     * match, the result is also {@link Decision#DENY}.</p>
     *
     * @param context the context of the action being evaluated
     * @return the result of the policy evaluation
     * @throws NullPointerException if {@code context} is {@code null}
     */
    public PolicyResult evaluate(ActionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        List<PolicyRule> capabilityRules = rules.get(context.getCapability());

        if (capabilityRules == null) {
            return new PolicyResult(Decision.DENY, "No policy rule exists for capability: "
                    + context.getCapability());
        }

        for (PolicyRule rule : capabilityRules) {
            if (rule.matches(context)) {
                return new PolicyResult(rule.getDecision(), rule.getReason());
            }
        }

        return new PolicyResult(Decision.DENY, "No policy condition was satisfied");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<String, List<PolicyRule>> rules = new HashMap<>();

        /**
         * Adds a policy rule for a capability.
         *
         * <p>Multiple rules may be registered for the same capability.
         * Rules are evaluated in the order in which they are added.</p>
         *
         * @param capability the capability to which the rule applies
         * @param rule the policy rule
         * @return this builder
         * @throws NullPointerException if {@code capability} or {@code rule}
         *                              is {@code null}
         * @throws IllegalArgumentException if {@code capability} is blank
         */
        public Builder addRule(String capability, PolicyRule rule) {
            Objects.requireNonNull(capability, "capability must not be null");
            Objects.requireNonNull(rule, "rule must not be null");

            if (capability.trim().isEmpty()) {
                throw new IllegalArgumentException("capability must not be blank");
            }

            rules.computeIfAbsent(capability, key -> new ArrayList<>()).add(rule);

            return this;
        }

        public AiPolicy build() {
            return new AiPolicy(rules);
        }
    }
}