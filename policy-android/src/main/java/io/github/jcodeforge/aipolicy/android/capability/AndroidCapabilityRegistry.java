package io.github.jcodeforge.aipolicy.android.capability;

import android.content.Context;
import java.util.Collection;
import java.util.Objects;
import java.util.ServiceLoader;
import io.github.jcodeforge.aipolicy.capability.Capability;
import io.github.jcodeforge.aipolicy.capability.CapabilityDiscovery;
import io.github.jcodeforge.aipolicy.capability.CapabilityIndex;
import io.github.jcodeforge.aipolicy.capability.CapabilityIndexProvider;
import io.github.jcodeforge.aipolicy.capability.CapabilityProvider;
import io.github.jcodeforge.aipolicy.capability.CapabilityRegistry;

public final class AndroidCapabilityRegistry implements CapabilityProvider {

    private static final AndroidCapabilityRegistry INSTANCE = new AndroidCapabilityRegistry();

    private final CapabilityRegistry registry = new CapabilityRegistry();

    private final CapabilityDiscovery discovery = new CapabilityDiscovery();

    private boolean initialized;

    private AndroidCapabilityRegistry() {
    }

    public static AndroidCapabilityRegistry getInstance() {
        return INSTANCE;
    }

    void initialize(Context context) {
        Objects.requireNonNull(context, "context must not be null");

        if (initialized) {
            return;
        }

        ServiceLoader<CapabilityIndexProvider> loader = ServiceLoader.load(CapabilityIndexProvider.class);

        for (CapabilityIndexProvider provider : loader) {
            CapabilityIndex index = Objects.requireNonNull(provider.getCapabilityIndex(),
                    "capability index must not be null");

            for (Class<?> capabilityClass : index.getCapabilityClasses()) {
                Collection<Capability> capabilities = discovery.discover(capabilityClass);
                registry.registerAll(capabilities);
            };
        }

        initialized = true;
    }

    @Override
    public Collection<Capability> getCapabilities() {
        return registry.getAll();
    }

    @Override
    public Capability getCapability(String name) {
        return registry.get(name);
    }

    @Override
    public boolean hasCapability(String name) {
        return registry.contains(name);
    }
}