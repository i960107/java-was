package codesquad.application.handler;

import codesquad.was.http.HttpCookie;
import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.server.Handler;
import codesquad.was.server.exception.AuthenticationException;

public class UserLoginHandler extends Handler {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        try {
            request.login();
        } catch (AuthenticationException exception) {
            response.sendRedirect("/user/login_failed.html");
            return;
        }
        HttpCookie sidCookie = new HttpCookie(HttpHeaders.SID, request.getSessionId());
        sidCookie.setPath("/");
        response.setCookie(sidCookie);
        response.sendRedirect("/index.html");
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.sendRedirect("/login/index.html");
    }
}
