package codesquad.was.server.authenticator;

import java.util.Optional;

public interface UserAuthBase {
    Optional<Principal> auth(String username, String password);
}
