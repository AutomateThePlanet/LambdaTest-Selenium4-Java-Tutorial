package utilities;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class ResourcesReader {
    @SneakyThrows
    public static String getFileAsString(Class<?> moduleClass, String fileName) {
        InputStream input = moduleClass.getResourceAsStream("/" + fileName);
        if (input == null) {
            input = InputStream.nullInputStream();
        }
        return IOUtils.toString(input, StandardCharsets.UTF_8);
    }
}