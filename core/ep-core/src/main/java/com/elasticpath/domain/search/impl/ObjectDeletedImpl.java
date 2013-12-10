/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.search.impl;

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

import com.elasticpath.domain.search.ObjectDeleted;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * The default implementation of <code>Object</code>.
 */
@Entity
@Table(name = ObjectDeletedImpl.TABLE_NAME)
@DataCache(enabled = true)
public class ObjectDeletedImpl extends AbstractPersistableImpl implements ObjectDeleted {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TOBJECTDELETED";
	
	private String objectType;

	private long objectUid;

	private Date deletedDate;

	private long uidPk;

	/**
	 * Default constructor.
	 */
	public ObjectDeletedImpl() {
		super();
	}
	
	@Override
	@Basic
	@Column(name = "OBJECT_TYPE", nullable = false)
	public String getObjectType() {
		return objectType;
	}

	@Override
	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	/**
	 * Returns the uid of the deleted object.
	 *
	 * @return the uid of the deleted object
	 */
	@Basic
	@Column(name = "OBJECT_UID", nullable = false)
	public long getObjectUid() {
		return objectUid;
	}

	/**
	 * Sets the uid of the deleted object.
	 *
	 * @param objectUid the uid of the deleted object.
	 */
	public void setObjectUid(final long objectUid) {
		this.objectUid = objectUid;
	}

	/**
	 * Returns the date when the object was deleted.
	 *
	 * @return the date when the object was deleted
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DELETED_DATE", nullable = false)
	public Date getDeletedDate() {
		return deletedDate;
	}

	/**
	 * Sets the date when the object was deleted.
	 *
	 * @param deletedDate the date when the object was deleted
	 */
	public void setDeletedDate(final Date deletedDate) {
		this.deletedDate = deletedDate;
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

}
