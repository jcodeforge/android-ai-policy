package io.github.jcodeforge.aipolicy;

import org.junit.Test;

import static org.junit.Assert.*;

import io.github.jcodeforge.aipolicy.condition.AlwaysCondition;
import io.github.jcodeforge.aipolicy.condition.AndCondition;
import io.github.jcodeforge.aipolicy.condition.CallerCondition;
import io.github.jcodeforge.aipolicy.condition.NotCondition;
import io.github.jcodeforge.aipolicy.condition.OrCondition;
import io.github.jcodeforge.aipolicy.condition.UserInitiatedCondition;

public class AiPolicyTest {

    @Test
    public void allowedCapabilityReturnsAllow() {
        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", new PolicyRule(Decision.ALLOW, null))
                .build();

        ActionContext context = new ActionContext("customer.read", "com.example.agent",
                true);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.ALLOW, result.getDecision());
        assertNull(result.getReason());
    }

    @Test
    public void deniedCapabilityReturnsDeny() {
        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.export",
                        new PolicyRule(Decision.DENY, "Export not permitted"))
                .build();

        ActionContext context = new ActionContext("customer.export", "com.example.agent",
                false);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.DENY, result.getDecision());
        assertEquals("Export not permitted", result.getReason());
    }

    @Test
    public void confirmationCapabilityReturnsRequireConfirmation() {
        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.delete", new PolicyRule(Decision.REQUIRE_CONFIRMATION,
                        "Confirmation required"))
                .build();

        ActionContext context = new ActionContext("customer.delete", "com.example.agent",
                false);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.REQUIRE_CONFIRMATION, result.getDecision());
        assertEquals("Confirmation required", result.getReason());
    }

    @Test
    public void unknownCapabilityIsDenied() {
        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", new PolicyRule(Decision.ALLOW, null))
                .build();

        ActionContext context = new ActionContext("customer.delete", "com.example.agent",
                false);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.DENY, result.getDecision());
        assertNotNull(result.getReason());
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

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.DENY, result.getDecision());
    }

    @Test
    public void matchingConditionReturnsConfiguredRule() {
        PolicyRule rule = new PolicyRule(Decision.REQUIRE_CONFIRMATION,
                "Confirmation required", new UserInitiatedCondition());

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.delete", rule)
                .build();

        ActionContext context = new ActionContext("customer.delete",
                "com.example.agent", true);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.REQUIRE_CONFIRMATION, result.getDecision());
        assertEquals("Confirmation required", result.getReason());
    }

    @Test
    public void nonMatchingConditionReturnsDeny() {
        PolicyRule rule = new PolicyRule(Decision.REQUIRE_CONFIRMATION, "Confirmation required",
                new UserInitiatedCondition());

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.delete", rule)
                .build();

        ActionContext context = new ActionContext("customer.delete", "com.example.agent",
                false);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.DENY, result.getDecision());
        assertEquals("No policy condition was satisfied", result.getReason());
    }

    @Test
    public void alwaysConditionReturnsConfiguredRule() {
        PolicyRule rule = new PolicyRule(Decision.ALLOW, null);

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", rule)
                .build();

        ActionContext context = new ActionContext("customer.read", "com.example.agent",
                false);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.ALLOW, result.getDecision());
    }

    @Test
    public void andConditionAllowsWhenAllConditionsMatch() {
        PolicyRule rule = new PolicyRule(
                Decision.ALLOW,
                null,
                new AndCondition(new CallerCondition("com.example.agent"),
                        new UserInitiatedCondition())
        );

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", rule)
                .build();

        ActionContext context = new ActionContext("customer.read", "com.example.agent",
                true);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.ALLOW, result.getDecision());
    }

    @Test
    public void andConditionDeniesWhenConditionDoesNotMatch() {
        PolicyRule rule = new PolicyRule(
                Decision.ALLOW,
                null,
                new AndCondition(new CallerCondition("com.example.agent"),
                        new UserInitiatedCondition())
        );

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", rule)
                .build();

        ActionContext context = new ActionContext("customer.read", "com.example.agent",
                false);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.DENY, result.getDecision());
    }

    @Test
    public void orConditionAllowsWhenOneConditionMatches() {
        PolicyRule rule = new PolicyRule(
                Decision.ALLOW,
                null,
                new OrCondition(new CallerCondition("com.example.agent"),
                        new UserInitiatedCondition())
        );

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", rule)
                .build();

        ActionContext context = new ActionContext("customer.read", "com.example.other",
                true);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.ALLOW, result.getDecision());
    }

    @Test
    public void notConditionInvertsCondition() {
        PolicyRule rule = new PolicyRule(
                Decision.ALLOW,
                null,
                new NotCondition(new UserInitiatedCondition())
        );

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", rule)
                .build();

        ActionContext context = new ActionContext("customer.read", "com.example.agent",
                false);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.ALLOW, result.getDecision());
    }

    @Test
    public void nonMatchingRuleFallsBackToNextRule() {
        PolicyRule conditionalRule = new PolicyRule(Decision.ALLOW, null,
                new CallerCondition("com.example.trusted"));

        PolicyRule fallbackRule = new PolicyRule(Decision.REQUIRE_CONFIRMATION,
                "Confirmation required", new AlwaysCondition());

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.delete", conditionalRule)
                .addRule("customer.delete", fallbackRule)
                .build();

        ActionContext context = new ActionContext("customer.delete",
                "com.example.other", false);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.REQUIRE_CONFIRMATION, result.getDecision());
        assertEquals("Confirmation required", result.getReason());
    }

    @Test
    public void firstMatchingRuleWins() {
        PolicyRule firstRule = new PolicyRule(Decision.ALLOW, null, new AlwaysCondition());

        PolicyRule secondRule = new PolicyRule(Decision.DENY, "Should not be reached",
                new AlwaysCondition());

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", firstRule)
                .addRule("customer.read", secondRule)
                .build();

        ActionContext context = new ActionContext("customer.read", "com.example.agent",
                true);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.ALLOW, result.getDecision());
        assertNull(result.getReason());
    }

    @Test
    public void deniesWhenNoRuleConditionMatches() {
        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.delete",
                        new PolicyRule(Decision.ALLOW, null,
                                new CallerCondition("com.example.trusted"))
                )
                .addRule("customer.delete",
                       new PolicyRule(Decision.REQUIRE_CONFIRMATION, "Confirmation required",
                               new UserInitiatedCondition())
                )
                .build();

        ActionContext context = new ActionContext("customer.delete", "com.example.other",
                false);

        PolicyResult result = policy.evaluate(context);

        assertEquals(Decision.DENY, result.getDecision());
        assertEquals("No policy condition was satisfied", result.getReason());
    }
}