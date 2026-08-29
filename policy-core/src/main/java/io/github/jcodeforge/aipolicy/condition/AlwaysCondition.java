package io.github.jcodeforge.aipolicy.condition;

import java.util.Objects;
import io.github.jcodeforge.aipolicy.ActionContext;

public final class AlwaysCondition implements PolicyCondition {

    @Override
    public boolean matches(ActionContext context) {
        Objects.requireNonNull(context, "context must not be null");
        return true;
    }
}
