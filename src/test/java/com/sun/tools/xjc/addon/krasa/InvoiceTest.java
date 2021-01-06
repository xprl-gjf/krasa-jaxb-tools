package com.sun.tools.xjc.addon.krasa;

/**
 * Having an exclusiveMin with decimal is tricky. It could be possible to add a very
 * small amount to the minimum or just ignore it. BTW applying minimum to a decimal is
 * a very bad idea anyway. It is possible to assume the value is intended as integer
 * (as can be seen on many XSDs) and so ignore this issue at all.
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
                        .annotation("DecimalMin").assertValue("1")
                        .annotation("NotNull").assertNoValues();
    }

}
