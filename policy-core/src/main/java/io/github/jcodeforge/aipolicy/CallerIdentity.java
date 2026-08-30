package io.github.jcodeforge.aipolicy;

import java.util.Objects;

public final class CallerIdentity {

    private final String id;
    private final CallerType type;

    public CallerIdentity(String id, CallerType type) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");

        if (id.trim().isEmpty()) {
            throw new IllegalArgumentException("id must not be blank");
        }
    }

    public String getId() {
        return id;
    }

    public CallerType getType() {
        return type;
    }
}