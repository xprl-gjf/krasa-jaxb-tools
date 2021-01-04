package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JFieldVar;
import java.lang.reflect.Field;
import java.math.BigInteger;

/**
 * @author Vojtech Krasa
 */
class Utils {

    public static final String[] NUMBERS = new String[] {
        "BigDecimal", "BigInteger", "String", "byte", "short", "int", "long" 
    };

    public static int toInt(Object value) {
        if (value instanceof BigInteger) {
            // xjc
            return ((BigInteger) value).intValue();
        } else if (value instanceof Integer) {
            // cxf-codegen
            return (Integer) value;
        } else {
            throw new IllegalArgumentException(
                    "unknown type " + value.getClass());
        }
    }

    private static Field getSimpleField(String fieldName, Class<?> clazz) {
        Class<?> tmpClass = clazz;
        try {
            do {
                for (Field field : tmpClass.getDeclaredFields()) {
                    String candidateName = field.getName();
                    if (!candidateName.equals(fieldName)) {
                        continue;
                    }
                    field.setAccessible(true);
                    return field;
                }
                tmpClass = tmpClass.getSuperclass();
            } while (tmpClass != null);
        } catch (Exception e) {
            System.err.println("krasa-jaxb-tools - Field '" + fieldName +
                    "' not found on class " + clazz);
        }
        return null;
    }

    public static Object getField(String path, Object obj) {
        try {
            int idx = path.indexOf(".");
            if (idx != -1) {
                String field = path.substring(0, idx);
                Field declaredField = obj.getClass().getDeclaredField(field);
                declaredField.setAccessible(true);
                Object result = declaredField.get(obj);
                
                String nextField = path.substring(path.indexOf(".") + 1);
                return getField(nextField, result);
            } else {
                Field simpleField = getSimpleField(path, obj.getClass());
                simpleField.setAccessible(true);
                return simpleField.get(obj);
            }
        } catch (Exception e) {
            System.err.println("krasa-jaxb-tools - Field " + path +
                    " not found on " + obj.getClass().getName());
        }
        return null;
    }

    @Deprecated
    public static Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean equals(String value, long val) {
        return value.equals(BigInteger.valueOf(val).toString());
    }

    public static boolean isMin(String value) {
        return equals(value, -9223372036854775808L) || equals(value,
                -2147483648L);
    }

    public static boolean isMax(String value) {
        return equals(value, 9223372036854775807L) || equals(value, 2147483647L);
    }

    public static boolean isNumber(JFieldVar field) {
        for (String type : NUMBERS) {
            if (type.equalsIgnoreCase(field.type().name())) {
                return true;
            }
        }
        try {
            if (isNumber(Class.forName(field.type().fullName()))) {
                return true;
            }
        } catch (Exception e) {
            // whatever
        }
        return false;
    }

    protected static boolean isNumber(Class<?> aClass) {
        return Number.class.isAssignableFrom(aClass);
    }

    static boolean isCustomType(JFieldVar var) {
        return "JDirectClass".equals(var.type().getClass().getSimpleName());
    }
}
