/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.ajax.bean.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sfweb.ajax.bean.ShippingServiceLevelBean;

/**
 * Represents a shipping service level that has been wrapped for easy dwr conversion.
 */
public class ShippingServiceLevelBeanImpl extends AbstractEpDomainImpl implements ShippingServiceLevelBean {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private final ShippingServiceLevel shippingServiceLevel;

	private Locale locale;

	/**
	 * Wraps a <code>ShippingServiceLevel</code> object for easy localized dwr conversion.
	 * 
	 * @param shippingServiceLevel the <code>ShippingServiceLevel</code> to be wrapped.
	 * @param locale the current locale.
	 */
	public ShippingServiceLevelBeanImpl(final ShippingServiceLevel shippingServiceLevel, final Locale locale) {
		super();
		this.shippingServiceLevel = shippingServiceLevel;
		this.locale = locale;
	}

	/**
	 * Return the <code>ShippingServiceLevel</code> name for the given locale.
	 * 
	 * @return returns the localized name.
	 */
	public String getDisplayName() {
		return this.shippingServiceLevel.getDisplayName(locale, true);
	}
	
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
	public String getDisplayName(final Locale locale, final boolean fallback) {
		return this.shippingServiceLevel.getDisplayName(locale, fallback);
	}

	/**
	 * Returns the shipping cost before any discount has been applied.
	 * 
	 * @param shoppingCart the shopping cart containing items to be shipped
	 * @return the regular price shipping cost.
	 */
	// TODO: remove this method [MSC-7032]
	public Money calculateRegularPriceShippingCost(final ShoppingCart shoppingCart) {
		return this.shippingServiceLevel.calculateRegularPriceShippingCost(shoppingCart);
	}

	@Override
	public Money calculateRegularPriceShippingCost(final Collection< ? extends ShoppingItem> shoppingItems, final Currency currency) {
		return shippingServiceLevel.calculateRegularPriceShippingCost(shoppingItems, currency);
	}

	/**
	 * Return the calculated, after-promotion shipping cost.
	 * 
	 * @param shoppingCart the shopping cart containing items to be shipped
	 * @return the shipping cost.
	 */
	public Money calculateShippingCost(final ShoppingCart shoppingCart) {
		return shippingServiceLevel.calculateShippingCost(shoppingCart.getApportionedLeafItems(), shoppingCart.getCurrency());
	}

	@Override
	public Money calculateShippingCost(final Collection< ? extends ShoppingItem> shoppingItems, final Currency currency) {
		return shippingServiceLevel.calculateShippingCost(shoppingItems, currency);
	}	
	
	/**
	 * Clears any discount that has been set for this shipping service level.
	 */
	public void clearPromotions() {
		this.shippingServiceLevel.clearPromotions();
	}

	/**
	 * @return Returns the carrier.
	 */
	public String getCarrier() {
		return this.shippingServiceLevel.getCarrier();
	}

	/**
	 * Returns the <code>LocalizedProperties</code>.
	 * 
	 * @return the <code>LocalizedProperties</code>
	 */
	public LocalizedProperties getLocalizedProperties() {
		return this.shippingServiceLevel.getLocalizedProperties();
	}

	/**
	 * Return the <code>ShippingServiceLevel</code> name for the given locale.
	 * 
	 * @param locale the locale to retrieve the name.
	 * @return Returns the name.
	 */
	public String getName(final Locale locale) {
		return this.shippingServiceLevel.getDisplayName(locale, true);
	}

	/**
	 * Return the calculated before-discount shipping cost (make sure calculateRegularPriceShippingCost has been invoked already).
	 * 
	 * @return the regular price shipping cost.
	 */
	public Money getRegularPriceShippingCost() {
		return this.shippingServiceLevel.getRegularPriceShippingCost();
	}

	/**
	 * Return the calculated, after-promotion shipping cost (make sure calculateShippingCost has been invoked already). This method is intended for
	 * use when the shopping cart used in the calculation is no longer available (DWR outbound conversion)
	 * 
	 * @return the shipping cost.
	 */
	public Money getShippingCost() {
		return this.shippingServiceLevel.getShippingCost();
	}

	/**
	 * Get the shipping cost calculation method associated with this <code>ShippingServiceLevel</code>.
	 * 
	 * @return shippingCostCalculationMethod.
	 */
	public ShippingCostCalculationMethod getShippingCostCalculationMethod() {
		return this.shippingServiceLevel.getShippingCostCalculationMethod();
	}

