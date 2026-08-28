package io.github.jcodeforge.aipolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class AiPolicy {

    private final Map<String, PolicyRule> rules;

    private AiPolicy(Map<String, PolicyRule> rules) {
        this.rules = Map.copyOf(rules);
    }

    public PolicyRule evaluate(ActionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        PolicyRule rule = rules.get(context.getCapability());

        if (rule == null) {
            return new PolicyRule(Decision.DENY, "No policy rule exists for capability: "
                    + context.getCapability());
        }

        return rule;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<String, PolicyRule> rules = new HashMap<>();

        public Builder addRule(String capability, PolicyRule rule) {
            Objects.requireNonNull(capability, "capability must not be null");
            Objects.requireNonNull(rule, "rule must not be null");

            if (capability.trim().isEmpty()) {
                throw new IllegalArgumentException("capability must not be blank");
            }

            if (rules.containsKey(capability)) {
                throw new IllegalArgumentException("A policy rule already exists for capability: "
                        + capability);
            }

            rules.put(capability, rule);

            return this;
        }

        public AiPolicy build() {
            return new AiPolicy(rules);
        }
    }
}