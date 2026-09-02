package io.github.jcodeforge.aipolicy.android;

import java.util.Objects;

/**
 * Represents the Android caller associated with an action.
 *
 * <p>The caller contains Android-specific identity information.
 * It is converted to the platform-independent {@code CallerIdentity}
 * by the Android policy layer.</p>
 */
public final class AndroidCaller {

    private final AndroidCallerType type;
    private final int uid;
    private final String packageName;

    public AndroidCaller(AndroidCallerType type, int uid, String packageName) {
        this.type = Objects.requireNonNull(type, "type must not be null");

        if (uid < 0) {
            throw new IllegalArgumentException("uid must not be negative");
        }

        this.uid = uid;
        this.packageName = Objects.requireNonNull(packageName, "packageName must not be null");

        if (packageName.trim().isEmpty()) {
            throw new IllegalArgumentException("packageName must not be blank");
        }
    }

    public AndroidCallerType getType() {
        return type;
    }

    public int getUid() {
        return uid;
    }

    public String getPackageName() {
        return packageName;
    }
}