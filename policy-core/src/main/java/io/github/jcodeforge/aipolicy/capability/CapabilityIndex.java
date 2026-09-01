package io.github.jcodeforge.aipolicy.capability;

import java.util.List;

/**
 * Provides the capabilities generated for an application.
 */
public interface CapabilityIndex {

    /**
     * Returns all generated capabilities.
     *
     * @return generated capabilities
     */
    List<Capability> getCapabilities();
}