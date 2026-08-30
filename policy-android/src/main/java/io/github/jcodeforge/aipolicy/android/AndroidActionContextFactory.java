package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import io.github.jcodeforge.aipolicy.ActionContext;

public final class AndroidActionContextFactory {

    private AndroidActionContextFactory() {
    }

    public static ActionContext create(Context context, String capability, String caller,
                                       boolean userInitiated) {

        throw new UnsupportedOperationException();
    }
}