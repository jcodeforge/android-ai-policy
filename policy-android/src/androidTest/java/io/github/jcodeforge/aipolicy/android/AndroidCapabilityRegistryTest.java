package io.github.jcodeforge.aipolicy.android;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import io.github.jcodeforge.aipolicy.CallerType;
import io.github.jcodeforge.aipolicy.capability.AiCapability;
import io.github.jcodeforge.aipolicy.capability.Capability;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

@RunWith(AndroidJUnit4.class)
public class AndroidCapabilityRegistryTest {

    public static final class FirstServiceTest {

        @AiCapability(
                name = "customer.read",
                description = "Read customer information"
        )
        public void readCustomer() {
        }
    }

    public static final class SecondServiceTest {

        @AiCapability(
                name = "customer.delete",
                description = "Delete customer",
                userInitiatedRequired = true,
                allowedCallerTypes = {CallerType.SELF},
                requiredPermissions = {"android.permission.INTERNET"}
        )
        public void deleteCustomer() {
        }
    }

    @Test
    public void registersCapabilitiesAutomatically() {
        AndroidCapabilityRegistry registry = getInitializedRegistry();

        Capability capability = registry.getCapability("customer.read");

        assertNotNull(capability);
        assertTrue(registry.hasCapability("customer.read"));
    }

    @Test
    public void registersCapabilitiesFromMultipleClasses() {
        AndroidCapabilityRegistry registry = getInitializedRegistry();

        assertTrue(registry.hasCapability("customer.read"));
        assertTrue(registry.hasCapability("customer.delete"));
    }

    @Test
    public void registersCapabilityMetadata() {
        AndroidCapabilityRegistry registry = getInitializedRegistry();

        Capability capability = registry.getCapability("customer.delete");

        assertNotNull(capability);
        assertEquals("customer.delete", capability.getName());
        assertEquals("Delete customer", capability.getDescription());
        assertTrue(capability.isUserInitiatedRequired());
        assertEquals(List.of(CallerType.SELF), capability.getAllowedCallerTypes());
        assertEquals(List.of("android.permission.INTERNET"), capability.getRequiredPermissions());
    }

    @Test
    public void registersCapabilitiesWithoutMetadata() {
        AndroidCapabilityRegistry registry = getInitializedRegistry();

        Capability capability = registry.getCapability("customer.read");

        assertNotNull(capability);
        assertEquals("customer.read", capability.getName());
        assertEquals("Read customer information", capability.getDescription());
        assertFalse(capability.isUserInitiatedRequired());
        assertTrue(capability.getAllowedCallerTypes().isEmpty());
        assertTrue(capability.getRequiredPermissions().isEmpty());
    }

    @Test
    public void returnsRegisteredCapabilities() {
        AndroidCapabilityRegistry registry = getInitializedRegistry();

        assertNotNull(registry.getCapabilities());
        assertFalse(registry.getCapabilities().isEmpty());
    }

    @Test
    public void returnsNullForUnknownCapability() {
        AndroidCapabilityRegistry registry = getInitializedRegistry();

        assertNull(registry.getCapability("does.not.exist"));
        assertFalse(registry.hasCapability("does.not.exist"));
    }

    private AndroidCapabilityRegistry getInitializedRegistry() {
        Context context = ApplicationProvider.getApplicationContext();

        AndroidCapabilityRegistry registry = AndroidCapabilityRegistry.getInstance();

        registry.initialize(context);

        return registry;
    }
}