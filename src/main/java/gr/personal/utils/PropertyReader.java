package gr.personal.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Nick Kanakis on 20/7/2017.
 */
public final class PropertyReader {

    private PropertyReader() {}

    public static String fetchValue(String file, String key) throws IOException {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(file);
            prop.load(input);
            return prop.getProperty(key);
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public static String fetchValue(String key) throws IOException {
        return fetchValue("src/main/resources/application.properties", key);
    }
}
