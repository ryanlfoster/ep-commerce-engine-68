package com.elasticpath.common.dto.assembler;

import java.util.Currency;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * A simple JAXB adapter which converts between Currency and String.
 */
public class CurrencyXmlAdapter extends XmlAdapter<String, Currency> {

	@Override
	public Currency unmarshal(final String currency) throws Exception {
		return Currency.getInstance(currency);
	}

	@Override
	public String marshal(final Currency currency) throws Exception {
		return currency.toString();
	}

}
