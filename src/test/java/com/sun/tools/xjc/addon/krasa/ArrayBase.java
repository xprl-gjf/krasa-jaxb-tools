package com.sun.tools.xjc.addon.krasa;

public abstract class ArrayBase extends AnnotationsMojoTestHelper {

    public ArrayBase(ValidationAnnotation annotation) {
        super("array", annotation);
    }

    public void test() {
        element("Array")
                .annotationCanonicalName(getPkg() + ".validation.constraints.Size")
                .annotationCanonicalName(getPkg() + ".validation.constraints.NotNull")
                .attribute("arrayOfBytes")
                        .annotation("Size").assertParam("max", 18).end()
                        .annotation("NotNull").assertNoValues();
    }

}
