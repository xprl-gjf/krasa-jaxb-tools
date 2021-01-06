package com.sun.tools.xjc.addon.krasa;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ListsTest extends RunXJC2MojoTestHelper {

    @Override
    public String getFolderName() {
        return "lists";
    }

    @Override
    public String getNamespace() {
        return "a";
    }

    public void test() {
        element("Container")
                .attribute("listOfString")
                        .annotation("Size")
                            .assertParam("min", 0)
                            .assertParam("max", 5);
    }
    
}
