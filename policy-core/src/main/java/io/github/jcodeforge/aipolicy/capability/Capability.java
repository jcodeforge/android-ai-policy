package io.github.jcodeforge.aipolicy.capability;

import java.util.Objects;

/**
 * Represents an AI-accessible capability.
 *
 * <p>A capability has a stable identifier and a human-readable
 * description. Capability instances are immutable and can be used
 * by registration, discovery, and policy evaluation APIs.</p>
 */
public final class Capability {

    private final String name;
    private final String description;

    /**
     * Creates a capability.
     *
     * @param name unique capability identifier
     * @param description human-readable capability description
     * @throws NullPointerException if {@code name} or {@code description}
     *                              is {@code null}
     * @throws IllegalArgumentException if {@code name} or
     *                                  {@code description} is blank
     */
    public Capability(String name, String description) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        if (description.trim().isEmpty()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }

    /**
     * Returns the unique capability identifier.
     *
     * @return capability identifier
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the human-readable capability description.
     *
     * @return capability description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Capability)) {
            return false;
        }

        Capability other = (Capability) object;

        return name.equals(other.name) && description.equals(other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return "Capability{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}