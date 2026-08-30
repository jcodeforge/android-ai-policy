package io.github.jcodeforge.aipolicy.android;

import android.os.Binder;

public final class BinderCallingUidProvider {

    public int getCallingUid() {
        return Binder.getCallingUid();
    }
}