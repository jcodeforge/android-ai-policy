package io.github.jcodeforge.aipolicy.condition;

import java.util.Objects;
import io.github.jcodeforge.aipolicy.ActionContext;

/**
 * A policy condition that checks whether the action was requested by
 * a specific caller.
 *
 * <p>The condition compares the expected caller identifier with the
 * identifier provided by the {@link ActionContext}.</p>
 */
public final class CallerCondition implements PolicyCondition {

    private final String expectedCaller;

    /**
     * Creates a caller condition.
     *
     * @param expectedCaller identifier of the caller that is allowed
     * @throws NullPointerException if {@code expectedCaller} is {@code null}
     * @throws IllegalArgumentException if {@code expectedCaller} is blank
     */
    public CallerCondition(String expectedCaller) {
        this.expectedCaller = Objects.requireNonNull(expectedCaller,
                "expectedCaller must not be null");

        if (expectedCaller.trim().isEmpty()) {
            throw new IllegalArgumentException("expectedCaller must not be blank");
        }
    }

    /**
     * Determines whether the caller in the action context matches
     * the expected caller.
     *
     * @param context action context containing the caller identity
     * @return {@code true} if the caller identifier matches the expected
     *         caller; {@code false} otherwise
     * @throws NullPointerException if {@code context} is {@code null}
     */
    @Override
    public boolean matches(ActionContext context) {
        return expectedCaller.equals(context.getCallerIdentity().getId());
    }
}
