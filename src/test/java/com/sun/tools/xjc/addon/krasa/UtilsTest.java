package com.sun.tools.xjc.addon.krasa;

import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vojtech Krasa
 */
public class UtilsTest {

    @Test
    public void testIsNumber() throws Exception {
        Assert.assertFalse(Utils.isNumber(String.class));
        Assert.assertFalse(Utils.isNumber(IllegalStateException.class));
        Assert.assertTrue(Utils.isNumber(BigDecimal.class));
    }
}
