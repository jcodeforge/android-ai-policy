package io.github.jcodeforge.aipolicy.capability;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a method as an AI-accessible capability.
 *
 * <p>The annotation provides metadata that can later be discovered and
 * registered by the AI Capability API. The annotated method itself is not
 * invoked automatically; it only declares capability metadata.</p>
 *
 * <p>Capabilities should use stable, dot-separated identifiers such as
 * {@code customer.read} or {@code invoice.create}.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AiCapability {

    /**
     * Unique capability identifier.
     *
     * <p>This identifier is used by policies and capability discovery.</p>
     *
     * @return capability identifier
     */
    String name();

    /**
     * Human-readable description of the capability.
     *
     * @return capability description
     */
    String description();
}