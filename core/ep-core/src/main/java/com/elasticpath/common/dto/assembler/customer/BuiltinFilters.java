package com.elasticpath.common.dto.assembler.customer;

import com.elasticpath.common.dto.customer.CreditCardDTO;
import com.elasticpath.domain.customer.CustomerCreditCard;

/**
 * Simple builtin credit card filters.
 * Note that we do not ship with one that copies credit card numbers out of the box, although the two here can be used to write your own.
 */
@SuppressWarnings("PMD")
public abstract class BuiltinFilters {

	/**
	 * A simple filter which always returns null.
	 */
	public static final CreditCardFilter EMPTYING = new CreditCardFilter() {
		
		@Override
		public CreditCardDTO filter(final CustomerCreditCard card) {
			return null;
		}
	};

	/**
	 * A simple credit card filter which returns a static fake credit card number value, depending on the card's type.
	 * If the card's type cannot be matched null is returned.
	 */
	public static final CreditCardFilter STATIC = new CreditCardFilter() {

		@Override
		public CreditCardDTO filter(final CustomerCreditCard sourceCard) {
			String staticNumber = getStaticNumber(sourceCard.getCardType());
			if (staticNumber == null) {
				return null;
			}
			
			CreditCardDTO targetCard = new CreditCardDTO();

			if (sourceCard.getBillingAddress() != null) {
				targetCard.setBillingAddressGuid(sourceCard.getBillingAddress().getGuid());
			}
			targetCard.setGuid(sourceCard.getGuid());
			targetCard.setCardHolderName(sourceCard.getCardHolderName());
			targetCard.setCardType(sourceCard.getCardType());
			targetCard.setCardNumber(staticNumber);
			targetCard.setDefaultCard(sourceCard.isDefaultCard());
			targetCard.setExpiryMonth(sourceCard.getExpiryMonth());
			targetCard.setExpiryYear(sourceCard.getExpiryYear());
			targetCard.setIssueNumber(sourceCard.getIssueNumber());
			targetCard.setStartMonth(sourceCard.getStartMonth());
			targetCard.setStartYear(sourceCard.getStartYear());
			
			return targetCard;
		}

		private String getStaticNumber(final String cardType) {
			final String type = cardType.toLowerCase().trim();

			if (type.startsWith("visa")) {
				return "4111111111111111";
			} else if (type.startsWith("master")) {
				return "5500000000000004";
			} else if (type.startsWith("american")) {
				return "340000000000009";
			} else if (type.startsWith("diner")) {
				return "30000000000004";
			} else if (type.startsWith("discover")) {
				return "6011000000000004";
			}

			return null;
		}
	};
}
