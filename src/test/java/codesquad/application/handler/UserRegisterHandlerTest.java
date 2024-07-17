package codesquad.application.handler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.TestUtil.assertRedirectResponse;
import static util.TestUtil.post;
import static util.TestUtil.setUpDbAndJdbcTemplate;

import codesquad.application.db.JdbcTemplate;
import codesquad.application.db.UserDao;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.HttpStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserRegisterHandlerTest {

    private static UserDao userDao;

    private static UserRegisterHandler userRegisterHandler;

    @BeforeAll
    static void beforeAll() {
        JdbcTemplate jdbcTemplate = setUpDbAndJdbcTemplate();
        userDao = new UserDao(jdbcTemplate);
        userRegisterHandler = new UserRegisterHandler(userDao);
    }

    @BeforeEach
    void setUp() {
        userDao.deleteAll();
    }

    @Test
    @DisplayName("회원가입 후 리다이렉트 한다.")
    void registerUser() {
        //given
        Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("username", List.of("iwer"));
        parameters.put("nickname", List.of("박재성"));
        parameters.put("password", List.of("123"));

        HttpRequest request = post("/create", parameters, null);
        HttpResponse response = new HttpResponse(request);

        //when
        userRegisterHandler.doPost(request, response);

        //then
        assertEquals(1, userDao.findAll().size());
        assertThat(userDao.findAll().get(0))
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("username", "iwer")
                .hasFieldOrPropertyWithValue("nickname", "박재성")
                .hasFieldOrPropertyWithValue("password", "123");
        assertRedirectResponse(response, "/index.html");
    }

    @Test
    @DisplayName("누락된 필드가 있으면 예외를 던진다.")
    void whenRequireParameterIsAbsentThrowsError() {
        //given
        Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("username", List.of("iwer"));
        parameters.put("password", List.of("123"));

        HttpRequest request = post("/create", parameters, null);
        HttpResponse response = new HttpResponse(request);

        //when
        userRegisterHandler.doPost(request, response);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

}
