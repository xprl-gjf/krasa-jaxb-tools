package com.sun.tools.xjc.addon.krasa;

public class ABaseTest extends RunXJC2MojoTestHelper {

    @Override
    public String getFolderName() {
        return "abase";
    }

    public void testNotNullAndSizeMax() {
        element("AddressType")
                .attribute("name")
                        .annotation("Size").assertValue("max", 50).end()
                        .annotation("NotNull").assertNoValues();
    }

    public void testNotNullAndSizeMinAndMax() {
        element("AddressType")
                .attribute("countryCode")
                        .annotation("NotNull").assertNoValues()
                        .annotation("Size")
                                .assertValue("min", 2)
                                .assertValue("max", 2);
    }

    public void testValidAndSizeMinMax() {
        element("AddressType")
                .attribute("phoneNumber")
                        .annotation("Valid").assertNoValues()
                        .annotation("Size")
                                .assertValue("min", 0)
                                .assertValue("max", 3);
    }
    
    public void testAnnotationNotPresent() {
        element("AddressType")
                .attribute("isDefaultOneClick")
                        .assertNoAnnotationsPresent();
    }
    
    public void testPattern() {
        element("EmailAddressType")
                .attribute("preferredFormat")
                        .annotation("Pattern")
                                .assertValue("regexp", 
                                        "(\\\\QTextOnly\\\\E)|(\\\\QHTML\\\\E)");
    }
}
