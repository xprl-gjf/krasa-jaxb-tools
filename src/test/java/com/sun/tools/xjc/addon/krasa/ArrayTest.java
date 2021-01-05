package com.sun.tools.xjc.addon.krasa;

public class ArrayTest extends RunXJC2MojoTestHelper {

    @Override
    public String getFolderName() {
        return "array";
    }

    @Override
    public String getNamespace() {
        return "a";
    }

    public void test() {
        element("Array")
                .attribute("arrayOfBytes")
                        .annotation("Size").assertValue("max", 18).end()
                        .annotation("NotNull").assertNoValues();
    }

}
