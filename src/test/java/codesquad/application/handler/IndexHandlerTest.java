package codesquad.application.handler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IndexHandlerTest {
    private static IndexHandler indexHandler;

    @BeforeAll
    static void beforeAll() {
        indexHandler = new IndexHandler();
    }

    @Test
    @DisplayName("파일이 있으면 파일을 응답에 쓴다")
    void response() {
    }
}
