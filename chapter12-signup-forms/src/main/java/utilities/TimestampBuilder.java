package utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampBuilder {
    public static synchronized String getGuid() {
        return java.util.UUID.randomUUID().toString();
    }

    public static synchronized String buildUniqueTextByPrefix(String prefix) {
        return buildUniqueText(prefix, "", "");
    }

    public static synchronized String buildUniqueTextByPrefix(String prefix, String separator) {
        return buildUniqueText(prefix, "", separator);
    }

    public static synchronized String buildUniqueTextBySuffix(String suffix) {
        return buildUniqueText("", suffix, "");
    }

    public static synchronized String buildUniqueTextBySuffix(String suffix, String separator) {
        return buildUniqueText("", suffix, separator);
    }

    public static synchronized String buildUniqueText(String prefix, String suffix, String separator) {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmssSS");
        String datetime = ft.format(dNow);
        try {
            Thread.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return prefix + separator + datetime + separator + suffix;
    }
}
