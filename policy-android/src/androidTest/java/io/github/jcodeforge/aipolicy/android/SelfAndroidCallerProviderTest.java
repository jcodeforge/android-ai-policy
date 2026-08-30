package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SelfAndroidCallerProviderTest {

    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void returnsSelfCaller() {
        SelfAndroidCallerProvider provider = new SelfAndroidCallerProvider(context);

        AndroidCaller caller = provider.getCaller();

        assertEquals(AndroidCallerType.SELF, caller.getType());
        assertEquals(android.os.Process.myUid(), caller.getUid());
        assertEquals(context.getPackageName(), caller.getPackageName());
    }

    @Test(expected = NullPointerException.class)
    public void contextMustNotBeNull() {
        new SelfAndroidCallerProvider(null);
    }
}