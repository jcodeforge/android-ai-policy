package io.github.jcodeforge.aipolicy.android.condition;

import android.content.Context;
import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.android.provider.AndroidPermissionProvider;
import io.github.jcodeforge.aipolicy.condition.PolicyCondition;
import java.util.Objects;

public final class AndroidPermissionCondition implements PolicyCondition {

    private final AndroidPermissionProvider permissionProvider;
    private final String permission;

    public AndroidPermissionCondition(Context context, String permission) {
        Objects.requireNonNull(context, "context must not be null");
        this.permissionProvider = new AndroidPermissionProvider(context.getApplicationContext());

        this.permission = Objects.requireNonNull(permission, "permission must not be null");

        if (permission.trim().isEmpty()) {
            throw new IllegalArgumentException("permission must not be blank");
        }
    }

    @Override
    public boolean matches(ActionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        return permissionProvider.hasPermission(permission);
    }
}