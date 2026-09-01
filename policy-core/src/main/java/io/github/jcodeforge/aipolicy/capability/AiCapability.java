package io.github.jcodeforge.aipolicy.capability;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import io.github.jcodeforge.aipolicy.CallerType;

/**
 * Declares a method as an AI-accessible capability.
 *
 * <p>The annotation contains the metadata required by the policy system
 * to describe and evaluate the capability. The annotation processor
 * generates the corresponding capability metadata at compile time.</p>
 *
 * <p>Applications do not need to create or register capabilities
 * manually.</p>
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

    /**
     * Determines whether the action must have been explicitly initiated
     * by the user.
     *
     * @return {@code true} if user initiation is required
     */
    boolean userInitiatedRequired() default false;

    /**
     * Caller types that are permitted to request this capability.
     *
     * <p>An empty array means that the capability does not impose a
     * caller-type restriction.</p>
     *
     * @return permitted caller types
     */
    CallerType[] allowedCallerTypes() default {};

    /**
     * Android permissions required for this capability.
     *
     * <p>An empty array means that the capability does not require any
     * additional Android permissions.</p>
     *
     * @return required Android permissions
     */
    String[] requiredPermissions() default {};
}