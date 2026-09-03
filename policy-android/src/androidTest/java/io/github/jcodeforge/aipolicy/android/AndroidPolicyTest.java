package io.github.jcodeforge.aipolicy.android;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import io.github.jcodeforge.aipolicy.CallerType;
import io.github.jcodeforge.aipolicy.PolicyResult;
import io.github.jcodeforge.aipolicy.capability.AiCapability;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AndroidPolicyTest {

    /**
     * Test capabilities discovered by the annotation processor.
     */
    public static final class TestCapabilities {

        @AiCapability(
                name = "test.customer.read",
                description = "Read customer information",
                allowedCallerTypes = {
                        CallerType.EXTERNAL
                }
        )
        public void readCustomer() {
        }

        @AiCapability(
                name = "test.customer.delete",
                description = "Delete customer",
                userInitiatedRequired = true
        )
        public void deleteCustomer() {
        }

        @AiCapability(
                name = "test.customer.permission",
                description = "Access protected customer information",
                requiredPermissions = {
                        "android.permission.INTERNET"
                }
        )
        public void accessProtectedCustomerInformation() {
        }

        @AiCapability(
                name = "test.customer.camera",
                description = "Access camera",
                requiredPermissions = {
                        "android.permission.CAMERA"
                }
        )
        public void accessCamera() {
        }

        @AiCapability(
                name = "customer.self",
                description = "Self-only customer operation",
                allowedCallerTypes = {
                        CallerType.SELF
                }
        )
        public void selfOnlyOperation() {
        }
    }

    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test(expected = NullPointerException.class)
    public void requiresContext() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy.forSelfCalls(null);
            }
        });
    }

    @Test
    public void createsPolicyForSelfCalls() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy policy = AndroidPolicy.forSelfCalls(context);
                assertNotNull(policy);
            }
        });
    }

    @Test
    public void evaluatesSelfCall() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy policy = AndroidPolicy.forSelfCalls(context);

                PolicyResult result = policy.evaluate("test.customer.read", true);

                assertTrue(result.isDenied());
                assertEquals("Caller type is not allowed", result.getReason());
            }
        });
    }

    @Test
    public void evaluatesUserInitiatedCapability() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy policy = AndroidPolicy.forSelfCalls(context);

                PolicyResult result = policy.evaluate("test.customer.delete", true);

                assertTrue(result.isAllowed());
            }
        });
    }

    @Test
    public void deniesUserInitiatedCapabilityWhenNotUserInitiated() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy policy = AndroidPolicy.forSelfCalls(context);

                PolicyResult result = policy.evaluate("test.customer.delete", false);

                assertTrue(result.isDenied());

                assertEquals("Capability requires user initiation", result.getReason());
            }
        });

    }

    @Test
    public void createsPolicyForExternalCalls() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy policy = AndroidPolicy.forExternalCalls(context);

                assertNotNull(policy);
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void externalCallsRequireContext() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy.forExternalCalls(null);
            }
        });
    }

    @Test
    public void evaluatesRequiredPermission() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy policy =
                        AndroidPolicy.forSelfCalls(context);

                PolicyResult result =
                        policy.evaluate("test.customer.permission", true);

                assertTrue(result.isAllowed());
            }
        });
    }

    @Test
    public void deniesWhenRequiredPermissionIsMissing() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy policy = AndroidPolicy.forSelfCalls(context);

                PolicyResult result = policy.evaluate("test.customer.camera", true);

                assertTrue(result.isDenied());

                assertEquals("Required permission is missing: android.permission.CAMERA",
                        result.getReason());
            }
        });
    }

    @Test
    public void deniesUnknownCapability() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                AndroidPolicy policy = AndroidPolicy.forSelfCalls(context);

                PolicyResult result = policy.evaluate("does.not.exist", true);

                assertTrue(result.isDenied());

                assertEquals("Unknown capability: does.not.exist", result.getReason()
                );
            }
        });
    }
}