package com.sun.tools.xjc.addon.krasa;

public class NillableBase extends AnnotationsMojoTestHelper {

    public NillableBase(ValidationAnnotation annotation) {
        super("nillable", annotation);
    }

    public void test() {
        element("Nillable")
                .annotationCanonicalName(getPkg() + ".validation.constraints.NotNull")
                .attribute("notNullable")
                        .annotation("NotNull").assertNoValues()
                .end()
                .attribute("nullable")
                        .assertNoAnnotationsPresent();
    }

}
