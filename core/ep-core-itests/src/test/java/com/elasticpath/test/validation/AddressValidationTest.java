package com.elasticpath.test.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

import org.apache.commons.lang.RandomStringUtils;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.impl.AbstractAddressImpl;

/**
 * Validation tests for {@link AbstractAddressImpl}.
 */
public class AddressValidationTest extends AbstractValidationTest {

	private Address address;

	/** Test initialization. */
	@Before
	public void initialize() {
		address = new TestAddressImpl();
	}

	/** Test validation for last name property. */
	@Test
	public void testValidateLastName() {
		Set<ConstraintViolation<Address>> violations = getValidator().validate(address);
		assertViolationsContains("Unset value should fail", violations, "lastName");

		assertValidationViolation("Blanks not allowed", address, "lastName", RandomStringUtils.random(5, WHITESPACE));
		assertValidationSuccess(address, "lastName", "sharks");
		assertValidationSuccess("String is just long enough", address, "lastName", RandomStringUtils.random(Address.MEDIUM_MAXLENGTH));
		assertValidationViolation("String is just too long", address, "lastName", RandomStringUtils.random(Address.MEDIUM_MAXLENGTH + 1));
	}

	/** Test validation for first name property. */
	@Test
	public void testValidateFirstName() {
		Set<ConstraintViolation<Address>> violations = getValidator().validate(address);
		assertViolationsContains("unset value", violations, "firstName");

		assertValidationViolation("no blanks", address, "firstName", RandomStringUtils.random(5, WHITESPACE));
		assertValidationSuccess(address, "firstName", "lazors!");
		assertValidationSuccess("just long enough", address, "firstName", RandomStringUtils.random(Address.MEDIUM_MAXLENGTH));
		assertValidationViolation("too long", address, "firstName", RandomStringUtils.random(Address.MEDIUM_MAXLENGTH + 1));
	}

	/** Test validation for street1 property. */
	@Test
	public void testValidateStreet1() {
		Set<ConstraintViolation<Address>> violations = getValidator().validate(address);
		assertViolationsContains("unset value", violations, "street1");

		assertValidationViolation("no blanks", address, "street1", RandomStringUtils.random(5, WHITESPACE));
		assertValidationSuccess(address, "street1", "123 Fake Street");
		assertValidationSuccess("just long enough", address, "street1", RandomStringUtils.random(Address.LONG_MAXLENGTH));
		assertValidationViolation("too long", address, "street1", RandomStringUtils.random(Address.LONG_MAXLENGTH + 1));
	}

	/** Test validation for city property. */
	@Test
	public void testValidateCity() {
		Set<ConstraintViolation<Address>> violations = getValidator().validate(address);
		assertViolationsContains("unset value", violations, "city");

		assertValidationViolation("no blanks", address, "city", RandomStringUtils.random(5, WHITESPACE));
		assertValidationSuccess(address, "city", "chicago shish kabob");
		assertValidationSuccess("just long enough", address, "city", RandomStringUtils.random(Address.LONG_MAXLENGTH));
		assertValidationViolation("too long", address, "city", RandomStringUtils.random(Address.LONG_MAXLENGTH + 1));
	}

	/** Test validation for zip property. */
	@Test
	public void testValidateZip() {
		Set<ConstraintViolation<Address>> violations = getValidator().validate(address);
		assertViolationsContains("unset value", violations, "zipOrPostalCode");

		assertValidationViolation("no blanks", address, "zipOrPostalCode", RandomStringUtils.random(5, WHITESPACE));
		assertValidationSuccess(address, "zipOrPostalCode", "6669666-a");
		assertValidationSuccess("just long enough", address, "zipOrPostalCode", RandomStringUtils.random(Address.SHORT_MAXLENGTH));
		assertValidationViolation("too long", address, "zipOrPostalCode", RandomStringUtils.random(Address.SHORT_MAXLENGTH + 1));
	}

