package com.elasticpath.domain.catalogview.impl;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.CatalogViewConstants;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.RangeFilterType;

/**
 * Default implementation of {@link PriceFilter}.
 */
public class PriceFilterImpl extends AbstractRangeFilterImpl<PriceFilter, BigDecimal> implements
		PriceFilter {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private static final String INVALID_FILTER_ID = "Invalid filter id: ";

	private Currency currency;

	/**
	 * Initialize the filter with the given set of properties.
	 * 
	 * @param properties a map of property name to value
	 */
	@Override
	public void initialize(final Map<String, Object> properties) {
		this.setCurrency((Currency) properties.get(CURRENCY_PROPERTY));
		BigDecimal lowerValue = (BigDecimal) properties.get(LOWER_VALUE_PROPERTY);
		BigDecimal upperValue = (BigDecimal) properties.get(UPPER_VALUE_PROPERTY);
		this.setLowerValue(lowerValue);
		this.setUpperValue(upperValue);
		this.setAlias((String) properties.get(PriceFilter.ALIAS_PROPERTY));
		this.setId(getSeoId());
	}

	/**
	 * Returns the display name of the filter with the given locale.
	 *
	 * @param locale the locale
	 * @return the display name of the filter with the given locale.
	 */
	@Override
	public String getDisplayName(final Locale locale) {

		String displayname = super.getDisplayName(locale);

		if (displayname != null) {
			return displayname;
		}
		
		if (getRangeType() == RangeFilterType.ALL && getCurrency() == null) {
			throw new EpDomainException("Filter not initialized");
		}

		String currencySymbol = this.currency.getSymbol(locale);
		if (this.getRangeType() == RangeFilterType.BETWEEN) {
			displayname = currencySymbol + getLowerValue() + " - " + getUpperValue();
		} else if (this.getRangeType() == RangeFilterType.LESS_THAN) {
			displayname = "< " + currencySymbol + getUpperValue();
		} else if (this.getRangeType() == RangeFilterType.MORE_THAN) {
			displayname = "> " + currencySymbol + getLowerValue();
		}

		return displayname;
	}

	/**
	 * Returns the currency of this price filter.
	 *
	 * @return the currency of this price filter.
	 */
	@Override
	public Currency getCurrency() {
		return this.currency;
	}

	/**
	 * Set the currency of this price filter.
	 *
	 * @param currency the currency.
	 */
	@Override
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * Returns the SEO url of the filter with the given locale. Currently the id will be used as the seo url.
	 *
	 * @param locale the locale
	 * @return the SEO url of the filter with the given locale.
	 */
	@Override
	public String getSeoName(final Locale locale) {
		final String seoName = super.getSeoName(locale);
		if (seoName != null) {
			return seoName;
		}
		final StringBuffer sbf = new StringBuffer();
		char seperator = '-';

		if (getRangeType() == RangeFilterType.ALL) {
			sbf.append(currency.toString());
		} else if (getRangeType() == RangeFilterType.BETWEEN) {
			sbf.append(convertTypeToString(RangeFilterType.BETWEEN)).append(seperator).append(currency.toString()).append(
					seperator).append(getLowerValue()).append(seperator).append(AND).append(seperator).append(
					getUpperValue());
		} else if (getRangeType() == RangeFilterType.LESS_THAN) {
			sbf.append(convertTypeToString(RangeFilterType.LESS_THAN)).append(seperator).append(currency.toString()).append(
					seperator).append(this.getUpperValue().toString());
		} else if (getRangeType() == RangeFilterType.MORE_THAN) {
			sbf.append(convertTypeToString(RangeFilterType.MORE_THAN)).append(seperator).append(currency.toString()).append(
					seperator).append(this.getLowerValue().toString());
		} else {
			// should never get here
			throw new EpDomainException("Unimplemented!");
		}

		return sbf.toString().toLowerCase();

	}

	@Override
	public String getSeoId() {
		StringBuilder seoString = new StringBuilder(SeoConstants.PRICE_FILTER_PREFIX);
		seoString.append(getCurrency().getCurrencyCode());
		seoString.append(getSeparatorInToken()); 
		seoString.append(getAlias());
		return seoString.toString();
	}
	
	/**
	 * Converts a range type to a string value.
	 *
	 * @param filterType the range filter type
	 * @return the string value of the filter type
	 */
	private String convertTypeToString(final RangeFilterType filterType) {
		// do this here so that the constants aren't in two different classes
		switch (filterType) {
		case ALL:
			return "all";
		case BETWEEN:
			return "between";
		case LESS_THAN:
			return "lessthan";
		case MORE_THAN:
			return "morethan";
		default:
			// should never get here
			return null;
		}
	}
	
	@Override
	public Map<String, Object> parseFilterString(final String filterIdStr) {
		if (filterIdStr.startsWith(CatalogViewConstants.PRICE_FILTER_PREFIX)) {
			return parseCatalogFilterString(filterIdStr);
		} else if (filterIdStr.startsWith(SeoConstants.PRICE_FILTER_PREFIX)) {
			return parseSeoFilterString(filterIdStr);
		} else {
			throw new EpCatalogViewRequestBindException(INVALID_FILTER_ID + filterIdStr);
		}
	}

	/**
	 * Parse the catalog formatted filter string.
	 * 
	 * @param filterIdStr a filter string in catalog format (e.g. price-USD-10-20)
	 * @return a map of property name to value
	 */
	protected Map<String, Object> parseCatalogFilterString(final String filterIdStr) {
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		final String idWithoutPrefix = filterIdStr.substring(filterIdStr.indexOf(CatalogViewConstants.PRICE_FILTER_PREFIX)
				+ CatalogViewConstants.PRICE_FILTER_PREFIX.length());
		
		final String[] tokens = idWithoutPrefix.split("\\-");
		final int lowerValuePosition = 2;
		final int upperValuePosition = 4;
		
		BigDecimal lowerPrice;
		BigDecimal upperPrice;
		try {
			tokenMap.put(CURRENCY_PROPERTY, Currency.getInstance(tokens[1]));
			
			if (tokens[0].equals(convertTypeToString(RangeFilterType.BETWEEN))) {
				lowerPrice = ConverterUtils.string2BigDecimal(tokens[lowerValuePosition]);
				upperPrice = ConverterUtils.string2BigDecimal(tokens[upperValuePosition]);
			} else if (tokens[0].equals(convertTypeToString(RangeFilterType.LESS_THAN))) {
				lowerPrice = null;
				upperPrice = ConverterUtils.string2BigDecimal(tokens[lowerValuePosition]);
			} else if (tokens[0].equals(convertTypeToString(RangeFilterType.MORE_THAN))) {
				lowerPrice = ConverterUtils.string2BigDecimal(tokens[lowerValuePosition]);
				upperPrice = null;
			} else {
				throw new EpCatalogViewRequestBindException(INVALID_FILTER_ID + filterIdStr);
			}
			
			// if lower value is greater than upper value
			if (lowerPrice != null && upperPrice != null && lowerPrice.compareTo(upperPrice) > 0) {
				throw new EpCatalogViewRequestBindException(INVALID_FILTER_ID + filterIdStr);
			}
			
		} catch (RuntimeException e) {
			throw new EpCatalogViewRequestBindException(INVALID_FILTER_ID + filterIdStr, e);
		}
		tokenMap.put(LOWER_VALUE_PROPERTY, lowerPrice);
		tokenMap.put(UPPER_VALUE_PROPERTY, upperPrice);
		
		String aliasString = generateAliasFromParsedFilterStr(lowerPrice, upperPrice);
		tokenMap.put(ALIAS_PROPERTY, aliasString);
		
		return tokenMap;
	}

	/**
	 * Parse the SEO formatted filter ID string.
	 * 
	 * @param filterIdStr a filter string in SEO format (e.g. prUSD_10_20)
	 * @return a map of property name to value
	 */
	protected Map<String, Object> parseSeoFilterString(final String filterIdStr) {
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		final String idWithoutPrefix = filterIdStr.substring(filterIdStr.indexOf(SeoConstants.PRICE_FILTER_PREFIX)
				+ SeoConstants.PRICE_FILTER_PREFIX.length());
		String[] tokens = idWithoutPrefix.split(getSeparatorInToken());
		if (tokens.length != RANGE_TOKENS) { // ignore illegal price filter token
			if (tokens.length == UPPER_VALUE_POSITION) {
				//if there is no upper limit, reconstructing array of tokens
				String[] newTokens = new String[]{tokens[0], tokens[1], null};
				tokens = newTokens;
			} else {
				throw new EpCatalogViewRequestBindException(INVALID_FILTER_ID + filterIdStr);
			}
		}
		try {
			tokenMap.put(CURRENCY_PROPERTY, Currency.getInstance(tokens[0]));
			
			BigDecimal lowerValue = null;
			BigDecimal upperValue = null;
			try {
				if (!StringUtils.isEmpty(tokens[LOWER_VALUE_POSITION])) {
					lowerValue = new BigDecimal(tokens[LOWER_VALUE_POSITION]);
				}
				if (!StringUtils.isEmpty(tokens[UPPER_VALUE_POSITION]) && !SeoConstants.MAX_VALUE.equals((tokens[UPPER_VALUE_POSITION]))) {
					upperValue = new BigDecimal(tokens[UPPER_VALUE_POSITION]);
				}
			} catch (NumberFormatException e) {
				throw new EpCatalogViewRequestBindException(INVALID_FILTER_ID + filterIdStr, e);
			}
			
			if (lowerValue != null && upperValue != null && lowerValue.compareTo(upperValue) > 0) {
				throw new EpCatalogViewRequestBindException(INVALID_FILTER_ID + filterIdStr);
			}
			tokenMap.put(LOWER_VALUE_PROPERTY, lowerValue);
			tokenMap.put(UPPER_VALUE_PROPERTY, upperValue);
			
			String aliasString = generateAliasFromParsedFilterStr(lowerValue, upperValue);
			tokenMap.put(ALIAS_PROPERTY, aliasString);
		} catch (RuntimeException e) {
			throw new EpCatalogViewRequestBindException(INVALID_FILTER_ID + filterIdStr, e);
		}
		return tokenMap;
	}
	
	/**
	 * Construct the alias for the priceFilter, when we are initialize a filter using a string.
	 *
	 * @param lowerValue - the low price
	 * @param upperValue - the high price
	 * @return the created string.
	 */
	private String generateAliasFromParsedFilterStr(final BigDecimal lowerValue, final BigDecimal upperValue) {
		final StringBuilder aliasString = new StringBuilder();
		if (lowerValue != null) {
			aliasString.append(lowerValue);
		}
		
		aliasString.append(getSeparatorInToken());
		
		if (upperValue != null) {	
			aliasString.append(upperValue);
		}
		return aliasString.toString();
	}
}
