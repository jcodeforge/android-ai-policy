package io.github.jcodeforge.aipolicy.capability;

import org.junit.Test;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AiCapabilityTest {

    @AiCapability(
            name = "customer.read",
            description = "Read customer information"
    )
    public void readCustomer() {
    }

    @Test
    public void exposesCapabilityMetadata() throws Exception {
        Method method = AiCapabilityTest.class.getMethod("readCustomer");

        AiCapability capability = method.getAnnotation(AiCapability.class);

        assertNotNull(capability);
        assertEquals("customer.read", capability.name());
        assertEquals("Read customer information", capability.description());
    }

    @Test
    public void hasRuntimeRetention() {
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME,
                AiCapability.class.getAnnotation(java.lang.annotation.Retention.class).value()
        );
    }

    @Test
    public void targetsMethods() {
        java.lang.annotation.Target target =
                AiCapability.class.getAnnotation(java.lang.annotation.Target.class);

        assertNotNull(target);
        assertEquals(1, target.value().length);
        assertEquals(java.lang.annotation.ElementType.METHOD, target.value()[0]);
    }
}