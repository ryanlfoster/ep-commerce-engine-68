package com.elasticpath.sfweb.formbean.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.elasticpath.sfweb.formbean.ShoppingCartFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * Default implementation of {@link ShoppingCartFormBean}.
 */
@SuppressWarnings({ "PMD.TooManyFields" })
public class ShoppingCartFormBeanImpl extends CartFormBeanImpl implements ShoppingCartFormBean {

	private static final long serialVersionUID = 1L;

	/** Specifies if the gift certificate or promotion code entered is valid. */
	private boolean codeValid = true;

	/** The list of promotion codes added to the cart and successfully applied. */
	private final List<String> appliedPromotionCodes = new ArrayList<String>();

	/** The list of promotion codes added to the cart but not applied. */
	private final List<String> notAppliedPromotionCodes = new ArrayList<String>();

	private final List<ShippingServiceLevel> shippingServiceLevelList = new ArrayList<ShippingServiceLevel>();

	private boolean isRequiredShipping;

	private Address shippingAddress;

	private Address estimateAddress;

	private Money subtotalMoney;

	private Money subtotalDiscountMoney;

	private boolean inclusiveTaxCalculationInUse;

	private boolean estimateMode;

	private Money shippingCost;

	private Money beforeTaxShippingCost;

	private Money beforeTaxTotal;

	private TaxCalculationResult taxCalculationResult;

	private final Set<GiftCertificate> appliedGiftCertificates = new HashSet<GiftCertificate>();

	private Money giftCertificateDiscountMoney;

	private Money totalMoney;

	private ViewHistory viewHistory;

	private long selectedShippingServiceLevel;

	private Locale locale;

	private Map<Quantity, FrequencyAndRecurringPrice> frequencyMap;


	@Override
	public boolean isCodeValid() {
		return codeValid;
	}

	@Override
	public void setCodeValid(final boolean codeValid) {
		this.codeValid = codeValid;
	}

	@Override
	public List<String> getAppliedPromotionCodes() {
		return appliedPromotionCodes;
	}

	@Override
	public List<String> getNotAppliedPromotionCodes() {
		return notAppliedPromotionCodes;
	}

	@Override
	public List<ShippingServiceLevel> getShippingServiceLevelList() {
		return shippingServiceLevelList;
	}

	@Override
	public boolean requiresShipping() {
		return isRequiredShipping;
	}

	@Override
	public void setRequiresShipping(final boolean requiresShipping) {
		isRequiredShipping = requiresShipping;
	}

	@Override
	public void setShippingAddress(final Address address) {
		shippingAddress = address;
	}

	@Override
	public Address getShippingAddress() {
		return shippingAddress;
	}

	/**
	 * Set the address used to estimate shipping and taxes.
	 *
	 * @param address the <code>Address</code>
	 */
	public void setEstimateAddress(final Address address) {
		this.estimateAddress = address;
	}

	/**
	 * Gets the address used to estimate shipping and taxes.
	 *
	 * @return the estimated shipping & taxes cost address
	 */
	public Address getEstimateAddress() {
		return estimateAddress;
	}

	@Override
	public Money getSubtotalMoney() {
		return subtotalMoney;
	}

	@Override
	public void setSubtotalMoney(final Money subtotalMoney) {
		this.subtotalMoney = subtotalMoney;
	}

	@Override
	public void setSubtotalDiscountMoney(final Money discountAmountMoney) {
		subtotalDiscountMoney = discountAmountMoney;
	}

	@Override
	public boolean hasSubtotalDiscount() {
		return BigDecimal.ZERO.compareTo(subtotalDiscountMoney.getAmount()) < 0;
	}

	@Override
	public Money getSubtotalDiscountMoney() {
		return subtotalDiscountMoney;
	}

	@Override
	public boolean isInclusiveTaxCalculationInUse() {
		return inclusiveTaxCalculationInUse;
	}

	@Override
	public void setInclusiveTaxCalculationInUse(final boolean inclusiveTaxCalculationInUse) {
		this.inclusiveTaxCalculationInUse = inclusiveTaxCalculationInUse;
	}

	@Override
	public boolean isEstimateMode() {
		return estimateMode;
	}

	@Override
	public void setEstimateMode(final boolean estimateMode) {
		this.estimateMode = estimateMode;
	}

	@Override
	public Money getShippingCost() {
		return shippingCost;
	}

	@Override
	public void setShippingCost(final Money shippingCost) {
		this.shippingCost = shippingCost;
	}

	@Override
	public Money getBeforeTaxShippingCost() {
		return beforeTaxShippingCost;
	}

	@Override
	public void setBeforeTaxShippingCost(final Money beforeTaxShippingCost) {
		this.beforeTaxShippingCost = beforeTaxShippingCost;
	}

	@Override
	public Money getBeforeTaxTotal() {
		return beforeTaxTotal;
	}

	@Override
	public void setBeforeTaxTotal(final Money beforeTaxTotal) {
		this.beforeTaxTotal = beforeTaxTotal;
	}

	@Override
	public TaxCalculationResult getTaxCalculationResult() {
		return taxCalculationResult;
	}

	@Override
	public void setTaxCalculationResult(final TaxCalculationResult taxCalculationResult) {
		this.taxCalculationResult = taxCalculationResult;
	}

	@Override
	public Set<GiftCertificate> getAppliedGiftCertificates() {
		return appliedGiftCertificates;
	}

	@Override
	public Money getGiftCertificateDiscountMoney() {
		return giftCertificateDiscountMoney;
	}

	@Override
	public void setGiftCertificateDiscountMoney(final Money giftCertificateDiscountMoney) {
		this.giftCertificateDiscountMoney = giftCertificateDiscountMoney;
	}

	@Override
	public Money getTotalMoney() {
		return totalMoney;
	}

	@Override
	public void setTotalMoney(final Money totalMoney) {
		this.totalMoney = totalMoney;
	}

	@Override
	public ViewHistory getViewHistory() {
		return viewHistory;
	}

	@Override
	public void setViewHistory(final ViewHistory viewHistory) {
		this.viewHistory = viewHistory;
	}

	@Override
	public long getSelectedShippingServiceLevelUid() {
		return selectedShippingServiceLevel;
	}

	@Override
	public void setSelectedShippingServiceLevelUid(final long selectedShippingServiceLevel) {
		this.selectedShippingServiceLevel = selectedShippingServiceLevel;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	@Override
	public ShoppingItemFormBean getShoppingItemFormBeanBy(final long uidPk) {
		for (final ShoppingItemFormBean bean : getCartItems()) {
			if (bean.getUpdateShoppingItemUid() == uidPk) {
				return bean;
			}
		}

		return null;
	}
	@Override
	public Map<Quantity, FrequencyAndRecurringPrice> getFrequencyMap() {

		return frequencyMap;
	}


	@Override
	public void setFrequencyMap(final Map<Quantity, FrequencyAndRecurringPrice> frequencyMap) {
		this.frequencyMap = frequencyMap;
	}


}
