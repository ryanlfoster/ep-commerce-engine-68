package com.elasticpath.importexport.common.dto.productcategory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of product category object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ProductCategoryDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "code", required = true)
	private String categoryCode;

	@XmlElement(name = "default")
	private Boolean defaultCategory = Boolean.FALSE;

	@XmlElement(name = "featuredorder")
	private int featuredOrder;

	/**
	 * Gets the category code.
	 * 
	 * @return category code
	 */
	public String getCategoryCode() {
		return categoryCode;
	}

	/**
	 * Sets the category code.
	 * 
	 * @param categoryCode category code
	 */
	public void setCategoryCode(final String categoryCode) {
		this.categoryCode = categoryCode;
	}

	/**
	 * Get the featured product order.
	 * 
	 * @return the featured product order
	 */
	public int getFeaturedOrder() {
		return featuredOrder;
	}

	/**
	 * Set the featured product order.
	 * 
	 * @param featuredOrder the featured product order to set
	 */
	public void setFeaturedOrder(final int featuredOrder) {
		this.featuredOrder = featuredOrder;
	}

	/**
	 * Gets the default category.
	 * 
	 * @return true if category is default false otherwise
	 */
	public Boolean isDefaultCategory() {
		return defaultCategory;
	}

	/**
	 * Sets the default category.
	 * 
	 * @param defaultCategory true if category is default, false otherwise
	 */
	public void setDefaultCategory(final Boolean defaultCategory) {
		this.defaultCategory = defaultCategory;
	}
	
	
}
