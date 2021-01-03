package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.*;
import com.sun.xml.xsom.impl.AttributeUseImpl;
import com.sun.xml.xsom.impl.ElementDecl;
import com.sun.xml.xsom.impl.ParticleImpl;
import com.sun.xml.xsom.impl.SimpleTypeImpl;
import com.sun.xml.xsom.impl.parser.DelayedRef;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.xml.sax.ErrorHandler;

/**
 * big thanks to original author: cocorossello
 */
public class JaxbValidationsPlugins extends Plugin {

    private static final String PLUGIN_OPTION_NAME = "XJsr303Annotations";
    private static final String TARGET_NAMESPACE_PARAMETER =
            PLUGIN_OPTION_NAME + ":targetNamespace";
    private static final String JSR_349 = PLUGIN_OPTION_NAME + ":JSR_349";
    private static final String GENERATE_NOT_NULL_ANNOTATIONS =
            PLUGIN_OPTION_NAME + ":generateNotNullAnnotations";
    private static final String NOT_NULL_ANNOTATIONS_CUSTOM_MESSAGES =
            PLUGIN_OPTION_NAME + ":notNullAnnotationsCustomMessages";
    private static final String VERBOSE = PLUGIN_OPTION_NAME + ":verbose";
    private static final String GENERATE_JPA_ANNOTATIONS =
            PLUGIN_OPTION_NAME + ":jpa";
    static final String GENERATE_SERVICE_VALIDATION_ANNOTATIONS =
            PLUGIN_OPTION_NAME + ":generateServiceValidationAnnotations";
    private static final String NAMESPACE =
            "http://jaxb.dev.java.net/plugin/code-injector";

    private String targetNamespace = null;
    private boolean jsr349 = false;
    private boolean verbose = true;
    private boolean notNullAnnotations = true;
    private boolean notNullCustomMessages;
    private boolean notNullPrefixFieldName;
    private boolean notNullPrefixClassName;
    private String notNullCustomMessage = null;
    private boolean jpaAnnotations = false;
    private String serviceValidationAnnotations = null;

    @Override
    public String getOptionName() {
        return PLUGIN_OPTION_NAME;
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i)
            throws BadCommandLineException, IOException {

        ArgumentParser argParser = new ArgumentParser(args[i]);

        argParser.extractString(TARGET_NAMESPACE_PARAMETER)
                .ifPresent(v -> targetNamespace = v);

        argParser.extractBoolean(JSR_349)
                .ifPresent(v -> jsr349 = v);

        argParser.extractBoolean(GENERATE_NOT_NULL_ANNOTATIONS)
                .ifPresent(v -> notNullAnnotations = v);

        argParser.extractString(NOT_NULL_ANNOTATIONS_CUSTOM_MESSAGES)
                .ifPresent(value -> {
                    notNullCustomMessages = Boolean.parseBoolean(value);

                    if (!notNullCustomMessages) {
                        if (value.equalsIgnoreCase("classname")) {
                            notNullCustomMessages = true;
                            notNullPrefixFieldName = true;
                            notNullPrefixClassName = true;
                        } else if (value.equalsIgnoreCase("fieldname")) {
                            notNullCustomMessages = true;
                            notNullPrefixFieldName = true;
                        } else if (value.length() != 0 &&
                                !value.equalsIgnoreCase("false")) {
                            notNullCustomMessage = value;
                        }
                    }
                });

        argParser.extractBoolean(VERBOSE)
                .ifPresent(v -> verbose = v);

        argParser.extractBoolean(GENERATE_JPA_ANNOTATIONS)
                .ifPresent(v -> jpaAnnotations = v);

        argParser.extractString(GENERATE_SERVICE_VALIDATION_ANNOTATIONS)
                .ifPresent(value -> serviceValidationAnnotations = value);

        return argParser.getCounter();
    }

    @Override
    public List<String> getCustomizationURIs() {
        return Collections.singletonList(NAMESPACE);
    }

    @Override
    public boolean isCustomizationTagName(String nsUri, String localName) {
        return nsUri.equals(NAMESPACE) &&
                localName.equals("code");
    }

    @Override
    public void onActivated(Options opts) throws BadCommandLineException {
        super.onActivated(opts);
    }

    @Override
    public String getUsage() {
        return "  -" + PLUGIN_OPTION_NAME + "      :  " +
                "inject Bean validation annotations (JSR 303); " +
                "-" + PLUGIN_OPTION_NAME +
                ":targetNamespace=http://www.foo.com/bar  :      " +
                "additional settings for @Valid annotation";
    }

