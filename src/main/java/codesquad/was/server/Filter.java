package codesquad.was.server;

import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;

public abstract class Filter {
    private final int order;

    Filter(int order) {
        this.order = order;
    }

    public void doFilter(HttpRequest request, HttpResponse response) {
        beforeHandler(request, response);

        afterHandler(request, response);
    }

    public abstract void beforeHandler(HttpRequest request, HttpResponse response);

    public abstract void afterHandler(HttpRequest request, HttpResponse response);
}
