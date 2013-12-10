package com.elasticpath.common.dto.pricing.impl;

import java.math.BigDecimal;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.common.dto.pricing.DisplayPriceDTO;
import com.elasticpath.domain.misc.Money;

/**
 * DTO for displaying a price of a certain price tier and price list.
 * Used as a read-only object.
 */
public class DisplayPriceDTOImpl implements Dto, DisplayPriceDTO {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090907L;
	
	private final String priceListName;
	private final String priceListGuid;
	
	private final String objectGuid;
	private final String objectType;
	
	private final BigDecimal listPrice;
	private final BigDecimal quantity;
	private final BigDecimal salePrice;
	private Money lowestPrice;
	
	/**
	 * constructor for use in the JPQL only.
	 * @param priceListName the price list name
	 * @param priceListGuid the price list guid
	 * @param objectGuid the guid of object (e.g. SKU)
	 * @param objectType the type of object (e.g. SKU, Product)
	 * @param listPrice the list price
	 * @param quantity the price tier
	 * @param salePrice the sale price
	 */
	public DisplayPriceDTOImpl(final String priceListName,
			final String priceListGuid,
			final String objectGuid,
			final String objectType,
			final BigDecimal listPrice,
			final BigDecimal quantity,
			final BigDecimal salePrice) {
		this.priceListName = priceListName;
		this.priceListGuid = priceListGuid;
		this.objectGuid = objectGuid;
		this.objectType = objectType;
		this.listPrice = listPrice;
		this.quantity = quantity;
		this.salePrice = salePrice;
	}

	@Override
	public String getPriceListName() {
		return priceListName;
	}
	@Override
	public String getPriceListGuid() {
		return priceListGuid;
	}
	@Override
	public String getObjectGuid() {
		return objectGuid;
	}
	@Override
	public String getObjectType() {
		return objectType;
	}
	@Override
	public BigDecimal getListPrice() {
		return listPrice;
	}
	@Override
	public BigDecimal getQuantity() {
		return quantity;
	}

	@Override
	public BigDecimal getSalePrice() {
		return salePrice;
	}
	
	@Override
	public Money getLowestPrice() {
		return lowestPrice;
	}

	@Override
	public void setLowestPrice(final Money lowestPrice) {
		this.lowestPrice = lowestPrice;
	}
	
}
