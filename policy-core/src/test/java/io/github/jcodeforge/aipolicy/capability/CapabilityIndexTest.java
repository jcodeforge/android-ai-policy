package io.github.jcodeforge.aipolicy.capability;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class CapabilityIndexTest {

    private static final class TestCapabilityIndex implements CapabilityIndex {

        private final Capability customer =
                new Capability("customer.read", "Read customer information");

        private final Capability invoice =
                new Capability("invoice.read", "Read invoice information");

        @Override
        public List<Capability> getCapabilities() {
            return Arrays.asList(customer, invoice);
        }
    }

    @Test
    public void returnsCapabilities() {
        CapabilityIndex index = new TestCapabilityIndex();

        List<Capability> capabilities = index.getCapabilities();

        assertNotNull(capabilities);
        assertEquals(2, capabilities.size());

        assertEquals("customer.read", capabilities.get(0).getName());

        assertEquals("invoice.read", capabilities.get(1).getName());
    }

    @Test
    public void returnsSameCapabilityInstances() {
        TestCapabilityIndex index = new TestCapabilityIndex();

        List<Capability> first = index.getCapabilities();
        List<Capability> second = index.getCapabilities();

        assertSame(first.get(0), second.get(0));
        assertSame(first.get(1), second.get(1));
    }
}