package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.github.jcodeforge.aipolicy.AiPolicy;
import io.github.jcodeforge.aipolicy.Decision;
import io.github.jcodeforge.aipolicy.PolicyResult;
import io.github.jcodeforge.aipolicy.PolicyRule;
import io.github.jcodeforge.aipolicy.android.condition.AndroidPermissionCondition;
import io.github.jcodeforge.aipolicy.condition.AndCondition;
import io.github.jcodeforge.aipolicy.condition.CallerCondition;
import io.github.jcodeforge.aipolicy.condition.UserInitiatedCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import androidx.test.platform.app.InstrumentationRegistry;

@RunWith(AndroidJUnit4.class)
public class AndroidPolicyTest {

    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test(expected = NullPointerException.class)
    public void requiresContext() {
        AiPolicy aiPolicy = AiPolicy.builder()
                .addRule("customer.read", new PolicyRule(Decision.ALLOW, null))
                .build();

        AndroidPolicy.forSelfCalls(null, aiPolicy);
    }

    @Test(expected = NullPointerException.class)
    public void requiresPolicy() {
        AndroidPolicy.forSelfCalls(context, null);
    }

    @Test
    public void createsPolicyForSelfCalls() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AiPolicy aiPolicy = AiPolicy.builder()
                        .addRule("customer.read", new PolicyRule(Decision.ALLOW, null))
                        .build();

                AndroidPolicy policy = AndroidPolicy.forSelfCalls(context, aiPolicy);

                assertNotNull(policy);
            }
        });
    }

    @Test
    public void evaluatesSelfCall() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                String packageName = context.getPackageName();

                AiPolicy aiPolicy = AiPolicy.builder()
                        .addRule("customer.read", new PolicyRule(Decision.ALLOW, null,
                                new CallerCondition(packageName)))
                        .build();

                AndroidPolicy policy = AndroidPolicy.forSelfCalls(context, aiPolicy);

                PolicyResult result = policy.evaluate("customer.read", true);

                assertTrue(result.isAllowed());
            }
        });
    }

    @Test
    public void evaluatesSelfCallWithMultipleConditions() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                String packageName = context.getPackageName();

                AiPolicy aiPolicy = AiPolicy.builder()
                        .addRule("customer.delete", new PolicyRule(Decision.ALLOW, null,
                                new AndCondition(new CallerCondition(packageName),
                                        new UserInitiatedCondition()))
                        )
                        .build();

                AndroidPolicy policy = AndroidPolicy.forSelfCalls(context, aiPolicy);

                PolicyResult result = policy.evaluate("customer.delete", true);

                assertTrue(result.isAllowed());
            }
        });
    }

    @Test
    public void createsPolicyForExternalCalls() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AiPolicy aiPolicy = AiPolicy.builder()
                        .addRule("customer.read", new PolicyRule(Decision.ALLOW,
                                null))
                        .build();

                AndroidPolicy policy = AndroidPolicy.forExternalCalls(context, aiPolicy);

                assertNotNull(policy);
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void externalCallsRequiresContext() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AiPolicy aiPolicy = AiPolicy.builder()
                        .addRule("customer.read", new PolicyRule(Decision.ALLOW, null))
                        .build();

                AndroidPolicy.forExternalCalls(null, aiPolicy);
            }
        });

    }

    @Test(expected = NullPointerException.class)
    public void externalCallsRequiresPolicy() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy.forExternalCalls(context, null);
            }
        });
    }

    @Test
    public void evaluatesPermissionCondition() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AiPolicy aiPolicy = AiPolicy.builder()
                        .addRule("customer.delete", new PolicyRule(Decision.ALLOW,
                                null,
                                new AndroidPermissionCondition(context, "android.permission.INTERNET"))
                        )
                        .build();

                        AndroidPolicy policy = AndroidPolicy.forSelfCalls(context, aiPolicy);

                        PolicyResult result = policy.evaluate("customer.delete", true);

                        assertTrue(result.isAllowed());
                    }
        });
    }

    @Test
    public void deniesWhenPermissionIsMissing() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AiPolicy aiPolicy = AiPolicy.builder()
                        .addRule("customer.delete", new PolicyRule(Decision.ALLOW,
                                        null, new AndroidPermissionCondition(context,
                                        "android.permission.CAMERA")))
                        .build();

                        AndroidPolicy policy = AndroidPolicy.forSelfCalls(context, aiPolicy);

                        PolicyResult result = policy.evaluate("customer.delete",
                                true);

                        assertTrue(result.isDenied());
                    }
                });
    }

    @Test
    public void deniesUnknownCapability() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AiPolicy aiPolicy = AiPolicy.builder().build();

                AndroidPolicy policy = AndroidPolicy.forSelfCalls(context, aiPolicy);
                PolicyResult result = policy.evaluate("does.not.exist", true);

                assertTrue(result.isDenied());
                assertEquals("Unknown capability: does.not.exist", result.getReason());
            }
        });
    }
}