package io.github.jcodeforge.aipolicy;

import org.junit.Test;

import static org.junit.Assert.*;

public class PolicyRuleTest {

    @Test
    public void allowPolicyHasAllowDecision() {
        PolicyRule rule = new PolicyRule(Decision.ALLOW, null);

        assertEquals(Decision.ALLOW, rule.getDecision());
        assertNull(rule.getReason());
    }

    @Test
    public void denyPolicyHasDenyDecisionAndReason() {
        String reason = "Action is not permitted";

        PolicyRule rule = new PolicyRule(Decision.DENY, reason);

        assertEquals(Decision.DENY, rule.getDecision());
        assertEquals(reason, rule.getReason());
    }

    @Test
    public void confirmationPolicyHasConfirmationDecisionAndReason() {
        String reason = "User confirmation is required";

        PolicyRule rule = new PolicyRule(Decision.REQUIRE_CONFIRMATION, reason);

        assertEquals(Decision.REQUIRE_CONFIRMATION, rule.getDecision());
        assertEquals(reason, rule.getReason());
    }

    @Test(expected = NullPointerException.class)
    public void decisionMustNotBeNull() {
        new PolicyRule(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void allowMustNotHaveReason() {
        new PolicyRule(Decision.ALLOW, "This should not be allowed");
    }

    @Test(expected = IllegalArgumentException.class)
    public void denyRequiresReason() {
        new PolicyRule(Decision.DENY, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmationRequiresReason() {
        new PolicyRule(Decision.REQUIRE_CONFIRMATION, null);
    }
}