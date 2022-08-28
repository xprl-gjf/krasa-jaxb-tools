package com.sun.tools.xjc.addon.krasa;

/**
 * Validation API 2.0 supports inclusive for @DecimalMin and @DecimalMax
 *
 * @see https://github.com/krasa/krasa-jaxb-tools/issues/38
 *
 * @author Francesco Illuminati 
 */
public class InvoiceBase extends AnnotationsMojoTestHelper {

    public InvoiceBase(ValidationAnnotation annotation) {
        super("invoice", annotation);
    }

    public void test() {
        element("Invoice")
                .annotationCanonicalName(getPkg() + ".validation.constraints.DecimalMin")
                .annotationCanonicalName(getPkg() + ".validation.constraints.NotNull")
                .attribute("amount")
                        .annotation("DecimalMin")
                            .assertParam("value", 0)
                            .assertParam("inclusive", false)
                            .end()
                        .annotation("NotNull").assertNoValues();
    }

}
