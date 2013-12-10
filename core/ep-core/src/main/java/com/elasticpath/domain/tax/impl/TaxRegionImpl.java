/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * A <code>TaxValue</code> represents mapping among <code>TaxCode</code>s and its values.
 */
@Entity
@Table(name = TaxRegionImpl.TABLE_NAME)
@DataCache(enabled = true)
public class TaxRegionImpl extends AbstractPersistableImpl implements TaxRegion {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TTAXREGION";

	private String regionName;

	private Map<String, TaxValue> taxValueMap;

	private Set<TaxValue> taxValueSet;

	private long uidPk;

	@Override
	@Basic
	@Column(name = "REGION_NAME")
	public String getRegionName() {
		return regionName;
	}

	@Override
	public void setRegionName(final String regionName) {
		this.regionName = regionName;
	}

	/**
	 * Get tax value set.
	 * 
	 * @return Tax value Set
	 */
	@OneToMany(targetEntity = TaxValueImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@ElementJoinColumn(name = "TAX_REGION_UID")
	@ElementForeignKey
	@ElementDependent
	protected Set<TaxValue> getTaxValueSet() {
		if (taxValueSet == null) {
			taxValueSet = new HashSet<TaxValue>();
		}
		return taxValueSet;
	}

	/**
	 * Set tax value set.
	 * 
	 * @param taxValueSet the taxValueSet
	 */
	protected void setTaxValueSet(final Set<TaxValue> taxValueSet) {
		this.taxValueSet = taxValueSet;
	}

	@Override
	@Transient
	public Map<String, TaxValue> getTaxValuesMap() {
		if (taxValueMap == null) {
			taxValueMap = new HashMap<String, TaxValue>();
			for (TaxValue taxValue : getTaxValueSet()) {
				taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);
			}
		}
		return taxValueMap;
	}

	@Override
	public void setTaxValuesMap(final Map<String, TaxValue> taxValuesMap) {
		this.taxValueMap = taxValuesMap;
		setTaxValueSet(new HashSet<TaxValue>(taxValuesMap.values()));
	}

	@Override
	@Transient
	public BigDecimal getValue(final String taxCode) {
		return getTaxRate(taxCode);
	}

	/**
	 * @param taxCode the code representing the tax for which the rate is required
	 * @return the tax rate expressed as a percentage (e.g. a 7.5% tax is represented as 7.5)
	 */
	@Transient
	public BigDecimal getTaxRate(final String taxCode) {
		if (getTaxValuesMap() != null) {
			TaxValue taxValue = getTaxValuesMap().get(taxCode);
			if (taxValue != null) {
				return taxValue.getTaxValue();
			}
		}
		return null;
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
		return this.uidPk;
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
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TaxRegionImpl)) {
			return false;
		}
		final TaxRegionImpl other = (TaxRegionImpl) obj;

		if (!objectsEqual(regionName, other.getRegionName())) {
			return false;
		}

		if (!objectsEqual(taxValueMap, other.getTaxValuesMap())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(regionName).append(taxValueMap).toHashCode();
	}

	/**
	 * Compares two objects for equality.
	 */
	private boolean objectsEqual(final Object object, final Object other) {
		if (object == null) {
			if (other != null) {
				return false;
			}
		} else if (!object.equals(other)) {
			return false;
		}
		return true;
	}

	@Override
	public void addTaxValue(final TaxValue targetValue) {
		getTaxValueSet().add(targetValue);
	}
}
