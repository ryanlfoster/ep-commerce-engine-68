package com.elasticpath.service.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.ServiceOrderShipment;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.AppliedRule.Visitor;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShipmentTypeShoppingCartVisitor;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.shoppingcart.OrderFactory;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.service.tax.impl.DiscountApportioningCalculator;

/**
 * Creates Orders from ShoppingCarts.
 */
public class OrderFactoryImpl implements OrderFactory {

	private static final String RULE_DELETED_MESSAGE = "Referenced rule was deleted";

	private OrderService orderService;
	private OrderSkuFactory orderSkuFactory;
	private BeanFactory beanFactory;
	private RuleService ruleService;
	private Predicate serviceOrderSkuPredicate;
	private CartOrderService cartOrderService;

	/** Hook point for alternate logging applied rule strategies. */
	private Visitor appliedRuleVisitor;

	/**
	 * Creates an {@code Order} from the items in a {@code ShoppingCart}.
	 * @param customer the customer
	 * @param shoppingCart the shopping cart
	 * @param isOrderExchange whether this order is created as a result of an Exchange
	 * @param awaitExchangeCompletion whether the order should wait for completion of the exchange before being fulfilled
	 * @param exchange the applicable {@code OrderReturn}, if any
	 * @return the Order that's created
	 */
	@Override
	public Order createAndPersistNewOrderFromShoppingCart(final Customer customer, final ShoppingCart shoppingCart,
			final boolean isOrderExchange,
			final boolean awaitExchangeCompletion, final OrderReturn exchange) {
		// create order and fill out its fields
		final Order order = createEmptyOrder(customer, shoppingCart, isOrderExchange, awaitExchangeCompletion);
		if (isOrderExchange) {
			//Exchange order will need to derive payment information from exchange further.
			order.setExchange(exchange);
		}
		fillInOrderDetails(order, shoppingCart, customer,
				isOrderExchange, awaitExchangeCompletion);
		return order;
	}

	/**
	 * Create an empty order object so we can use the order number.
	 *
	 * @param shoppingCart the shopping cart belonging to this order
	 */
	private Order createEmptyOrder(final Customer customer, final ShoppingCart shoppingCart,
			final boolean isExchangeOrder, final boolean awaitExchangeCompletion) {
		Order order = getBean(ContextIdNames.ORDER);
		order.setCreatedDate(new Date());
		order.setIpAddress(shoppingCart.getIpAddress());
		order.setCurrency(shoppingCart.getCurrency());
		order.setLocale(shoppingCart.getLocale());
		order.setStore(shoppingCart.getStore());
		order.setCustomer(customer);

		if (isExchangeOrder && !awaitExchangeCompletion) {
			order.setExchangeOrder(Boolean.TRUE);
		} else if (isExchangeOrder && awaitExchangeCompletion) {
			order.setExchangeOrder(Boolean.TRUE);
			order = getOrderService().awaitExchnageCompletionForOrder(order);
		}

		setCartOrderGuidOnOrder(order, shoppingCart);

		return getOrderService().add(order);
	}

	/**
	 * Sets the associated cart order GUID on the given order.
	 *
	 * @param order the order
	 * @param shoppingCart the shopping cart
	 */
	protected void setCartOrderGuidOnOrder(final Order order, final ShoppingCart shoppingCart) {
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(shoppingCart.getGuid());
		if (cartOrder != null) {
			order.setCartOrderGuid(cartOrder.getGuid());
		}
	}

	/**
	 * Populates all the fields of the order. Creates order shipments and sets the corresponding order skus.
	 */
	private void fillInOrderDetails(final Order order, final ShoppingCart shoppingCart, final Customer customer,
			final boolean isExchangeOrder, final boolean awaitExchangeCompletion) {
		order.setCustomer(customer);
		order.setModifiedBy(getEventOriginatorHelper().getCustomerOriginator(customer));

		order.setAppliedRules(getAppliedOrderRules(shoppingCart.getAppliedRules()));

		// Set the billing address
		final OrderAddress billingAddress = getBean(ContextIdNames.ORDER_ADDRESS);
		if (shoppingCart.getBillingAddress() != null) {
			billingAddress.init(shoppingCart.getBillingAddress());
			order.setBillingAddress(billingAddress);
		}

		if (customer.isAnonymous()) {
			updateAnonymousCustomer(order, customer);
		}

		// Create & add shipments
		// Allocate inventory to order
		createOrderSkusFromCartItems(shoppingCart, order, isExchangeOrder, awaitExchangeCompletion);

		// CM user uid, null if order not placed through CSR
		order.setCmUserUID(shoppingCart.getCmUserUID());

	}

