package io.github.jcodeforge.aipolicy.capability;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class CapabilityDiscoveryTest {

    @AiCapability(
            name = "customer.read",
            description = "Read customer information"
    )
    public void readCustomer() {
    }

    @AiCapability(
            name = "customer.delete",
            description = "Delete customer"
    )
    public void deleteCustomer() {
    }

    public void internalMethod() {
    }

    // Java reflection does not guarantee any order. We need to find it explicitly
    @Test
    public void discoversAnnotatedMethods() {
        CapabilityDiscovery discovery = new CapabilityDiscovery();

        List<Capability> capabilities = discovery.discover(CapabilityDiscoveryTest.class);

        assertEquals(2, capabilities.size());

        Capability read = capabilities.stream()
                .filter(capability ->
                        "customer.read".equals(capability.getName()))
                .findFirst()
                .orElseThrow(AssertionError::new);

        Capability delete = capabilities.stream()
                .filter(capability ->
                        "customer.delete".equals(capability.getName()))
                .findFirst()
                .orElseThrow(AssertionError::new);

        assertEquals("Read customer information", read.getDescription());
        assertEquals("Delete customer", delete.getDescription());
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

        assertEquals(0, capabilities.size());
    }

    @Test(expected = NullPointerException.class)
    public void requiresType() {
        new CapabilityDiscovery().discover(null);
    }

    private static final class EmptyService {
    }
}