	/** Test validation for zip property. */
	@Test
	public void testValidateCountry() {
		Set<ConstraintViolation<Address>> violations = getValidator().validate(address);
		assertViolationsContains("unset value", violations, "country");

		assertValidationViolation("no blanks", address, "country", RandomStringUtils.random(5, WHITESPACE));
		assertValidationSuccess(address, "country", "Barnworld");
		assertValidationSuccess("just long enough", address, "country", RandomStringUtils.random(Address.LONG_MAXLENGTH));
		assertValidationViolation("too long", address, "country", RandomStringUtils.random(Address.LONG_MAXLENGTH + 1));
	}

	/** Test validation for fax property. */
	@Test
	public void testValidateFax() {
		Set<ConstraintViolation<Address>> violations = getValidator().validate(address);
		assertViolationsNotContains("unset value", violations, "faxNumber");

		assertValidationSuccess("just long enough", address, "faxNumber", RandomStringUtils.random(Address.SHORT_MAXLENGTH));
		assertValidationViolation("too long", address, "faxNumber", RandomStringUtils.random(Address.SHORT_MAXLENGTH + 1));
	}

	/** Test validation for street2 property. */
	@Test
	public void testValidateStreet2() {
		Set<ConstraintViolation<Address>> violations = getValidator().validate(address);
		assertViolationsNotContains("unset value", violations, "street2");

		assertValidationSuccess("just long enough", address, "street2", RandomStringUtils.random(Address.LONG_MAXLENGTH));
		assertValidationViolation("too long", address, "street2", RandomStringUtils.random(Address.LONG_MAXLENGTH + 1));
	}

	/** Test validation for sub country property. */
	@Test
	public void testValidateSubCountry() {
		Set<ConstraintViolation<Address>> violations = getValidator().validate(address);
		assertViolationsNotContains("unset value", violations, "subCountry");

		assertValidationSuccess("just long enough", address, "subCountry", RandomStringUtils.random(Address.LONG_MAXLENGTH));
		assertValidationViolation("too long", address, "subCountry", RandomStringUtils.random(Address.LONG_MAXLENGTH + 1));
	}

	/** Test validation of country/sub-countries. */
	@Test
	public void testValidateValidCountry() {
		Set<ConstraintViolation<Address>> violations = getValidator().validate(address);
		assertViolationsContains("unset value", violations, "country");
		assertViolationsNotContains("unset value", violations, "subCountry");

		address.setCountry("uS");
		violations = getValidator().validate(address);
		assertViolationsNotContains("valid country", violations, "country");
		assertViolationsContains("missing sub-country", violations, "subCountry");

		address.setSubCountry("invalid value");
		violations = getValidator().validate(address);
		assertViolationsNotContains("valid country", violations, "country");
		assertViolationsContains("invalid sub-country", violations, "subCountry");

		address.setSubCountry("Ca");
		violations = getValidator().validate(address);
		assertViolationsNotContains("valid country", violations, "country");
		assertViolationsNotContains("valid sub-country", violations, "subCountry");

		address.setCountry(null);
		violations = getValidator().validate(address);
		assertViolationsContains("not missing country", violations, "country");
		assertViolationsNotContains("shouldn't validate if country invalid", violations, "subCountry");

		address.setCountry("JP");
		violations = getValidator().validate(address);
		assertViolationsNotContains("valid country", violations, "country");
		assertViolationsContains("JP doesn't have subcountries, should be invalid", violations, "subCountry");

		address.setSubCountry(null);
		violations = getValidator().validate(address);
		assertViolationsNotContains("valid country", violations, "country");
		assertViolationsNotContains("we don't know about subcountries here, allow anything", violations, "subCountry");
	}

	private static class TestAddressImpl extends AbstractAddressImpl {
		private static final long serialVersionUID = 1L;

		@Override
		public long getUidPk() {
			return 0;
		}

		@Override
		public void setUidPk(final long uidPk) {
			// do nothing
		}
	}
}
