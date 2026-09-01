package io.github.jcodeforge.aipolicy.android.capability;

import android.content.Context;
import java.util.Collection;
import io.github.jcodeforge.aipolicy.capability.Capability;
import io.github.jcodeforge.aipolicy.capability.CapabilityRegistry;

public final class AndroidCapabilityRegistry {

    private static final AndroidCapabilityRegistry INSTANCE = new AndroidCapabilityRegistry();

    private final CapabilityRegistry registry = new CapabilityRegistry();

    private AndroidCapabilityRegistry() {
    }

    public static AndroidCapabilityRegistry getInstance() {
        return INSTANCE;
    }

    void initialize(Context context) {
        // discovery will happen here
    }

    public Capability get(String name) {
        return registry.get(name);
    }

    public Collection<Capability> getAll() {
        return registry.getAll();
    }

    public boolean contains(String name) {
        return registry.contains(name);
    }
}