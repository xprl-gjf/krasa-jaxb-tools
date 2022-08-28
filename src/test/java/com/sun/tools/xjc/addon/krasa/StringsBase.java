package com.sun.tools.xjc.addon.krasa;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringsBase extends AnnotationsMojoTestHelper {

    public StringsBase(ValidationAnnotation annotation) {
        super("strings", annotation);
    }

    public void test() {
        element("Strings")
                .annotationCanonicalName(getPkg() + ".validation.constraints.Size")
                .annotationCanonicalName(getPkg() + ".validation.constraints.NotNull")
                .attribute("address")
                        .annotation("Size")
                                .assertParam("min", "21")
                                .assertParam("max", "43")
                        .end()
                        .annotation("NotNull").assertNoValues();
    }

}
