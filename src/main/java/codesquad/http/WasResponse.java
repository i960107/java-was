package codesquad.http;

import codesquad.HttpSCStatus;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class WasResponse<T> implements Response<T> {
    private final String protocol;
    private final HttpSCStatus status;
    private final Map<String, String> headers;
    private final T body;

    public WasResponse(String protocol, HttpSCStatus status, Map<String, String> headers, T body) {
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static WasResponse<Void> fail(String protocol, HttpSCStatus status, Map<String, String> headers) {
        return new WasResponse<>(protocol, status, headers, null);
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getStatusCode() {
        return status.getCode();
    }

    @Override
    public String getStatusMessage() {
        return status.name();
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public Optional<String> getHeader(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    @Override
    public T getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WasResponse { ").append(System.lineSeparator());
        sb.append("protocol='").append(protocol).append('\'').append(System.lineSeparator());
        sb.append("statusCode='").append(getStatusCode()).append('\'').append(System.lineSeparator());
        sb.append("statusMeessage='").append(getStatusMessage()).append('\'').append(System.lineSeparator());
        sb.append("headers=").append(headers).append(System.lineSeparator());
        sb.append("body=").append(body).append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        return sb.toString();
    }
}


