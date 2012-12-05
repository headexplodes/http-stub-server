package au.com.sensis.stubby;

import java.util.regex.Pattern;

public class ParamPattern {
    
    public String name;
    public Pattern pattern;
    
    public ParamPattern(String name, Pattern pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return name + " =~ m/" + pattern.pattern() + "/";
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ParamPattern)
                && ((ParamPattern)obj).name.equals(name)
                && ((ParamPattern)obj).pattern.pattern().equals(pattern.pattern());
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + name.hashCode();
        result = 31 * result + pattern.pattern().hashCode();
        return result;
    }

}
