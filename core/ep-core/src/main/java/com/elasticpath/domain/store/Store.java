package com.elasticpath.domain.store;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * <code>Store</code> represents a store made available to customers.
 */
public interface Store extends Persistable, Comparable<Store> {

	/**
	 * Gets the content encoding for the <code>Store</code>.
	 * 
	 * @return the content encoding for the <code>Store</code>
	 */
	String getContentEncoding();

	/**
	 * Sets the content encoding for the <code>Store</code>.
	 * 
	 * @param contentEncoding the content encoding for the <code>Store</code>
	 */
	void setContentEncoding(final String contentEncoding);

	/**
	 * Gets the country of this <code>Store</code>.
	 * 
	 * @return the country of this <code>Store</code>
	 */
	String getCountry();

	/**
	 * Sets the country of this <code>Store</code>.
	 * 
	 * @param country the country of this <code>Store</code>
	 */
	void setCountry(final String country);

	/**
	 * Gets whether the <code>Store</code> is CVV2 verification is enabled.
	 * 
	 * @return boolean designating whether CVV2 verification is enabled
	 */
	boolean isCreditCardCvv2Enabled();

	/**
	 * Sets whether the <code>Store</code> is CVV2 verification is enabled.
	 * 
	 * @param creditCardCvv2Enabled boolean designating whether CVV2 verification is enabled
	 */
	void setCreditCardCvv2Enabled(final boolean creditCardCvv2Enabled);
	
	/**
	 * Returns <code>true</code> if this {@link Store} saves full/unmasked
	 * credit card numbers on orders placed; <code>false</code> if it saves
	 * masked credit card numbers.
	 * 
	 * @return <code>true</code> if this {@link Store} saves full/unmasked
	 * credit card numbers on orders placed; <code>false</code> if it saves
	 * masked credit card numbers.d
	 */
	boolean isStoreFullCreditCardsEnabled();
	
	/**
	 * Sets whether this {@link Store} saves full/unmasked credit card numbers
	 * on orders placed.
	 * 
	 * @param savingCreditCardWithOrdersEnabled
	 *            <code>true</code> if this {@link Store} saves full/unmasked
	 *            credit card numbers on orders placed; <code>false</code> if
	 *            it saves masked credit card numbers.
	 */
	void setStoreFullCreditCardsEnabled(final boolean savingCreditCardWithOrdersEnabled);

	/**
	 * Gets the default currency for this <code>Store</code>.
	 * 
	 * @return the default currency for this <code>Store</code>
	 */
	Currency getDefaultCurrency();

	/**
	 * Sets the default currency for this <code>Store</code>.
	 * 
	 * @param defaultCurrency the default currency for this <code>Store</code>
	 */
	void setDefaultCurrency(final Currency defaultCurrency);

	/**
	 * Gets the default locale for this <code>Store</code>.
	 * 
	 * @return the default locale for this <code>Store</code>
	 */
	Locale getDefaultLocale();

	/**
	 * Sets the default locale for this <code>Store</code>.
	 * 
	 * @param defaultLocale the default locale for this <code>Store</code>
	 */
	void setDefaultLocale(final Locale defaultLocale);

	/**
	 * Gets the description of the <code>Store</code>.
	 * 
	 * @return the description of the <code>Store</code>
	 */
	String getDescription();

	/**
	 * Sets the description of the <code>Store</code>.
	 * 
	 * @param description the description of the <code>Store</code>
	 */
	void setDescription(final String description);

	/**
	 * Gets whether the <code>Store</code> should display out of stock items. If set to false, out of stock items will not be shown (will be
	 * "hidden").
	 * 
	 * @return boolean designating whether out of stock items are shown
	 */
	boolean isDisplayOutOfStock();

	/**
	 * Sets whether the <code>Store</code> should display out of stock items. If set to false, out of stock items will not be shown (will be
	 * "hidden").
	 * 
	 * @param displayOutOfStock boolean designating whether out of stock items are shown
	 */
	void setDisplayOutOfStock(final boolean displayOutOfStock);

	/**
	 * Gets whether this <code>Store</code> is currently enabled.
	 * 
	 * @return boolean designating that a store is live (enabled)
	 */
	boolean isEnabled();

	/**
	 * Sets whether this <code>Store</code> is currently enabled.
	 * 
	 * @param enabled boolean designating that a store is live (enabled)
	 */
	void setEnabled(final boolean enabled);

	/**
	 * Gets the name of the <code>Store</code>.
	 * 
	 * @return the name of the <code>Store</code>
	 */
	String getName();

