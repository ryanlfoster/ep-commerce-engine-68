/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart;

import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.EpService;

/**
 * Provides checkout-related services.
 */
public interface CheckoutService extends EpService {

	/**
	 * Processes an order for the items in the specified shopping cart. This will create an order, update customer account information, and empty the
	 * shopping cart. This method currently requires that the order payment has been successfully processed.
	 *
	 * @param shoppingCart the {@link ShoppingCart}.
	 * @param orderPayment the orderPayment representing the payment detail information.
	 * This OrderPayment simply keeps track of how the customer will be paying; it does not necessarily represent all
	 * of the payments on the order. For example, if paying only by Gift Certificate then the OrderPayment will
	 * have the payment type set to GiftCertificate, but there will be no information on the gift certificates
	 * being used. That information is contained within the ShoppingCart object.
	 * <br>Javadoc tags not used due to a Checkstyle processing error:
	 *            throws CardDeclinedException if the card is declined throws CardExpiredException if the card has expired throws CardErrorException
	 *            if there was an error processing the given information throws EpServiceException if the payment processing fails throws
	 *            InsufficientInventoryException if there is not enough inventory to complete the order
	 * @return results of the checkout
	 * @deprecated use checkout(shoppingCart, orderPayment, throwExceptions) 
	 */
	@Deprecated
	CheckoutResults checkout(final ShoppingCart shoppingCart, final OrderPayment orderPayment);
	
	/**
	 * Processes an order for the items in the specified shopping cart. This will create an order, update customer account information, and empty the
	 * shopping cart. This method currently requires that the order payment has been successfully processed.
	 *
	 * @param shoppingCart the {@link ShoppingCart}.
	 * @param orderPayment the orderPayment representing the payment detail information.
	 * This OrderPayment simply keeps track of how the customer will be paying; it does not necessarily represent all
	 * of the payments on the order. For example, if paying only by Gift Certificate then the OrderPayment will
	 * have the payment type set to GiftCertificate, but there will be no information on the gift certificates
	 * being used. That information is contained within the ShoppingCart object.
	 * <br>Javadoc tags not used due to a Checkstyle processing error:
	 * throws CardDeclinedException if the card is declined throws CardExpiredException if the card has expired throws CardErrorException
	 * if there was an error processing the given information throws EpServiceException if the payment processing fails throws
	 * InsufficientInventoryException if there is not enough inventory to complete the order
	 * @param throwExceptions whether to throw exceptions or just return them in the results.
	 * @return results of the checkout
	 */
	CheckoutResults checkout(final ShoppingCart shoppingCart, final OrderPayment orderPayment, final boolean throwExceptions);
	
	/**
	 * Retrieve the valid shippingServiceLevels based on the given shoppingCart,
	 * and set the first one of the valid shippingServiceLevels as the selected shipping option by default.
	 * @param shoppingCart the current shopping cart.
	 */
	void retrieveShippingOption(final ShoppingCart shoppingCart);

	/**
	 * Complete the tax related calculation for the given shoppingCart.
	 * @param shoppingCart the current shopping cart.
	 */
	void calculateTaxAndBeforeTaxValue(final ShoppingCart shoppingCart);

	/**
	 * Processes an order for the items in the specified exchange shopping cart.
	 * This will create an order, update customer account information.
	 * Exchange shopping cart is temporary entity that's why it wont be updated to the DB.
	 * Order Payment must be either null which means that no order payments required now or or be fully populated and contain amount to be charged.
	 *
	 * @param exchange the exchange, order exchange is being created for.
	 * @param orderPayment the orderPayment representing the payment detail information. Amount must be specified.
	 * If null, no order processing will be done.
	 * @param awaitExchangeCompletion specifies if physical return required for exchange.
	 *
	 * Javadoc tags not used due to a Checkstyle processing error:
	 * throws CardDeclinedException if the card is declined
	 * throws CardExpiredException if the card has expired
	 * throws CardErrorException if there was an error processing the given information
	 * throws EpServiceException if the payment processing fails
	 * throws InsufficientInventoryException if there is not enough inventory to complete the order
	 */
	void checkoutExchangeOrder(final OrderReturn exchange, final OrderPayment orderPayment, final boolean awaitExchangeCompletion);
}
