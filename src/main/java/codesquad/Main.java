package codesquad;

import codesquad.application.db.InMemoryUserRepository;
import codesquad.application.handler.IndexHandler;
import codesquad.application.handler.UserListHandler;
import codesquad.application.handler.UserLoginHandler;
import codesquad.application.handler.UserLogoutHandler;
import codesquad.application.handler.UserRegisterHandler;
import codesquad.was.http.HttpProtocol;
import codesquad.was.server.ServerContext;
import codesquad.was.server.DefaultHandler;
import codesquad.was.server.authenticator.DefaultAuthenticator;
import codesquad.was.server.session.InMemorySessionManager;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        ServerContext handlerServerContext = new ServerContext(
                new InMemorySessionManager(),
                new DefaultAuthenticator(userRepository)
        );
        handlerServerContext.addHandler("/create", new UserRegisterHandler(userRepository));
        handlerServerContext.addHandler("/login", new UserLoginHandler());
        handlerServerContext.addHandler("/logout", new UserLogoutHandler());
        handlerServerContext.addHandler("/user/list", new UserListHandler(userRepository));
        handlerServerContext.addHandler("/index.html", new IndexHandler());
        handlerServerContext.addHandler("/", new DefaultHandler());

        HttpProtocol httpProtocol = new HttpProtocol(handlerServerContext);
        httpProtocol.start();
        //todo 종료 graceful하게
    }
}
