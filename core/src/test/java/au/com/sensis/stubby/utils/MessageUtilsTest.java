package au.com.sensis.stubby.utils;

import org.junit.Assert;
import org.junit.Test;

public class MessageUtilsTest {

    @Test
    public void testUpperCaseHeader() {
        Assert.assertEquals("Header", MessageUtils.upperCaseHeader("header"));
        Assert.assertEquals("Header-Name", MessageUtils.upperCaseHeader("header-name"));
        Assert.assertEquals("X-Header-Name", MessageUtils.upperCaseHeader("x-header-name"));
        Assert.assertEquals("-X-Header-Name", MessageUtils.upperCaseHeader("-x-header-name"));
    }

}
