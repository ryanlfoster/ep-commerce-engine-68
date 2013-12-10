/**
 *
 */
package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertNotNull;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.catalog.AvailabilityException;
import com.elasticpath.domain.catalog.InsufficientInventoryException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.shoppingcart.actions.CheckoutAction;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;
import com.elasticpath.service.shoppingcart.actions.impl.StockCheckerCheckoutAction;

/**
 * Tests for the {@code CheckoutServiceImpl} class.
 */
public class CheckoutServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test that when checkout fails due to an exception thrown during processing of an action,
	 * all previous actions are rolled back.
	 */
	@Test
	public void testCheckoutRollbackMechanism() {
		final Shopper shopper = context.mock(Shopper.class);
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final ReversibleCheckoutAction action1 = context.mock(ReversibleCheckoutAction.class, "action1");
		final ReversibleCheckoutAction action2 = context.mock(ReversibleCheckoutAction.class, "action2");

		final CheckoutResults checkoutResults = context.mock(CheckoutResults.class);
		context.checking(new Expectations() { {
			oneOf(action1).execute(with(aNonNull(CheckoutActionContext.class)));
			oneOf(action2).execute(with(aNonNull(CheckoutActionContext.class)));
			will(throwException(new EpSystemException("testing exception handling.")));
			allowing(shopper).getCurrentShoppingCart(); will(returnValue(shoppingCart));
			oneOf(action2).rollback(with(aNonNull(CheckoutActionContext.class)));
			oneOf(action1).rollback(with(aNonNull(CheckoutActionContext.class)));
			allowing(shopper).getUidPk(); will(returnValue(1L));
			
			oneOf(checkoutResults).setOrder(null);
		} });

		CheckoutServiceImpl service = new CheckoutServiceImpl();
		List<ReversibleCheckoutAction> actionList = new ArrayList<ReversibleCheckoutAction>();
		actionList.add(action1);
		actionList.add(action2);
		service.setReversibleActionList(actionList);

		try {
			service.checkoutInternal(shoppingCart, null, true, false, null, checkoutResults);
		} catch (EpSystemException ex) {
			assertNotNull("The EpSystemException should be rethrown after the payments have been rolled back.", ex);
		}
		//The check is performed by expecting the payment service rollBackPayments() method call on the mock object
	}

	/**
	 * TODO: Test that if a payment service throws an exception during the processing of OrderPayments,
	 * all changes to the Order will be rolled back (including any payments already made), and then then EpSystemException
	 * will be re-thrown. The ShoppingCart will still have all of its items.
	 * This test should simply ensure that the PaymentService.initializePayments method throws an exception
	 * and then check that it's handled property.
	 */


	/**
	 * Test that when there is insufficient inventory, checkout will fail with an InsufficientInventoryException
	 * and the shopping cart will not be emptied.
	 */
	@Test(expected = InsufficientInventoryException.class)
	public void testInsufficientInventoryOnCheckout() {
		//If the ShoppingCart were cleared this would fail because the mock shopping cart is not expecting it.

		final Shopper shopper = context.mock(Shopper.class);
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Store store = context.mock(Store.class);
		final Warehouse warehouse = context.mock(Warehouse.class);
		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);
		final ProductSku productSku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		final Locale locale = Locale.CANADA;
		final OrderPayment orderPayment = context.mock(OrderPayment.class);
		final OrderReturn orderReturn = context.mock(OrderReturn.class);
		final CheckoutResults checkoutResults = context.mock(CheckoutResults.class);
		final List<ShoppingItem> items = new ArrayList<ShoppingItem>();
		items.add(shoppingItem);
		final AllocationService allocationService = context.mock(AllocationService.class);
		context.checking(new Expectations() { {
			allowing(shoppingCart).isExchangeOrderShoppingCart(); will(returnValue(false));
			allowing(shoppingCart).getNumItems(); will(returnValue(1));
			allowing(shoppingCart).getStore(); will(returnValue(store));
			allowing(shoppingCart).getLocale(); will(returnValue(locale));
			allowing(store).getWarehouse(); will(returnValue(warehouse));
			allowing(shoppingCart).getLeafShoppingItems(); will(returnValue(items));
			allowing(shoppingCart).getShopper(); will(returnValue(shopper));
//			allowing(shopper).getCurrentShoppingCart(); will(returnValue(shoppingCart));
			//Constructing the exception requires the following
			allowing(shoppingItem).getProductSku(); will(returnValue(productSku));
			allowing(shoppingCart).getStore(); will(returnValue(store));
			allowing(warehouse).getCode(); will(returnValue("warehouseCode"));
			allowing(productSku).getProduct(); will(returnValue(product));
			allowing(warehouse).getUidPk(); will(returnValue(1L));
			allowing(productSku).getSkuCode(); will(returnValue("skuCode"));
			allowing(product).getDisplayName(locale); will(returnValue("myLocale"));
			allowing(shoppingItem).getQuantity(); will(returnValue(1));
			allowing(shopper).getUidPk(); will(returnValue(1L));
			allowing(allocationService).hasSufficientUnallocatedQty(productSku,
					1L, 1); will(returnValue(false));
//			allowing(shopper).updateTransientDataWith(with(any(CustomerSession.class)));
		} });

		CheckoutAction stockCheckerAction = new StockCheckerCheckoutAction() {
			@Override
			protected void setCartItemErrorMessage(final ShoppingItem cartItem, final ErrorMessage errorMessage, final Locale locale) {
				//do nothing
			}
		};
		((StockCheckerCheckoutAction) stockCheckerAction).setAllocationService(allocationService);

		CheckoutServiceImpl service = new CheckoutServiceImpl();
		service.setSetupActionList(Collections.singletonList(stockCheckerAction));

		service.checkoutInternal(shoppingCart, orderPayment, false, false, orderReturn, checkoutResults);
	}

	/**
	 * Test that when a product sku is unavailable (date range falls outside the current date),
	 * checkout will fail with an AvailabilityException and the shopping cart will not be emptied.
	 */
	@Test(expected = AvailabilityException.class)
	public void testUnavailableOnCheckout() {
		//If the ShoppingCart were cleared this would fail because the mock shopping cart is not expecting it.

		final Shopper shopper = context.mock(Shopper.class);
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Store store = context.mock(Store.class);
		final Warehouse warehouse = context.mock(Warehouse.class);
		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);
		final ProductSku productSku = context.mock(ProductSku.class);
		final Locale locale = Locale.CANADA;
		final OrderPayment orderPayment = context.mock(OrderPayment.class);
		final OrderReturn orderReturn = context.mock(OrderReturn.class);
		final CheckoutResults checkoutResults = context.mock(CheckoutResults.class);
		final List<ShoppingItem> items = new ArrayList<ShoppingItem>();
		items.add(shoppingItem);

		context.checking(new Expectations() { {
			allowing(shoppingCart).isExchangeOrderShoppingCart(); will(returnValue(false));
			allowing(shoppingCart).getNumItems(); will(returnValue(1));
			allowing(shoppingCart).getStore(); will(returnValue(store));
			allowing(shoppingCart).getLocale(); will(returnValue(locale));
			allowing(store).getWarehouse(); will(returnValue(warehouse));
			allowing(shoppingCart).getLeafShoppingItems(); will(returnValue(items));
			allowing(shoppingCart).getShopper(); will(returnValue(shopper));
			allowing(shopper).getUidPk(); will(returnValue(1L));
			allowing(shoppingItem).getProductSku(); will(returnValue(productSku));
//			allowing(shopper).getCurrentShoppingCart(); will(returnValue(shoppingCart));
			allowing(productSku).isWithinDateRange(); will(returnValue(false));
			//Constructing the exception requires the following
			allowing(productSku).getSkuCode(); will(returnValue("skuCode"));
//			allowing(shopper).updateTransientDataWith(with(any(CustomerSession.class)));
		} });

		CheckoutAction stockCheckerAction = new StockCheckerCheckoutAction() {
			@Override
			protected void verifyInventory(final ShoppingItem cartItem, final Warehouse warehouse, final Locale locale) {
				//do nothing - inventory is fine
			}
			@Override
			protected void setCartItemErrorMessage(final ShoppingItem cartItem, final ErrorMessage errorMessage, final Locale locale) {
				//do nothing
			}
		};

		CheckoutServiceImpl service = new CheckoutServiceImpl();
		service.setSetupActionList(Collections.singletonList(stockCheckerAction));

		service.checkoutInternal(shoppingCart, orderPayment, false, false, orderReturn, checkoutResults);
	}

	/**
	 * TODO: Test that when a customer account is updated before checkout,
	 * anonymous customers will be persisted using the CustomerService and
	 * the CustomerSessionService will also update the persisted CustomerSession.
	 * (Test the {@link CheckoutServiceImpl#updateCustomerAccount(ShoppingCart)} method).
	 */

}
