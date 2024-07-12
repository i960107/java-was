package codesquad.was.server;

import codesquad.was.http.HttpStatus;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.server.exception.FilterRegistrationException;
import codesquad.was.server.exception.MalformedPathException;
import codesquad.was.server.exception.MethodNotAllowedException;
import codesquad.was.server.exception.ResourceNotFoundException;
import codesquad.was.server.session.InMemorySessionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerContext {

    private static final String DEFAULT_PATH = "/";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final InMemorySessionManager sessionManager;

    private final Map<String, Handler> mappings;

    private final List<Filter> filters;

    private final Pattern supportedPathPattern = Pattern.compile("^[\\w\\-./가-힣]*$");


    public HandlerContext() {
        this.sessionManager = new InMemorySessionManager();
        mappings = new HashMap<>();
        filters = new ArrayList<>();
    }

    public InMemorySessionManager getSessionManager() {
        return sessionManager;
    }

    public void addHandler(String path, Handler handler) {
        if (!supportedPathPattern.matcher(path).matches()) {
            throw new MalformedPathException();
        }
        if (handler == null) {
            log.warn("fail to register handler : null");
            throw new FilterRegistrationException();
        }
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
            response.sendError(HttpStatus.NOT_MODIFIED);
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
}
