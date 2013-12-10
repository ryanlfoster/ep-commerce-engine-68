package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.misc.Money;

/**
 * Tests the StandardMoneyFormatter class.
 */
@SuppressWarnings("PMD.AvoidDecimalLiteralsInBigDecimalConstructor")
public class StandardMoneyFormatterTest {

	private static final String ELEVEN_FIFTYFOUR_DOLLARS = "$11.54";
	private static final Currency CAD = Currency.getInstance("CAD");
	private static final Currency USD = Currency.getInstance("USD");
	private static final Currency EUR = Currency.getInstance("EUR");
	private static final String EUR_SIGN = "\u20ac";

	private StandardMoneyFormatter formatter;

	/**
	 * Set up tests.
	 */
	@Before
	public void setUp() {
		formatter = new StandardMoneyFormatter();
	}

	/**
	 * Test method for formatCurrency.
	 */
	@Test
	public void testFormatAmountAndSymbol() {
		final BigDecimal amount1 = new BigDecimal(11F);
		final BigDecimal amount2 = new BigDecimal(5.5F);

		final Money money1 = new MoneyImpl(amount1, CAD);
		final Money money2 = new MoneyImpl(amount2, USD);

		assertEquals("$11.00", formatter.formatCurrency(money1, Locale.CANADA));
		assertEquals("$5.50", formatter.formatCurrency(money2, Locale.CANADA));
	}

	/**
	 * Test method for formatAmount().
	 */
	@Test
	public void testFormatAmount() {
		final BigDecimal amount1 = new BigDecimal(11F);
		final BigDecimal amount2 = new BigDecimal(5.5F);

		final Money money1 = new MoneyImpl(amount1, CAD);
		final Money money2 = new MoneyImpl(amount2, USD);

		assertEquals("11.00", formatter.formatAmount(money1, Locale.CANADA));
		assertEquals("5.50", formatter.formatAmount(money2, Locale.CANADA));
	}

	/**
	 * Test method for 'formatCurrencySymbol()'.
	 */
	@Test
	public void testGetCurrencySymbol() {
		assertEquals("$", formatter.formatCurrencySymbol(CAD));
		assertEquals("$", formatter.formatCurrencySymbol(USD));
		assertEquals(EUR_SIGN, formatter.formatCurrencySymbol(EUR));
	}

	/**
	 * Test method for 'formatCurrency()' for different default locales.
	 */
	@Test
	public void testFormatAmountAndCurrencySymbolWithLocales() {
		final BigDecimal amount1 = new BigDecimal(11.5F);

		Locale.setDefault(Locale.CANADA);
		final MoneyImpl money1 = new MoneyImpl(amount1, CAD);
		final MoneyImpl money2 = new MoneyImpl(amount1, USD);
		final MoneyImpl money3 = new MoneyImpl(amount1, EUR);

		assertEquals("$11.50", formatter.formatCurrency(money1, Locale.CANADA));
		assertEquals("$11.50", formatter.formatCurrency(money2, Locale.US));
		assertEquals("11,50" + EUR_SIGN, formatter.formatCurrency(money3, Locale.FRANCE));
	}

	/**
	 * Test method for 'formatCurrency()' for Canadian locale.
	 */
	@Test
	public void testFormatCurrencyWithCanadaLocale() {
		final MoneyImpl money1 = new MoneyImpl(new BigDecimal(11.5356F), CAD);

		//the locale sent to the method has the country and language
		assertEquals(ELEVEN_FIFTYFOUR_DOLLARS, formatter.formatCurrency(money1, Locale.CANADA));
	}

	/**
	 * Test method for 'formatCurrency()' for US locale.
	 */
	@Test
	public void testFormatCurrencyWithUSLocale() {
		final MoneyImpl money1 = new MoneyImpl(new BigDecimal(11.5356F), USD);

		//the locale sent to the method has the country and language
		assertEquals(ELEVEN_FIFTYFOUR_DOLLARS, formatter.formatCurrency(money1, Locale.US));
	}

