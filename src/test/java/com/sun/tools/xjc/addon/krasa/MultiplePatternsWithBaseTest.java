package com.sun.tools.xjc.addon.krasa;

import java.util.List;
import java.util.stream.Collectors;

public class MultiplePatternsWithBaseTest extends RunXJC2MojoTestHelper {

    @Override
    public String getFolderName() {
        return "multiplePatternsWithBase";
    }

    @Override
    public String getNamespace() {
        return "a";
    }
    
    public void test() {
        List<String> annotations = element("MultiPatternWithBase")
                .getAnnotations("multiplePatternsWithBase");
        
        assertFalse(annotations.isEmpty());
        
        String text = annotations.stream()
                .map(s -> s.trim())
                .collect(Collectors.joining("\n"));
        
        String expected = "@Pattern.List({\n" +
            "@Pattern(regexp = \"[Y-Z]\"),\n" +
            "@Pattern(regexp = \"([0-9])|([A-B])\")\n" +
            "})";
        
        assertEquals(expected, text);
    }

}