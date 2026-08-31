package io.github.jcodeforge.aipolicy.android;

import java.util.Objects;
import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.CallerIdentity;
import io.github.jcodeforge.aipolicy.CallerType;
import io.github.jcodeforge.aipolicy.android.provider.AndroidApplicationStateProvider;
import io.github.jcodeforge.aipolicy.android.provider.AndroidCallerProvider;

/**
 * Creates {@link ActionContext} instances from Android-specific runtime
 * information.
 *
 * <p>This factory combines the current Android caller identity, application
 * lifecycle state, requested capability, and user-initiation information
 * into the platform-independent {@link ActionContext} used by the core
 * policy engine.</p>
 *
 * <p>The factory is an internal implementation detail of the Android
 * integration and is not part of the public Android API.</p>
 */
final class AndroidActionContextFactory {

    private final AndroidApplicationStateProvider stateProvider;
    private final AndroidCallerProvider callerProvider;

    /**
     * Creates an action context factory.
     *
     * @param stateProvider provider for the current application state
     * @param callerProvider provider for the current Android caller
     * @throws NullPointerException if {@code stateProvider} or
     *                              {@code callerProvider} is {@code null}
     */
    public AndroidActionContextFactory(AndroidApplicationStateProvider stateProvider,
                                       AndroidCallerProvider callerProvider) {

        this.stateProvider = Objects.requireNonNull(stateProvider,
                "stateProvider must not be null");

        this.callerProvider = Objects.requireNonNull(callerProvider,
                "callerProvider must not be null");
    }

    /**
     * Creates an {@link ActionContext} for the requested capability.
     *
     * <p>The caller identity is obtained from the configured caller
     * provider and mapped from {@link AndroidCallerType} to the
     * platform-independent {@link CallerType}. The current application
     * state is obtained from the configured state provider.</p>
     *
     * @param capability capability being requested
     * @param userInitiated whether the action was explicitly initiated
     *                      by the user
     * @return a populated action context
     * @throws NullPointerException if the caller provider returns
     *                              {@code null}, or if a required value
     *                              is {@code null}
     * @throws IllegalArgumentException if the Android caller type is
     *                                  unsupported
     */
    public ActionContext create(String capability, boolean userInitiated) {
        AndroidCaller caller = Objects.requireNonNull(callerProvider.getCaller(),
                "caller must not be null");

        CallerIdentity callerIdentity = new CallerIdentity(caller.getPackageName(),
                toCallerType(caller.getType()));

        return new ActionContext(capability, callerIdentity, userInitiated,
                stateProvider.getApplicationState());
    }

    /**
     * Converts an Android-specific caller type to the corresponding
     * platform-independent caller type.
     *
     * @param type Android caller type
     * @return corresponding core caller type
     * @throws IllegalArgumentException if the caller type is unsupported
     */
    private CallerType toCallerType(AndroidCallerType type) {
        switch (type) {
            case SELF: return CallerType.SELF;
            case EXTERNAL: return CallerType.EXTERNAL;
            case UNKNOWN: return CallerType.UNKNOWN;
            default: throw new IllegalArgumentException("Unsupported caller type: " + type);
        }
    }
}