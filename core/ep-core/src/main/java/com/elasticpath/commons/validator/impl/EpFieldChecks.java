package com.elasticpath.commons.validator.impl; //NOPMD

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.util.ValidatorUtils;
import org.springframework.validation.Errors;
import org.springmodules.validation.commons.FieldChecks;

import com.elasticpath.commons.util.impl.DateUtils;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.service.misc.TimeService;

/**
 * <code>EpFieldChecks</code> defines customized validation rules to be integrated into the springmodules validator.
 */
public class EpFieldChecks extends FieldChecks {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final String EMAIL_DELIMITER = ",";

	/**
	 * EpEmailValidator singleton.
	 */
	private static final EpEmailValidator EP_EMAIL_VALIDATOR = EpEmailValidator.getInstance();

	private static EpCreditCardValidator creditCardValidator;

	private static TimeService timeService;
	
	private static Geography geography;

	/**
	 * Validate if the two fields are same.
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	public static boolean validateTwoFields(final Object bean, final ValidatorAction validatorAction, final Field field, final Errors errors) {

		final String value = FieldChecks.extractValue(bean, field);
		final String sProperty2 = field.getVarValue("secondProperty");
		final String value2 = ValidatorUtils.getValueAsString(bean, sProperty2);


		if (!GenericValidator.isBlankOrNull(value) && !value.equals(value2)) {
			rejectValue(errors, field, validatorAction);
			return false;
		}

		return true;
	}

	/**
	 * Validate a phone number.
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	public static boolean validatePhoneNumber(final Object bean, final ValidatorAction validatorAction, final Field field, final Errors errors) {

		final String value = FieldChecks.extractValue(bean, field);
		final String pattern = field.getVarValue("pattern");
		final int minLength = Integer.parseInt(field.getVarValue("minlength"));

		//This checks that the pattern matches and that the number of *digits* is greater than minLength
		if (StringUtils.isNotBlank(value) && (!value.matches(pattern) || value.replaceAll("[^0-9]", "").trim().length() < minLength)) {
			rejectValue(errors, field, validatorAction);
			return false;
		}

		return true;
	}

	/**
	 * Validate a not null field, based on the specified condition.
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	public static boolean validateRequiredWhen(final Object bean, final ValidatorAction validatorAction, final Field field, final Errors errors) {

		final String value = FieldChecks.extractValue(bean, field);
		final String condition = field.getVarValue("requiredWhen");
		if (isConditionFulfilled(bean, condition) && StringUtils.isEmpty(value)) {
			rejectValue(errors, field, validatorAction);
			return false;
		}
		return true;
	}

	/**
	 * Checks if the given condition is true. If no condition is supplied
	 * the result is that the condition has been fulfilled.
	 */
	private static boolean isConditionFulfilled(final Object bean, final String condition) {
		if (condition == null) {
			return true;
		}
		final String[] conditionParts = condition.split("==");
		final String cValue = ValidatorUtils.getValueAsString(bean, conditionParts[0].trim());
		if (conditionParts.length == 2
				&& conditionParts[1].trim().equals(cValue)) {
			return true;
		}
		return false;
	}

