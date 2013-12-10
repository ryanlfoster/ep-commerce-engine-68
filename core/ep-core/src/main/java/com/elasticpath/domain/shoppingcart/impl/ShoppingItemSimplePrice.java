package com.elasticpath.domain.shoppingcart.impl;

import static com.elasticpath.persistence.api.AbstractPersistableImpl.DECIMAL_PRECISION;
import static com.elasticpath.persistence.api.AbstractPersistableImpl.DECIMAL_SCALE;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.elasticpath.domain.catalog.SimplePrice;
import com.elasticpath.domain.misc.Money;

/**
 * Contains the information about a simple price, which will be used to store a snapshot of price for shopping items.
 */
@Embeddable
public class ShoppingItemSimplePrice implements Serializable {

	private BigDecimal listUnitPrice;
	private BigDecimal saleUnitPrice;
	private BigDecimal compUnitPrice;

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Default constructor.
	 */
	public ShoppingItemSimplePrice() {
		//Nothing!
	}

	/**
	 * Constructs the class from a Price object.
	 *
	 * @param price the price object
	 * @param quantity the quantity of the shopping item
	 */
	public ShoppingItemSimplePrice(final SimplePrice price, final int quantity) {
		listUnitPrice = getMoneyValue(price.getListPrice(quantity));
		saleUnitPrice = getMoneyValue(price.getSalePrice(quantity));
		compUnitPrice = getMoneyValue(price.getComputedPrice(quantity));
	}

	/**
	 * Constructs the class from the price pieces.
	 * @param list the list unit price
	 * @param sale the sale unit price
	 * @param prom the promoted unit price
	 */
	public ShoppingItemSimplePrice(final BigDecimal list, final BigDecimal sale, final BigDecimal prom) {
		this.listUnitPrice = list;
		this.saleUnitPrice = sale;
		this.compUnitPrice = prom;
	}

	@Basic
	@Column(name = "LIST_UNIT_PRICE", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	public BigDecimal getListUnitPrice() {
		return this.listUnitPrice;
	}

	public void setListUnitPrice(final BigDecimal price) {
		listUnitPrice = price;
	}

	@Basic
	@Column(name = "SALE_UNIT_PRICE", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	public BigDecimal getSaleUnitPrice() {
		return this.saleUnitPrice;
	}

	public void setSaleUnitPrice(final BigDecimal price) {
		saleUnitPrice = price;
	}

	@Basic
	@Column(name = "PROMO_UNIT_PRICE", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	public BigDecimal getPromotedUnitPrice() {
		return this.compUnitPrice;
	}

	public void setPromotedUnitPrice(final BigDecimal price) {
		compUnitPrice = price;
	}

	private BigDecimal getMoneyValue(final Money money) {
		if (money == null) {
			return null;
		}
		return money.getAmount();
	}

}
