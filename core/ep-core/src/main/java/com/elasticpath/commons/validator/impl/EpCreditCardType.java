package com.elasticpath.commons.validator.impl;

import org.apache.commons.validator.CreditCardValidator.CreditCardType;

/**
 * Implementation for a credit card type that generalizes the checks for all credit cards.
 */
public class EpCreditCardType implements CreditCardType {
	private String[] prefixes;

	private int[] supportedLengths;

	/**
	 * Matches a credit card number.
	 * 
	 * @param card the card number
	 * @return true if the card number matches the requirements
	 */
	public boolean matches(final String card) {
		return checkLength(card.length(), supportedLengths) && checkPrefixes(card, prefixes);
	}

	private boolean checkLength(final int cardLength, final int[] supportedLengths) {
		for (int supportedLength : supportedLengths) {
			if (supportedLength == cardLength) {
				return true;
			}
		}
		return false;
	}

	private boolean checkPrefixes(final String card, final String[] prefixes) {
		for (String prefix : prefixes) {
			if (card.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the supported prefixes.
	 * 
	 * @param prefixes the supported credit card number prefixes
	 */
	public void setPrefixes(final String prefixes) {
		this.prefixes = prefixes.trim().split("[\\s]*,[\\s]*");
	}

	/**
	 * Sets the supported lengths as a string of a separated by comma list.
	 * 
	 * @param supportedLengthsStr the supported credit card number lengths as a string
	 */
	public void setSupportedLengths(final String supportedLengthsStr) {
		String[] lengthsStr = supportedLengthsStr.trim().split("[\\s]*,[\\s]*");
		this.supportedLengths = new int[lengthsStr.length];

		for (int i = 0; i < lengthsStr.length; i++) {
			supportedLengths[i] = Integer.parseInt(lengthsStr[i].trim());
		}
	}
}