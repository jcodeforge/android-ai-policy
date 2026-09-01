package io.github.jcodeforge.aipolicy.capability;

import io.github.jcodeforge.aipolicy.CallerType;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CapabilityDiscoveryTest {

    @AiCapability(
            name = "customer.read",
            description = "Read customer information"
    )
    public void readCustomer() {
    }

    @AiCapability(
            name = "customer.delete",
            description = "Delete customer",
            userInitiatedRequired = true,
            allowedCallerTypes = {
                    CallerType.SELF
            },
            requiredPermissions = {
                    "android.permission.INTERNET"
            }
    )
    public void deleteCustomer() {
    }

    public void internalMethod() {
    }

    @Test
    public void discoversCapabilityMetadata() {
        CapabilityDiscovery discovery = new CapabilityDiscovery();

        List<Capability> capabilities =
                discovery.discover(CapabilityDiscoveryTest.class);

        assertEquals(2, capabilities.size());

        Capability deleteCapability = findCapability(capabilities, "customer.delete");

        assertEquals("Delete customer", deleteCapability.getDescription());

        assertTrue(deleteCapability.isUserInitiatedRequired());

        assertEquals(List.of(CallerType.SELF), deleteCapability.getAllowedCallerTypes());

        assertEquals(List.of("android.permission.INTERNET"), deleteCapability.getRequiredPermissions());
    }

    @Test
    public void usesDefaultMetadata() {
        CapabilityDiscovery discovery = new CapabilityDiscovery();

        List<Capability> capabilities = discovery.discover(CapabilityDiscoveryTest.class);

        Capability readCapability = findCapability(capabilities, "customer.read");

        assertEquals("Read customer information", readCapability.getDescription());
        assertFalse(readCapability.isUserInitiatedRequired());
        assertTrue(readCapability.getAllowedCallerTypes().isEmpty());
        assertTrue(readCapability.getRequiredPermissions().isEmpty());
    }

    @Test
    public void ignoresMethodsWithoutAnnotation() {
        CapabilityDiscovery discovery = new CapabilityDiscovery();

        List<Capability> capabilities = discovery.discover(CapabilityDiscoveryTest.class);

        assertEquals(2, capabilities.size());
    }

    @Test
    public void returnsEmptyListWhenNoCapabilitiesExist() {
        CapabilityDiscovery discovery = new CapabilityDiscovery();

        List<Capability> capabilities = discovery.discover(EmptyService.class);

        assertTrue(capabilities.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void requiresType() {
        new CapabilityDiscovery().discover(null);
    }

    private static Capability findCapability(List<Capability> capabilities, String name) {
        for (Capability capability : capabilities) {
            if (capability.getName().equals(name)) {
                return capability;
            }
        }

        throw new AssertionError("Capability not found: " + name);
    }

    private static final class EmptyService {
    }
}