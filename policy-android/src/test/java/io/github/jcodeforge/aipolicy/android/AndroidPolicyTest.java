package io.github.jcodeforge.aipolicy.android;

import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.AiPolicy;
import io.github.jcodeforge.aipolicy.PolicyResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AndroidPolicyTest {

    @Mock AiPolicy policy;
    @Mock AndroidActionContextFactory contextFactory;
    @Mock ActionContext context;
    @Mock PolicyResult result;

    AndroidPolicy SUT;

    @Test
    public void evaluatesAction() {
        when(contextFactory.create("customer.read", true)).thenReturn(context);
        when(policy.evaluate(context)).thenReturn(result);

        SUT = new AndroidPolicy(policy, contextFactory);

        PolicyResult actual = SUT.evaluate("customer.read", true);

        assertSame(result, actual);
    }

    @Test
    public void evaluatesNonUserInitiatedAction() {
        when(contextFactory.create("customer.read", false)).thenReturn(context);
        when(policy.evaluate(context)).thenReturn(result);

        SUT = new AndroidPolicy(policy, contextFactory);

        PolicyResult actual = SUT.evaluate("customer.read", false);

        assertSame(result, actual);
    }

    @Test
    public void evaluatesDeniedAction() {
        when(contextFactory.create("customer.delete", false)).thenReturn(context);
        when(policy.evaluate(context)).thenReturn(result);

        SUT = new AndroidPolicy(policy, contextFactory);

        assertSame(result, SUT.evaluate("customer.delete", false));
    }

    @Test
    public void evaluatesConfirmationRequiredAction() {
        when(contextFactory.create("customer.delete", true)).thenReturn(context);
        when(policy.evaluate(context)).thenReturn(result);

        SUT = new AndroidPolicy(policy, contextFactory);

        assertSame(result, SUT.evaluate("customer.delete", true));
    }

    @Test(expected = NullPointerException.class)
    public void requiresPolicy() {
        new AndroidPolicy(null, contextFactory);
    }

    @Test(expected = NullPointerException.class)
    public void requiresContextFactory() {
        new AndroidPolicy(policy, null);
    }
}