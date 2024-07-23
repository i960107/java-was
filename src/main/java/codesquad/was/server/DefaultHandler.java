package codesquad.was.server;

import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.MimeType;
import codesquad.was.server.exception.ResourceNotFoundException;
import codesquad.was.server.exception.ServerException;
import codesquad.was.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler extends Handler {

    private static final String STATIC_FOLDER = "static";

    private static final File UPLOAD_DIR = new File(System.getProperty("user.dir"), "/upload");

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPath();

        if (path.equals("/")) {
            response.sendRedirect("/index.html");
            return;
        }

        byte[] bytes;

        try {
            InputStream file = getFile(path);
            bytes = file.readAllBytes();
        } catch (IOException e) {
            throw new ServerException();
        }

        response.send(MimeType.getMimeTypeFromExtension(path), bytes);
    }

    private InputStream getFile(String path) throws IOException {
        if (!isFileName(path)) {
            throw new ResourceNotFoundException();
        }

        InputStream resource;
        if (path.startsWith("/upload")) {
            resource = new FileInputStream(new File(UPLOAD_DIR, path.substring("/upload".length())));
        } else {
            resource = IOUtil.getClassPathResource(STATIC_FOLDER + path);
        }

        if (resource == null) {
            throw new ResourceNotFoundException();
        }

        return resource;
    }

    private boolean isFileName(String path) {
        try {
            MimeType.getMimeTypeFromExtension(path);
        } catch (IllegalArgumentException exception) {
            return false;
        }
        return true;
    }
}
