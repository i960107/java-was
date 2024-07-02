package codesquad.http;

import java.util.Map;
import java.util.Optional;

public interface Response<T> {

    String getProtocol();

    String getStatusCode();

    String getStatusMessage();

    Map<String, String> getHeaders();

    Optional<String> getHeader(String key);

    T getBody();
}
