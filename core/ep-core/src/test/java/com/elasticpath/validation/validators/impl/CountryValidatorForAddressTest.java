package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.misc.Geography;

/**
 * Tests for {@link CountryValidatorForAddress}.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class CountryValidatorForAddressTest {

	private static final String COUNTRY = "random country";
	private static final String SUBCOUNTRY = "random sub country";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private CountryValidatorForAddress validator;
	private Geography geography;
	private Address address;
	private Set<String> countrySet;
	private ConstraintValidatorContext validatorContext;

	/** Test initialization. */
	@Before
	public void initialize() {
		validator = new CountryValidatorForAddress();

		geography = context.mock(Geography.class);
		validator.setGeography(geography);
		address = context.mock(Address.class);

		validatorContext = context.mock(ConstraintValidatorContext.class);

		countrySet = new HashSet<String>();
		context.checking(new Expectations() {
			{
				allowing(geography).getCountryCodes();
				will(returnValue(countrySet));

				allowing(validatorContext);
			}
		});
	}

	private void shouldGetAddressCountry(final Address address, final String country, final String subCountry) {
		context.checking(new Expectations() {
			{
				allowing(address).getCountry();
				will(returnValue(country));
				allowing(address).getSubCountry();
				will(returnValue(subCountry));
			}
		});

	}

	private void shouldGetAddressCountry(final String country, final String subCountry) {
		shouldGetAddressCountry(address, country, subCountry);
	}

	private void shouldGetCountry(final String country, final String... subCountries) {
		countrySet.add(country);

		final Set<String> subCountryList = new HashSet<String>();
		if (subCountries != null) {
			for (String subCountry : subCountries) {
				subCountryList.add(subCountry);
			}
		}

		context.checking(new Expectations() {
			{
				allowing(geography).getSubCountryCodes(country);
				will(returnValue(subCountryList));
			}
		});
	}

	/** If we don't know about any country, we should pass. */
	@Test
	public void testNoCountries() {
		assertTrue("no countries shouldn't give errors", validator.isValid(address, validatorContext));
	}

	/** {@code null} values should be allowed. */
	@Test
	public void testNull() {
		assertTrue("nulls are allowed", validator.isValid(null, validatorContext));
	}

	/** A {@code null} country should be valid. */
	@Test
	public void testCountryNull() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry(null, null);
		assertTrue("nulls shouldn't fail country validation", validator.isValid(address, validatorContext));
	}

	/** A {@code null} country, but valid country should fail. */
	@Test
	public void testCountryNullValidSubCountry() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry(null, SUBCOUNTRY);
		assertFalse("sub-country was entered therefore country invalid", validator.isValid(address, validatorContext));
	}

	/** If a country doesn't have any sub-countries, but a value is provided, then we should fail validation. */
	@Test
	public void testValidCountryNoSubcountry() {
		shouldGetCountry(COUNTRY);
		shouldGetAddressCountry(COUNTRY, SUBCOUNTRY);
		assertFalse("sub-country isn't known", validator.isValid(address, validatorContext));
	}

	/** Happy path when we find a valid country and a valid sub-country. */
	@Test
	public void testValidCountryValidSubCountry() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry(COUNTRY, SUBCOUNTRY);
		assertTrue("happy path", validator.isValid(address, validatorContext));
	}

	/** If you missing a sub-country, then validation should fail. */
	@Test
	public void testValidCountryMissingSubCountry() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry(COUNTRY, null);
		assertFalse("missing sub country", validator.isValid(address, validatorContext));
	}

	/** Defining an invalid sub-country with a valid country should fail. */
	@Test
	public void testValidCountryInvalidSubCountry() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry(COUNTRY, "another sub-country that doesn't exist");
		assertFalse("invalid sub-country", validator.isValid(address, validatorContext));
	}

	/** Case insensitive matches are still valid. */
	@Test
	public void testValidCountryCaseInsensitiveUpper() {
		String country = "Country with Some case 12";
		shouldGetCountry(country);
		shouldGetAddressCountry(country.toUpperCase(Locale.getDefault()), null);
		assertTrue("case shouldn't matter", validator.isValid(address, validatorContext));
	}

	/** Case insensitive matches are still valid. */
	@Test
	public void testValidCountryCaseInsensitiveLower() {
		String country = "Country wIth Some case 12";
		shouldGetCountry(country);
		shouldGetAddressCountry(country.toLowerCase(Locale.getDefault()), null);
		assertTrue("case shouldn't matter", validator.isValid(address, validatorContext));
	}

	/** Sub country values are case insensitive. */
	@Test
	public void testSubcountryValidCaseInsensitiveUpper() {
		String subCountry = "Country with Some case 12";
		shouldGetCountry(COUNTRY, subCountry);
		shouldGetAddressCountry(COUNTRY, subCountry.toUpperCase(Locale.getDefault()));
		assertTrue("sub country case shouldn't matter", validator.isValid(address, validatorContext));
	}

	/** Sub country values are case insensitive. */
	@Test
	public void testSubCountryValidCaseInsensitiveLower() {
		String subCountry = "subCountry wIth Some case 12";
		shouldGetCountry(COUNTRY, subCountry);
		shouldGetAddressCountry(COUNTRY, subCountry.toLowerCase(Locale.getDefault()));
		assertTrue("sub country case shouldn't matter", validator.isValid(address, validatorContext));
	}

	/** Leading/trailing whitespace on the country shouldn't inhibit country matching. */
	@Test
	public void testValidCountryTrimmed() {
		String whitespaceCountry = "  leading  value   		";
		shouldGetCountry(whitespaceCountry);

		String countryTrimmed = whitespaceCountry.trim();
		shouldGetAddressCountry(countryTrimmed, null);
		assertFalse("trimmed value shouldn't be the same", countryTrimmed.equals(whitespaceCountry));
		assertTrue("trimmed country should be found", validator.isValid(address, validatorContext));
	}

	/** Leading/trailing whitespace on the value shouldn't inhibit country matching. */
	@Test
	public void testValidCountryValueTrimmed() {
		String whitespaceCountry = "  	leading  value   		";
		String countryTrimmed = whitespaceCountry.trim();
		shouldGetCountry(countryTrimmed);
		shouldGetAddressCountry(whitespaceCountry, null);

		assertFalse("trimmed value shouldn't be the same", countryTrimmed.equals(whitespaceCountry));
		assertTrue("trimmed country should be found", validator.isValid(address, null));
	}

	/** Leading/trailing whitespace on the subcountry shouldn't inhibit matching. */
	@Test
	public void testValidSubCountryTrimmed() {
		String whitespaceSubCountry = "  leading  value   		";
		shouldGetCountry(COUNTRY, whitespaceSubCountry);

		String subCountryTrimmed = whitespaceSubCountry.trim();
		shouldGetAddressCountry(COUNTRY, subCountryTrimmed);
		assertFalse("trimmed value the same?", subCountryTrimmed.equals(whitespaceSubCountry));
		assertTrue("trimmed sub-country should be found", validator.isValid(address, validatorContext));
	}

	/** Leading/tailing whitespace on the value of the subcountry shouldn't inhibit matching. */
	@Test
	public void testValidSubCountryValueTrimmed() {
		String whitespaceSubCountry = "  	leading  value   		";
		String subCountryTrimmed = whitespaceSubCountry.trim();
		shouldGetCountry(COUNTRY, subCountryTrimmed);
		shouldGetAddressCountry(COUNTRY, whitespaceSubCountry);

		assertFalse("trimmed value be the same!?", subCountryTrimmed.equals(whitespaceSubCountry));
		assertTrue("trimmed sub-country should be found", validator.isValid(address, null));
	}

	/** Multiple countries shouldn't prevent matching if there is a match. */
	@Test
	public void testMultipleCountries() {
		final String country1 = "country1";
		final String country2 = "country2";
		final String country2sub1 = "subcountry1";
		final String country2sub2 = "subcountry2";
		final String country3 = "country3";
		final String country3sub1 = "subcountry3";

		final Address address1 = context.mock(Address.class, "address-1");
		final Address address2 = context.mock(Address.class, "address-2");
		final Address address3 = context.mock(Address.class, "address-3");
		final Address address4 = context.mock(Address.class, "address-4");

		shouldGetAddressCountry(address1, country1, null);
		shouldGetAddressCountry(address2, country2, country2sub1);
		shouldGetAddressCountry(address3, country2, country2sub2);
		shouldGetAddressCountry(address4, country3, country3sub1);

		assertTrue(validator.isValid(address1, validatorContext));
		assertTrue(validator.isValid(address2, validatorContext));
		assertTrue(validator.isValid(address3, validatorContext));
		assertTrue(validator.isValid(address4, validatorContext));
	}

	/** Tests that the property path is set to country for country violations. */
	@Test
	public void testCountyErrorPropertyPath() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry("a different country", null);

		final ConstraintValidatorContext validatorContext = context.mock(ConstraintValidatorContext.class, "validationContext-1");
		context.checking(new Expectations() {
			{
				ConstraintViolationBuilder builder = context.mock(ConstraintViolationBuilder.class);

				allowing(validatorContext).disableDefaultConstraintViolation();
				allowing(validatorContext).buildConstraintViolationWithTemplate(with(Expectations.<String> anything()));
				will(returnValue(builder));

				allowing(builder).addNode("country");
			}
		});

		assertFalse("expecting a failure", validator.isValid(address, validatorContext));
	}

	/** Tests that the property path is set to country for country violations. */
	@Test
	public void testSubCountyErrorPropertyPath() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry(COUNTRY, "a different sub-country");

		final ConstraintValidatorContext validatorContext = context.mock(ConstraintValidatorContext.class, "validationContext-1");
		context.checking(new Expectations() {
			{
				ConstraintViolationBuilder builder = context.mock(ConstraintViolationBuilder.class);

				allowing(validatorContext).disableDefaultConstraintViolation();
				allowing(validatorContext).buildConstraintViolationWithTemplate(with(Expectations.<String> anything()));
				will(returnValue(builder));

				allowing(builder).addNode("subCountry");
			}
		});

		assertFalse("expecting a failure", validator.isValid(address, validatorContext));
	}

	/** Blank countries should be valid. */
	@Test
	public void testBlankCountryNoSubCountry() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry("      ", null);
		assertTrue("blank country allowed", validator.isValid(address, validatorContext));
	}

	/** Blank country with a subcountry is invalid. */
	@Test
	public void testBlankCountryWithSubCountry() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry("  ", SUBCOUNTRY);
		assertFalse("blank country only allowed if no subcountry", validator.isValid(address, validatorContext));
	}

	/** Blank country with a blank subcountry is valid. */
	@Test
	public void testBlankCountryWithBlankSubCountry() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry("  ", "      ");
		assertTrue("both blank are allowed", validator.isValid(address, validatorContext));
	}

	/** Blank subcountry where one is required should fail. */
	@Test
	public void testCountryBlankSubCountry() {
		shouldGetCountry(COUNTRY, SUBCOUNTRY);
		shouldGetAddressCountry(COUNTRY, "     ");
		assertFalse("missing sub-country", validator.isValid(address, validatorContext));
	}
}
