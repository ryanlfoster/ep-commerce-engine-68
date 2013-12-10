/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.objectgroup.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Business object group member representing an object in the system with reference to it by identifier and type.
 */
@Entity
@Table(name = BusinessObjectMetadataImpl.TABLE_NAME)
@DataCache(enabled = false)
public class BusinessObjectMetadataImpl extends AbstractPersistableImpl implements BusinessObjectMetadata {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The entity table name.
	 */
	protected static final String TABLE_NAME = "TOBJECTMETADATA";
	
	private long uidPk;
	private String metadataKey;
	private String metadataValue;
	
	private BusinessObjectGroupMember businessObjectGroupMember;


	/**
	 * Gets the UIPK of this entity.
	 * 
	 * @return a long representing the UIDPK
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}
	
	/**
	 * Sets the UIDPK.
	 * 
	 * @param uidPk the UIDPK to set
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
	@Override
	@Basic
	@Column(name = "METADATA_KEY", nullable = false)
	public String getMetadataKey() {
		return metadataKey;
	}
	
	@Override
	@Basic
	@Column(name = "METADATA_VALUE", nullable = false)
	public String getMetadataValue() {
		return metadataValue;
	}

	@Override
	public void setMetadataValue(final String metadataValue) {
		this.metadataValue = metadataValue;
	}

	@Override
	public void setMetadataKey(final String metadataKey) {
		this.metadataKey = metadataKey;
	}

	@Override
	public String toString() {
		return new ToStringBuilder("business object metadata:").append(getMetadataKey()).append(getMetadataValue()).toString();
	}

	
	@Override
	@OneToOne(targetEntity = BusinessObjectGroupMemberImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "OBJECT_GROUP_MEMBER_UID")
	@ForeignKey
	public BusinessObjectGroupMember getBusinessObjectGroupMember() {
		return businessObjectGroupMember;
	}

	@Override
	public void setBusinessObjectGroupMember(
			final BusinessObjectGroupMember businessObjectGroupMember) {
		this.businessObjectGroupMember = businessObjectGroupMember;
	}
	
}
