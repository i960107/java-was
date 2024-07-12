package codesquad.was.server.session;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    private final String id;
    private final Map<String, Object> attributes;
    private final LocalDateTime createdAt;

    public Session(String id, LocalDateTime createdAt) {
        this.id = id;
        this.attributes = new ConcurrentHashMap<>();
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Optional<Object> getAttribute(String key) {
        return Optional.ofNullable(attributes.get(key));
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }
}
