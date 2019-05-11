package Utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Created by Mufleh on 24/03/2019.
 */
public final class JSONUtil {

    public static final Supplier<ObjectMapper> OBJECT_MAPPER_SUPPLIER = () -> {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    };

    private static final ObjectMapper MAPPER;

    public static <T> String toJsonString(T sourceItem) throws JsonProcessingException {
        return MAPPER.writeValueAsString(sourceItem);
    }

    public static <T> T fromString(String sourceJson, Class<T> objectType) throws IOException {
        return MAPPER.readValue(sourceJson,objectType);
    }

    static {
        MAPPER = OBJECT_MAPPER_SUPPLIER.get();
    }

}
