package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserInitiatedConditionTest {

    private final PolicyCondition SUT = new UserInitiatedCondition();

    @Test
    public void matchesUserInitiatedAction() {
        ActionContext context = new ActionContext("customer.delete",
                "com.example.agent", true);

        assertTrue(SUT.matches(context));
    }

    @Test
    public void doesNotMatchNonUserInitiatedAction() {
        ActionContext context = new ActionContext("customer.delete",
                "com.example.agent", false);

        assertFalse(SUT.matches(context));
    }

    @Test(expected = NullPointerException.class)
    public void contextMustNotBeNull() {
        SUT.matches(null);
    }
}