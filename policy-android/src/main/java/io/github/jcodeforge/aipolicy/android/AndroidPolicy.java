package io.github.jcodeforge.aipolicy.android;

import io.github.jcodeforge.aipolicy.ActionContext;
import io.github.jcodeforge.aipolicy.AiPolicy;
import io.github.jcodeforge.aipolicy.PolicyResult;
import java.util.Objects;

public final class AndroidPolicy {

    private final AiPolicy policy;
    private final AndroidActionContextFactory contextFactory;

    public AndroidPolicy(AiPolicy policy, AndroidActionContextFactory contextFactory) {
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
        this.contextFactory = Objects.requireNonNull(contextFactory, "contextFactory must not be null");
    }

    public PolicyResult evaluate(String capability, boolean userInitiated) {
        ActionContext context = contextFactory.create(capability, userInitiated);

        return policy.evaluate(context);
    }
}