/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * <code>Attribute</code> represents a customized property of an object like <code>Category</code> or <code>Product</code>.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity" })
@Entity
@Table(name = AttributeImpl.TABLE_NAME)
@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = { @FetchAttribute(name = "localeDependant"),
		@FetchAttribute(name = "multiValueEnabled"), @FetchAttribute(name = "attributeTypeId"), @FetchAttribute(name = "key") })
public class AttributeImpl extends AbstractLegacyEntityImpl implements Attribute {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TATTRIBUTE";

	// Give an empty string as default key to make comparison easier.
	private String key = "";

	private boolean localeDependant;

	// Give an empty string as default name to make comparison easier.
	private String name = "";

	private boolean required;

	private boolean valueLookupEnabled;

	private boolean system;

	private boolean multiValueEnabled;

	private int attributeTypeId;

	private int attributeUsageId;

	private long uidPk;

	private Catalog catalog;

	private boolean global;

	/**
	 * Get the attribute key.
	 *
	 * @return the attribute key
	 */
	@Basic
	@Column(name = "ATTRIBUTE_KEY")
	public String getKey() {
		return key;
	}

	/**
	 * Set the attribute key.
	 *
	 * @param key the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Return <code>true</code> if the attribute is dependant on the locale.
	 *
	 * @return <code>true</code> if the attribute is dependant on the locale
	 */
	@Basic
	@Column(name = "LOCALE_DEPENDANT")
	public boolean isLocaleDependant() {
		return localeDependant;
	}

	/**
	 * Set the locale dependant flag.
	 *
	 * @param localeDependant the locale-dependant flag.
	 */
	public void setLocaleDependant(final boolean localeDependant) {
		this.localeDependant = localeDependant;
	}

	/**
	 * Return the <code>AttributeType</code> of this attribute.
	 *
	 * @return the <code>AttributeType</code> of this attribute
	 */
	@Transient
	public AttributeType getAttributeType() {
		try {
			return AttributeType.valueOf(getAttributeTypeId());
		} catch (IllegalArgumentException iae) {
			return null;
		}
	}

	/**
	 * Set the <code>AttributeType</code> of this attribute.
	 *
	 * @param attributeType the attribute type.
	 */
	public void setAttributeType(final AttributeType attributeType) {
		if (attributeType == null) {
			setAttributeTypeId(0);
		} else {
			setAttributeTypeId(attributeType.getTypeId());
		}
	}

