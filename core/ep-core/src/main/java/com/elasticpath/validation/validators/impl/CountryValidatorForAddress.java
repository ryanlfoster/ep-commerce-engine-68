package com.elasticpath.validation.validators.impl;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.validation.constraints.ValidCountry;

/**
 * {@link ValidCountry} validator for {@link Address}.
 * <p>
 * In addition to validating the country, we also validate the sub-country.
 * </p>
 */
public class CountryValidatorForAddress implements ConstraintValidator<ValidCountry, Address> {

	private Geography geography;

	@Override
	public void initialize(final ValidCountry constraintAnnotation) {
		// do nothing
	}

	@Override
	public boolean isValid(final Address value, final ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		Set<String> countryCodes = geography.getCountryCodes();
		if (countryCodes.isEmpty()) {
			return true;
		}

		if (StringUtils.isBlank(value.getCountry())) {
			if (StringUtils.isBlank(value.getSubCountry())) {
				return true;
			}

			buildCountryViolation(context);
			return false;
		}

		String valueCountryTrimmed = value.getCountry().trim();
		for (String countryCode : countryCodes) {
			// we need to iterate anyways to deal with case, so lets trim as well
			if (!countryCode.trim().equalsIgnoreCase(valueCountryTrimmed)) {
				continue;
			}

			Set<String> subCountryCodes = geography.getSubCountryCodes(countryCode);
			if (StringUtils.isBlank(value.getSubCountry())) {
				if (subCountryCodes.isEmpty()) {
					return true;
				}
				buildSubCountryViolation(context, "{com.elasticpath.validation.constraints.subCountry.missing}");
				return false;
			}

			String valueSubCountryTrimmed = value.getSubCountry().trim();
			for (String subCountryCode : subCountryCodes) {
				if (subCountryCode.trim().equalsIgnoreCase(valueSubCountryTrimmed)) {
					return true;
				}
			}

			buildSubCountryViolation(context, "{com.elasticpath.validation.constraints.validSubCountry}");
			return false;
		}

		buildCountryViolation(context);
		return false;
	}

	private void buildSubCountryViolation(final ConstraintValidatorContext context, final String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addNode("subCountry").addConstraintViolation();
	}

	private void buildCountryViolation(final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate("{com.elasticpath.validation.constraints.validCountry}").addNode("country")
				.addConstraintViolation();
	}

	public void setGeography(final Geography geography) {
		this.geography = geography;
	}
}
