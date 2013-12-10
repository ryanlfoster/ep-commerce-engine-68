package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.misc.Geography;

/**
 * Tests for {@link CountryValidatorForString}.
 */
public class CountryValidatorForStringTest {

	private static final String COUNTRY = "country";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private CountryValidatorForString validator;
	private Geography geography;

	/** Test initialization. */
	@Before
	public void initialize() {
		validator = new CountryValidatorForString();

		geography = context.mock(Geography.class);
		validator.setGeography(geography);
	}

	private void shouldGetCountries(final String... countryNames) {
		if (countryNames == null) {
			return;
		}

		final Set<String> countries = new HashSet<String>();
		for (String country : countryNames) {
			countries.add(country);
		}

		context.checking(new Expectations() {
			{
				allowing(geography).getCountryCodes();
				will(returnValue(countries));
			}
		});
	}

	/** If we don't know about any country, we should pass. */
	@Test
	public void testNoCountries() {
		shouldGetCountries();
		assertTrue("no countriess shouldn't give errors", validator.isValid(COUNTRY, null));
	}

	/** {@code null} values should be allowed. */
	@Test
	public void testNull() {
		assertTrue("nulls are allowed", validator.isValid(null, null));
	}

	/** Happy path when we find a valid country. */
	@Test
	public void testValidCountry() {
		shouldGetCountries(COUNTRY);
		assertTrue("happy path", validator.isValid(COUNTRY, null));
	}

	/** Case insensitive matches are still valid. */
	@Test
	public void testValidCountryCaseInsensitive() {
		String country = "Country with Some case 12";
		shouldGetCountries(country);
		assertTrue("case shouldn't matter", validator.isValid(country.toLowerCase(Locale.getDefault()), null));
		assertTrue("case shouldn't matter", validator.isValid(country.toUpperCase(Locale.getDefault()), null));
	}

	/** Leading/trailing whitespace on the country shouldn't inhibit country matching. */
	@Test
	public void testValidCountryTrimmed() {
		String whitespaceCountry = "  leading  value   		";
		shouldGetCountries(whitespaceCountry);

		String countryTrimmed = whitespaceCountry.trim();
		assertFalse("trimmed value shouldn't be the same", countryTrimmed.equals(whitespaceCountry));
		assertTrue("trimmed country should be found", validator.isValid(countryTrimmed, null));
	}

	/** Leading/trailing whitespace on the value shouldn't inhibit country matching. */
	@Test
	public void testValidCountryValueTrimmed() {
		String whitespaceCountry = "  leading  value   		";
		String countryTrimmed = whitespaceCountry.trim();
		shouldGetCountries(countryTrimmed);

		assertFalse("trimmed value shouldn't be the same", countryTrimmed.equals(whitespaceCountry));
		assertTrue("trimmed country should be found", validator.isValid(whitespaceCountry, null));
	}

	/** A blank string shouldn't be validated. */
	@Test
	public void testBlankString() {
		assertTrue("blank string is valid", validator.isValid("      ", null));
		assertTrue("empty string is valid", validator.isValid("", null));
	}

	/** Multiple countries shouldn't prevent matching if there is a match. */
	@Test
	public void testMultipleCountries() {
		String country1 = "country1";
		String country2 = "country2";
		String country3 = "country3";

		shouldGetCountries(country1, country2, country3);
		assertFalse("not a valid country", validator.isValid("some other one", null));

		assertTrue("picked the first one", validator.isValid(country1, null));
		assertTrue("picked the third one", validator.isValid(country3, null));
		assertTrue("picked the second one", validator.isValid(country2, null));
	}
}
