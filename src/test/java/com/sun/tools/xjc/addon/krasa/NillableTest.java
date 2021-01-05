package com.sun.tools.xjc.addon.krasa;

public class NillableTest extends RunXJC2MojoTestHelper {

    @Override
    public String getFolderName() {
        return "nillable";
    }

    @Override
    public String getNamespace() {
        return "a";
    }

    public void test() {
        element("Nillable")
                .attribute("notNullable")
                        .annotation("NotNull").assertNoValues()
                .end()
                .attribute("nullable")
                        .assertNoAnnotationsPresent();
    }

}
