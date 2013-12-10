/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.ajax.bean;

import java.util.List;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.inventory.InventoryDto;

/**
 * This bean is used for transfering data to the client from the <code>SkuConfigurationService</code>.
 */
public interface GuidedSkuSelectionBean {

 	/**
	 * @return is purchasable
	 */
	boolean isPurchasable();
		
	/**
	 * Sets the purchasable flag.
	 * @param isPurchasable is purchasable
	 */
	void setPurchasable(boolean isPurchasable);
	
	/**
	 * Get the <code>ProductSku</code>.
	 * 
	 * @return the <code>ProductSku</code>
	 */
	ProductSku getProductSku();

	/**
	 * Set the <code>ProductSku</code>.
	 * 
	 * @param productSku the <code>ProductSku</code>
	 */
	void setProductSku(final ProductSku productSku);

	/**
	 * Get the path to the image to be displayed.
	 * 
	 * @return the path to the image to be displayed
	 */
	String getImageUrl();

	/**
	 * Set the path to the image to be displayed.
	 * 
	 * @param imageUrl the path to the image to be displayed
	 */
	void setImageUrl(final String imageUrl);

	/**
	 * Get the skus's list price.
	 * 
	 * @return the list price as a <code>MoneyImpl</code>
	 */
	String getListPrice();

	/**
	 * Set the sku's list price.
	 * 
	 * @param listPrice the sku's list price
	 */
	void setListPrice(final String listPrice);

	/**
	 * Returns the lowest of the price values specified.
	 * 
	 * @return the lowest price
	 */
	String getLowestPrice();

	/**
	 * Set the sku's lowest price.
	 * 
	 * @param lowestPrice the sku's lowest price
	 */
	void setLowestPrice(final String lowestPrice);

	/**
	 * returns the savings if the price has a discount.
	 * 
	 * @return the price savings
	 */
	String getDollarSavings();

	/**
	 * Set the sku's dollar savings.
	 * 
	 * @param dollarSavings the sku's dollar savings
	 */
	void setDollarSavings(final String dollarSavings);

	/**
	 * Check if the lowest price is less than the list price, i.e. the price has a discount.
	 * 
	 * @return true if the price has a lower price than the list price.
	 */
	boolean isLowestLessThanList();

	/**
	 * Sets if the sku's lowest price is less than the list price.
	 * 
	 * @param isLowestLessThanList true if the price has a lower price than the list price.
	 */
	void setLowestLessThanList(final boolean isLowestLessThanList);

	/**
	 * Set the price tier contents as the String Array to be diplayed.
	 * 
	 * @param priceTierContents the formated price tier contents to be displayed.
	 */
	void setPriceTierContents(final String[] priceTierContents);

	/**
	 * Get the price tier contents as the String Array to be diplayed.
	 * 
	 * @return the formated price tier contents
	 */
	String[] getPriceTierContents();
	
	/**
	 * Get the inventory for this product sku.
	 * @return the inventory
	 */
	InventoryDto getInventory();
	
	/**
	 * Set the inventory to be displayed.
	 * @param inventoryDto the product sku's inventory.
	 */
	void setInventory(final InventoryDto inventoryDto);
	
	/**
	 * Gets the availability code for this sku.
	 * 
	 * @return the availability message code as a string
	 */
	String getAvailabilityCode();
	
	/**
	 * Sets the availability code.
	 * 
	 * @param code the availability message code as a string
	 */
	void setAvailabilityCode(String code);

	/**
	 * Sets whether there is infinite quantity in stock.
	 * 
	 * @param infiniteQtyInStock true if availability criteria is set to ALWAYS_AVAILABLE
	 */
	void setInfiniteQuantity(boolean infiniteQtyInStock);

	/**
	 * Gets whether there is infinite quantity in stock.
	 * 
	 * @return true if availability criteria is set to ALWAYS_AVAILABLE
	 */
	boolean isInfiniteQuantity();

	/**
	 * Sets availability flag.
	 * 
	 * @param isSkuAvailable true if the underlying SKU is available 
	 */
	void setAvailable(boolean isSkuAvailable);
	
	/**
	 * Gets the availability flag.
	 * 
	 * @return true if the underlying SKU is available
	 */
	boolean isSkuAvailable();

	/**
	 * 
	 * @return price tiers.
	 */
	List<PriceTierBean> getPriceTiers();
	
	/**
	 * Sets price tiers.
	 * @param priceTierBeans list of price tiers.
	 */
	void setPriceTiers(List<PriceTierBean> priceTierBeans);
	
	/**
	 * Gets the minimum order quantity of the corresponding product.
	 *
	 * @return the minimum order quantity
	 */
	int getMinOrderQty();
	
	
	/**
	 * Sets the minimum order quantity of the corresponding product.
	 *
	 * @param minOrderQty the new minimum order quantity
	 */
	void setMinOrderQty(final int minOrderQty);
}
