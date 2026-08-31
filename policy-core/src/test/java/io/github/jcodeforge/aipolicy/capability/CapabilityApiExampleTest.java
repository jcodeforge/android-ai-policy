package io.github.jcodeforge.aipolicy.capability;

import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.AiPolicy;
import io.github.jcodeforge.aipolicy.CallerIdentity;
import io.github.jcodeforge.aipolicy.CallerType;
import io.github.jcodeforge.aipolicy.Decision;
import io.github.jcodeforge.aipolicy.PolicyResult;
import io.github.jcodeforge.aipolicy.PolicyRule;
import io.github.jcodeforge.aipolicy.condition.UserInitiatedCondition;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Complete example of the AI Capability API.
 *
 * <p>This example demonstrates how to:</p>
 *
 * <ol>
 *     <li>Declare AI-accessible methods using {@link AiCapability}</li>
 *     <li>Discover declared capabilities</li>
 *     <li>Register discovered capabilities</li>
 *     <li>Define capability-specific policies</li>
 *     <li>Check whether a capability may be invoked</li>
 * </ol>
 */
public class CapabilityApiExampleTest {

    /**
     * Example service containing AI-accessible capabilities.
     */
    public static final class CustomerService {

        /**
         * Reads customer information.
         */
        @AiCapability(
                name = "customer.read",
                description = "Read customer information"
        )
        public void readCustomer() {
            // Application logic would go here.
        }

        /**
         * Deletes a customer.
         */
        @AiCapability(
                name = "customer.delete",
                description = "Delete a customer"
        )
        public void deleteCustomer() {
            // Application logic would go here.
        }

        /**
         * Internal application functionality.
         *
         * <p>This method is not an AI capability because it is not
         * annotated with {@link AiCapability}.</p>
         */
        public void internalOperation() {
            // Not AI accessible.
        }
    }

    @Test
    public void completeCapabilityApiExample() {
        /*
         * 1. Discover capabilities declared on the service.
         */
        CapabilityDiscovery discovery = new CapabilityDiscovery();

        List<Capability> discovered = discovery.discover(CustomerService.class);

        assertEquals(2, discovered.size());

        /*
         * 2. Register the discovered capabilities.
         */
        CapabilityRegistry registry = new CapabilityRegistry();
        registry.registerAll(discovered);

        assertEquals(2, registry.size());

        /*
         * 3. Retrieve a specific capability.
         */
        Capability readCapability = registry.get("customer.read");

        assertNotNull(readCapability);
        assertEquals("Read customer information", readCapability.getDescription());

        /*
         * 4. Define capability-specific policies.
         *
         * customer.read:
         *     Always allowed for this example.
         *
         * customer.delete:
         *     Only allowed when explicitly initiated by the user.
         */
        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.read", new PolicyRule(Decision.ALLOW,
                        null)
                )
                .addRule(
                        "customer.delete",
                        new PolicyRule(Decision.ALLOW, null,
                                new UserInitiatedCondition())
                )
                .build();

        /*
         * 5. Create the action context for the invocation.
         *
         * The core API is platform independent, so the caller identity
         * is supplied by the application or platform integration.
         */
        ActionContext context = new ActionContext("customer.read",
                new CallerIdentity("com.example.application", CallerType.SELF), true);

        /*
         * 6. Check whether the capability may be invoked.
         */
        PolicyResult result = readCapability.evaluate(policy, context);

        assertTrue(result.isAllowed());
    }

    @Test
    public void capabilitySpecificPolicyExample() {
        CapabilityRegistry registry = new CapabilityRegistry();

        registry.register(new Capability("customer.delete", "Delete a customer"));

        Capability deleteCapability = registry.get("customer.delete");

        AiPolicy policy = AiPolicy.builder()
                .addRule("customer.delete",
                        new PolicyRule(Decision.ALLOW, null,
                                new UserInitiatedCondition())
                )
                .build();

        ActionContext userInitiated = new ActionContext("customer.delete",
                new CallerIdentity("com.example.application", CallerType.SELF), true);

        ActionContext notUserInitiated =
                new ActionContext("customer.delete", new CallerIdentity(
                        "com.example.application", CallerType.SELF), false);

        PolicyResult allowed = deleteCapability.evaluate(policy, userInitiated);
        PolicyResult denied = deleteCapability.evaluate(policy, notUserInitiated);

        assertTrue(allowed.isAllowed());
        assertFalse(denied.isAllowed());
    }
}