package io.github.jcodeforge.aipolicy.android.provider;

import android.os.Process;

public final class ProcessUidProvider {

    public int getUid() {
        return Process.myUid();
    }
}