package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import io.github.jcodeforge.aipolicy.capability.Capability;
import java.util.Collection;
import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.AiPolicy;
import io.github.jcodeforge.aipolicy.PolicyResult;
import io.github.jcodeforge.aipolicy.android.provider.AndroidApplicationStateProvider;
import io.github.jcodeforge.aipolicy.android.provider.AndroidCallerProvider;
import io.github.jcodeforge.aipolicy.android.provider.BinderCallingUidProvider;
import io.github.jcodeforge.aipolicy.android.provider.DefaultAndroidApplicationStateProvider;
import io.github.jcodeforge.aipolicy.android.provider.ExternalAndroidCallerProvider;
import io.github.jcodeforge.aipolicy.android.provider.ProcessUidProvider;
import io.github.jcodeforge.aipolicy.android.provider.SelfAndroidCallerProvider;
import io.github.jcodeforge.aipolicy.capability.CapabilityProvider;
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
 */
public final class AndroidPolicy {

    private final AiPolicy aiPolicy;

    private final AndroidActionContextFactory contextFactory;

    private final CapabilityProvider capabilityProvider;

    private AndroidPolicy(AiPolicy aiPolicy, AndroidActionContextFactory contextFactory,
                          CapabilityProvider capabilityProvider) {
        this.aiPolicy = Objects.requireNonNull(aiPolicy, "policy must not be null");
        this.contextFactory = Objects.requireNonNull(contextFactory,
                "contextFactory must not be null");
        this.capabilityProvider = Objects.requireNonNull(
                capabilityProvider,
                "capabilityProvider must not be null"
        );
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
     * @param capability capability to evaluate
     * @param userInitiated whether the action was explicitly initiated
     *                      by the user
     * @return the result of the policy evaluation
     * @throws NullPointerException if {@code capability} is {@code null}
     *                              and rejected by the underlying context
     *                              factory
     */
    public PolicyResult evaluate(String capability, boolean userInitiated) {
        ActionContext context = contextFactory.create(capability, userInitiated);

        return aiPolicy.evaluate(context);
    }

    /**
     * Returns all capabilities discovered in the application.
     *
     * <p>Capabilities are discovered and registered automatically when the
     * application starts. The returned collection represents the capabilities
     * currently available to the application.</p>
     *
     * @return all discovered capabilities; never {@code null}
     */
    public Collection<Capability> getCapabilities() {
        return capabilityProvider.getCapabilities();
    }

    /**
     * Returns a capability by name.
     *
     * @param name the capability name
     * @return the capability, or {@code null} if no such capability exists
     */
    public Capability getCapability(String name) {
        return capabilityProvider.getCapability(name);
    }

    /**
     * Determines whether a capability is registered.
     *
     * @param name the capability name
     * @return {@code true} if the capability exists
     */
    public boolean hasCapability(String name) {
        return capabilityProvider.hasCapability(name);
    }
}