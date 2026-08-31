package io.github.jcodeforge.aipolicy.capability;

import org.junit.Test;

import static org.junit.Assert.*;

import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.AiPolicy;
import io.github.jcodeforge.aipolicy.CallerIdentity;
import io.github.jcodeforge.aipolicy.CallerType;
import io.github.jcodeforge.aipolicy.Decision;
import io.github.jcodeforge.aipolicy.PolicyResult;
import io.github.jcodeforge.aipolicy.PolicyRule;

public class CapabilityTest {

    @Test
    public void createsCapability() {
        Capability capability = new Capability("customer.read",
                "Read customer information");

        assertEquals("customer.read", capability.getName());
        assertEquals("Read customer information", capability.getDescription());
    }

    @Test(expected = NullPointerException.class)
    public void requiresName() {
        new Capability(null, "Read customer information");
    }

    @Test(expected = NullPointerException.class)
    public void requiresDescription() {
        new Capability("customer.read", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBlankName() {
        new Capability("   ", "Read customer information");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBlankDescription() {
        new Capability("customer.read", "   ");
    }

    @Test
    public void equalCapabilitiesAreEqual() {
        Capability first = new Capability("customer.read",
                "Read customer information");

        Capability second = new Capability("customer.read",
                "Read customer information");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void differentCapabilitiesAreNotEqual() {
        Capability first = new Capability("customer.read",
                "Read customer information");

        Capability second = new Capability("customer.delete", "Delete customer");

        assertNotEquals(first, second);
    }

    @Test
    public void checksAllowedInvocation() {
        Capability capability = new Capability("customer.read",
                "Read customer information");

        ActionContext context = new ActionContext("customer.read",
                new CallerIdentity("com.example.app", CallerType.SELF),
                true);

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", new PolicyRule(Decision.ALLOW, null))
                .build();

        PolicyResult result = capability.evaluate(policy, context);

        assertTrue(result.isAllowed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsDifferentCapability() {
        Capability capability = new Capability("customer.read",
                "Read customer information");

        ActionContext context = new ActionContext("customer.delete",
                new CallerIdentity("com.example.app", CallerType.SELF), true);

        AiPolicy policy = AiPolicy.builder().build();

        capability.evaluate(policy, context);
    }

    @Test
    public void checksDeniedInvocation() {
        Capability capability = new Capability("customer.delete", "Delete customer");

        ActionContext context = new ActionContext("customer.delete",
                new CallerIdentity("com.example.app", CallerType.SELF), false);

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.delete", new PolicyRule(Decision.DENY,
                        "Deletion is not allowed"))
                .build();

        PolicyResult result = capability.evaluate(policy, context);

        assertFalse(result.isAllowed());
    }

    @Test(expected = NullPointerException.class)
    public void requiresPolicyForEvaluation() {
        Capability capability = new Capability("customer.read",
                "Read customer information");

        ActionContext context = new ActionContext("customer.read",
                new CallerIdentity("com.example.app", CallerType.SELF), true);

        capability.evaluate(null, context);
    }
}