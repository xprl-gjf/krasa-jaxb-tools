package com.sun.tools.xjc.addon.krasa;

/**
 * Validation API 2.0 supports inclusive for @DecimalMin and @DecimalMax
 * 
 * @see https://github.com/krasa/krasa-jaxb-tools/issues/38
 * 
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class InvoiceTest extends RunXJC2MojoTestHelper {

    @Override
    public String getFolderName() {
        return "invoice";
    }

    @Override
    public String getNamespace() {
        return "a";
    }
    
    public void test() {
        element("Invoice")
                .attribute("amount")
                        .annotation("DecimalMin")
                            .assertParam("value", 0)
                            .assertParam("inclusive", false)
                            .end()
                        .annotation("NotNull").assertNoValues();
    }

}
