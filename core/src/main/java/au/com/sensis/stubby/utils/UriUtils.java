package au.com.sensis.stubby.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class UriUtils {

    public static URI stripHost(URI uri) throws URISyntaxException {
        return new URI(
                null,
                null,
                null,
                0,
                uri.getPath(),
                uri.getQuery(),
                uri.getFragment());
    }

    public static String stripHost(String uri) {
        try {
            return stripHost(new URI(uri)).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
