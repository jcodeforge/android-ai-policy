package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;

public interface PolicyCondition {

    boolean matches(ActionContext context);
}
