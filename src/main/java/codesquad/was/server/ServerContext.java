package codesquad.was.server;

import codesquad.application.db.DBConfig;
import codesquad.was.dbcp.ConnectionPool;
import codesquad.was.dbcp.DefaultConnectionPool;
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
import codesquad.was.util.IOUtil;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerContext {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_PATH = "/";

    private SessionManager sessionManager;

    private Authenticator authenticator;

    private Map<String, Handler> mappings;

    private Pattern supportedPathPattern = Pattern.compile("^[\\w\\-./가-힣]*$");

    private List<Filter> filters;

    private ConnectionPool connectionPool;

    public ServerContext(
            SessionManager sessionManager,
            Authenticator authenticator,
            DBConfig dbConfig
    ) {
        this.sessionManager = sessionManager;
        this.authenticator = authenticator;
        this.connectionPool = setUpConnectionPool(dbConfig);
        createTable();
        mappings = new HashMap<>();
        filters = new ArrayList<>();
    }

    public ServerContext() {
        this.mappings = new HashMap<>();
        this.filters = new ArrayList<>();
    }

    private ConnectionPool setUpConnectionPool(DBConfig dbConfig) {
        ConnectionPool pool = new DefaultConnectionPool(
                dbConfig.getUrl(),
                dbConfig.getUsername(),
                dbConfig.getPassword(),
                dbConfig.getMaxPoolSize(),
                dbConfig.getMinIdle(),
                dbConfig.getConnectionTimeout(),
                dbConfig.getIdleTimeout()
        );
        if (dbConfig.getUrl().startsWith("jdbc:h2")) {
            try {
                Server h2console = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
                h2console.start();
                log.info("h2 console started");
            } catch (SQLException exception) {
            }
        }
        return pool;
    }

    private void createTable() {
        try (Connection conn = this.connectionPool.getConnection()) {
            RunScript.execute(conn,
                    new InputStreamReader(IOUtil.getClassPathResource("init.sql")));
            log.info("table created");
        } catch (SQLException e) {
            log.warn("fail to create table");
        }
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

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setConnectionPool(DBConfig config) {
        this.connectionPool = setUpConnectionPool(config);
        createTable();
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        createTable();
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

    public ConnectionPool getConnectionPool() {
        return connectionPool;
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

    public void clear() {
        try {
            log.info("clear server context");
            connectionPool.shutDown();
        } catch (SQLException e) {
            log.warn("fail to shutdown connection pool");
        }
    }
}
