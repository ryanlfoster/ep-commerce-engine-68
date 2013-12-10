/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax;

import java.math.BigDecimal;

import com.elasticpath.persistence.api.Persistable;

/**
 * Required for JPA Mapping.
 */
public interface TaxValue extends Persistable {

	/**
	 * Returns the <code>TaxCode</code> associated with this <code>TaxValue</code>.
	 *
	 * @return the <code>TaxCode</code>
	 */
	TaxCode getTaxCode();

	/**
	 * Set the <code>TaxCode</code> associated with this <code>TaxValue</code>.
	 *
	 * @param taxCode - the sales tax code.
	 */
	void setTaxCode(TaxCode taxCode);

	/**
	 * @return the taxValue
	 */
	BigDecimal getTaxValue();

	/**
	 * @param taxValue the taxValue to set
	 */
	void setTaxValue(BigDecimal taxValue);
}
