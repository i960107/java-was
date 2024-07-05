package codesquad.server;

import codesquad.MimeTypes;
import codesquad.http.HttpHeaders;
import codesquad.http.HttpStatus;
import codesquad.http.WasRequest;
import codesquad.http.WasResponse;
import codesquad.server.exception.ResourceNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler implements Handler {

    private static final String STATIC_FOLDER = "static";

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void handle(WasRequest request, WasResponse response) throws IOException {
        File file = getFile(request.getPath());

        HttpHeaders headers = new HttpHeaders();
        HttpHeaders.setCommonHeader(headers);
        headers.setHeader(HttpHeaders.CONTENT_LENGTH_HEADER, String.valueOf(file.length()));
        headers.setHeader(HttpHeaders.CONTENT_TYPE_HEADER, MimeTypes.getMimeType(file.getName()));

        response.send(request.getProtocol(), HttpStatus.OK, headers, new FileInputStream(file));
    }

    @Override
    public boolean canHandle(WasRequest request) {
        return hasSupportedExtension(request.getPath());
    }

    private boolean hasSupportedExtension(String path) {
        if (!path.contains(".")) {
            return false;
        }
        try {
            MimeTypes.getMimeType(path);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private File getFile(String path) {
        URL resource = getClass().getClassLoader().getResource(STATIC_FOLDER + path);

        if (resource == null) {
            throw new ResourceNotFoundException();
        }

        File file = new File(resource.getFile());
        if (file.isDirectory() || !file.exists()) {
            throw new ResourceNotFoundException();
        }

        return file;
    }
}
