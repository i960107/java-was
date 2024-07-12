package codesquad.application.handler;

import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpStatus;
import codesquad.was.http.MimeTypes;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.server.Handler;
import codesquad.was.server.exception.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler implements Handler {

    private static final String STATIC_FOLDER = "static";

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        InputStream file = getFile(request.getPath());
        byte[] bytes;

        try {
            bytes = file.readAllBytes();
        } catch (IOException e) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        HttpHeaders.setCommonHeader(headers);
        headers.setHeader(HttpHeaders.CONTENT_LENGTH_HEADER, String.valueOf(bytes.length));
        headers.setHeader(HttpHeaders.CONTENT_TYPE_HEADER, MimeTypes.getMimeTypeFromExtension(request.getPath()));

        response.send(headers, bytes);
    }

    private InputStream getFile(String path) {
        if (!isFileName(path)) {
            throw new ResourceNotFoundException();
        }

        InputStream resource = getClass().getClassLoader().getResourceAsStream(STATIC_FOLDER + path);
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
