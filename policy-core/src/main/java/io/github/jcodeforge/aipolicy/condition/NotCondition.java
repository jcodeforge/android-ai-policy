package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;
import java.util.Objects;

public final class NotCondition implements PolicyCondition {

    private final PolicyCondition condition;

    public NotCondition(PolicyCondition condition) {
        this.condition = Objects.requireNonNull(condition, "condition must not be null");
    }

    @Override
    public boolean matches(ActionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        return !condition.matches(context);
    }
}