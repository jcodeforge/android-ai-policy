package io.github.jcodeforge.aipolicy.condition;

import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.ApplicationState;
import io.github.jcodeforge.aipolicy.CallerIdentity;
import io.github.jcodeforge.aipolicy.CallerType;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApplicationStateConditionTest {

    private final CallerIdentity agentCaller = new CallerIdentity("com.example.agent",
            CallerType.EXTERNAL
    );

    @Test
    public void foregroundConditionMatchesForegroundContext() {
        ApplicationStateCondition condition = new ApplicationStateCondition(ApplicationState.FOREGROUND);

        ActionContext context = new ActionContext(
                "customer.read",
                agentCaller,
                true,
                ApplicationState.FOREGROUND
        );

        assertTrue(condition.matches(context));
    }

    @Test
    public void foregroundConditionDoesNotMatchBackgroundContext() {
        ApplicationStateCondition condition = new ApplicationStateCondition(ApplicationState.FOREGROUND);

        ActionContext context = new ActionContext(
                "customer.read",
                agentCaller,
                true,
                ApplicationState.BACKGROUND
        );

        assertFalse(condition.matches(context));
    }

    @Test
    public void backgroundConditionMatchesBackgroundContext() {
        ApplicationStateCondition condition = new ApplicationStateCondition(ApplicationState.BACKGROUND);

        ActionContext context = new ActionContext(
                "customer.read",
                agentCaller,
                false,
                ApplicationState.BACKGROUND
        );

        assertTrue(condition.matches(context));
    }

    @Test
    public void backgroundConditionDoesNotMatchForegroundContext() {
        ApplicationStateCondition condition = new ApplicationStateCondition(ApplicationState.BACKGROUND);

        ActionContext context = new ActionContext(
                "customer.read",
                agentCaller,
                false,
                ApplicationState.FOREGROUND
        );

        assertFalse(condition.matches(context));
    }

    @Test(expected = NullPointerException.class)
    public void conditionRequiresApplicationState() {
        new ApplicationStateCondition(null);
    }

    @Test(expected = NullPointerException.class)
    public void matchingRequiresContext() {
        ApplicationStateCondition condition =
                new ApplicationStateCondition(ApplicationState.FOREGROUND);

        condition.matches(null);
    }
}