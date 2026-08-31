package io.github.jcodeforge.aipolicy.android;

import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.android.condition.AndroidPermissionCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.pm.PackageManager;

@RunWith(MockitoJUnitRunner.class)
public class AndroidPermissionConditionTest {

    @Mock Context androidContext;
    @Mock ActionContext actionContext;

    private AndroidPermissionCondition SUT;

    @Before
    public void setup() {
        when(androidContext.getApplicationContext()).thenReturn(androidContext);

        SUT = new AndroidPermissionCondition(androidContext, "android.permission.CAMERA");
    }

    @Test
    public void matchesWhenPermissionIsGranted() {
        when(androidContext.checkSelfPermission("android.permission.CAMERA"))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        assertTrue(SUT.matches(actionContext));
    }

    @Test
    public void doesNotMatchWhenPermissionIsDenied() {
        when(androidContext.checkSelfPermission("android.permission.CAMERA"))
                .thenReturn(PackageManager.PERMISSION_DENIED);

        assertFalse(SUT.matches(actionContext));
    }

    @Test(expected = NullPointerException.class)
    public void requiresPermissionProvider() {
        new AndroidPermissionCondition(null, "android.permission.CAMERA");
    }

    @Test(expected = NullPointerException.class)
    public void requiresPermission() {
        new AndroidPermissionCondition(androidContext, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBlankPermission() {
        new AndroidPermissionCondition(androidContext, "   ");
    }

    @Test(expected = NullPointerException.class)
    public void requiresContext() {
        SUT.matches(null);
    }
}