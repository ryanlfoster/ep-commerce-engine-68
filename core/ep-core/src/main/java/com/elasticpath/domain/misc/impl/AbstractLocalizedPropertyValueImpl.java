/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;

import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Represents a localized property value.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE")
@Table(name = AbstractLocalizedPropertyValueImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.PRODUCT_SKU_INDEX, attributes = { 
			@FetchAttribute(name = "localizedPropertyKey"),
			@FetchAttribute(name = "value")
	})
})
public abstract class AbstractLocalizedPropertyValueImpl extends AbstractPersistableImpl implements LocalizedPropertyValue {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TLOCALIZEDPROPERTIES";

	private String localizedPropertyKey;

	private String value;

	private long uidPk;

	/**
	 * Get the value.
	 * <p>This implementation is annotated with OpenJPA annotations for persistence purposes.
	 * Of particular note are the two annotations:
	 * <ul><li>{@code @Externalizer("com.elasticpath.persistence.api.StringExternalizer.toExternalForm")}</li>
	 * <li>{@code @Factory("com.elasticpath.persistence.api.StringExternalizer.toInternalForm")}</li></ul>
	 * which ensure that even if the underlying database (e.g. Oracle) converts empty strings to null values,
	 * JPA won't complain.</p>
	 * @return the value
	 */
	@Basic
	@Column(name = "VALUE", nullable = false)
	@Externalizer("com.elasticpath.persistence.api.StringExternalizer.toExternalForm")
	@Factory("com.elasticpath.persistence.api.StringExternalizer.toInternalForm")
	public String getValue() {
		return value;
	}

	/**
	 * Set the value.
	 *
	 * @param value the value
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Get the localized property key.
	 *
	 * @return the key
	 */
	@Basic
	@Column(name = "LOCALIZED_PROPERTY_KEY", nullable = false)
	public String getLocalizedPropertyKey() {
		return localizedPropertyKey;
	}

	/**
	 * Set the localized property key.
	 *
	 * @param localizedPropertyKey the key
	 */
	public void setLocalizedPropertyKey(final String localizedPropertyKey) {
		this.localizedPropertyKey = localizedPropertyKey;
	}

	/**
	 * String representation of this object.
	 *
	 * @return the string representation
	 */
	public String toString() {
		return this.getLocalizedPropertyKey() + ":" + this.getValue();
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
		if (!(obj instanceof AbstractLocalizedPropertyValueImpl)) {
			return false;
		}
		AbstractLocalizedPropertyValueImpl other = (AbstractLocalizedPropertyValueImpl) obj;
		
		if (!ObjectUtils.equals(this.localizedPropertyKey, other.localizedPropertyKey)) {
			return false;
		}

		if (!ObjectUtils.equals(this.value, other.value)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ObjectUtils.hashCode(this.localizedPropertyKey);
		result = prime * result + ObjectUtils.hashCode(this.value);
		
		return result;
	}
}
