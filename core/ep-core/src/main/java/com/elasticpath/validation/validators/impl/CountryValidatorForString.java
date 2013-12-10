package com.elasticpath.validation.validators.impl;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.validation.constraints.ValidCountry;

/**
 * {@link ValidCountry} validator for strings.
 * <p>
 * This validator assumes the given string is purely a country and validates without case sensitivity.
 * </p>
 */
public class CountryValidatorForString implements ConstraintValidator<ValidCountry, String> {

	private Geography geography;

	@Override
	public void initialize(final ValidCountry constraintAnnotation) {
		// do nothing
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (StringUtils.isBlank(value)) {
			return true;
		}

		Set<String> countryCodes = geography.getCountryCodes();
		if (countryCodes.isEmpty()) {
			return true;
		}

		String trimmedValue = value.trim();
		for (String countryCode : countryCodes) {
			// we need to iterate anyways to deal with case, so lets trim as well
			if (countryCode.trim().equalsIgnoreCase(trimmedValue)) {
				return true;
			}
		}
		return false;
	}

	public void setGeography(final Geography geography) {
		this.geography = geography;
	}
}
