# Android AI Policy

A lightweight policy engine for controlling AI-initiated actions in Android applications.

> **Status:** Early development — API and architecture are subject to change.

---

## Built on Android AppFunctions

Android AI Policy is designed to work alongside **Google's Android AppFunctions**.

[Android AppFunctions](https://developer.android.com/ai/appfunctions) provides a standard way for
Android applications to expose application functionality to AI agents. Developers can annotate
functions that an AI agent can discover and invoke, making application capabilities available
to the emerging Android AI ecosystem.

However, exposing a function to an AI agent and deciding whether that action should actually be
allowed are two different concerns. Android AI Policy provides the policy layer for this decision.

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

## Quickstart

Java:

```kotlin
implementation("io.github.jcodeforge:aipolicy-android:1.0.0")
annotationProcessor("io.github.jcodeforge:aipolicy-processor:1.0.0")
```

Kotlin:
```kotlin
implementation("io.github.jcodeforge:aipolicy-android:1.0.0")
ksp("io.github.jcodeforge:aipolicy-processor-ksp:1.0.0")
```

---

## Basic Usage

```java
public final class CustomerService {

    @AppFunctions
    @AiCapability(
            name = "customer.delete",
            description = "Delete a customer",
            userInitiatedRequired = true,
            allowedCallerTypes = {CallerType.SELF},
            requiredPermissions = {"android.permission.WRITE_CONTACTS"}
    )
    public void deleteCustomer() {
        AndroidPolicy policy = AndroidPolicy.forSelfCalls(context);

        PolicyResult result = policy.evaluate("customer.delete", true);
        
        if (result.isAllowed()) {
            ...
        }
    }
}
```
---

## Caller Types, Permissions & User Initiation

Android AI Policy allows capabilities to define additional security requirements:

- Caller types specify who is allowed to perform an action, such as the application itself (SELF) 
or an external caller (EXTERNAL).
- Permissions specify Android permissions that must be granted before an action is allowed.
- User initiation can require an action to be explicitly initiated by the user before it can be performed.

These requirements are defined with @AiCapability and checked automatically during policy evaluation.

---

## What the Library Does — and Does Not Do

Android AI Policy provides a policy layer for AI-accessible Android capabilities. It evaluates 
whether an action is allowed based on caller type, Android permissions, and user-initiation requirements.

It does not execute the action, invoke application methods, authenticate users, or replace 
Android's permission system. The application remains responsible for performing the actual business
operation after a capability is allowed or not.

---

## Java & Kotlin Support

Android AI Policy supports both Java and Kotlin applications.

Use the same @AiCapability annotation in both languages. Java capabilities are processed using an 
annotation processor, while Kotlin capabilities are processed using KSP.

Both generate capability metadata that is automatically combined at runtime.

---

## License

This project is licensed under the Apache License 2.0. See the `LICENSE` file for details.

---

## Support

If this library is useful to you, consider supporting its development.

Development requires time for implementing new features, improving documentation, 
maintaining standards compatibility, and providing support.


<a href="https://paypal.me/juniorscholle">
  <img src="https://img.shields.io/badge/Donate-PayPal-00457C?logo=paypal&logoColor=white" alt="Donate with PayPal">
</a>

**PayPal:** https://paypal.me/juniorscholle  

---

## Articles

- Coming soon