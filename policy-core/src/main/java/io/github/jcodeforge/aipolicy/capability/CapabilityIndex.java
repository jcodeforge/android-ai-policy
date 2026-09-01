package io.github.jcodeforge.aipolicy.capability;

import java.util.List;

/**
 * Provides classes containing AI capabilities.
 */
public interface CapabilityIndex {

    /**
     * Returns classes that may contain {@link AiCapability} methods.
     *
     * @return capability classes
     */
    List<Class<?>> getCapabilityClasses();
}