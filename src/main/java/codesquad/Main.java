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
        ServerContext context = null;
        try {
            context = new ServerContext();
            // 1. session manager 설정
            context.setSessionManager(new InMemorySessionManager());

            // 2. connection pool 설정
            DBConfig dbConfig = DBConfig.getDBConfig("application.properties");
            context.setConnectionPool(dbConfig);

            // 3. dao 객체 생성
            JdbcTemplate jdbcTemplate = new JdbcTemplate(context.getConnectionPool());
            UserDao userDao = new UserDao(jdbcTemplate);
            PostDao postDao = new PostDao(jdbcTemplate);

            // 4. authenticator 설정
            context.setAuthenticator(new DefaultAuthenticator(userDao));

            // 5. handler 등록
            context.addHandler("/create", new UserRegisterHandler(userDao));
            context.addHandler("/login", new UserLoginHandler());
            context.addHandler("/logout", new UserLogoutHandler());
            context.addHandler("/user/list", new UserListHandler(userDao));
            context.addHandler("/index.html", new IndexHandler(postDao));
            context.addHandler("/post", new PostWriteHandler(postDao));
            context.addHandler("/", new DefaultHandler());
        } catch (Exception e) {
            if (context != null) {
                context.clear();
            }
            System.exit(1);
        }

        HttpProtocol server = null;
        server = new HttpProtocol(context);
        server.start();
    }
}
