package com.elasticpath.common.dto.customer;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.common.dto.assembler.CurrencyXmlAdapter;
import com.elasticpath.common.dto.assembler.LocaleXmlAdapter;

/**
 * JAXB DTO for a Customer Session (currently unused).
 */
@XmlRootElement(name = CustomerSessionDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class CustomerSessionDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/** Root XML element name. */
	public static final String ROOT_ELEMENT = "session";

	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlElement(required = true)
	private String email;

	@XmlElement(required = true)
	@XmlJavaTypeAdapter(value = LocaleXmlAdapter.class)
	private Locale locale;

	@XmlElement(required = true)
	@XmlJavaTypeAdapter(value = CurrencyXmlAdapter.class)
	private Currency currency;

	@XmlElement(name = "ip_address", required = true)
	private String ipAddress;

	@XmlElement(required = true)
	private Date created;

	@XmlElement(name = "last_accessed_date", required = true)
	private Date lastAccessedDate;

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(final Date created) {
		this.created = created;
	}

	public Date getLastAccessedDate() {
		return lastAccessedDate;
	}

	public void setLastAccessedDate(final Date lastAccessedDate) {
		this.lastAccessedDate = lastAccessedDate;
	}

}
