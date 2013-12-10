package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ListenableObject;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.domain.tax.impl.TaxRegionImpl;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for AbstractOrderShipment.
 */
public class AbstractOrderShipmentTest {

	private static final BigDecimal TAX_VALUE2 = new BigDecimal("4.53");

	private static final BigDecimal TAX_VALUE1 = new BigDecimal("10.44");

	private ExtOrderShipment shipment;

	private boolean fired = false;

	private static final long FAKE_UIDPK = 10000L;
	private static final String FAKE_ORDER_NUMBER = "100000";

	private static final String TAX_CAT_NAME1 = "taxCatName1";

	private static final String TAX_CAT_NAME2 = "taxCatName2";

	private static final String TAX_CAT_NAME3 = "taxCatName3";

	private static final BigDecimal TAX_VALUE3 = new BigDecimal("34.22");

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepare for the tests.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		shipment = new TempOrderShipment();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/** Test adding an orderSku.
	 * @throws Exception on error */
	@Test
	public void testAddOrderSku() throws Exception {
		OrderSku orderSku = new OrderSkuImpl();
		shipment.addShipmentOrderSku(orderSku);
		assertSame(orderSku, shipment.getShipmentOrderSkus().toArray()[0]);
	}

	/** Test removing an order sku that was previously added.
	 * @throws Exception on error */
	@Test
	public void testRemoveOrderSku() throws Exception {
		OrderSku orderSku = new OrderSkuImpl();
		shipment.addShipmentOrderSku(orderSku);
		assertSame(1, shipment.getShipmentOrderSkus().size());
		shipment.removeShipmentOrderSku(orderSku);
		assertTrue(shipment.getShipmentOrderSkus().isEmpty());
	}

	/** Test removing an order sku that was never added.
	 * @throws Exception on error */
	@Test
	public void testRemoveUnknownOrderSkuFailsSilently() throws Exception {
		OrderSku orderSku = new OrderSkuImpl();
		ProductSku productSku1 = new ProductSkuImpl();
		productSku1.setGuid("SKU1");
		ProductSku productSku2 = new ProductSkuImpl();
		productSku2.setGuid("SKU2");

		orderSku.setProductSku(productSku1);
		shipment.addShipmentOrderSku(orderSku);
		assertEquals("Shipment should contain 1 sku", 1, shipment.getShipmentOrderSkus().size());
		OrderSku secondSku = new OrderSkuImpl();
		secondSku.setGuid("orderSku2");
		secondSku.setProductSku(productSku2);
		assertFalse("The skus should not be equal", orderSku.equals(secondSku));
		shipment.removeShipmentOrderSku(secondSku);
		assertEquals("Shipment should still contain 1 sku", 1, shipment.getShipmentOrderSkus().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.PhysicalOrderShipmentImpl.getShipmentOrderSkus()'.
	 */
	@Test
	public void testGetSetShipmentOrderSkus() {

		AbstractOrderShipmentImpl orderShipmentImpl = new AbstractOrderShipmentImpl() {
			private static final long serialVersionUID = 2701120160460208926L;

			@Override
			public boolean isCancellable() {
				return false;
			}

			@Override
			public Money getTotalTaxMoney() {
				return null;
			}

			@Override
			protected void recalculateTransientDerivedValues() {
				// TODO Auto-generated method stub
			}

			@Override
			protected void recalculate() {
				// TODO Auto-generated method stub
			}

			@Override
			public ShipmentType getOrderShipmentType() {
				return null;
			}
		};

		Set<OrderSku> orderSkuSet = new HashSet<OrderSku>();

		ProductSku productSku1 = new ProductSkuImpl();
		productSku1.setGuid("SKU1");
		ProductSku productSku2 = new ProductSkuImpl();
		productSku2.setGuid("SKU2");


		OrderSku orderSku1 = new OrderSkuImpl();
		OrderSku orderSku2 = new OrderSkuImpl();
		orderSku2.setGuid("OrderSku2");
		orderSku1.setProductSku(productSku1);
		orderSku1.setShipment(orderShipmentImpl);
		orderSku2.setProductSku(productSku2);
		orderSkuSet.add(orderSku1);

		for (OrderSku sku : orderSkuSet) {
			orderShipmentImpl.addShipmentOrderSku(sku);
		}

		assertEquals(orderSkuSet, orderShipmentImpl.getShipmentOrderSkus());

		int numSkus = orderShipmentImpl.getShipmentOrderSkus().size();
		orderShipmentImpl.addShipmentOrderSku(orderSku2);
		assertEquals(numSkus + 1, orderShipmentImpl.getShipmentOrderSkus().size());
	}

	/**
	 *	Test that event is fired when status is changed.
	 */
	@Test
	public void testFirePropertyChange() {
		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				((ListenableObject) shipment).removePropertyChangeListener(this);
				setEventFired(true);
			}
		};
		((ListenableObject) shipment).addPropertyChangeListener("shipmentStatus", listener, true);
		shipment.setStatus(OrderShipmentStatus.RELEASED);
		assertTrue("No event fired on status change", isEventFired());
	}

