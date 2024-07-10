package codesquad.server;

import codesquad.http.HttpHeaders;
import codesquad.http.MimeTypes;
import codesquad.http.WasRequest;
import codesquad.http.WasResponse;
import codesquad.server.exception.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler implements Handler {

    private static final String STATIC_FOLDER = "static";

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void handle(WasRequest request, WasResponse response) throws IOException {
        InputStream file = getFile(request.getPath());
        byte[] bytes = file.readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        HttpHeaders.setCommonHeader(headers);
        headers.setHeader(HttpHeaders.CONTENT_LENGTH_HEADER, String.valueOf(bytes.length));
        headers.setHeader(HttpHeaders.CONTENT_TYPE_HEADER, MimeTypes.getMimeTypeFromExtension(request.getPath()));

        response.send(headers, bytes);
    }

    @Override
    public boolean canHandle(WasRequest request) {
        return hasSupportedExtension(request.getPath());
    }

    private boolean hasSupportedExtension(String path) {
        try {
            MimeTypes.getMimeTypeFromExtension(path);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private InputStream getFile(String path) {
        InputStream resource = getClass().getClassLoader().getResourceAsStream(STATIC_FOLDER + path);

        if (resource == null) {
            throw new ResourceNotFoundException();
        }

        return resource;
    }
}
