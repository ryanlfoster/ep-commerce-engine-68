/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * The comparator factory which uses to create some comparators for Payments.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class PaymentsComparatorFactory {

	private PaymentsComparatorFactory() {

	}

	/**
	 * Creates an OrderPayment comparator.
	 *
	 * @return an OrderPayment comparator instance
	 */
	public static Comparator<OrderPayment> getOrderPaymentDateCompatator() {
		return new Comparator<OrderPayment>() {
			/**
			 * Compare by created date in descending order.
			 */
			public int compare(final OrderPayment arg0, final OrderPayment arg1) {
				int comparedByDate = arg1.getCreatedDate().compareTo(arg0.getCreatedDate());
				if (comparedByDate == 0) {
					// descending
					return arg1.getAmount().compareTo(arg0.getAmount());
				}
				return comparedByDate;
			}
		};
	}

	/**
	 * Gets a {@link Comparator} which compares payment sources of two {@link OrderPayment}s. 
	 * 
	 * @return payment source comparator
	 */
	public static Comparator<OrderPayment> getPaymentSourceComparator() {
		return new Comparator<OrderPayment>() {
			@Override
			public int compare(final OrderPayment payment1, final OrderPayment payment2) {
				if (payment1 == payment2) { // NOPMD
					return 0;
				}
				
				if (payment1 == null) {
					return 1;
				}
				
				if (payment2 == null) {
					return -1;
				}

				if (payment1.getPaymentMethod().equals(payment2.getPaymentMethod())) {
					if (PaymentType.CREDITCARD.equals(payment1.getPaymentMethod())) {
						return new CreditCardOrderPaymentComparator().compare(payment1, payment2);
					}

					if (PaymentType.GIFT_CERTIFICATE.equals(payment1.getPaymentMethod())) {
						return new GiftCertificateOrderPaymentComparator().compare(payment1, payment2);
					}
					
					if (PaymentType.PAYMENT_TOKEN.equals(payment1.getPaymentMethod())) {
						return new PaymentTokenOrderPaymentComparator().compare(payment1, payment2);
					}
				}
				
				return payment1.getPaymentMethod().compareTo(payment2.getPaymentMethod());
			}
		};
	}

	/**
	 * Return list of all unique payment sources for the specified list of payments.
	 * 
	 * @param transactionType the transaction type of the payments that is desired 
	 * 		  or <code>null</code> in case no filtering should be applied by this criterion. 
	 * 		  For the available types see {@link OrderPayment}.
	 * @param allPayments list of all payments from which payment sources should be mined.
	 * @param exceptionalPaymentType the payment types which are not be include to result list
	 * @return list of unique payment sources without payments which have paymentType from exceptionalPaymentType array.
	 */
	public static List<OrderPayment> getListOfUniquePayments(
			final String transactionType,
			final Iterable<OrderPayment> allPayments,
			final PaymentType... exceptionalPaymentType) {

		TreeSet<OrderPayment> unique = new TreeSet<OrderPayment>(getPaymentSourceComparator());
		List<PaymentType> excludedPaymentTypes = Arrays.asList(exceptionalPaymentType);

		for (OrderPayment orderPayment : allPayments) {
			if (!excludedPaymentTypes.contains(orderPayment.getPaymentMethod())
					&& (transactionType == null || transactionType.equals(orderPayment.getTransactionType()))
					&& orderPayment.getStatus() != OrderPaymentStatus.FAILED) {
				unique.add(orderPayment);
			}
		}

		return new ArrayList<OrderPayment>(unique);
	}

}
