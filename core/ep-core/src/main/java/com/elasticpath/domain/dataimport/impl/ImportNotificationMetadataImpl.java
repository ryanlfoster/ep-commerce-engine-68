/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.dataimport.ImportNotificationMetadata;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Default implementation for {@link ImportNotificationMetadata}.
 */
@Entity
@Table(name = ImportNotificationMetadataImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ImportNotificationMetadataImpl extends AbstractPersistableImpl implements ImportNotificationMetadata {

	/**
	 * This entities table name.
	 */
	public static final String TABLE_NAME = "TIMPORTNOTIFICATIONMETADATA";
	private static final long serialVersionUID = 1248343408207472486L;

	private String key;
	private String value;

	private long uidPk;
	/**
	 *
	 * @return the key
	 */
	@Basic(optional = false)
	@Column(name = "METADATA_KEY", nullable = false)
	public String getKey() {
		return key;
	}
	/**
	 *
	 * @param key the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}
	/**
	 *
	 * @return the value
	 */
	@Basic(optional = false)
	@Column(name = "METADATA_VALUE", nullable = false)
	public String getValue() {
		return value;
	}
	/**
	 *
	 * @param value the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof ImportNotificationMetadataImpl)) {
			return false;
		}
		
		ImportNotificationMetadataImpl data = (ImportNotificationMetadataImpl) other;
		return ObjectUtils.equals(this.key, data.key);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.key);
	}

	@Override
	public String toString() {
		return new ToStringBuilder("ImportNotificationMetadataImpl").
			append("key", getKey()).
			append("value", getValue()).
			toString();
	}
	
	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
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

}
