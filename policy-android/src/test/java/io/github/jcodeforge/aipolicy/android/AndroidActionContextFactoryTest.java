package io.github.jcodeforge.aipolicy.android;

import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.ApplicationState;
import io.github.jcodeforge.aipolicy.CallerType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AndroidActionContextFactoryTest {

    @Mock AndroidApplicationStateProvider stateProvider;
    @Mock AndroidCallerProvider callerProvider;
    @Mock AndroidCaller caller;

    @Test
    public void createsContextWithApplicationStateAndCallerIdentity() {
        when(stateProvider.getApplicationState()).thenReturn(ApplicationState.FOREGROUND);
        when(callerProvider.getCaller()).thenReturn(caller);
        when(caller.getPackageName()).thenReturn("com.example.agent");
        when(caller.getType()).thenReturn(AndroidCallerType.EXTERNAL);

        AndroidActionContextFactory factory = new AndroidActionContextFactory(stateProvider,
                callerProvider);

        ActionContext context = factory.create("customer.read", true);

        assertEquals("customer.read", context.getCapability());
        assertEquals("com.example.agent", context.getCallerIdentity().getId());
        assertEquals(CallerType.EXTERNAL, context.getCallerIdentity().getType());
        assertTrue(context.isUserInitiated());
        assertEquals(ApplicationState.FOREGROUND, context.getApplicationState());
    }

    @Test
    public void mapsSelfCallerType() {
        when(stateProvider.getApplicationState()).thenReturn(ApplicationState.FOREGROUND);
        when(callerProvider.getCaller()).thenReturn(caller);
        when(caller.getPackageName()).thenReturn("com.example.app");
        when(caller.getType()).thenReturn(AndroidCallerType.SELF);

        AndroidActionContextFactory factory = new AndroidActionContextFactory(stateProvider,
                callerProvider);

        ActionContext context = factory.create("customer.read", true);

        assertEquals(CallerType.SELF, context.getCallerIdentity().getType());
    }

    @Test
    public void mapsUnknownCallerType() {
        when(stateProvider.getApplicationState()).thenReturn(ApplicationState.UNKNOWN);
        when(callerProvider.getCaller()).thenReturn(caller);
        when(caller.getPackageName()).thenReturn("unknown");
        when(caller.getType()).thenReturn(AndroidCallerType.UNKNOWN);

        AndroidActionContextFactory factory = new AndroidActionContextFactory(
                stateProvider, callerProvider);

        ActionContext context = factory.create("customer.read", false);

        assertEquals(CallerType.UNKNOWN, context.getCallerIdentity().getType());
    }

    @Test(expected = NullPointerException.class)
    public void requiresStateProvider() {
        new AndroidActionContextFactory(null, callerProvider);
    }

    @Test(expected = NullPointerException.class)
    public void requiresCallerProvider() {
        new AndroidActionContextFactory(stateProvider, null);
    }

    @Test(expected = NullPointerException.class)
    public void requiresCallerFromProvider() {
        when(callerProvider.getCaller()).thenReturn(null);

        AndroidActionContextFactory factory = new AndroidActionContextFactory(stateProvider,
                callerProvider);

        factory.create("customer.read", true);
    }
}