package io.github.jcodeforge.aipolicy.android.provider;

import android.os.Binder;

public final class BinderCallingUidProvider {

    public int getCallingUid() {
        return Binder.getCallingUid();
    }
}