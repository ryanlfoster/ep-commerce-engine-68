/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.search.query;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * A criteria for advanced product associations search.
 */
public class ProductAssociationSearchCriteria extends AbstractSearchCriteriaImpl implements SearchCriteria {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private Product sourceProduct = null;

	private Product targetProduct = null;
	
	private String sourceProductCode = null;
	
	private String targetProductCode = null;

	private Integer associationType = null;
	
	private String catalogCode = null;
	
	private boolean withinCatalogOnly = false;

	/**
	 * Returns The Source Product.
	 * 
	 * @return the sourceProduct
	 */
	public Product getSourceProduct() {
		return sourceProduct;
	}

	/**
	 * Sets The Source Product.
	 * 
	 * @param sourceProduct the sourceProduct to set
	 */
	public void setSourceProduct(final Product sourceProduct) {
		this.sourceProduct = sourceProduct;
	}

	/**
	 * Returns The Target Product.
	 * 
	 * @return the targetProduct
	 */
	public Product getTargetProduct() {
		return targetProduct;
	}

	/**
	 * Sets The Target Product.
	 * 
	 * @param targetProduct the targetProduct to set
	 */
	public void setTargetProduct(final Product targetProduct) {
		this.targetProduct = targetProduct;
	}

	/**
	 * Returns The Association Type.
	 * 
	 * @return the associationType
	 */
	public Integer getAssociationType() {
		return associationType;
	}

	/**
	 * Sets The Association Type.
	 * 
	 * @param associationType the associationType to set
	 */
	public void setAssociationType(final Integer associationType) {
		this.associationType = associationType;
	}

	/**
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	public void optimize() {
		if (sourceProduct != null || (sourceProductCode != null && sourceProductCode.trim().length() < 1)) {
			sourceProductCode = null;
		}
		if (targetProduct != null || (targetProductCode != null && targetProductCode.trim().length() < 1)) {
			targetProductCode = null;
		}
	}

	/**
	 * Returns the index type this criteria deals with.
	 * 
	 * @return the index type this criteria deals with
	 */
	public IndexType getIndexType() {
		return null;
	}

	/**
	 *
	 * @return the sourceProductCode
	 */
	public String getSourceProductCode() {
		return sourceProductCode;
	}

	/**
	 *
	 * @param sourceProductCode the sourceProductCode to set
	 */
	public void setSourceProductCode(final String sourceProductCode) {
		this.sourceProductCode = sourceProductCode;
	}

	/**
	 *
	 * @return the targetProductCode
	 */
	public String getTargetProductCode() {
		return targetProductCode;
	}

	/**
	 *
	 * @param targetProductCode the targetProductCode to set
	 */
	public void setTargetProductCode(final String targetProductCode) {
		this.targetProductCode = targetProductCode;
	}
	
	/**
	 * 
	 * @param catalogCode the catalog code to set
	 */
	public void setCatalogCode(final String catalogCode) {
		this.catalogCode = catalogCode;
	}
	
	/**
	 * 
	 * @return the catalog code
	 */
	public String getCatalogCode() {
		return this.catalogCode;
	}

	/**
	 * 
	 * @return the withinCatalogOnly
	 */
	public boolean isWithinCatalogOnly() {
		return withinCatalogOnly;
	}

	/**
	 *
	 * @param withinCatalogOnly the withinCatalogOnly to set
	 */
	public void setWithinCatalogOnly(final boolean withinCatalogOnly) {
		this.withinCatalogOnly = withinCatalogOnly;
	}
	
	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public int hashCode() {
	   return HashCodeBuilder.reflectionHashCode(this);
	 }
}
