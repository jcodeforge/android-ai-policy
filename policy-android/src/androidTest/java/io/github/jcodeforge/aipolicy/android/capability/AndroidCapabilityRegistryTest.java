package io.github.jcodeforge.aipolicy.android.capability;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.github.jcodeforge.aipolicy.capability.AiCapability;
import io.github.jcodeforge.aipolicy.capability.Capability;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
                description = "Delete customer"
        )
        public void deleteCustomer() {
        }
    }

    @Test
    public void registersCapabilitiesAutomatically() {
        AndroidCapabilityRegistry registry = AndroidCapabilityRegistry.getInstance();

        Capability capability = registry.getCapability("customer.read");

        assertNotNull(capability);
        assertTrue(registry.hasCapability("customer.read"));
    }

    @Test
    public void registersCapabilitiesFromMultipleClasses() {
        AndroidCapabilityRegistry registry = AndroidCapabilityRegistry.getInstance();

        assertTrue(registry.hasCapability("customer.read"));
        assertTrue(registry.hasCapability("customer.delete"));
    }

    @Test
    public void queriesRegisteredCapability() {
        AndroidCapabilityRegistry registry = AndroidCapabilityRegistry.getInstance();

        Capability capability = registry.getCapability("customer.read");

        assertNotNull(capability);
        assertEquals("customer.read", capability.getName());
    }

    @Test
    public void returnsRegisteredCapabilities() {
        AndroidCapabilityRegistry registry = AndroidCapabilityRegistry.getInstance();

        assertNotNull(registry.getCapabilities());
        assertFalse(registry.getCapabilities().isEmpty());
    }
}