package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;

public class ABaseTest extends RunXJC2MojoTestHelper {

    public void testAddressName() {
        element("AddressType")
                .attribute("name")
                .annotation("Size")
                .assertValue("max", 50);
    }
    
    public void testCountryCode() {
        element("AddressType")
                .attribute("countryCode")
                .annotation("NotNull")
                .assertNoValues();
    }
    
    @Override
    public File getGeneratedDirectory() {
        return new File(getBaseDir(), "target/generated-sources/abase");
    }

    @Override
    public File getSchemaDirectory() {
        return new File(getBaseDir(), "src/test/resources/abase");
    }

    @Override
    protected void configureMojo(AbstractXJC2Mojo mojo) {
        super.configureMojo(mojo);
        mojo.setProject(new MavenProject());
        mojo.setForceRegenerate(true);
        mojo.setExtension(true);
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<>(super.getArgs());
        args.add("-XJsr303Annotations");
        args.add("-XJsr303Annotations:targetNamespace=");
        // args.add("-XJsr303Annotations:targetNamespace=a");
        // args.add("-XJsr303Annotations:JSR_349=true");
        return args;
    }
}
