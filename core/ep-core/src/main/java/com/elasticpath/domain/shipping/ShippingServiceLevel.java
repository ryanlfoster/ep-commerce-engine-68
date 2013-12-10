/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.exception.SCCMCurrencyMissingException;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.Entity;

/**
 * A ShippingServiceLevel represents a shipping option associated with a shipping region.
 */
public interface ShippingServiceLevel extends Entity {

	/**
	 * The name of localized property -- name.
	 */
	String LOCALIZED_PROPERTY_NAME = "shippingServiceLevelDisplayName";

	/**
	 * Get the shipping region associated with this <code>ShippingServiceLevel</code>.
	 * 
	 * @return the shippingRegion.
	 */
	ShippingRegion getShippingRegion();

	/**
	 * Set the shipping region associated with this <code>ShippingServiceLevel</code>.
	 * 
	 * @param shippingRegion the shipping region to be associated with this shippingServiceLevel.
	 */
	void setShippingRegion(final ShippingRegion shippingRegion);

	/**
	 * Get the shipping cost calculation method associated with this <code>ShippingServiceLevel</code>.
	 * 
	 * @return shippingCostCalculationMethod.
	 */
	ShippingCostCalculationMethod getShippingCostCalculationMethod();

	/**
	 * Set the shipping cost calculation method associated with this <code>ShippingServiceLevel</code>.
	 * 
	 * @param shippingCostCalculationMethod the shipping cost calculation method to be associated with this shippingServiceLevel.
	 */
	void setShippingCostCalculationMethod(final ShippingCostCalculationMethod shippingCostCalculationMethod);

	/**
	 * Returns the <code>LocalizedProperties</code>.
	 * 
	 * @return the <code>LocalizedProperties</code>
	 */
	LocalizedProperties getLocalizedProperties();
	
	/**
	 * Set the localized properties map.
	 * 
	 * @param localizedPropertiesMap the map
	 */
	void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> localizedPropertiesMap);

	/**
	 * Return the calculated, after-promotion shipping cost.
	 * 
	 * @param shoppingCart the shopping cart containing items to be shipped
	 * @return the shipping cost.
	 * @throws SCCMCurrencyMissingException if the calculation method can not find a matching param value for currency.
	 */
	Money calculateShippingCost(final ShoppingCart shoppingCart)
		throws SCCMCurrencyMissingException;

	/**
	 * Return the calculated, after-promotion shipping cost.
	 * 	 
	 * @param shoppingItems items to calculation shipping cost for
	 * @param currency the currency
	 * @return the shipping cost.
	 * @throws SCCMCurrencyMissingException if the calculation method can not find a matching param value for currency.
	 */
	Money calculateShippingCost(final Collection< ? extends ShoppingItem> shoppingItems, final Currency currency) 
		throws SCCMCurrencyMissingException;
	
	
	/**
	 * Return the calculated, after-promotion shipping cost (make sure calculateShippingCost has been invoked already). This method is intended for
	 * use when the shopping cart used in the calculation is no longer available (DWR outbound conversion)
	 * 
	 * @return the shipping cost.
	 */
	Money getShippingCost();

	/**
	 * Calculate and return the shipping cost before any discount has been applied.
	 * 
	 * @param shoppingCart the shopping cart containing items to be shipped
	 * @return the regular price shipping cost.
	 * @throws SCCMCurrencyMissingException if the calculation method can not find a matching param value for currency.
	 * @deprecated Should call calculateRegularPriceShippingCost(lineItems, currency) instead.
	 */
	// TODO: remove this method [MSC-7032]
	@Deprecated
	Money calculateRegularPriceShippingCost(final ShoppingCart shoppingCart)
		throws SCCMCurrencyMissingException;	

	/**
	 * Calculate and return the shipping cost before any discount has been applied.
	 * 
	 * @param shoppingItems the line items to calculate the shipping cost of
	 * @param currency the currency
	 * @return the regular price shipping cost.
	 * @throws SCCMCurrencyMissingException if the calculation method can not find a matching param value for currency.
	 */
	Money calculateRegularPriceShippingCost(final Collection< ? extends ShoppingItem> shoppingItems, final Currency currency)
		throws SCCMCurrencyMissingException;

	/**
	 * Return the calculated before-discount shipping cost (make sure calculateRegularPriceShippingCost has been invoked already).
	 * 
	 * @return the regular price shipping cost.
	 */
	Money getRegularPriceShippingCost();

	/**
	 * Clears any discount that has been set for this shipping service level.
	 */
	void clearPromotions();

	/**
	 * Applies a discount to this shipping service level.
	 * 
	 * @param discount the amount to be discounted
	 * @return true if the shipping discount is applied (best price)
	 */
	boolean setShippingDiscount(final Money discount);

	/**
	 * @return Returns the carrier.
	 */
	String getCarrier();

	/**
	 * @param carrier The carrier to set.
	 */
	void setCarrier(final String carrier);
	
	/**
	 * Returns the shipping service level code.
	 *
	 * @return the shipping service level code
	 */
	String getCode();

	/**
	 * Sets the shipping service level code.
	 *
	 * @param code the shipping service level code
	 */
	void setCode(final String code);

	/**
	 * Return the <code>ShippingServiceLevel</code> name for the given locale.
	 * Falls back to the Store's default locale if not found for the given locale.
	 * 
	 * @param locale the locale for which to retrieve the name
	 * @return The name of the ShippingServiceLevel
	 * @deprecated call getDisplayName(Locale, boolean) instead
	 */
	@Deprecated
	String getName(final Locale locale);

	/**
	 * @return Returns the store.
	 */
	Store getStore();

	/**
	 * @param store The store to set.
	 */
	void setStore(final Store store);

	/**
	 * Default cost for the service (for Google Checkout).
	 * @return Returns the default cost.
	 */
	BigDecimal getDefaultCost();

	/**
	 * Default cost for the service (for Google Checkout).
	 * @param defaultCost The defaultCost to set.
	 */
	void setDefaultCost(final BigDecimal defaultCost);
	
	/**
	 * Get the DisplayName for this ShippingServiceLevel in the
	 * given Locale, falling back to the Store's default locale
	 * if the ShippingServicelevel has no DisplayName in the given locale.
	 * 
	 * @param locale the locale for which the DisplayName should be returned
	 * @param fallback whether the display name should be returned for the Store's
	 * default locale if the name for the given locale is not found.
	 * @return the display name for the given locale, or the fallback locale if requested,
	 * or null if none can be found.
	 */
	String getDisplayName(Locale locale, boolean fallback);
	
	/**
	 * Checks if the service level is enabled.
	 * 
	 * @return true if the service level is active
	 */
	boolean isEnabled();
	
	/**
	 * Sets the service level enabled/disabled.
	 * 
	 * @param enabled the boolean value
	 */
	void setEnabled(boolean enabled);

	/**
	 * @return the date when this shipping service level was last modified
	 */
	Date getLastModifiedDate();

	/**
	 * @param lastModifiedDate the date when this shipping service level was last modified
	 */
	void setLastModifiedDate(final Date lastModifiedDate);

	/**
	 * This is applicable only if it is enabled, matches the given store code, and the given shipping address is in this region.
	 * @param storeCode the store code
	 * @param shippingAddress the shipping address
	 * @return true iff it is applicable
	 */
	boolean isApplicable(String storeCode, Address shippingAddress);
}
