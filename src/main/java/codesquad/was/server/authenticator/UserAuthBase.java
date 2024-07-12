package codesquad.was.server.authenticator;

public interface UserAuthBase {
    boolean auth(String username, String password);
}
