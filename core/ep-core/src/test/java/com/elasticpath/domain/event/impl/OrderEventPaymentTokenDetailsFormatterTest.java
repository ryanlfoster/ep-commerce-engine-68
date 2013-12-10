package com.elasticpath.domain.event.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.event.OrderEventPaymentDetailFormatter;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;



/**
 * Test for {@link OrderEventPaymentTokenDetailsFormatter}.
 */
public class OrderEventPaymentTokenDetailsFormatterTest {
	
	/**
	 * Ensure correct format for payment token.
	 */
	@Test
	public void ensureCorrectFormatForPaymentToken() {

		OrderPayment orderPayment = new OrderPaymentImpl();
		String testTokenDisplayValue = "test token display value";
		PaymentToken paymentToken = new PaymentTokenImpl.TokenBuilder()
			.withDisplayValue(testTokenDisplayValue)
			.build(); 
		orderPayment.usePaymentToken(paymentToken);
		
		OrderEventPaymentDetailFormatter formatter = new OrderEventPaymentTokenDetailsFormatter();
		String paymentDetails = formatter.formatPaymentDetails(orderPayment);
		assertThat("Details should have token display value", paymentDetails, containsString(testTokenDisplayValue));
	}

}
