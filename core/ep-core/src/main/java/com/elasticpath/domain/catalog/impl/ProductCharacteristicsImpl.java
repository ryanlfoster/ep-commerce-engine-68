package com.elasticpath.domain.catalog.impl;

import com.elasticpath.domain.catalog.ProductCharacteristics;

/**
 * Represents product characteristics.
 */
public class ProductCharacteristicsImpl implements ProductCharacteristics {
	private static final long serialVersionUID = 1L;
	
	private boolean multipleConfigurations;
	private boolean bundle;
	private boolean calculatedBundle;
	private boolean dynamicBundle;
	private Long bundleUid;
	
	@Override
	public boolean hasMultipleConfigurations() {
		return multipleConfigurations;
	}

	public void setMultipleConfigurations(final boolean multipleConfigurations) {
		this.multipleConfigurations = multipleConfigurations;
	}

	@Override
	public boolean isBundle() {
		return bundle;
	}

	public void setBundle(final boolean bundle) {
		this.bundle = bundle;
	}

	@Override
	public boolean isCalculatedBundle() {
		return calculatedBundle;
	}

	public void setCalculatedBundle(final boolean calculatedBundle) {
		this.calculatedBundle = calculatedBundle;
	}

	@Override
	public boolean isDynamicBundle() {
		return dynamicBundle;
	}

	public void setDynamicBundle(final boolean dynamicBundle) {
		this.dynamicBundle = dynamicBundle;
	}

	@Override
	public Long getBundleUid() {
		return bundleUid;
	}
	
	public void setBundleUid(final Long bundleUid) {
		this.bundleUid = bundleUid;
	}

}
