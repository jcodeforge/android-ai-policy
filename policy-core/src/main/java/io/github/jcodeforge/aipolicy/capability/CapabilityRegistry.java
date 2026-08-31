package io.github.jcodeforge.aipolicy.capability;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Registry of AI-accessible capabilities.
 *
 * <p>Capabilities are registered by their unique name and can later
 * be retrieved or enumerated for discovery and invocation.</p>
 */
public final class CapabilityRegistry {

    private final Map<String, Capability> capabilities = new LinkedHashMap<>();

    /**
     * Registers a capability.
     *
     * @param capability capability to register
     * @throws NullPointerException if {@code capability} is {@code null}
     * @throws IllegalArgumentException if a capability with the same
     *                                  name is already registered
     */
    public void register(Capability capability) {
        Objects.requireNonNull(capability, "capability must not be null");

        String name = capability.getName();

        if (capabilities.containsKey(name)) {
            throw new IllegalArgumentException("Capability already registered: " + name);
        }

        capabilities.put(name, capability);
    }

    /**
     * Registers all supplied capabilities.
     *
     * @param capabilities capabilities to register
     * @throws NullPointerException if {@code capabilities} or an element
     *                              is {@code null}
     * @throws IllegalArgumentException if a capability with the same name
     *                                  is already registered
     */
    public void registerAll(Collection<Capability> capabilities) {
        Objects.requireNonNull(capabilities, "capabilities must not be null");

        for (Capability capability : capabilities) {
            register(capability);
        }
    }

    /**
     * Returns the capability registered under the given name.
     *
     * @param name capability name
     * @return the registered capability, or {@code null} if none exists
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public Capability get(String name) {
        Objects.requireNonNull(name, "name must not be null");

        return capabilities.get(name);
    }

    /**
     * Returns all registered capabilities.
     *
     * @return an immutable collection of registered capabilities
     */
    public Collection<Capability> getAll() {
        return Collections.unmodifiableCollection(capabilities.values());
    }

    /**
     * Determines whether a capability is registered.
     *
     * @param name capability name
     * @return {@code true} if the capability is registered
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public boolean contains(String name) {
        Objects.requireNonNull(name, "name must not be null");

        return capabilities.containsKey(name);
    }

    /**
     * Returns the number of registered capabilities.
     *
     * @return number of registered capabilities
     */
    public int size() {
        return capabilities.size();
    }
}