package com.elasticpath.domain.misc.impl;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.MoneyFormatter;

/**
 * Out of the box behavior for money formatting.
 * This class uses java.text API to perform money formatting of
 * currency symbol and prices based on standardizations.
 *
 * Display digits to the right of a decimal point or comma default to injected vals.
 *
 * @see java.text.NumberFormat
 * @see java.text.DecimalFormatSymbols
 */
public class StandardMoneyFormatter implements MoneyFormatter  {

	private static final Logger LOG = Logger.getLogger(StandardMoneyFormatter.class);

	private static final long serialVersionUID = -5237562692825280136L;
	private static final int DEFAULT_FRACTION_DIGITS = 2;

	private int minDisplayFractionDigits = -1;
	private int maxDisplayFractionDigits = -1;


	/**
	 * Formats the amount without currency symbol in the given Money object.  Convenience method equivalent to
	 * format(Money, false, locale).
	 *
	 * @param money the Money object to format
	 * @param locale the Locale
	 *
	 * @return A locale-specific string containing the amount of currency in the money object
	 */
	@Override
	public String formatAmount(final Money money, final Locale locale) {
		final boolean includeCurrencySymbol = false;
		return format(money, includeCurrencySymbol, locale);
	}

	/**
	 * Formats the amount and currency symbol in the given Money object.  Convenience method equivalent to
	 * format(Money, true, locale).
	 *
	 * @param money the Money object to format
	 * @param locale the Locale
	 *
	 * @return A locale-specific string containing the amount of currency in the money object
	 */
	@Override
	public String formatCurrency(final Money money, final Locale locale) {
		final boolean includeCurrencySymbol = true;
		return format(money, includeCurrencySymbol, locale);
	}

	/**
	 * Formats the given currency + amount according to the given locale.  Convenience method equivalent to
	 * format(MoneyFactory.createMoney(amount, currency), locale).
	 *
	 * @param currency currency.
	 * @param amount amount of money.
	 * @param locale the locale
	 * @return amount string
	 */
	@Override
	public String formatCurrency(final Currency currency, final BigDecimal amount, final Locale locale) {
		return formatCurrency(MoneyFactory.createMoney(amount, currency), locale);
	}

	/**
	 * Attempts to look up the locale of the currency against the internally represented set of
	 * supported locales. Will pass this to the currency if available.
	 * @param currency the currency to look up a symbol for
	 * @return the symbol associated with the current currency.
	 */
	@Override
	public String formatCurrencySymbol(final Currency currency) {
		Locale mappedLocale = DEFAULT_CURRENCY_LOCALE_MAP.get(currency);
		if (mappedLocale != null) {
			return currency.getSymbol(mappedLocale);
		}
		return currency.getSymbol();
	}

	/**
	 * Returns the symbol associated with the current currency.
	 * @param currency the currency to get the symbol for.
	 * @return the currency symbol
	 * @deprecated Use formatCurrencySymbol instead
	 */
	@Deprecated
	@Override
	public String getCurrencySymbol(final Currency currency) {
		return formatCurrencySymbol(currency);
	}

	/**
	 * Formats a percentage amount using the given locale.  Convenience method for NumberFormat.getPercentInstance().format()
	 * @param percentage the percentage amount
	 * @param locale the locale to format with
	 * @return the given percentage, formatted with the given locale
	 */
	@Override
	public String formatPercentage(final double percentage, final Locale locale) {
		return NumberFormat.getPercentInstance(locale).format(percentage);
	}

