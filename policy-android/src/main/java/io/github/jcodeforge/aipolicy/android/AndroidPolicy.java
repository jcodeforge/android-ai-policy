package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
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
import java.util.Objects;

public final class AndroidPolicy {

    private final AiPolicy aiPolicy;

    private final AndroidActionContextFactory contextFactory;

    private AndroidPolicy(AiPolicy aiPolicy, AndroidActionContextFactory contextFactory) {
        this.aiPolicy = Objects.requireNonNull(aiPolicy, "policy must not be null");
        this.contextFactory = Objects.requireNonNull(contextFactory, "context must not be null");
    }

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

        return new AndroidPolicy(aiPolicy, contextFactory);
    }

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

        return new AndroidPolicy(aiPolicy, contextFactory);
    }

    public PolicyResult evaluate(String capability, boolean userInitiated) {
        ActionContext context = contextFactory.create(capability, userInitiated);

        return aiPolicy.evaluate(context);
    }
}