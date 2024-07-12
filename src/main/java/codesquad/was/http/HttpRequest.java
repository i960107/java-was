package codesquad.was.http;

import codesquad.was.server.ServerContext;
import codesquad.was.server.authenticator.Principal;
import codesquad.was.server.session.Session;
import codesquad.was.server.session.SessionManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    public static final String SESSION_PRINCIPAL_KEY = "principal";

    private HttpMethod method;

    private String path;

    private String protocol;

    private String host;

    private MimeTypes contentType;

    private HttpHeaders headers;

    private Map<String, List<String>> parameters;

    private List<HttpCookie> cookies;

    private byte[] body;

    private Session session;

    private Principal principal;

    private ServerContext context;

    private String sessionId;

    public HttpRequest() {
    }

    public HttpRequest(ServerContext context) {
        this.context = context;
        this.cookies = new ArrayList<>();
        parameters = new HashMap<>();
    }

    public HttpRequest(
            ServerContext context,
            HttpMethod method,
            String path,
            String protocol,
            String host,
            HttpHeaders headers,
            List<HttpCookie> cookies,
            Map<String, List<String>> parameters,
            byte[] body) {
        this.context = context;
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.host = host;
        this.headers = headers;
        this.cookies = cookies == null ? new ArrayList<>() : cookies;
        this.parameters = parameters == null ? new HashMap<>() : parameters;
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

    public List<HttpCookie> getCookies() {
        return cookies.stream().toList();
    }

    public Principal getPrincipal() {
        return principal;
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

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public void addParameters(Map<String, List<String>> parameters) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.putAll(parameters);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setCookies(List<HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public boolean isGet() {
        return method == HttpMethod.GET;
    }

    public boolean isPost() {
        return method == HttpMethod.POST;
    }

    public Optional<HttpCookie> getCookie(String name) {
        return cookies.stream().filter(cookie -> cookie.getKey().equals(name)).findFirst();
    }

    public Session getSession(boolean create) {
        if (this.session != null) {
            return session;
        }

        // sessionId 탐색 순서 1. request.sessionId 2. request.cookie(SID) 3.새로 생성
        String existingSessionId = sessionId;
        if (existingSessionId == null) {
            Optional<HttpCookie> sidCookie = getCookie(HttpHeaders.SID);
            if (sidCookie.isPresent()) {
                existingSessionId = sidCookie.get().getValue();
            }
        }

        SessionManager sessionManager = context.getSessionManager();
        if (existingSessionId != null) {
            sessionManager.getSession(existingSessionId)
                    .ifPresent(existingSession -> {
                        this.session = existingSession;
                        this.sessionId = existingSession.getId();
                    });
        }
        if (session == null && create) {
            session = sessionManager.createSession();
            sessionId = session.getId();
        }

        return session;
    }

    public boolean isAuthenticated() {
        return this.principal != null;
    }

    public void authenticate() {
        Session session = getSession(false);
        if (session == null) {
            return;
        }
        session.getAttribute(SESSION_PRINCIPAL_KEY)
                .ifPresent(principal -> {
                    this.principal = (Principal) principal;
                });
    }

    public void login() {
        Principal newPrincipal = context.getAuthenticator().authenticate(this);
        getSession(true).setAttribute(SESSION_PRINCIPAL_KEY, newPrincipal);
        this.principal = newPrincipal;
    }

    public String getSessionId() {
        if (sessionId != null) {
            return sessionId;
        }

        sessionId = getSession(true).getId();
        return sessionId;
    }

    public void logout() {
        this.principal = null;
        Session session = getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_PRINCIPAL_KEY);
        }
    }

    public void invalidateSession() {
        Session existingSession = getSession(false);
        if (existingSession == null) {
            return;
        }
        SessionManager sessionManager = context.getSessionManager();
        sessionManager.removeSession(existingSession.getId());
        this.session = null;
        this.sessionId = null;
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
        sb.append("cookies=").append(cookies).append(System.lineSeparator());
        sb.append("parameters=").append(parameters).append(System.lineSeparator());
        sb.append("body=").append(Arrays.toString(body)).append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        return sb.toString();
    }

}
