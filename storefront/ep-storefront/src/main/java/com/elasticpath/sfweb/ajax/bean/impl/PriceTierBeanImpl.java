package com.elasticpath.sfweb.ajax.bean.impl;

import java.math.BigDecimal;

import com.elasticpath.sfweb.ajax.bean.PriceTierBean;

/**
 * PriceTierBeanImpl.
 * @author mren
 *
 */
public class PriceTierBeanImpl implements PriceTierBean {

	private int minQty;
	private BigDecimal price;

	@Override
	public int getMinQty() {
		return minQty;
	}

	@Override
	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public void setMinQty(final int minQty) {
		this.minQty = minQty;
	}

	@Override
	public void setPrice(final BigDecimal price) {
		this.price = price;
	}

}