	/**
	 * Set the name and phone number of the anonymous customer from the billing address (only
	 * if they don't have values already set) to enable searching.
	 *
	 * @param order the order
	 * @param customer the anonymous customer
	 */
	protected void updateAnonymousCustomer(final Order order, final Customer customer) {
		boolean customerUpdated = false;
		
		final OrderAddress billingAddress = order.getBillingAddress();
		if (billingAddress == null) {
			return;
		}
		
		final CustomerService customerService = getBean(ContextIdNames.CUSTOMER_SERVICE);
		Customer customerToUpdate = customer;
		if (customer.isPersisted()) {
			customerToUpdate = customerService.findByGuid(customer.getGuid());
		}

		// First & last names belong together
		if (customerToUpdate.getFirstName() == null && customer.getLastName() == null) {
			customerToUpdate.setFirstName(billingAddress.getFirstName());
			customerToUpdate.setLastName(billingAddress.getLastName());
			customerUpdated = true;
		}
		
		if (customerToUpdate.getPhoneNumber() == null) {
			customerToUpdate.setPhoneNumber(billingAddress.getPhoneNumber());
			customerUpdated = true;
		}

		if (customerUpdated) {
			final Customer updatedCustomer = customerService.update(customerToUpdate);
			order.setCustomer(updatedCustomer);
		}
	}

