package codesquad.was.server.session;

import java.util.Optional;

public interface SessionManager {
    Optional<Session> getSession(String sessionId);

    void removeSession(String sessionId);

    Session createSession();

    void changeSessionId(Session session, String changeId);
}
