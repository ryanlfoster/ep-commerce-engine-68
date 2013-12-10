/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.domain.customer.TagSetInvalidationDeterminer;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.impl.AbstractRuleEngineImpl;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;


/**
 * The default implementation of <code>CustomerSession</code>.
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class CustomerSessionImpl implements CustomerSession {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private boolean signedIn = false;

	private TagSet tagSet;

	private PriceListStack priceListStack;

	private boolean priceListStackValid = false;

	private boolean checkoutSignIn = false;

	private CustomerSessionMemento customerSessionMemento;

	private Shopper shopper;


	/**
	 * Default constructor.
	 */
	public CustomerSessionImpl() {
		setupTagSet();
	}

	// CustomerSessionTransientData interface

	@Override
	public boolean isSignedIn() {
		return signedIn;
	}

	@Override
	public void setSignedIn(final boolean signedIn) {
		this.signedIn = signedIn;
	}

	@Override
	public boolean isCheckoutSignIn() {
	    return checkoutSignIn;
	}

	@Override
	public void setCheckoutSignIn(final boolean checkoutSignIn) {
	    this.checkoutSignIn = checkoutSignIn;
	}

	@Override
	public Locale getLocale() {
		// LocaleUtils.toLocale is null-safe but will throw an IllegalArgumentException if the
		// input is not a valid Locale string.
		try {
			return LocaleUtils.toLocale(getCustomerSessionMemento().getLocaleStr());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public void setLocale(final Locale locale) {
		if (locale != null) {
			getCustomerSessionMemento().setLocaleStr(locale.toString());
		}
	}

	@Override
	public PriceListStack getPriceListStack() {
		return priceListStack;
	}

	@Override
	public void setPriceListStack(final PriceListStack priceListStack) {
		this.priceListStack = priceListStack;
		this.setPriceListStackValid(true);
	}

	@Override
	public boolean isPriceListStackValid() {
		return priceListStackValid;
	}

	@Override
	public void setPriceListStackValid(final boolean priceListStackValid) {
		this.priceListStackValid = priceListStackValid;
	}

	private void setupTagSet() {
		this.tagSet = new TagSet();
		this.tagSet.addListener(this);
	}

	@Override
	public TagSet getCustomerTagSet() {
		return tagSet;
	}
	/**
	 * @param tagSet the customer's {@link TagSet}
	 */
	public void setCustomerTagSet(final TagSet tagSet) {
		if (this.getCustomerTagSet() != null) {
			this.getCustomerTagSet().removeListener(this);
		}
		this.tagSet = tagSet;
		this.setPriceListStackValid(false);
		if (this.getCustomerTagSet() != null) {
			this.getCustomerTagSet().addListener(this);
		}
	}

	@Override
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	public void onEvent(final String key, final Tag tag) {
		TagSetInvalidationDeterminer invalidationDeterminer = ElasticPathImpl.getInstance().getBean("priceListStackInvalidator");

		if (invalidationDeterminer.needInvalidate(key)) {
			setPriceListStackValid(false);
			shopper.getCache().cacheInvalidate(AbstractRuleEngineImpl.RULE_IDS_KEY);
		}
	}

	// CustomerSession interface

	@Override
	public Date getCreationDate() {
		return customerSessionMemento.getCreationDate();
	}

	@Override
	public void setCreationDate(final Date creationDate) {
		customerSessionMemento.setCreationDate(creationDate);
	}

	@Override
	public Date getLastAccessedDate() {
		return customerSessionMemento.getLastAccessedDate();
	}

	@Override
	public void setLastAccessedDate(final Date lastAccessedDate) {
		customerSessionMemento.setLastAccessedDate(lastAccessedDate);
	}

	@Override
	public void setShopper(final Shopper shopper) {
		if (shopper == null) {
			throw new EpServiceException("Shopper should not be null.");
		}
		this.shopper = shopper;
		getCustomerSessionMemento().setShopperUid(shopper.getUidPk());
	}

	@Override
	public Shopper getShopper() {
		return shopper;
	}

	/**
	 * Get the currency of the customer corresponding to the shopping cart.
	 *
	 * @return the <code>Currency</code>
	 */
	public Currency getCurrency() {
		return customerSessionMemento.getCurrency();
	}

	/**
	 * Set the currency of the customer corresponding to the shopping cart.
	 *
	 * @param currency the <code>Currency</code>
	 */
	public void setCurrency(final Currency currency) {
		customerSessionMemento.setCurrency(currency);
		setPriceListStackValid(false);
	}

	/**
	 * Get the ipAddress of the user from the shopping cart.
	 *
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return customerSessionMemento.getIpAddress();
	}

	/**
	 * Set the users ip Address into the shopping cart.
	 *
	 * @param ipAddress the ipAddress of the user.
	 */
	public void setIpAddress(final String ipAddress) {
		customerSessionMemento.setIpAddress(ipAddress);
	}

	/**
	 * Return the guid.
	 * @return the guid.
	 */
	public String getGuid() {
		return customerSessionMemento.getGuid();
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	public void setGuid(final String guid) {
		customerSessionMemento.setGuid(guid);
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	public long getUidPk() {
		return customerSessionMemento.getUidPk();
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		customerSessionMemento.setUidPk(uidPk);
	}

	@Override
	public CustomerSessionMemento getCustomerSessionMemento() {
		return customerSessionMemento;
	}

	@Override
	public void setCustomerSessionMemento(final CustomerSessionMemento customerSessionMemento) {
		this.customerSessionMemento = customerSessionMemento;
	}

	@Override
	public ShoppingCart getShoppingCart() {
		return shopper.getCurrentShoppingCart();
	}

	@Override
	public void setShoppingCart(final ShoppingCart shoppingCart) {
		shopper.setCurrentShoppingCart(shoppingCart);
	}

}
