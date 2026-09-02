package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import io.github.jcodeforge.aipolicy.capability.AppFunctionCapability;
import io.github.jcodeforge.aipolicy.capability.AppFunctionCapabilityIndex;
import io.github.jcodeforge.aipolicy.capability.Capability;
import io.github.jcodeforge.aipolicy.capability.CapabilityIndex;
import io.github.jcodeforge.aipolicy.capability.CapabilityIndexProvider;
import io.github.jcodeforge.aipolicy.capability.CapabilityRegistry;

final class AndroidCapabilityRegistry {

    private static final AndroidCapabilityRegistry INSTANCE = new AndroidCapabilityRegistry();

    private final CapabilityRegistry registry = new CapabilityRegistry();

    private final Map<String, AppFunctionCapability> appFunctionCapabilities =
            new LinkedHashMap<>();

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

            Collection<Capability> capabilities =
                    Objects.requireNonNull(index.getCapabilities(),
                            "capabilities must not be null");

            registry.registerAll(capabilities);

            AppFunctionCapabilityIndex appFunctionIndex = Objects.requireNonNull(
                    provider.getAppFunctionCapabilityIndex(),
                    "app function capability index must not be null");

            List<AppFunctionCapability> appFunctionEntries = Objects.requireNonNull(
                    appFunctionIndex.getAppFunctionCapabilities(),
                    "app function capabilities must not be null");

            for (AppFunctionCapability appFunctionCapability : appFunctionEntries) {
                String functionId = appFunctionCapability.getFunctionId();

                if (appFunctionCapabilities.containsKey(functionId)) {
                    throw new IllegalArgumentException("AppFunction capability already registered: "
                            + functionId);
                }

                appFunctionCapabilities.put(functionId, appFunctionCapability);
            }
        }

        initialized = true;
    }

    public Collection<Capability> getCapabilities() {
        return registry.getAll();
    }

    public Capability getCapability(String name) {
        return registry.get(name);
    }

    public boolean hasCapability(String name) {
        return registry.contains(name);
    }

    public AppFunctionCapability getAppFunctionCapability(String functionId) {
        return appFunctionCapabilities.get(functionId);
    }

    public boolean hasAppFunctionCapability(String functionId) {
        return appFunctionCapabilities.containsKey(functionId);
    }

    public Collection<AppFunctionCapability> getAppFunctionCapabilities() {
        return List.copyOf(appFunctionCapabilities.values());
    }
}