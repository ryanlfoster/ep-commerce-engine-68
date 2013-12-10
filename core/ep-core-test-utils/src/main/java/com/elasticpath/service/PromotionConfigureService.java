package com.elasticpath.service;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.store.Store;

/**
 * Service to help configure promotion rules.
 */
public interface PromotionConfigureService {

	/**
	 * Returns <code>RuleParameter</code> defined by key. NOTICE: in the future this method should be moved to <code>RuleElement</code>
	 * 
	 * @param element element in which we should find <code>RuleParameter</code>.
	 * @param key key parameter that defines concrete <code>RuleParameter</code> in <code>RuleElement</code>.
	 * @return <code>RuleParameter</code> by key or null if it doesn't exists in <code>RuleElement</code>.
	 */
	RuleParameter retrieveRuleParameterByKey(final RuleElement element, final String key);

	/**
	 * Returns <code>RuleParameter</code> defined by key.
	 * 
	 * @param exception rule exception in which we should find <code>RuleParameter</code>
	 * @param key key parameter that defines concrete <code>RuleParameter</code> in <code>RuleException</code>
	 * @return <code>RuleParameter</code> by key or null if it doesn't exists in <code>RuleException</code>.
	 */
	RuleParameter retrieveRuleParameterByKey(final RuleException exception, final String key);

	/**
	 * Convert category GUID into category UIDPK.
	 * 
	 * @param value category CODE (or GUID which is the same for category)
	 * @param catalog to find the category into
	 * @return string representation of category UIDPK
	 */
	String getCategoryIdValue(final String value, final Catalog catalog);

	/**
	 * Convert shipping service level display name into shipping service level UIDPK.
	 * 
	 * @param value shipping service level display name
	 * @param store the store shipping service level is used by
	 * @return string representation of shipping service level UIDPK
	 */
	String getShippingServiceLevelIdValue(final String value, final Store store);

	/**
	 * Convert product GUID into product UIDPK.
	 * 
	 * @param value product GUID string value
	 * @return product UIDPK string representation
	 */
	String getProductIdValue(final String value);

	/**
	 * Convert sku CODE into product's UIDPK.
	 * 
	 * @param value sku CODE
	 * @return string representation of product UIDPK
	 */
	String getSkuCodeValue(final String value);

	/**
	 * Check if a brand with the given CODE exists in database.
	 * 
	 * @param value brand CODE
	 * @return brand CODE
	 */
	String getBrandIdValue(final String value);
}
