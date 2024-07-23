package codesquad.application.handler;

import codesquad.application.db.PostDao;
import codesquad.application.view.IndexViewRenderer;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.MimeType;
import codesquad.was.server.Handler;
import java.util.HashMap;
import java.util.Map;

public class IndexHandler extends Handler {

    private final IndexViewRenderer view;

    private final PostDao postDao;

    public IndexHandler(PostDao postDao) {
        this.view = new IndexViewRenderer();
        this.postDao = postDao;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        request.authenticate();

        Map<String, Object> parameters = new HashMap<>();
        String username = request.getPrincipal() != null ? request.getPrincipal().getUsername() : null;
        parameters.put("loginUsername", username);
        parameters.put("posts", postDao.findAllPostWithDetail());

        String html = view.render(parameters);

        response.send(MimeType.html, html.getBytes());
    }
}
