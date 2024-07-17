package codesquad;

import codesquad.application.db.DBConfig;
import codesquad.application.db.JdbcTemplate;
import codesquad.application.db.PostDao;
import codesquad.application.db.UserDao;
import codesquad.application.handler.IndexHandler;
import codesquad.application.handler.PostWriteHandler;
import codesquad.application.handler.UserListHandler;
import codesquad.application.handler.UserLoginHandler;
import codesquad.application.handler.UserLogoutHandler;
import codesquad.application.handler.UserRegisterHandler;
import codesquad.was.http.HttpProtocol;
import codesquad.was.server.DefaultHandler;
import codesquad.was.server.ServerContext;
import codesquad.was.server.authenticator.DefaultAuthenticator;
import codesquad.was.server.session.InMemorySessionManager;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        DBConfig dbConfig = DBConfig.getDBConfig("application.properties");

        ServerContext context = new ServerContext();
        context.setSessionManager(new InMemorySessionManager());
        context.setConnectionPool(dbConfig);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(context.getConnectionPool());
        UserDao userDao = new UserDao(jdbcTemplate);
        context.setAuthenticator(new DefaultAuthenticator(userDao));

        context.addHandler("/create", new UserRegisterHandler(userDao));
        context.addHandler("/login", new UserLoginHandler());
        context.addHandler("/logout", new UserLogoutHandler());
        context.addHandler("/user/list", new UserListHandler(userDao));
        context.addHandler("/index.html", new IndexHandler());
        context.addHandler("/index", new IndexHandler());
        PostDao postDao = new PostDao(jdbcTemplate);
        context.addHandler("/post", new PostWriteHandler(postDao));
        context.addHandler("/", new DefaultHandler());

        HttpProtocol httpProtocol = new HttpProtocol(context);
        httpProtocol.start();
    }
}
