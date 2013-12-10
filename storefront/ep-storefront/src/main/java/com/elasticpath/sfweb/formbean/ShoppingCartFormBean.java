package com.elasticpath.sfweb.formbean;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPrice;
import com.elasticpath.domain.shoppingcart.ViewHistory;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * Shopping cart form bean.
 */
public interface ShoppingCartFormBean extends CartFormBean {

	/**
	 * Indicates if the promotion or gift certificate code entered by the user is valid.
	 * 
	 * @return true if the code is valid
	 */
	boolean isCodeValid();

	/**
	 * Set whether or not the promotion or gift certificate code entered by the user is valid.
	 * 
	 * @param codeValid set to true if the code is valid
	 */
	void setCodeValid(final boolean codeValid);

	/**
	 * Gets the list of promotion codes applied to the cart.
	 * 
	 * @return the promotion codes
	 */
	List<String> getAppliedPromotionCodes();
	
	/**
	 * Gets the list of promotion codes not applied to the cart.
	 * 
	 * @return the promotion codes
	 */
	List<String> getNotAppliedPromotionCodes();
	
	/**
	 * Gets {@link ShoppingItemFormBean} when it is in the cart.
	 * 
	 * @param uidPk the uidPk of shopping item
	 * @return the shopping item form bean
	 */
	ShoppingItemFormBean getShoppingItemFormBeanBy(final long uidPk);

	/**
	 * Return the list of shippingServiceLevel list available based on the current shopping cart info.
	 * 
	 * @return the list of shippingServiceLevel list available based on the current shopping cart info.
	 */
	List<ShippingServiceLevel> getShippingServiceLevelList();

	/**
	 * Returns true if the cart contains items that must be shipped to the customer.
	 * 
	 * @return true if the cart contains items that must be shipped to the customer.
	 */
	boolean requiresShipping();

	/**
	 * Sets if it requires shipping.
	 * 
	 * @param requiresShipping a flag
	 */
	void setRequiresShipping(final boolean requiresShipping);

	/**
	 * Set the shipping address.
	 * 
	 * @param address the <code>Address</code>
	 */
	void setShippingAddress(final Address address);

	/**
	 * Get the shipping address.
	 * 
	 * @return the preferred shipping address
	 */
	Address getShippingAddress();

	/**
	 * Set the address used to estimate shipping and taxes.
	 *
	 * @param address the <code>Address</code>
	 */
	void setEstimateAddress(final Address address);

	/**
	 * Gets the address used to estimate shipping and taxes.
	 *
	 * @return the estimated shipping & taxes cost address
	 */
	Address getEstimateAddress();

	/**
	 * Get the subtotal of all items in the cart.
	 * 
	 * @return a <code>Money</code> object representing the subtotal
	 */
	Money getSubtotalMoney();

	/**
	 * Sets the subtotal.
	 * 
	 * @param subtotalMoney a subtotal
	 */
	void setSubtotalMoney(final Money subtotalMoney);

	/**
	 * Applies a discount to the shopping cart subtotal.
	 * 
	 * @param discountAmountMoney the amount to discount the subtotal by 
	 */
	void setSubtotalDiscountMoney(final Money discountAmountMoney);

	/**
	 * Returns true if an order subtotal discount has been applied.
	 * 
	 * @return true if an order subtotal discount has been applied
	 */
	boolean hasSubtotalDiscount();

	/**
	 * Get the amount discounted from the order subtotal.
	 * 
	 * @return the order subtotal discount as a <code>Money</code> object
	 */
	Money getSubtotalDiscountMoney();

	/**
	 * Return true if the "inclusive" tax calculation method is in use; otherwise false. This is based on the shippingAddress. If there is no
	 * taxJurisdiction set, return false by default.
	 * 
	 * @return true if the "inclusive" tax calculation method is in use; otherwise false.
	 */
	boolean isInclusiveTaxCalculationInUse();

	/**
	 * Sets the inclusive tax calculation in use.
	 * 
	 * @param inclusiveTaxCalculationInUse inclusive tax calculation
	 */
	void setInclusiveTaxCalculationInUse(final boolean inclusiveTaxCalculationInUse);

	/**
	 * Get the indicator of whether in the estimate shipping and taxes mode.
	 * 
	 * @return true when estimating shipping and taxes; otherwise, false.
	 */
	boolean isEstimateMode();

