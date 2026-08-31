package io.github.jcodeforge.aipolicy.android;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import io.github.jcodeforge.aipolicy.ApplicationState;
import io.github.jcodeforge.aipolicy.android.provider.DefaultAndroidApplicationStateProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DefaultAndroidApplicationStateProviderTest {

    @Test
    public void startsWithUnknownState() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                DefaultAndroidApplicationStateProvider provider =
                        new DefaultAndroidApplicationStateProvider();

                assertEquals(ApplicationState.UNKNOWN, provider.getApplicationState());
            }
        });
    }

    @Test
    public void changesToForegroundOnStart() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                DefaultAndroidApplicationStateProvider provider = new DefaultAndroidApplicationStateProvider();
                provider.onStart(null);

                assertEquals(ApplicationState.FOREGROUND, provider.getApplicationState());
            }
        });
    }

    @Test
    public void changesToBackgroundOnStop() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                DefaultAndroidApplicationStateProvider provider =
                        new DefaultAndroidApplicationStateProvider();
                provider.onStop(null);

                assertEquals(ApplicationState.BACKGROUND, provider.getApplicationState());
            }
        });
    }

    @Test
    public void changesFromForegroundToBackground() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                DefaultAndroidApplicationStateProvider provider = new DefaultAndroidApplicationStateProvider();

                provider.onStart(null);
                assertEquals(ApplicationState.FOREGROUND, provider.getApplicationState());

                provider.onStop(null);
                assertEquals(ApplicationState.BACKGROUND, provider.getApplicationState());
            }
        });
    }
}

