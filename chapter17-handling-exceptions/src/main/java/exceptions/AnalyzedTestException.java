package exceptions;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

public class AnalyzedTestException extends Exception {
    public AnalyzedTestException(String errorMessage) {
        super(errorMessage);
    }

    public AnalyzedTestException(String errorMessage, Exception innerException) {
        super(formatExceptionMessage(errorMessage), innerException);
    }

    public AnalyzedTestException(String errorMessage, String url, Exception innerException) {
        super(formatExceptionMessage(errorMessage, url), innerException);
    }

    private static String formatExceptionMessage(String exceptionMessage) {
        var sb = new StringBuilder();
        sb.append("\n");
        sb.append("\n");

        for (int i = 0; i < 40; i++) {
            sb.append('#');
        }

        char[] charArray = new char[40];
        for (int i = 0; i < 40; i++) {
            charArray[i] = 'a';
        }
        String newString = new String(charArray);

        // commons-lang3
        sb.append(StringUtils.repeat('#', 40));
        sb.append("\n");
        sb.append("\n");
        sb.append(exceptionMessage);
        // Guava
        sb.append(Strings.repeat("#", 40));
        sb.append("\n");
        return sb.toString();
    }

    private static String formatExceptionMessage(String exceptionMessage, String url) {
        var sb = new StringBuilder();
        sb.append("\n");
        sb.append("\n");
        // commons-lang3
        sb.append(StringUtils.repeat('#', 40));
        sb.append("\n");
        sb.append("\n");
        sb.append(exceptionMessage);
        // Guava
        sb.append(Strings.repeat("#", 40));
        sb.append("\n");
        sb.append("URL: " + url);
        return sb.toString();
    }
}
