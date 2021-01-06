package com.sun.tools.xjc.addon.krasa;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NumbersTest extends RunXJC2MojoTestHelper {

    @Override
    public String getFolderName() {
        return "numbers";
    }

    @Override
    public String getNamespace() {
        return "a";
    }

    public void test() {
        element("Numbers")
                .attribute("decimalValue")
                        .assertClass(BigDecimal.class)
                        .annotation("NotNull").assertNoValues().end()
                
                .attribute("integerValue")
                        .assertClass(BigInteger.class)
                        .annotation("NotNull").assertNoValues().end()
                
                .attribute("negativeIntegerValue")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMax").assertValue(-1)
                        .annotation("NotNull").assertNoValues().end()
                
                .attribute("nonPositiveIntegerValue")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMax").assertValue(0)
                        .annotation("NotNull").assertNoValues().end()
                
                .attribute("nonNegativeIntegerValue")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMin").assertValue(0)
                        .annotation("NotNull").assertNoValues().end()
                
                .attribute("positiveIntegerValue")
                        .assertClass(BigInteger.class)
                            .annotation("DecimalMin").assertValue(1)
                            .annotation("NotNull").assertNoValues().end()
                
                .attribute("valueDimension")
                        .assertClass(BigDecimal.class)
                        .annotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 2)
                        .end()
                        .annotation("NotNull").assertNoValues().end()
                
                .attribute("valuePositiveNonZeroDimensionIncl")
                        .assertClass(BigDecimal.class)
                        .annotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 2)
                        .end()
                        .annotation("DecimalMin").assertValue("0.00")
                        .annotation("NotNull").assertNoValues().end()
                
                .attribute("valuePositiveNonZeroDimensionExcl")
                        .assertClass(BigDecimal.class)
                        .annotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 2)
                        .end()
                        .annotation("DecimalMin").assertValue("1.00")
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
                        .annotation("DecimalMin").assertValue("1")
                        .annotation("NotNull").assertNoValues().end();
    }

}
