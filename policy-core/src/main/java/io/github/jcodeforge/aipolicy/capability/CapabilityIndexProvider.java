package io.github.jcodeforge.aipolicy.capability;

/**
 * Provides the capability index generated for an application.
 *
 * <p>The implementation is normally generated automatically by the
 * AI capability annotation processor. Applications should not need
 * to implement or instantiate this interface directly.</p>
 */
public interface CapabilityIndexProvider {

    /**
     * Returns the generated capability index.
     *
     * @return the capability index; never {@code null}
     */
    CapabilityIndex getCapabilityIndex();
}