package com.elasticpath.common.dto.store;

import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.common.dto.assembler.CurrencyXmlAdapter;
import com.elasticpath.common.dto.assembler.LocaleXmlAdapter;
import com.elasticpath.common.dto.assembler.TimeZoneXmlAdapter;

/**
 * Localization, Internationalization, time and currency form Globalization. This is the JAXB DTO to contain these values for a Store.
 */
@XmlRootElement(name = "globalization")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class StoreGlobalizationDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(value = TimeZoneXmlAdapter.class)
	private TimeZone timeZone;

	@XmlAttribute(required = true)
	private String country;

	@XmlAttribute(name = "sub_country")
	private String subCountry;

	@XmlAttribute(name = "content_encoding")
	private String contentEncoding;

	@XmlAttribute(name = "default_locale")
	@XmlJavaTypeAdapter(value = LocaleXmlAdapter.class)
	private Locale defaultLocale;

	@XmlAttribute(name = "default_currency")
	@XmlJavaTypeAdapter(value = CurrencyXmlAdapter.class)
	private Currency defaultCurrency;

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(final TimeZone timezone) {
		this.timeZone = timezone;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public String getSubCountry() {
		return subCountry;
	}

	public void setSubCountry(final String subCountry) {
		this.subCountry = subCountry;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(final String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(final Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public Currency getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(final Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}
}
