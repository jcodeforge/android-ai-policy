package io.github.jcodeforge.aipolicy.capability;

import io.github.jcodeforge.aipolicy.CallerType;
import org.junit.Test;
import java.util.Collections;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CapabilityTest {

    @Test
    public void createsCapability() {
        Capability capability = new Capability("customer.read",
                "Read customer information");

        assertEquals("customer.read", capability.getName());
        assertEquals("Read customer information", capability.getDescription());

        assertFalse(capability.isUserInitiatedRequired());
        assertTrue(capability.getAllowedCallerTypes().isEmpty());
        assertTrue(capability.getRequiredPermissions().isEmpty());
    }

    @Test
    public void createsCapabilityWithMetadata() {
        Capability capability = new Capability(
                "customer.delete",
                "Delete customer",
                true,
                Collections.singletonList(CallerType.SELF),
                Collections.singletonList("android.permission.INTERNET")
        );

        assertEquals("customer.delete", capability.getName());
        assertEquals("Delete customer", capability.getDescription());
        assertTrue(capability.isUserInitiatedRequired());
        assertEquals(Collections.singletonList(CallerType.SELF), capability.getAllowedCallerTypes());
        assertEquals(Collections.singletonList("android.permission.INTERNET"),
                capability.getRequiredPermissions());
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

    @Test(expected = NullPointerException.class)
    public void requiresAllowedCallerTypes() {
        new Capability(
                "customer.read",
                "Read customer information",
                false,
                null,
                Collections.emptyList()
        );
    }

    @Test(expected = NullPointerException.class)
    public void requiresRequiredPermissions() {
        new Capability(
                "customer.read",
                "Read customer information",
                false,
                Collections.emptyList(),
                null
        );
    }

    @Test
    public void equalCapabilitiesAreEqual() {
        Capability first = new Capability("customer.read", "Read customer information");

        Capability second = new Capability(
                "customer.read",
                "Read customer information"
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void differentCapabilitiesAreNotEqual() {
        Capability first = new Capability(
                "customer.read",
                "Read customer information"
        );

        Capability second = new Capability(
                "customer.delete",
                "Delete customer"
        );

        assertNotEquals(first, second);
    }

    @Test
    public void differentUserInitiatedRequirementMakesCapabilitiesUnequal() {
        Capability first = new Capability(
                "customer.delete",
                "Delete customer",
                false,
                Collections.emptyList(),
                Collections.emptyList()
        );

        Capability second = new Capability(
                "customer.delete",
                "Delete customer",
                true,
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertNotEquals(first, second);
    }

    @Test
    public void differentCallerTypesMakeCapabilitiesUnequal() {
        Capability first = new Capability(
                "customer.delete",
                "Delete customer",
                false,
                Collections.emptyList(),
                Collections.emptyList()
        );

        Capability second = new Capability(
                "customer.delete",
                "Delete customer",
                false,
                Collections.singletonList(CallerType.SELF),
                Collections.emptyList()
        );

        assertNotEquals(first, second);
    }

    @Test
    public void differentPermissionsMakeCapabilitiesUnequal() {
        Capability first = new Capability(
                "customer.delete",
                "Delete customer",
                false,
                Collections.emptyList(),
                Collections.emptyList()
        );

        Capability second = new Capability(
                "customer.delete",
                "Delete customer",
                false,
                Collections.emptyList(),
                Collections.singletonList(
                        "android.permission.INTERNET"
                )
        );

        assertNotEquals(first, second);
    }

    @Test
    public void metadataCollectionsAreImmutable() {
        Capability capability = new Capability(
                "customer.delete",
                "Delete customer",
                true,
                Collections.singletonList(CallerType.SELF),
                Collections.singletonList(
                        "android.permission.INTERNET"
                )
        );

        try {
            capability.getAllowedCallerTypes()
                    .add(CallerType.EXTERNAL);

            throw new AssertionError("Allowed caller types must be immutable");

        } catch (UnsupportedOperationException expected) {
            // Expected.
        }

        try {
            capability.getRequiredPermissions().add("android.permission.CAMERA");

            throw new AssertionError("Required permissions must be immutable");

        } catch (UnsupportedOperationException expected) {
            // Expected.
        }
    }
}