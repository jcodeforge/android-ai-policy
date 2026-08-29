# Android AI Policy

A lightweight policy engine for controlling AI-initiated actions in Android applications.

> **Status:** Early development — API and architecture are subject to change.

---

## Overview

Android AI Policy provides a policy layer between AI-accessible capabilities and application business logic.

It can answer questions such as:

- Is an action allowed?
- Is user confirmation required?
- Who initiated the action?
- Under which conditions is an action permitted?
- Why was an action denied?

---

## Basic Usage

Create a policy:

```java
AiPolicy policy = AiPolicy.builder()
        .addRule(
                "customer.read",
                new PolicyRule(
                        Decision.ALLOW,
                        null
                )
        )
        .addRule(
                "customer.delete",
                new PolicyRule(
                        Decision.REQUIRE_CONFIRMATION,
                        "User confirmation is required"
                )
        )
        .build();

ActionContext context = new ActionContext(
        "customer.delete",
        "com.example.aiagent",
        true
);

// Evaluate the action
PolicyResult result = policy.evaluate(context);

if (result.getDecision() == Decision.ALLOW) {
    // Execute action
}

//Conditional Rules
PolicyRule rule = new PolicyRule(
        Decision.ALLOW,
        null,
        new CallerCondition("com.example.aiagent")
);

PolicyRule rule = new AndCondition(
        new CallerCondition("com.example.aiagent"),
        new UserInitiatedCondition()
);
```

---

## Evaluation Notes

Multiple rules can be registered for the same capability.

Rules are evaluated in registration order. The first matching rule wins.

If no rule matches, the result is DENY.

---

## License

Apache License 2.0