	/**
	 * Test method for 'formatCurrency()' for  German .
	 */
	@Test
	public void testFormatCurrencyWithGermanLocale() {
		final MoneyImpl money1 = new MoneyImpl(new BigDecimal(11.5356F), EUR);

		assertEquals("11,54" + EUR_SIGN, formatter.formatCurrency(money1, Locale.GERMANY));
		assertEquals(EUR_SIGN + "11.54", formatter.formatCurrency(money1, Locale.GERMAN));
	}

	/**
	 * Test correct formatting returned from 'formatCurrency()'
	 * when a locale is set using language only (no country) and the correct/expected currency is available.
	 * In this case fr (french) and Euros.
	 */
	@Test
	public void testFormatCurrencyForLocaleNoCountryWithCurrency() {
		final MoneyImpl money1 = new MoneyImpl(new BigDecimal(11.5356F), EUR);

		assertEquals(EUR_SIGN + "11.54", formatter.formatCurrency(money1, new Locale("fr")));
	}

	/**
	 * Test correct formatting returned from 'formatCurrency()'
	 * when a locale is set using language only (no country) and the wrong currency is set.
	 * This will mimic a missing currency and a fall back to the default.
	 * In this case fr (french) and $.
	 */
	@Test
	public void testFormatCurrencyForLocaleNoCountryWithoutCurrency() {
		final MoneyImpl money1 = new MoneyImpl(new BigDecimal(11.5356F), USD);

		assertEquals(ELEVEN_FIFTYFOUR_DOLLARS, formatter.formatCurrency(money1, new Locale("fr")));
	}

	/**
	 * Test correct formatting returned from 'formatCurrency()'
	 * when a locale is set using language and country and the correct currency is also set.
	 * In this case finnish/finland and euros.
	 */
	@Test
	public void testFormatCurrencyForLocaleWithCountryWithCurrency() {
		final MoneyImpl money1 = new MoneyImpl(new BigDecimal(11.5356F), EUR);

		assertEquals("11,54" + EUR_SIGN,  formatter.formatCurrency(money1, new Locale("fi", "FI")));
	}

	/**
	 * Test correct formatting returned from 'formatCurrency()'
	 * when a locale is set using language and country and the correct currency is also set.
	 * In this case french/France and dollars.
	 */
	@Test
	public void testFormatCurrencyForLocaleWithCountryWithoutCurrency() {
		final MoneyImpl money1 = new MoneyImpl(new BigDecimal(11.5356F), USD);

		assertEquals(ELEVEN_FIFTYFOUR_DOLLARS, formatter.formatCurrency(money1, new Locale("fr", "FR")));
	}

	/**
	 * Test that when we have only a certain currency available that even if the locale (from the request)
	 * is different we will show the price correctly.
	 * In this case currency is euros but locale is US. We should not show $ in display price.
	 */
	@Test
	public void testFormatCurrencyWhenLocaleDifferentFromCurrency() {
		// CHECKSTYLE:OFF Magic Numbers Goood!
		MoneyImpl money3 = new MoneyImpl(new BigDecimal(11.5356F), EUR);

		assertEquals(EUR_SIGN + "11.54", formatter.formatCurrency(money3, Locale.US));
		// CHECKSTYLE:ON
	}

	/**
	 * Test method for 'formatPercentage'.
	 */
	@Test
	public void testFormatPercentage() {
		// CHECKSTYLE:OFF Magic Numbers Goood!
		assertEquals("0%", formatter.formatPercentage(0, Locale.ENGLISH));
		assertEquals("50%", formatter.formatPercentage(0.50, Locale.ENGLISH));
		assertEquals("50 %", formatter.formatPercentage(0.50, Locale.FRANCE));
		// CHECKSTYLE:ON
	}
}
