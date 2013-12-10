package com.elasticpath.service.payment;

import java.math.BigDecimal;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.payment.GiftCertificateTransaction;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.payment.gateway.GiftCertificateAuthorizationRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateCaptureRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateOrderPaymentDto;
import com.elasticpath.service.payment.impl.GiftCertificateTransactionResponse;

/**
 * Gift certificate transaction service. All payment gateway calls should end up calling the GiftCertificateTransactionService.
 */
public interface GiftCertificateTransactionService extends EpPersistenceService {

	/**
	 * save or merge the given GiftCertificateTransaction.
	 * 
	 * @param giftCertificateTransaction the Gift Certificate Transaction to merge
	 * @return giftCertificate
	 * @throws EpServiceException - in case of any errors
	 */
	GiftCertificateTransaction saveOrMerge(final GiftCertificateTransaction giftCertificateTransaction) throws EpServiceException;

	/**
	 * Adds the given GiftCertificateTransaction.
	 * 
	 * @param giftCertificateTransaction the Gift Certificate Transaction to add
	 * @return the persisted instance of Gift Certificate Transaction
	 * @throws EpServiceException - in case of any errors
	 */
	GiftCertificateTransaction add(final GiftCertificateTransaction giftCertificateTransaction) throws EpServiceException;

	/**
	 * Generic get method for all persistable domain models.
	 * 
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Object getObject(final long uid) throws EpServiceException;

	/**
	 * Pre-authorize a payment.
	 * 
	 * @param authorizationRequest the {@link GiftCertificateAuthorizationRequest}
	 * @param billingAddress the name and address of the person being billed
	 * @return GiftCertificateTransactionResponse the transaction response 
	 */
	GiftCertificateTransactionResponse preAuthorize(final GiftCertificateAuthorizationRequest authorizationRequest, 
			final AddressDto billingAddress);

	/**
	 * Captures a payment on a previously authorized amount.
	 * 
	 * @param captureRequest the {@link GiftCertificateCaptureRequest}
	 * @return GiftCertificateTransactionResponse the transaction response	 
	 */
	GiftCertificateTransactionResponse capture(final GiftCertificateCaptureRequest captureRequest);

	/**
	 * Reverse a previous pre-authorization.
	 * 
	 * @param orderPayment the payment that was previously pre-authorized
	 */
	void reversePreAuthorization(final GiftCertificateOrderPaymentDto orderPayment);

	/**
	 * Make a payment without pre-authorization.
	 * 
	 * @param orderPayment the payment to be preauthorized
	 * @param billingAddress the name and address of the person being billed	 
	 */
	void sale(final GiftCertificateOrderPaymentDto orderPayment, final AddressDto billingAddress);

	/**
	 * Obtains the balance of the gift certificate.
	 * 
	 * @param giftCertificate the gift certificate
	 * @return the balance amount
	 */
	BigDecimal getBalance(GiftCertificate giftCertificate);
	
	/**
	 * 
	 * Obtains the reserved amount of the gift certificate.
	 * @param giftCertificate the gift certificate
	 * @return the reserved amount
	 */
	BigDecimal getReservedAmount(final GiftCertificate giftCertificate);

	/**
	 * Set the time service.
	 * 
	 * @param timeService the <code>TimeService</code> instance.
	 */
	void setTimeService(final TimeService timeService);
	
	/**
	 * Calculate authorized and captured amount by transactions.
	 *
	 * @param allTransactions list of included transactions.
	 * @return total authorized and captured amount.
	 */
	BigDecimal calcTransactionBalance(final List<GiftCertificateTransaction> allTransactions);	
	
	/**
	 * Retrieves a list of {@link com.elasticpath.domain.payment.GiftCertificateTransaction}s for the given giftCertificate.
	 *
	 * @param giftCertificate the given {@link com.elasticpath.domain.catalog.GiftCertificate}
	 * @return total authorized and captured amount.
	 */
	List<GiftCertificateTransaction> getGiftCertificateTransactions(final GiftCertificate giftCertificate);

}
