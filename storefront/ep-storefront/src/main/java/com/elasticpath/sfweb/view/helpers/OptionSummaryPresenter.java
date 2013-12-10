package com.elasticpath.sfweb.view.helpers;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.search.AdvancedSearchRequest;
import com.elasticpath.domain.misc.impl.BrandComparatorImpl;
import com.elasticpath.sfweb.viewbean.AdvancedSearchSummaryOptionsBean;
import com.elasticpath.sfweb.viewbean.impl.AdvancedSearchSummaryOptionsBeanImpl;

/**
 * Presents view information for the options summary for the search results.
 */
public class OptionSummaryPresenter {
	
	private static final String OR_WITH_SPACES = " / ";
	private static final String COMMA = ",";
	
	/**
	 * Gets the AdvancedSearchSummaryOptionsBean built from the parameters from the search request.
	 * @param searchRequest The search request
	 * @param locale The locale
	 * @param currency The currency
	 * @return AdvancedSearchSummaryOptionsBean The advanced search summary options bean with data prepopulated.
	 */
	public AdvancedSearchSummaryOptionsBean getSummaryOptionsBean(
			final AdvancedSearchRequest searchRequest, final Locale locale, final Currency currency) {
		
		List<Filter< ? >> advSearchFilters = searchRequest.getAdvancedSearchFilters();
		
		AdvancedSearchSummaryOptionsBean advancedSearchSummaryOptionsBean =
			new AdvancedSearchSummaryOptionsBeanImpl();
		
		advancedSearchSummaryOptionsBean.setBrandString(
				buildBrandOptionSummary(advSearchFilters, locale));
		advancedSearchSummaryOptionsBean.setPriceString(
				buildPriceOptionSummary(advSearchFilters, currency));
		advancedSearchSummaryOptionsBean.setAttributeMap(
				buildAttributeKeyValueMap(advSearchFilters, locale));
		
		return advancedSearchSummaryOptionsBean;
	}

	/**
	 * Builds a map of the attribute key and the localised display value of the attribute filter.
	 * @param filters The list of filters to parse
	 * @param locale The locale
	 * @return A map containing the attribute keys and its attribute filter display value
	 */
	protected Map<String, String> buildAttributeKeyValueMap(final List<Filter< ? >> filters,
			final Locale locale) {
		Map<String, String> attributeKeyValueMap = new HashMap<String, String>();
		
		for (Filter< ? > filter : filters) {
			if (filter instanceof AttributeFilter< ? >) {
				AttributeFilter< ? > attributeFilter = (AttributeFilter< ? >) filter;
				attributeKeyValueMap.put(attributeFilter.getAttributeKey(), attributeFilter.getDisplayName(locale));
			}
		}
		return attributeKeyValueMap;
	}

	/**
	 * Constructs the brand options summary string.
	 * @param filters The Filters to parse
	 * @param locale The locale to use
	 * @return A String containing the representation of the brands from the search request.
	 */
	protected String buildBrandOptionSummary(
		final List<Filter< ? >> filters, final Locale locale) {
		
		StringBuilder brandStringBuilder = new StringBuilder();
		Set<Brand> brands = new TreeSet<Brand>(new BrandComparatorImpl(locale));
		for (Filter< ? > filter : filters) {
			if (filter instanceof BrandFilter) {
				brands.addAll(((BrandFilter) filter).getBrands());
			}
		}
		for (Brand brand : brands) {
			brandStringBuilder.append(brand.getDisplayName(locale, false))
				.append(OR_WITH_SPACES);
		}
		
		//remove the last or, if any
		if (brandStringBuilder.length() > 0) {
			brandStringBuilder.delete(brandStringBuilder.length() - OR_WITH_SPACES.length(), brandStringBuilder.length());
			brandStringBuilder.append(COMMA);
		}
		
		return brandStringBuilder.toString();
	}
	
	/**
	 * Constructs the price options summary string.
	 * @param filters The Filters to parse
	 * @param currency The currency
	 * @return A String containing the representation of the low price and the high price from the search request.
	 */
	protected String buildPriceOptionSummary(final List<Filter< ? >> filters, final Currency currency) {
		StringBuilder priceStringBuilder = new StringBuilder();	
		for (Filter< ? > filter : filters) {
			if (filter instanceof PriceFilter) {
				String fromAmount = ((PriceFilter) filter).getLowerValue().toString();
				String toAmount = ((PriceFilter) filter).getUpperValue().toString();
				String currencyCode = currency.getCurrencyCode();
				
				priceStringBuilder.append(currencyCode)
					.append(fromAmount)
					.append("-")
					.append(currencyCode)
					.append(toAmount)
					.append(COMMA);
				
				//only expecting 1 price filter string in advanced search request
				break;
			}
		}
		return priceStringBuilder.toString();
	}
}