	/**
	 * Test that a shipment status gets updated as its order sku items change in allocated quantity.
	 * Test also that order status will override shipment status.
	 */
	@Test
	public void testStatusChange() {
		TaxCalculationResultImpl taxCalculationResultImpl = new TaxCalculationResultImpl();
		taxCalculationResultImpl.setTaxInclusive(true);
		Money moneyImpl = MoneyFactory.createMoney(BigDecimal.ZERO, Currency.getInstance(Locale.US));
		taxCalculationResultImpl.setBeforeTaxShippingCost(moneyImpl);
		taxCalculationResultImpl.setDefaultCurrency(Currency.getInstance(Locale.CANADA));
		taxCalculationResultImpl.setBeforeTaxSubTotal(moneyImpl);

		final Order order = new OrderImpl();
		order.holdOrder();
		final OrderSku sku = new OrderSkuImpl();
		sku.setPrice(2, null);
		shipment.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);
		shipment.addShipmentOrderSku(sku);
		order.setUidPk(FAKE_UIDPK);
		order.setOrderNumber(FAKE_ORDER_NUMBER);
		order.addShipment(shipment);
		assertEquals(OrderShipmentStatus.ONHOLD, shipment.getShipmentStatus());
		assertEquals(OrderStatus.ONHOLD, order.getStatus());

		//Resume the order
		order.releaseHoldOnOrder();
		assertEquals(OrderShipmentStatus.AWAITING_INVENTORY, shipment.getShipmentStatus());
		sku.setAllocatedQuantity(2);
		assertEquals(OrderShipmentStatus.INVENTORY_ASSIGNED, shipment.getShipmentStatus());
		sku.setAllocatedQuantity(1);
		assertEquals(OrderShipmentStatus.AWAITING_INVENTORY, shipment.getShipmentStatus());

