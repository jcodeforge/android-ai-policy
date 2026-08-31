package io.github.jcodeforge.aipolicy.android.provider;

import android.content.Context;
import android.content.pm.PackageManager;
import java.util.Objects;

public final class AndroidPermissionProvider {

    private final Context context;

    public AndroidPermissionProvider(Context context) {
        this.context = Objects.requireNonNull(context, "context must not be null")
                .getApplicationContext();
    }

    public boolean hasPermission(String permission) {
        Objects.requireNonNull(permission, "permission must not be null");

        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
}