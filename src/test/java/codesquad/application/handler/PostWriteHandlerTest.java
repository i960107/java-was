package codesquad.application.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.TestUtil.setUpDbAndGetConnectionPool;

import codesquad.application.db.JdbcTemplate;
import codesquad.application.db.PostDao;
import codesquad.was.dbcp.ConnectionPool;
import codesquad.was.http.HttpCookie;
import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpRequestParser;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.HttpStatus;
import codesquad.was.server.ServerContext;
import codesquad.was.server.authenticator.Authenticator;
import codesquad.was.server.authenticator.Principal;
import codesquad.was.server.authenticator.Role;
import codesquad.was.server.session.InMemorySessionManager;
import codesquad.was.server.session.Session;
import codesquad.was.server.session.SessionManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostWriteHandlerTest {

    private static PostWriteHandler handler;

    private static ServerContext context;

    private static SessionManager sessionManager;

    private static PostDao dao;

    private static String sid;

    @BeforeAll
    static void beforeAll() {
        context = new ServerContext();

        sessionManager = new InMemorySessionManager();
        context.setSessionManager(sessionManager);

        ConnectionPool pool = setUpDbAndGetConnectionPool();
        dao = new PostDao(new JdbcTemplate(pool));
        handler = new PostWriteHandler(dao);
        context.addHandler("/post", handler);

        context.setAuthenticator(new Authenticator() {
            @Override
            public Principal authenticate(HttpRequest request) {
                if (request.getCookie(HttpHeaders.SID).isEmpty()) {
                    return null;
                }
                return new Principal(1L, "woowa", Role.USER);
            }
        });
    }

    @BeforeEach
    void setUp() {
        dao.deleteAll();
    }

    @Test
    void testPostWithAuthenticatedUser() throws IOException {
        //given
        InputStream input = getClass().getClassLoader().getResourceAsStream("post-create.txt");
        HttpRequest request = new HttpRequest(context);
        Session session = request.getSession(true);
        sid = session.getId();
        HttpRequestParser.parse(request, input);
        request.setCookies(List.of(new HttpCookie(HttpHeaders.SID, sid)));
        request.login();

        HttpResponse response = new HttpResponse(request);

        handler.doPost(request, response);

        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertEquals("/index.html", response.getHeaders().getHeaderSingleValue(HttpHeaders.LOCATION).get());
        assertEquals(1, dao.findAll().size());
    }

    @Test
    void testPostWithUnAuthenticatedUser() throws IOException {
        //given
        InputStream input = getClass().getClassLoader().getResourceAsStream("post-create.txt");
        HttpRequest request = new HttpRequest(context);
        HttpRequestParser.parse(request, input);
        HttpResponse response = new HttpResponse(request);

        handler.doPost(request, response);

        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertEquals("/login/index.html", response.getHeaders().getHeaderSingleValue(HttpHeaders.LOCATION).get());
        assertEquals(0, dao.findAll().size());
    }

}
