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

### 1. Declare capabilities

Annotate application methods that should be accessible to AI systems.

```java
public final class CustomerService {

    @AiCapability(
            name = "customer.read",
            description = "Read customer information"
    )
    public void readCustomer() {
        // ...
    }

    @AiCapability(
            name = "customer.delete",
            description = "Delete a customer"
    )
    public void deleteCustomer() {
        // ...
    }
}
```

---

## Evaluation Notes

Multiple rules can be registered for the same capability.

Rules are evaluated in registration order. The first matching rule wins.

If no rule matches, the result is DENY.

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