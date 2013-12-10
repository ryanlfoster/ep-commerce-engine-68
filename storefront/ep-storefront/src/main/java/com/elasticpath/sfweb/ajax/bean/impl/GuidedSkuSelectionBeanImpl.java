/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.ajax.bean.impl;

import java.util.List;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.sfweb.ajax.bean.GuidedSkuSelectionBean;
import com.elasticpath.sfweb.ajax.bean.PriceTierBean;

/**
 * This bean is used for transferring data to the client from
 * the <code>SkuConfigurationService</code>.
 */
public class GuidedSkuSelectionBeanImpl implements GuidedSkuSelectionBean {

	private ProductSku productSku;
	private String imageUrl;
	private String listPrice;
	private String lowestPrice;
	private String dollarSavings;
	private boolean lowestLessThanList;
	private String[] priceTierContents;
	private InventoryDto inventoryDto;
	private String availabilityCode;
	private boolean infiniteQuantity;
	private boolean skuAvailable;
	private boolean purchasable;
	
	private int minOrderQty;
	
	private List<PriceTierBean> priceTiers;
	@Override
	public boolean isPurchasable() {
		return purchasable;
	}
	 
	@Override
	public void setPurchasable(final boolean purchasable) {
		this.purchasable = purchasable;
	}

	/**
	 * Get the <code>ProductSku</code>.
	 * @return the <code>ProductSku</code>
	 */
	public ProductSku getProductSku() {
		return this.productSku;
	}

	/**
	 * Set the <code>ProductSku</code>.
	 * @param productSku the <code>ProductSku</code>
	 */
	public void setProductSku(final ProductSku productSku) {
		this.productSku = productSku;
	}

	/**
	 * Get the path to the image to be displayed.
	 * @return the path to the image to be displayed
	 */
	public String getImageUrl() {
		return this.imageUrl;
	}

	/**
	 * Set the path to the image to be displayed.
	 * @param imageUrl the path to the image to be displayed
	 */
	public void setImageUrl(final String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * Get the skus's list price.
	 * @return the list price as a <code>MoneyImpl</code>
	 */
	public String getListPrice() {
		return this.listPrice;
	}

	/**
	 * Set the sku's list price.
	 * @param listPrice the sku's list price
	 */
	public void setListPrice(final String listPrice) {
		this.listPrice = listPrice;
	}

	/**
	 * Returns the lowest of the price values specified.
	 * @return the lowest price
	 */
	public String getLowestPrice() {
		return this.lowestPrice;
	}

	/**
	 * Set the sku's lowest price.
	 * @param lowestPrice the sku's lowest price
	 */
	public void setLowestPrice(final String lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	/**
	 * Calculates the savings if the price has a discount.
	 * @return the price savings
	 */
	public String getDollarSavings() {
		return this.dollarSavings;
	}

	/**
	 * Set the sku's dollar savings.
	 * @param dollarSavings the sku's dollar savings
	 */
	public void setDollarSavings(final String dollarSavings) {
		this.dollarSavings = dollarSavings;
	}

	/**
	 * Check if the lowest price is less than the list price, i.e. the price has a discount.
	 * @return true if the price has a lower price than the list price.
	 */
	public boolean isLowestLessThanList() {
		return this.lowestLessThanList;
	}

	/**
	 * Sets if the sku's lowest price is less than the list price.
	 * @param isLowestLessThanList true if the price has a lower price than the list price.
	 */
	public void setLowestLessThanList(final boolean isLowestLessThanList) {
		this.lowestLessThanList = isLowestLessThanList;
	}

	/**
	 * Get the price tier contents as the String Array to be displayed.
	 * @return the formated price tier contents
	 */
	public String[] getPriceTierContents() {
		return this.priceTierContents.clone();

	}

	/**
	 * Set the price tier contents as the String Array to be displayed.
	 * @param priceTierContents the formated price tier contents to be displayed.
	 */
	public void setPriceTierContents(final String[] priceTierContents) {
		this.priceTierContents = priceTierContents.clone();
	}

	/**
	 * Get the inventory for this product sku.
	 * @return the inventory
	 */
	public InventoryDto getInventory() {
		return this.inventoryDto;
	}
	
	/**
	 * Set the inventory to be displayed.
	 * @param inventoryDto the product sku's inventory.
	 */
	public void setInventory(final InventoryDto inventoryDto) {
		this.inventoryDto = inventoryDto;
	}

	@Override
	public String getAvailabilityCode() {
		return availabilityCode;
	}

	@Override
	public void setAvailabilityCode(final String code) {
		this.availabilityCode = code;
	}

	@Override
	public boolean isInfiniteQuantity() {
		return infiniteQuantity;
	}

	@Override
	public void setInfiniteQuantity(final boolean infiniteQtyInStock) {
		this.infiniteQuantity = infiniteQtyInStock;
	}

	@Override
	public void setAvailable(final boolean isSkuAvailable) {
		this.skuAvailable = isSkuAvailable;
	}

	@Override
	public boolean isSkuAvailable() {
		return skuAvailable;
	}

	@Override
	public List<PriceTierBean> getPriceTiers() {
		return priceTiers;
	}

	@Override
	public void setPriceTiers(final List<PriceTierBean> priceTiers) {
		this.priceTiers = priceTiers;
	}

	@Override
	public int getMinOrderQty() {
		return minOrderQty;
	}

	@Override
	public void setMinOrderQty(final int minOrderQty) {
		this.minOrderQty = minOrderQty;
		
	}

}