		order.cancelOrder();
		assertEquals(OrderShipmentStatus.CANCELLED, shipment.getShipmentStatus());
	}

	/**
	 * Test helper method.
	 *
	 * @return if event is fired during execution
	 */
	boolean isEventFired() {
		return fired;
	}

	/**
	 * Test helper method.
	 *
	 * @param eventFired the event fired
	 */
	void setEventFired(final boolean eventFired) {
		fired = eventFired;
	}

	/**
	 * Tests that only a shipment in the PACKING or RELEASED(for packing) state can have its funds captured.
	 * NOTICE: The shipment in state RELEASED can be complited in order to allow company that is using their own warehouse
	 * system to complete the shipment through web-services without ever having done the actual pickpack stuff in our warehouse.
	 */
	@Test
	public void testIsReadyForFundsCapture() {
		for (OrderShipmentStatus status : OrderShipmentStatus.values()) {
			shipment.setStatus(status);
			if (OrderShipmentStatus.RELEASED == status) {
				assertTrue(shipment.isReadyForFundsCapture());
			} else {
				assertFalse(shipment.isReadyForFundsCapture());
			}
		}
	}

	/**
	 * Tests OrderShipment.updateTaxValues().
	 *
	 * Test Scenario:
	 *
	 * 1. Add two tax categories with respective amounts to the taxResult
	 * 2. Update the tax values on the shipment
	 * 3. Check the shipment tax values
	 *
	 * 4. Add new tax category and an amount to the taxResult
	 * 5. Update the tax values on the shipment
	 * 6. Check the shipment tax values
	 *
	 * 7. Create new taxResult with tax categories 1 and 2
	 * 8. Update the tax values on the shipment
	 * 9. Check the tax values. There should be again only those two values
	 *    as number 3 should have been removed.
	 */
	@Test
	public void testUpdateTaxValue() {

		expectationsFactory.allowingBeanFactoryGetBean("randomGuid", RandomGuidImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("localizedProperties", LocalizedPropertiesImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("orderTaxValue", OrderTaxValueImpl.class);

		OrderImpl orderImpl = new OrderImpl();
		Store storeImpl = new StoreImpl();
		storeImpl.setCode("store_code");
		orderImpl.setStore(storeImpl);

		shipment.setOrder(orderImpl);

		TaxCalculationResult taxResult = new TaxCalculationResultImpl();

		// 1.
		TaxCategory taxCategory1 = newTaxCategory(TAX_CAT_NAME1);
		Money amount1 = newMoney(TAX_VALUE1);
		taxResult.addTaxValue(taxCategory1, amount1);

		TaxCategory taxCategory2 = newTaxCategory(TAX_CAT_NAME2);
		Money amount2 = newMoney(TAX_VALUE2);
		taxResult.addTaxValue(taxCategory2, amount2);

		// 2.
		shipment.updateTaxValues(taxResult);

		Set<OrderTaxValue> orderShipmentTaxes = shipment.getShipmentTaxes();

		assertEquals(2, orderShipmentTaxes.size());

		// 3.
		checkTaxValues(orderShipmentTaxes);

		// 4.
		Money amount3 = newMoney(TAX_VALUE3);
		TaxCategory taxCategory3 = newTaxCategory(TAX_CAT_NAME3);
		taxResult.addTaxValue(taxCategory3, amount3);

		// 5.
		shipment.updateTaxValues(taxResult);

		// 6.
		final int threeTaxValues = 3;
		assertEquals(threeTaxValues, shipment.getShipmentTaxes().size());

		// 7.
		taxResult = new TaxCalculationResultImpl();

		taxResult.addTaxValue(taxCategory1, amount1);
		taxResult.addTaxValue(taxCategory2, amount2);

		// 8.
		shipment.updateTaxValues(taxResult);

		// 9.
		assertEquals(2, shipment.getShipmentTaxes().size());
		checkTaxValues(shipment.getShipmentTaxes());
	}

	/**
	 * Checks if the tax values refer to the same tax categories we added in the tax result.
	 */
	private void checkTaxValues(final Set<OrderTaxValue> orderShipmentTaxes) {
		boolean taxCat1Exists = false;
		boolean taxCat2Exists = false;
		for (OrderTaxValue taxValue : orderShipmentTaxes) {
			if (taxValue.getTaxCategoryName().equals(TAX_CAT_NAME1)) {
				taxCat1Exists = true;
				assertEquals(TAX_VALUE1, taxValue.getTaxValue());
			} else if (taxValue.getTaxCategoryName().equals(TAX_CAT_NAME2)) {
				taxCat2Exists = true;
				assertEquals(TAX_VALUE2, taxValue.getTaxValue());
			}
		}

		assertTrue(taxCat1Exists);
		assertTrue(taxCat2Exists);
	}

	private TaxCategory newTaxCategory(final String name) {
		TaxCategory taxCategory1 = new TaxCategoryImpl() {

			private static final long serialVersionUID = -3511239220425122338L;

			@Override
			public String getDisplayName(final Locale locale) {
				return name;
			}

		};
		taxCategory1.initialize();
		taxCategory1.addTaxRegion(new TaxRegionImpl());
		taxCategory1.setName(name);
		return taxCategory1;
	}

	private Money newMoney(final BigDecimal value) {
		return MoneyFactory.createMoney(value, Currency.getInstance(Locale.CANADA));
	}

	/**
	 * Extension of {@link OrderShipment} interface.
	 */
	private interface ExtOrderShipment extends OrderShipment {
		/**
		 *
		 * @param taxResult the tax result
		 */
		void updateTaxValues(TaxCalculationResult taxResult);
	}



	/**
	 * Temp test impl of {@link AbstractOrderShipmentImpl}.
	 */
	private class TempOrderShipment extends AbstractOrderShipmentImpl implements ExtOrderShipment  {

		private static final long serialVersionUID = 1302965398950367944L;

		@Override
		public ShipmentType getOrderShipmentType() {
			return ShipmentType.PHYSICAL;
		}

		@Override
		protected void recalculate() {
			// no implementation
		}

		@Override
		public Money getTotalBeforeTaxMoney() {
			return null;
		}

		@Override
		public Money getTotalTaxMoney() {
			return null;
		}

		@Override
		public boolean isCancellable() {
			return false;
		}

		@Override
		public void updateTaxValues(final TaxCalculationResult taxResult) { //NOPMD
			super.updateTaxValues(taxResult);
		}

		@Override
		protected void recalculateTransientDerivedValues() {
			// abstract method
		}

	}
}
