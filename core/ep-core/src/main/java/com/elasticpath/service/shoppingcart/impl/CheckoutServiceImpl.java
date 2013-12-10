/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.actions.CheckoutAction;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutAction;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;
import com.elasticpath.service.shoppingcart.actions.impl.CheckoutActionContextImpl;
import com.elasticpath.service.shoppingcart.actions.impl.FinalizeCheckoutActionContextImpl;

/**
 * Provides a service to execute a shopping cart checkout.
 */
public class CheckoutServiceImpl extends AbstractEpServiceImpl implements CheckoutService {

	private static final Logger LOG = Logger.getLogger(CheckoutServiceImpl.class);

	private List<CheckoutAction> setupActionList = Collections.emptyList();

	private List<ReversibleCheckoutAction> reversibleActionList = Collections.emptyList();

	private List<FinalizeCheckoutAction> finalizeActionList = Collections.emptyList();

	private ShippingServiceLevelService shippingServiceLevelService;

	@Override
	public void retrieveShippingOption(final ShoppingCart shoppingCart) {
		if (shoppingCart.requiresShipping()) {
			final List<ShippingServiceLevel> validShippingServiceLevels = this.shippingServiceLevelService
			.retrieveShippingServiceLevel(shoppingCart);
			shoppingCart.setShippingServiceLevelList(validShippingServiceLevels);
			if (validShippingServiceLevels.isEmpty()) {
				shoppingCart.clearSelectedShippingServiceLevel();
			} else if (!this.isValidShippingServiceLevelId(validShippingServiceLevels, shoppingCart.getSelectedShippingServiceLevel().getUidPk())) {
				shoppingCart.setSelectedShippingServiceLevelUid(validShippingServiceLevels.get(0).getUidPk());
			}
		} else {
			shoppingCart.setShippingServiceLevelList(null);
			shoppingCart.clearSelectedShippingServiceLevel();
		}
	}