    @Override
    public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
//        try {
        for (ClassOutline co : model.getClasses()) {
            List<CPropertyInfo> properties = co.target.getProperties();
            for (CPropertyInfo property : properties) {
                if (property instanceof CElementPropertyInfo) {
                    processElement((CElementPropertyInfo) property,
                            co, model);
                } else if (property instanceof CAttributePropertyInfo) {
                    processAttribute((CAttributePropertyInfo) property,
                            co, model);
                } else if (property instanceof CValuePropertyInfo) {
                    processAttribute((CValuePropertyInfo) property,
                            co, model);
                }
            }
        }
        return true;
//        } catch (Exception e) {
//            log(e);
//            return false;
//        }
    }

    /**
     * XS:Element
     *
     * @param property
     */
    public void processElement(CElementPropertyInfo property,
            ClassOutline classOutline, Outline model) {

        XSComponent schemaComponent = property.getSchemaComponent();
        ParticleImpl particle = (ParticleImpl) schemaComponent;

        int maxOccurs = particle.getMaxOccurs().intValue();
        int minOccurs = particle.getMinOccurs().intValue();
        boolean nillable = ((ElementDecl) particle.getTerm()).isNillable();
        boolean required = property.isRequired();

        JFieldVar field = classOutline.implClass.fields()
                .get(propertyName(property));

        if ((minOccurs > 0 || required || !nillable) &&
                !hasAnnotation(field, NotNull.class)) {
            addNotNullValidation(classOutline, field);
        }

        if (!hasAnnotation(field, Size.class)) {
            if (maxOccurs != 0 || minOccurs != 0) {
                log("@Size (" + minOccurs + "," + maxOccurs + ") " +
                        propertyName(property) +
                        " added to class " + classOutline.implClass.name());

                final JAnnotationUse annotation = field.annotate(Size.class);

                if (minOccurs != -1) {
                    annotation.param("min", minOccurs);
                }
                if (maxOccurs != -1) {
                    annotation.param("max", maxOccurs);
                }
            }
        }

        XSTerm term = particle.getTerm();
        if (term instanceof ElementDecl) {
            processElement(property, classOutline, field,
                    (ElementDecl) term);

        } else if (term instanceof DelayedRef.Element) {

            XSElementDecl xsElementDecl = ((DelayedRef.Element) term).get();
            processElement(property, classOutline, field,
                    (ElementDecl) xsElementDecl);
        }

    }

    private void processElement(CElementPropertyInfo property,
            ClassOutline clase, JFieldVar var, ElementDecl element) {
        String propertyName = propertyName(property);
        String className = clase.implClass.name();
        XSType elementType = element.getType();

        validAnnotation(elementType, var, propertyName, className);

        if (elementType instanceof XSSimpleType) {
            processType((XSSimpleType) elementType,
                    var, propertyName, className);

        } else if (elementType.getBaseType() instanceof XSSimpleType) {
            processType((XSSimpleType) elementType.getBaseType(),
                    var, propertyName, className);
        }
    }

    private void addNotNullValidation(ClassOutline co, JFieldVar field) {
        if (!notNullAnnotations) {
            return;
        }

        final String className = co.implClass.name();
        log("@NotNull: " + field.name() + " added to class " + className);

        JAnnotationUse annotation = field.annotate(NotNull.class);
        if (notNullPrefixClassName) {
            String message = String.format("%s.%s {%s.message}",
                    className, field.name(),
                    NotNull.class.getName());
            annotation.param("message", message);

        } else if (notNullPrefixFieldName) {
            String message = String.format("%s {%s.message}",
                    field.name(), NotNull.class.getName());
            annotation.param("message", message);

        } else if (notNullCustomMessages) {
            String message = String.format("{%s.message}",
                    NotNull.class.getName());
            annotation.param("message", message);

        } else if (notNullCustomMessage != null) {
            String message = notNullCustomMessage
                    .replace("{ClassName}", className)
                    .replace("{FieldName}", field.name());
            annotation.param("message", message);
        }
    }

    private void validAnnotation(final XSType elementType, JFieldVar var,
            final String propertyName,
            final String className) {
        if ((targetNamespace == null || elementType.getTargetNamespace().
                startsWith(targetNamespace)) &&
                (elementType.isComplexType() || Utils.isCustomType(var))) {
            if (!hasAnnotation(var, Valid.class)) {
                log("@Valid: " + propertyName + " added to class " + className);
                var.annotate(Valid.class);
            }
        }
    }

    public void processType(XSSimpleType simpleType, JFieldVar field,
            String propertyName, String className) {
        if (!hasAnnotation(field, Size.class) && 
                isSizeAnnotationApplicable(field)) {
            Integer maxLength =
                    simpleType.getFacet("maxLength") == null ? null : Utils.
                    parseInt(simpleType.getFacet(
                            "maxLength").getValue().value);
            Integer minLength =
                    simpleType.getFacet("minLength") == null ? null : Utils.
                    parseInt(simpleType.getFacet(
                            "minLength").getValue().value);
            Integer length = simpleType.getFacet("length") == null ? null :
                    Utils.parseInt(simpleType.getFacet(
                            "length").getValue().value);

            if (maxLength != null && minLength != null) {
                log("@Size(" + minLength + "," + maxLength + "): " +
                        propertyName + " added to class " +
                        className);
                field.annotate(Size.class)
                        .param("min", minLength)
                        .param("max", maxLength);

            } else if (minLength != null) {
                log("@Size(" + minLength + ", null): " + propertyName +
                        " added to class " + className);
                field.annotate(Size.class).param("min", minLength);

            } else if (maxLength != null) {
                log("@Size(null, " + maxLength + "): " + propertyName +
                        " added to class " + className);
                field.annotate(Size.class).param("max", maxLength);

            } else if (length != null) {
                log("@Size(" + length + "," + length + "): " + propertyName +
                        " added to class " + className);
                field.annotate(Size.class).param("min", length).param("max",
                        length);
            }
        }
        if (jpaAnnotations && isSizeAnnotationApplicable(field)) {
            Integer maxLength =
                    simpleType.getFacet("maxLength") == null ? null : Utils.
                    parseInt(simpleType.getFacet(
                            "maxLength").getValue().value);
            if (maxLength != null) {
                log("@Column(null, " + maxLength + "): " + propertyName +
                        " added to class " + className);
                field.annotate(Column.class).param("length", maxLength);
            }
        }
        //TODO minExclusive=0, fractionDigits=2 wrong annotation https://github.com/krasa/krasa-jaxb-tools/issues/38 
        XSFacet maxInclusive = simpleType.getFacet("maxInclusive");
        if (maxInclusive != null && Utils.isNumber(field) && isValidValue(
                maxInclusive) &&
                !hasAnnotation(field, DecimalMax.class)) {
            log("@DecimalMax(" + maxInclusive.getValue().value + "): " +
                    propertyName +
                    " added to class " + className);
            field.annotate(DecimalMax.class).param("value", maxInclusive.
                    getValue().value);
        }
        XSFacet minInclusive = simpleType.getFacet("minInclusive");
        if (minInclusive != null && Utils.isNumber(field) && isValidValue(
                minInclusive) &&
                !hasAnnotation(field, DecimalMin.class)) {
            log("@DecimalMin(" + minInclusive.getValue().value + "): " +
                    propertyName +
                    " added to class " + className);
            field.annotate(DecimalMin.class).param("value", minInclusive.
                    getValue().value);
        }

        XSFacet maxExclusive = simpleType.getFacet("maxExclusive");
        if (maxExclusive != null && Utils.isNumber(field) && isValidValue(
                maxExclusive) &&
                !hasAnnotation(field, DecimalMax.class)) {
            JAnnotationUse annotate = field.annotate(DecimalMax.class);
            if (jsr349) {
                log("@DecimalMax(value = " + maxExclusive.getValue().value +
                        ", inclusive = false): " + propertyName +
                        " added to class " + className);
                annotate.param("value", maxExclusive.getValue().value);
                annotate.param("inclusive", false);
            } else {
                final BigInteger value = new BigInteger(
                        maxExclusive.getValue().value).subtract(BigInteger.ONE);
                log("@DecimalMax(" + value.toString() + "): " + propertyName +
                        " added to class " + className);
                annotate.param("value", value.toString());
            }
        }
        XSFacet minExclusive = simpleType.getFacet("minExclusive");
        if (minExclusive != null && Utils.isNumber(field) && isValidValue(
                minExclusive) &&
                !hasAnnotation(field, DecimalMin.class)) {
            JAnnotationUse annotate = field.annotate(DecimalMin.class);
            if (jsr349) {
                log("@DecimalMin(value = " + minExclusive.getValue().value +
                        ", inclusive = false): " + propertyName +
                        " added to class " + className);
                annotate.param("value", minExclusive.getValue().value);
                annotate.param("inclusive", false);
            } else {
                final BigInteger value = new BigInteger(
                        minExclusive.getValue().value).add(BigInteger.ONE);
                log("@DecimalMax(" + value.toString() + "): " + propertyName +
                        " added to class " + className);
                annotate.param("value", value.toString());
            }
        }

        if (simpleType.getFacet("totalDigits") != null && Utils.isNumber(field)) {
            Integer totalDigits = simpleType.getFacet("totalDigits") == null ?
                    null :
                    Utils.parseInt(simpleType.getFacet("totalDigits").
                            getValue().value);
            int fractionDigits = simpleType.getFacet("fractionDigits") == null ?
                    0 :
                    Utils.parseInt(simpleType.getFacet("fractionDigits").
                            getValue().value);
            if (!hasAnnotation(field, Digits.class)) {
                log("@Digits(" + totalDigits + "," + fractionDigits + "): " +
                        propertyName +
                        " added to class " + className);
                JAnnotationUse annox = field.annotate(Digits.class).param(
                        "integer", totalDigits);
                annox.param("fraction", fractionDigits);
            }
            if (jpaAnnotations) {
                field.annotate(Column.class).param("precision", totalDigits).
                        param("scale", fractionDigits);
            }
        }
        /**
         * <annox:annotate annox:class="javax.validation.constraints.Pattern"
         * message="Name can only contain capital letters, numbers and the symbols '-', '_', '/', ' '"
         * regexp="^[A-Z0-9_\s//-]*" />
         */
        List<XSFacet> patternList = simpleType.getFacets("pattern");
        if (patternList.size() > 1) { // More than one pattern
            if ("String".equals(field.type().name())) {
                if (simpleType.getBaseType() instanceof XSSimpleType &&
                        ((XSSimpleType) simpleType.getBaseType())
                                .getFacet("pattern") != null) {
                    log("@Pattern.List: " + propertyName + " added to class " +
                            className);
                    JAnnotationUse patternListAnnotation = field.annotate(
                            Pattern.List.class);
                    JAnnotationArrayMember listValue = patternListAnnotation.
                            paramArray("value");

                    String basePattern = ((XSSimpleType) simpleType.
                            getBaseType()).getFacet("pattern").getValue().value;
                    listValue.annotate(Pattern.class).param("regexp",
                            replaceXmlProprietals(basePattern));

                    log("@Pattern: " + propertyName + " added to class " +
                            className);
                    final JAnnotationUse patternAnnotation = listValue.annotate(
                            Pattern.class);
                    annotateMultiplePattern(patternList, patternAnnotation);
                } else {
                    log("@Pattern: " + propertyName + " added to class " +
                            className);
                    final JAnnotationUse patternAnnotation = field.annotate(
                            Pattern.class);
                    annotateMultiplePattern(patternList, patternAnnotation);
                }
            }
        } else if (simpleType.getFacet("pattern") != null) {
            String pattern = simpleType.getFacet("pattern").getValue().value;
            if ("String".equals(field.type().name())) {
                if (simpleType.getBaseType() instanceof XSSimpleType &&
                        ((XSSimpleType) simpleType.getBaseType())
                                .getFacet("pattern") != null) {
                    log("@Pattern.List: " + propertyName + " added to class " +
                            className);
                    JAnnotationUse patternListAnnotation = field.annotate(
                            Pattern.List.class);
                    JAnnotationArrayMember listValue = patternListAnnotation.
                            paramArray("value");
                    String basePattern = ((XSSimpleType) simpleType.
                            getBaseType()).getFacet("pattern").getValue().value;
                    listValue.annotate(Pattern.class).param("regexp",
                            replaceXmlProprietals(basePattern));
                    // cxf-codegen fix
                    if (!"\\c+".equals(pattern)) {
                        log("@Pattern(" + pattern + "): " + propertyName +
                                " added to class " + className);
                        if (!hasAnnotation(field, Pattern.class)) {
                            listValue.annotate(Pattern.class).param("regexp",
                                    replaceXmlProprietals(pattern));
                        }
                    }
                } else {
                    // cxf-codegen fix
                    if (!"\\c+".equals(pattern)) {
                        log("@Pattern(" + pattern + "): " + propertyName +
                                " added to class " + className);
                        if (!hasAnnotation(field, Pattern.class)) {
                            field.annotate(Pattern.class).param("regexp",
                                    replaceXmlProprietals(pattern));
                        }
                    }
                }
            }
        } else if ("String".equals(field.type().name())) {
            final List<XSFacet> enumerationList = simpleType.getFacets(
                    "enumeration");
            if (enumerationList.size() > 1) { // More than one pattern
                log("@Pattern: " + propertyName + " added to class " + className);
                final JAnnotationUse patternListAnnotation = field.annotate(
                        Pattern.class);
                annotateMultipleEnumerationPattern(enumerationList,
                        patternListAnnotation);
            } else if (simpleType.getFacet("enumeration") != null) {
                final String pattern = simpleType.getFacet("enumeration").
                        getValue().value;
                // cxf-codegen fix
                if (!"\\c+".equals(pattern)) {
                    log("@Pattern(" + pattern + "): " + propertyName +
                            " added to class " + className);
                    field.annotate(Pattern.class).param("regexp", escapeRegex(
                            replaceXmlProprietals(pattern)));
                }
            }
        }
    }

    private void annotateMultiplePattern(final List<XSFacet> patternList,
            final JAnnotationUse patternAnnotation) {
        StringBuilder sb = new StringBuilder();
        for (XSFacet xsFacet : patternList) {
            final String value = xsFacet.getValue().value;
            // cxf-codegen fix
            if (!"\\c+".equals(value)) {
                sb.append("(").append(replaceXmlProprietals(value)).append(")|");
            }
        }
        patternAnnotation.param("regexp", sb.substring(0, sb.length() - 1));
    }

    private void annotateMultipleEnumerationPattern(
            final List<XSFacet> patternList,
            final JAnnotationUse patternAnnotation) {
        StringBuilder sb = new StringBuilder();
        for (XSFacet xsFacet : patternList) {
            final String value = xsFacet.getValue().value;
            // cxf-codegen fix
            if (!"\\c+".equals(value)) {
                sb.append("(").append(escapeRegex(replaceXmlProprietals(value))).
                        append(")|");
            }
        }
        patternAnnotation.param("regexp", sb.substring(0, sb.length() - 1));
    }

    private String replaceXmlProprietals(String pattern) {
        return pattern.replace("\\i", "[_:A-Za-z]").replace("\\c",
                "[-._:A-Za-z0-9]");
    }

    /*
	 * \Q indicates begin of quoted regex text, \E indicates end of quoted regex text
     */
    private String escapeRegex(String pattern) {
        return java.util.regex.Pattern.quote(pattern);
    }

    private boolean isSizeAnnotationApplicable(JFieldVar field) {
        return field.type().name().equals("String") || field.type().isArray();
    }

    /*attribute from parent declaration*/
    private void processAttribute(CValuePropertyInfo property,
            ClassOutline clase, Outline model) {
        FieldOutline field = model.getField(property);
        String propertyName = property.getName(false);
        String className = clase.implClass.name();

        log("Attribute " + propertyName + " added to class " + className);
        XSComponent definition = property.getSchemaComponent();
        SimpleTypeImpl particle = (SimpleTypeImpl) definition;
        XSSimpleType type = particle.asSimpleType();
        JFieldVar var = clase.implClass.fields().get(propertyName);

//		if (particle.isRequired()) {
//			if (!hasAnnotation(var, NotNull.class)) {
//				if (notNullAnnotations) {
//					System.out.println("@NotNull: " + propertyName + " added to class " + className);
//					var.annotate(NotNull.class);
//				}
//			}
//		}
        validAnnotation(type, var, propertyName, className);
        processType(type, var, propertyName, className);
    }

    /**
     * XS:Attribute
     */
    public void processAttribute(CAttributePropertyInfo property,
            ClassOutline clase, Outline model) {
        FieldOutline field = model.getField(property);
        String propertyName = property.getName(false);
        String className = clase.implClass.name();

        log("Attribute " + propertyName + " added to class " + className);
        XSComponent definition = property.getSchemaComponent();
        AttributeUseImpl particle = (AttributeUseImpl) definition;
        XSSimpleType type = particle.getDecl().getType();

        JFieldVar var = clase.implClass.fields().get(propertyName);
        if (particle.isRequired()) {
            if (!hasAnnotation(var, NotNull.class)) {
                addNotNullValidation(clase, var);
            }
        }

        validAnnotation(type, var, propertyName, className);
        processType(type, var, propertyName, className);
    }

    protected boolean isValidValue(XSFacet facet) {
        String value = facet.getValue().value;
        // cxf-codegen puts max and min as value when there is not anything defined in wsdl.
        return value != null && !Utils.isMax(value) && !Utils.isMin(value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean hasAnnotation(JFieldVar var, Class annotationClass) {
        List<JAnnotationUse> list = (List<JAnnotationUse>) Utils.getField(
                "annotations", var);
        if (list != null) {
            for (JAnnotationUse annotationUse : list) {
                if (((Class) Utils.getField("clazz._class", annotationUse)).
                        getCanonicalName().equals(
                                annotationClass.getCanonicalName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String propertyName(CElementPropertyInfo property) {
        return property.getName(false);
    }

    private void log(Exception e) {
        e.printStackTrace();
    }

    private void log(String log) {
        if (verbose) {
            System.out.println(log);
        }
    }
}
