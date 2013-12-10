package com.elasticpath.domain.tax;

import com.elasticpath.persistence.api.Entity;

/**
 * <code>TaxCode</code> represents a type of sales tax, e.g Books, Liquor, Tobacco, Clothing.
 */
public interface TaxCode extends Entity {

	/** The REQUIRED sales tax code for Shipping. */
	String TAX_CODE_SHIPPING = "SHIPPING";

	/**
	 * Return the sales tax code.
	 * 
	 * @return the sales tax code.
	 */
	String getCode();

	/**
	 * Set the sales tax code.
	 * 
	 * @param code - the sales tax code.
	 */
	void setCode(final String code);
}
