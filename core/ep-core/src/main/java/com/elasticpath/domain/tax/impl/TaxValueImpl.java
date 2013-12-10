/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax.impl;


import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Required for JPA Mapping.
 */
@Entity
@Table(name = TaxValueImpl.TABLE_NAME)
@DataCache(enabled = true)
public class TaxValueImpl extends AbstractPersistableImpl implements TaxValue {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TTAXVALUE";

	private TaxCode taxCode;

	private BigDecimal taxValue;

	private long uidPk;

	@Override
	@ManyToOne(targetEntity = TaxCodeImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "TAX_CODE_UID", nullable = false)
	@ForeignKey
	public TaxCode getTaxCode() {
		return taxCode;
	}

	@Override
	public void setTaxCode(final TaxCode taxCode) {
		this.taxCode = taxCode;
	}

	@Override
	@Basic
	@Column(name = "VALUE")
	public BigDecimal getTaxValue() {
		return taxValue;
	}

	@Override
	public void setTaxValue(final BigDecimal taxValue) {
		this.taxValue = taxValue;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getTaxCode()).append(getTaxValue()).toHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		TaxValue other = (TaxValue) obj;
		return new EqualsBuilder().append(getTaxValue(), other.getTaxValue()).append(getTaxCode(), other.getTaxCode()).isEquals();
	}

}
