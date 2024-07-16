package codesquad.application.handler;

import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.server.Handler;

public class UserLogoutHandler extends Handler {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        request.logout();
        request.invalidateSession();
        response.removeCookie(HttpHeaders.SID);
        response.sendRedirect("/index.html");
    }

}
