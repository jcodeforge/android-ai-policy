package io.github.jcodeforge.aipolicy.android.condition;

import android.content.Context;
import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.android.provider.AndroidPermissionProvider;
import io.github.jcodeforge.aipolicy.condition.PolicyCondition;
import java.util.Objects;

/**
 * A policy condition that checks whether the application holds
 * a specified Android permission.
 *
 * <p>The condition uses an {@link AndroidPermissionProvider} internally
 * to query the current permission state from the Android application
 * context.</p>
 *
 * <p>This condition is Android-specific and implements the core
 * {@link PolicyCondition} abstraction so that it can be used as part
 * of an {@code AiPolicy}.</p>
 */
public final class AndroidPermissionCondition implements PolicyCondition {

    private final AndroidPermissionProvider permissionProvider;
    private final String permission;

    /**
     * Creates an Android permission condition.
     *
     * @param context Android context used to check the permission
     * @param permission Android permission to check
     * @throws NullPointerException if {@code context} or {@code permission}
     *                              is {@code null}
     * @throws IllegalArgumentException if {@code permission} is blank
     */
    public AndroidPermissionCondition(Context context, String permission) {
        Objects.requireNonNull(context, "context must not be null");
        this.permissionProvider = new AndroidPermissionProvider(context.getApplicationContext());

        this.permission = Objects.requireNonNull(permission, "permission must not be null");

        if (permission.trim().isEmpty()) {
            throw new IllegalArgumentException("permission must not be blank");
        }
    }

    /**
     * Determines whether the required Android permission is currently
     * granted.
     *
     * <p>The supplied {@link ActionContext} is validated but is not
     * otherwise used because the permission state is obtained directly
     * from the Android permission provider.</p>
     *
     * @param context action context for the policy evaluation
     * @return {@code true} if the required Android permission is granted;
     *         {@code false} otherwise
     * @throws NullPointerException if {@code context} is {@code null}
     */
    @Override
    public boolean matches(ActionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        return permissionProvider.hasPermission(permission);
    }
}