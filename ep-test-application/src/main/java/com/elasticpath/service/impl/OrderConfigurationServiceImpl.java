package com.elasticpath.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.testcontext.ShoppingTestData;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.CustomerAuthenticationService;
import com.elasticpath.service.CustomerOrderingService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.OrderConfigurationService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shopper.ShopperService;

/**
 * Service to help configure the order creation for <code>OrderFitFixture</code>.
 */
public class OrderConfigurationServiceImpl implements OrderConfigurationService {

	private CustomerAuthenticationService customerAuthenticationService;

	private ShopperService shopperService;

	private CustomerOrderingService customerOrderingService;

	private OrderService orderService;

	private ElasticPath elasticPath;

	private CartDirector cartDirector;

	/**
	 * Creates the shopping cart for given customer with given sku.
	 *
	 * @param store the store to create the shopping cart in. May be different than the customer store.
	 * @param customer the customer
	 * @param skuMap the sku to add to shopping cart
	 * @return new shopping cart
	 */
	public ShoppingCart createShoppingCart(final Store store, final Customer customer, final Map<ProductSku, Integer> skuMap) {
		final ShoppingTestData shoppingTestData = ShoppingTestData.getInstance();
		customerAuthenticationService.loginStore(store, customer.getEmail());
		ShoppingCart shoppingCart = shoppingTestData.getCustomerSession().getShopper().getCurrentShoppingCart();
		for (Entry<ProductSku, Integer> entry : skuMap.entrySet()) {
			cartDirector.addItemToCart(shoppingCart, new ShoppingItemDto(entry.getKey().getSkuCode(), entry.getValue()));
		}
		return shoppingCart;
	}

//	/**
//	 * Creates the customer session for given customer with given sku.
//	 *
//	 * @param store the store to create the shopping cart in. May be different than the customer store.
//	 * @param customer the customer
//	 * @param skuMap the sku to add to shopping cart
//	 * @return new shopping cart
//	 */
//	public CustomerSession createCustomerSession(final Store store, final Customer customer, final Map<ProductSku, Integer> skuMap) {
//		CustomerSession customerSession = customerAuthenticationService.handleCustomerSignIn(store, customer, null);
//		final ShoppingCart shoppingCart = customerAuthenticationService.loadShoppingCart(customerSession, store);
//		for (Entry<ProductSku, Integer> entry : skuMap.entrySet()) {
//			cartDirector.addItemToCart(shoppingCart, new ShoppingItemDto(entry.getKey().getSkuCode(), entry.getValue()));
//		}
//
//		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
//		final Shopper savedShopper = shopperService.save(shopper);
//		customerSession.setShopper(savedShopper);
//
//		shopper.setCurrentShoppingCart(shoppingCart);
//		customerSession.setShopper(shopper);
//		return customerSession;
//	}

	/**
	 * Selects the customer billing and shipping addresses for given shopping cart.
	 *
	 * @param shopper the shopper
	 * @param streetShippingAddress the street of shipping address
	 * @param streetBillingAddress the street of billing address
	 */
	public void selectCustomerAddressesToShoppingCart(final Shopper shopper, final String streetShippingAddress,
			final String streetBillingAddress) {

		ShoppingCart shoppingCartToEdit = shopper.getCurrentShoppingCart();
		selectCustomerAddressByStreet(shopper.getCustomer(), shoppingCartToEdit, streetShippingAddress,
				new CustomerAddressSelector() {
					public void selectCustomerAddress(final ShoppingCart shoppingCart, final CustomerAddress customerAddress) {
						if (shoppingCart.requiresShipping()) {
							customerOrderingService.selectShippingAddress(shopper, customerAddress);
						}
					}
				});

		selectCustomerAddressByStreet(shopper.getCustomer(), shoppingCartToEdit, streetBillingAddress,
				new CustomerAddressSelector() {
					public void selectCustomerAddress(final ShoppingCart shoppingCart, final CustomerAddress customerAddress) {
						customerOrderingService.selectBillingAddress(shopper, customerAddress);
					}
				});

	}

