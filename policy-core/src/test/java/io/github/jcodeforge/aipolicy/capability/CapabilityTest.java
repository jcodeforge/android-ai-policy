package io.github.jcodeforge.aipolicy.capability;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
}