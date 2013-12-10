package com.elasticpath.sfweb.view.helpers;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.service.catalogview.FilterFactory;

/**
 * Helper class to map the names of the fields to some identifier that is unique for the rendered page.
 *
 */
public class ParameterMapper {
			
	private static final String TO_FIELD = "toField";
	
	private static final String FROM_FIELD = "fromField";
	
	private static final String ADVANCED_SEARCH_BUTTON_NAME = "search";
	
	private static final String AMOUNT_FROM = "amountFrom";
	
	private static final String AMOUNT_TO = "amountTo";

	private static final String FILTERS_PARAMETER = "filters";
	
	private static final String NON_PREDEFINED_ATTRIBUTE_RANGE_FILTER_MAP = "nonPreDefinedAttributeRangeFilterMap";
	

	private FilterFactory filterFactory;
	
	private static final Logger LOG = Logger.getLogger(ParameterMapper.class);
	
	/**
	 * Converts the attribute key to the attribute prefix plus the key.
	 * @param attributeKey The attribute key
	 * @param storeCode store code to use in conversion
	 * @return The String to map the attribute key to, or an empty String if attribute key
	 *  cannot be found in the system.
	 */
	public String convertAttributeToParameter(final String attributeKey, final String storeCode) {
		AttributeValueFilter attributeValueFilter = filterFactory.getAllSimpleValuesMap(storeCode).get(attributeKey);
		
		if (attributeValueFilter == null) {
			LOG.warn(String.format("Cannot find attribute key <%s> in the configuration. ", attributeKey));
			return StringUtils.EMPTY;
		}
		return attributeValueFilter.getAttributePrefixAndKey();
	}
	
	/**
	 * Converts an attribute key to the request parameter field for an attribute range filter for the FROM field.
	 * Useful as a locator of a "from" range element.
	 * @param attributeKey The attribute key
	 * @return The String with the attribute range prefix plus "from", or an empty String if attribute key is null or empty.
	 */
	public String convertFromRangeAttributeToParameter(final String attributeKey) {
		if (StringUtils.isEmpty(attributeKey)) {
			LOG.warn("Unable to construct Attribute Range From field since the attribute key is non-defined");
			return StringUtils.EMPTY;
		}
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append(NON_PREDEFINED_ATTRIBUTE_RANGE_FILTER_MAP + "[" + attributeKey + "].");
		strBuilder.append(FROM_FIELD);
		
		return strBuilder.toString();
	}
	
	/**
	 * Converts an attribute key to the request parameter field for an attribute range filter for the TO field.
	 * Useful as a locator of a "to" range element.
	 * @param attributeKey The attribute key
	 * @return The String with the attribute range prefix plus "to", or an empty String if attribute key is null or empty.
	 */
	public String convertToRangeAttributeToParameter(final String attributeKey) {
		if (StringUtils.isEmpty(attributeKey)) {
			LOG.warn("Unable to construct Attribute Range To field since the attribute key is non-defined");
			return StringUtils.EMPTY;
		}
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append(NON_PREDEFINED_ATTRIBUTE_RANGE_FILTER_MAP + "[" + attributeKey + "].");
		strBuilder.append(TO_FIELD);
		
		return strBuilder.toString();
	}

	/**
	 * @param filterFactory the filterFactory to set
	 */
	public void setFilterFactory(final FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}

	/**
	 * @return the filterFactory
	 */
	protected FilterFactory getFilterFactory() {
		return filterFactory;
	}
	
	/**
	 * @return ADVANCED_SEARCH_BUTTON_NAME
	 */
	public String getSearchFormButtonName() {
		return ADVANCED_SEARCH_BUTTON_NAME;
	}

	/**
	 * @return AMOUNT_TO
	 */
	public String getAmountTo() {
		return AMOUNT_TO;
	}
	
	/**
	 * @return AMOUNT_FROM
	 */
	public String getAmountFrom() {
		return AMOUNT_FROM;
	}
	
	/**
	 * @return The filters parameter name.
	 */
	public String getFiltersParameter() {
		return FILTERS_PARAMETER;
	}
	
	/**
	 * @return NON_PREDEFINED_ATTRIBUTE_RANGE_FILTER_MAP
	 */	
	public String getNonPreDefinedAttributeRangeFilterMap() {
		return NON_PREDEFINED_ATTRIBUTE_RANGE_FILTER_MAP;
	}

	/**
	 * @return TO_FIELD
	 */	
	public String getToField() {
		return TO_FIELD;
	}

	/**
	 * @return FROM_FIELD
	 */	
	public String getFromField() {
		return FROM_FIELD;
	}	
}
