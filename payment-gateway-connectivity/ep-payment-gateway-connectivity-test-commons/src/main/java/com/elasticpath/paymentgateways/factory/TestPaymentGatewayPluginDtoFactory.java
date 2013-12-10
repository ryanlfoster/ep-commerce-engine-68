package com.elasticpath.paymentgateways.factory;

import java.math.BigDecimal;
import java.util.Calendar;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.CardDetailsPaymentMethod;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.impl.AddressDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.CardDetailsPaymentMethodImpl;
import com.elasticpath.plugin.payment.dto.impl.OrderPaymentDtoImpl;

/**
 * Factory to create dtos used in the payment gateway plugin converter for transaction request and responses.
 */
public final class TestPaymentGatewayPluginDtoFactory {

	private TestPaymentGatewayPluginDtoFactory() {
		// Static class
	}

	/**
	 * Creates a test {@link CardDetailsPaymentMethod} with test parameters.
	 * 
	 * @return created test {@link CardDetailsPaymentMethod}
	 */
	public static CardDetailsPaymentMethod createTestCardDetailsPaymentMethod() {
		CardDetailsPaymentMethodImpl testCardDetails = new CardDetailsPaymentMethodImpl();
		testCardDetails.setEmail("john.doe@elasticpath.com");
		testCardDetails.setCardType("001");
		testCardDetails.setUnencryptedCardNumber("4111111111111111");
		testCardDetails.setExpiryMonth("01");
		testCardDetails.setExpiryYear(String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 1));
		testCardDetails.setCvv2Code("123");
		testCardDetails.setReferenceId("12345");
		return testCardDetails;
	}

	/**
	 * Creates a test billing {@link AddressDto} with test parameters.
	 * 
	 * @return created test billing {@link AddressDto}
	 */
	public static AddressDto createTestBillingAddress() {
		AddressDtoImpl testBillingAddress = new AddressDtoImpl();
		testBillingAddress.setCity("Beverly Hills");
		testBillingAddress.setCountry("US");
		testBillingAddress.setFirstName("John");
		testBillingAddress.setLastName("Doe");
		testBillingAddress.setZipOrPostalCode("90210");
		testBillingAddress.setSubCountry("CA");
		testBillingAddress.setStreet1("123 Main Street");
		return testBillingAddress;
	}
	
	/**
	 * Creates a test {@link OrderPaymentDto} with test parameters.
	 *
	 * @return created test {@link OrderPaymentDto}
	 */
	public static OrderPaymentDto createTestOrderPayment() {
		OrderPaymentDto testOrderPayment = new OrderPaymentDtoImpl();
		testOrderPayment.setAmount(BigDecimal.TEN);
		testOrderPayment.setCurrencyCode("USD");
		testOrderPayment.setReferenceId("12345");
		return testOrderPayment;
	}
}
