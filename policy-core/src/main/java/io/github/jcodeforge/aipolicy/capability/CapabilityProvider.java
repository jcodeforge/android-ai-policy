package io.github.jcodeforge.aipolicy.capability;

import java.util.Collection;

/**
 * Provides access to capabilities registered by the application.
 *
 * <p>Implementations are responsible for exposing capabilities discovered
 * and registered by the policy framework.</p>
 */
public interface CapabilityProvider {

    /**
     * Returns all registered capabilities.
     *
     * @return all registered capabilities; never {@code null}
     */
    Collection<Capability> getCapabilities();

    /**
     * Returns a capability by name.
     *
     * @param name the capability name
     * @return the capability, or {@code null} if it is not registered
     */
    Capability getCapability(String name);

    /**
     * Determines whether a capability is registered.
     *
     * @param name the capability name
     * @return {@code true} if the capability exists
     */
    boolean hasCapability(String name);
}