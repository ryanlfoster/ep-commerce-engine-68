package com.elasticpath.test.persister.testscenarios.payment;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.event.EventOriginatorType;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Wrapper around order. Wraps real-world methods like releaseShipment.
 */
public class OrderHandler {

	private static final String CURRENCY_CODE = "USD";

	private static final String ADMIN = "admin";

	/**
	 * The cost will be applied for the whole shopping cart and for the particular shipment.
	 */
	public static final BigDecimal SHIPPING_COST = BigDecimal.valueOf(10d);

	/** Default GC balance $50 */
	public static final BigDecimal GC_BALANCE = BigDecimal.valueOf(50d);

	private final Product product;

	private final CheckoutService checkoutService;

	private final OrderService orderService;

	private final PaymentService paymentService;

	private final GiftCertificateService giftCertificateService;
	
	private final CustomerSessionService customerSessionService;

	private Order order;

	final private ElasticPath elasticPath;

	final private TestDataPersisterFactory persisterContainer;

	private final SimpleStoreScenario scenario;

	/**
	 * Creates the handler, creates all required services. Real order is not created.
	 * 
	 * @param scenario2
	 * @throws Exception in case of any errors.
	 */
	public OrderHandler(final ElasticPath elasticPath, final TestDataPersisterFactory persisterFactory, final SimpleStoreScenario scenario)
			throws Exception {
		this.elasticPath = elasticPath;
		this.persisterContainer = persisterFactory;
		checkoutService = elasticPath.getBean("checkoutService");
		this.scenario = scenario;

		product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(),
				scenario.getWarehouse());

		scenario.getStore().setPaymentGateways(setUpPaymentGatewayAndProperties());

		orderService = elasticPath.getBean(ContextIdNames.ORDER_SERVICE);

		paymentService = elasticPath.getBean(ContextIdNames.PAYMENT_SERVICE);

		giftCertificateService = elasticPath.getBean("giftCertificateService");
		
