package io.github.jcodeforge.aipolicy.android;

import org.junit.Test;
import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.ApplicationState;
import io.github.jcodeforge.aipolicy.CallerType;

import static org.junit.Assert.*;

public class AndroidActionContextFactoryTest {

    @Test
    public void createsContextWithApplicationStateAndCallerIdentity() {
        AndroidApplicationStateProvider stateProvider = new AndroidApplicationStateProvider() {
            @Override
            public ApplicationState getApplicationState() {
                return ApplicationState.FOREGROUND;
            }
        };

        AndroidCallerProvider callerProvider = new AndroidCallerProvider() {
            @Override
            public AndroidCaller getCaller() {
                return new AndroidCaller(AndroidCallerType.EXTERNAL, 12345,
                        "com.example.agent");
                    }
                };

        AndroidActionContextFactory factory = new AndroidActionContextFactory(stateProvider,
                callerProvider);

        ActionContext context = factory.create("customer.read", true);

        assertEquals("customer.read", context.getCapability());
        assertEquals("com.example.agent", context.getCallerIdentity().getId());
        assertEquals(CallerType.EXTERNAL, context.getCallerIdentity().getType());
        assertTrue(context.isUserInitiated());
        assertEquals(ApplicationState.FOREGROUND, context.getApplicationState());
    }

    @Test(expected = NullPointerException.class)
    public void requiresStateProvider() {
        new AndroidActionContextFactory(null, new AndroidCallerProvider() {
            @Override
            public AndroidCaller getCaller() {
                return new AndroidCaller(AndroidCallerType.SELF, 12345, "com.example.app");
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void requiresCallerProvider() {
        new AndroidActionContextFactory(new AndroidApplicationStateProvider() {
            @Override
            public ApplicationState getApplicationState() {
                return ApplicationState.UNKNOWN;
            }
            }, null);
    }
}