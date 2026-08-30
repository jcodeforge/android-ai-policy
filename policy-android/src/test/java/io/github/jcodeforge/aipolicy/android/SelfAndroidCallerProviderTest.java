package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SelfAndroidCallerProviderTest {

    @Mock Context context;
    @Mock ProcessUidProvider processUidProvider;

    SelfAndroidCallerProvider SUT;

    @Before
    public void setup() {
        when(context.getApplicationContext()).thenReturn(context);
        when(processUidProvider.getUid()).thenReturn(12345);
        when(context.getPackageName()).thenReturn("com.example.app");

        SUT = new SelfAndroidCallerProvider(context, processUidProvider);
    }

    @Test
    public void returnsSelfCaller() {
        AndroidCaller caller = SUT.getCaller();

        assertEquals(AndroidCallerType.SELF, caller.getType());
        assertEquals(12345, caller.getUid());
        assertEquals("com.example.app", caller.getPackageName());
    }

    @Test(expected = NullPointerException.class)
    public void contextMustNotBeNull() {
        new SelfAndroidCallerProvider(null, processUidProvider);
    }

    @Test(expected = NullPointerException.class)
    public void processUidProviderMustNotBeNull() {
        new SelfAndroidCallerProvider(context, null);
    }
}