package io.github.jcodeforge.aipolicy.android.capability;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.startup.Initializer;
import java.util.Collections;
import java.util.List;

public final class AndroidCapabilityRegistryInitializer
        implements Initializer<AndroidCapabilityRegistry> {

    @NonNull
    @Override
    public AndroidCapabilityRegistry create(@NonNull Context context) {
        AndroidCapabilityRegistry registry = AndroidCapabilityRegistry.getInstance();
        registry.initialize(context);

        return registry;
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return Collections.emptyList();
    }
}