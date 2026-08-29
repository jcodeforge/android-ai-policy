package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class OrCondition implements PolicyCondition {

    private final List<PolicyCondition> conditions;

    public OrCondition(PolicyCondition... conditions) {
        Objects.requireNonNull(conditions, "conditions must not be null");

        if (conditions.length == 0) {
            throw new IllegalArgumentException("at least one condition is required");
        }

        this.conditions = Arrays.asList(conditions);

        for (PolicyCondition condition : this.conditions) {
            Objects.requireNonNull(condition, "condition must not be null");
        }
    }

    @Override
    public boolean matches(ActionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        for (PolicyCondition condition : conditions) {
            if (condition.matches(context)) {
                return true;
            }
        }

        return false;
    }
}