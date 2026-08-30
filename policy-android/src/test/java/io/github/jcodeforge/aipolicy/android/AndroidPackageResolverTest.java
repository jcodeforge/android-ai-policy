package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import android.content.pm.PackageManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AndroidPackageResolverTest {

    @Mock Context context;
    @Mock PackageManager packageManager;

    AndroidPackageResolver SUT;

    @Before
    public void setup() {
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getPackageManager()).thenReturn(packageManager);

        SUT = new AndroidPackageResolver(context);
    }

    @Test
    public void resolvesPackageNameForUid() {
        when(packageManager.getPackagesForUid(12345)).thenReturn(new String[]{"com.example.agent"});

        String packageName = SUT.resolvePackageName(12345);

        assertEquals("com.example.agent", packageName);
    }

    @Test
    public void returnsFirstPackageWhenUidHasMultiplePackages() {
        when(packageManager.getPackagesForUid(12345)).thenReturn(new String[]{
                "com.example.agent",
                "com.example.other"});

        assertEquals("com.example.agent", SUT.resolvePackageName(12345));
    }

    @Test(expected = IllegalStateException.class)
    public void throwsWhenUidCannotBeResolved() {
        when(packageManager.getPackagesForUid(12345)).thenReturn(null);

        SUT.resolvePackageName(12345);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsWhenUidHasNoPackages() {
        when(packageManager.getPackagesForUid(12345)).thenReturn(new String[0]);

        SUT.resolvePackageName(12345);
    }

    @Test(expected = NullPointerException.class)
    public void requiresContext() {
        new AndroidPackageResolver(null);
    }
}
