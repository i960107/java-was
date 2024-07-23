package codesquad.application.handler;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.TestUtil.assertRedirectResponse;
import static util.TestUtil.get;
import static util.TestUtil.post;
import static util.TestUtil.setUpDbAndGetConnectionPool;

import codesquad.application.db.JdbcTemplate;
import codesquad.application.db.UserDao;
import codesquad.application.model.User;
import codesquad.was.dbcp.ConnectionPool;
import codesquad.was.http.HttpCookie;
import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.server.ServerContext;
import codesquad.was.server.authenticator.DefaultAuthenticator;
import codesquad.was.server.session.InMemorySessionManager;
import codesquad.was.server.session.SessionManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserLogoutTest {

    private static String path;

    private static UserLogoutHandler handler;

    private static ServerContext serverContext;

    private static SessionManager sessionManager;

    private static UserDao userDao;

    private String username;
    private String password;
    private String sessionId;

    @BeforeAll
    static void beforeAll() {

        serverContext = new ServerContext();

        sessionManager = new InMemorySessionManager();
        serverContext.setSessionManager(sessionManager);

        ConnectionPool connectionPool = setUpDbAndGetConnectionPool();
        serverContext.setConnectionPool(connectionPool);

        path = "/logout";
        handler = new UserLogoutHandler();
        serverContext.addHandler(path, handler);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(serverContext.getConnectionPool());
        userDao = new UserDao(jdbcTemplate);
        serverContext.setAuthenticator(new DefaultAuthenticator(userDao));
    }

    @BeforeEach
    void setUp() {
        userDao.deleteAll();
        username = "박재성";
        password = "123456";
        User user = new User(username, "nickname", password);
        userDao.save(user);

        Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("username", List.of(username));
        parameters.put("password", List.of(password));

        HttpRequest request = post(serverContext, "/login", parameters, null);

        request.login();
        this.sessionId = request.getSessionId();
    }

    @Test
    @DisplayName("로그아웃시 세션을 만료하고 쿠키를 지운다.")
    void testLogoutSuccess() {
        //given
        HttpHeaders headers = HttpHeaders.getDefault();
        HttpRequest request = get(serverContext, path, headers);
        HttpResponse response = new HttpResponse(request);
        request.setCookies(List.of(new HttpCookie(HttpHeaders.SID, sessionId)));

        //when
        handler.doGet(request, response);

        //then
        assertNull(request.getPrincipal());
        assertNull(request.getSession(false));
        assertTrue(sessionManager.getSession(sessionId).isEmpty());
        assertRedirectResponse(response, "/index.html");
        assertTrue(response.getHeaders().contains(HttpHeaders.SET_COOKIE));
        assertTrue(response.getHeaders().getHeaderSingleValue(HttpHeaders.SET_COOKIE).isPresent());
        String cookieValue = response.getHeaders().getHeaderSingleValue(HttpHeaders.SET_COOKIE).get();
        assertTrue(cookieValue.contains(HttpHeaders.SID + "=; "));
        assertTrue(cookieValue.contains("Max-Age=0"));
    }

}
