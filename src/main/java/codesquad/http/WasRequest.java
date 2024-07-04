package codesquad.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class WasRequest implements Request {

    private String method;

    private String path;

    private String protocol;

    private String host;

    private HttpHeader headers;

    private Map<String, String> parameters;

    private byte[] body;

    public WasRequest(String method,
                      String path,
                      String protocol,
                      String host,
                      HttpHeader headers,
                      Map<String, String> parameters,
                      byte[] body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.host = host;
        this.headers = headers;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    @Override
    public Optional<String> getHeader(String name) {
        return headers.getHeader(name);
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public Optional<String> getParameter(String name) {
        return Optional.of(parameters.get(name));
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WasRequest { ").append(System.lineSeparator());
        sb.append("method='").append(method).append('\'').append(System.lineSeparator());
        sb.append("path='").append(path).append('\'').append(System.lineSeparator());
        sb.append("protocol='").append(protocol).append('\'').append(System.lineSeparator());
        sb.append("host='").append(host).append('\'').append(System.lineSeparator());
        sb.append("headers=").append(headers).append(System.lineSeparator());
        sb.append("parameters=").append(parameters).append(System.lineSeparator());
        sb.append("body=").append(Arrays.toString(body)).append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        return sb.toString();
    }
}
