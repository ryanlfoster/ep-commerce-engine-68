/**
 * 
 */
package com.elasticpath.sellingchannel.presentation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.tree.TreeNode;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.inventory.InventoryDto;

/**
 * <p>Represents a (potentially) flattened view of an {@code OrderSku}.
 * An {@code OrderItemDocument} differs from an OrderSku in that it knows
 * its level within the tree of {@code OrderSku}s.</p>
 * <p>This object is typically used when a presentation layer needs to show
 * a tree representation of an OrderSku but wants to iterate through a list rather
 * than recurse through a tree. View technologies such as Velocity don't appreciate recursive
 * traversals.</p>
 */
public interface OrderItemPresentationBean extends TreeNode<OrderItemPresentationBean> {

	/**
	 * Gets the digital asset belong to this order SKU.
	 * 
	 * @return the digital asset belong to this order SKU
	 */
	DigitalAsset getDigitalAsset();

	/**
	 * Get the product's display name.
	 * 
	 * @return the product's display name.
	 */
	String getDisplayName();

	/**
	 * Get the encrypted uidPk string.
	 * 
	 * @return the encrypted uidPk string
	 */
	String getEncryptedUidPk();

	/**
	 * Get the product's image path.
	 * 
	 * @return the product's image path.
	 */
	String getImage();
	
	/**
	 * @return the display sku options string.
	 */
	String getDisplaySkuOptions();

	/**
	 * Adds child {@link OrderItemPresentationBean}.
	 * 
	 * @param child {@link OrderItemPresentationBean}
	 */
	void addChild(OrderItemPresentationBean child);

	/**
	 * @return a list of {@link OrderItemFormBean}s
	 */
	List<OrderItemPresentationBean> getChildren();

	/**
	 * @return whether the order item is allocated or not
	 */
	boolean isAllocated();

	/**
	 * @return the list price as money instance
	 */
	Money getListPriceMoney();

	/**
	 * @return the unit price as money instance
	 */
	Money getUnitPriceMoney();

	/**
	 * 
	 * @return whether the unit price is less than list price or not
	 */
	boolean isUnitLessThanList();

	/**
	 * @return the dollar savings as money instance
	 */
	Money getDollarSavingsMoney();

	/**
	 * 
	 * @param displaySkuOptions The display Sku Options.
	 */
	void setDisplaySkuOptions(String displaySkuOptions);

	/**
	 * 
	 * @param allocated True if allocated.
	 */
	void setAllocated(boolean allocated);

	/**
	 * 
	 * @param digitalAsset The digital asset.
	 */
	void setDigitalAsset(DigitalAsset digitalAsset);

	/**
	 * 
	 * @param encryptedUidPk The encrypted uid pk.
	 */
	void setEncryptedUidPk(String encryptedUidPk);

	/**
	 * 
	 * @param listPrice The list price.
	 */
	void setListPriceMoney(Money listPrice);

	/**
	 * 
	 * @param unitLessThanList True if the unit price is less than the list price.
	 */
	void setUnitLessThanList(boolean unitLessThanList);

	/**
	 * 
	 * @param unitPrice The unit price.
	 */
	void setUnitPriceMoney(Money unitPrice);

	/**
	 * 
	 * @param dollarSavings The dollar savings.
	 */
	void setDollarSavingsMoney(Money dollarSavings);

	/**
	 * @param quantity the quantity.
	 */
	void setQuantity(final int quantity);

	/**
	 * @param skuCode the sku code.
	 */
	void setSkuCode(final String skuCode);

	/**
	 * @param productSku the product sku
	 */
	void setProductSku(final ProductSku productSku);

	/**
	 * 
	 * @param level The level
	 */
	void setLevel(int level);

	/**
	 * 
	 * @return The level in the tree.
	 */
	int getLevel();

	
	/**
	 * 
	 * @return the product sku
	 */
	ProductSku getProductSku();

	/**
	 * 
	 * @return The sku code
	 */
	String getSkuCode();

	/**
	 * 
	 * @return The quantity
	 */
	int getQuantity();

	/**
	 * 
	 * @param displayName The display name
	 */
	void setDisplayName(String displayName);

	/**
	 * @param image The image file name.
	 */
	void setImage(String image);
	
	/**
	 * @return the line total (quantity * unit price).
	 */
	Money getTotalMoney();
	
	/**
	 * 
	 * @param amount The line total.
	 */
	void setTotalMoney(Money amount);
	
	/**
	 * @return map of order item fields
	 */
	Map<String, String> getOrderItemFields();
	
	/**
	 *	Sets the map of order item fields.
	 * @param orderItemFields map of order item fields to set
	 */
	void setOrderItemFields(Map<String, String> orderItemFields);

	/**
	 * Adds a flag for view layer indicating that we need to show some special properties.
	 * 
	 * @param flagName flag name
	 * @param value true, if the flag is set. Otherwise false
	 */
	void addViewFlag(String flagName, boolean value);
	
	/**
	 * @param flagName flag name
	 * @return true if flag is on, false if it is off or doesn't exist 
	 */
	boolean isViewFlagOn(String flagName);
	
	/**
	 * @return {@link InventoryDto}.
	 */
	InventoryDto getInventory();
	
	/**
	 * @param inventoryDto {@link InventoryDto}
	 */
	void setInventory(InventoryDto inventoryDto);

	
	
	/**
	 * Get the price.
	 *
	 * @return the price
	 */
	Price getPrice();

	/**
	 * Set the price.
	 *
	 * @param price the price to set
	 */
	void setPrice(final Price price);
	
	/**
	 * @return true if this OrderSku is a calculated bundle.
	 */
	boolean isCalculatedBundle();
	
	/**
	 * @param calculatedBundle whether this OrderSku is a calculated bundle.
	 */
	void setCalculatedBundle(boolean calculatedBundle);

	/**
	 * @return true if this OrderSku is a calculated bundle item, which contributes to its parent calculated price.
	 */
	boolean isCalculatedBundleItem();
	
	/**
	 * @param calculatedBundleItem whether this OrderSku is a calculated bundle item,
	 * which contributes to its parent calculated price.
	 */
	void setCalculatedBundleItem(boolean calculatedBundleItem);

	/**
	 * Gets the filtered sku opton values for the given locale. Does not include values for the Frequency Sku Option
	 * @param locale the locale to use.
	 * @return filtered display name
	 */
	String getFilteredSkuOptionValues(Locale locale);
	
	
}
