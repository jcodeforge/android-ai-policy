package io.github.jcodeforge.aipolicy.capability;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Discovers AI-accessible capabilities declared with {@link AiCapability}.
 *
 * <p>Capabilities are discovered from methods annotated with
 * {@link AiCapability}.</p>
 */
public final class CapabilityDiscovery {

    /**
     * Discovers capabilities declared by the supplied class.
     *
     * <p>Only methods declared directly by {@code clazz} are inspected.
     * Inherited methods are not included.</p>
     *
     * @param clazz class to inspect
     * @return discovered capabilities
     * @throws NullPointerException if {@code clazz} is {@code null}
     */
    public List<Capability> discover(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz must not be null");

        List<Capability> capabilities = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            AiCapability annotation = method.getAnnotation(AiCapability.class);

            if (annotation == null) {
                continue;
            }

            capabilities.add(new Capability(annotation.name(), annotation.description(),
                    annotation.userInitiatedRequired(), Arrays.asList(annotation.allowedCallerTypes()),
                    Arrays.asList(annotation.requiredPermissions())
            ));
        }

        return capabilities;
    }
}