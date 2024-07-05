package codesquad.server;

import java.lang.reflect.Field;
import java.util.Map;

public class RequestParamModelMapper {
    public static <T> T map(Map<String, String> requestParameters, Class<T> type) throws Exception {
        T model = type.getDeclaredConstructor().newInstance();

        for (Field field : type.getDeclaredFields()) {
            if (requestParameters.containsKey(field.getName())) {
                field.setAccessible(true);
                field.set(model, requestParameters.get(field.getName()));
            }
        }
        return model;
    }
}
