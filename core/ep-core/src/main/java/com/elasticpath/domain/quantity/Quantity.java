/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.quantity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Represents a dimensioned value with both an amount and its unit.
 */
@Embeddable
public class Quantity implements Serializable {
	/**
	 * Denotes the default precision for the amount field.
	 */
	public static final int AMOUNT_PRECISION = 19;

	/**
	 * Denotes the default scale for the amount field.
	 */
	public static final int AMOUNT_SCALE = 8;

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private Number amount;
	
	private String unit;
	
	/**
	 * Construct a new Quantity with the given amount and unit.
	 * 
	 * @param amount the amount
	 * @param unit the unit of measure
	 */
	public Quantity(final Number amount, final String unit) {
		super();
		this.amount = amount;
		this.unit = unit;
	}
	
	/**
	 * No-args constructor for use by JPA.
	 */
	protected Quantity() {
		super();
	}
	
	/**
	 * @return the amount of this quantity.
	 */
	@Basic
	@Column(name = "AMOUNT", scale = AMOUNT_SCALE, precision = AMOUNT_PRECISION)
	public Number getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set.
	 */
	protected void setAmount(final Number amount) {
		this.amount = amount;
	}
	
	/**
	 * @return the unit that this quantity represents.
	 */
	@Basic
	@Column(name = "UNIT")
	public String getUnit() {
		return unit;
	}
	
	/**
	 * @param unit the unit
	 */
	protected void setUnit(final String unit) {
		this.unit = unit;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Quantity)) {
			return false;
		}
		
		Quantity quantity = (Quantity) other;
		return StringUtils.equals(getUnit(), quantity.getUnit())
			&& ObjectUtils.equals(getCanonicalAmount(), quantity.getCanonicalAmount());
	}

	@Transient
	private Object getCanonicalAmount() {
		if (getAmount() == null) {
			return null;
		}
		return new BigDecimal(getAmount().toString()).setScale(AMOUNT_SCALE);
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 43;
		int hashcode = 1;	
		
		hashcode = prime * hashcode + ObjectUtils.hashCode(getUnit());
		hashcode = prime * hashcode + ObjectUtils.hashCode(getCanonicalAmount());
		
		return hashcode;
	}
	
}
