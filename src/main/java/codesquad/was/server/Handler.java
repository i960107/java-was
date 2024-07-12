package codesquad.was.util.server;

import codesquad.http.WasRequest;
import codesquad.http.WasResponse;
import java.io.IOException;

public interface Handler {

    void handle(WasRequest request, WasResponse response) throws IOException;

    boolean canHandle(WasRequest request);
}
