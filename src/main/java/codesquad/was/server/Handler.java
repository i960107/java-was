package codesquad.was.server;

import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.server.exception.MethodNotAllowedException;

public interface Handler {

    default void doGet(HttpRequest request, HttpResponse response) {
        throw new MethodNotAllowedException();
    }

    default void doPost(HttpRequest request, HttpResponse response) {
        throw new MethodNotAllowedException();
    }
}
