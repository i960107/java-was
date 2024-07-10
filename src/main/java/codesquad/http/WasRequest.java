package codesquad.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WasRequest {

    private HttpMethod method;

    private String path;

    private String protocol;

    private String host;

    private MimeTypes contentType;

    private HttpHeaders headers;

    private Map<String, List<String>> parameters;

    private byte[] body;

    public WasRequest() {
        parameters = new HashMap<>();
    }

    public WasRequest(HttpMethod method,
                      String path,
                      String protocol,
                      String host,
                      HttpHeaders headers,
                      Map<String, List<String>> parameters,
                      byte[] body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.host = host;
        this.headers = headers;
        this.parameters = parameters;
        this.body = body;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Optional<HttpHeader> getHeader(String name) {
        return headers.getHeader(name);
    }

    public Map<String, List<String>> getParameterMap() {
        return Collections.unmodifiableMap(parameters);
    }

    public List<String> getParameters(String name) {
        if (parameters.containsKey(name)) {
            return Collections.unmodifiableList(parameters.get(name));
        } else {
            return Collections.emptyList();
        }
    }

    public Optional<String> getParameter(String name) {
        return getParameters(name).stream().findFirst();
    }


    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public byte[] getBody() {
        return body;
    }

    public MimeTypes getContentType() {
        return contentType;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public void setContentType(MimeTypes contentType) {
        this.contentType = contentType;
    }

    public void addParameters(Map<String, List<String>> parameters) {
        this.parameters.putAll(parameters);
    }

    public void setBody(byte[] body) {
        this.body = body;
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
