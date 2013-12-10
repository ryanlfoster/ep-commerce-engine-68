/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import java.util.Currency;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;

import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.persistence.api.AbstractEntityImpl;


/**
 * The default implementation of <code>CustomerSession</code>.
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
@Entity
@Table(name = CustomerSessionMementoImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CustomerSessionMementoImpl extends AbstractEntityImpl implements CustomerSessionMemento {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCUSTOMERSESSION";

	private Date creationDate;

	private Date lastAccessedDate;

	private long shopperUid;

	private Currency currency;

	private String ipAddress;

	private static final int LOCALE_LENGTH = 20;

	private String localeStr;

	private long uidPk;

	private String guid;


	/**
	 * Allocation size for JPA_GENERATED_KEYS id.
	 */
	private static final int ALLOCATION_SIZE = 1000;


	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE", nullable = false)
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_ACCESSED_DATE", nullable = false)
	public Date getLastAccessedDate() {
		return lastAccessedDate;
	}

	@Override
	public void setLastAccessedDate(final Date lastAccessedDate) {
		this.lastAccessedDate = lastAccessedDate;
	}

	@Override
	@Basic
	@Column(name = "SHOPPER_UID")
	public long getShopperUid() {
		return shopperUid;
	}

	@Override
	public void setShopperUid(final long shopperUid) {
		this.shopperUid = shopperUid;
	}

	@Override
	@Basic
	@Column(name = "LOCALE", length = LOCALE_LENGTH, nullable = false)
	public String getLocaleStr() {
		return localeStr;
	}

	@Override
	public void setLocaleStr(final String localeStr) {
		this.localeStr = localeStr;
	}

	@Override
	@Persistent
	@Externalizer("toString")
	@Factory("com.elasticpath.commons.util.impl.ConverterUtils.currencyFromString")
	@Column(name = "CURRENCY")
	public Currency getCurrency() {
		return currency;
	}

	@Override
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	@Override
	@Basic
	@Column(name = "IP_ADDRESS")
	public String getIpAddress() {
		return ipAddress;
	}

	@Override
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME,  allocationSize = ALLOCATION_SIZE)
			public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

}
