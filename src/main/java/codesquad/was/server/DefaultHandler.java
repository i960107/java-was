package codesquad.was.server;

import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.MimeTypes;
import codesquad.was.server.exception.ResourceNotFoundException;
import codesquad.was.server.exception.ServerException;
import codesquad.was.util.IOUtil;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler extends Handler {

    private static final String STATIC_FOLDER = "static";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String path =  request.getPath();

        if(path.equals("/")){
            response.sendRedirect("/index.html");
            return;
        }

        InputStream file = getFile(path);
        byte[] bytes;

        try {
            bytes = file.readAllBytes();
        } catch (IOException e) {
            throw new ServerException();
        }

        response.send(MimeTypes.getMimeTypeFromExtension(path), bytes);
    }

    private InputStream getFile(String path) {
        if (!isFileName(path)) {
            throw new ResourceNotFoundException();
        }

        InputStream resource = IOUtil.getClassPathResource(STATIC_FOLDER + path);
        if (resource == null) {
            throw new ResourceNotFoundException();
        }

        return resource;
    }

    private boolean isFileName(String path) {
        try {
            MimeTypes.getMimeTypeFromExtension(path);
        } catch (IllegalArgumentException exception) {
            return false;
        }
        return true;
    }
}
