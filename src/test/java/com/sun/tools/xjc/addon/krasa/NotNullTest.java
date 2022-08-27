package com.sun.tools.xjc.addon.krasa;

import java.util.ArrayList;
import java.util.List;

public class NotNullTest extends RunXJC2MojoTestHelper {

    @Override
    public String getFolderName() {
        return "notNull";
    }

    @Override
    public String getNamespace() {
        return "a";
    }

    public void test() {
        element("NotNull")
                .attribute("notNullString")
                        .annotation("jakarta.validation.constraints.NotNull")
                            .assertParam("message", "NotNull.notNullString " +
                                    "{jakarta.validation.constraints.NotNull.message}");
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<String>(super.getArgs());
        args.add("-XJsr303Annotations");
        args.add("-XJsr303Annotations:notNullAnnotationsCustomMessages=ClassName");
        // args.add("-XJsr303Annotations:JSR_349=true");
        return args;
    }
}
