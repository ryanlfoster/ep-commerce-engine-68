package com.elasticpath.sfweb.viewbean.impl;

import java.util.Map;

import com.elasticpath.sfweb.viewbean.AdvancedSearchSummaryOptionsBean;

/**
 * Implementation for AdvancedSearchSummaryOptionsBean.
 */
public class AdvancedSearchSummaryOptionsBeanImpl implements
		AdvancedSearchSummaryOptionsBean {

	private String brandString;
	private String priceString;
	private Map<String, String> attributeMap;
	@Override
	public String getBrandString() {
		return brandString;
	}
	
	@Override
	public String getPriceString() {
		return priceString;
	}

	@Override
	public void setBrandString(final String brandString) {
		this.brandString = brandString;
	}

	@Override
	public void setPriceString(final String priceString) {
		this.priceString = priceString;
	}

	@Override
	public Map<String, String> getAttributeMap() {
		return this.attributeMap;
	}

	@Override
	public void setAttributeMap(final Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}

}
