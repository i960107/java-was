package codesquad.was.server;

import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.server.exception.MethodNotAllowedException;

public abstract class Handler {

    protected Handler() {
    }

    public void doGet(HttpRequest request, HttpResponse response) {
        throw new MethodNotAllowedException();
    }

    public void doPost(HttpRequest request, HttpResponse response) {
        throw new MethodNotAllowedException();
    }
}