	/**
	 * Sets the name of the <code>Store</code>.
	 * 
	 * @param name the name of the <code>Store</code>
	 */
	void setName(final String name);

	/**
	 * Gets the <code>StoreType</code> of the <code>Store</code>.
	 * 
	 * @return the <code>StoreType</code> of the <code>Store</code>
	 */
	StoreType getStoreType();

	/**
	 * Sets the <code>StoreType</code> of the <code>Store</code>.
	 * 
	 * @param storeType the <code>StoreType</code> of the <code>Store</code>
	 */
	void setStoreType(final StoreType storeType);

	/**
	 * Gets the sub-country of this <code>Store</code>.
	 * 
	 * @return the sub-country of this <code>Store</code>
	 */
	String getSubCountry();

	/**
	 * Sets the sub-country of this <code>Store</code>.
	 * 
	 * @param subCountry the sub-country of this <code>Store</code>
	 */
	void setSubCountry(final String subCountry);

	/**
	 * Gets the url of the <code>Store</code>.
	 * 
	 * @return the url of the <code>Store</code>
	 */
	String getUrl();

	/**
	 * Sets the url of the <code>Store</code>.
	 * 
	 * @param url the url of the <code>Store</code>
	 */
	void setUrl(final String url);

	/**
	 * Gets a list of <code>Warehouse</code>s that fulfill orders made through
     * this <code>Store</code>. At this time, only one Warehouse per Store is supported, so this
     * list will only contain at most one Warehouse.
	 * 
	 * @return the <code>Warehouse</code> for the <code>Store</code>, if one exists, otherwise null
	 */
	List<Warehouse> getWarehouses();
	
	/**
	 * Gets the first <code>Warehouse</code> for associated with the <code>Store</code>.
	 * 
	 * @return the first <code>Warehouse</code> for the <code>Store</code>, if one exists, otherwise null
	 */
	Warehouse getWarehouse();

	/**
	 * Sets the list of <code>Warehouse</code>s for the <code>Store</code>.
	 * 
	 * @param warehouse the <code>Warehouse</code> for the <code>Store</code>
	 */
	void setWarehouses(final List<Warehouse> warehouse);
	

	/**
	 * Gets the set of <code>CreditCardType</code>s for this <code>Store</code>.
	 * 
	 * @return the set of <code>CreditCardType</code>s for this <code>Store</code>
	 */
	Set<CreditCardType> getCreditCardTypes();

	/**
	 * Sets the set of <code>CreditCardType</code>s for this <code>Store</code>.
	 * 
	 * @param creditCardTypes the set of <code>CreditCardType</code>s for this <code>Store</code>
	 */
	void setCreditCardTypes(final Set<CreditCardType> creditCardTypes);

	/**
	 * Gets a set of tax codes for this <code>Store</code>.
	 * 
	 * @return the set of tax codes for this <code>Store</code>
	 */
	Set<TaxCode> getTaxCodes();

	/**
	 * Sets a set of tax codes for this <code>Store</code>.
	 * 
	 * @param taxCodes the set of tax codes for this <code>Store</code>
	 */
	void setTaxCodes(final Set<TaxCode> taxCodes);

	/**
	 * Gets a set of tax jurisdictions for this <code>Store</code>.
	 * 
	 * @return the tax jurisdictions for this <code>Store</code>
	 */
	Set<TaxJurisdiction> getTaxJurisdictions();

	/**
	 * Sets a set of tax jurisdictions for this <code>Store</code>.
	 * 
	 * @param taxJurisdictions the tax jurisdictions for this <code>Store</code>
	 */
	void setTaxJurisdictions(final Set<TaxJurisdiction> taxJurisdictions);

	/**
	 * Gets time time zone of the <code>Store</code>.
	 * 
	 * @return the time zone of the <code>Store</code>
	 */
	TimeZone getTimeZone();

	/**
	 * Sets time time zone of the <code>Store</code>.
	 * 
	 * @param timeZone the time zone of the <code>Store</code>
	 */
	void setTimeZone(final TimeZone timeZone);

	/**
	 * Gets a set of available payment gateways for this <code>Store</code>.
	 * 
	 * @return a set of available payment gateways for this <code>Store</code>.
	 */
	Set<PaymentGateway> getPaymentGateways();

	/**
	 * Sets a set of available payment gateways for this <code>Store</code>.
	 * 
	 * @param paymentGateways a set of available payment gateways for this <code>Store</code>.
	 */
	void setPaymentGateways(final Set<PaymentGateway> paymentGateways);

