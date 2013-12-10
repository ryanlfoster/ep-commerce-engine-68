/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.audit.impl;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.audit.ChangeOperation;
import com.elasticpath.domain.audit.DataChanged;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.openjpa.ChangeType;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

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

/**
 * Represents a change set.
 */
@Entity
@Table(name = DataChangedImpl.TABLE_NAME)
@DataCache(enabled = false)
public class DataChangedImpl extends AbstractPersistableImpl implements DataChanged {

	private static final long serialVersionUID = 3645404746418939942L;

	/** The name of the table & generator to use for persistence. */
	public static final String TABLE_NAME = "TDATACHANGED";

	private long uidPk;

	private String objectName;

	private long objectUid;

	private String objectGuid;

	private String fieldName;

	private String fieldOldValue;
	
	private String fieldNewValue;

	private String changeTypeName;

	private ChangeOperation changeOperation;

	/**
	 * Gets the unique identifier for this domain model object.
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS",
					pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
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
	 * Get the name of the object that has a change.
	 *
	 * @return the object name
	 */
	@Basic
	@Column(name = "OBJECT_NAME")
	public String getObjectName() {
		return objectName;
	}

	/**
	 * Set the name of the object that has a change.
	 *
	 * @param objectName the object name to set
	 */
	public void setObjectName(final String objectName) {
		this.objectName = objectName;
	}

	/**
	 * Get the identity of the changed object.
	 *
	 * @return the objectUid
	 */
	@Basic
	@Column(name = "OBJECT_UID")
	public long getObjectUid() {
		return objectUid;
	}

	/**
	 * Set the UID of the changed object.
	 *
	 * @param objectUid the objectUid to set
	 */
	public void setObjectUid(final long objectUid) {
		this.objectUid = objectUid;
	}

	/**
	 * Get the object GUID.
	 *
	 * @return the objectGuid
	 */
	@Basic
	@Column(name = "OBJECT_GUID")
	public String getObjectGuid() {
		return objectGuid;
	}

	/**
	 * Set the object GUID.
	 *
	 * @param objectGuid the objectGuid to set
	 */
	public void setObjectGuid(final String objectGuid) {
		this.objectGuid = objectGuid;
	}

	/**
	 * Get the name of the field that has changed.
	 *
	 * @return the fieldName
	 */
	@Basic
	@Column(name = "FIELD_NAME")
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Set the name of the field that has changed.
	 *
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Get the old value of the changed field.
	 *
	 * @return the fieldValue
	 */
	@Column(name = "FIELD_OLD_VALUE", length = GlobalConstants.LONG_TEXT_MAX_LENGTH)
	public String getFieldOldValue() {
		return fieldOldValue;
	}

	/**
	 * Set the old value of the changed field.
	 *
	 * @param fieldValue the fieldValue to set
	 */
	public void setFieldOldValue(final String fieldValue) {
		this.fieldOldValue = fieldValue;
	}

	/**
	 * Get the new value of the changed field.
	 *
	 * @return the fieldValue
	 */
	@Column(name = "FIELD_NEW_VALUE", length = GlobalConstants.LONG_TEXT_MAX_LENGTH)
	public String getFieldNewValue() {
		return fieldNewValue;
	}

	/**
	 * Set the new value of the changed field.
	 *
	 * @param fieldValue the fieldValue to set
	 */
	public void setFieldNewValue(final String fieldValue) {
		this.fieldNewValue = fieldValue;
	}

	/**
	 * Get the change type.
	 *
	 * @return the <code>ChangeType</code>
	 */
	@Basic
	@Column(name = "CHANGE_TYPE", nullable = false)
	public String getChangeTypeName() {
		return changeTypeName;
	}

	/**
	 * Set the change type.
	 *
	 * @param changeTypeName the name of change type
	 */
	public void setChangeTypeName(final String changeTypeName) {
		this.changeTypeName = changeTypeName;
	}
	
	/**
	 * Get the change type.
	 *
	 * @return the <code>ChangeType</code>
	 */
	@Transient
	public ChangeType getChangeType() {
		return ChangeType.getChangeType(getChangeTypeName());
	}

	/**
	 * Set the change type.
	 *
	 * @param changeType the <code>ChangeType</code> to set
	 */
	public void setChangeType(final ChangeType changeType) {
		setChangeTypeName(changeType.getName());
	}

	/**
	 * Get the operation this change record is part of.
	 *
	 * @return the <code>ChangeOperation</code>
	 */
	@ManyToOne(targetEntity = AbstractChangeOperationImpl.class)
	@JoinColumn(name = "CHANGE_OPERATION_UID")
	@ForeignKey
	public ChangeOperation getChangeOperation() {
		return changeOperation;
	}

	/**
	 * Set the operation this change record is part of.
	 *
	 * @param changeOperation the <code>ChangeOperation</code> to set
	 */
	public void setChangeOperation(final ChangeOperation changeOperation) {
		this.changeOperation = changeOperation;
	}


}
