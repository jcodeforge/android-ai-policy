package io.github.jcodeforge.aipolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AiPolicy {

    private final Map<String, List<PolicyRule>> rules;

    private AiPolicy(Map<String, List<PolicyRule>> rules) {
        Map<String, List<PolicyRule>> copiedRules = new HashMap<>();

        for (Map.Entry<String, List<PolicyRule>> entry : rules.entrySet()) {
            copiedRules.put(entry.getKey(), List.copyOf(entry.getValue()));
        }

        this.rules = Map.copyOf(copiedRules);
    }

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