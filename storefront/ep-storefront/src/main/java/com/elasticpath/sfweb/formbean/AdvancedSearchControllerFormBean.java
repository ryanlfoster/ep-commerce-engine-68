package com.elasticpath.sfweb.formbean;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Interface for the advanced search controller form.
 */
public interface AdvancedSearchControllerFormBean extends EpFormBean {

	/**
	 * Gets the amountFrom field.
	 * @return The amount from field
	 */
	String getAmountFrom();
	
	/**
	 * Gets the amountTo field.
	 * @return The amount to field
	 */
	String getAmountTo();
	
	/**
	 * Sets the amountTo field.
	 * @param amountTo The amount to field to set
	 */
	void setAmountTo(String amountTo);
	
	/**
	 * Sets the amountFrom field.
	 * @param amountFrom The amount from field to set
	 */
	void setAmountFrom(String amountFrom);
	
	/**
	 * Sets the store code.
	 * @param storeCode The store code to set.
	 */
	void setStoreCode(String storeCode);

	/**
	 * Gets the locale field.
	 * @return The locale from field
	 */	
	Locale getLocale();

	/**
	 * Sets the locale.
	 * @param locale The locale to set.
	 */	
	void setLocale(Locale locale);

	/**
	 * Gets the attribute range filters that do not have ranges defined in the configuration.
	 * @return A HashMap containing the attribute keys and the <code>NonPreDefinedAttributeRangeFieldFormBeanImpl</code>s
	 */
	Map<String, NonPreDefinedAttributeRangeFieldFormBean> getNonPreDefinedAttributeRangeFilterMap();

	/**
	 * Sets the attribute range filters that do not have ranges defined in the configuration.
	 * @param nonPreDefinedAttributeRangeFilterMap  A HashMap containing the attribute keys and
	 * 	the <code>NonPreDefinedAttributeRangeFieldFormBeanImpl</code>s
	 */
	void setNonPreDefinedAttributeRangeFilterMap(
			final Map<String, NonPreDefinedAttributeRangeFieldFormBean> nonPreDefinedAttributeRangeFilterMap);

	/**
	 * Gets the attributes values which have been selected. This is a map of attribute keys to filter ID strings.
	 * 
	 * @return attributes value map
	 */
	Map<String, String> getAttributeValuesMap();

	/**
	 * Sets the attributes values which have been selected. This is a map of attribute keys to filter ID strings.
	 * 
	 * @param attributeValuesMap new value map
	 */
	void setAttributeValuesMap(Map<String, String> attributeValuesMap);

	/**
	 * Gets selected brand codes.
	 * 
	 * @return selected brand codes
	 */
	List<String> getBrands();

	/**
	 * Sets the selected brand codes.
	 * 
	 * @param brandsList selected brand codes
	 */
	void setBrands(List<String> brandsList);
}
