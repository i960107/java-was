package codesquad.processor;

import codesquad.http.WasRequest;
import codesquad.http.WasResponse;

public interface RequestProcessor {
     void process(WasRequest request, WasResponse response);
}
