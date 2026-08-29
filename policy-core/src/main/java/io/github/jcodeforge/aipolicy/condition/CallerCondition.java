package io.github.jcodeforge.aipolicy.condition;

import java.util.Objects;
import io.github.jcodeforge.aipolicy.ActionContext;

public final class CallerCondition implements PolicyCondition {

    private final String expectedCaller;

    public CallerCondition(String expectedCaller) {
        this.expectedCaller = Objects.requireNonNull(expectedCaller,
                "expectedCaller must not be null");

        if (expectedCaller.trim().isEmpty()) {
            throw new IllegalArgumentException("expectedCaller must not be blank");
        }
    }

    @Override
    public boolean matches(ActionContext context) {
        return expectedCaller.equals(context.getCaller());
    }

    public String getExpectedCaller() {
        return expectedCaller;
    }
}