	/**
	 * Get the product system name.
	 *
	 * @return the product system name
	 */
	@Basic
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the product system name.
	 *
	 * @param name the product system name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Compares this attribute with the specified object for order.
	 *
	 * @param attribute the given attribute
	 * @return a negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the specified object.
	 * @throws EpDomainException in case the given object is not a <code>Attribute</code>
	 */
	public int compareTo(final Attribute attribute) throws EpDomainException {
		// Compare references first
		if (this == attribute) {
			return 0;
		}

		// Then compare name
		final int nameCompareResult = getName().compareTo(attribute.getName());
		if (nameCompareResult != 0) {
			return nameCompareResult;
		}

		// Then compare key
		final int keyCompareResult = getKey().compareTo(attribute.getKey());
		if (keyCompareResult != 0) {
			return keyCompareResult;
		}

		// Finally compare uidPk
		if (getUidPk() < attribute.getUidPk()) {
			return -1;
		} else if (getUidPk() > attribute.getUidPk()) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Return the <code>AttributeUsage</code> of this attribute.
	 *
	 * @return the <code>AttributeUsage</code> of this attribute
	 */
	@Transient
	public AttributeUsage getAttributeUsage() {
		return lookupAttributeUsage(getAttributeUsageIdInternal());
	}

	/**
	 * Set the <code>AttributeUsage</code> of this attribute.
	 *
	 * @param attributeUsage the attribute usage
	 */
	public void setAttributeUsage(final AttributeUsage attributeUsage) {
		if (attributeUsage == null) {
			setAttributeUsageId(0);
		} else {
			setAttributeUsageId(attributeUsage.getValue());
		}
	}

	/**
	 * Return <code>true</code> if the attribute is required.
	 *
	 * @return <code>true</code> if the attribute is required
	 */
	@Basic
	@Column(name = "REQUIRED")
	public boolean isRequired() {
		return required;
	}

	/**
	 * Set the required flag.
	 *
	 * @param required the required flag
	 */
	public void setRequired(final boolean required) {
		this.required = required;
	}

	/**
	 * Return <code>true</code> if the attribute is system attribute.
	 *
	 * @return <code>true</code> if the attribute is system attribute
	 */
	@Basic
	@Column(name = "SYSTEM")
	public boolean isSystem() {
		return system;
	}

	/**
	 * Set the system flag.
	 *
	 * @param system the system flag
	 */
	public void setSystem(final boolean system) {
		this.system = system;
	}

	/**
	 * Return <code>true</code> if the value lookup for this attribute is enabled. If value-lookup is enabled, users have the option of selecting
	 * from existing previously existing attribute values when setting the attribute's value.
	 *
	 * @return <code>true</code> if the value lookup for this attribute is enabled.
	 */
	@Basic
	@Column(name = "VALUE_LOOKUP_ENABLED")
	public boolean isValueLookupEnabled() {
		return valueLookupEnabled;
	}

	/**
	 * Sets whether or not the user will have the option of selecting from previously existing values when editing an attribute.
	 *
	 * @param valueLookupEnabled set to <code>true</code> if lookup is to be enabled.
	 */
	public void setValueLookupEnabled(final boolean valueLookupEnabled) {
		this.valueLookupEnabled = valueLookupEnabled;
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Transient
	public String getGuid() {
		return getKey();
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		setKey(guid);
	}

	/**
	 * Return true if the attribute can have multi value.
	 *
	 * @return true if the attribute can have multi value.
	 */
	@Basic
	@Column(name = "MULTI_VALUE_ENABLED")
	public boolean isMultiValueEnabled() {
		return multiValueEnabled;
	}

	/**
	 * Set the attribute can have multi value.
	 *
	 * @param multiValueEnabled the multiValueEnabled to set
	 */
	public void setMultiValueEnabled(final boolean multiValueEnabled) {
		this.multiValueEnabled = multiValueEnabled;
	}

	/**
	 * Get the attribute type Id.
	 *
	 * @return the attribute type Id
	 */
	@Basic
	@Column(name = "ATTRIBUTE_TYPE")
	protected int getAttributeTypeId() {
		return attributeTypeId;
	}

	/**
	 * Set the attribute type Id.
	 *
	 * @param attributeTypeId the Id
	 */
	protected void setAttributeTypeId(final int attributeTypeId) {
		this.attributeTypeId = attributeTypeId;
	}

	/**
	 * Get the attribute usage Id.
	 *
	 * @return the Id
	 */
	@Basic
	@Column(name = "ATTRIBUTE_USAGE")
	protected int getAttributeUsageIdInternal() {
		return attributeUsageId;
	}

	/**
	 * Get the attribute usage Id.
	 *
	 * @return the Id
	 */
	@Transient
	public int getAttributeUsageId() {
		return getAttributeUsageIdInternal();
	}
	
	/**
	 * Set the attribute usage Id.
	 *
	 * @param attributeUsageId the Id
	 */
	protected void setAttributeUsageIdInternal(final int attributeUsageId) {
		this.attributeUsageId = attributeUsageId;		
	}

	/**
	 * Set the attribute usage Id.
	 *
	 * @param attributeUsageId the Id
	 */
	@Transient
	public void setAttributeUsageId(final int attributeUsageId) {
		setAttributeUsageIdInternal(attributeUsageId);
	}

	private AttributeUsage lookupAttributeUsage(final int attributeUsageId) {
		if (attributeUsageId == 0) {
			return null;
		}
		return ((AttributeUsageImpl) getBean(ContextIdNames.ATTRIBUTE_USAGE))
			.getAttributeUsageById(attributeUsageId);
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

	/**
	 * Get the catalog that this attribute belongs to (for catalog related attributes).
	 *
	 * @return the catalog
	 */
	@ManyToOne(optional = true, targetEntity = CatalogImpl.class)
	@JoinColumn(name = "CATALOG_UID", nullable = true)
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Set the catalog that this attribute type belongs to (for catalog related attributes).
	 *
	 * @param catalog the catalog to set
	 */
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Gets whether this attribute is global.
	 *
	 * @return whether this attribute is global
	 */
	@Column(name = "ATTR_GLOBAL")
	public boolean isGlobal() {
		return global;
	}

	/**
	 * Sets whether this attribute is global.
	 *
	 * @param global whether this attribute is global
	 */
	public void setGlobal(final boolean global) {
		this.global = global;
	}

	@Override
	@SuppressWarnings({ "PMD.UselessOverridingMethod" })
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!super.equals(obj)) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		return true;
	}


    /**
     * String representation of this object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
            final StringBuffer sbf = new StringBuffer();
            sbf.append("Attribute -> name: ").append(getName());
            sbf.append(" Type Usage: ").append(getAttributeUsage());
            sbf.append(" GUID: ").append(getGuid());
            return sbf.toString();
    }
}
