package codesquad.was.server;

import codesquad.was.dbcp.ConnectionPool;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.HttpStatus;
import codesquad.was.server.authenticator.Authenticator;
import codesquad.was.server.exception.AuthenticationException;
import codesquad.was.server.exception.FilterRegistrationException;
import codesquad.was.server.exception.MalformedPathException;
import codesquad.was.server.exception.MethodNotAllowedException;
import codesquad.was.server.exception.ResourceNotFoundException;
import codesquad.was.server.session.SessionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerContext {

    private static final String DEFAULT_PATH = "/";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SessionManager sessionManager;

    private final Authenticator authenticator;

    private final Map<String, Handler> mappings;

    private final Pattern supportedPathPattern = Pattern.compile("^[\\w\\-./가-힣]*$");

    private final List<Filter> filters;

    private final ConnectionPool connectionPool;

    public ServerContext(
            SessionManager sessionManager,
            Authenticator authenticator,
            ConnectionPool connectionPool
    ) {
        this.sessionManager = sessionManager;
        this.authenticator = authenticator;
        this.connectionPool = connectionPool;
        mappings = new HashMap<>();
        filters = new ArrayList<>();
    }

    public void addHandler(String path, Handler handler) {
        if (!supportedPathPattern.matcher(path).matches()) {
            throw new MalformedPathException();
        }
        if (handler == null) {
            log.warn("fail to register handler : null");
            throw new FilterRegistrationException();
        }
        handler.setServerContext(this);
        this.mappings.put(path, handler);
    }

    private void addFilter(int order, Filter filter) {
        if (filters.size() != order) {
            log.warn("fail to register {}. order should be {}", filter.getClass().getName(), filters.size());
            throw new FilterRegistrationException();
        }
        if (filter == null) {
            log.warn("fail to register filter : null");
            throw new FilterRegistrationException();
        }
        filters.add(filter);
    }

    public void handle(HttpRequest request, HttpResponse response) {

        try {
            Handler handler = getMappedHandler(request);

            filters
                    .forEach(filter -> filter.beforeHandler(request, response));

            log.info("handler mapped : {}", handler.getClass().getName());
            if (request.isGet()) {
                handler.doGet(request, response);
            } else if (request.isPost()) {
                handler.doPost(request, response);
            }

            filters
                    .forEach(filter -> filter.afterHandler(request, response));
        } catch (ResourceNotFoundException e) {
            response.sendError(HttpStatus.NOT_FOUND);
        } catch (MethodNotAllowedException e) {
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
        } catch (AuthenticationException e) {
            response.sendError(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Handler getMappedHandler(HttpRequest request) {
        Handler handler;
        if (!mappings.containsKey(request.getPath())) {

            handler = mappings.get(DEFAULT_PATH);
        } else {
            handler = mappings.get(request.getPath());
        }

        if (handler == null) {
            throw new ResourceNotFoundException();
        }
        return handler;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }
}
