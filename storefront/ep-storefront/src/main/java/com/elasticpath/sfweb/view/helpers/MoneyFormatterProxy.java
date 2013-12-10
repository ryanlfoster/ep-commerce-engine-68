package com.elasticpath.sfweb.view.helpers;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.MoneyFormatter;

/**
 * This class loads a MoneyFormatter from the bean factory then proxies calls to it.  It's purpose is to allow inclusion of a configurable
 * MoneyFormatter instance in the velocity toolbox.
 */
public class MoneyFormatterProxy implements MoneyFormatter {
	private static final long serialVersionUID = -3083163082730150455L;

	private final AtomicReference<MoneyFormatter> formatter = new AtomicReference<MoneyFormatter>();

	/**
	 * Formats the amount in the given Money object.  For instance, will format 5.50 CDN -> 5.50
	 *
	 * @param money  the Money object to format
	 * @param locale the Locale
	 * @return A locale-specific string containing the amount of currency in the money object
	 */
	@Override
	public String formatAmount(final Money money, final Locale locale) {
		return getFormatter().formatAmount(money, locale);
	}

	/**
	 * Formats the amount in the given Money object.  For instance, will format 5.50 CDN -> $5.50
	 *
	 * @param money  the Money object to format
	 * @param locale the Locale
	 * @return A locale-specific string containing the amount of currency in the money object
	 */
	@Override
	public String formatCurrency(final Money money, final Locale locale) {
		return getFormatter().formatCurrency(money, locale);
	}

	/**
	 * Formats the given currency + amount according to the given locale.  Convenience method equivalent to
	 * format(MoneyFactory.createMoney(amount, currency), locale).
	 *
	 * @param currency currency.
	 * @param amount   amount of money.
	 * @param locale the locale
	 * @return amount string
	 */
	@Override
	public String formatCurrency(final Currency currency, final BigDecimal amount, final Locale locale) {
		return getFormatter().formatCurrency(currency, amount, locale);
	}

	/**
	 * Returns the symbol associated with the current currency.
	 *
	 * @param currency the currency to get the symbol for.
	 * @return the currency symbol
	 */
	@Override
	public String formatCurrencySymbol(final Currency currency) {
		return getFormatter().formatCurrencySymbol(currency);
	}

	/**
	 * Formats a percentage amount using the given locale.  Convenience method for NumberFormat.getPercentInstance().format()
	 *
	 * @param percentage the percentage amount
	 * @param locale     the locale to format with
	 * @return the given percentage, formatted with the given locale
	 */
	@Override
	public String formatPercentage(final double percentage, final Locale locale) {
		return getFormatter().formatPercentage(percentage, locale);
	}

	/**
	 * Formats the passed in <code>Money<code>.
	 *
	 * @param money                 money
	 * @param includeCurrencySymbol indicates whether to include the currency symbol.
	 * @param locale                the locale to format with
	 * @return formatted money
	 * @deprecated Replaced by formatAmount() or formatCurrency()
	 */
	@Deprecated
	@Override
	public String format(final Money money, final boolean includeCurrencySymbol, final Locale locale) {
		return getFormatter().format(money, includeCurrencySymbol, locale);
	}

	/**
	 * Returns the symbol associated with the current currency.
	 *
	 * @param currency the currency to get the symbol for.
	 * @return the currency symbol
	 * @deprecated Use formatCurrencySymbol instead
	 */
	@Deprecated
	@Override
	public String getCurrencySymbol(final Currency currency) {
		return getFormatter().getCurrencySymbol(currency);
	}

	/**
	 * Lazy Loads the MoneyFormatter instance.
	 * @return the MoneyFormatter instance.
	 */
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	protected MoneyFormatter getFormatter() {
		if (formatter.get() == null) {
			formatter.compareAndSet(null, (MoneyFormatter) ElasticPathImpl.getInstance().getBean(ContextIdNames.MONEY_FORMATTER));
		}

		return formatter.get();
	}
}