	/**
	 * Get the shipping region associated with this <code>ShippingServiceLevel</code>.
	 * 
	 * @return the shippingRegion.
	 */
	public ShippingRegion getShippingRegion() {
		return this.shippingServiceLevel.getShippingRegion();
	}

	/**
	 * @param carrier The carrier to set.
	 */
	public void setCarrier(final String carrier) {
		this.shippingServiceLevel.setCarrier(carrier);
	}

	/**
	 * Set the shipping cost calculation method associated with this <code>ShippingServiceLevel</code>.
	 * 
	 * @param shippingCostCalculationMethod the shipping cost calculation method to be associated with this shippingServiceLevel.
	 */
	public void setShippingCostCalculationMethod(final ShippingCostCalculationMethod shippingCostCalculationMethod) {
		this.shippingServiceLevel.setShippingCostCalculationMethod(shippingCostCalculationMethod);
	}

	/**
	 * Applies a discount to this shipping service level.
	 * 
	 * @param discount the amount to be discounted
	 * @return true if the discount was applied (best price)
	 */
	public boolean setShippingDiscount(final Money discount) {
		return this.shippingServiceLevel.setShippingDiscount(discount);
	}

	/**
	 * Set the shipping region associated with this <code>ShippingServiceLevel</code>.
	 * 
	 * @param shippingRegion the shipping region to be associated with this shippingServiceLevel.
	 */
	public void setShippingRegion(final ShippingRegion shippingRegion) {
		this.shippingServiceLevel.setShippingRegion(shippingRegion);
	}

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	public String getGuid() {
		return this.shippingServiceLevel.getGuid();
	}
        
	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	public void setGuid(final String guid) {
		this.shippingServiceLevel.setGuid(guid);
	}

	/**
	 * Return the code.
	 * 
	 * @return the code.
	 */
	public String getCode() {
		return this.shippingServiceLevel.getCode();
	}

	/**
	 * Set the code.
	 * 
	 * @param code the code to set.
	 */
	public void setCode(final String code) {
		this.shippingServiceLevel.setCode(code);
	}
	
	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	public long getUidPk() {
		return this.shippingServiceLevel.getUidPk();
	}

	/**
	 * True if the object has previously been persisted.
	 * 
	 * @return true if the object has previously been persisted.
	 */
	public boolean isPersisted() {
		return this.shippingServiceLevel.isPersisted();
	}

	/**
	 * Set default values for those fields need default values.
	 */
	public void initialize() {
		this.shippingServiceLevel.initialize();
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.shippingServiceLevel.setUidPk(uidPk);
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	/**
	 * @return the store;
	 */
	public Store getStore() {
		return shippingServiceLevel.getStore();
	}

	/**
	 * @param store the store to set.
	 */
	public void setStore(final Store store) {
		shippingServiceLevel.setStore(store);
	}

	/**
	 * Default cost for the service (for Google Checkout).
	 * 
	 * @return Returns the default cost.
	 */
	public BigDecimal getDefaultCost() {
		return this.shippingServiceLevel.getDefaultCost();
	}

	/**
	 * Default cost for the service (for Google Checkout).
	 * 
	 * @param defaultCost The defaultCost to set.
	 */
	public void setDefaultCost(final BigDecimal defaultCost) {
		this.shippingServiceLevel.setDefaultCost(defaultCost);
	}

	/**
	 * This method is not supported in this implementation.
	 * @param localizedPropertiesMap unsupported
	 */
	public void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> localizedPropertiesMap) {
		throw new UnsupportedOperationException("Not designed to be called by ajax");		
	}

	/**
	 * Gets the value of whether the service level is enabled.
	 * 
	 * @return true if enabled.
	 */
	public boolean isEnabled() {
		return this.shippingServiceLevel.isEnabled();
	}

	/**
	 * Not supported.
	 * 
	 * @param enabled true if should be enabled
	 */
	public void setEnabled(final boolean enabled) {
		throw new UnsupportedOperationException("Not designed to be called by ajax");		
	}

	/**
	 * @return last modified date
	 */
	public Date getLastModifiedDate() {
		return shippingServiceLevel.getLastModifiedDate();
	}

	/**
	 * Not supported.
	 * 
	 * @param lastModifiedDate last modified date
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		throw new UnsupportedOperationException("Not designed to be called by ajax");
	}

	@Override
	public boolean isApplicable(final String storeCode, final Address shippingAddress) {
		return shippingServiceLevel.isApplicable(storeCode, shippingAddress);
	}
}

