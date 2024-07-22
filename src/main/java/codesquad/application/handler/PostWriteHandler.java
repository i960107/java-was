package codesquad.application.handler;

import codesquad.application.db.PostDao;
import codesquad.application.model.Post;
import codesquad.application.model.PostWriteRequest;
import codesquad.application.util.RequestPartModelMapper;
import codesquad.application.view.PostWriteViewRenderer;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.HttpStatus;
import codesquad.was.http.MimeType;
import codesquad.was.http.Part;
import codesquad.was.server.Handler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostWriteHandler extends Handler {

    public static final String UPLOAD_DIR = "/upload";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final PostDao postDao;

    private final PostWriteViewRenderer postWriteViewRenderer;

    private final File uploadDir;

    public PostWriteHandler(PostDao postDao) {
        String applicationPath = System.getProperty("user.dir");
        File uploadDir = new File(applicationPath, UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean success = uploadDir.mkdir();
            if (!success) {
                throw new RuntimeException("fail to create post image upload directory");
            }
        }
        this.uploadDir = uploadDir;
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

        response.send(MimeType.html, html.getBytes());
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        request.authenticate();

        if (!request.isAuthenticated()) {
            response.sendRedirect("/login/index.html");
            return;
        }
        PostWriteRequest post = RequestPartModelMapper.map(request.getParts(), PostWriteRequest.class);

        if (post.getImage() == null || post.getImage().getContentType() == MimeType.text) {
            response.sendError(HttpStatus.BAD_REQUEST);
        }

        String filename = post.getImage().getFileName();
        String uniqueFileName = createUniqueFileName(post.getImage().getContentType(), filename);

        saveImage(post.getImage(), uniqueFileName);
        Post postSaved = postDao.save(post.toEntity(uniqueFileName));
        log.info("post saved {} ", postSaved);
        response.sendRedirect("/index.html");
    }

    private String createUniqueFileName(MimeType contentType, String originalName) {
        StringBuilder saveFilename = new StringBuilder();
        saveFilename.append(UUID.randomUUID());
        saveFilename.append(".");
        saveFilename.append(contentType.name());
        return saveFilename.toString();
    }

    private void saveImage(Part part, String saveFileName) {
        File uploadFile = new File(uploadDir, saveFileName);
        try (FileOutputStream fos = new FileOutputStream(uploadFile)) {
            fos.write(part.getContent());
        } catch (IOException e) {
            log.warn("fail to save file");
            throw new RuntimeException("fail to save file");
        }
    }
}
