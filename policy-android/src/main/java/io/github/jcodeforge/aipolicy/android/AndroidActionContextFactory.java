package io.github.jcodeforge.aipolicy.android;

import java.util.Objects;
import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.CallerIdentity;
import io.github.jcodeforge.aipolicy.CallerType;

public final class AndroidActionContextFactory {

    private final AndroidApplicationStateProvider stateProvider;
    private final AndroidCallerProvider callerProvider;

    public AndroidActionContextFactory(AndroidApplicationStateProvider stateProvider,
                                       AndroidCallerProvider callerProvider) {

        this.stateProvider = Objects.requireNonNull(stateProvider,
                "stateProvider must not be null");

        this.callerProvider = Objects.requireNonNull(callerProvider,
                "callerProvider must not be null");
    }

    public ActionContext create(String capability, boolean userInitiated) {
        AndroidCaller caller = callerProvider.getCaller();

        CallerIdentity callerIdentity = new CallerIdentity(
                caller.getPackageName(),
                toCallerType(caller.getType())
        );

        return new ActionContext(capability, callerIdentity, userInitiated,
                stateProvider.getApplicationState());
    }

    private CallerType toCallerType(AndroidCallerType type) {
        switch (type) {
            case SELF: return CallerType.SELF;
            case EXTERNAL: return CallerType.EXTERNAL;
            case UNKNOWN: return CallerType.UNKNOWN;
            default: throw new IllegalArgumentException("Unsupported caller type: " + type);
        }
    }
}