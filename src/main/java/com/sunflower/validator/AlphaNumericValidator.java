package com.sunflower.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.sunflower.model.AlphaNumeric;

public class AlphaNumericValidator implements ConstraintValidator<AlphaNumeric, String> {
	AlphaNumeric alphaNumeric;

	@Override
	public void initialize(AlphaNumeric alphaNumeric) {
		this.alphaNumeric = alphaNumeric;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean isValid = isValid(value, alphaNumeric.allowedPunc());
		
		if (!isValid) {
			context.disableDefaultConstraintViolation();
			String errorMessage = alphaNumeric.message();
			context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
		}
		
		return isValid;
	}

	private boolean isValid(String value, String allowedPunc) {
		boolean isValid = true;
		
		if (value == null) {
			return true;
		}
		for (char ch: ((String)value).toCharArray()) {
			if (Character.isWhitespace(ch)) {
				continue;
			}
			if (Character.isLetter(ch)) {
				continue;
			}
			if (allowedPunc.indexOf(ch) >= 0) {
				continue;
			}
			isValid = false;
		}
		return isValid;
	}

}
