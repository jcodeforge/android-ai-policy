package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import android.os.Process;

public final class SelfAndroidCallerProvider implements AndroidCallerProvider {

    private final Context context;

    public SelfAndroidCallerProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public AndroidCaller getCaller() {
        return new AndroidCaller(AndroidCallerType.SELF, Process.myUid(), context.getPackageName());
    }
}