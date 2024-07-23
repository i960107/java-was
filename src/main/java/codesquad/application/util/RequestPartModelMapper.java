package codesquad.application.util;

import codesquad.application.handler.exception.ModelMappingException;
import codesquad.was.http.MimeType;
import codesquad.was.http.Part;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestPartModelMapper {

    private static Logger log = LoggerFactory.getLogger(RequestPartModelMapper.class);

    private RequestPartModelMapper() {
    }

    public static <T> T map(List<Part> parts, Class<T> type) {
        try {
            T model = type.getDeclaredConstructor().newInstance();

            for (Field field : type.getDeclaredFields()) {
                Optional<Part> matched = parts.stream().filter(part -> part.getName().equals(field.getName()))
                        .findAny();
                if (matched.isEmpty()) {
                    continue;
                }
                Part part = matched.get();
                field.setAccessible(true);
                Object value;
                if (part.getContentType().equals(MimeType.text)) {
                    value = castValue(field.getType(), new String(part.getContent()));
                } else {
                    value = part;
                }
                field.set(model, value);
            }
            return model;
        } catch (Exception e) {
            log.warn("fail to map parameters to model {} : {}", type.getName(), e);
            throw new ModelMappingException("fail to map parameters to model" + type.getName());
        }
    }

    private static Object castValue(Class<?> targetType, String value) throws IllegalArgumentException {
        if (targetType.equals(String.class)) {
            return value;
        } else if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
            return Integer.parseInt(value);
        } else if (targetType.equals(double.class) || targetType.equals(Double.class)) {
            return Double.valueOf(value);
        } else if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) {
            return Boolean.valueOf(value);
        } else if (targetType.equals(long.class) || targetType.equals(Long.class)) {
            return Long.valueOf(value);
        } else {
            throw new IllegalArgumentException("Unsupported target type: " + targetType);
        }
    }
}
