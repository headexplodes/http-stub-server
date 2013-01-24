package au.com.sensis.stubby.utils;

import org.junit.Assert;
import org.junit.Test;

public class HttpMessageUtilsTest {

    @Test
    public void testUpperCaseHeader() {
        Assert.assertEquals("Header", HttpMessageUtils.upperCaseHeader("header"));
        Assert.assertEquals("Header-Name", HttpMessageUtils.upperCaseHeader("header-name"));
        Assert.assertEquals("X-Header-Name", HttpMessageUtils.upperCaseHeader("x-header-name"));
        Assert.assertEquals("-X-Header-Name", HttpMessageUtils.upperCaseHeader("-x-header-name"));
    }

}
