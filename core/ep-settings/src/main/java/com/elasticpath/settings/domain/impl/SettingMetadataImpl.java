/**
 * 
 */
package com.elasticpath.settings.domain.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.settings.domain.SettingMetadata;

/**
 * Default implementation of {@link SettingMetadata}.
 */
@Entity
@Table(name = SettingMetadataImpl.TABLE_NAME)
@DataCache(enabled = false)
public class SettingMetadataImpl extends AbstractPersistableImpl implements SettingMetadata {
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSETTINGMETADATA";
	
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 6000000001L;
	
	private String value;
	private String key;
	private long uidPk;
	private static final int VALUE_LENGTH = 2000; 
	
	@Override
	@Basic
	@Column(name = "METADATA_KEY", nullable = false)
	public String getKey() {
		return key;
	}

	@Override
	@Basic
	@Column(name = "VALUE", nullable = false, length = VALUE_LENGTH)
	public String getValue() {
		return value;
	}

	@Override
	public void setKey(final String key) {
		this.key = key;
	}

	@Override
	public void setValue(final String value) {
		this.value = value;
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
	public String toString() {
		return key + "=" + value;
	}
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof SettingMetadataImpl)) {
			return false;
		}
		
		SettingMetadataImpl setting = (SettingMetadataImpl) other;
		return ObjectUtils.equals(this.key, setting.key);
	}
	
	
	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(key);
	}
	
}
