package io.github.jcodeforge.aipolicy.android.capability;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@RunWith(AndroidJUnit4.class)
public class AndroidCapabilityRegistryTest {

    @Test
    public void returnsSingletonInstance() {
        AndroidCapabilityRegistry first = AndroidCapabilityRegistry.getInstance();
        AndroidCapabilityRegistry second = AndroidCapabilityRegistry.getInstance();

        assertSame(first, second);
    }

    @Test
    public void isInitializedAutomatically() {
        Context context = ApplicationProvider.getApplicationContext();
        AndroidCapabilityRegistry registry = AndroidCapabilityRegistry.getInstance();

        assertNotNull(registry);
    }
}