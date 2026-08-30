package io.github.jcodeforge.aipolicy.android.provider;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import io.github.jcodeforge.aipolicy.ApplicationState;

/**
 * Default implementation that tracks the application's foreground/background
 * state using ProcessLifecycleOwner.
 */
public final class DefaultAndroidApplicationStateProvider implements AndroidApplicationStateProvider,
        DefaultLifecycleObserver {

    private volatile ApplicationState state = ApplicationState.UNKNOWN;

    public DefaultAndroidApplicationStateProvider() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public ApplicationState getApplicationState() {
        return state;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        state = ApplicationState.FOREGROUND;
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        state = ApplicationState.BACKGROUND;
    }
}