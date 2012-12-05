package au.com.sensis.stubby.utils;

public class PatternUtils {

    private static final String[] REGEXP_CHARS = {"<", "(", "[", "{", "\\", "^", "-", "=", "$", "!", "|", "]", "}", ")", "?", "*", "+", ".", ">"};
    
    public static String quote(String pattern) { // escape special regexp chars
        String result = pattern;
        for (String ch : REGEXP_CHARS) {
            result = result.replace(ch, "\\" + ch);
        }
        return result;
    }
    
}
