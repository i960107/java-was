package codesquad.was.server.authenticator;

import codesquad.was.http.HttpRequest;
import codesquad.was.server.exception.AuthenticationException;
import codesquad.was.server.exception.ServerInitializeException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAuthenticator implements Authenticator {

    private Logger log = LoggerFactory.getLogger(getClass());

    private final UserAuthBase userAuthBase;

    public DefaultAuthenticator(UserAuthBase userAuthBase) {
        if (userAuthBase == null) {
            throw new ServerInitializeException();
        }
        this.userAuthBase = userAuthBase;
    }

    @Override
    public Principal authenticate(HttpRequest request) {
        String username = request.getParameter("username")
                .orElseThrow(AuthenticationException::new);
        String password = request.getParameter("password")
                .orElseThrow(AuthenticationException::new);
        Optional<Principal> principal = userAuthBase.auth(username, password);
        if (principal.isEmpty()) {
            log.info("authenticate failed  : {} / {}", username, password);
            throw new AuthenticationException();
        }
        log.info("authenticate success  : {} / {}", username, password);
        return principal.get();
    }

}
