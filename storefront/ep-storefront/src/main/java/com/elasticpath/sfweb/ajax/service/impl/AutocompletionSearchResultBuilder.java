package com.elasticpath.sfweb.ajax.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.impl.StoreThemeMessageSource;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.SimplePrice;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.misc.MoneyFormatter;
import com.elasticpath.sfweb.ajax.bean.AutocompletionSearchResult;
import com.elasticpath.sfweb.ajax.bean.impl.AutocompletionSearchResultImpl;

/**
 * 
 * Build result list for autocompletion search.
 *
 */
final class AutocompletionSearchResultBuilder {
	
	private static final String PROPERTY_BASE = "productTemplate.recurringPrice.";

	private AutocompletionSearchResultBuilder() {
		// prevent instancation
	}
	
	/**
	 * Builds a list of autocompletion search results.
	 *
	 * @param productsList list of products
	 * @param prices optional map of product's prices
	 * @param configuration configuration for how to display result
	 * @param locale locale
	 * @param seoUrlBuilder mandatory if  <code>seoEnabled</code> is true, otherwise optional
	 * @param baseUrl store front context url 
	 * @param messageSource the message source for finding recurring price label
	 * @param moneyFormatter money formatter
	 * @return list of {@link AutocompletionSearchResult}
	 */
	// CHECKSTYLE:OFF >8 parameters
	static List<AutocompletionSearchResult> build(
			final List<Product> productsList, 
			final Map<String, Price> prices,
			final AutocompletionSearchResultConfiguration configuration,
			final Locale locale,
			final SeoUrlBuilder seoUrlBuilder,
			final String baseUrl,
			final StoreThemeMessageSource messageSource,
			final MoneyFormatter moneyFormatter
	// CHECKSTYLE:ON
			) {
		
		final List<AutocompletionSearchResult> resultList = new ArrayList<AutocompletionSearchResult>();
		
		for (Product product : productsList) {			
			final LocaleDependantFields ldf = product.getLocaleDependantFields(locale);			
			resultList.add(
					new AutocompletionSearchResultImpl(
							StringEscapeUtils.escapeHtml(product.getCode()),
							StringEscapeUtils.escapeHtml(getLimitedString(ldf.getDisplayName(), configuration.getProductNameMaxLength())),
							StringEscapeUtils.escapeHtml(getLimitedString(ldf.getDescription(), configuration.getProductDescriptionMaxLength())),
							getImage(product.getImage(), configuration.isShowThumbnail()),
							getUrl(product, baseUrl, seoUrlBuilder, locale, configuration.isSeoEnabled()),
							getPrice(product, prices, messageSource, moneyFormatter, locale)
							)					
					);			
		}		
		return resultList;
		
	}
	
	/**
	 * Get the product link url.
	 * @param product product
	 * @param baseUrl store front context url 
	 * @param seoUrlBuilder seo builder
	 * @param locale locale
	 * @param seoEnabled true if seo enabled
	 * @return product url, depends on seo settings 
	 */
	private static String getUrl(final Product product, 
			final String baseUrl, 
			final SeoUrlBuilder seoUrlBuilder, 
			final Locale locale, 
			final boolean seoEnabled) {
		StringBuilder builder = new StringBuilder(baseUrl);
		builder.append('/');
		if (seoEnabled) {
			builder.append(seoUrlBuilder.productSeoUrl(product, locale));
		} else { 
			builder.append(WebConstants.PRODUCT_VIEW_PAGE)
				.append('?')
				.append(WebConstants.REQUEST_PID)
				.append('=')
				.append(product.getGuid()); 
		}
		return builder.toString();		
	}	
	
	/**
	 * Get the product image according to settings. 
	 * @param image the image filename
	 * @param showThumbnail true if need to show
	 * @return image filename or null
	 */
	private static String getImage(final String image, final boolean showThumbnail) {
		if (showThumbnail) {
			return image;
		}
		return null;		
	}

	/**
	 * Get price by given product and his price map.
	 *
	 * @param product given product
	 * @param prices given price map
	 * @param messageSource to lookup the recurring price string
	 * @param moneyFormatter given money formatter
	 * @param locale the locale to display the recurring price in  @return money and symbol string, if price present , otherwise null
	 *
	 * @return The product's price, formatted as a stringi
	 */
	static String getPrice(final Product product, final Map<String, Price> prices,
						   final StoreThemeMessageSource messageSource, final MoneyFormatter moneyFormatter, final Locale locale) {

		if (MapUtils.isNotEmpty(prices) && prices.containsKey(product.getCode())) {			
			Price price = prices.get(product.getCode());
			if (price != null) {
				PricingScheme pricingScheme = price.getPricingScheme();
				PriceSchedule scheduleForLowestPrice = pricingScheme.getScheduleForLowestPrice();
				SimplePrice lowestPrice = pricingScheme.getSimplePriceForSchedule(scheduleForLowestPrice);
				String result = moneyFormatter.formatCurrency(lowestPrice.getLowestPrice(), locale);
				if (scheduleForLowestPrice.getType().equals(PriceScheduleType.RECURRING)) {
					result += " " + getDisplayableRecurringMessage(messageSource, scheduleForLowestPrice, locale);
				}	
				return result;
			}
		}
		return null;
	}

	


	/**
	 * Method to get the string part of the recurring price (ie monthly, annually, etc).
	 * @param messageSource the message source which has the properties file
	 * @param pricingSchedule the pricing schedule to use
	 * @param locale the locale to use
	 * @return a string  based on the message source and pricing schedule
	 */
	private static String getDisplayableRecurringMessage(
			final StoreThemeMessageSource messageSource, final PriceSchedule pricingSchedule, final Locale locale) {
		String unitOfRecurrence = "";
		if (pricingSchedule.getPaymentSchedule() != null && pricingSchedule.getPaymentSchedule().getName() != null) {

			String name = pricingSchedule.getPaymentSchedule().getName();
			String codeToGet = PROPERTY_BASE + name;			
			unitOfRecurrence = messageSource.getMessage(codeToGet, null, name, locale);
		}
		return unitOfRecurrence;
	}
	
	/**
	 * @param original the original string
	 * @param maxLength maximum number of characters allowed (0 for unlimited)
	 * @return original string if it has less than #maxLength characters or
	 *         subtring of original of length #maxLength and "..." appended on the end
	 */
	private static String getLimitedString(final String original, final int maxLength) {
		if (original != null && original.length() > maxLength) {
			return original.substring(0, maxLength - 1) + "...";
		}
		return original;
	}

}
