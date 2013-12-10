package com.elasticpath.domain.pricing.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Persistent;

import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/** @see com.elasticpath.domain.pricing.PriceListDescriptor */
@Entity
@Table(name = PriceListDescriptorImpl.TABLE_NAME)
@DataCache(enabled = true)
public class PriceListDescriptorImpl extends AbstractEntityImpl implements PriceListDescriptor {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	/** Database Table. */
	public static final String TABLE_NAME = "TPRICELIST";
	private long uidPk;
	private String description;
	private String currencyCode;
	private String name;
	private boolean hidden;
	private String guid;
	
	/**
	 * @return the Currency code for this price list.
	 */
	@Persistent
	@Column(name = "CURRENCY")
	public String getCurrencyCode() {
		return this.currencyCode;
	}

	/**
	 * @return the description of the price list.
	 */
	@Basic
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}

	/** 
	 * @return the name of the price list descriptor.
	 */
	@Basic
	@Column(name = "NAME")
	public String getName() {
		return this.name;
	}
	
	@Override
	@Basic
	@Column(name = "HIDDEN")
	public boolean isHidden() {
		return this.hidden;
	}
	
	@Override
	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
	}
	
	/**
	 * Set the currency code.
	 * @param code to set.
	 */
	public void setCurrencyCode(final String code) {
		this.currencyCode = code;
	}

	/**
	 * Set the description of the price list.
	 * @param description the description for the price list
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/** 
	 * @param name the name to set.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Generate the hash code.
	 *
	 * @return the hash code.
	 */
    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Determines whether the given object is equal to this PriceListDescriptor.
	 * Two PLDs are considered equal if their GUIDs are equal.
	 * @param obj the object to which this one should be compared for equality
	 * @return true if the given object is equal to this one
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PriceListDescriptorImpl)) {
			return false;
		}
		return super.equals(obj);
	}

	/**
	 * Returns a brief description of this PriceListDescriptor. The exact details
	 * of the representation are unspecified and are subject to change.
	 * 
	 * @return the string representation of PriceListDescriptorImpl.
	 */
	@Override
	public String toString() {
		return "[PriceListDescriptor: "
			+ "GUID=" + this.getGuid()
			+ "Name=" + this.getName()
			+ "CurrencyCode=" + this.getCurrencyCode()
			+ "Description=" + this.getDescription()
			+ "Hidden=" + this.isHidden()
			+ "]";	
	}
	
	
}
