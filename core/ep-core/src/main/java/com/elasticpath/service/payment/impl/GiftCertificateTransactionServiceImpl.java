package com.elasticpath.service.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.GiftCertificateCodeGenerator;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.GiftCertificateTransaction;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.exceptions.GiftCertificateException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.payment.GiftCertificateTransactionService;
import com.elasticpath.service.payment.gateway.GiftCertificateAuthorizationRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateCaptureRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateOrderPaymentDto;

/**
 * Gift certificate transaction service implementation. All payment gateway calls should end up calling the GiftCertificateTransactionService.
 */
public class GiftCertificateTransactionServiceImpl extends AbstractEpPersistenceServiceImpl implements GiftCertificateTransactionService {

	private GiftCertificateCodeGenerator giftCertificateCodeGenerator;

	private TimeService timeService;

	@Override
	public GiftCertificateTransaction saveOrMerge(final GiftCertificateTransaction giftCertificateTransaction) throws EpServiceException {
		return getPersistenceEngine().saveOrMerge(giftCertificateTransaction);
	}

	@Override
	public GiftCertificateTransaction add(final GiftCertificateTransaction giftCertificateTransaction) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().save(giftCertificateTransaction);
		return giftCertificateTransaction;
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return load(uid);
	}

	private GiftCertificateTransaction load(final long giftCertificateTransactionUid) throws EpServiceException {
		sanityCheck();
		GiftCertificateTransaction giftCertificateTransaction = null;
		if (giftCertificateTransactionUid <= 0) {
			giftCertificateTransaction = getBean(ContextIdNames.GIFT_CERTIFICATE_TRANSACTION);
		} else {
			giftCertificateTransaction = getPersistentBeanFinder().load(ContextIdNames.GIFT_CERTIFICATE_TRANSACTION, giftCertificateTransactionUid);
		}
		return giftCertificateTransaction;
	}
	
	/**
	 * Gets GiftCertificateTransactionService. New transaction should be creates in order to
	 * properly save GiftCertificateTransaction.  
	 *
	 * @return new bean of GiftCertificateTransactionService
	 */
	protected GiftCertificateTransactionService getGiftCertificateTransactionService() {
		return getBean(ContextIdNames.GIFT_CERTIFICATE_TRANSACTION_SERVICE);
	}

	@Override
	public GiftCertificateTransactionResponse preAuthorize(final GiftCertificateAuthorizationRequest authorizationRequest, 
			final AddressDto billingAddress) {
		GiftCertificate giftCertificate = authorizationRequest.getGiftCertificate();
		
		List<GiftCertificateTransaction> allTransactions = getGiftCertificateTransactions(giftCertificate);

		BigDecimal amount = authorizationRequest.getMoney().getAmount();
		if (getBalanceInternal(allTransactions, giftCertificate).compareTo(amount) >= 0) {
			GiftCertificateTransaction authTransaction = createTransaction(giftCertificate, 
					OrderPayment.AUTHORIZATION_TRANSACTION);
			authTransaction.setAmount(amount);
			authTransaction.setAuthorizationCode(getGiftCertificateCodeGenerator().generateCode());
			getGiftCertificateTransactionService().add(authTransaction);
			return createResponse(authTransaction.getAuthorizationCode(), giftCertificate.getGiftCertificateCode());
		} else {
			throw new GiftCertificateException("Not enough balance on this gift certificate");
		}
	}

	@Override
	public GiftCertificateTransactionResponse capture(final GiftCertificateCaptureRequest captureRequest) {
		BigDecimal amount = captureRequest.getMoney().getAmount();
		GiftCertificate giftCertificate = captureRequest.getGiftCertificate();
		List<GiftCertificateTransaction> allTransactions = getGiftCertificateTransactions(giftCertificate);

		GiftCertificateTransaction authTransaction = getAuthTransaction(allTransactions, captureRequest.getAuthorizationCode());
		if (authTransaction == null) {
			throw new GiftCertificateException("Associated authrization transaction could not be found for the capture payment.");
		}

		if (getReverseTransaction(allTransactions, authTransaction.getAuthorizationCode()) != null) {
			throw new GiftCertificateException("Associated authrization transaction has already been reversed.");
		}

		if (getCaptureTransaction(allTransactions, authTransaction.getAuthorizationCode()) != null) {
			throw new GiftCertificateException("This authrization transaction has already been captured.");
		}

		// now we're sure that the auth transaction has nor reversed transaction neither capture one associated.

		// Get money from the reserved amount, it won't affect the balance.
		if (authTransaction.getAmount().compareTo(amount) >= 0) {
			GiftCertificateTransaction captureTransaction = createTransaction(giftCertificate, OrderPayment.CAPTURE_TRANSACTION);
			captureTransaction.setAmount(amount);
			captureTransaction.setAuthorizationCode(authTransaction.getAuthorizationCode());
			
			String giftCertificateCode = giftCertificate.getGiftCertificateCode();
			captureRequest.setReferenceId(giftCertificateCode);
			getGiftCertificateTransactionService().add(captureTransaction);			
			return createResponse(null, giftCertificateCode);
		} else {
			throw new GiftCertificateException("Something wrong on this gift certificate, the reserved amount can't cover this payment.");
		}
	}

	@Override
	public void reversePreAuthorization(final GiftCertificateOrderPaymentDto orderPayment) {
		List<GiftCertificateTransaction> allTransactions = getGiftCertificateTransactions(orderPayment.getGiftCertificate());

		BigDecimal amount = orderPayment.getAmount();

		GiftCertificateTransaction authTransaction = getAuthTransaction(allTransactions, orderPayment.getAuthorizationCode());
		if (authTransaction == null) {
			throw new GiftCertificateException("Associated authrization transaction could not be found for the reverse payment.");
		}

		if (getReverseTransaction(allTransactions, authTransaction.getAuthorizationCode()) != null) {
			throw new GiftCertificateException("Associated authrization transaction has already been reversed.");
		}
		
		if (getCaptureTransaction(allTransactions, authTransaction.getAuthorizationCode()) != null) {
			throw new GiftCertificateException("Associated authrization transaction has already been captured.");
		}

		if (!amount.equals(authTransaction.getAmount())) {
			throw new GiftCertificateException("Reversed amound should equal to authorized amount for gift certificate.");
		}

		GiftCertificateTransaction reverseTransaction = createTransaction(orderPayment.getGiftCertificate(), OrderPayment.REVERSE_AUTHORIZATION);
		reverseTransaction.setAmount(amount);
		reverseTransaction.setAuthorizationCode(authTransaction.getAuthorizationCode());
		orderPayment.setReferenceId(orderPayment.getGiftCertificate().getGiftCertificateCode());
		getGiftCertificateTransactionService().add(reverseTransaction);
	}

	@Override
	public void sale(final GiftCertificateOrderPaymentDto orderPayment, final AddressDto billingAddress) {
		// TODO: implement
	}

	@Override
	public BigDecimal getBalance(final GiftCertificate giftCertificate) {
		List<GiftCertificateTransaction> allTransactions = getGiftCertificateTransactions(giftCertificate);
		return getBalanceInternal(allTransactions, giftCertificate);
	}
	
	@Override
	public BigDecimal getReservedAmount(final GiftCertificate giftCertificate) {
		List<GiftCertificateTransaction> allTransactions = getGiftCertificateTransactions(giftCertificate);
		return getBalanceInternal(allTransactions, giftCertificate);
	}

	private BigDecimal getBalanceInternal(final List<GiftCertificateTransaction> allTransactions, final GiftCertificate giftCertificate) {
		BigDecimal balance = giftCertificate.getPurchaseAmount();
		BigDecimal allocatedAmount = calcTransactionBalance(allTransactions);

		return balance.subtract(allocatedAmount);
	}

	/**
	 * Calculate authorized and captured amount by transactions.
	 *
	 * @param allTransactions list of included transactions.
	 * @return total authorized and captured amount.
	 */
	public BigDecimal calcTransactionBalance(final List<GiftCertificateTransaction> allTransactions) {
				/**
		        Auth_transaction
		              |
		Has_reversed_transaction_associated
		          /     \
		       YES      NO
		    /             \
		  minus=0       Has_capture_transaction
		                           /      \
		                       YES          NO
		                      /              \
		            Minus=Captured        Minus=Authed
		*/
		List<GiftCertificateTransaction> authTransactions = getAuthTransactions(allTransactions);
		BigDecimal allocatedAmount = BigDecimal.ZERO;
		for (GiftCertificateTransaction authTransaction : authTransactions) {
			final BigDecimal dueTransaction;
			GiftCertificateTransaction reverseTarnsaction = getReverseTransaction(allTransactions, authTransaction.getAuthorizationCode());
			if (reverseTarnsaction == null) {
				GiftCertificateTransaction captureTarnsaction = getCaptureTransaction(allTransactions, authTransaction.getAuthorizationCode());
				if (captureTarnsaction == null) {
					dueTransaction = authTransaction.getAmount();
				} else {
					dueTransaction = captureTarnsaction.getAmount();
				}
			} else {
				dueTransaction = BigDecimal.ZERO;
			}
			allocatedAmount = allocatedAmount.add(dueTransaction);
		}
		return allocatedAmount;
	}

	private GiftCertificateTransaction getAuthTransaction(final List<GiftCertificateTransaction> allTransactions, final String authorizationCode) {
		List<GiftCertificateTransaction> authTransactions = new ArrayList<GiftCertificateTransaction>();
		for (GiftCertificateTransaction giftCertificateTransaction : getAuthTransactions(allTransactions)) {
			if (authorizationCode.equals(giftCertificateTransaction.getAuthorizationCode())) {
				authTransactions.add(giftCertificateTransaction);
			}
		}

		if (authTransactions.isEmpty()) {
			return null;
		} else if (authTransactions.size() > 1) {
			throw new GiftCertificateException("Found more than 1 gift certificate authorized transactions for the auth code.");
		}
		return authTransactions.get(0);
	}

	private List<GiftCertificateTransaction> getAuthTransactions(final List<GiftCertificateTransaction> allTransactions) {
		List<GiftCertificateTransaction> authTransactions = new ArrayList<GiftCertificateTransaction>();

		for (GiftCertificateTransaction giftCertificateTransaction : allTransactions) {
			if (OrderPayment.AUTHORIZATION_TRANSACTION.equals(giftCertificateTransaction.getTransactionType())) {
				authTransactions.add(giftCertificateTransaction);
			}
		}
		return authTransactions;
	}

	private GiftCertificateTransaction getReverseTransaction(final List<GiftCertificateTransaction> allTransactions,
			final String authorizationCode) {
		List<GiftCertificateTransaction> reverseTransactions = new ArrayList<GiftCertificateTransaction>();

		for (GiftCertificateTransaction giftCertificateTransaction : allTransactions) {
			if (OrderPayment.REVERSE_AUTHORIZATION.equals(giftCertificateTransaction.getTransactionType())
					&& authorizationCode.equals(giftCertificateTransaction.getAuthorizationCode())) {
				reverseTransactions.add(giftCertificateTransaction);
			}
		}

		if (reverseTransactions.isEmpty()) {
			return null;
		} else if (reverseTransactions.size() > 1) {
			throw new GiftCertificateException("Found more than 1 gift certificate reverse transactions for the auth code.");
		}
		return reverseTransactions.get(0);
	}

	private GiftCertificateTransaction getCaptureTransaction(final List<GiftCertificateTransaction> allTransactions,
			final String authorizationCode) {

		List<GiftCertificateTransaction> captureTransactions = new ArrayList<GiftCertificateTransaction>();

		for (GiftCertificateTransaction giftCertificateTransaction : allTransactions) {
			if (OrderPayment.CAPTURE_TRANSACTION.equals(giftCertificateTransaction.getTransactionType())
					&& authorizationCode.equals(giftCertificateTransaction.getAuthorizationCode())) {
				captureTransactions.add(giftCertificateTransaction);
			}
		}

		if (captureTransactions.isEmpty()) {
			return null;
		} else if (captureTransactions.size() > 1) {
			throw new GiftCertificateException("Found more than 1 gift certificate capture transactions for the auth code.");
		}
		return captureTransactions.get(0);
	}

	@Override
	public List<GiftCertificateTransaction> getGiftCertificateTransactions(final GiftCertificate giftCertificate) {
		final List<GiftCertificateTransaction> allGCTransactions = getPersistenceEngine().retrieveByNamedQuery("GIFT_CERTIFICATE_TRANSACTIONS",
				giftCertificate.getUidPk());
		return allGCTransactions;
	}

	private GiftCertificateTransaction createTransaction(final GiftCertificate giftCertificate, final String transactionType) {
		GiftCertificateTransaction certificateTransaction = getBean(ContextIdNames.GIFT_CERTIFICATE_TRANSACTION);
		certificateTransaction.setTransactionType(transactionType);
		certificateTransaction.setCreatedDate(timeService.getCurrentTime());
		certificateTransaction.setGiftCertificate(giftCertificate);
		return certificateTransaction;
	}
	
	private GiftCertificateTransactionResponse createResponse(final String authorizationCode, final String giftCertificateCode) {
		GiftCertificateTransactionResponse response = new GiftCertificateTransactionResponseImpl();
		response.setAuthorizationCode(authorizationCode);
		response.setGiftCertificateCode(giftCertificateCode);
		return response;
	}
	
	@Override
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * @param giftCertificateCodeGenerator the giftCertificateCodeGenerator to set
	 */
	public void setGiftCertificateCodeGenerator(final GiftCertificateCodeGenerator giftCertificateCodeGenerator) {
		this.giftCertificateCodeGenerator = giftCertificateCodeGenerator;
	}

	/**
	 * @return the giftCertificateCodeGenerator
	 */
	public GiftCertificateCodeGenerator getGiftCertificateCodeGenerator() {
		return giftCertificateCodeGenerator;
	}

	

}