	private void selectCustomerAddressByStreet(final Customer customer, final ShoppingCart shoppingCart, final String streetAddress,
			final CustomerAddressSelector customerAddressSelector) {
		if (StringUtils.isEmpty(streetAddress)) {
			return;
		}

		for (CustomerAddress customerAddress : customer.getAddresses()) {
			if (streetAddress.equals(customerAddress.getStreet1())) {
				customerAddressSelector.selectCustomerAddress(shoppingCart, customerAddress);
				return;
			}
		}

		throw new EpServiceException("Selected address could not be found");
	}

	/**
	 * Selects the shipping service level in given shopping cart.
	 *
	 * @param shoppingCart the shoppign cart
	 * @param locale the default store locale
	 * @param shippingServiceLevelName the shipping service level name
	 * @return modified shopping cart
	 */
	public ShoppingCart selectShippingServiceLevel(final ShoppingCart shoppingCart, final Locale locale, final String shippingServiceLevelName) {
		if (StringUtils.isEmpty(shippingServiceLevelName)) {
			return shoppingCart;
		}

		for (ShippingServiceLevel shippingServiceLevel : shoppingCart.getShippingServiceLevelList()) {
			if (shippingServiceLevelName.equals(shippingServiceLevel.getDisplayName(locale, true))) {
				return customerOrderingService.selectShippingServiceLevel(shoppingCart, shippingServiceLevel.getUidPk());
			}
		}
		throw new EpServiceException("Selected shipping service level could not be found");
	}

	/**
	 * Creates the order payment based on customer credit card.
	 *
	 * @param customer the customer
	 * @param cardHolderName the card holder name for card that will be used for payment creation
	 * @return the order payment
	 */
	public OrderPayment createOrderPayment(final Customer customer, final String cardHolderName) {
		for (CustomerCreditCard creditCard : customer.getCreditCards()) {
			if (creditCard.getCardHolderName().equals(cardHolderName)) {
				final OrderPayment orderPayment = elasticPath.getBean(ContextIdNames.ORDER_PAYMENT);
				orderPayment.setCardType(creditCard.getCardType());
				orderPayment.setCardHolderName(creditCard.getCardHolderName());
				orderPayment.setUnencryptedCardNumber(creditCard.getUnencryptedCardNumber());
				orderPayment.setCvv2Code(creditCard.getSecurityCode());
				orderPayment.setExpiryMonth(creditCard.getExpiryMonth());
				orderPayment.setExpiryYear(creditCard.getExpiryYear());
				orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
				return orderPayment;
			}
		}

		throw new EpServiceException("Selected card holder name could not be found");
	}

	/**
	 * This interface represents methods for selection customer address for given shopping cart.
	 */
	private interface CustomerAddressSelector {

		/**
		 * Selects the customer address for given shopping cart.
		 *
		 * @param shoppingCart the shopping cart
		 * @param customerAddress the customer address to select
		 */
		void selectCustomerAddress(final ShoppingCart shoppingCart, final CustomerAddress customerAddress);
	}

	public void setCustomerAuthenticationService(final CustomerAuthenticationService customerAuthenticationService) {
		this.customerAuthenticationService = customerAuthenticationService;
	}

	public void setCustomerOrderingService(final CustomerOrderingService customerOrderingService) {
		this.customerOrderingService = customerOrderingService;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	public void setElasticPath(final ElasticPath elasticPath) {
		this.elasticPath = elasticPath;
	}

	@Override
	public List<Order> getCustomerOrders(final String customerEmail) {
		return orderService.findOrderByCustomerEmail(customerEmail, true);
	}

	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	@Override
	public void setShopperService(ShopperService shopperService) {
		this.shopperService = shopperService;
	}

	@Override
	public ShopperService getShopperService() {
		return shopperService;
	}
}
