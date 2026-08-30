package io.github.jcodeforge.aipolicy.android;

import org.junit.Test;

import static org.junit.Assert.*;

public class AndroidCallerTest {

    @Test
    public void callerCanBeCreated() {
        AndroidCaller caller = new AndroidCaller(AndroidCallerType.EXTERNAL, 12345,
                "com.example.agent");

        assertEquals(AndroidCallerType.EXTERNAL, caller.getType());
        assertEquals(12345, caller.getUid());
        assertEquals("com.example.agent", caller.getPackageName());
    }

    @Test(expected = NullPointerException.class)
    public void typeMustNotBeNull() {
        new AndroidCaller(null, 12345, "com.example.agent");
    }

    @Test(expected = IllegalArgumentException.class)
    public void uidMustNotBeNegative() {
        new AndroidCaller(AndroidCallerType.EXTERNAL, -1, "com.example.agent");
    }

    @Test(expected = NullPointerException.class)
    public void packageNameMustNotBeNull() {
        new AndroidCaller(AndroidCallerType.EXTERNAL, 12345, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void packageNameMustNotBeBlank() {
        new AndroidCaller(AndroidCallerType.EXTERNAL, 12345, "   ");
    }
}