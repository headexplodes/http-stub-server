package au.com.sensis.stubby.utils;

import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("serial")
public class CaseInsensitiveMap<T> extends TreeMap<String, T> {

    public CaseInsensitiveMap() {
        super(String.CASE_INSENSITIVE_ORDER);
    }

    public CaseInsensitiveMap(Map<String,? extends T> map) {
        this();
        putAll(map);
    }
    
}