		customerSessionService = elasticPath.getBean("customerSessionService");
	}

	private Set<PaymentGateway> setUpPaymentGatewayAndProperties() {

		final Set<PaymentGateway> gateways = new HashSet<PaymentGateway>();

		gateways.add(persisterContainer.getStoreTestPersister().persistDefaultPaymentGateway());
		gateways.add(persisterContainer.getStoreTestPersister().persistGiftCertificatePaymentGateway());

		return gateways;
	}

	/**
	 * Create order using provided list of shopping cart items, template conventional payment and optionally array of template gift certificates
	 * payments. Checkout service is used to create the order.
	 * 
	 * @param shoppingCartItems shopping cart items
	 * @param conventionalPayment conventionalPayment
	 * @param giftCertificates array of gift certificates
	 */
	public void createOrderByConventionalPaymentAndGCs(final List<ShoppingItem> shoppingCartItems, final OrderPayment conventionalPayment,
			final GiftCertificate... giftCertificates) {
		if (order != null) {
			throw new EpServiceException("Order has already been created.");
		}

		// construct and save new shopping cart
		final CustomerSession customerSession = createCustomerSessionWithShoppingCart(shoppingCartItems, giftCertificates);
		final Shopper shopper = customerSession.getShopper();
		final ShoppingCart shoppingCart = shopper.getCurrentShoppingCart();

		checkoutService.checkout(shoppingCart, conventionalPayment);

		order = orderService.findOrderByOrderNumber(shoppingCart.getCompletedOrder().getOrderNumber());
	}

	/**
	 * Create order using provided list of shopping cart items and array of template gift certificates payments. Checkout service is used to create
	 * the order.
	 * 
	 * @param shoppingCartItems shopping cart items
	 * @param giftCertificates array of gift certificates
	 */
	public void createOrderByGC(final List<ShoppingItem> shoppingCartItems, final GiftCertificate... giftCertificates) {
		createOrderByConventionalPaymentAndGCs(shoppingCartItems, null, giftCertificates);
	}

	/**
	 * Create order using provided list of shopping cart items, template conventional payment. Checkout service is used to create the order.
	 * 
	 * @param shoppingCartItems shopping cart items
	 * @param conventionalPayment conventionalPayment
	 */
	public void createOrderByConventionalPayment(final List<ShoppingItem> shoppingCartItems, final OrderPayment conventionalPayment) {
		createOrderByConventionalPaymentAndGCs(shoppingCartItems, conventionalPayment, (GiftCertificate[]) null);
	}

	/**
	 * Saves the order to the DB then clears order and and gets it back from the DB. This is done to prevent using in-memory objects.
	 */
	public void saveOrder() {
		checkOrder();
		orderService.update(order);

		String orderNumber = order.getOrderNumber();
		order = null;
		order = orderService.findOrderByOrderNumber(orderNumber);
	}

	/**
	 * Gets fresh order from DB to prevent in-memory cashing issues.
	 */
	public void refreshOrder() {
		checkOrder();
		String orderNumber = order.getOrderNumber();
		order = null;
		order = orderService.findOrderByOrderNumber(orderNumber);
	}

	/**
	 * Increases sku quantity of the specified shipment.
	 * 
	 * @param shipmentNumber the shipment to be increased.
	 * @param plus number of skus to be advanced
	 * @param templatePayment template order payment to be used for auth adjustment.
	 * @return PaymentResult
	 */
	public PaymentResult increaseShipment(final int shipmentNumber, final int plus, final OrderPayment templatePayment) {
		checkOrder();
		OrderShipment orderShipment = getOrderShipment(shipmentNumber);
		Set<OrderSku> skus = orderShipment.getShipmentOrderSkus();
		if (skus != null && !skus.isEmpty()) {
			OrderSku orderSku = skus.iterator().next();
			orderSku.setPrice(orderSku.getQuantity() + plus, orderSku.getPrice());
		}
		PaymentResult paymentResult = paymentService.adjustShipmentPayment(orderShipment, templatePayment);
		if (paymentResult.getResultCode() == PaymentResult.CODE_FAILED) {
			refreshOrder();
		} else {
			saveOrder();
		}
		sleep();
		return paymentResult;
	}

	/**
	 * Decreases sku quantity of the specified shipment.
	 * 
	 * @param shipmentNumber the shipment to be decreased.
	 * @param minus number of skus to be subtracted from the shipment.
	 * @return PaymentResult
	 */
	public PaymentResult decreaseShipment(final int shipmentNumber, final int minus) {
		checkOrder();
		OrderShipment orderShipment = getOrderShipment(shipmentNumber);
		Set<OrderSku> skus = orderShipment.getShipmentOrderSkus();
		if (skus != null && !skus.isEmpty()) {
			OrderSku orderSku = skus.iterator().next();
			orderSku.setPrice(orderSku.getQuantity() - minus, orderSku.getPrice());
		}
		PaymentResult paymentResult = paymentService.adjustShipmentPayment(orderShipment);
		if (paymentResult != null && paymentResult.getResultCode() == PaymentResult.CODE_FAILED) {
			refreshOrder();
		} else {
			saveOrder();
		}
		return paymentResult;
	}

	/**
	 * Adds new physical shipment to the order.
	 * 
	 * @param orderSkus list of skus which constitute the shipment.
	 * @param templatePayment template order payment to be used for authorization.
	 * @return PaymentResult
	 */
	public PaymentResult addPhysicalShipment(final List<OrderSku> orderSkus, final OrderPayment templatePayment) {
		checkOrder();
		OrderShipment orderShipment = getOrderShipment(1); // get default
		// shipment

		if (!(orderShipment instanceof PhysicalOrderShipment)) {
			return null;
		}

		PhysicalOrderShipment newShipment = elasticPath.getBean(ContextIdNames.PHYSICAL_ORDER_SHIPMENT);

		PhysicalOrderShipment physicalOrderShipment = (PhysicalOrderShipment) orderShipment;

		/** populate shipment with the details */
		newShipment.setShipmentAddress(physicalOrderShipment.getShipmentAddress());
		newShipment.setShippingServiceLevelGuid(physicalOrderShipment.getShippingServiceLevelGuid());
		newShipment.setServiceLevel(physicalOrderShipment.getServiceLevel());
		newShipment.setCarrier(physicalOrderShipment.getCarrier());
		newShipment.setShippingCost(SHIPPING_COST);
		newShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
		newShipment.setCreatedDate(new Date());

		/** add skus */
		for (OrderSku orderSku : orderSkus) {
			newShipment.addShipmentOrderSku(orderSku);
		}

		order.addShipment(newShipment);

		/** initialize new shipment. */
		PaymentResult paymentResult = paymentService.initializeNewShipmentPayment(newShipment, templatePayment);
		if (paymentResult.getResultCode() == PaymentResult.CODE_FAILED) {
			refreshOrder();
		} else {
			saveOrder();
		}
		sleep();
		return paymentResult;
	}

	/**
	 * Processes release of the specified shipment.
	 * 
	 * @param shipmentNumber shipment's number to be released.
	 */
	public void releaseShipment(final int shipmentNumber) {
		checkOrder();
		applyOriginator();
		OrderShipment orderShipment = getOrderShipment(shipmentNumber);
		orderService.processReleaseShipment(orderShipment);
		refreshOrder();
	}

	/**
	 * Processes completion of the specified shipment.
	 * 
	 * @param shipmentNumber shipment's number to be completed.
	 */
	public void completeShipment(final int shipmentNumber) {
		checkOrder();
		OrderShipment orderShipment = getOrderShipment(shipmentNumber);
		orderService.completeShipment(orderShipment.getShipmentNumber(), "none", true, null, true, ((EventOriginatorHelper) elasticPath
				.getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER)).getSystemOriginator());
		saveOrder();
	}

	/**
	 * Processes order cancellation.
	 */
	public void cancelOrder() {
		checkOrder();
		paymentService.cancelOrderPayments(order);
		saveOrder();
	}

	/**
	 * Wipes out underlying order. The orderhandler can and should be reused then, just create new order.
	 */
	public void clear() {
		order = null;
	}

	/**
	 * Creates the gift certificate for the default amount of $50.
	 * 
	 * @return GiftCertificate
	 */
	public GiftCertificate createGiftCertificate() {
		return createGiftCertificates(GC_BALANCE);
	}

	/**
	 * Creates the gift certificate of the specified balance.
	 * 
	 * @param preferred gc balance.
	 * @return GiftCertificate
	 */
	public GiftCertificate createGiftCertificates(final BigDecimal amount) {
		GiftCertificate resultCertificate = createGiftCertificate(amount);
		return giftCertificateService.add(resultCertificate);
	}

	/**
	 * Returns underlying actual order.
	 * 
	 * @return the order
	 */
	public Order getRealOrder() {
		checkOrder();
		return order;
	}

	/**
	 * Gets order shipment constituting shipment number as: order_number-shipmentNumber
	 * 
	 * @param shipmentNumber
	 * @return OrderShipment if found
	 */
	public OrderShipment getOrderShipment(final int shipmentNumber) {
		checkOrder();
		return order.getShipment(order.getOrderNumber() + "-" + shipmentNumber);
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @return the elasticPath
	 */
	public ElasticPath getElasticPath() {
		return elasticPath;
	}

	/**
	 * Create a non-persistent gift certificate tied to the default store and with a creation date of new Date().
	 * 
	 * @return the gift certificate.
	 */
	private GiftCertificate createGiftCertificate(final BigDecimal amount) {
		GiftCertificate giftCertificate = elasticPath.getBean(ContextIdNames.GIFT_CERTIFICATE);
		giftCertificate.setStore(scenario.getStore());
		giftCertificate.setCreationDate(new Date());
		giftCertificate.setPurchaseAmount(amount);
		giftCertificate.setCurrencyCode(CURRENCY_CODE);
		giftCertificate.setGiftCertificateCode("GC0000" + System.currentTimeMillis()); // should be at least 18 chars
		return giftCertificate;
	}

	private void applyOriginator() {
		EventOriginator originator = elasticPath.getBean(ContextIdNames.EVENT_ORIGINATOR);
		CmUserService cmUserService = elasticPath.getBean(ContextIdNames.CMUSER_SERVICE);

		originator.setCmUser(cmUserService.findByUserName(ADMIN));
		originator.setType(EventOriginatorType.CMUSER);
		order.setModifiedBy(originator);
	}

	// ***************** internal methods
	/**
	 * @return
	 */
	private CustomerSession createCustomerSessionWithShoppingCart(final List<ShoppingItem> shoppingCartItems, final GiftCertificate... giftCertificates) {
		Shopper shopper = elasticPath.getBean(ContextIdNames.SHOPPER);
		ShoppingCart shoppingCart = elasticPath.getBean(ContextIdNames.SHOPPING_CART);
		shoppingCart.initialize();

		shoppingCart.setBillingAddress(getBillingAddress());
		shoppingCart.setShippingAddress(getBillingAddress());

		shoppingCart.setShippingServiceLevelList(Arrays.asList(scenario.getShippingServiceLevel()));
		shoppingCart.setSelectedShippingServiceLevelUid(scenario.getShippingServiceLevel().getUidPk());
		shoppingCart.setCurrency(Currency.getInstance(Locale.US));
		shoppingCart.setStore(scenario.getStore());
		shoppingCart.setShippingCost(SHIPPING_COST);

		if (giftCertificates != null) {
			for (GiftCertificate giftCertificate : giftCertificates) {
				shoppingCart.applyGiftCertificate(giftCertificate);
			}
		}

		if (shoppingCartItems != null) {
			for (ShoppingItem item : shoppingCartItems) {
				shoppingCart.addCartItem(item);
			}
		}

		shopper.setCurrentShoppingCart(shoppingCart);
		
		CustomerSession customerSession = getCustomerSession();
		customerSession.setShopper(shopper);
		return customerSession;
	}

	/**
	 * @return
	 */
	private CustomerSession getCustomerSession() {
		CustomerSession session = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSession();
		session.setCreationDate(new Date());
		session.setCurrency(Currency.getInstance(Locale.US));
		session.setLastAccessedDate(new Date());
		session.setGuid("" + System.currentTimeMillis());
		session.setLocale(Locale.US);
		session.getShopper().setCustomer(persisterContainer.getStoreTestPersister().createDefaultCustomer(scenario.getStore()));

		return session;
	}

	/**
	 * Initializes a mock billing address.
	 * 
	 * @return the Address
	 */
	private Address getBillingAddress() {
		Address billingAddress = new CustomerAddressImpl();
		billingAddress.setFirstName("Billy");
		billingAddress.setLastName("Bob");
		billingAddress.setCountry("US");
		billingAddress.setStreet1("1295 Charleston Road");
		billingAddress.setCity("Mountain View");
		billingAddress.setSubCountry("CA");
		billingAddress.setZipOrPostalCode("94043");
		billingAddress.setGuid(Utils.uniqueCode("address"));

		return billingAddress;
	}

	private void checkOrder() {
		if (order == null) {
			throw new EpServiceException("Order wasn't yet created.");
		}
	}

	/**
	 * Sleep is required at the end of payment-related operation, because if I do init shipment then immediatly increasing which lead to
	 * re-authorization, then it's possible that two authorization will have same date in the DB. This in turn leads to situation when incorrect
	 * payment (first one) is considered to be active. In real life the case is impossible since user-delays.
	 */
	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
}