	private void createOrderSkusFromCartItems(final ShoppingCart shoppingCart, final Order order,
			final boolean isExchangeOrder, final boolean awaitExchangeCompletion) {

		final Collection<OrderSku> rootItems = getOrderSkuFactory().createOrderSkus(shoppingCart.getCartItems(),
				shoppingCart.getLocale());

		final Set<OrderSku> physicalSkus = new HashSet<OrderSku>();
		final Set<OrderSku> electronicSkus = new HashSet<OrderSku>();
		final Set<OrderSku> serviceShipmentSkus = new HashSet<OrderSku>();

		splitAccordingToShipmentType(rootItems, physicalSkus, electronicSkus, serviceShipmentSkus);

		//no promotion is applied to service items. So they won't contribute to the splitShipmentMode
		final boolean splitShipmentMode = !physicalSkus.isEmpty() && !electronicSkus.isEmpty();
		Map<String, BigDecimal> discountByShoppingItemUid = null;
		if (splitShipmentMode) {
			//Apportion discount to individual items
			Collection<OrderSku> nonServiceItems;
			if (serviceShipmentSkus.isEmpty()) {
				nonServiceItems = rootItems;
			} else {
				//making sure the promotions are not apportioned to service items.
				nonServiceItems = getNonServiceItems(rootItems);
			}

			DiscountApportioningCalculator discountCalculator = new DiscountApportioningCalculator();
			discountByShoppingItemUid = discountCalculator.apportionDiscountToShoppingItems(shoppingCart.getSubtotalDiscountMoney(),
					nonServiceItems);
		}
		// Form the order shipments out from the cart item types
		if (!physicalSkus.isEmpty()) {
			//Total up the shipment discount from the skus included
			final BigDecimal discountForShipment = getDiscountForShipment(discountByShoppingItemUid,
					physicalSkus, splitShipmentMode, shoppingCart.getSubtotalDiscount());
			final OrderShipment physicalShipment = createPhysicalShipment(discountForShipment,
					shoppingCart, physicalSkus, isExchangeOrder, awaitExchangeCompletion);
			physicalShipment.setOrder(order);
			order.addShipment(physicalShipment);
		}
		if (!electronicSkus.isEmpty()) {
			//Total up the shipment discount from the skus included
			//electronic shipment can not be returned.
			final BigDecimal discountForShipment = getDiscountForShipment(discountByShoppingItemUid,
					electronicSkus, splitShipmentMode, shoppingCart.getSubtotalDiscount());
			final OrderShipment electronicShipment = createElectronicShipment(discountForShipment, shoppingCart, electronicSkus);
			electronicShipment.setOrder(order);
			order.addShipment(electronicShipment);
		}
		if (!serviceShipmentSkus.isEmpty()) {
			final OrderShipment serviceOrderShipment = createServiceShipment(serviceShipmentSkus);
			serviceOrderShipment.setOrder(order);
			order.addShipment(serviceOrderShipment);
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<OrderSku> getNonServiceItems(final Collection<OrderSku> rootItems) {
		return CollectionUtils.selectRejected(rootItems, serviceOrderSkuPredicate);
	}

	private BigDecimal getDiscountForShipment(final Map<String, BigDecimal> discountByShoppingItem, final Set<OrderSku> shoppingItems,
			final boolean splitShipmentMode, final BigDecimal cartSubtotalDiscount) {
		if (!splitShipmentMode) {
			return cartSubtotalDiscount;
		}
		BigDecimal discount = BigDecimal.ZERO;
		for (OrderSku sku : shoppingItems) {
			if (discountByShoppingItem.containsKey(sku.getGuid())) {
				discount = discount.add(discountByShoppingItem.get(sku.getGuid()));
			}
		}
		return discount;
	}

	private void splitAccordingToShipmentType(final Collection<? extends OrderSku> rootItems,
												final Set<OrderSku> physicalSkus,
												final Set<OrderSku> electronicSkus,
												final Set<OrderSku> serviceSkus) {
		ShipmentTypeShoppingCartVisitor visitor = getBeanFactory().getBean(ContextIdNames.SHIPMENT_TYPE_SHOPPING_CART_VISITOR);
		for (OrderSku sku : rootItems) {
			sku.accept(visitor);
		}
		for (ShoppingItem item : visitor.getElectronicSkus()) {
			electronicSkus.add((OrderSku) item);
		}
		for (ShoppingItem item : visitor.getPhysicalSkus()) {
			physicalSkus.add((OrderSku) item);
		}
		for (ShoppingItem item : visitor.getServiceSkus()) {
			serviceSkus.add((OrderSku) item);
		}
	}

	/**
	 * Create the OrderShipment. Status of shipment is set according to allocation status of its OrderSkus.
	 *
	 * @param splitShipmentMode
	 * @param shoppingCart
	 * @param orderSkuSet
	 * @param isExchangeOrder
	 * @param awaitExchangeCompletion
	 * @return
	 */
	private OrderShipment createPhysicalShipment(final BigDecimal subtotalDiscount, final ShoppingCart shoppingCart, final Set<OrderSku> orderSkuSet,
			final boolean isExchangeOrder, final boolean awaitExchangeCompletion) {
		final PhysicalOrderShipment orderShipment = getBean(ContextIdNames.PHYSICAL_ORDER_SHIPMENT);
		final OrderAddress shippingAddress = getBean(ContextIdNames.ORDER_ADDRESS);
		shippingAddress.init(shoppingCart.getShippingAddress());
		orderShipment.setShipmentAddress(shippingAddress);
		orderShipment.setCreatedDate(new Date());
		orderShipment.setCarrier(shoppingCart.getSelectedShippingServiceLevel().getCarrier());
		orderShipment.setServiceLevel(shoppingCart.getSelectedShippingServiceLevel().getDisplayName(shoppingCart.getLocale(), false));
		orderShipment.setShippingServiceLevelGuid(shoppingCart.getSelectedShippingServiceLevel().getGuid());
		orderShipment.setShippingCost(shoppingCart.getShippingCost().getAmount());
		orderShipment.setBeforeTaxShippingCost(shoppingCart.getBeforeTaxShippingCost().getAmount());
		addOrderSkusToShipment(orderSkuSet, orderShipment);

		if (!isExchangeOrder || (isExchangeOrder && !awaitExchangeCompletion)) {
			orderShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
			for (final OrderSku skus : orderSkuSet) {
				if (!skus.isAllocated()) {
					orderShipment.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);
				}
			}
		} else if (isExchangeOrder && awaitExchangeCompletion) {
			orderShipment.setStatus(OrderShipmentStatus.ONHOLD);
		}

		// set discount
		orderShipment.setSubtotalDiscount(subtotalDiscount);

		orderShipment.setInclusiveTax(shoppingCart.isInclusiveTaxCalculationInUse());

		return orderShipment;
	}

	/**
	 * Adds skus to shipment.
	 */
	private void addOrderSkusToShipment(final Set<OrderSku> orderSkuSet, final OrderShipment orderShipment) {
		for (OrderSku sku : orderSkuSet) {
			orderShipment.addShipmentOrderSku(sku);
		}
	}

	private OrderShipment createElectronicShipment(final BigDecimal subtotalDiscount,
			final ShoppingCart shoppingCart, final Set<OrderSku> orderSkuSet) {

		final ElectronicOrderShipment orderShipment = getBean(ContextIdNames.ELECTRONIC_ORDER_SHIPMENT);
		orderShipment.setCreatedDate(new Date());
		orderShipment.setStatus(OrderShipmentStatus.RELEASED);
		// add skus
		addOrderSkusToShipment(orderSkuSet, orderShipment);

		orderShipment.setInclusiveTax(shoppingCart.isInclusiveTaxCalculationInUse());

		orderShipment.setSubtotalDiscount(subtotalDiscount);

		return orderShipment;
	}


	private OrderShipment createServiceShipment(final Set<OrderSku> orderSkus) {
		final ServiceOrderShipment orderShipment = getBean(ContextIdNames.SERVICE_ORDER_SHIPMENT);
		addOrderSkusToShipment(orderSkus, orderShipment);
		orderShipment.setCreatedDate(new Date());
		orderShipment.setSubtotalDiscount(BigDecimal.ZERO.setScale(2));
		orderShipment.setStatus(OrderShipmentStatus.SHIPPED);
		return orderShipment;
	}

	private EventOriginatorHelper getEventOriginatorHelper() {
		return getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
	}

	/**
	 * Creates <code>AppliedRule</code> objects from the set of applied rule uids and returns them.
	 *
	 * @return a <code>Set</code> of applied rules
	 */
	private Set<AppliedRule> getAppliedOrderRules(final Set<Long> appliedRuleIds) {
		final Set<AppliedRule> appliedRuleSet = new HashSet<AppliedRule>();
		for (final Long currAppliedRuleId : appliedRuleIds) {
			final AppliedRule appliedRule = getBean(ContextIdNames.APPLIED_RULE);
			try {
				final Rule rule = getRuleService().load(currAppliedRuleId.longValue());
				appliedRule.initialize(rule);
				appliedRule.accept(appliedRuleVisitor);

			} catch (final EpPersistenceException epe) {
				appliedRule.setRuleUid(currAppliedRuleId.longValue());
				appliedRule.setRuleName(RULE_DELETED_MESSAGE);
				appliedRule.setRuleCode(RULE_DELETED_MESSAGE);
			}

			appliedRuleSet.add(appliedRule);
		}
		return appliedRuleSet;
	}
	
	/**
	 * Convenience method for getting a bean instance from elastic path.
	 * @param <T> the type of bean to return
	 * @param beanName the name of the bean to get an instance of.
	 * @return an instance of the requested bean.
	 */
	protected <T> T getBean(final String beanName) {
		return getBeanFactory().<T>getBean(beanName);
	}

	/**
	 * @return the orderService
	 */
	OrderService getOrderService() {
		return orderService;
	}

	/**
	 * @param orderService the orderService to set
	 */
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * @return the orderSkuFactory
	 */
	OrderSkuFactory getOrderSkuFactory() {
		return orderSkuFactory;
	}

	/**
	 * @param appliedRuleVisitor the appliedRuleLoggingVisitor to set
	 */
	public void setAppliedRuleVisitor(final Visitor appliedRuleVisitor) {
		this.appliedRuleVisitor = appliedRuleVisitor;
	}
	/**
	 * @return the appliedRuleLoggingVisitor
	 */
	protected Visitor getAppliedRuleVisitor() {
		return appliedRuleVisitor;
	}

	/**
	 * @param orderSkuFactory the orderSkuFactory to set
	 */
	public void setOrderSkuFactory(final OrderSkuFactory orderSkuFactory) {
		this.orderSkuFactory = orderSkuFactory;
	}
	/**
	 * @return the beanFactory
	 */
	BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the ruleService
	 */
	RuleService getRuleService() {
		return ruleService;
	}

	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}


	/**
	 * Set the {@link Predicate} to be used to check whether an {@link OrderSku} should be put into the {@link ServiceOrderShipment}.
	 *
	 * @param serviceOrderSkuPredicate the predicate to set
	 */
	public void setServiceOrderSkuPredicate(final Predicate serviceOrderSkuPredicate) {
		this.serviceOrderSkuPredicate = serviceOrderSkuPredicate;
	}

	/**
	 * Gets the cart order service.
	 *
	 * @return the cartOrderService
	 */
	protected CartOrderService getCartOrderService() {
		return cartOrderService;
	}

	/**
	 * Sets the cart order service.
	 *
	 * @param cartOrderService the cartOrderService to set
	 */
	public void setCartOrderService(final CartOrderService cartOrderService) {
		this.cartOrderService = cartOrderService;
	}

}