	/**
	 * Set the indicator of whether in the estimate shipping and taxes mode. Disabling estimate mode cleards the billing and shipping addresses
	 * (because they may not be full, valid addresses), but tax and shipping calculations are not cleared. If you wish to clear the estimated
	 * calculations/values, then call clearEstimates().
	 * 
	 * @param estimateMode true when estimating shipping and taxes; otherwise, false.
	 */
	void setEstimateMode(final boolean estimateMode);

	/**
	 * Return the shippingCost of the <code>ShoppingCart</code>.
	 * 
	 * @return the shippingCost of the <code>ShoppingCart</code>
	 */
	Money getShippingCost();

	/**
	 * Sets the shipping cost.
	 * 
	 * @param shippingCost the shipping cost
	 */
	void setShippingCost(final Money shippingCost);

	/**
	 * Return the before-tax shippingCost.
	 * 
	 * @return the before-tax shippingCost.
	 */
	Money getBeforeTaxShippingCost();

	/**
	 * Sets the amount before tax shipping cost.
	 * 
	 * @param beforeTaxShippingCost an amount
	 */
	void setBeforeTaxShippingCost(final Money beforeTaxShippingCost);

	/**
	 * Return the before-tax total.
	 * 
	 * @return the before-tax total.
	 */
	Money getBeforeTaxTotal();

	/**
	 * Sets the amount before tax total.
	 * 
	 * @param beforeTaxTotal an amount
	 */
	void setBeforeTaxTotal(final Money beforeTaxTotal);

	/**
	 * Retrieves the tax calculation result.
	 * 
	 * @return the current tax values
	 */
	TaxCalculationResult getTaxCalculationResult();

	/**
	 * Sets the result of tax calculation.
	 * 
	 * @param taxCalculationResult {@link TaxCalculationResult}
	 */
	void setTaxCalculationResult(final TaxCalculationResult taxCalculationResult);

	/**
	 * Get the applied gift certificate.
	 * 
	 * @return the appliedGiftCertificates
	 */
	Set<GiftCertificate> getAppliedGiftCertificates();

	/**
	 * Get the amount redeemed from gift certificate.
	 * 
	 * @return the gift certificate discount as a <code>Money</code> object
	 */
	Money getGiftCertificateDiscountMoney();

	/**
	 * Sets the gift certificate discount amount.
	 * 
	 * @param giftCertificateDiscountMoney an amount
	 */
	void setGiftCertificateDiscountMoney(final Money giftCertificateDiscountMoney);

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc.
	 * 
	 * @return a <code>Money</code> object representing the total
	 */
	Money getTotalMoney();

	/**
	 * Sets the total amount.
	 * 
	 * @param totalMoney an amount
	 */
	void setTotalMoney(final Money totalMoney);

	/**
	 * Get the View History of the user from the shopping cart.
	 * 
	 * @return the ViewHistory
	 */
	ViewHistory getViewHistory();

	/**
	 * Sets the view history.
	 * 
	 * @param viewHistory {@link ViewHistory}
	 */
	void setViewHistory(final ViewHistory viewHistory);

	/**
	 * Get the selectedShippingServiceLevel.
	 * 
	 * @return the selected ShippingServiceLevel.
	 */
	long getSelectedShippingServiceLevelUid();

	/**
	 * Sets the selected shipping service.
	 * @param selectedShippingServiceLevelUid the selected shopping service level uid.
	 */
	void setSelectedShippingServiceLevelUid(long selectedShippingServiceLevelUid);

	/**
	 * Get the locale of the customer corresponding to the shopping cart.
	 * 
	 * @return the <code>Locale</code>
	 */
	Locale getLocale();

	/**
	 * Sets the locale.
	 * 
	 * @param locale a locale
	 */
	void setLocale(final Locale locale);
	
	/**
	 *	get the frequency map.
	 * @return frequency/money  map.
	 */
	Map<Quantity, FrequencyAndRecurringPrice> getFrequencyMap();

	/**
	 * set the frequency map.
	 * @param frequencyMap  a map.
	 */
	void setFrequencyMap(final Map<Quantity, FrequencyAndRecurringPrice> frequencyMap);
}
