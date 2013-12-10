package com.elasticpath.common.pricing.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Locale;

import com.elasticpath.common.pricing.service.BaseAmountFilterExt;

/**
 *
 * Implementation of the BaseAmountFilterExt.
 *
 */
public class BaseAmountFilterExtImpl extends BaseAmountFilterImpl implements
		Serializable, BaseAmountFilterExt {
	
	private BigDecimal highestPrice;
	
	private BigDecimal lowestPrice;
	
	private Locale locale;

	private int startIndex;

	private int limit;
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20091127L;
	
	@Override
	public BigDecimal getHighestPrice() {
		return highestPrice;
	}

	@Override	
	public BigDecimal getLowestPrice() {
		return lowestPrice;
	}

	@Override	
	public void setHighestPrice(final BigDecimal highestPrice) {
		this.highestPrice = highestPrice;
	}
	
	@Override
	public void setLowestPrice(final BigDecimal lowestPrice) {
		this.lowestPrice = lowestPrice;
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
	public int getLimit() {
		return limit;
	}

	@Override	
	public void setLimit(final int limit) {
		this.limit = limit;
	}

	/** {@inheritDoc} */
	public int getStartIndex() {
		return startIndex;
	}

	/** {@inheritDoc} */
	public void setStartIndex(final int startIndex) {
		this.startIndex = startIndex;
	}
}
