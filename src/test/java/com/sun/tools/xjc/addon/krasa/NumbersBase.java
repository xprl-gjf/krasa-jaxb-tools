package com.sun.tools.xjc.addon.krasa;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Francesco Illuminati 
 */
public class NumbersBase extends AnnotationsMojoTestHelper {

    public NumbersBase(ValidationAnnotation annotation) {
        super("numbers", annotation);
    }

    public void test() {
        element("Numbers")
                .annotationCanonicalName(getPkg() + ".validation.constraints.NotNull")
                .annotationCanonicalName(getPkg() + ".validation.constraints.DecimalMax")
                .annotationCanonicalName(getPkg() + ".validation.constraints.DecimalMin")
                .annotationCanonicalName(getPkg() + ".validation.constraints.Digits")
                .attribute("decimalValue")
                        .assertClass(BigDecimal.class)
                        .annotation("NotNull").assertNoValues().end()

                .attribute("integerValue")
                        .assertClass(BigInteger.class)
                        .annotation("NotNull").assertNoValues().end()

                .attribute("negativeIntegerValue")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMax")
                                .assertParam("value", -1)
                                .assertParam("inclusive", true).end()
                        .annotation("NotNull").assertNoValues().end()

                .attribute("nonPositiveIntegerValue")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMax")
                                .assertParam("value", 0)
                                .assertParam("inclusive", true).end()
                        .annotation("NotNull").assertNoValues().end()

                .attribute("nonNegativeIntegerValue")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMin")
                                .assertParam("value", 0)
                                .assertParam("inclusive", true).end()
                        .annotation("NotNull").assertNoValues().end()

                .attribute("positiveIntegerValue")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMin")
                                .assertParam("value", 1)
                                .assertParam("inclusive", true).end()
                        .annotation("NotNull").assertNoValues().end()

                .attribute("valueDimension")
                        .assertClass(BigDecimal.class)
                        .annotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 2)
                        .end()
                        .annotation("NotNull").assertNoValues().end()

                .attribute("valuePositiveDimension")
                        .assertClass(BigDecimal.class)
                        .annotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 2)
                        .end()
                        .annotation("DecimalMin")
                                .assertParam("value", "0.00")
                                .assertParam("inclusive", true).end()
                        .annotation("NotNull").assertNoValues().end()

                .attribute("valuePositiveNonZeroDimension")
                        .assertClass(BigDecimal.class)
                        .annotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 2)
                        .end()
                        .annotation("DecimalMin")
                                .assertParam("value", "0.00")
                                .assertParam("inclusive", false).end()
                        .annotation("NotNull").assertNoValues().end()

                .attribute("valueFourPositiveNonZeroDecimal")
                        .assertClass(BigDecimal.class)
                        .annotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 4)
                        .end()
                        .annotation("DecimalMin")
                                .assertParam("value", "0.0000")
                                .assertParam("inclusive", false).end()
                        .annotation("NotNull").assertNoValues().end()

                .attribute("valueSixDigitDecimalFractionOne")
                        .assertClass(BigDecimal.class)
                        .annotation("Digits")
                            .assertParam("integer", 6)
                            .assertParam("fraction", 1)
                        .end()
                        .annotation("NotNull").assertNoValues().end()

                .attribute("valueFourDigitYear")
                        .assertClass(BigInteger.class)
                        .annotation("Digits")
                            .assertParam("integer", 4)
                            .assertParam("fraction", 0)
                        .end()
                        .annotation("DecimalMin")
                                .assertParam("value", "1")
                                .assertParam("inclusive", true).end()
                        .annotation("NotNull").assertNoValues().end();
    }

}
