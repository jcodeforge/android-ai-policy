package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.AiPolicy;
import io.github.jcodeforge.aipolicy.Decision;
import io.github.jcodeforge.aipolicy.PolicyResult;
import io.github.jcodeforge.aipolicy.android.provider.AndroidApplicationStateProvider;
import io.github.jcodeforge.aipolicy.android.provider.AndroidCallerProvider;
import io.github.jcodeforge.aipolicy.android.provider.BinderCallingUidProvider;
import io.github.jcodeforge.aipolicy.android.provider.DefaultAndroidApplicationStateProvider;
import io.github.jcodeforge.aipolicy.android.provider.ExternalAndroidCallerProvider;
import io.github.jcodeforge.aipolicy.android.provider.ProcessUidProvider;
import io.github.jcodeforge.aipolicy.android.provider.SelfAndroidCallerProvider;
import java.util.Objects;

/**
 * Android integration for {@link AiPolicy}.
 *
 * <p>{@code AndroidPolicy} provides the Android-specific context required
 * for policy evaluation, including the caller identity and application
 * lifecycle state.</p>
 *
 * <p>Use {@link #forSelfCalls(Context, AiPolicy)} when evaluating actions
 * initiated by the application itself. Use
 * {@link #forExternalCalls(Context, AiPolicy)} when evaluating calls
 * received from an external Android Binder caller.</p>
 *
 * <p>The Android-specific context and caller resolution are created
 * internally. Applications only need to provide an Android
 * {@link Context} and an {@link AiPolicy}.</p>
 *
 * <p>Capabilities are discovered and registered automatically by the
 * Android integration. Applications do not need to create, initialize,
 * or manage a capability registry.</p>
 */
public final class AndroidPolicy {

    private final AiPolicy aiPolicy;

    private final AndroidActionContextFactory contextFactory;

    private final AndroidCapabilityRegistry capabilityRegistry;

    private AndroidPolicy(AiPolicy aiPolicy, AndroidActionContextFactory contextFactory,
                          AndroidCapabilityRegistry capabilityRegistry) {
        this.aiPolicy = Objects.requireNonNull(aiPolicy, "policy must not be null");
        this.contextFactory = Objects.requireNonNull(contextFactory,
                "contextFactory must not be null");
        this.capabilityRegistry = Objects.requireNonNull(capabilityRegistry,
                "capabilityProvider must not be null");
    }

    /**
     * Creates an {@code AndroidPolicy} for actions initiated by the
     * application itself.
     *
     * <p>The resulting policy identifies the current application as
     * {@link AndroidCallerType#SELF}.</p>
     *
     * @param context Android context used to obtain the application context
     * @param aiPolicy policy to evaluate
     * @return an Android policy configured for self-initiated calls
     * @throws NullPointerException if {@code context} or {@code aiPolicy}
     *                              is {@code null}
     */
    public static AndroidPolicy forSelfCalls(Context context, AiPolicy aiPolicy) {
        Objects.requireNonNull(context, "context must not be null");
        Objects.requireNonNull(aiPolicy, "policy must not be null");

        Context applicationContext = context.getApplicationContext();

        AndroidApplicationStateProvider stateProvider = new DefaultAndroidApplicationStateProvider();
        ProcessUidProvider processUidProvider = new ProcessUidProvider();
        AndroidCallerProvider callerProvider = new SelfAndroidCallerProvider(applicationContext,
                processUidProvider);

        AndroidActionContextFactory contextFactory = new AndroidActionContextFactory(stateProvider,
                callerProvider);

        return new AndroidPolicy(aiPolicy, contextFactory, AndroidCapabilityRegistry.getInstance());
    }

    /**
     * Creates an {@code AndroidPolicy} for calls received from an
     * external Android Binder caller.
     *
     * <p>The resulting policy resolves the calling UID and associated
     * package name and identifies the caller as
     * {@link AndroidCallerType#EXTERNAL}.</p>
     *
     * @param context Android context used to resolve the calling package
     * @param aiPolicy policy to evaluate
     * @return an Android policy configured for external Binder calls
     * @throws NullPointerException if {@code context} or {@code aiPolicy}
     *                              is {@code null}
     */
    public static AndroidPolicy forExternalCalls(Context context, AiPolicy aiPolicy) {
        Objects.requireNonNull(context, "context must not be null");
        Objects.requireNonNull(aiPolicy, "policy must not be null");

        Context applicationContext = context.getApplicationContext();

        AndroidApplicationStateProvider stateProvider = new DefaultAndroidApplicationStateProvider();
        AndroidPackageResolver packageResolver = new AndroidPackageResolver(applicationContext);
        BinderCallingUidProvider callingUidProvider = new BinderCallingUidProvider();
        ProcessUidProvider processUidProvider = new ProcessUidProvider();

        AndroidCallerProvider callerProvider = new ExternalAndroidCallerProvider(packageResolver,
                callingUidProvider, processUidProvider);

        AndroidActionContextFactory contextFactory = new AndroidActionContextFactory(stateProvider,
                callerProvider);

        return new AndroidPolicy(aiPolicy, contextFactory, AndroidCapabilityRegistry.getInstance());
    }

    /**
     * Evaluates a capability using the configured Android context.
     *
     * <p>The capability name is evaluated against the configured
     * {@link AiPolicy}. The returned result describes whether the
     * requested action is allowed.</p>
     *
     * <p>This method only evaluates the policy. It does not invoke
     * the underlying application method associated with the capability.</p>
     *
     * @param capability capability name to evaluate
     * @param userInitiated whether the action was explicitly initiated
     *                      by the user
     * @return the result of the policy evaluation
     * @throws NullPointerException if {@code capability} is {@code null}
     */
    public PolicyResult evaluate(String capability, boolean userInitiated) {
        Objects.requireNonNull(capability, "capability must not be null");

        if (!capabilityRegistry.hasCapability(capability)) {
            return new PolicyResult(Decision.DENY, "Unknown capability: " + capability);
        }

        ActionContext context = contextFactory.create(capability, userInitiated);

        return aiPolicy.evaluate(context);
    }
}