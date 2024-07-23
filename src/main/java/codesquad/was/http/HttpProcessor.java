package codesquad.was.http;

import codesquad.was.http.exception.HttpProtocolException;
import codesquad.was.server.ServerContext;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProcessor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ServerContext context;

    public HttpProcessor(ServerContext context) {
        this.context = context;
    }

    public void process(Socket socket) {
        try (
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
        ) {
            HttpRequest request = null;

            try {
                request = new HttpRequest(context);
                processRequest(inputStream, request);
            } catch (HttpProtocolException e) {
                log.warn("error parsing http request : {} ", request);
                writeErrorResponse(outputStream);
                return;
            }

            HttpResponse response = new HttpResponse(request);
            processResponse(request, response);
            HttpResponseWriter.write(outputStream, response);
            log.info(response.toString());

        } catch (IOException e) {
            log.warn("io exception occurred while processing request : {}{}", e.getClass(), e.getMessage());
        }
    }

    public HttpRequest processRequest(InputStream input, HttpRequest request) throws IOException {
        HttpRequestParser.parse(request, input);

        log.info(request.toString());

        return request;
    }

    public void processResponse(HttpRequest request, HttpResponse response) throws IOException {
        context.handle(request, response);
    }

    private static void writeErrorResponse(BufferedOutputStream outputStream) throws IOException {
        HttpResponse errorResponse = new HttpResponse(null);
        errorResponse.sendError(HttpStatus.BAD_REQUEST);
        HttpResponseWriter.write(outputStream, errorResponse);
    }

}
