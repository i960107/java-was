package codesquad.was.server.authenticator;

import codesquad.was.http.HttpRequest;

public interface Authenticator {

    Principal authenticate(HttpRequest request);

}
