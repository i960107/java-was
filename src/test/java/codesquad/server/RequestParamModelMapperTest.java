package codesquad.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import codesquad.application.handler.exception.ModelMappingException;
import codesquad.application.util.RequestParamModelMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestParamModelMapperTest {
    public static class TestModel {
        private String name;
        private int age;
        private List<String> interests;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public List<String> getInterests() {
            return interests;
        }

        public void setInterests(List<String> interests) {
            this.interests = interests;
        }
    }


    @DisplayName("모든 요청 매개변수를 모델 객체에 매핑")
    @Test
    void testMap_allParameters() {
        // given
        Map<String, List<String>> requestParameters = new HashMap<>();
        requestParameters.put("name", List.of("John"));
        requestParameters.put("age", List.of("30"));
        requestParameters.put("interests", Arrays.asList("Reading", "Traveling"));

        // when
        TestModel model = RequestParamModelMapper.map(requestParameters, TestModel.class);

        // then
        assertEquals("John", model.getName());
        assertEquals(30, model.getAge());
        assertEquals(List.of("Reading", "Traveling"), model.getInterests());
    }

    @DisplayName("필드가 누락된 경우 모델 객체에 매핑하지 않음")
    @Test
    void testMap_missingField() {
        // given
        Map<String, List<String>> requestParameters = new HashMap<>();
        requestParameters.put("name", List.of("John"));

        // when
        TestModel model = RequestParamModelMapper.map(requestParameters, TestModel.class);

        // then
        assertEquals("John", model.getName());
        assertEquals(0, model.getAge());  // int의 기본값은 0
        assertNull(model.getInterests()); // 리스트의 기본값은 null
    }

    @DisplayName("잘못된 타입의 매개변수를 모델 객체에 매핑하려고 할 때 예외 발생")
    @Test
    void testMap_invalidType() {
        // given
        Map<String, List<String>> requestParameters = new HashMap<>();
        requestParameters.put("name", List.of("John"));
        requestParameters.put("age", List.of("thirty")); // 잘못된 타입

        // when & then
        assertThrows(ModelMappingException.class, () -> {
            RequestParamModelMapper.map(requestParameters, TestModel.class);
        });
    }

    @DisplayName("빈 매개변수를 모델 객체에 매핑")
    @Test
    void testMap_emptyParameters() {
        // given
        Map<String, List<String>> requestParameters = null;

        // when && then
        assertThrows(ModelMappingException.class, () -> {
            RequestParamModelMapper.map(requestParameters, TestModel.class);
        });
    }
}
