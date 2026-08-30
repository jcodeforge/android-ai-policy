package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import java.util.Objects;

public final class AndroidPackageResolver {

    private final Context context;

    public AndroidPackageResolver(Context context) {
        this.context = Objects.requireNonNull(context, "context must not be null")
                .getApplicationContext();
    }

    public String resolvePackageName(int uid) {
        String[] packages = context.getPackageManager().getPackagesForUid(uid);

        if (packages == null || packages.length == 0) {
            throw new IllegalStateException("Unable to resolve package for caller UID: " + uid);
        }

        return packages[0];
    }
}