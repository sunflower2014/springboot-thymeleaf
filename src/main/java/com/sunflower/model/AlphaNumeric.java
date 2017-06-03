package com.sunflower.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import com.sunflower.validator.AlphaNumericValidator;

@Documented
@Constraint(validatedBy = AlphaNumericValidator.class)
@Target({METHOD, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AlphaNumeric {
	String message() default "validator.invalidAlphaNumeric";
	String allowedPunc() default "#.-_()";
	Class<?>[] groups() default {};
	Class<? extends javax.validation.Payload>[] payload() default {};
}
