package codesquad.application.handler;

import codesquad.application.db.UserDao;
import codesquad.application.view.UserListViewRenderer;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.MimeTypes;
import codesquad.was.server.Handler;
import java.util.HashMap;
import java.util.Map;

public class UserListHandler extends Handler {

    private UserDao userDao;

    private UserListViewRenderer userListViewRenderer;

    public UserListHandler(UserDao userDao) {
        this.userDao = userDao;
        this.userListViewRenderer = new UserListViewRenderer();
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        request.authenticate();

        if (!request.isAuthenticated()) {
            response.sendRedirect("/login/index.html");
            return;
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("loginUsername", request.getPrincipal().getUsername());
        parameters.put("users", userDao.findAll());
        String html = userListViewRenderer.render(parameters);

        response.send(MimeTypes.html, html.getBytes());
    }
}
