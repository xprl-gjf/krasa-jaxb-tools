package com.sun.tools.xjc.addon.krasa;

public class ChoicesBase extends AnnotationsMojoTestHelper {

    public ChoicesBase(ValidationAnnotation annotation) {
        super("choices", annotation);
    }

    public void test() {
        element("Choices")
                .attribute("tea")
                        .annotation("XmlElement")
                                .assertParam("name", "Tea")
                        .end()
                .end()
                .attribute("coffee")
                        .annotation("XmlElement")
                                .assertParam("name", "Coffee")
                        .end()
                .end();
    }

}
