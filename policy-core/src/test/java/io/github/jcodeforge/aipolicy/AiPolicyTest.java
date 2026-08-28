package io.github.jcodeforge.aipolicy;

import org.junit.Test;

import static org.junit.Assert.*;

public class AiPolicyTest {

    @Test
    public void allowedCapabilityReturnsAllow() {
        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", new PolicyRule(Decision.ALLOW, null))
                .build();

        ActionContext context = new ActionContext("customer.read", "com.example.agent",
                true);

        PolicyRule rule = policy.evaluate(context);

        assertEquals(Decision.ALLOW, rule.getDecision());
        assertNull(rule.getReason());
    }

    @Test
    public void deniedCapabilityReturnsDeny() {
        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.export",
                        new PolicyRule(Decision.DENY, "Export not permitted"))
                .build();

        ActionContext context = new ActionContext("customer.export", "com.example.agent",
                false);

        PolicyRule rule = policy.evaluate(context);

        assertEquals(Decision.DENY, rule.getDecision());
        assertEquals("Export not permitted", rule.getReason());
    }

    @Test
    public void confirmationCapabilityReturnsRequireConfirmation() {
        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.delete", new PolicyRule(Decision.REQUIRE_CONFIRMATION,
                        "Confirmation required"))
                .build();

        ActionContext context = new ActionContext("customer.delete", "com.example.agent",
                false);

        PolicyRule rule = policy.evaluate(context);

        assertEquals(Decision.REQUIRE_CONFIRMATION, rule.getDecision());
        assertEquals("Confirmation required", rule.getReason());
    }

    @Test
    public void unknownCapabilityIsDenied() {
        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", new PolicyRule(Decision.ALLOW, null))
                .build();

        ActionContext context = new ActionContext("customer.delete", "com.example.agent",
                false);

        PolicyRule rule = policy.evaluate(context);

        assertEquals(Decision.DENY, rule.getDecision());
        assertNotNull(rule.getReason());
    }

    @Test(expected = NullPointerException.class)
    public void evaluationRequiresContext() {
        AiPolicy policy = AiPolicy.builder().build();

        policy.evaluate(null);
    }

    @Test(expected = NullPointerException.class)
    public void capabilityMustNotBeNull() {
        AiPolicy.builder().addRule(null, new PolicyRule(Decision.ALLOW, null));
    }

    @Test(expected = NullPointerException.class)
    public void ruleMustNotBeNull() {
        AiPolicy.builder().addRule("customer.read", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void blankCapabilityMustNotBeAllowed() {
        AiPolicy.builder().addRule("   ", new PolicyRule(Decision.ALLOW, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicateCapabilityMustNotBeAllowed() {
        AiPolicy.builder()
                .addRule("customer.read", new PolicyRule(Decision.ALLOW, null))
                .addRule("customer.read", new PolicyRule(Decision.DENY, "Not allowed"));
    }

    @Test(expected = NullPointerException.class)
    public void nullCapabilityMustNotBeAllowed() {
        AiPolicy.builder().addRule(null, new PolicyRule(Decision.ALLOW, null));
    }

    @Test(expected = NullPointerException.class)
    public void nullRuleMustNotBeAllowed() {
        AiPolicy.builder().addRule("customer.read", null);
    }

    @Test
    public void policyIsImmutableAfterBuild() {
        AiPolicy.Builder builder = AiPolicy.builder();

        builder.addRule("customer.read", new PolicyRule(Decision.ALLOW, null));

        AiPolicy policy = builder.build();

        builder.addRule("customer.delete", new PolicyRule(Decision.DENY,
                "Not permitted"));

        ActionContext context = new ActionContext("customer.delete",
                "com.example.agent", false);

        PolicyRule result = policy.evaluate(context);

        assertEquals(Decision.DENY, result.getDecision());
    }
}