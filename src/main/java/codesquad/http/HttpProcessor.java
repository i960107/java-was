package codesquad.http;

import codesquad.HttpSCStatus;
import codesquad.IOUtil;
import codesquad.MimeTypes;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProcessor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final HandlerContext handlerContext;

    public HttpProcessor(HandlerContext handlerContext) {
        this.handlerContext = handlerContext;
    }

    public void process(Socket socket) throws IOException {
        try (
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
        ) {
            Request request = processRequest(inputStream);
            processResponse(outputStream, request, request.getPath());
        }
    }

    public Request processRequest(InputStream input) throws IOException {
        Request request = HttpRequestParser.parse(input);
        log.info(request.toString());
        return request;
    }

    public void processResponse(OutputStream outputStream, Request request, String path) throws IOException {
        Response<?> response = null;

        HttpHeader header = new HttpHeader();
        addCommonHeader(header);

        Optional<File> file = getFile(path);

        if (file.isEmpty()) {
            response = WasResponse.fail(request.getProtocol(), HttpSCStatus.NOT_FOUND, header);
        } else {
            File fileToResponse = file.get();
            header.setHeader(HttpHeader.CONTENT_LENGTH_HEADER, String.valueOf(fileToResponse.length()));
            header.setHeader(HttpHeader.CONTENT_TYPE_HEADER, MimeTypes.getMimeType(fileToResponse.getName()));
            response = new WasResponse<File>(
                    request.getProtocol(),
                    HttpSCStatus.OK,
                    header,
                    fileToResponse
            );
        }

        log.info(response.toString());
        HttpResponseWriter.write(outputStream, response);
    }

    private void addCommonHeader(HttpHeader headers) {
        headers.setHeader(HttpHeader.DATE_HEADER, IOUtil.getDateStringUtc());
        headers.setHeader(HttpHeader.CONNECTION_HEADER, "close");
    }

    private Optional<File> getFile(String path) {
        URL resource = getClass().getClassLoader().getResource(STATIC_FOLDER + path);

        if (resource == null) {
            return Optional.empty();
        }

        File file = new File(resource.getFile());
        if (!file.exists()) {
            return Optional.empty();
        }
        if (file.isDirectory()) {
            file = new File(file, INDEX_PAGE);
        }
        return file.exists() ? Optional.of(file) : Optional.empty();
    }

}
