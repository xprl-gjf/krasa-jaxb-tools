/*
 * Copyright 2021 Francesco Illuminati <fillumina@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class RunXJC2MojoTestHelper extends RunXJC2Mojo {

    public void setUp() throws Exception {
        super.testExecute();
    }

    public void testExecute() throws Exception {
        // override RunXJC2Mojo own method
    }
    
    public ArtifactTester element(String elementName) {
        final String filename = elementName + ".java";
        List<String> lines;
        try {
            lines = readFile(filename);
        } catch (IOException ex) {
            throw new AssertionError("error loading file " + filename, ex);
        }
        return new ArtifactTester(filename, lines);
    }

    private List<String> readFile(String filename) throws IOException {
        String fullPath = getGeneratedDirectory().getAbsolutePath() + File.separator +
                "generated" + File.separator + filename;
        Path path = Paths.get(fullPath);
        List<String> content = Files.readAllLines(path);
        return content;
    }

    public class ArtifactTester {

        private final String filename;
        private final List<String> lines;

        private ArtifactTester(String filename, List<String> lines) {
            this.filename = filename;
            this.lines = lines;
        }

        public AttributeTester attribute(String attributeName) {
            int line = getLineForAttribute(attributeName);
            int prevAttribute = prevAttributeLine(attributeName, line);
            List<String> annotationList = lines.subList(prevAttribute, line);
            return new AttributeTester(this, filename, attributeName, annotationList);
        }

        public RunXJC2MojoTestHelper end() {
            return RunXJC2MojoTestHelper.this;
        }

        private int getLineForAttribute(String attributeName) {
            for (int i = 0, l = lines.size(); i < l; i++) {
                String line = lines.get(i).trim();
                if (line.startsWith("protected ") && line.endsWith(attributeName + ";")) {
                    return i;
                }
            }
            throw new AssertionError(
                    "attribute " + attributeName + " not found in file " + filename);
        }

        private int prevAttributeLine(String attributeName, int attributeLine) {
            for (int i = attributeLine - 1; i >= 0; i--) {
                String line = lines.get(i).trim();
                if (line.isBlank() ||
                        line.startsWith("public ") ||
                        line.startsWith("protected ")) {
                    return i + 1;
                }
            }
            throw new AssertionError(
                    "cannot extract validatitions for " + attributeName + " in file " + filename);
        }
    }

    public static class AttributeTester {

        private final ArtifactTester parent;
        private final String filename;
        private final String attributeName;
        private final List<String> annotationList;

        public AttributeTester(ArtifactTester parent, String filename, String attributeName,
                List<String> annotationList) {
            this.parent = parent;
            this.filename = filename;
            this.attributeName = attributeName;
            this.annotationList = annotationList;
        }

        public ArtifactTester end() {
            return parent;
        }

        public AnnotationTester annotation(String annotation) {
            String line = annotationList.stream()
                    .filter(l -> l.trim().startsWith("@" + annotation))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError(
                            "annotation " + annotation + " of attribute " + attributeName +
                            " in " + filename + " not found "));
            return new AnnotationTester(this, line, annotation);
        }
    }

    public static class AnnotationTester {

        private final AttributeTester parent;
        private final String line;
        private final String annotation;
        private final Map<String, String> valueMap = new HashMap<>();

        public AnnotationTester(AttributeTester parent, String line, String annotationName) {
            this.parent = parent;
            this.line = line;
            this.annotation = annotationName;

            if (line.contains("=")) {
                int start = line.indexOf("(");
                String values = line.substring(start + 1, line.length() - 1);
                String[] pairs = values.split(",");
                for (String p : pairs) {
                    String[] kv = p.split("=");
                    valueMap.put(kv[0].trim(), kv[1].trim());
                }
            }
        }

        public AnnotationTester assertNoValues() {
            if (!valueMap.isEmpty()) {
                throw new AssertionError("annotation " + annotation +
                        " of attribute " + parent.attributeName +
                        " in " + parent.filename + " not empty: " + valueMap);
            }
            return this;
        }

        public AnnotationTester assertValue(String name, Object value) {
            String v = valueMap.get(name);
            if (v == null) {
                throw new AssertionError("annotation " + annotation +
                        " of attribute " + parent.attributeName +
                        " in " + parent.filename + " not found: " + valueMap);
            }
            if (!v.equals(value.toString())) {
                throw new AssertionError("annotation " + annotation +
                        " of attribute " + parent.attributeName +
                        " in " + parent.filename + " mismatched value, expected: " + value +
                        " found " + v);
            }
            return this;
        }

    }

}
