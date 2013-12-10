package com.elasticpath.sfweb.ajax.bean;

import java.math.BigDecimal;
import java.util.List;

import com.elasticpath.sfweb.ajax.bean.impl.AggregatedPrice;

/**
 * Represents a json bundle item.
 * 
 * <table style="border: 1;">
 * <tr>
 * <td>isCalculatedBundle</td>
 * <td>isCalculatedBundleItem</td>
 * <td>description</td>
 * </tr>		
 * <tr>
 * <td>true</td>
 * <td>true</td>
 * <td>This is a nested calculated bundle.</td>
 * </tr>		
 * <tr>
 * <td>true</td>
 * <td>false</td>
 * <td>This is the top level calculated bundle.</td>
 * </tr>		
 * <tr>
 * <td>false</td>
 * <td>true</td>
 * <td>This is a simple calculated bundle item.</td>
 * </tr>		
 * <tr>
 * <td>false</td>
 * <td>false</td>
 * <td>This is NOT calculated bundle or item, it's an assigned bundlt or item.</td>
 * </tr>
 * 
 * </table>
 * 
 * @author mren
 *
 */
public interface JsonBundleItemBean {

	/**
	 * Returns the product code for the related product.
	 * @return the product code.
	 */
	String getProductCode();

	/**
	 * 
	 * @param productCode The product code to set.
	 */
	void setProductCode(final String productCode);

	/**	
	 * 
	 * @param child The JsonBundleItemDto to be added.
	 */
	void addConstituent(final JsonBundleItemBean child);

	/**
	 * Gets all constituents.
	 * 
	 * @return list of children.
	 */
	List<JsonBundleItemBean> getConstituents();

	/**
	 * Gets all constituents.
	 * @param constituents to be set.
	 */
	void setConstituents(List<JsonBundleItemBean> constituents);

	/**
	 * @return the SKU code
	 */
	String getSkuCode();

	/**
	 * @param skuCode The skuCode to set
	 */
	void setSkuCode(final String skuCode);

	/**
	 * @param quantity The quantity to set.
	 */
	void setQuantity(final int quantity);

	/**
	 * @return The quantity.
	 */
	int getQuantity();

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
	 * 
	 * @return selection rule
	 */
	int getSelectionRule();

	/**
	 * Set selection rule.
	 * @param selectionRule the selection rule to set.
	 */
	void setSelectionRule(final int selectionRule);

	/**
	 * 
	 * @return effective price of this item, according to the sku and quantity. 
	 */
	BigDecimal getPrice();

	/**
	 * 
	 * @param price price to be set.
	 */
	void setPrice(final BigDecimal price);

	/**
	 * 
	 * @return path
	 */
	String getPath();

	/**
	 * 
	 * @param path path of this item.
	 */
	void setPath(final String path);

	/**
	 * 
	 * @return whether this is a calculated bundle.
	 */
	boolean isCalculatedBundle();

	/**
	 * 
	 * @param calculatedBundle whether this is calculated bundle
	 */
	void setCalculatedBundle(final boolean calculatedBundle);

	/**
	 * 
	 * @return true if parent is a calculated bundle.
	 */
	boolean isCalculatedBundleItem();

	/**
	 * 
	 * @param calculatedItem whether is calculated item.
	 */
	void setCalculatedBundleItem(final boolean calculatedItem);
	
	/**
	 * 
	 * @return price tiers of this item.
	 */
	List<PriceTierBean> getPriceTiers();

	/**
	 * 
	 * @param priceTiers price tiers to be set.
	 */
	void setPriceTiers(List<PriceTierBean> priceTiers);

	/**
	 * 
	 * @return price adjustment of this item.
	 */
	BigDecimal getPriceAdjustment();
	
	/**
	 * 
	 * @param adjustmentAmount BigDecimal price adjustment of this item.
	 */
	void setPriceAdjustment(final BigDecimal adjustmentAmount);
	
	
	/**
	 * The value of recurring price, e.g. 5.
	 * @return the recurringPrice
	 */
	BigDecimal getRecurringPrice();

	/**
	 * @param recurringPrice the recurringPrice to set
	 */
	void setRecurringPrice(BigDecimal recurringPrice);

	/**
	 * The display text of the payment schedule, e.g. "per week".
	 * @return the paymentSchedule
	 */
	String getPaymentSchedule();

	/**
	 * @param paymentSchedule the paymentSchedule to set
	 */
	void setPaymentSchedule(String paymentSchedule);

	/**
	 * 
	 * @return Recurring Price Tiers.
	 */
	List<PriceTierBean> getRecurringPriceTiers();
	
	/**
	 * 
	 * @param priceTiers to set.
	 */
	void setRecurringPriceTiers(List<PriceTierBean> priceTiers);

	/**
	 * 
	 * @return List<Pair<Integer, String>>
	 */
	List<AggregatedPrice> getAggregatedPrices();
	
	/**
	 * 
	 * @param aggregatedPrices to set.
	 */
	void setAggregatedPrices(List<AggregatedPrice> aggregatedPrices);
}