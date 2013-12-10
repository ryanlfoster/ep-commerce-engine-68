/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.tree.TreeNode;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.service.shoppingcart.impl.ItemPricing;

/**
 * Represents a quantity of SKUs in a shopping cart, in progress cart, wish list, etc.
 */
public interface ShoppingItem extends Entity, TreeNode<ShoppingItem> {
	/**
	 * Get the product SKU corresponding to this item.
	 * 
	 * @return a <code>ProductSKU</code>
	 */
	ProductSku getProductSku();
	
	/**
	 * set the product SKU corresponding to this item.
	 * 
	 * @param productSku the SKU corresponding to this item
	 */
	void setProductSku(ProductSku productSku);
	
	/**
	 * @return The name of the type of product represented by this Item.
	 */
	String getProductTypeName();
	
	/**
	 * Get the quantity of this item.
	 * 
	 * @return the quantity
	 */
	int getQuantity();
    
	/**
	 * Get the currency of this item.
	 * 
	 * @return the currency
	 */
	Currency getCurrency();
	
	/** 
	 * @return the list unit price as a <code>Money</code> object.
	 */
	Money getListUnitPrice();
	
	/**
	 * @return the sale unit price as a <code>Money</code> object.  Can be null.
	 */
	Money getSaleUnitPrice();
	
	/**
	 * @return the catalog promoted price as a <code>Money</code> object.  Can be null.
	 */
	Money getPromotedUnitPrice();
	
	/**
	 * @return the lowest unit price as a <code>Money</code> object
	 * Calculated based on if the item is on sale and/or has had a catalog promotion applied
	 * Note: identical behavior as <code>PriceTier</code> object
	 * @deprecated Call getUnitPriceCalc().getAmount() instead.
	 */
	@Deprecated
	Money getLowestUnitPrice();
	
	/**
	 * Get the total amount as a <code>Money</code> object.
	 * 
	 * @return the total amount that this line item is worth: (lowest unit price * quantity) - discount.
	 */
	Money getTotal();
	
	/**
	 * Set the price details on the shopping item.
	 * 
	 * @param quantity - the new quantity
	 * @param price - the new price (contains Currency)
	 */
	void setPrice(int quantity, Price price);
	
	/**
	 * Get the Price of an item if it has been set.
	 * Note: this will not be guaranteed to to return all price tier pricing
	 * 
	 * @return the price
	 */
	Price getPrice();
	
	/**
	 * Get the discount as a <code>Money</code> object.
	 * 
	 * @return the discount that has been applied to this line item.
	 */
	Money getDiscount();
	
	/**
	 * Apply a discount to this shopping item.
	 * 
	 * @param discount - the discount amount to apply
	 */
	void applyDiscount(BigDecimal discount);
	
	/**
	 * Clear discount.
	 */
	void clearDiscount();
	
	/**
	 * Get the tax as a <code>Money</code> object.
	 * 
	 * @return the tax that has been calculated for this line item.
	 */
	Money getTax();
	
	/**
	 * Apply a discount to this shopping item.
	 * 
	 * @param tax - the tax amount to set
	 */
	void setTax(BigDecimal tax);

	/**
	 * Returns true if this {@code ShoppingItem} has other items that depend on it such that 
	 * the dependent items cannot exist on their own.
	 * 
	 * @return true if this {@code ShoppingItem} has other items that depend on it.
	 */
	boolean hasBundleItems();

	/**
	 * Specify that another {@code ShoppingItem} depends on this {@code ShoppingItem}.
	 * 
	 * @param dependentShoppingItem the other, dependent item
	 */
	void addChildItem(final ShoppingItem dependentShoppingItem);

	/**
	 * Gets this item's dependent {@code ShoppingItem}s.
	 * 
	 * @return the set of dependent {@code ShoppingItem}s.
	 */
	List<ShoppingItem> getBundleItems();

	/**
	 * Sets this item's dependent {@code ShoppingItem}s.
	 * 
	 * @param dependentShoppingItems the set of dependent items.
	 */
	void setBundleItems(final List<ShoppingItem> dependentShoppingItems);