	private boolean isValidShippingServiceLevelId(final List<ShippingServiceLevel> validServiceLevels, final long selectedId) {
		for (final ShippingServiceLevel shippingServiceLevel : validServiceLevels) {
			final ShippingServiceLevel currServiceLevel = shippingServiceLevel;
			if (currServiceLevel.getUidPk() == selectedId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void calculateTaxAndBeforeTaxValue(final ShoppingCart shoppingCart) {
		shoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices();
	}

	@Override
	public CheckoutResults checkout(final ShoppingCart shoppingCart, final OrderPayment orderPayment) {
		return checkout(shoppingCart, orderPayment, true);
	}

	@Override
	public CheckoutResults checkout(final ShoppingCart shoppingCart, final OrderPayment orderPayment, final boolean throwExceptions) {
		final CheckoutResults checkoutResults = getBean(ContextIdNames.CHECKOUT_RESULTS);
		if (!shoppingCart.isExchangeOrderShoppingCart()) {
			try {
				this.checkoutInternal(shoppingCart, orderPayment, false, false, null, checkoutResults);
			} catch (EpSystemException exception) {
				if (throwExceptions || checkoutResults.getOrder() == null) {
					throw exception;
				}
				checkoutResults.setOrderFailed(true);
				checkoutResults.setFailureCause(exception);
			}
		}
		return checkoutResults;
	}

	@Override
	public void checkoutExchangeOrder(final OrderReturn exchange, final OrderPayment orderPayment, final boolean awaitExchangeCompletion) {
		final CheckoutResults checkoutResults = getBean(ContextIdNames.CHECKOUT_RESULTS);
		final ShoppingCart shoppingCart = exchange.getExchangeShoppingCart();
		if (shoppingCart.isExchangeOrderShoppingCart()) {
			this.checkoutInternal(shoppingCart, orderPayment, true, awaitExchangeCompletion, exchange, checkoutResults);
		}
	}

	/**
	 * Internal checkout method performs the actual checkout operations.
	 *
	 * @param shoppingCart the {@link ShoppingCart}.
	 * @param templateOrderPayment orderPayment representing the payment detail information (not including gift certificates)
	 * @param isOrderExchange whether this order is part of an exchange
	 * @param awaitExchangeCompletion whether the order created via this checkout must wait for completion of a physical
	 * exchange before being filled
	 * @param exchange the OrderReturn linked to the order that is to be created as a result of this checkout
	 * @param checkoutResults an empty {@code CheckoutResults} to be populated with the results of the checkout
	 */
	protected void checkoutInternal(final ShoppingCart shoppingCart, final OrderPayment templateOrderPayment, final boolean isOrderExchange,
			final boolean awaitExchangeCompletion, final OrderReturn exchange, final CheckoutResults checkoutResults) {

		final CheckoutActionContext actionContext = createActionContext(
				shoppingCart, templateOrderPayment, isOrderExchange,
				awaitExchangeCompletion, exchange);

		// Keep track of ReversibleCheckoutAction objects so we can rollback if necessary.
		final List<ReversibleCheckoutAction> executedActions = new ArrayList<ReversibleCheckoutAction>();

		LOG.debug("Checkout process started.");

		// Execute setup actions
		for (final CheckoutAction action : setupActionList) {
			LOG.debug("Executing checkout action " + action.getClass().getName());
			action.execute(actionContext);
		}

		// Execute reversible actions
		try {
			for (final ReversibleCheckoutAction action : reversibleActionList) {
				executedActions.add(action);
				LOG.debug("Executing checkout action " + action.getClass().getName());
				action.execute(actionContext);
			}
		} catch (final EpSystemException e) {
			rollbackCheckout(executedActions, actionContext, e);
			throw e;
		} catch (final Exception e) {
			rollbackCheckout(executedActions, actionContext, e);
			throw new EpServiceException("Checkout failed.", e);
		} finally {
			checkoutResults.setOrder(actionContext.getOrder());
		}

		final FinalizeCheckoutActionContext finalizeActionContext = createFinalizeActionContext(actionContext);

		// Execute finalize actions
		for (final FinalizeCheckoutAction action : finalizeActionList) {
			LOG.debug("Executing checkout action " + action.getClass().getName());
			action.execute(finalizeActionContext);
		}

		checkoutResults.setEmailFailed(finalizeActionContext.isEmailFailed());

		// TODO: Suggest that the completed order be put on the checkoutResult class instead
		// of the shopping cart.

		shoppingCart.setCompletedOrder(actionContext.getOrder());

		LOG.debug("Checkout process completed: Created order " + actionContext.getOrder().getOrderNumber());
	}

	/**
	 * Create an actionContext object for use by the SetupCheckoutAction and ReversibleCheckoutAction
	 * commands.
	 *
	 * Note: Protected to allow for extension.
	 *
	 * @param shoppingCart the {@link ShoppingCart}
	 * @param templateOrderPayment a place holder for the order payment details (credit card #, etc)
	 * @param isOrderExchange is this an exchange?
	 * @param awaitExchangeCompletion ???
	 * @param exchange the order return details
	 * @return a populated actionContext instance
	 */
	protected CheckoutActionContext createActionContext(
			final ShoppingCart shoppingCart,
			final OrderPayment templateOrderPayment,
			final boolean isOrderExchange,
			final boolean awaitExchangeCompletion, final OrderReturn exchange) {
		final CheckoutActionContext actionContext = new CheckoutActionContextImpl(shoppingCart, templateOrderPayment,
				isOrderExchange, awaitExchangeCompletion, exchange);
		return actionContext;
	}

	/**
	 * Create a finalizeActionContext object for use by the finalizeCheckoutAction commands.
	 *
	 * @param actionContext the original actionContext
	 * @return a populated finalizeActionContext instance
	 */
	protected FinalizeCheckoutActionContextImpl createFinalizeActionContext(
			final CheckoutActionContext actionContext) {
		return new FinalizeCheckoutActionContextImpl(actionContext);
	}

	private void rollbackCheckout(final List<ReversibleCheckoutAction> executedActions,
			final CheckoutActionContext actionContext, final Exception exception) {
        if (exception instanceof PaymentProcessingException) {
            LOG.debug("Payment processing error occurred during checkout", exception);
        } else {
    		LOG.error("Error occurred during checkout", exception);
        }
		LOG.debug("Checkout rollback process started.");
		for (int index = executedActions.size() - 1; index >= 0; index--) {
			final ReversibleCheckoutAction action = executedActions.get(index);
			LOG.debug("Executing checkout action rollback " + action.getClass().getName());
			action.rollback(actionContext);
		}
		LOG.debug("Checkout rollback process completed.");
	}

	public void setShippingServiceLevelService(final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

	protected List<CheckoutAction> getSetupActionList() {
		return setupActionList;
	}

	public void setSetupActionList(final List<CheckoutAction> setupActionList) {
		this.setupActionList = setupActionList;
	}

	protected List<ReversibleCheckoutAction> getReversibleActionList() {
		return reversibleActionList;
	}

	public void setReversibleActionList(final List<ReversibleCheckoutAction> reversibleActionList) {
		this.reversibleActionList = reversibleActionList;
	}

	protected List<FinalizeCheckoutAction> getFinalizeActionList() {
		return finalizeActionList;
	}

	public void setFinalizeActionList(final List<FinalizeCheckoutAction> finalizeActionList) {
		this.finalizeActionList = finalizeActionList;
	}

}
