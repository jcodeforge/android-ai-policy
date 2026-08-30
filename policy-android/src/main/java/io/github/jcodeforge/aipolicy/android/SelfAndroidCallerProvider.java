package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import java.util.Objects;

public final class SelfAndroidCallerProvider implements AndroidCallerProvider {

    private final Context context;
    private final ProcessUidProvider processUidProvider;

    public SelfAndroidCallerProvider(Context context, ProcessUidProvider processUidProvider) {
        this.context = Objects.requireNonNull(context,
                "context must not be null").getApplicationContext();

        this.processUidProvider = Objects.requireNonNull(processUidProvider,
                "processUidProvider must not be null");
    }

    @Override
    public AndroidCaller getCaller() {
        return new AndroidCaller(AndroidCallerType.SELF, processUidProvider.getUid(),
                context.getPackageName());
    }
}