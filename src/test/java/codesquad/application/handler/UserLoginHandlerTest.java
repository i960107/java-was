package codesquad.application.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.TestUtil.assertRedirectResponse;
import static util.TestUtil.get;
import static util.TestUtil.post;

import codesquad.application.db.InMemoryUserRepository;
import codesquad.application.model.User;
import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.server.ServerContext;
import codesquad.was.server.authenticator.DefaultAuthenticator;
import codesquad.was.server.authenticator.Role;
import codesquad.was.server.session.InMemorySessionManager;
import codesquad.was.server.session.SessionManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserLoginHandlerTest {
    private static UserLoginHandler handler;

    private static ServerContext serverContext;

    private static SessionManager sessionManager;

    private static InMemoryUserRepository userRepository;

    private String username;
    private String password;

    @BeforeAll
    static void beforeAll() {
        sessionManager = new InMemorySessionManager();
        userRepository = new InMemoryUserRepository();
        ServerContext context = new ServerContext(
                sessionManager,
                new DefaultAuthenticator(userRepository));
        serverContext = context;
        handler = new UserLoginHandler();
        context.addHandler("/login", handler);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        username = "박재성";
        password = "123456";
        User user = new User(username, "nickname", password);
        userRepository.save(user);
    }

    @Test
    @DisplayName("로그인 성공시 SID 쿠키를 담아서 리다이렉트한다.")
    void loginSuccess() {
        //given
        Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("username", List.of(username));
        parameters.put("password", List.of(password));

        HttpRequest request = post(serverContext, "/login", parameters, null);
        HttpResponse response = new HttpResponse(request);

        //when
        handler.doPost(request, response);

        //then
        assertEquals(true, request.isAuthenticated());
        assertNotNull(request.getPrincipal());
        assertEquals(username, request.getPrincipal().getUsername());
        assertEquals(Role.USER, request.getPrincipal().getRole());
        assertNotNull(request.getSession(false));
        assertTrue(request.getSession(false).getAttribute(HttpRequest.SESSION_PRINCIPAL_KEY).isPresent());
        assertRedirectResponse(response, "/index.html");
        assertTrue(response.getHeaders().contains(HttpHeaders.SET_COOKIE));
        assertTrue(response.getHeaders().getHeaderSingleValue(HttpHeaders.SET_COOKIE).isPresent());
        assertTrue(response.getHeaders().getHeaderSingleValue(HttpHeaders.SET_COOKIE).get().contains(HttpHeaders.SID));
    }

    @Test
    @DisplayName("로그인 실패시 로그인 실패 페이지로 리다이렉트한다.")
    void loginFail() {
        //given
        Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("username", List.of(username));
        parameters.put("password", List.of(""));

        HttpRequest request = post(serverContext, "/login", parameters, null);
        HttpResponse response = new HttpResponse(request);

        //when
        handler.doPost(request, response);

        //then
        assertRedirectResponse(response, "/user/login_failed.html");
    }

    @Test
    @DisplayName("로그인 페이지 요청시 로그인 페이지로 리다이렉트한다.")
    void getLoginPage() {
        //given
        HttpRequest request = get("/login");
        HttpResponse response = new HttpResponse(request);

        //when
        handler.doGet(request, response);

        //then
        assertRedirectResponse(response, "/login/index.html");
    }

}
