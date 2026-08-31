package io.github.jcodeforge.aipolicy.capability;

import org.junit.Test;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CapabilityRegistryTest {

    @Test
    public void registersCapability() {
        CapabilityRegistry registry = new CapabilityRegistry();

        Capability capability = new Capability("customer.read",
                "Read customer information");

        registry.register(capability);

        assertTrue(registry.contains("customer.read"));
        assertSame(capability, registry.get("customer.read"));
        assertEquals(1, registry.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsDuplicateCapability() {
        CapabilityRegistry registry = new CapabilityRegistry();

        registry.register(new Capability("customer.read", "Read customer information"));
        registry.register(new Capability("customer.read", "Read customer data"));
    }

    @Test
    public void returnsNullForUnknownCapability() {
        CapabilityRegistry registry = new CapabilityRegistry();

        assertFalse(registry.contains("customer.read"));
        assertNull(registry.get("customer.read"));
    }

    @Test
    public void returnsAllCapabilities() {
        CapabilityRegistry registry = new CapabilityRegistry();

        Capability read = new Capability("customer.read", "Read customer information");
        Capability delete = new Capability("customer.delete", "Delete customer");

        registry.register(read);
        registry.register(delete);

        Collection<Capability> capabilities = registry.getAll();

        assertEquals(2, capabilities.size());
        assertTrue(capabilities.contains(read));
        assertTrue(capabilities.contains(delete));
    }

    @Test(expected = NullPointerException.class)
    public void requiresCapability() {
        new CapabilityRegistry().register(null);
    }

    @Test(expected = NullPointerException.class)
    public void requiresNameForGet() {
        new CapabilityRegistry().get(null);
    }

    @Test(expected = NullPointerException.class)
    public void requiresNameForContains() {
        new CapabilityRegistry().contains(null);
    }
}