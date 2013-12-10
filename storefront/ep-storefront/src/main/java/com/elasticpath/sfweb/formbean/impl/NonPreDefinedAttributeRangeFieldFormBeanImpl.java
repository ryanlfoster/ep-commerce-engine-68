package com.elasticpath.sfweb.formbean.impl;

import com.elasticpath.sfweb.formbean.NonPreDefinedAttributeRangeFieldFormBean;

/**
 * Implementation of NonPreDefinedAttributeRangeFieldFormBean.
 */
public class NonPreDefinedAttributeRangeFieldFormBeanImpl implements NonPreDefinedAttributeRangeFieldFormBean {
	
	private String fromField;
	private String toField;

	@Override
	public void setFromField(final String fromField) {
		this.fromField = fromField;
	}

	@Override
	public String getFromField() {
		return fromField;
	}

	@Override
	public void setToField(final String toField) {
		this.toField = toField;
	}

	@Override
	public String getToField() {
		return toField;
	}
}
