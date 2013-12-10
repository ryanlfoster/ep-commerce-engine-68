package com.elasticpath.sfweb.controller.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CouponUsageByCouponCodeComparator;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.formbean.ManageAccountFormBean;
import com.elasticpath.sfweb.tools.impl.NonPurchasableProductsRequestHelper;

/**
 * The Spring MVC controller for customer account management page.
 */
public class ManageAccountControllerImpl extends AbstractEpFormController {

	private static final Logger LOG = Logger.getLogger(ManageAccountControllerImpl.class);

	private String unauthorizedView;

	private CustomerService customerService;

	private OrderService orderService;

	private SettingsReader settingsReader;

	private CouponUsageService couponUsageService;

	private RuleService ruleService;
	
	private ProductSkuService productSkuService;

	/**
	 * Prepare the command object for the create account form.
	 * 
	 * @param request -the current request.
	 * @return the command object.
	 */
	@Override
	protected Object formBackingObject(final HttpServletRequest request) {
		LOG.debug("entering 'formBackingObject' method...");

		Customer customer;

		// retrieve customer from session
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final long customerID = customerSession.getShopper().getCustomer().getUidPk();

		// load requested customer from database
		if (customerID > 0) {
			customer = customerService.get(customerID);
			customerService.verifyCustomer(customer);
		} else {
			return new ModelAndView(getUnauthorizedView());
		}

		// template may use customer in session instead of request
		customerSession.getShopper().setCustomer(customer);

		final String storeCode = getRequestHelper().getStoreConfig().getStoreCode();
		
		LOG.debug("Loading orders started");
		final List<Order> orders = orderService.findOrdersByCustomerGuidAndStoreCode(customer.getGuid(), storeCode, false);
		LOG.debug("Loading orders done");
		
		final List<CouponUsage> customerCoupons = getCustomerCoupons(customer);
		final Map<String, String> couponNames = getCouponNames(customerCoupons, customerSession.getLocale());

		NonPurchasableProductsRequestHelper nonPurchasableProductsRequestHelper = new NonPurchasableProductsRequestHelper(productSkuService);
		nonPurchasableProductsRequestHelper.setNonPurchasableProductsInfoToRequest(request, customerSession.getShopper().getCurrentShoppingCart());
		
		return createFormBackingObject(customer, orders, customerCoupons, couponNames);
	}

	/**
	 * @param customer
	 * @param orders
	 * @param customerCoupons
	 * @param couponNames
	 * @return
	 */
	private ManageAccountFormBean createFormBackingObject(final Customer customer, final List<Order> orders,
			final List<CouponUsage> customerCoupons, final Map<String, String> couponNames) {
		final ManageAccountFormBean backingObject = getBean(ContextIdNames.MANAGE_ACCOUNT_FORM_BEAN);
		backingObject.setCustomerCoupons(customerCoupons);
		backingObject.setCouponNames(couponNames);
		backingObject.setCustomer(customer);
		backingObject.setOrders(orders);
		backingObject.setStoreCustomerCC(getSettingsReader().getSettingValue("COMMERCE/SYSTEM/storeCustomerCreditCards").getValue());
		return backingObject;
	}

	/**
	 * Get the sorted list of coupon code.
	 * 
	 * @param customer current customer
	 * @return sorted by coupon code list of {@link CouponUsage}
	 */
	protected List<CouponUsage> getCustomerCoupons(final Customer customer) {
		final Long storeUidPk = getRequestHelper().getStoreConfig().getStore().getUidPk();
		List<CouponUsage> customerCoupons = new ArrayList<CouponUsage>(couponUsageService.findAllUsagesByEmailAddress(customer.getEmail(),
				getCurrentDateTime(), storeUidPk));
		CouponUsageByCouponCodeComparator couponCodeComparator = getBean(ContextIdNames.COUPON_USAGE_CODE_COMPARATOR);
		Collections.sort(customerCoupons, couponCodeComparator);
		return customerCoupons;
	}

	/**
	 * This method for testing purpose only.
	 * 
	 * @return current date.
	 */
	Date getCurrentDateTime() {
		return new Date();
	}

	/**
	 * Get the map of coupon code and his localized name by given locale.
	 * 
	 * @param customerCoupons customer coupons.
	 * @param locale locale
	 * @return map of coupon code and his localized name.
	 */
	protected Map<String, String> getCouponNames(final List<CouponUsage> customerCoupons, final Locale locale) {
		if (customerCoupons.isEmpty()) {
			return Collections.emptyMap();
		}

		final Set<String> codes = new HashSet<String>(customerCoupons.size());
		for (CouponUsage couponUsage : customerCoupons) {
			codes.add(couponUsage.getCoupon().getCouponCode());
		}
		return getRuleService().getPromotionNamesForCouponCodes(locale, codes);
	}

	/**
	 * Handle the update coupons form submit.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @param command -the command object
	 * @param errors - will be written back in case of any business error happens
	 * @return return the view
	 * @throws EpSfWebException in case of any error happens
	 */
	@Override
	protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response, final Object command,
			final BindException errors) throws EpSfWebException {
		LOG.debug("entering 'onSubmit' method...");

		final ManageAccountFormBean formBean = (ManageAccountFormBean) command;
		for (CouponUsage couponUsage : formBean.getCustomerCoupons()) {
			couponUsageService.update(couponUsage);
			updateCart(couponUsage, request);
		}
		return createModelAndView(getSuccessView(), getCommandName(), command);
	}

	/**
	 * Create a ModelAndView. Extracted method to facilitate testing.
	 * 
	 * @param view the target view.
	 * @param commandName the command name being used.
	 * @param command the command being used.
	 * @return the ModelAndView.
	 */
	ModelAndView createModelAndView(final String view, final String commandName, final Object command) {
		return new ModelAndView(view, commandName, command);
	}

	private void updateCart(final CouponUsage couponUsage, final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		if (couponUsage.isActiveInCart()) {
			shoppingCart.applyPromotionCode(couponUsage.getCoupon().getCouponCode());
		} else {
			shoppingCart.removePromotionCode(couponUsage.getCoupon().getCouponCode());
		}
	}

	/**
	 * Sets the customer service.
	 * 
	 * @param customerService the customer service
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * Sets the order service.
	 * 
	 * @param orderService the order service.
	 */
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * Sets the unauthorized view name.
	 * 
	 * @param unauthorizedView name of the unauthorized view
	 */
	public final void setUnauthorizedView(final String unauthorizedView) {
		this.unauthorizedView = unauthorizedView;
	}

	/**
	 * Gets the unauthorized view name.
	 * 
	 * @return name of the unauthorized view
	 */
	public String getUnauthorizedView() {
		return this.unauthorizedView;
	}

	/**
	 * Get the settings reader to be used for retrieving settings.
	 * 
	 * @return the settingsReader
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * Set the settings reader to be used for retrieving settings.
	 * 
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Setter for {@link CouponUsageService}.
	 * 
	 * @param couponUsageService {@link CouponUsageService}.
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}

	/**
	 * Set rule service for show name of promo by coupon code.
	 * 
	 * @param ruleService {@link RuleService} to use.
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * @return rule service.
	 */
	protected RuleService getRuleService() {
		return ruleService;
	}

	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}
}
