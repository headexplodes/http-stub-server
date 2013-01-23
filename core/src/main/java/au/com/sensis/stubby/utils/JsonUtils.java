package au.com.sensis.stubby.utils;

import java.io.IOException;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class JsonUtils {
    
    public static ObjectMapper mapper() {
        return new ObjectMapper();
    }
    
    public static ObjectMapper defaultMapper() {
        ObjectMapper result = mapper();
        result.enable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        result.setSerializationInclusion(Inclusion.NON_NULL);
        return result;
    }
    
    public static ObjectWriter prettyWriter() {
        return defaultMapper().writerWithDefaultPrettyPrinter();
    }
    
    public static String prettyPrint(Object value) {
        try {
            return prettyWriter().writeValueAsString(value); 
        } catch (IOException e) {
            throw new RuntimeException("Error rendering JSON", e);
        }
    }

}
