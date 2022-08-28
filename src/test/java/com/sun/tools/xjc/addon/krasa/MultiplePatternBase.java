package com.sun.tools.xjc.addon.krasa;

public class MultiplePatternBase extends AnnotationsMojoTestHelper {

    public MultiplePatternBase(ValidationAnnotation annotation) {
        super("multiplePatterns", annotation);
    }

    public void test() {
        element("Multipattern")
                .annotationCanonicalName(getPkg() + ".validation.constraints.Pattern")
                .attribute("multiplePatterns")
                    .annotation("Pattern")
                        .assertParam("regexp", "([0-9])|([A-B])");
    }

}
