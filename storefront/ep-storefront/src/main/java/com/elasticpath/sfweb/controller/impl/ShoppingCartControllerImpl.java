/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.exceptions.GiftCertificateCurrencyMismatchException;
import com.elasticpath.plugin.payment.exceptions.GiftCertificateZeroBalanceException;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.catalog.ProductCharacteristicsService;
import com.elasticpath.service.rules.EpRuleEngine;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.sfweb.controller.ShoppingCartFormBeanFactory;
import com.elasticpath.sfweb.formbean.ShoppingCartFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBeanContainer;

/**
 * The Spring MVC controller for the shopping cart page.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ShoppingCartControllerImpl extends AbstractEpFormController {

	private static final String UPDATE_CART_ITEM_QUANTITY = "updateCartItemQuantity";

	// Possible actions for a submit to this controller.
	private static final String APPLY_CODE = "applyCode";

	private static final String REMOVE_PROMO_CODE = "removePromoCode";

	private static final String PAY_PAL_SHORTCUT_BUTTON_X = "payPalShortcut.x";

	private static final String CART_CHECKOUT = "cartCheckout";

	// Request parameter names
	private static final String CODE = "code";

	private static final String PROMO_CODE_TO_DELETE = "promoCodeToDelete";

	private ShippingServiceLevelService shippingServiceLevelService;

	private String checkoutView;

	private String shoppingCartView;

	private String payPalShortcutView;

	private GiftCertificateService giftCertificateService;

	private CheckoutService checkoutService;

	private CartDirector cartDirector;

	private PriceLookupFacade priceLookupFacade;

	private EpRuleEngine ruleEngine;

	private RuleService ruleService;

	private static final int MAX_ASSOCIATIONS = 2;

	private ShoppingCartFormBeanFactory shoppingCartFormBeanFactory;

	private ShoppingItemAssembler shoppingItemAssembler;

	private ProductInventoryShoppingService productInventoryShoppingService;

	private ProductAssociationService productAssociationService;
	
	private ProductCharacteristicsService productCharacteristicsService;

	/**
	 * Initialize the shopping cart.<br>
	 * The view uses the session shopping cart and that's why the form backing object is not used.
	 *
	 * @param request the http request
	 * @return the command object from the super class
	 */
	@Override
	protected Object formBackingObject(final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		shoppingCart.clearEstimates();
		ShoppingCartFormBean shoppingCartFormBean = shoppingCartFormBeanFactory.createShoppingCartFormBean(request);
		// fix [MSC-4489] update shipping rates once the quantity changes
		if (!shoppingCart.getShippingServiceLevelList().isEmpty()) {
			checkoutService.retrieveShippingOption(shoppingCart);
		}
		shoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices();

		return shoppingCartFormBean;
	}

	/**
	 * Prepare the reference data map.
	 *
	 * @param request the current request.
	 * @param command form object with request parameters bound onto it
	 * @param errors validation errors holder
	 * @return reference data map.
	 */
	@Override
	protected Map<String, Object> referenceData(final HttpServletRequest request, final Object command, final Errors errors) {
		final Map<String, Object> extraParamMap = new HashMap<String, Object>();
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Shopper shopper = customerSession.getShopper();
		final ShoppingCart shoppingCart = shopper.getCurrentShoppingCart();
		final Store store = shoppingCart.getStore();
		final ShoppingCartFormBean shoppingCartFormBean = (ShoppingCartFormBean) command;

		handleMergedCart(extraParamMap, shoppingCart);
		handleNoTierOneFromWishList(extraParamMap, shoppingCart);
		extraParamMap.put(WebConstants.REQUEST_SHIPPING_COUNTRY_NAME_MAP, shippingServiceLevelService.getSortedCountriesWithShippingAllowed(
				shoppingCart.getLocale(), store));
		extraParamMap.put(WebConstants.REQUEST_SHIPPING_COUNTRY_SUBCOUNTRY_MAP, shippingServiceLevelService
				.getCountrySubCountryMapWithShippingService(shoppingCart.getLocale()));
		final Map<PaymentType, PaymentGateway> paymentGatewayMap = getRequestHelper().getStoreConfig().getStore().getPaymentGatewayMap();

		extraParamMap.put("payPalEnabled", isPayPalExpressEnabled(paymentGatewayMap, shoppingCart));
		extraParamMap.put("gcPaymentEnabled", paymentGatewayMap.containsKey(PaymentType.GIFT_CERTIFICATE));

		final Set<StoreProduct> cartProducts = getStoreProductsFromCartItems(shoppingCartFormBean.getCartItems());
		final List<ProductAssociation> cartCrossSells = productAssociationService.getProductAssociationsByType(cartProducts,
				ProductAssociation.CROSS_SELL, MAX_ASSOCIATIONS, cartProducts);
		List<Product> targetProducts = getTargetProducts(cartCrossSells);
		final Map<String, Price> cartCrossSellPrices = getPrices(shopper, targetProducts);
		final Map<String, ProductCharacteristics> cartCrossSellCharacteristics = getProductCharacteristicsService().getProductCharacteristicsMap(
				targetProducts);
		
		extraParamMap.put("cartCrossSells", cartCrossSells);
		extraParamMap.put("cartCrossSellPrices", cartCrossSellPrices);
		extraParamMap.put("cartCrossSellCharacteristics", cartCrossSellCharacteristics);
		extraParamMap.put("availabilityMap", getAvailabilityMap(shoppingCart.getCartItems(), store.getWarehouse(), store));
		extraParamMap.put("catalog", store.getCatalog());
		extraParamMap.put("warehouse", store.getWarehouse());

		// locale dependent promotion names
		extraParamMap.put("promotionCodeNames", getRuleService().getPromotionNamesForCouponCodes(shoppingCart.getLocale(),
				shoppingCart.getPromotionCodes()));

		return extraParamMap;
	}

	private boolean isPayPalExpressEnabled(final Map<PaymentType, PaymentGateway> paymentGatewayMap, final ShoppingCart shoppingCart) {
		return paymentGatewayMap.containsKey(PaymentType.PAYPAL_EXPRESS)
				&& !shoppingCart.hasRecurringPricedShoppingItems();
	}

	/**
	 * Add a merged cart flag to use on the velocity template and clear merged cart on shoppingCart.
	 *
	 * @param extraParamMap add the mergedCart flag to the parameter map
	 * @param shoppingCart the shopping cart
	 */
	protected void handleMergedCart(final Map<String, Object> extraParamMap, final ShoppingCart shoppingCart) {
		extraParamMap.put("mergedCart", shoppingCart.isMergedNotification());
		shoppingCart.setMergedNotification(false);
	}
	/**
	 * Add a flag to use on velocity template to indicate that a wishlist item was added with a quantity greater than 1.
	 * Also clear the shopping carts flag.
	 * @param extraParamMap add the noTierOne flag to the param map.
	 * @param shoppingCart the shopping cart to use.
	 */
	private void handleNoTierOneFromWishList(final Map<String, Object> extraParamMap,
			final ShoppingCart shoppingCart) {
		extraParamMap.put("NoTierOneFromWL", shoppingCart.hasItemWithNoTierOneFromWishList());
		shoppingCart.setItemWithNoTierOneFromWishList(false);
	}

	private Set<StoreProduct> getStoreProductsFromCartItems(final List<ShoppingItemFormBean> cartItems) {
		final Set<StoreProduct> cartProducts = new HashSet<StoreProduct>();
		for (ShoppingItemFormBean shoppingItemFormBean : cartItems) {
			cartProducts.add(shoppingItemFormBean.getProduct());
		}
		return cartProducts;
	}

	/**
	 * @param shopingItems list of shopping items
	 * @param warehouse the warehouse to look up
	 * @param store the store to check inventory
	 * @return the availability of shopping items from allocation service
	 */
	protected Map<Long, SkuInventoryDetails> getAvailabilityMap(final List<ShoppingItem> shopingItems, final Warehouse warehouse, final Store store) {
		final Map<Long, SkuInventoryDetails> availabilityMap = new HashMap<Long, SkuInventoryDetails>();
		for (ShoppingItem item : shopingItems) {

			final ShoppingItemDto dto = getShoppingItemAssembler().assembleShoppingItemDtoFrom(item);
			final SkuInventoryDetails skuInventoryDetails = productInventoryShoppingService.getSkuInventoryDetails(item.getProductSku(), store, dto);

			availabilityMap.put(item.getUidPk(), skuInventoryDetails);
		}
		return availabilityMap;
	}

	/**
	 * Setter for {@link ShoppingCartFormBeanFactory}.
	 *
	 * @param shoppingCartFormBeanFactory {@link ShoppingCartFormBeanFactory}.
	 */
	public void setShoppingCartFormBeanFactory(final ShoppingCartFormBeanFactory shoppingCartFormBeanFactory) {
		this.shoppingCartFormBeanFactory = shoppingCartFormBeanFactory;
	}

	/**
	 * @param associations list of product associations
	 * @return list of target products for the associations
	 */
	protected List<Product> getTargetProducts(final List<ProductAssociation> associations) {
		List<Product> targetAssociationProducts = new ArrayList<Product>();
		for (ProductAssociation association : associations) {
			targetAssociationProducts.add(association.getTargetProduct());
		}
		return targetAssociationProducts;
	}

	/**
	 * Get prices for products.<br>
	 * Apply promos on prices.
	 *
	 * @param shopper the customer session
	 * @param currency the currency
	 * @param catalog the catalog for the prices
	 * @param products the products
	 * @return list of Prices
	 */
	private Map<String, Price> getPrices(final Shopper shopper, final List<Product> products) {
		final ShoppingCart cart = shopper.getCurrentShoppingCart();
		return priceLookupFacade.getPromotedPricesForProducts(products, cart.getStore(), shopper, cart.getAppliedRules());
	}

	/**
	 * Handle the form submit.
	 *
	 * @param request -the request
	 * @param response -the response else { shoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices(); }
	 * @param command -the command object
	 * @param errors - will be written back in case of any business error happens
	 * @return return the view
	 */
	@Override
	protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response, final Object command,
			final BindException errors) {

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShopper().getCurrentShoppingCart();

		// Get the submit action and set to empty string if null
		final String action = request.getParameter("cartAction");

		// Default the view to the shopping cart view
		ModelAndView nextView = new ModelAndView(getShoppingCartView());
		if (UPDATE_CART_ITEM_QUANTITY.equals(action)) {
			final String cartItemIndex = request.getParameter("updateCartItemIndex");

			doUpdateCartItemQuantity(command, shoppingCart, Integer.parseInt(cartItemIndex));
			shoppingCart.setCodeValid(true); // need to hide this message after it has been displayed once ROXY-466

		} else if (APPLY_CODE.equals(action)) {
			final Map<PaymentType, PaymentGateway> paymentGatewayMap = getRequestHelper().getStoreConfig().getStore().getPaymentGatewayMap();
			final boolean isGCPaymentEnabled = paymentGatewayMap.containsKey(PaymentType.GIFT_CERTIFICATE);

			String code = "";
			if (request.getParameter(CODE) != null) {
				code = request.getParameter(CODE).trim();
			}
			if (isGCPaymentEnabled
					&& giftCertificateService.isGiftCertificateCodeExist(code, getRequestHelper().getStoreConfig().getStore().getUidPk())) {
				try {
					shoppingCart.applyGiftCertificate(giftCertificateService.findByGiftCertificateCode(code));
					request.getSession().removeAttribute(WebConstants.GC_ZERO_BALANCE);
					request.getSession().removeAttribute(WebConstants.GC_CURRENCY_MISMATCH);
				} catch (final GiftCertificateZeroBalanceException e) {
					request.getSession().setAttribute(WebConstants.GC_ZERO_BALANCE, true);
				} catch (final GiftCertificateCurrencyMismatchException e) {
					request.getSession().setAttribute(WebConstants.GC_CURRENCY_MISMATCH, true);
				}

			} else if (shoppingCart.applyPromotionCode(code)) {
				shoppingCart.setCodeValid(true);
			} else {
				shoppingCart.setCodeValid(false);
			}
		} else if (REMOVE_PROMO_CODE.equals(action)) {
			final String promotionCode = request.getParameter(PROMO_CODE_TO_DELETE);
			shoppingCart.removePromotionCode(promotionCode);
			shoppingCart.setCodeValid(true); // need to hide this message after it has been displayed once ROXY-466
		} else if (request.getParameter(PAY_PAL_SHORTCUT_BUTTON_X) != null && shoppingCart.getNumItems() > 0) {
			nextView = new ModelAndView(getPayPalShortcutView());
			shoppingCart.setCodeValid(true); // need to hide this message after it has been displayed once ROXY-466
		} else if (CART_CHECKOUT.equals(action) && shoppingCart.getNumItems() > 0) {
			request.getSession().setAttribute(WebConstants.IS_CHECKOUT_SIGN_IN, "true");

			customerSession.setCheckoutSignIn(true);

			shoppingCart.setEstimateMode(false);
			// We're doing a normal checkout so we need to clear out any PayPal session that may have been set previously.
			request.getSession().removeAttribute(WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION);

			nextView = new ModelAndView(getCheckoutView());
			shoppingCart.setCodeValid(true); // need to hide this message after it has been displayed once ROXY-466
		}

		final ShoppingCart updatedShoppingCart = cartDirector.saveShoppingCart(shoppingCart);
		updatedShoppingCart.setShopper(customerSession.getShopper());
		customerSession.getShopper().setCurrentShoppingCart(updatedShoppingCart);

		return nextView;
	}

	/**
	 * Called internally by onSubmit when the quantity of a cart item should be updated.
	 *
	 * @param command The command object. Must be type CartUpdateFormBean.
	 * @param shoppingCart The shoppingCart.
	 * @param cartItemIndex index of the cart item in shoppingcart
	 */
	void doUpdateCartItemQuantity(final Object command, final ShoppingCart shoppingCart, final int cartItemIndex) {
		// The command object (form bean) was setup with a static copy of the cart.
		// Possibly, the cart has been changed by something else in between times
		// so we find the cartItems in the cart by guid and then asking the
		// CartDirector to update each one.

		// Note that we are doing the check against the command object here because
		// the command object is a UI concern.

		final ShoppingItemFormBeanContainer cartFormBean = (ShoppingItemFormBeanContainer) command;
		final ShoppingItemFormBean itemFormBean = cartFormBean.getCartItems().get(cartItemIndex);
		final ShoppingItem shoppingItem = shoppingCart.getCartItemById(itemFormBean.getUpdateShoppingItemUid());

		if (shoppingItem != null) {
			ShoppingItemDto dto = getShoppingItemAssembler().assembleShoppingItemDtoFrom(shoppingItem);
			dto.setQuantity(itemFormBean.getQuantity());
			cartDirector.updateCartItem(shoppingCart, itemFormBean.getUpdateShoppingItemUid(), dto);
		}
	}

	/**
	 * Set the checkout view.
	 *
	 * @param checkoutView the checkout view
	 */
	public void setCheckoutView(final String checkoutView) {
		this.checkoutView = checkoutView;
	}

	/**
	 * Get the Shopping Cart View view name.
	 *
	 * @return the Shopping Cart View
	 */
	public String getShoppingCartView() {
		return this.shoppingCartView;
	}

	/**
	 * Set the checkout view.
	 *
	 * @param shoppingCartView the ShoppingCart View
	 */
	public void setShoppingCartView(final String shoppingCartView) {
		this.shoppingCartView = shoppingCartView;
	}

	/**
	 * Get the checkout view name.
	 *
	 * @return the checkout view
	 */
	public String getCheckoutView() {
		return this.checkoutView;
	}

	/**
	 * Set the <code>ShippingServiceLevelService</code> singleton to retrieve the country/subcountry map with shipping service configured.
	 *
	 * @param shippingServiceLevelService the shippingServiceLevelService instance
	 */
	public void setShippingServiceLevelService(final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

	/**
	 * @return the giftCertificateService
	 */
	public GiftCertificateService getGiftCertificateService() {
		return giftCertificateService;
	}

	/**
	 * @param giftCertificateService the giftCertificateService to set
	 */
	public void setGiftCertificateService(final GiftCertificateService giftCertificateService) {
		this.giftCertificateService = giftCertificateService;
	}

	/**
	 * @return the payPalShortcutView
	 */
	public String getPayPalShortcutView() {
		return payPalShortcutView;
	}

	/**
	 * @param payPalShortcutView the payPalShortcutView to set
	 */
	public void setPayPalShortcutView(final String payPalShortcutView) {
		this.payPalShortcutView = payPalShortcutView;
	}

	/**
	 * Set the checkout service.
	 *
	 * @param checkoutService the checkout service to set.
	 */
	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

	/**
	 * @param cartDirector Sets the cart director.
	 */
	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	/**
	 * @param ruleEngine the ruleEngine to set
	 */
	public void setRuleEngine(final EpRuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}

	/**
	 * @return the ruleEngine
	 */
	public EpRuleEngine getRuleEngine() {
		return ruleEngine;
	}

	/**
	 * @return rule service
	 */
	public RuleService getRuleService() {
		return ruleService;
	}

	/**
	 * @param ruleService rule service
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * @param priceLookupFacade the priceLookupFacade to set
	 */
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}

	/**
	 * @param shoppingItemAssembler the shoppingItemAssembler to set
	 */
	public void setShoppingItemAssembler(final ShoppingItemAssembler shoppingItemAssembler) {
		this.shoppingItemAssembler = shoppingItemAssembler;
	}

	/**
	 * @return the shoppingItemAssembler
	 */
	public ShoppingItemAssembler getShoppingItemAssembler() {
		return shoppingItemAssembler;
	}

	/**
	 * @param productInventoryShoppingService the {@link ProductInventoryShoppingService}
	 */
	public void setProductInventoryShoppingService(final ProductInventoryShoppingService productInventoryShoppingService) {
		this.productInventoryShoppingService = productInventoryShoppingService;
	}

	/**
	 * @param productAssociationService the {@link ProductAssociationService}
	 */
	@Required
	public void setProductAssociationService(final ProductAssociationService productAssociationService) {
		this.productAssociationService = productAssociationService;
	}

	protected ProductCharacteristicsService getProductCharacteristicsService() {
		return productCharacteristicsService;
	}

	public void setProductCharacteristicsService(final ProductCharacteristicsService productCharacteristicsService) {
		this.productCharacteristicsService = productCharacteristicsService;
	}

}
