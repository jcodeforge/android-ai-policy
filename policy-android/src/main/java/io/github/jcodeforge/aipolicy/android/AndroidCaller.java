package io.github.jcodeforge.aipolicy.android;

import java.util.Objects;

/**
 * Identifies the Android process that initiated an action.
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