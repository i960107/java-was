package codesquad.http;

import codesquad.server.Handler;
import codesquad.server.HandlerContext;
import codesquad.server.exception.HandlerException;
import codesquad.http.exception.HttpProtocolException;
import codesquad.server.exception.MethodNotAllowedException;
import codesquad.server.exception.NoMatchHandlerException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
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
            WasRequest request = new WasRequest();
            WasResponse response = new WasResponse(outputStream);
            try {
                processRequest(request, inputStream);
                processResponse(request, response);
            } catch (HttpProtocolException e) {
                response.sendError(request.getProtocol(), HttpStatus.BAD_REQUEST, HttpHeaders.getDefault());
            }
        }
    }

    public WasRequest processRequest(WasRequest request, InputStream input) throws IOException {
        HttpRequestParser.parse(request, input);
        log.info(request.toString());
        return request;
    }

    public void processResponse(WasRequest request, WasResponse response) throws IOException {

        try {
            Optional<Handler> handler = handlerContext.getMappedHandler(request);
            if (handler.isEmpty()) {
                throw new NoMatchHandlerException();
            }

            handler.get().handle(request, response);

        } catch (HandlerException he) {
            if (he instanceof MethodNotAllowedException) {
                response.sendError(request.getProtocol(), HttpStatus.METHOD_NOT_ALLOWED, HttpHeaders.getDefault());
            } else {
                response.sendError(request.getProtocol(), HttpStatus.NOT_FOUND, HttpHeaders.getDefault());
            }
        }
    }

}
