package codesquad.was.server.session;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionManager implements SessionManager{

    private ConcurrentHashMap<String, Session> sessions;

    public InMemorySessionManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<Session> getSession(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    @Override
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    @Override
    public Session createSession() {
        Session session = new Session(UUID.randomUUID().toString(), LocalDateTime.now());
        sessions.put(session.getId(), session);
        return session;
    }
}
