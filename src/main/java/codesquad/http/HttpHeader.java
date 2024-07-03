package codesquad.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpHeader {
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";

    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    public static final String HOST_HEADER = "Host";

    public static final String DATE_HEADER = "Date";

    public static final String CONNECTION_HEADER = "Connection";

    private final Map<String, String> headers;

    public HttpHeader() {
        this.headers = new HashMap<>();
    }

    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public Optional<String> getHeader(String name) {
        return Optional.ofNullable(headers.get(name));
    }

    public boolean containsKey(String name) {
        return headers.containsKey(name);
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
}