	/**
	 * Formats the passed in <code>Money</code> using the java.text.* api.
	 * If a full locale (country and language) and currency are supplied the api can make a correct
	 * job of formatting the currency display for the locale.
	 *
	 * @param money the <code>Money</code> to format.
	 * @param includeCurrencySymbol indicates whether to include the currency symbol.
	 * @param locale locale
	 * @return the correctly formatted <code>Money</code> with appropriate placed currency symbol (or not).
	 */
	@Override
	public String format(final Money money, final boolean includeCurrencySymbol, final Locale locale) {
		final NumberFormat numberFormat = getNumberFormat(includeCurrencySymbol, locale);

		if (locale == null || money.getCurrency() == null
				|| !StringUtils.equals(numberFormat.getCurrency().getCurrencyCode(), money.getCurrency().getCurrencyCode())) {
			LOG.debug("Unable to make a decision on currency formatting, due to null locale or differing info in formatter and actual currency. "
					+ "Using default formatting");
			return basicFormat(money, includeCurrencySymbol);
		}

		numberFormat.setMaximumFractionDigits(getMaxDisplayFractionDigits());
		numberFormat.setMinimumFractionDigits(getMinDisplayFractionDigits());

		final BigDecimal moneyAmountAsBigDecimal = money.getAmount().setScale(getMaxDisplayFractionDigits(),
				BigDecimal.ROUND_HALF_UP);

		String formattedValue = StringUtils.deleteWhitespace(numberFormat.format(moneyAmountAsBigDecimal));
		if (!includeCurrencySymbol) {
			return formattedValue;
		}

		final String currencySymbol = formatCurrencySymbol(money.getCurrency());
		// done this way, because the Locale set may not have a country val,
		// and you can't create an instance of Currency without a country
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
		final String currencySymbolFromNumberFormat = symbols.getCurrencySymbol();

		if (!currencySymbol.equals(currencySymbolFromNumberFormat)) {
			formattedValue = StringUtils.replaceOnce(formattedValue, currencySymbolFromNumberFormat, currencySymbol);
		}

		return formattedValue;
	}

	/**
	 * Returns a <code>NumberFormat</code> used for formatting currency based on locale.
	 * @param includeCurrencySymbol includeCurrencySymbol
	 * @param locale the locale to use
	 * @return a <code>NumberFormat</code>
	 * @see java.text.NumberFormat
	 */
	protected NumberFormat getNumberFormat(final boolean includeCurrencySymbol, final Locale locale) {
		if (locale == null) {
			if (includeCurrencySymbol) {
				return NumberFormat.getCurrencyInstance();
			}
			return NumberFormat.getInstance();
		}

		if (includeCurrencySymbol) {
			return NumberFormat.getCurrencyInstance(locale);
		}
		return NumberFormat.getInstance(locale);
	}

	/**
	 * The minimum number of fractional digits to display.
	 * @return the minimum number of fractional digits to display
	 */
	protected int getMinDisplayFractionDigits() {
		if (this.minDisplayFractionDigits == -1) {
			return DEFAULT_FRACTION_DIGITS;
		}
		return this.minDisplayFractionDigits;
	}

	/**
	 * The maximum number of fractional digits to display.
	 * @return the maximum number of fractional digits to display
	 */
	protected int getMaxDisplayFractionDigits() {
		if (this.maxDisplayFractionDigits == -1) {
			return DEFAULT_FRACTION_DIGITS;
		}
		return this.maxDisplayFractionDigits;
	}

	public void setMinDisplayFractionDigits(final int minDisplayFractionDigits) {
		this.minDisplayFractionDigits = minDisplayFractionDigits;
	}

	public void setMaxDisplayFractionDigits(final int maxDisplayFractionDigits) {
		this.maxDisplayFractionDigits = maxDisplayFractionDigits;
	}

	/**
	 * Fall back method when this class does not have enough reliable info to use the java.text api
	 * to format the currency symbol and price.
	 * The currency symbol will always be placed to the left of the price and a decimal point
	 * used to separate fractional prices.
	 *
	 * @param money the money to format
	 * @param includeCurrencySymbol indicates whether to include the currency symbol.
	 *
	 * @return a default of currency symbol + price (e.g $2.99) or just the price
	 */
	private String basicFormat(final Money money, final boolean includeCurrencySymbol) {
		final BigDecimal amountAsBigDecimal = money.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		if (includeCurrencySymbol) {
			return formatCurrencySymbol(money.getCurrency()).concat(amountAsBigDecimal.toString());
		}
		return amountAsBigDecimal.toString();
	}

	private static final Map<Currency, Locale> DEFAULT_CURRENCY_LOCALE_MAP = new HashMap<Currency, Locale>();
	static {
		for (Locale currLocale : Locale.getAvailableLocales()) {
			try {
				Currency currency = Currency.getInstance(currLocale);
				if (currency != null && DEFAULT_CURRENCY_LOCALE_MAP.get(currency) == null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Setting default locale "
								+ currLocale.toString() + " for currency "
								+ currency.getCurrencyCode() + " with symbol: "
								+ currency.getSymbol(currLocale));
					}
					DEFAULT_CURRENCY_LOCALE_MAP.put(currency, currLocale);
				}
			} catch (IllegalArgumentException illegalArgException) {
				// Country of the given locale is not a supported ISO 3166 country code; do nothing.
				LOG.debug("Country of the given locale is not a supported ISO 3166 country code");
			}
		}
	}

}
