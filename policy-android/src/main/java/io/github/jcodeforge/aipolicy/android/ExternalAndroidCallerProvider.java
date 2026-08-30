package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import android.os.Binder;
import java.util.Objects;
import android.os.Process;

public final class ExternalAndroidCallerProvider implements AndroidCallerProvider {

    private final Context context;

    public ExternalAndroidCallerProvider(Context context) {
        this.context = Objects.requireNonNull(context, "context must not be null")
                .getApplicationContext();
    }

    @Override
    public AndroidCaller getCaller() {
        int uid = Binder.getCallingUid();

        if (uid == Process.myUid()) {
            throw new IllegalStateException("No external Binder caller is present");
        }

        String packageName = resolvePackageName(uid);

        return new AndroidCaller(AndroidCallerType.EXTERNAL, uid, packageName);
    }

    private String resolvePackageName(int uid) {
        String[] packages = context.getPackageManager().getPackagesForUid(uid);

        if (packages == null || packages.length == 0) {
            throw new IllegalStateException("Unable to resolve package for caller UID: " + uid);
        }

        return packages[0];
    }
}