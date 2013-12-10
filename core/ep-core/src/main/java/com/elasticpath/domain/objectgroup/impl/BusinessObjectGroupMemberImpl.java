/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.objectgroup.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Business object group member representing an object in the system with reference to it by identifier and type.
 */
@Entity
@Table(name = BusinessObjectGroupMemberImpl.TABLE_NAME, 
		uniqueConstraints = @UniqueConstraint(columnNames = { "OBJECT_GROUP_ID", "OBJECT_TYPE", "OBJECT_IDENTIFIER" }))
@DataCache(enabled = false)
public class BusinessObjectGroupMemberImpl extends AbstractEntityImpl implements BusinessObjectGroupMember {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The entity table name.
	 */
	protected static final String TABLE_NAME = "TOBJECTGROUPMEMBER";
	
	private String groupId;
	private String objectIdentifier;
	private String objectType;
	private long uidPk;

	private String guid;
	
	@Override
	@Basic
	@Column(name = "OBJECT_GROUP_ID", nullable = false)
	public String getGroupId() {
		return groupId;
	}

	@Override
	@Basic
	@Column(name = "OBJECT_IDENTIFIER", nullable = false)
	public String getObjectIdentifier() {
		return objectIdentifier;
	}

	@Override
	@Basic
	@Column(name = "OBJECT_TYPE", nullable = false)
	public String getObjectType() {
		return objectType;
	}

	@Override
	@Basic
	@Column(name = "GUID", nullable = false, unique = true)
	public String getGuid() {
		return guid;
	}

	/**
	 * Sets the GUID.
	 * 
	 * @param guid the GUID to set
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

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

	/**
	 *
	 * @param groupId the groupId to set
	 */
	public void setGroupId(final String groupId) {
		this.groupId = groupId;
	}

	/**
	 *
	 * @param objectIdentifier the objectIdentifier to set
	 */
	public void setObjectIdentifier(final String objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}

	/**
	 *
	 * @param objectType the objectType to set
	 */
	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	@Override
	public String toString() {
		return getGroupId() + "=" + getObjectType() + ":" + getObjectIdentifier();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof BusinessObjectGroupMember)) {
			return false;
		}
		return super.equals(obj);
	}
	
    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public int hashCode() {
    	return super.hashCode();
    }

}
