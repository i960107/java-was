package codesquad.was.server;

import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpStatus;
import codesquad.was.http.MimeTypes;
import codesquad.was.http.WasRequest;
import codesquad.was.http.WasResponse;
import codesquad.was.server.exception.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler implements Handler {

    private static final String STATIC_FOLDER = "static";

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void doGet(WasRequest request, WasResponse response) {
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
        InputStream resource = getClass().getClassLoader().getResourceAsStream(STATIC_FOLDER + path);

        if (resource == null) {
            throw new ResourceNotFoundException();
        }

        return resource;
    }
}
