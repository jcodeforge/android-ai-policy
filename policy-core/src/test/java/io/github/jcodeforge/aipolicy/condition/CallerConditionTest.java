package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class CallerConditionTest {

    @Test
    public void matchesExpectedCaller() {
        CallerCondition condition = new CallerCondition("com.example.agent");

        ActionContext context = new ActionContext("customer.read",
                "com.example.agent", true);

        assertTrue(condition.matches(context));
    }

    @Test
    public void doesNotMatchDifferentCaller() {
        CallerCondition condition = new CallerCondition("com.example.agent");

        ActionContext context = new ActionContext("customer.read", "com.example.other",
                true);

        assertFalse(condition.matches(context));
    }

    @Test
    public void callerComparisonIsCaseSensitive() {
        CallerCondition condition = new CallerCondition("com.example.agent");

        ActionContext context = new ActionContext("customer.read",
                "COM.EXAMPLE.AGENT", true);

        assertFalse(condition.matches(context));
    }

    @Test(expected = NullPointerException.class)
    public void expectedCallerMustNotBeNull() {
        new CallerCondition(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void expectedCallerMustNotBeBlank() {
        new CallerCondition("   ");
    }

    @Test(expected = NullPointerException.class)
    public void contextMustNotBeNull() {
        CallerCondition condition = new CallerCondition("com.example.agent");
        condition.matches(null);
    }
}