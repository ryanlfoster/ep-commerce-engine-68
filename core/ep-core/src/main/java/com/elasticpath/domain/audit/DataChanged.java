/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.audit;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.openjpa.ChangeType;

/**
 * Represents a change to a field of an object.
 */
public interface DataChanged extends Persistable {

	/**
	 * Get the name of the object that has a change.
	 *
	 * @return the object name
	 */
	String getObjectName();

	/**
	 * Set the name of the object that has a change.
	 *
	 * @param objectName the object name to set
	 */
	void setObjectName(final String objectName);

	/**
	 * Get the identity of the changed object.
	 *
	 * @return the objectUid
	 */
	long getObjectUid();

	/**
	 * Set the UID of the changed object.
	 *
	 * @param objectUid the objectUid to set
	 */
	void setObjectUid(final long objectUid);

	/**
	 * Get the object GUID.
	 *
	 * @return the objectGuid
	 */
	String getObjectGuid();

	/**
	 * Set the object GUID.
	 *
	 * @param objectGuid the objectGuid to set
	 */
	void setObjectGuid(final String objectGuid);

	/**
	 * Get the name of the field that has changed.
	 *
	 * @return the fieldName
	 */
	String getFieldName();

	/**
	 * Set the name of the field that has changed.
	 *
	 * @param fieldName the fieldName to set
	 */
	void setFieldName(final String fieldName);

	/**
	 * Get the previous value of the changed field.
	 *
	 * @return the previous value of the field
	 */
	String getFieldOldValue();

	/**
	 * Set the previous value of the changed field.
	 *
	 * @param fieldValue the old fieldValue to set
	 */
	void setFieldOldValue(final String fieldValue);

	/**
	 * Get the new value of the changed field.
	 *
	 * @return the fieldValue
	 */
	String getFieldNewValue();

	/**
	 * Set the new value of the changed field.
	 *
	 * @param fieldValue the fieldValue to set
	 */
	void setFieldNewValue(final String fieldValue);

	/**
	 * Get the change type.
	 *
	 * @return the changeType
	 */
	ChangeType getChangeType();

	/**
	 * Set the change type.
	 *
	 * @param changeType the changeType to set
	 */
	void setChangeType(final ChangeType changeType);

	/**
	 * Get the operation this change record is part of.
	 *
	 * @return the changeOperation
	 */
	ChangeOperation getChangeOperation();

	/**
	 * Set the operation this change record is part of.
	 *
	 * @param changeOperation the changeOperation to set
	 */
	void setChangeOperation(final ChangeOperation changeOperation);

}