	/**
	 * Gets a map of available payment gateways for this <code>Store</code>.
	 * 
	 * @return a map of available payment gateways for this <code>Store</code>.
	 */
	Map<PaymentType, PaymentGateway> getPaymentGatewayMap();

	/**
	 * Gets the unique code associated with the <code>Store</code>.
	 * 
	 * @return the unique code associated with the <code>Store</code>
	 */
	String getCode();

	/**
	 * Sets the unique code associated with the <code>Store</code>.
	 * 
	 * @param code the unique code associated with the <code>Store</code>
	 */
	void setCode(final String code);

	/**
	 * Get this Store's Catalog.
	 * @return the catalog belonging to this Store
	 */
	Catalog getCatalog();

	/**
	 * Set this Store's Catalog.
	 * @param catalog the catalog to set
	 */
	void setCatalog(final Catalog catalog);

	 /**
	 * Gets the friendly name displayed when sending an email from this <code>Store</code>.
	 *
	 * @return the friendly name displayed when sending an email from this <code>Store</code>
	 */
	 String getEmailSenderName();
	
	 /**
	 * Sets the friendly name displayed when sending an email from this <code>Store</code>.
	 *
	 * @param emailSenderName the friendly name displayed when sending an email from this
	 * <code>Store</code>
	 */
	 void setEmailSenderName(final String emailSenderName);
	
	 /**
	 * Gets the physical email address displayed when sending an email from this
	 * <code>Store</code>.
	 *
	 * @return Gets the physical email address displayed when sending an email from this
	 * <code>Store</code>
	 */
	 String getEmailSenderAddress();
	
	 /**
	 * Sets the physical email address displayed when sending an email from this
	 * <code>Store</code>.
	 *
	 * @param emailSenderAddress Gets the physical email address displayed when sending an email
	 * from this <code>Store</code>
	 */
	 void setEmailSenderAddress(final String emailSenderAddress);

	 /**
	 * Gets the physical email address used for notifications on certain events for this
	 * <code>Store</code>.
	 *
	 * @return Gets the physical email address used for notifications on certain events for this
	 * <code>Store</code>
	 */
	 String getStoreAdminEmailAddress();
	
	 /**
	 * Sets the physical email address used for notifications on certain events for this
	 * <code>Store</code>.
	 *
	 * @param emailAddress Gets the physical email address used for notifications on certain events for this
	 * from this <code>Store</code>
	 */
	 void setStoreAdminEmailAddress(final String emailAddress);
	
	/**
	 * Get the collection of store uids associated to this store.
	 * 
	 * @return collection of uids
	 */
	Collection<Long> getAssociatedStoreUids();
	
	/**
	 * Get the unmodified collection of supported currencies for a store.  
	 * 
	 * @return the supported currencies for a store 
	 */
	Collection<Currency> getSupportedCurrencies();

	/**
	 * Set the collection of <code>Currency</code>s that are supported by this store.
	 * 
	 * @param supportedCurrencies the supportedCurrencies to set
	 * @throws DefaultValueRemovalForbiddenException if the given Currencies do not contain the default currency
	 */
	void setSupportedCurrencies(final Collection<Currency> supportedCurrencies) 
		throws DefaultValueRemovalForbiddenException;

	/**
	 * Get the unmodifiable collection of locales that are supported by a store.  
	 * 
	 * @return the locales that are supported by this store
	 */
	Collection<Locale> getSupportedLocales();

	/**
	 * Set the collection of locales that are supported by this store.
	 * 
	 * @param supportedLocales the supportedLocales to set
	 * @throws DefaultValueRemovalForbiddenException if the new locales do not contain the default locale
	 */
	void setSupportedLocales(final Collection<Locale> supportedLocales) 
		throws DefaultValueRemovalForbiddenException;
	
	/**
	 * Gets the value of storeState.
	 *
	 * @return the value of storeState.
	 */
	StoreState getStoreState();
	/**
	 * Sets the value of storeState.
	 *
	 * @param storeState the storeState to set
	 */
	void setStoreState(final StoreState storeState);
	
	/**
	 * Determines if this {@link Store} is configured with a gateway that supports the selected {@link PaymentType}.
	 * @param paymentType the payment type of the gateway
	 * @return true if a gateway is configured with the supported payment type, false otherwise.
	 */
	boolean supportsPaymentType(PaymentType paymentType);
}
