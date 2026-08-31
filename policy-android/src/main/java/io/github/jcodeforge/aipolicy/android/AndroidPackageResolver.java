package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import java.util.Objects;

/**
 * Resolves Android package names from process UIDs.
 *
 * <p>This class uses the application's {@link Context} to query the Android
 * package manager and determine which package is associated with a given UID.</p>
 *
 * <p>The application context is retained to avoid holding a reference to a
 * shorter-lived Android component context.</p>
 */
public final class AndroidPackageResolver {

    private final Context context;

    /**
     * Creates a package resolver using the supplied Android context.
     *
     * @param context Android context used to access the package manager
     * @throws NullPointerException if {@code context} is {@code null}
     */
    public AndroidPackageResolver(Context context) {
        this.context = Objects.requireNonNull(context, "context must not be null")
                .getApplicationContext();
    }

    /**
     * Resolves the package name associated with the specified UID.
     *
     * <p>If multiple packages are associated with the UID, the first
     * package returned by the Android package manager is used.</p>
     *
     * @param uid Android process UID
     * @return the package name associated with the UID
     * @throws IllegalStateException if no package can be resolved for
     *                               the specified UID
     */
    public String resolvePackageName(int uid) {
        String[] packages = context.getPackageManager().getPackagesForUid(uid);

        if (packages == null || packages.length == 0) {
            throw new IllegalStateException("Unable to resolve package for caller UID: " + uid);
        }

        return packages[0];
    }
}