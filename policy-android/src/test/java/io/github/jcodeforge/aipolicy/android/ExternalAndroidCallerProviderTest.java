package io.github.jcodeforge.aipolicy.android;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternalAndroidCallerProviderTest {

    @Mock AndroidPackageResolver resolver;
    @Mock BinderCallingUidProvider callingUidProvider;
    @Mock ProcessUidProvider processUidProvider;

    @Test
    public void requiresResolver() {
        try {
            new ExternalAndroidCallerProvider(null, callingUidProvider,
                    processUidProvider);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("packageResolver must not be null", expected.getMessage());
        }
    }

    @Test
    public void requiresCallingUidProvider() {
        try {
            new ExternalAndroidCallerProvider(resolver, null, processUidProvider);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("callingUidProvider must not be null", expected.getMessage());
        }
    }

    @Test
    public void requiresProcessUidProvider() {
        try {
            new ExternalAndroidCallerProvider(resolver, callingUidProvider, null);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("processUidProvider must not be null", expected.getMessage());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsLocalProcessAsExternalCaller() {
        when(callingUidProvider.getCallingUid()).thenReturn(12345);
        when(processUidProvider.getUid()).thenReturn(12345);

        ExternalAndroidCallerProvider provider = new ExternalAndroidCallerProvider(resolver,
                callingUidProvider, processUidProvider);

        provider.getCaller();
    }

    @Test
    public void createsExternalCaller() {
        when(callingUidProvider.getCallingUid()).thenReturn(12345);
        when(processUidProvider.getUid()).thenReturn(54321);
        when(resolver.resolvePackageName(12345)).thenReturn("com.example.agent");

        ExternalAndroidCallerProvider provider = new ExternalAndroidCallerProvider(resolver,
                callingUidProvider, processUidProvider);

        AndroidCaller caller = provider.getCaller();

        assertEquals(AndroidCallerType.EXTERNAL, caller.getType());
        assertEquals(12345, caller.getUid());
        assertEquals("com.example.agent", caller.getPackageName());
    }
}