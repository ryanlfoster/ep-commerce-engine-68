package com.elasticpath.service.audit.impl;

import java.util.Map;

import com.elasticpath.domain.audit.ChangeOperation;
import com.elasticpath.domain.audit.ChangeTransaction;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.persistence.api.Persistable;

/**
 * Defines methods for persisting and accessing audit data.
 */
public interface AuditDao {

	/**
	 * Persist the data changed record to the database.
	 * 
	 * @param object the object whose data has changed
	 * @param fieldName the name of the field that changed
	 * @param changeType the type of change
	 * @param oldValue the old field value
	 * @param newValue the new field value
	 * @param operation the <code>ChangeOperation</code> this change belongs to
	 */
	void persistDataChanged(final Persistable object, final String fieldName, final ChangeType changeType, final String oldValue,
			final String newValue, final ChangeOperation operation);

	/**
	 * Join the changeset transaction identified, or create a new one if none exist.
	 * 
	 * @param transactionId the ID of the transaction to join.
	 * @param persistable the object being persisted.
	 * @param metadata additional metadata to be attached to the transaction
	 * @return a <code>ChangesetTransaction</code>
	 */
	ChangeTransaction persistChangeSetTransaction(final String transactionId, final Object persistable, final Map<String, Object> metadata);

	/**
	 * Persist a single change operation, i.e. a call to a persistable method such as merge, save or delete.
	 * 
	 * @param object the root object of the operation
	 * @param type the type of change
	 * @param csTransaction the transaction this operation belongs to
	 * @param index the index indicating the order of this operation within the transaction
	 * @return a <code>ChangeOperation</code> object
	 */
	ChangeOperation persistSingleChangeOperation(final Persistable object, final ChangeType type, final ChangeTransaction csTransaction, 
			final int index);

	/**
	 * Persist a bulk change operation - i.e. a call to a bulk update or delete query.
	 * 
	 * @param queryString the query string being used
	 * @param parameters the parameters to the query
	 * @param changeType the type of change
	 * @param csTransaction the transaction this operation belongs to
	 * @param index the index indicating the order of this operation within the transaction
	 * @return a <code>ChangeOperation</code> object
	 */
	ChangeOperation persistBulkChangeOperation(final String queryString, final String parameters, final ChangeType changeType,
			final ChangeTransaction csTransaction,  final int index);

}