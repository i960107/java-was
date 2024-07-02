package codesquad.http;

import codesquad.HttpSCStatus;
import codesquad.IOUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProcessor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String STATIC_FOLDER = "static";

    private static final String DEFAULT_PATH = "/";

    private static final String DEFAULT_PAGE = "/main/index.html";

    public void process(Socket socket) throws IOException {
        try (
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
        ) {
            Request request = processRequest(inputStream);
            String fileName = request.getPath().equals(DEFAULT_PATH) ? DEFAULT_PAGE : request.getPath();
            processResponse(outputStream, request, fileName);
        }
    }

    public Request processRequest(InputStream input) throws IOException {
        Request request = HttpRequestParser.parse(input);
        log.info(request.toString());
        return request;
    }

    public void processResponse(OutputStream outputStream, Request request, String fileName) throws IOException {
        Response<?> response = null;

        HashMap<String, String> headers = new HashMap<>();
        addCommonHeader(headers);

        URL resource = getClass().getClassLoader().getResource(STATIC_FOLDER + fileName);

        if (resource != null) {
            File file = new File(resource.getFile());
            if (file.exists() && file.isFile()) {
                headers.put(HttpResponseWriter.CONTENT_LENGTH_HEADER, String.valueOf(file.length()));
                response = new WasResponse<File>(
                        request.getProtocol(),
                        HttpSCStatus.OK,
                        headers,
                        file
                );
            }
        }

        if (response == null) {
            response = WasResponse.fail(request.getProtocol(), HttpSCStatus.NOT_FOUND, headers);
        }
        log.info(response.toString());
        HttpResponseWriter.write(outputStream, response);
    }

    private void addCommonHeader(Map<String, String> headers) {
        headers.put("Date", IOUtil.getDateStringUtc());
        headers.put("Connection", "close");
    }

}
