package codesquad.processor;

import codesquad.http.WasRequest;
import codesquad.http.WasResponse;
import codesquad.processor.exception.ProcessorException;

public abstract class RequestProcessor {
    public void process(WasRequest request, WasResponse response) throws ProcessorException {
        switch (request.getMethod()) {
            case GET:
                doGet(request, response);
                break;
            case POST:
                doPost(request, response);
                break;
        }
    }

    public abstract void doGet(WasRequest request, WasResponse response) throws ProcessorException;

    public abstract void doPost(WasRequest request, WasResponse response) throws  ProcessorException;
}
