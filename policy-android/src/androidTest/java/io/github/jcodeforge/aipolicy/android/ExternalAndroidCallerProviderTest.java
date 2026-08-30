package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExternalAndroidCallerProviderTest {

    @Test
    public void requiresContext() {
        try {
            new ExternalAndroidCallerProvider(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("context must not be null", expected.getMessage());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsLocalProcessAsExternalCaller() {
        Context context = ApplicationProvider.getApplicationContext();

        ExternalAndroidCallerProvider provider = new ExternalAndroidCallerProvider(context);

        provider.getCaller();
    }
}