package io.github.jcodeforge.aipolicy.android;

/**
 * Identifies the type of Android caller associated with an action.
 *
 * <p>The caller type is determined by the Android caller provider and is
 * included in the {@code CallerIdentity} used during policy evaluation.</p>
 */
public enum AndroidCallerType {

    /**
     * The current application process is the caller.
     */
    SELF,

    /**
     * The caller is an external application or process.
     */
    EXTERNAL,

    /**
     * The caller type could not be determined.
     */
    UNKNOWN
}