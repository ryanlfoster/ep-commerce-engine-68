package com.elasticpath.sfweb.formbean.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.sfweb.formbean.AdvancedSearchControllerFormBean;
import com.elasticpath.sfweb.formbean.NonPreDefinedAttributeRangeFieldFormBean;

/**
 * Implementation of AdvancedSearchControllerFormBean.
 */
public class AdvancedSearchControllerFormBeanImpl extends EpFormBeanImpl implements	AdvancedSearchControllerFormBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private String amountTo;

	private String amountFrom;	
			
	private String storeCode;
	
	private Map<String, NonPreDefinedAttributeRangeFieldFormBean> nonPreDefinedAttributeRangeFilterMap;

	private Locale locale;

	private Map<String, String> attributeValuesMap = new HashMap<String, String>();

	private List<String> brands = new ArrayList<String>();

	@Override
	public String getAmountFrom() {
		return amountFrom;
	}

	@Override
	public void setAmountFrom(final String amountFrom) {
		this.amountFrom = amountFrom;
	}

	@Override
	public String getAmountTo() {
		return amountTo;
	}

	@Override
	public void setAmountTo(final String amountTo) {
		this.amountTo = amountTo;
	}
	
	@Override
	public Map<String, NonPreDefinedAttributeRangeFieldFormBean> getNonPreDefinedAttributeRangeFilterMap() {
		return nonPreDefinedAttributeRangeFilterMap;
	}
	
	@Override
	public void setNonPreDefinedAttributeRangeFilterMap(
			final Map<String, NonPreDefinedAttributeRangeFieldFormBean> nonPreDefinedAttributeRangeFilterMap) {
		this.nonPreDefinedAttributeRangeFilterMap = nonPreDefinedAttributeRangeFilterMap;
	}

	/**
	 * @param storeCode the storeCode to set
	 */
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	/**
	 * @return the storeCode
	 */
	public String getStoreCode() {
		return storeCode;
	}

	@Override	
	public Locale getLocale() {
		return locale;
	}

	@Override	
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}	

	@Override
	public Map<String, String> getAttributeValuesMap() {
		return attributeValuesMap;
	}
	
	@Override
	public void setAttributeValuesMap(final Map<String, String> attributeValuesMap) {
		this.attributeValuesMap = attributeValuesMap;
	}

	@Override
	public List<String> getBrands() {
		return brands;
	}

	@Override
	public void setBrands(final List<String> brands) {
		this.brands = brands;
	}
}
