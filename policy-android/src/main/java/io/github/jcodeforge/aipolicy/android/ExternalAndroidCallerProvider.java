package io.github.jcodeforge.aipolicy.android;

import java.util.Objects;

public final class ExternalAndroidCallerProvider implements AndroidCallerProvider {

    private final AndroidPackageResolver packageResolver;
    private final BinderCallingUidProvider callingUidProvider;
    private final ProcessUidProvider processUidProvider;

    public ExternalAndroidCallerProvider(AndroidPackageResolver packageResolver,
                                         BinderCallingUidProvider callingUidProvider,
                                         ProcessUidProvider processUidProvider) {
        this.packageResolver = Objects.requireNonNull(packageResolver,
                "packageResolver must not be null");

        this.callingUidProvider = Objects.requireNonNull(callingUidProvider,
                "callingUidProvider must not be null"
        );

        this.processUidProvider = Objects.requireNonNull(processUidProvider,
                "processUidProvider must not be null"
        );
    }

    @Override
    public AndroidCaller getCaller() {
        int uid = callingUidProvider.getCallingUid();

        if (uid == processUidProvider.getUid()) {
            throw new IllegalStateException("No external Binder caller is present");
        }

        String packageName = packageResolver.resolvePackageName(uid);

        return new AndroidCaller(AndroidCallerType.EXTERNAL, uid, packageName);
    }
}