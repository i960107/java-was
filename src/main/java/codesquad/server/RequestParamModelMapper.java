package codesquad.server;

import codesquad.processor.exception.ModelMappingException;
import codesquad.processor.exception.ProcessorException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class RequestParamModelMapper {

    public static <T> T map(Map<String, List<String>> requestParameters, Class<T> type) throws ProcessorException {
        try {
            T model = type.getDeclaredConstructor().newInstance();

            for (Field field : type.getDeclaredFields()) {
                if (!requestParameters.containsKey(field.getName())) {
                    continue;
                }
                List<String> values = requestParameters.get(field.getName());
                field.setAccessible(true);
                if (values.size() == 1) {
                    field.set(model, values.get(0));
                } else {
                    field.set(model, values);
                }
            }

            return model;
        }catch (Exception e){
            throw new ModelMappingException("fail to map parameters to model" + type.getName());
        }
    }
}
