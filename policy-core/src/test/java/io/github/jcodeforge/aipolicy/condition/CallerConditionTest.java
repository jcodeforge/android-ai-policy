package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.CallerIdentity;
import io.github.jcodeforge.aipolicy.CallerType;
import org.junit.Test;

import static org.junit.Assert.*;

public class CallerConditionTest {

    private final CallerIdentity agentCaller =
            new CallerIdentity("com.example.agent", CallerType.EXTERNAL);

    private final CallerIdentity otherCaller =
            new CallerIdentity("com.example.other", CallerType.EXTERNAL);

    @Test
    public void matchesExpectedCaller() {
        CallerCondition condition = new CallerCondition("com.example.agent");

        ActionContext context = new ActionContext("customer.read", agentCaller, true);

        assertTrue(condition.matches(context));
    }

    @Test
    public void doesNotMatchDifferentCaller() {
        CallerCondition condition = new CallerCondition("com.example.agent");

        ActionContext context = new ActionContext("customer.read", otherCaller, true);

        assertFalse(condition.matches(context));
    }

    @Test
    public void callerComparisonIsCaseSensitive() {
        CallerCondition condition = new CallerCondition("com.example.agent");

        CallerIdentity caller = new CallerIdentity("COM.EXAMPLE.AGENT", CallerType.EXTERNAL);

        ActionContext context = new ActionContext("customer.read", caller, true);

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