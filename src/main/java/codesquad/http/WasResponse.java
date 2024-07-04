package codesquad.http;

import codesquad.HttpSCStatus;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class WasResponse<T> implements Response<T> {
    private final String protocol;
    private final HttpSCStatus status;
    private final HttpHeader header;
    private final T body;

    public WasResponse(String protocol, HttpSCStatus status, HttpHeader header, T body) {
        this.protocol = protocol;
        this.status = status;
        this.header = header;
        this.body = body;
    }

    public static WasResponse<Void> fail(String protocol, HttpSCStatus status, HttpHeader header) {
        return new WasResponse<>(protocol, status, header, null);
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
        return header.getHeaders();
    }

    @Override
    public Optional<String> getHeader(String key) {
        return header.getHeader(key);
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
        sb.append("headers=").append(header.getHeaders()).append(System.lineSeparator());
        sb.append("body=").append(body).append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        return sb.toString();
    }
}


