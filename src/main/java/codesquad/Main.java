package codesquad;

import codesquad.application.db.DBConfig;
import codesquad.application.db.InMemoryUserRepository;
import codesquad.application.handler.IndexHandler;
import codesquad.application.handler.UserListHandler;
import codesquad.application.handler.UserLoginHandler;
import codesquad.application.handler.UserLogoutHandler;
import codesquad.application.handler.UserRegisterHandler;
import codesquad.was.dbcp.ConnectionPool;
import codesquad.was.dbcp.DefaultConnectionPool;
import codesquad.was.http.HttpProtocol;
import codesquad.was.server.ServerContext;
import codesquad.was.server.DefaultHandler;
import codesquad.was.server.authenticator.DefaultAuthenticator;
import codesquad.was.server.session.InMemorySessionManager;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        DBConfig dbConfig = DBConfig.getDBConfig("application.properties");
        ConnectionPool connectionPool = new DefaultConnectionPool(
                dbConfig.getUrl(),
                dbConfig.getUsername(),
                dbConfig.getPassword(),
                dbConfig.getMaxPoolSize(),
                dbConfig.getMinIdle(),
                dbConfig.getConnectionTimeout(),
                dbConfig.getIdleTimeout()
        );
        ServerContext context = new ServerContext(
                new InMemorySessionManager(),
                new DefaultAuthenticator(userRepository),
                connectionPool
        );
        context.addHandler("/create", new UserRegisterHandler(userRepository));
        context.addHandler("/login", new UserLoginHandler());
        context.addHandler("/logout", new UserLogoutHandler());
        context.addHandler("/user/list", new UserListHandler(userRepository));
        context.addHandler("/index.html", new IndexHandler());
        context.addHandler("/", new DefaultHandler());

        HttpProtocol httpProtocol = new HttpProtocol(context);
        httpProtocol.start();
    }
}
