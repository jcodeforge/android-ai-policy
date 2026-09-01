package io.github.jcodeforge.aipolicy.capability;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import io.github.jcodeforge.aipolicy.CallerType;

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

    private final boolean userInitiatedRequired;

    private final List<CallerType> allowedCallerTypes;

    private final List<String> requiredPermissions;

    /**
     * Creates a capability.
     *
     * @param name capability name
     * @param description capability description
     * @param userInitiatedRequired whether user initiation is required
     * @param allowedCallerTypes permitted caller types
     * @param requiredPermissions required Android permissions
     */
    public Capability(String name, String description, boolean userInitiatedRequired,
                      List<CallerType> allowedCallerTypes, List<String> requiredPermissions) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.userInitiatedRequired = userInitiatedRequired;
        this.allowedCallerTypes = List.copyOf(Objects.requireNonNull(allowedCallerTypes,
                "allowedCallerTypes must not be null"));
        this.requiredPermissions = List.copyOf(Objects.requireNonNull(requiredPermissions,
                "requiredPermissions must not be null"));

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        if (description.trim().isEmpty()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }

    /**
     * Convenience constructor for generated code.
     */
    public Capability(String name, String description, boolean userInitiatedRequired,
                      CallerType[] allowedCallerTypes, String[] requiredPermissions) {
        this(name, description, userInitiatedRequired, Arrays.asList(Objects.requireNonNull(
                allowedCallerTypes, "allowedCallerTypes must not be null")),
                Arrays.asList(Objects.requireNonNull(requiredPermissions,
                        "requiredPermissions must not be null")));
    }

    public Capability(String name, String description) {
        this(name, description, false, Collections.emptyList(),
                Collections.emptyList());
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

    public boolean isUserInitiatedRequired() {
        return userInitiatedRequired;
    }

    /**
     * Returns the caller types allowed to use this capability.
     *
     * @return immutable list of allowed caller types
     */
    public List<CallerType> getAllowedCallerTypes() {
        return allowedCallerTypes;
    }

    /**
     * Returns the Android permissions required by this capability.
     *
     * @return immutable list of required permissions
     */
    public List<String> getRequiredPermissions() {
        return requiredPermissions;
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

        return userInitiatedRequired == other.userInitiatedRequired
                && name.equals(other.name)
                && description.equals(other.description)
                && allowedCallerTypes.equals(other.allowedCallerTypes)
                && requiredPermissions.equals(other.requiredPermissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, userInitiatedRequired, allowedCallerTypes,
                requiredPermissions);
    }

    @Override
    public String toString() {
        return "Capability{"
                + "name='" + name + '\''
                + ", description='" + description + '\''
                + ", userInitiatedRequired=" + userInitiatedRequired
                + ", allowedCallerTypes=" + allowedCallerTypes
                + ", requiredPermissions=" + requiredPermissions
                + '}';
    }
}