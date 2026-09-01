package io.github.jcodeforge.aipolicy.android.capability;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.github.jcodeforge.aipolicy.capability.AiCapability;
import io.github.jcodeforge.aipolicy.capability.Capability;
import io.github.jcodeforge.aipolicy.capability.CapabilityIndex;
import io.github.jcodeforge.aipolicy.capability.CapabilityIndexProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AndroidCapabilityRegistryTest {

    public static final class TestService {

        @AiCapability(
                name = "customer.read",
                description = "Read customer information"
        )
        public void readCustomer() {
        }
    }

    public static final class TestCapabilityIndex implements CapabilityIndex {

        @Override
        public List<Class<?>> getCapabilityClasses() {
            return List.of(TestService.class);
        }
    }

    public static final class TestCapabilityIndexProvider implements CapabilityIndexProvider {

        @Override
        public CapabilityIndex getCapabilityIndex() {
            return new TestCapabilityIndex();
        }
    }

    @Test
    public void registersCapabilitiesFromIndexProvider() {
        AndroidCapabilityRegistry registry = AndroidCapabilityRegistry.getInstance();

        registry.initialize(androidx.test.core.app.ApplicationProvider.getApplicationContext());
        Capability capability = registry.get("customer.read");
        assertNotNull(capability);
        assertTrue(registry.contains("customer.read"));
    }

    @Test
    public void initializationIsIdempotent() {
        AndroidCapabilityRegistry registry = AndroidCapabilityRegistry.getInstance();

        registry.initialize(androidx.test.core.app.ApplicationProvider.getApplicationContext());
        registry.initialize(androidx.test.core.app.ApplicationProvider.getApplicationContext());
        assertTrue(registry.contains("customer.read"));
    }
}