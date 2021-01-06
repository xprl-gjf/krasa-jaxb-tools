package com.sun.tools.xjc.addon.krasa;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringsTest extends RunXJC2MojoTestHelper {

    @Override
    public String getFolderName() {
        return "strings";
    }

    @Override
    public String getNamespace() {
        return "a";
    }
    
    public void test() {
        element("Strings")
                .attribute("address")
                        .annotation("Size")
                                .assertParam("min", "21")
                                .assertParam("max", "43")
                        .end()
                        .annotation("NotNull").assertNoValues();
    }

}
