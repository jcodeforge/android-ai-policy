package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import android.content.pm.PackageManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import io.github.jcodeforge.aipolicy.android.provider.AndroidPermissionProvider;

@RunWith(MockitoJUnitRunner.class)
public class AndroidPermissionProviderTest {

    @Mock Context context;

    private AndroidPermissionProvider SUT;

    @Before
    public void setup() {
        when(context.getApplicationContext()).thenReturn(context);

        SUT = new AndroidPermissionProvider(context);
    }

    @Test
    public void returnsTrueWhenPermissionIsGranted() {
        when(context.checkSelfPermission("android.permission.INTERNET"))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        assertTrue(SUT.hasPermission("android.permission.INTERNET"));
    }

    @Test
    public void returnsFalseWhenPermissionIsDenied() {
        when(context.checkSelfPermission("android.permission.CAMERA"))
                .thenReturn(PackageManager.PERMISSION_DENIED);

        assertFalse(SUT.hasPermission("android.permission.CAMERA"));
    }

    @Test(expected = NullPointerException.class)
    public void requiresContext() {
        new AndroidPermissionProvider(null);
    }

    @Test(expected = NullPointerException.class)
    public void requiresPermission() {
        SUT.hasPermission(null);
    }
}