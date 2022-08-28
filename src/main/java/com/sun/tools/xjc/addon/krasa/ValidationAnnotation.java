package com.sun.tools.xjc.addon.krasa;

import java.lang.annotation.Annotation;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum ValidationAnnotation {

    JAVAX {
        @Override
        public Class<? extends Annotation> getNotNullClass() {
            return javax.validation.constraints.NotNull.class;
        }

        @Override
        public Class<? extends Annotation> getValidClass() {
            return javax.validation.Valid.class;
        }

        @Override
        public Class<? extends Annotation> getSizeClass() {
            return javax.validation.constraints.Size.class;
        }

        @Override
        public Class<? extends Annotation> getDigitsClass() {
            return javax.validation.constraints.Digits.class;
        }

        @Override
        public Class<? extends Annotation> getDecimalMinClass() {
            return javax.validation.constraints.DecimalMin.class;
        }

        @Override
        public Class<? extends Annotation> getDecimalMaxClass() {
            return javax.validation.constraints.DecimalMax.class;
        }

        @Override
        public Class<? extends Annotation> getPatternClass() {
            return javax.validation.constraints.Pattern.class;
        }

        @Override
        public Class<? extends Annotation> getPatternListClass() {
            return javax.validation.constraints.Pattern.List.class;
        }

    },
    JAKARTA {

        @Override
        public Class<? extends Annotation> getNotNullClass() {
            return jakarta.validation.constraints.NotNull.class;
        }

        @Override
        public Class<? extends Annotation> getValidClass() {
            return jakarta.validation.Valid.class;
        }

        @Override
        public Class<? extends Annotation> getSizeClass() {
            return jakarta.validation.constraints.Size.class;
        }

        @Override
        public Class<? extends Annotation> getDigitsClass() {
            return jakarta.validation.constraints.Digits.class;
        }

        @Override
        public Class<? extends Annotation> getDecimalMinClass() {
            return jakarta.validation.constraints.DecimalMin.class;
        }

        @Override
        public Class<? extends Annotation> getDecimalMaxClass() {
            return jakarta.validation.constraints.DecimalMax.class;
        }

        @Override
        public Class<? extends Annotation> getPatternClass() {
            return jakarta.validation.constraints.Pattern.class;
        }

        @Override
        public Class<? extends Annotation> getPatternListClass() {
            return jakarta.validation.constraints.Pattern.List.class;
        }

    };

    public abstract Class<? extends Annotation> getValidClass();

    public abstract Class<? extends Annotation> getNotNullClass();

    public abstract Class<? extends Annotation> getSizeClass();

    public abstract Class<? extends Annotation> getDigitsClass();

    public abstract Class<? extends Annotation> getDecimalMinClass();

    public abstract Class<? extends Annotation> getDecimalMaxClass();

    public abstract Class<? extends Annotation> getPatternClass();

    public abstract Class<? extends Annotation> getPatternListClass();

}
