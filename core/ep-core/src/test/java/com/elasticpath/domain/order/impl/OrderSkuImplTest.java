/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.DigitalAssetImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.sellingchannel.impl.ShoppingItemRecurringPriceAssemblerImpl;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.pricing.impl.PaymentScheduleHelperImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>OrderSkuImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports", "PMD.TooManyMethods" })
public class OrderSkuImplTest {
	private static final BigDecimal TEN_DOLLARS = BigDecimal.TEN;
	private static final BigDecimal EIGHTY_DOLLARS = new BigDecimal("80.00");

	private static final int TEST_INT = 1;

	private static final String TEST_STRING = "TestString";

	private static final String PROPERTY_NAME = "somePropert";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory expectationsFactory;

	private int uid;

	private boolean fired = false;

	/**
	 * Simple implementation of a PropertyChangeListener.
	 */
	private static class SimplePropertyListener implements PropertyChangeListener {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			// Do nothing.
		}

	}

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		ShoppingItemRecurringPriceAssemblerImpl recurringPriceAssembler = new ShoppingItemRecurringPriceAssemblerImpl();
		recurringPriceAssembler.setBeanFactory(beanFactory);
		final PaymentScheduleHelperImpl paymentScheduleHelper = new PaymentScheduleHelperImpl();
		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		paymentScheduleHelper.setSkuOptionService(skuOptionService);

		recurringPriceAssembler.setPaymentScheduleHelper(paymentScheduleHelper);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE_ASSEMBLER, recurringPriceAssembler);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private OrderSkuImpl createOrderSkuImpl() {
		final OrderSkuImpl orderSkuImpl = new OrderSkuImpl();

		final ProductSku productSku = context.mock(ProductSku.class, "ProductSku-" + ++uid);
		context.checking(new Expectations() {
			{
				allowing(productSku);
			}
		});
		orderSkuImpl.setProductSku(productSku);

		final OrderShipment shipment = new ElectronicOrderShipmentImpl();
		orderSkuImpl.setShipment(shipment);

		return orderSkuImpl;
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderSkuImpl.getCreatedDate()'.
	 */
	@Test
	public void testGetSetCreatedDate() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		Date testDate = new Date();
		orderSkuImpl.setCreatedDate(testDate);
		assertEquals(testDate, orderSkuImpl.getCreatedDate());

		orderSkuImpl.setCreatedDate(null);
		assertNotNull(orderSkuImpl.getCreatedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderSkuImpl.getLastModifiedDate()'.
	 */
	@Test
	public void testGetSetLastModifiedDate() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		Date testDate = new Date();
		orderSkuImpl.setLastModifiedDate(testDate);
		assertEquals(testDate, orderSkuImpl.getLastModifiedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderSkuImpl.getLastModifiedBy()'.
	 */
	@Test
	public void testGetSetLastModifiedBy() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		CmUser testUser = new CmUserImpl();
		orderSkuImpl.setLastModifiedBy(testUser);
		assertEquals(testUser, orderSkuImpl.getLastModifiedBy());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderSkuImpl.getProductSku()'.
	 */
	@Test
	public void testGetSetProductSku() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		ProductSku testProduct = new ProductSkuImpl();
		orderSkuImpl.setProductSku(testProduct);
		assertEquals(testProduct, orderSkuImpl.getProductSku());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderSkuImpl.getQuantity()'.
	 */
	@Test
	public void testGetSetQuantity() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		orderSkuImpl.setQuantity(TEST_INT);
		assertEquals(TEST_INT, orderSkuImpl.getQuantity());
	}

	/**
	 * Test Get/Set the product's display name.
	 */
	@Test
	public void testGetSetDisplayName() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		orderSkuImpl.setDisplayName(TEST_STRING);
		assertEquals(TEST_STRING, orderSkuImpl.getDisplayName());
	}

	/**
	 * Get the product's option values for display.
	 */
	@Test
	public void testGetSetDisplaySkuOptions() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		orderSkuImpl.setDisplaySkuOptions(TEST_STRING);
		assertEquals(TEST_STRING, orderSkuImpl.getDisplaySkuOptions());
	}

	/**
	 * Get the product's image path.
	 */
	@Test
	public void testGetSetImage() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		orderSkuImpl.setImage(TEST_STRING);
		assertEquals(TEST_STRING, orderSkuImpl.getImage());
	}

	/**
	 * Returns the shipping weight.
	 */
	@Test
	public void testGetSetWeight() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		orderSkuImpl.setWeight(TEST_INT);
		assertEquals(TEST_INT, orderSkuImpl.getWeight());
	}

	/**
	 * Get the tax amount.
	 */
	@Test
	public void testGetSetTax() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		BigDecimal testAmount = BigDecimal.ONE;
		orderSkuImpl.setTax(testAmount);
		assertEquals(0, testAmount.compareTo(orderSkuImpl.getTax().getAmount()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderSkuImpl.getDigitalAsset()'.
	 */
	@Test
	public void testGetSetDigitalAssets() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		final DigitalAsset digitalAsset = new DigitalAssetImpl();
		orderSkuImpl.setDigitalAsset(digitalAsset);
		assertSame(digitalAsset, orderSkuImpl.getDigitalAsset());
	}

	/**
	 * Get the tax code.
	 */
	@Test
	public void testGetSetTaxCode() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		final String taxCode = "Book";
		orderSkuImpl.setTaxCode(taxCode);
		assertEquals(taxCode, orderSkuImpl.getTaxCode());
	}


	/**
	 * Test that event gets fired when allocation changes.
	 */
	@Test
	public void testFirePropertyChange() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				setEventFired(true);
			}
		};
		orderSkuImpl.addPropertyChangeListener("orderInventoryAllocation", listener, true);
		orderSkuImpl.setAllocatedQuantity(2);
		assertTrue("No event fired on allocation change", isEventFired());
		setEventFired(false);
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
		this.fired = eventFired;
	}

	/**
	 * Tests that, when the quantity * the price is < the invoice item amount that the dollar savings is quantity * price - invoice item amount.
	 */
	@Test
	public void testGetDollarSavingsNonZero() {
		/**
		 * Test double.
		 */
		class OrderSkuDouble extends OrderSkuImpl {
			private static final long serialVersionUID = -3463737020595547087L;

			@Override
			public BigDecimal getInvoiceItemAmount() {
				return new BigDecimal("8.00");
			}
		}

		final OrderSkuImpl sku = new OrderSkuDouble();

		Money listPriceMoney = MoneyFactory.createMoney(new BigDecimal("4.44"), Currency.getInstance("CAD"));
		Price price = new PriceImpl();

		price.setListPrice(listPriceMoney);
		sku.setPrice(2, price);

		Money dollarSavings = sku.getDollarSavingsMoney();

		assertEquals("2 * 4.44 - 8.00", new BigDecimal("0.88"), dollarSavings.getAmount());
	}

	/**
	 * Tests that, when the quantity * the price is < the invoice item amount that the dollar savings is zero.
	 */
	@Test
	public void testGetDollarSavingsZero() {
		/**
		 * Test double.
		 */
		class OrderSkuDouble extends OrderSkuImpl {
			private static final long serialVersionUID = 267719763297058938L;

			@Override
			public BigDecimal getInvoiceItemAmount() {
				return new BigDecimal("9.00");
			}
		}

		final OrderSkuImpl sku = new OrderSkuDouble();

		Money listPriceMoney = MoneyFactory.createMoney(new BigDecimal("4.44"), Currency.getInstance("CAD"));
		Price price = new PriceImpl();

		price.setListPrice(listPriceMoney);
		sku.setPrice(2, price);

		Money dollarSavings = sku.getDollarSavingsMoney();

		assertEquals("Invoice item amount is bigger than the goods item total", new BigDecimal("0.00"), dollarSavings.getAmount());
	}

	/**
	 * Tests adding a property listener.
	 */
	@Test
	public void testAddRemovePropertyListenerWithReplace() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		assertEquals("Initial list of property listeners should be empty.", // NOPMD
				0, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		orderSkuImpl.addPropertyChangeListener(listener1);

		assertEquals("List of property listeners should contain one entry.", // NOPMD
				1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("Property listener should be the same!",
				listener1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners()[0]);

		// Add same listener again. Should replace.
		orderSkuImpl.addPropertyChangeListener(listener1);
		assertEquals("List of property listeners should contain one entry.",
				1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("Property listener should be the same!",
				listener1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners()[0]);

		// Remove listener.
		orderSkuImpl.removePropertyChangeListener(listener1);
		assertEquals("List of property listeners should be empty after removing.",
				0, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);
	}

	/**
	 * Tests adding a property listener without replacing.
	 */
	@Test
	public void testAddRemovePropertyListenerWithoutReplace() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		assertEquals("Initial list of property listeners should be empty.",
				0, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		orderSkuImpl.addPropertyChangeListener(listener1);

		assertEquals("List of property listeners should contain one entry.",
				1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("Property listener should be the same!",
				listener1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners()[0]);

		// Add same listener again. Should not replace.
		orderSkuImpl.addPropertyChangeListener(listener1, false);

		assertEquals("List of property listeners should contain two entries.",
				2, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("First property listener should be the same!",
				listener1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners()[0]);
		assertSame("Second property listener should be the same!",
				listener1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners()[1]);

		// Remove listeners.
		orderSkuImpl.removePropertyChangeListener(listener1);
		assertEquals("List of property listeners should contain one entry after removing first instance.",
				1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);
		orderSkuImpl.removePropertyChangeListener(listener1);
		assertEquals("List of property listeners should be empty after removing all listeners.",
				0, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);
	}

	/**
	 * Tests adding a property listener for a specific property.
	 */
	@Test
	public void testAddRemovePropertyListenerWithPropertyNameAndReplace() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		assertEquals("Initial list of property listeners should be empty.",
				0, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		orderSkuImpl.addPropertyChangeListener(PROPERTY_NAME, listener1);

		assertEquals("List of property listeners should contain one entry.",
				1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);

		// Add same listener again. Should replace.
		orderSkuImpl.addPropertyChangeListener(PROPERTY_NAME, listener1);
		assertEquals("List of property listeners should still contain only one entry.",
				1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);

		// Remove listener.
		orderSkuImpl.removePropertyChangeListener(PROPERTY_NAME, listener1);
		assertEquals("List of property listeners should be empty after removing.",
				0, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);

	}

	/**
	 * Tests adding a property listener for a specific property without replacing.
	 */
	@Test
	public void testAddRemovePropertyListenerWithPropertyNameWithoutReplace() {
		OrderSkuImpl orderSkuImpl = createOrderSkuImpl();

		assertEquals("Initial list of property listeners should be empty.",
				0, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		orderSkuImpl.addPropertyChangeListener(PROPERTY_NAME, listener1);

		assertEquals("List of property listeners should contain one entry.",
				1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);

		// Add same listener again. Should not replace.
		orderSkuImpl.addPropertyChangeListener(PROPERTY_NAME, listener1, false);
		assertEquals(2, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);

		// Remove listeners.
		orderSkuImpl.removePropertyChangeListener(PROPERTY_NAME, listener1);
		assertEquals(1, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);
		orderSkuImpl.removePropertyChangeListener(PROPERTY_NAME, listener1);
		assertEquals("List of property listeners should be empty after removing all listeners.",
				0, orderSkuImpl.getPropertyChangeSupport().getPropertyChangeListeners().length);
	}

	/**
	 * Test price calculator without cart discounts or taxes.
	 * $80 unit price @ tier 6+ (computed) = $80.00
	 */
	@Test
	public void testGetUnitPriceCalcWithoutDiscountsOrTaxes() {
		final int quantity = 10;
		OrderSkuImpl item = createOrderSkuImpl();
		item.setQuantity(quantity);
		item.setUnitPrice(EIGHTY_DOLLARS);
		item.applyDiscount(TEN_DOLLARS);
		assertEquals(EIGHTY_DOLLARS, item.getUnitPriceCalc().getAmount());
	}

	/**
	 * Test price calculator without cart discounts or taxes.
	 * ($80 unit price @ tier 6+) * 10 = $800.00
	 */
	@Test
	public void testGetPriceCalcWithoutDiscountsOrTaxes() {
		final int quantity = 10;
		OrderSkuImpl item = createOrderSkuImpl();
		item.setQuantity(quantity);
		item.setUnitPrice(EIGHTY_DOLLARS);
		item.applyDiscount(TEN_DOLLARS);
		assertEquals(new BigDecimal("800.00"), item.getPriceCalc().getAmount());
	}

	/**
	 * Test price calculator with cart discounts and taxes.
	 * $80 unit price - ($20 discount / 10 qty) + ($10 taxes / 10 qty) = $79.00
	 */
	@Test
	public void testGetUnitPriceCalcWithDiscountsAndTaxes() {
		final int quantity = 10;
		OrderSkuImpl item = createOrderSkuImpl();
		item.setQuantity(quantity);
		item.setUnitPrice(EIGHTY_DOLLARS);
		item.setTax(TEN_DOLLARS);
		item.applyDiscount(new BigDecimal("20.00"));

		assertEquals(new BigDecimal("79.00"), item.getUnitPriceCalc().withCartDiscounts().withTaxes().getAmount());
	}

	/**
	 * Test price calculator with cart discounts and taxes.
	 * ($80 unit price * 10) - $20 discount + $10 taxes = $790.00
	 */
	@Test
	public void testGetPriceCalcWithDiscountsAndTaxes() {
		final int quantity = 10;
		OrderSkuImpl item = createOrderSkuImpl();
		item.setQuantity(quantity);
		item.setUnitPrice(EIGHTY_DOLLARS);
		item.setTax(TEN_DOLLARS);
		item.applyDiscount(new BigDecimal("20.00"));

		assertEquals(new BigDecimal("790.00"), item.getPriceCalc().withCartDiscounts().withTaxes().getAmount());

		// This ensures that item.getPriceCalc().withCartDiscounts().getAmount() returns the same value as getTotal.
		// Can be removed once the deprecated getLowestUnitPrice method is removed.
		assertEquals(item.getTotal().getAmount(), item.getPriceCalc().withCartDiscounts().getAmount());
	}

	/**
	 * Test price calculator without taxes on an item with inclusive tax.
	 * $80 unit price = $80.00
	 */
	@Test
	public void testInclusiveTaxGetUnitPriceCalcDefaultTaxes() {
		final int quantity = 10;
		OrderSkuImpl item = createOrderSkuImpl();
		item.getShipment().setInclusiveTax(true);
		item.setQuantity(quantity);
		item.setUnitPrice(EIGHTY_DOLLARS);
		item.setTax(TEN_DOLLARS);

		assertEquals(EIGHTY_DOLLARS, item.getUnitPriceCalc().getAmount());
	}

	/**
	 * Test price calculator without taxes on an item with inclusive tax.
	 * $80 unit price - ($10 taxes / 10 qty) = $79.00
	 */
	@Test
	public void testInclusiveTaxGetUnitPriceCalcWithoutTaxes() {
		final int quantity = 10;
		OrderSkuImpl item = createOrderSkuImpl();
		item.getShipment().setInclusiveTax(true);
		item.setQuantity(quantity);
		item.setUnitPrice(EIGHTY_DOLLARS);
		item.setTax(TEN_DOLLARS);

		assertEquals(new BigDecimal("79.00"), item.getUnitPriceCalc().withoutTaxes().getAmount());
	}

}
