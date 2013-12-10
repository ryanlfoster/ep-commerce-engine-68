package com.elasticpath.sfweb.formbean;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.pricing.PriceAdjustment;

/**
 * Form bean for collecting information from the ShoppingCartController. Note that only quantity is required at present because shipping estimation
 * and code application use different mechanisms.
 */
public interface ShoppingItemFormBean extends Serializable {

	/**
	 * Adds a child form bean.
	 * 
	 * @param constituentFormBean The constituent form bean to add as a child of this form bean.
	 */
	void addConstituent(ShoppingItemFormBean constituentFormBean);

	/**
	 * Gets the list of children form beans.
	 * 
	 * @return The direct constituents sorted by constituent order.
	 */
	List<ShoppingItemFormBean> getConstituents();

	/**
	 * Gets the level of this form bean in the tree.
	 * 
	 * @return The level in the tree.
	 */
	int getLevel();

	/**
	 * Gets the path of shopping item from top most parent to itself, e.g., constituent[0].constituent[1].
	 * 
	 * @return the path.
	 */
	String getPath();

	/**
	 * Gets the price of this shopping item.
	 * 
	 * @return shopping item price
	 */
	Price getPrice();

	/**
	 * @return the price adjustment this shopping item contributes to overall price if selected.
	 */
	PriceAdjustment getPriceAdjustment();
	
	/**
	 * Gets the product that this form bean represents.
	 * 
	 * @return The product this form bean represents.
	 */
	StoreProduct getProduct();

	/**
	 * Gets the quantity that the shopping item associated with.
	 * 
	 * @return the quantity.
	 */
	int getQuantity();
	
	/**
	 * Gets if the product is dependent on a parent item.
	 * 
	 * @return if the item is dependent on a parent item.
	 */
	boolean isDependent();

	/**
	 * Gets the sku code for this shopping item.
	 * 
	 * @return The code for the selected sku for this shopping item.
	 */
	String getSkuCode();

	/**
	 * Gets the total amount that the shoppign item associated with.
	 * 
	 * @return shopping item total
	 */
	Money getTotal();

	/**
	 * Gets the shopping item uid associated with the shopping cart. 
	 * 
	 * @return The uid of the cart item that should be updated. 0 if this is an add.
	 */
	long getUpdateShoppingItemUid();

	/**
	 * Whether the bean should be updated. 
	 * 
	 * @return True if the {@code ShoppingItem} this form bean represents should be updated (subject to business rules around isConfigurable) instead
	 *         of added.
	 */
	boolean isForUpdate();

	/**
	 * Sets the level of this form bean in the tree.
	 * 
	 * @param level The level in the tree.
	 */
	void setLevel(int level);

	/**
	 * Sets the Spring reference(path).
	 * 
	 * @param path the path.
	 */
	void setPath(final String path);

	/**
	 * Sets the price associated with the shopping item.
	 * 
	 * @param price {@link Price}.
	 */
	void setPrice(final Price price);
	
	/**
	 * @param priceAdjustment the {@link PriceAdjustment} this shopping item will contribute 
	 */
	void setPriceAdjustment(final PriceAdjustment priceAdjustment);

	/**
	 * Sets the product associated with the shopping item.
	 * 
	 * @param product {@link StoreProduct}.
	 */
	void setProduct(final StoreProduct product);
	
	/**
	 * @return the product sku associated with the shopping item.
	 */
	ProductSku getProductSku();

	/**
	 * Sets the product sku associated with the shopping item.
	 * 
	 * @param productSku {@link ProductSky}.
	 */
	void setProductSku(final ProductSku productSku);

	/**
	 * Sets the quantity associated with the shipping item.
	 * 
	 * @param quantity The quantity to set.
	 */
	void setQuantity(final int quantity);
	
	
	/**
	 * Sets the if the item is dependent on a parent item.
	 * @param dependent sets if the item dependent on a parent item.
	 */
	void setDependent(final boolean dependent);

	/**
	 * Sets the sku code associated with the shopping item.
	 * 
	 * @param skuCode The code for the selected sku for this cart item.
	 */
	void setSkuCode(String skuCode);

	/**
	 * Sets the total amount associated with the shopping item.
	 * 
	 * @param total {@link Money}.
	 */
	void setTotal(final Money total);

	/**
	 * Setter for update shopping item uid.
	 * 
	 * @param shoppingItemUid The uid of the cart item that should be updated. 0 if this is an add.
	 */
	void setUpdateShoppingItemUid(final long shoppingItemUid);
	
	/**
	 * 
	 * @param selected if true then this item has been selected by the user.
	 */
	void setSelected(final boolean selected);

	/**
	 * @return true if this item has been selected by the user.
	 */
	boolean isSelected();
	
	/**
	 * @return special fields for gift certificate.
	 */
	GiftCertificateFormBean getGiftCertificateFields();
	
	/**
	 * @return whether the item is a specific SKU in a bundle
	 */
	boolean isFixedSku();
	
	/**
	 * @param fixedSku true if this item is a specific SKU in a bundle
	 */
	void setFixedSku(final boolean fixedSku);

	/**
	 * @return true if this shopping item form bean is a calculated bundle.
	 */
	boolean isCalculatedBundle();
	
	/**
	 * @param calculatedBundle whether this shopping item form bean is a calculated bundle.
	 */
	void setCalculatedBundle(boolean calculatedBundle);

	/**
	 * @return whether this shopping item form bean is a calculated bundle item,
	 * which contributes to its parent calculated price.
	 */
	boolean isCalculatedBundleItem();
	
	/**
	 * @param calculatedBundleItem whether this shopping item form bean is a calculated bundle item,
	 * which contributes to its parent calculated price.
	 */
	void setCalculatedBundleItem(boolean calculatedBundleItem);

	/**
	 * 
	 * @return selection rule of this item.
	 */
	int getSelectionRule();

	/**
	 * 
	 * @param selectionRule integer value of the selection rule.
	 */
	void setSelectionRule(final int selectionRule);

	/**
	 * @return the parent
	 */
	ShoppingItemFormBean getParent();

	/**
	 * @param parent the parent to set
	 */
	void setParent(ShoppingItemFormBean parent);
	
	/**
	 * 
	 * @return the quantity accumulated from the ancestors til root. 
	 */
	int getAccumulatedQuantity();
	
	/**
	 * Returns the minimum qty that a customer can order.
	 * 
	 * @return If not set then it returns 1.
	 */
	int getMinQty();
	
	/**
	 * Sets the minimum qty that a customer can order of this item.
	 * It takes into account a product's minimum orderable qty and it's minimum price tier.
	 * 
	 * @param qty The qty.
	 */
	void setMinQty(int qty);
	
	/**
	 * Gets the filtered sku opton values for the given locale. Does not include values for the Frequency Sku Option
	 * @param locale the locale to use.
	 * @return filtered display name
	 */
	String getFilteredSkuOptionValues(Locale locale);
}
