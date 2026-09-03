package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.CallerType;
import io.github.jcodeforge.aipolicy.Decision;
import io.github.jcodeforge.aipolicy.PolicyResult;
import io.github.jcodeforge.aipolicy.android.provider.AndroidApplicationStateProvider;
import io.github.jcodeforge.aipolicy.android.provider.AndroidCallerProvider;
import io.github.jcodeforge.aipolicy.android.provider.BinderCallingUidProvider;
import io.github.jcodeforge.aipolicy.android.provider.DefaultAndroidApplicationStateProvider;
import io.github.jcodeforge.aipolicy.android.provider.ExternalAndroidCallerProvider;
import io.github.jcodeforge.aipolicy.android.provider.ProcessUidProvider;
import io.github.jcodeforge.aipolicy.android.provider.SelfAndroidCallerProvider;
import io.github.jcodeforge.aipolicy.capability.Capability;
import java.util.Objects;
import android.content.pm.PackageManager;
import android.os.Binder;

/**
 * Android integration for AI capability policy evaluation.
 *
 * <p>Capabilities are declared using {@link
 * io.github.jcodeforge.aipolicy.capability.AiCapability} and registered
 * automatically by the Android integration.</p>
 *
 * <p>This class evaluates whether a capability may be used in the current
 * Android context. It does not invoke the underlying application method.</p>
 */
public final class AndroidPolicy {

    private final Context context;

    private final AndroidActionContextFactory contextFactory;

    private final AndroidCapabilityRegistry capabilityRegistry;

    private final BinderCallingUidProvider callingUidProvider;

    private final boolean isExternalCall;

    private AndroidPolicy(Context context, AndroidActionContextFactory contextFactory,
                          AndroidCapabilityRegistry capabilityRegistry,
                          BinderCallingUidProvider callingUidProvider, boolean isExternalCall) {

        this.context = Objects.requireNonNull(context, "context must not be null");
        this.contextFactory = Objects.requireNonNull(contextFactory,
                "contextFactory must not be null");
        this.capabilityRegistry = Objects.requireNonNull(capabilityRegistry,
                "capabilityRegistry must not be null");
        this.callingUidProvider = callingUidProvider;
        this.isExternalCall = isExternalCall;
    }

    /**
     * Creates an Android policy for actions initiated by the application
     * itself.
     *
     * @param context Android context
     * @return policy configured for self-initiated calls
     * @throws NullPointerException if {@code context} is {@code null}
     */
    public static AndroidPolicy forSelfCalls(Context context) {
        Objects.requireNonNull(context, "context must not be null");

        Context applicationContext = context.getApplicationContext();

        AndroidApplicationStateProvider stateProvider = new DefaultAndroidApplicationStateProvider();
        ProcessUidProvider processUidProvider = new ProcessUidProvider();
        AndroidCallerProvider callerProvider = new SelfAndroidCallerProvider(applicationContext,
                processUidProvider);

        AndroidActionContextFactory contextFactory = new AndroidActionContextFactory(stateProvider,
                callerProvider);

        return new AndroidPolicy(applicationContext, contextFactory,
                AndroidCapabilityRegistry.getInstance(), null, false);
    }

    /**
     * Creates an Android policy for calls received from an external
     * Android Binder caller.
     *
     * @param context Android context
     * @return policy configured for external calls
     * @throws NullPointerException if {@code context} is {@code null}
     */
    public static AndroidPolicy forExternalCalls(Context context) {
        Objects.requireNonNull(context, "context must not be null");

        Context applicationContext = context.getApplicationContext();

        AndroidApplicationStateProvider stateProvider = new DefaultAndroidApplicationStateProvider();
        AndroidPackageResolver packageResolver = new AndroidPackageResolver(applicationContext);
        BinderCallingUidProvider callingUidProvider = new BinderCallingUidProvider();
        ProcessUidProvider processUidProvider = new ProcessUidProvider();

        AndroidCallerProvider callerProvider = new ExternalAndroidCallerProvider(packageResolver,
                callingUidProvider, processUidProvider);

        AndroidActionContextFactory contextFactory = new AndroidActionContextFactory(stateProvider,
                callerProvider);

        return new AndroidPolicy(applicationContext, contextFactory,
                AndroidCapabilityRegistry.getInstance(), callingUidProvider, true);
    }

    /**
     * Evaluates a registered capability.
     *
     * <p>The capability is resolved from the generated capability registry
     * and its metadata is evaluated against the current Android context.</p>
     *
     * <p>This method only evaluates the capability. It does not invoke the
     * underlying application method.</p>
     *
     * @param capabilityName capability name
     * @param userInitiated whether the action was explicitly initiated
     *                      by the user
     * @return the result of the policy evaluation
     * @throws NullPointerException if {@code capabilityName} is {@code null}
     */
    public PolicyResult evaluate(String capabilityName, boolean userInitiated) {
        Objects.requireNonNull(capabilityName, "capabilityName must not be null");

        Capability capability = capabilityRegistry.getCapability(capabilityName);

        if (capability == null) {
            return new PolicyResult(Decision.DENY, "Unknown capability: " + capabilityName);
        }

        ActionContext context = contextFactory.create(capabilityName, userInitiated);

        return evaluateCapability(capability, context);
    }

    private PolicyResult evaluateCapability(Capability capability, ActionContext context) {
        if (capability.isUserInitiatedRequired() && !context.isUserInitiated()) {
            return new PolicyResult(Decision.DENY, "Capability requires user initiation");
        }

        CallerType callerType = context.getCallerIdentity().getType();

        if (!capability.getAllowedCallerTypes().isEmpty()
                && !capability.getAllowedCallerTypes().contains(callerType)) {

            return new PolicyResult(Decision.DENY, "Caller type is not allowed");
        }

        for (String permission : capability.getRequiredPermissions()) {
            if (!hasPermission(permission)) {
                return new PolicyResult(Decision.DENY, "Required permission is missing: "
                        + permission);
            }
        }

        return new PolicyResult(Decision.ALLOW, null);
    }

    private boolean hasPermission(String permission) {
        if (!isExternalCall) {
            return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;

        }

        int callingUid = callingUidProvider.getCallingUid();

        return context.checkPermission(permission, callingUid, Binder.getCallingPid())
                == PackageManager.PERMISSION_GRANTED;

    }
}