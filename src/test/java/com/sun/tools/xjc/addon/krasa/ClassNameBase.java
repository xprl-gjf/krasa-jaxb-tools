package com.sun.tools.xjc.addon.krasa;

import java.util.ArrayList;
import java.util.List;

public class ClassNameBase extends RunXJC2MojoTestHelper {

    private final String expectedClassName;

    public ClassNameBase(ValidationAnnotation validation, String expectedClassName)
    {
        super(validation);
        this.expectedClassName = expectedClassName;
    }

    @Override
    public String getFolderName() {
        return "notNull";
    }

    @Override
    public String getNamespace() {
        return "a";
    }

    public void test() {
        element("NotNullType")
            .attribute("notNullString")
                .annotation("NotNull")
                    .assertParam("message", "NotNullType.notNullString {" + this.expectedClassName + ".message}");
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<String>(super.getArgs());
        args.add("-XJsr303Annotations");
        args.add("-XJsr303Annotations:notNullAnnotationsCustomMessages=ClassName");
        args.add("-XJsr303Annotations:JSR_349=true");
        return args;
    }
}
