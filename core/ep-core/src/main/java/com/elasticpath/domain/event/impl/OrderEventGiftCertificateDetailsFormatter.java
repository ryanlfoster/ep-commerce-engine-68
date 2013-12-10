package com.elasticpath.domain.event.impl;

import com.elasticpath.domain.event.OrderEventPaymentDetailFormatter;
import com.elasticpath.domain.order.OrderPayment;

/**
 * Formats Gift Certificate Details for {@link com.elasticpath.domain.order.OrderEvent}. 
 */
public class OrderEventGiftCertificateDetailsFormatter implements OrderEventPaymentDetailFormatter {

	private static final char SPACE_CHAR = ' ';

	@Override
	public String formatPaymentDetails(final OrderPayment orderPayment) {
		StringBuffer creditCardDetails = new StringBuffer();
		creditCardDetails.append(SPACE_CHAR);
		creditCardDetails.append(orderPayment.getGiftCertificate().displayMaskedGiftCertificateCode());
		return creditCardDetails.toString();
	}

}