	/**
	 * Validate the field based on the specified conditions.
	 *
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	public static boolean validateCondition(final Object bean, final ValidatorAction validatorAction, final Field field, final Errors errors) {
		final String condition1 = field.getVarValue("condition1");
		final String condition2 = field.getVarValue("condition2");
		final String[] conditionParts1 = condition1.split("==");
		final String[] conditionParts2 = condition2.split("==");
		final String cValue1 = ValidatorUtils.getValueAsString(bean, conditionParts1[0].trim());
		final String cValue2 = ValidatorUtils.getValueAsString(bean, conditionParts2[0].trim());

		if (conditionParts1.length == 2 && conditionParts1[1].trim().equals(cValue1)
				&& conditionParts2.length == 2 && conditionParts2[1].trim().equals(cValue2)) {
			rejectValue(errors, field, validatorAction);
			return false;
		}
		return true;
	}

	/**
	 * Email Validation uses the EpEmail validator.
	 *
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	public static boolean validateEmail(final Object bean, final ValidatorAction validatorAction, final Field field, final Errors errors) {
		boolean validStatus = true;

		final String value = FieldChecks.extractValue(bean, field).trim();

		if (EP_EMAIL_VALIDATOR.isValid(value)) {
			validStatus = true;
		} else {
			rejectValue(errors, field, validatorAction);
			validStatus = false;
		}

		return validStatus;
	}

	/**
	 * Email Validation - for validating multiple emails delimited by a delimiter character.
	 *
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	public static boolean validateDelimitedEmails(final Object bean, final ValidatorAction validatorAction, final Field field, final Errors errors) {
		final String value = FieldChecks.extractValue(bean, field).trim();

		final String[] emails = value.split(EMAIL_DELIMITER);

		for (int i = 0; i < emails.length; i++) {
			if (!EP_EMAIL_VALIDATOR.isValid((emails[i]).trim())) {
				rejectValue(errors, field, validatorAction);
				return false;
			}
		}
		return true;
	}

	/**
	 * Validate that the field does not start and/or end whitespaces.
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	public static boolean validateNoLeadingTrailingWhiteSpaces(final Object bean,
			final ValidatorAction validatorAction, final Field field, final Errors errors) {

		final String value = FieldChecks.extractValue(bean, field);
		if (value != null && (value.startsWith(" ") || value.endsWith(" "))) {
			rejectValue(errors, field, validatorAction);
			return false;
		}
		return true;
	}

	/**
	 * Validate that the field does not contain < or > characters.
	 * (These characters may be used for cross-site scripting security hacks)
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	public static boolean validateSecurityCheck(final Object bean,
			final ValidatorAction validatorAction, final Field field, final Errors errors) {

		final String value = FieldChecks.extractValue(bean, field);
		if (value != null && (value.indexOf('>') >= 0 || value.indexOf('<') >= 0)) {
			rejectValue(errors, field, validatorAction);
			return false;
		}
		return true;
	}

	/**
	 * Validate an integer used to specify a quantity.
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	public static boolean validateEpCartQuantity(final Object bean, final ValidatorAction validatorAction, final Field field, final Errors errors) {

		final String value = FieldChecks.extractValue(bean, field);

		Integer newValue = null;

		try {
			newValue = Integer.valueOf(value);
		} catch (final NumberFormatException e) {
			rejectValue(errors, field, validatorAction);
			return false;
		}

		if (newValue.intValue() < 1) {
			rejectValue(errors, field, validatorAction);
			return false;
		}

		return true;
	}

	/**
	 * Validate whether the subCountry is required.
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	public static boolean validateSubCountryRequired(final Object bean,
			final ValidatorAction validatorAction, final Field field, final Errors errors) {

		final String subCountryCode = FieldChecks.extractValue(bean, field);
		final String sProperty2 = field.getVarValue("countryProperty");
		final String countryCode = ValidatorUtils.getValueAsString(bean, sProperty2);

		if (!geography.getSubCountryCodes(countryCode).isEmpty() && GenericValidator.isBlankOrNull(subCountryCode)) {
			rejectValue(errors, field, validatorAction);
			return false;
		}
		return true;
	}

	/**
	 * Checks whether credit card is expired or not.
	 *
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result
	 */
	public static boolean validateCreditCardExpiration(final Object bean, final ValidatorAction validatorAction,
			final Field field, final Errors errors) {
		// check whether there is a 'requiredWhen' field
		final String condition = field.getVarValue("validateExpirationWhen");
		final String expiryMonth = FieldChecks.extractValue(bean, field);
		final String expiryYearProperty = field.getVarValue("expiryYearProperty");
		final String expiryYear = ValidatorUtils.getValueAsString(bean, expiryYearProperty);

		if (isConditionFulfilled(bean, condition)
				&& expiryMonth != null
				&& expiryYear != null
				&& DateUtils.isExpired(timeService.getCurrentTime(), expiryYear, expiryMonth)) {
			rejectValue(errors, field, validatorAction);
			return false;
		}
		return true;
	}

	/**
	 * Checks if the field is a valid credit card number.
	 *
	 * @param bean The bean validation is being performed on.
	 * @param validatorAction The <code>ValidatorAction</code> that is currently being
	 * performed.
	 * @param field The <code>Field</code> object associated with the current
	 * field being validated.
	 * @param errors The <code>Errors</code> object to add errors to if any
	 * validation errors occur.
	 * -param request
	 * Current request object.
	 * @return The credit card as a Long, a null if invalid, blank, or null.
	 */
	public static Long validateCreditCard(final Object bean, final ValidatorAction validatorAction,
			final Field field, final Errors errors) {

		Long result = null;
		final String value = extractValue(bean, field);

		if (!GenericValidator.isBlankOrNull(value)) {
			if (creditCardValidator.isCreditCardValid(value)) {
				result = Long.parseLong(value);
			} else {
				rejectValue(errors, field, validatorAction);
			}
		}

		return result;
	}

	/**
	 * Sets the credit card validator that will be used to
	 * validate credit cards on the store front.
	 *
	 * @param creditCardValidator the validator instance
	 */
	public void setCreditCardValidator(final EpCreditCardValidator creditCardValidator) {
		EpFieldChecks.creditCardValidator = creditCardValidator;
	}

	/**
	 * Sets the time service.
	 *
	 * @param timeService the time service to set
	 */
	public void setTimeService(final TimeService timeService) {
		EpFieldChecks.timeService = timeService;
	}
	
	/**
	 * @param geography The Geography to set.
	 */
	public static void setGeography(final Geography geography) {
		EpFieldChecks.geography = geography;
	}
	
	/**
	 * @return The Geography that was set.
	 */
	protected static Geography getGeography() {
		return geography;
	}
}