	/**
	 * @return minimum order quantity
	 */
	int getMinOrderQty();
	
	/**
	 * Assigns {@code value} to {@code name}. Any previous value is replaced.
	 *
	 * @param name The name of the field to assign.
	 * @param value The value to assign to the field.
	 */
	void setFieldValue(final String name, final String value);
	
	/**
	 * Accesses the field for {@code name} and returns the current value. If the field has not been set
	 * then will return null.
	 *
	 * @param name The name of the field.
	 * @return The current value of the field or null.
	 */
	String getFieldValue(final String name);
	
	/**
	 * @return unmodifiable map of all key/value data field pairs
	 */
	Map<String, String> getFields();

	/**
	 * Returns the error message for this item.
	 * 
	 * @return the error message
	 */
	String getErrorMessage();

	/**
	 * Sets the error message for this item.
	 * 
	 * @param message the error message
	 */
	void setErrorMessage(final String message);
	
	/**
	 * Get the date that this was last modified on.
	 * 
	 * @return the last modified date
	 */
	Date getLastModifiedDate();
	
	/**
	 * Returns true if the Product this ShoppingItem represents is
	 * configured by the customer. i.e. this instance is dissimilar to another
	 * instance of this ShoppingItem even if they have the same SKU. For
	 * example, a gift certificate is dissimilar to any other gift certificate
	 * even though they have the same product code, product type and sku. This
	 * dissimilarity is because of customer configuration - i.e. the recipient
	 * email address for a gift certificate.
	 * 
	 * Another example of 'configurable' item is bundle.
	 *  
	 * Two configurable items with the same sku will be added as separate items,
	 * i.e. 
	 *     1 item of Camera and a bag and
	 *     1 item of Camera and a bag
	 * rather than 
	 *     2 items of Camera and a bag
	 * 
	 * @return True if the ShoppingItem is configurable.
	 */
	boolean isConfigurable();
	
	/**
	 * Sets field values.
	 * 
	 * @param itemFields item fields
	 */
	void mergeFieldValues(final Map<String, String> itemFields);

	/**
	 * Gets the ordering of the this shopping item in the tree hierarchy.
	 * 
	 * @return the ordering
	 */
	int getOrdering();

	/**
	 * Sets the ordering of this shopping item.
	 * 
	 * @param ordering the ordering
	 */
	void setOrdering(final int ordering);
	
	/**
	 * Returns flag that shows if any type of discount (e.g. price adjustment, cart/catalog promotions) can be applied to this item.
	 * If the flag is <code>false</code>, the item will not contribute to the eligibility of the cart for promotions.
	 *
	 * @return <code>true</code> if discount can be applied, false otherwise
	 */
	boolean isDiscountable();

	
	/**
	 * Returns flag that shows if the item can receive cart promotions.
	 *
	 * @return <code>true</code> if the item helps other items get promotion.
	 */
	boolean canReceiveCartPromotion();
	
	
	
	/**
	 * Returns flag that shows if this item is shippable vs. electronic
	 *
	 * @return <code>true</code> if this item is shippable, false otherwise
	 */
	boolean isShippable();
	
	/**
	 * Gets the {@link ItemPricing}.
	 * 
	 * @return {@link ItemPricing}.
	 */
	ItemPricing getLinePricing();
	
	/**
	 * Returns flag that shows if this item is a ProductBundle.
	 *
	 * @return <code>true</code> if this item is a bundle, false otherwise
	 */
	boolean isBundle();
	
	/**
	 * Accepts a ShoppingCartVisitor. If this item is a bundle then it is visited first and then each of its children accept the visitor.
	 * 
	 * @param visitor The visitor.
	 */
	void accept(ShoppingCartVisitor visitor);
	
	/**
	 * Retrieve a PriceCalculator that can be used for determining the appropriate quantity-multiplied cart item price to return.
	 * @return PriceCalculator object
	 */
	PriceCalculator getPriceCalc();


	/**
	 * Retrieve a PriceCalculator that can be used for determining the appropriate unit cart item price to return.
	 * @return PriceCalculator object
	 */
	PriceCalculator getUnitPriceCalc();

}
