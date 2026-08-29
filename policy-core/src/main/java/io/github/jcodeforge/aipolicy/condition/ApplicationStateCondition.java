package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.ApplicationState;
import java.util.Objects;

/**
 * Matches an action when the application is in a specified state.
 */
public final class ApplicationStateCondition implements PolicyCondition {

    private final ApplicationState expectedState;

    /**
     * Creates a condition for the specified application state.
     *
     * @param expectedState the application state that must match
     */
    public ApplicationStateCondition(ApplicationState expectedState) {
        this.expectedState = Objects.requireNonNull(expectedState,
                "expectedState must not be null");
    }

    /**
     * Returns whether the application is in the expected state.
     *
     * @param context the action context
     * @return {@code true} if the application state matches
     */
    @Override
    public boolean matches(ActionContext context) {
        return context.getApplicationState() == expectedState;
    }
}