package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.CallerIdentity;
import io.github.jcodeforge.aipolicy.CallerType;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserInitiatedConditionTest {

    private final PolicyCondition SUT = new UserInitiatedCondition();

    private final CallerIdentity agentCaller = new CallerIdentity("com.example.agent",
            CallerType.EXTERNAL);

    @Test
    public void matchesUserInitiatedAction() {
        ActionContext context = new ActionContext(
                "customer.delete",
                agentCaller,
                true
        );

        assertTrue(SUT.matches(context));
    }

    @Test
    public void doesNotMatchNonUserInitiatedAction() {
        ActionContext context = new ActionContext(
                "customer.delete",
                agentCaller,
                false
        );

        assertFalse(SUT.matches(context));
    }

    @Test(expected = NullPointerException.class)
    public void contextMustNotBeNull() {
        SUT.matches(null);
    }
}