package codesquad.application.handler;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.TestUtiㅣ.assertRedirectResponse;
import static util.TestUtiㅣ.get;
import static util.TestUtiㅣ.post;

import codesquad.application.db.InMemoryUserRepository;
import codesquad.application.model.User;
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

    private static InMemoryUserRepository userRepository;

    private String username;
    private String password;
    private String sessionId;

    @BeforeAll
    static void beforeAll() {
        path = "/logout";
        sessionManager = new InMemorySessionManager();
        userRepository = new InMemoryUserRepository();
        ServerContext context = new ServerContext(
                sessionManager,
                new DefaultAuthenticator(userRepository));
        serverContext = context;
        handler = new UserLogoutHandler();
        context.addHandler(path, handler);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        username = "박재성";
        password = "123456";
        User user = new User(username, "nickname", password);
        userRepository.save(user);

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
