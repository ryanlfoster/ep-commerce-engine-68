package com.elasticpath.sfweb.ajax.service.impl;

/**
 * Viewing parameters of autocompletion result items.
 */
class AutocompletionSearchResultConfiguration {

	private final boolean showThumbnail;
	private final boolean seoEnabled;
	private final int productNameMaxLength;
	private final int productDescriptionMaxLength;
	
	/**
	 * Constructor.
	 * 
	 * @param showThumbnail true if need to show product's thumbnail
	 * @param seoEnabled true if search engine optimization is enabled
	 * @param productNameMaxLength the maximum length allowed for product name
	 * @param productDescriptionMaxLength the maximum length allowed for product description
	 */
	public AutocompletionSearchResultConfiguration(
			final boolean showThumbnail,
			final boolean seoEnabled,
			final int productNameMaxLength,
			final int productDescriptionMaxLength) {
		this.showThumbnail = showThumbnail;
		this.seoEnabled = seoEnabled;
		this.productNameMaxLength = productNameMaxLength;
		this.productDescriptionMaxLength = productDescriptionMaxLength;
	}

	/**
	 * @return true if need to show product's thumbnail
	 */
	boolean isShowThumbnail() {
		return showThumbnail;
	}

	/**
	 * @return true if search engine optimization is enabled
	 */
	boolean isSeoEnabled() {
		return seoEnabled;
	}

	/**
	 * @return the maximum length allowed for product name
	 */
	int getProductNameMaxLength() {
		return productNameMaxLength;
	}

	/**
	 * @return the maximum length allowed for product description
	 */
	int getProductDescriptionMaxLength() {
		return productDescriptionMaxLength;
	}
	
}
