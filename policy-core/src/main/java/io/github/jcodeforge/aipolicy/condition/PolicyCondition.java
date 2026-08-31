package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;

/**
 * Defines a condition that can be evaluated against an {@link ActionContext}.
 *
 * <p>Policy conditions are used by policy rules to determine whether
 * the requirements for a particular action are satisfied.</p>
 *
 * <p>Implementations should return {@code true} when the condition
 * is satisfied and {@code false} otherwise.</p>
 */
public interface PolicyCondition {

    /**
     * Evaluates this condition against the supplied action context.
     *
     * @param context action context to evaluate
     * @return {@code true} if the condition is satisfied;
     *         {@code false} otherwise
     */
    boolean matches(ActionContext context);
}
