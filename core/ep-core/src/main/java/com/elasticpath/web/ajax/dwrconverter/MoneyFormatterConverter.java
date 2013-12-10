package com.elasticpath.web.ajax.dwrconverter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.dwrp.ProtocolConstants;
import org.directwebremoting.extend.InboundContext;
import org.directwebremoting.extend.InboundVariable;
import org.directwebremoting.extend.MarshallException;
import org.directwebremoting.util.Logger;
import org.directwebremoting.util.Messages;

import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;

/**
 * Extends <code>BeanConverter</code> to provide custom conversion for
 * <code>Locale</code> and <code>Money</code> types that need to be passed from Javascript to 
 * a java MoneyFormatter. Should only be used from within dwr.xml.
 * 
 * @see org.directwebremoting.BeanConverter#convertInbound
 */
public class MoneyFormatterConverter extends BeanConverter {

	private static final Logger LOG = Logger.getLogger(MoneyFormatterConverter.class);

	private static final String UNDERSCORE = "_";
	private static final String COLON_DELIM = ":";
	private static final String COMMA_DELIM = ",";
	
	//locale obj vars
	private static final String LANGUAGE_VAR = "language";
	private static final String COUNTRY_VAR = "country";
	private static final String LOCALE_CLASS_NAME = "java.util.Locale";
	
	//money obj vars
	private static final String AMOUNT_VAR = "amount";
	private static final String CURRENCY_VAR = "currency";
	private static final String CURRENCY_CODE_VAR = "currencyCode";
	private static final String MONEY_CLASS_NAME = "com.elasticpath.domain.misc.Money";
	
	
	/*
	 * Processes the incoming values into either a Locale or Money object and returns.
	 * 
	 * @param paramType paramType the class type
	 * @param inboundVariable inboundVariable the InboundVariable info
	 * @param inboundContext inboundContext the InboundContext info
	 * 
	 * @return a <code>Locale</code> or <code>Money</code> object based on the paramType
	 * 
	 * @throws MarshallException if an attempt to convert anything other than Locale or Money. Or if the incoming data
	 * is not of the expected format.
	 * 
	 * @see org.directwebremoting.Converter#convertInbound(java.lang.Class, org.directwebremoting.InboundVariable,
	 * org.directwebremoting.InboundContext)
	 * @see com.elasticpath.domain.misc.Money
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object convertInbound(final Class paramType, final InboundVariable inboundVariable,
			final InboundContext inboundContext) throws MarshallException {
		String variableReferences = inboundVariable.getValue();
		
		// If the text is null then the whole bean is null
		if (variableReferences.trim().equals(ProtocolConstants.INBOUND_NULL)) {
			return null;
		}

		if (!variableReferences.startsWith(ProtocolConstants.INBOUND_MAP_START)) {
			throw new MarshallException(paramType, Messages.getString("MoneyFormatterConverter.FormatError", ProtocolConstants.INBOUND_MAP_START));
		}

		if (!variableReferences.endsWith(ProtocolConstants.INBOUND_MAP_END)) {
			throw new MarshallException(paramType, Messages.getString("MoneyFormatterConverter.FormatError", ProtocolConstants.INBOUND_MAP_START));
		}

		try {
			variableReferences = stripEnclosingCurlyBracesWhitespace(variableReferences);
			if (StringUtils.equals(paramType.getName(), LOCALE_CLASS_NAME)) {
				return convertForLocale(variableReferences, inboundContext);
			} else if (StringUtils.equals(paramType.getName(), MONEY_CLASS_NAME)) {
				return convertForMoney(variableReferences, inboundContext);
			} else {
				throw new MarshallException(paramType, 
				"Unsupported Class type. MoneyFormatterConverter only supports java.util.Locale and com.elasticpath.domain.misc.Money");
			}
		} catch (Exception ex) {
			LOG.error("Could not convert" + paramType + " for dwr.", ex);
			throw new MarshallException(paramType, ex);
		}
	}
	
	private String getVariableReference(final String variableName, final String references) throws Exception {
		final String [] splitReferences = StringUtils.split(references, COMMA_DELIM);
		for (String s : splitReferences) {
			if (s.startsWith(variableName)) {
				return StringUtils.substringAfterLast(s, COLON_DELIM);
			}
		}
		return null;
	}
	
	private String stripEnclosingCurlyBracesWhitespace(final String stripMe) {
		return StringUtils.deleteWhitespace(stripMe.substring(1, stripMe.length() - 1));
	}
	
	private Locale convertForLocale(final String variableReferences, final InboundContext inboundContext) throws Exception {
		final String country  = inboundContext.getInboundVariable(getVariableReference(COUNTRY_VAR, variableReferences)).getValue();
		final String language = inboundContext.getInboundVariable(getVariableReference(LANGUAGE_VAR, variableReferences)).getValue();
		return createLocale(country, language);
	}
	
	private Locale createLocale(final String localeCountry, final String localeLanguage) throws Exception {
		if (StringUtils.isBlank(localeCountry)) {
			if (StringUtils.isBlank(localeLanguage)) {
				return null;
			}
			return LocaleUtils.toLocale(localeLanguage);
		}
		return LocaleUtils.toLocale(localeLanguage + UNDERSCORE + localeCountry);
	}
	
	private Money convertForMoney(final String variableReferences, final InboundContext inboundContext) throws Exception {
		final String amount = inboundContext.getInboundVariable(getVariableReference(AMOUNT_VAR, variableReferences)).getValue();
		//we have to drill down into this reference as it refers to an object originally (currency)
		// and we need to extract the currencyCode var out of it. nasty eh?!
		String currencyReference = inboundContext.getInboundVariable(getVariableReference(CURRENCY_VAR, variableReferences)).getValue();
		currencyReference = stripEnclosingCurlyBracesWhitespace(currencyReference);
		final String currencyCode = inboundContext.getInboundVariable(getVariableReference(CURRENCY_CODE_VAR, currencyReference)).getValue();
		
		return createMoney(amount, currencyCode);
	}
	
	private Money createMoney(final String amount, final String currencyCode) throws Exception {
		final Currency currency  = Currency.getInstance(currencyCode);
		final BigDecimal decimal = new BigDecimal(amount);

		return MoneyFactory.createMoney(decimal, currency);
	}

}
