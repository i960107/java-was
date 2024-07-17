package codesquad.application.handler;

import codesquad.application.db.PostDao;
import codesquad.application.model.Post;
import codesquad.application.util.RequestParamModelMapper;
import codesquad.application.view.PostWriteViewRenderer;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.MimeTypes;
import codesquad.was.server.Handler;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostWriteHandler extends Handler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final PostDao postDao;

    private final PostWriteViewRenderer postWriteViewRenderer;

    public PostWriteHandler(PostDao postDao) {
        this.postDao = postDao;
        this.postWriteViewRenderer = new PostWriteViewRenderer();
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
        parameters.put("userId", request.getPrincipal().getUserId());
        String html = postWriteViewRenderer.render(parameters);

        response.send(MimeTypes.html, html.getBytes());
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Post post = RequestParamModelMapper.map(request.getParameterMap(), Post.class);
        Post postSaved = postDao.save(post);
        log.info("post saved {} ", postSaved);
        response.sendRedirect("/index.html");
    }
}
