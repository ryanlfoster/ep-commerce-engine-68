/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.payment;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides payment gateway related services.
 */
public interface PaymentGatewayService extends EpPersistenceService {
	/**
	 * Saves or updates a given <code>PaymentGateway</code>.
	 * 
	 * @param paymentGateway the <code>PaymentGateway</code> to save or update
	 * @return the updated instance of the paymentGateway object
	 * @throws EpServiceException in case of any errors
	 * @see PaymentGateway
	 */
	PaymentGateway saveOrUpdate(final PaymentGateway paymentGateway) throws EpServiceException;

	/**
	 * Deletes a <code>PaymentGateway</code>.
	 * 
	 * @param paymentGateway the <code>PaymentGateway</code> to remove
	 * @throws EpServiceException in case of any errors
	 */
	void remove(final PaymentGateway paymentGateway) throws EpServiceException;

	/**
	 * Gets a <code>PaymentGateway</code> with the given UID. Return null if no matching records exist.
	 * 
	 * @param paymentGatewayUid the <code>PaymentGateway</code> UID
	 * @return the <code>PaymentGateway</code> if the UID exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	PaymentGateway getGateway(final long paymentGatewayUid) throws EpServiceException;

	/**
	 * Gets a list of all payment gateway UIDs.
	 * 
	 * @return a list of all payment gateway UIDs
	 * @throws EpServiceException in case of any errors
	 */
	List<Long> findAllPaymentGatewayUids() throws EpServiceException;

	/**
	 * Gets a list of all payment gateways.
	 * 
	 * @return a list of all payment gateways
	 * @throws EpServiceException in case of any errors
	 */
	List<PaymentGateway> findAllPaymentGateways() throws EpServiceException;

	/**
	 * Returns a list of <code>PaymentGateway</code> base on given uids.
	 * 
	 * @param gatewayUids a collection of paymentgateway uids
	 * @return a list of <code>PaymentGateway</code>s
	 */
	List<PaymentGateway> findByUids(final Collection<Long> gatewayUids);

	/**
	 * Return a list of <code>PaymentGateway</code> based on given gateway names.
	 * 
	 * @param gatewayNames a collection of paymentgateway names.
	 * @return a list of <code>PaymentGateway</code>s
	 */
	 List<PaymentGateway> findByNames(final Collection<String> gatewayNames);
	
	/**
	 * Gets the set of supported payment gateways.
	 * 
	 * @return the set of supported payment gateways
	 */
	Set<String> getSupportedPaymentGateways();

	/**
	 * Gets the payment gateway default properties.
	 * 
	 * @param gatewayType the gateway type
	 * @return the payment gateway default properties
	 */
	Properties getPaymentGatewayDefaultProperties(String gatewayType);

	/**
	 * Creates the or update payment gateway.
	 *
	 * @param name the name
	 * @param type the type
	 * @param prop the prop
	 * @return the payment gateway
	 */
	PaymentGateway addPaymentGateway(String name, String type, Properties prop);

	/**
	 * Gets the set of supported credit card types.
	 * 
	 * @return the set of supported credit card types.
	 */
	Set<String> getSupportedCreditCardTypes();

	/**
	 * Get the list of payment gateways (Long) uidPk in use.
	 * 
	 * @return the list of uids of <code>PaymentGateway</code>s in use.
	 * @throws EpServiceException - in case of any errors
	 */
	List<Long> getPaymentGatewaysInUse() throws EpServiceException;

	/**
	 * Get the payment gateway by payment gateway name.
	 * 
	 * @param paymentGatewayName name of the payment gateway
	 * @return the <code>PaymentGateway</code> in use.
	 * @throws EpServiceException - in case of any errors
	 */
	PaymentGateway getGatewayByName(final String paymentGatewayName);
}
