package codesquad.server;

import codesquad.http.HttpHeaders;
import codesquad.http.HttpStatus;
import codesquad.http.WasRequest;
import codesquad.http.WasResponse;
import codesquad.processor.RequestProcessor;
import codesquad.processor.exception.ProcessorException;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlMappingHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(UrlMappingHandler.class);

    private final Map<String, RequestProcessor> mappings;

    public UrlMappingHandler(Map<String, RequestProcessor> mappings) {
        this.mappings = mappings;
    }

    @Override
    public void handle(WasRequest request, WasResponse response) throws IOException {
        try {
            RequestProcessor processor = mappings.get(request.getPath());
            processor.process(request, response);
            log.info("request is handled by " + processor.getClass().getName());
        } catch (ProcessorException e) {
            response.sendError(request.getProtocol(), HttpStatus.INTERNAL_SERVER_ERROR, HttpHeaders.getDefault());
        }
    }

    @Override
    public boolean canHandle(WasRequest request) {
        return mappings.containsKey(request.getPath());
    }
}
