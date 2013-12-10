package com.elasticpath.common.dto.assembler;

import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.LocaleUtils;

/**
 * A simple JAXB adapter which converts between Locale and String.
 */
public class LocaleXmlAdapter extends XmlAdapter<String, Locale> {

	@Override
	public Locale unmarshal(final String locale) throws Exception {
		return LocaleUtils.toLocale(locale);
	}

	@Override
	public String marshal(final Locale locale) throws Exception {
		return locale.toString();
	}

}
