package codesquad.http;

import codesquad.http.exception.HttpProtocolException;
import codesquad.processor.exception.ProcessorException;
import codesquad.was.util.server.Handler;
import codesquad.was.util.server.HandlerContext;
import codesquad.was.util.server.exception.HandlerException;
import codesquad.was.util.server.exception.MethodNotAllowedException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProcessor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final HandlerContext handlerContext;

    public HttpProcessor(HandlerContext handlerContext) {
        this.handlerContext = handlerContext;
    }

    public void process(Socket socket) {
        try (
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
        ) {
            WasRequest request;
            WasResponse response;
            HttpStatus status = HttpStatus.OK;

            try {
                request = processRequest(inputStream);
            } catch (HttpProtocolException e) {
                request = new WasRequest();
                status = HttpStatus.BAD_REQUEST;
            }

            response = new WasResponse(request);
            processResponse(request, response);

            if (status != HttpStatus.OK) {
                response.sendError(status, HttpHeaders.getDefault());
            }

            HttpResponseWriter.write(outputStream, response);
        } catch (IOException e) {
            log.warn("io exception occured while processing request : {}", e.getMessage());
        }
    }

    public WasRequest processRequest(InputStream input) throws IOException {
        WasRequest request = new WasRequest();
        HttpRequestParser.parse(request, input);

        log.info(request.toString());

        return request;
    }

    public void processResponse(WasRequest request, WasResponse response) throws IOException {
        HttpStatus status = HttpStatus.OK;
        try {
            Handler handler = handlerContext.getMappedHandler(request);

            handler.handle(request, response);
        } catch (HandlerException he) {
            if (he.getClass().equals(MethodNotAllowedException.class)) {
                status = HttpStatus.METHOD_NOT_ALLOWED;
            } else {
                status = HttpStatus.NOT_FOUND;
            }
        } catch (ProcessorException processorException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (Exception exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        if (status != HttpStatus.OK) {
            response.sendError(status, HttpHeaders.getDefault());
        }

        log.info(response.toString());
    }

}
