package io.github.jcodeforge.aipolicy.capability;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class CapabilityIndexTest {

    private static final class TestCapabilityIndex implements CapabilityIndex {

        @Override
        public List<Class<?>> getCapabilityClasses() {
            return Arrays.asList(CustomerService.class, InvoiceService.class);
        }
    }

    private static final class CustomerService {
    }

    private static final class InvoiceService {
    }

    @Test
    public void returnsCapabilityClasses() {
        CapabilityIndex index = new TestCapabilityIndex();

        List<Class<?>> classes = index.getCapabilityClasses();

        assertNotNull(classes);
        assertEquals(2, classes.size());
        assertSame(CustomerService.class, classes.get(0));
        assertSame(InvoiceService.class, classes.get(1));
    }
}