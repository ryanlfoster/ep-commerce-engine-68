package com.elasticpath.sfweb.controller.impl;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPriceFactory;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.sfweb.controller.ShoppingCartFormBeanFactory;
import com.elasticpath.sfweb.formbean.ShoppingCartFormBean;

/**
 * Default implementation of {@link ShoppingCartFormBeanFactory}.
 */
public class ShoppingCartFormBeanFactoryImpl extends ShoppingItemFormBeanContainerFactoryImpl implements ShoppingCartFormBeanFactory {
	private CouponUsageService couponUsageService;

	/**
	 * @param couponUsageService the couponUsageService to set
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}

	/**
	 * Creates a {@link ShoppingCartFormBean} from a shopping cart.
	 *
	 * @param request the HTTP request
	 * @return {@link ShoppingCartFormBean}
	 */
	public ShoppingCartFormBean createShoppingCartFormBean(final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShopper().getCurrentShoppingCart();
		if (shoppingCart == null) {
			return null;
		}

		final ShoppingCartFormBean shoppingCartFormBean = createFrom(shoppingCart);

		mapCartItemsToFormBeans(request, shoppingCartFormBean);


		return shoppingCartFormBean;
	}

	/**
	 * Creates {@link ShoppingCartFormBean} out of a {@link ShoppingCart}.
	 *
	 * @param shoppingCart a {@link ShoppingCart}
	 * @return {@link ShoppingCartFormBean}
	 */
	ShoppingCartFormBean createFrom(final ShoppingCart shoppingCart) {
		final ShoppingCartFormBean formBean = getBeanFactory().getBean(ContextIdNames.SHOPPING_CART_FORM_BEAN);

		formBean.setCodeValid(shoppingCart.isCodeValid());
		if (!shoppingCart.isCodeValid()) {
			shoppingCart.setCodeValid(true); // need to hide this message after it has been displayed once ROXY-466
		}

		setPromotionCodesState(shoppingCart, formBean);

		formBean.getShippingServiceLevelList().addAll(shoppingCart.getShippingServiceLevelList());
		formBean.setRequiresShipping(shoppingCart.requiresShipping());
		formBean.setShippingAddress(shoppingCart.getShippingAddress());
		formBean.setSubtotalMoney(shoppingCart.getSubtotalMoney());
		formBean.setSubtotalDiscountMoney(shoppingCart.getSubtotalDiscountMoney());
		formBean.setInclusiveTaxCalculationInUse(shoppingCart.isInclusiveTaxCalculationInUse());
		formBean.setEstimateMode(shoppingCart.isEstimateMode());
		formBean.setShippingCost(shoppingCart.getShippingCost());
		formBean.setBeforeTaxShippingCost(shoppingCart.getBeforeTaxShippingCost());
		formBean.setBeforeTaxTotal(shoppingCart.getBeforeTaxTotal());
		formBean.setTaxCalculationResult(shoppingCart.getTaxCalculationResult());
		formBean.getAppliedGiftCertificates().addAll(shoppingCart.getAppliedGiftCertificates());
		formBean.setGiftCertificateDiscountMoney(shoppingCart.getGiftCertificateDiscountMoney());
		formBean.setTotalMoney(shoppingCart.getTotalMoney());
		formBean.setViewHistory(shoppingCart.getViewHistory());
		formBean.setLocale(shoppingCart.getLocale());
		formBean.setFrequencyMap(new FrequencyAndRecurringPriceFactory().getFrequencyMap(shoppingCart.getCartItems()));
		if (shoppingCart.getSelectedShippingServiceLevel() != null) {
			formBean.setSelectedShippingServiceLevelUid(shoppingCart.getSelectedShippingServiceLevel().getUidPk());
		}
		if (shoppingCart.getShippingAddress() == null) {
			formBean.setEstimateAddress(shoppingCart.getBillingAddress());
		} else {
			formBean.setEstimateAddress(shoppingCart.getShippingAddress());
		}

		return formBean;
	}

	/**
	 * Sets the state for promotion codes.
	 *
	 * Build a list of coupon codes that aren't in use by filtering out from the list of applied codes.
	 * String case needs to be ignored.
	 *
	 * @param shoppingCart the shopping cart.
	 * @param formBean the formbean.
	 */
	protected void setPromotionCodesState(final ShoppingCart shoppingCart, final ShoppingCartFormBean formBean) {
		Set<String> appliedCoupons = couponUsageService.resolveCouponsInCart(shoppingCart);
		Set<String> notAppliedCoupons = new HashSet<String>(shoppingCart.getPromotionCodes());
		for (String appliedCoupon : appliedCoupons) {
			for (String entry : shoppingCart.getPromotionCodes()) {
				if (appliedCoupon.equalsIgnoreCase(entry)) {
					notAppliedCoupons.remove(entry);
				}
			}
		}

		formBean.getAppliedPromotionCodes().addAll(appliedCoupons);
		formBean.getNotAppliedPromotionCodes().addAll(notAppliedCoupons);
	}
}
