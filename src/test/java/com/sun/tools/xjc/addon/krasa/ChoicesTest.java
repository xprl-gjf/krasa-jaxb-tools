package com.sun.tools.xjc.addon.krasa;

public class ChoicesTest extends RunXJC2MojoTestHelper {

    @Override
    public String getFolderName() {
        return "choices";
    }

    @Override
    public String getNamespace() {
        return "a";
    }

    public void test() {
        element("Choices")
                .attribute("tea")
                        .annotation("XmlElement")
                                .assertValue("name", "Tea")
                        .end()
                .end()
                .attribute("coffee")
                        .annotation("XmlElement")
                                .assertValue("name", "Coffee")
                        .end()
                .end();
    }

}
