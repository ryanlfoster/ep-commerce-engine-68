package com.elasticpath.persistence.impl;

import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.FlushModeType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.apache.openjpa.ee.ManagedRuntime;
import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.enhance.Reflection;
import org.apache.openjpa.event.LifecycleEvent;
import org.apache.openjpa.kernel.OpenJPAStateManager;
import org.apache.openjpa.meta.ClassMetaData;
import org.apache.openjpa.meta.FieldMetaData;
import org.apache.openjpa.meta.ValueMetaData;
import org.apache.openjpa.persistence.JPAFacadeHelper;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.domain.audit.ChangeOperation;
import com.elasticpath.domain.audit.ChangeTransaction;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.persistence.openjpa.JpaPersistenceEngine;
import com.elasticpath.persistence.openjpa.PersistenceEngineEntityListener;
import com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl;
import com.elasticpath.service.audit.impl.AuditDao;

/**
 * Listener for the relevant lifecycle events.
 * 
 * <ul>
 * <li>Persist events are used to ensure entirely new objects are saved.</li>
 * <li>Attach events are used to ensure new objects created as part of a merge are tracked.</li>
 * </ul>
 */
public class AuditEntityListener implements PersistenceEngineEntityListener {
	
	private static final Logger LOG = Logger.getLogger(AuditEntityListener.class);
	
	private AuditDao auditDao;
	
	private final ThreadLocal<ChangeOperation> changeOperation = new ThreadLocal<ChangeOperation>();
	
	private final ThreadLocal<ChangeOperation> previousOperation = new ThreadLocal<ChangeOperation>();
	
	private Collection<String> auditableClasses;
	
	private Map<String, String> nonAuditableNamedQueryMap = new HashMap<String, String>();
	
	private JpaPersistenceEngine persistenceEngine;

	private ThreadLocalMap<String, Object> metadataMap;

	@Override
	public void eventOccurred(final LifecycleEvent event) {
		switch(event.getType()) {
		case LifecycleEvent.BEFORE_PERSIST:
			beforePersist(event);
			break;
		case LifecycleEvent.BEFORE_ATTACH:
			beforeAttach(event);
			break;
		case LifecycleEvent.BEFORE_DELETE:
			beforeDelete(event);
			break;
		default:
			// no-op
		}
	}


	/**
	 * Before persisting, save changeset details for a new object.
	 *
	 * @param event the persist event
	 */
	private void beforePersist(final LifecycleEvent event) {
		Object object = event.getSource();
		if (!getAuditableClasses().contains(object.getClass().getName())) {
			return;
		}			
		
		saveNewObject((Persistable) object);
	}

	/**
	 * Before attaching, check if we are attaching a new object and if so save change details.
	 *
	 * @param event the attach event
	 */
	private void beforeAttach(final LifecycleEvent event) {
		Object object = event.getSource();
		if (!getAuditableClasses().contains(object.getClass().getName())) {
			return;
		}
		PersistenceCapable pcObject = (PersistenceCapable) object;
		if (pcObject.pcGetStateManager() == null) {
			saveNewObject((Persistable) object);
		} else {
			saveUpdatedFields(pcObject);
		}
	}
	
	/**
	 * Intercept delete and save a delete type changeset.
	 *
	 * @param event the delete event
	 */
	private void beforeDelete(final LifecycleEvent event) {
		Object sourceObject = event.getSource();
		if (!getAuditableClasses().contains(sourceObject.getClass().getName())) {
			return;
		}
		createDeleteSet((Persistable) sourceObject);
	}
	
	/**
	 * Save new object.
	 * @param object is the persistent object to save
	 */
	protected void saveNewObject(final Persistable object) {
		if (object instanceof PersistenceCapable) {
			ClassMetaData metaData = JPAFacadeHelper.getMetaData(persistenceEngine.getEntityManager(), object.getClass());			
			for (FieldMetaData fieldMetaData : metaData.getFields()) {				
				Object field = getField(object, fieldMetaData.getName());
				if (isFieldAuditable(fieldMetaData, field)) {
					recordDataChanged(object, fieldMetaData.getName(), field, ChangeType.CREATE);
				}
			}
		}
	}
	
	/**
	 * Determine whether the given field is auditable. Primary keys, version fields and transient fields
	 * do not get audited. Collections/Maps need to be examined further to determine auditability.
	 * 
	 * @param fieldMetaData the field's metadata
	 * @param field the field to examine
	 * @return whether this is an internal field or not
	 */
	protected boolean isFieldAuditable(final FieldMetaData fieldMetaData, final Object field) {
				
		if (fieldMetaData.isPrimaryKey()
			|| fieldMetaData.isVersion()
			|| fieldMetaData.isTransient()
			|| isNonAuditableCollectionOrMap(fieldMetaData, field)) {  	
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Record data changed for an object's fields.
	 * @param object is the persistent object
	 * @param fieldName is the name of the field
	 * @param field is the field of the object
	 * @param changeType is the type of change
	 */
	@SuppressWarnings("unchecked")
	protected void recordDataChanged(final Persistable object, final String fieldName, final Object field, final ChangeType changeType) {

		if (field instanceof Collection) {
			recordCollectionChanged(object, fieldName, (Collection<Persistable>) field, changeType);
		} else if (field instanceof Map) {
			recordMapChanged(object, fieldName, (Map<Object, Persistable>) field, changeType);
		} else {
			recordFieldChanged(object, fieldName, field, changeType);
			
		}

	}
	
	/**
	 * Determine whether the given field is a non-auditable collection. If the collection is
	 * embedded or the values are not <code>PersistenceCapable</code> then it does need to
	 * be audited.
	 *  
	 * @param fieldMetaData the field's meta data
	 * @param field the field to examine
	 * @return true if the field is a non-auditable collection or map
	 */
	protected boolean isNonAuditableCollectionOrMap(final FieldMetaData fieldMetaData, final Object field) {
		if (field instanceof Collection || field instanceof Map) {
			ValueMetaData vmd = fieldMetaData.getElement();
			if (vmd.isDeclaredTypePC()  || fieldMetaData.isEmbedded()) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Get a named field from the given object.
	 * 
	 * @param object the object that owns the field
	 * @param fieldName the name of the field
	 * @param <T> the Java type of the field
	 * @return the underlying field object
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getField(final Object object, final String fieldName) {
		Method method = Reflection.findGetter(object.getClass(), fieldName, true);
		return (T) Reflection.get(object, method);
	}
	
	/**
	 * Record a change to a collection of Persistence objects.
	 * This will record the Guid (where possible) or UidPk of the changed object as the value. 
	 * 
	 * @param object the object whose collection has changed
	 * @param fieldName the name of the collection field
	 * @param collection the collection
	 * @param changeType the type of change
	 */
	protected void recordCollectionChanged(final Persistable object, final String fieldName, final Collection<Persistable> collection, 
			final ChangeType changeType) {
		ChangeOperation operation = getCurrentOperation(object, changeType);
		if (ChangeType.CREATE.equals(changeType)) {
			for (Persistable element : collection) {
				getAuditDao().persistDataChanged(object, fieldName, changeType, null, getFieldValue(element), operation);
			}
		} else if (ChangeType.UPDATE.equals(changeType)) {
			Persistable oldObject = persistenceEngine.get(object.getClass(), object.getUidPk());
			Collection<?> oldCollection = getField(oldObject, fieldName);
			for (Object removed : CollectionUtils.subtract(oldCollection, collection)) {
				getAuditDao().persistDataChanged(object, fieldName, ChangeType.DELETE, getFieldValue(removed), null, operation);
			}
			for (Object added : CollectionUtils.subtract(collection, oldCollection)) {
				getAuditDao().persistDataChanged(object, fieldName, ChangeType.CREATE, null, getFieldValue(added), operation);
			}
		} else if (ChangeType.DELETE.equals(changeType)) { 
			for (Persistable element : collection) {
				getAuditDao().persistDataChanged(object, fieldName, changeType, getFieldValue(element), null, operation);
			}
		}
	}	
	
	/**
	 * Record a change to a map where the map values are Persistence objects.
	 * This will record the field value as key=Guid (where possible) or key=UidPk.
	 *
	 * @param object the object whose map has changed
	 * @param fieldName the name of the map field
	 * @param map the map
	 * @param changeType the type of change
	 */
	protected void recordMapChanged(final Persistable object, final String fieldName, final Map<Object, Persistable> map, 
			final ChangeType changeType) {
		ChangeOperation operation = getCurrentOperation(object, changeType);
		if (ChangeType.CREATE.equals(changeType)) {
			for (Map.Entry<Object, Persistable> entry : map.entrySet()) {
				getAuditDao().persistDataChanged(object, fieldName, changeType, null, entry.getKey() + "=" + getFieldValue(entry.getValue()), 
						operation);
			}
		} else if (ChangeType.UPDATE.equals(changeType)) {
			Persistable oldObject = persistenceEngine.get(object.getClass(), object.getUidPk());
			Map<Object, Persistable> oldMap = getField(oldObject, fieldName);
			for (Object removed : CollectionUtils.subtract(oldMap.keySet(), map.keySet())) {
				getAuditDao().persistDataChanged(object, fieldName, ChangeType.DELETE, removed + "=" + getFieldValue(oldMap.get(removed)), null, 
						operation);
			}
			for (Object added : CollectionUtils.subtract(map.keySet(), oldMap.keySet())) {
				getAuditDao().persistDataChanged(object, fieldName, ChangeType.CREATE, null, added + "=" + getFieldValue(map.get(added)), 
						operation);
			}
			for (Object possiblyChanged : CollectionUtils.intersection(oldMap.keySet(), map.keySet())) {
				if (!ObjectUtils.equals(oldMap.get(possiblyChanged), map.get(possiblyChanged))) {
					getAuditDao().persistDataChanged(object, fieldName, ChangeType.UPDATE, 
							possiblyChanged + "=" + getFieldValue(oldMap.get(possiblyChanged)), 
							possiblyChanged + "=" + getFieldValue(map.get(possiblyChanged)), operation);
				}
			}
		} else if (ChangeType.DELETE.equals(changeType)) {
			for (Map.Entry<Object, Persistable> entry : map.entrySet()) {
				getAuditDao().persistDataChanged(object, fieldName, changeType, entry.getKey() + "=" + getFieldValue(entry.getValue()), null, 
						operation);
			}
			
		}
	}
	
	/**
	 * Record a change to a basic field.
	 * 
	 * @param object the object whose field has changed
	 * @param fieldName the name of the field that changed
	 * @param field the field that changed
	 * @param changeType the type of change
	 */
	protected void recordFieldChanged(final Persistable object, final String fieldName, final Object field, final ChangeType changeType) {
		String oldValue = null;
		String newValue = null;
		
		// Update: set old value and new value
		if (ChangeType.CREATE.equals(changeType)) {
			newValue = getFieldValue(field);
		} else if (ChangeType.UPDATE.equals(changeType)) {			
			newValue = getFieldValue(field);
			Persistable oldObject = persistenceEngine.get(object.getClass(), object.getUidPk());
			oldValue = getFieldValue(getField(oldObject, fieldName));
		} else if (ChangeType.DELETE.equals(changeType)) {
			oldValue = getFieldValue(field);
		}
		getAuditDao().persistDataChanged(object, fieldName, changeType, oldValue, newValue, getCurrentOperation(object, changeType));
	}
	
	/**
	 * Get field value.
	 * @param field is the field
	 * @return the field value 
	 */
	protected String getFieldValue(final Object field) {
		if (field == null) {
			return null;
		}
		if (field instanceof Entity) {
			return ((Entity) field).getGuid();
		}
		if (field instanceof Persistable) {
			return String.valueOf(((Persistable) field).getUidPk());
		}
		return field.toString();
	}
	
	/**
	 * Save the dirty fields of the given object.	 
	 * @param object is the persistent object to save
	 */
	protected void saveUpdatedFields(final PersistenceCapable object) {
		OpenJPAStateManager manager = (OpenJPAStateManager) object.pcGetStateManager();
		ClassMetaData metaData = JPAFacadeHelper.getMetaData(persistenceEngine.getEntityManager(), object.getClass());
		BitSet dirtySet = manager.getDirty();
		for (int dirtyIndex = dirtySet.nextSetBit(0); dirtyIndex >= 0; dirtyIndex = dirtySet.nextSetBit(dirtyIndex + 1)) {
			FieldMetaData fieldMetaData = metaData.getField(dirtyIndex);
			Object field = getField(object, fieldMetaData.getName());
			if (isFieldAuditable(fieldMetaData, field)) {								
				recordDataChanged((Persistable) object, fieldMetaData.getName(), field, ChangeType.UPDATE);
			}
		}
	}
	
	/**
	 * Create delete set.
	 * @param object is the persistent object
	 */
	protected void createDeleteSet(final Persistable object) {								
		if (object instanceof PersistenceCapable) {
			PersistenceCapable pcObject = (PersistenceCapable) object;

			OpenJPAStateManager manager = (OpenJPAStateManager) pcObject.pcGetStateManager();

			ClassMetaData metaData = manager.getMetaData();
					
			//note: get all the fields - even if it causes them to be loaded.  
			//      a deleted bean may not be realized but auditing in this case should see the fields
			//      as they were in the db before the delete.
			for (FieldMetaData fieldMetaData : metaData.getFields()) {				
				Object field = getField(object, fieldMetaData.getName());
				if (isFieldAuditable(fieldMetaData, field)) {
					recordDataChanged(object, fieldMetaData.getName(), field, ChangeType.DELETE);
				}
			}		
		} else {
			LOG.error("Object not instance of PersistenceCapable");
		}
	}
			
	/**
	 * Get the current change operation from a <code>ThreadLocal</code> variable.
	 * 
	 * @param object The object that is being changed.
	 * @param type The type of change that is occurring.
	 * @return the current <code>ChangeOperation</code>
	 */
	protected ChangeOperation getCurrentOperation(final Persistable object, final ChangeType type) {
		if (changeOperation.get() == null) {
			ChangeTransaction csTransaction = joinChangeTransaction(getTransactionId(), object);
			ChangeOperation operation = getAuditDao().persistSingleChangeOperation(object, type, csTransaction, 
					getNextOperationIndex(getTransactionId()));
			changeOperation.set(operation);
		}
		return changeOperation.get();
	}
	
	/**
	 * Set the audit dao.
	 * 
	 * @return the auditDao
	 */
	public AuditDao getAuditDao() {
		return auditDao;
	}
	
	/**
	 * Get the audit dao.
	 * 
	 * @param auditDao the auditDao to set
	 */
	public void setAuditDao(final AuditDao auditDao) {
		this.auditDao = auditDao;
	}
	
	/**
	 * Get the collection of class names that we want to audit.
	 *
	 * @return the collection of auditable classes.
	 */
	public Collection<String> getAuditableClasses() {
		return auditableClasses;
	}

	/**
	 * Set the list of class names whose changes can be audited.
	 *
	 * @param auditableClasses the collection of auditable classes to set
	 */
	public void setAuditableClasses(final Collection<String> auditableClasses) {
		this.auditableClasses = auditableClasses;
	}
	
	/**
	 * Set the wired non-auditable query names onto the audit listener.
	 * 
	 * @param nonAuditableNamedQueries collection of named queries that should not be audited.
	 */
	public void setNonAuditableNamedQueries(final Collection<String> nonAuditableNamedQueries) {
		nonAuditableNamedQueryMap = new HashMap<String, String>();
		for (String namedQuery : nonAuditableNamedQueries) {
			nonAuditableNamedQueryMap.put(namedQuery, namedQuery);
		}
	}
	
	/*
	 * Returns true this query should be audited.
	 */
	private boolean isAuditableNamedQuery(final String namedQuery) {
		return !nonAuditableNamedQueryMap.containsKey(namedQuery);
	}
	
	/**
	 * @param persistenceEngine the persistenceEngine to set
	 */
	public void setPersistenceEngine(final JpaPersistenceEngineImpl persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}


	@Override
	public void beginSingleOperation(final Persistable object, final ChangeType type) {
		
		// If it's a delete then we always create the operation.
		// Otherwise we only do it if the class is auditable
		if (type == ChangeType.DELETE || getAuditableClasses().contains(object.getClass().getName())) {
		
			// Start a separate transaction for writing operation rows, and force a flush on commit
			PersistenceSession persistenceSession = persistenceEngine.getPersistenceSession();
			Transaction transaction = persistenceSession.beginTransaction();
			
			OpenJPAEntityManager openJpaEM = getOpenJPAEntityManager();
			final FlushModeType flushMode = openJpaEM.getFlushMode();
			openJpaEM.setFlushMode(FlushModeType.COMMIT);
						
			try {
				ChangeTransaction csTransaction = joinChangeTransaction(getTransactionId(), object);
				ChangeOperation operation = getAuditDao()
						.persistSingleChangeOperation(object, type,	csTransaction,
								getNextOperationIndex(getTransactionId()));
				transaction.commit();
				setCurrentOperation(operation);
			} finally {
				openJpaEM.setFlushMode(flushMode);
			}
		}
	}

	@Override
	public void beginBulkOperation(final String queryName, final String queryString,
			final String parameters, final ChangeType type) {
		
		if (isAuditableNamedQuery(queryName)) {
			// Start a separate transaction for writing operation rows, and force a flush on commit
			PersistenceSession persistenceSession = persistenceEngine.getPersistenceSession();
			Transaction transaction = persistenceSession.beginTransaction();
			
			ChangeTransaction csTransaction = joinChangeTransaction(getTransactionId(), null);
			ChangeOperation operation = getAuditDao().persistBulkChangeOperation(queryString, parameters, type, csTransaction, 
					getNextOperationIndex(getTransactionId()));
			transaction.commit();
			
			setCurrentOperation(operation);
		}
	}

	@Override
	public void endSingleOperation(final Persistable object,
			final ChangeType type) {
		if (type == ChangeType.DELETE || getAuditableClasses().contains(object.getClass().getName())) {
			setCurrentOperation(null);
		}
	}
	
	@Override
	public void endBulkOperation() {
		setCurrentOperation(null);
	}
	
	/**
	 * Join an existing change transaction if there is one otherwise create/persist a new one.
	 * 
	 * @param transactionId the transaction ID
	 * @param persistable the object being persisted.
	 * @return the <code>ChangeTransaction</code>
	 */
	private ChangeTransaction joinChangeTransaction(final String transactionId, final Object persistable) {
		ChangeOperation previousOp = this.previousOperation.get();
		if (previousOp != null && transactionId.equals(previousOp.getChangeTransaction().getTransactionId())) {
			return previousOp.getChangeTransaction();
		}
		return getAuditDao().persistChangeSetTransaction(transactionId, persistable, metadataMap);
	}
	
	/**
	 * Get the next operation index by incrementing the current operation if we are in the same
	 * transaction, otherwise we start at 1.
	 * 
	 * @param transactionId the transaction whose operation we are persisting
	 * @return the index of order of the next operation within the transaction
	 */
	protected int getNextOperationIndex(final String transactionId) {
		ChangeOperation previousOp = this.previousOperation.get();
		if (previousOp != null && previousOp.getChangeTransaction().getTransactionId().equals(transactionId)) {
			return previousOp.getOperationOrder() + 1;
		}
		return 1;
	}
	
	/**
	 * Set the current operation into a <code>ThreadLocal</code> variable as we need
	 * to be able to access it from a listener called by OpenJPA.
	 * 
	 * @param operation the operation to set
	 */
	protected void setCurrentOperation(final ChangeOperation operation) {
		if (changeOperation.get() != null) {
			previousOperation.set(changeOperation.get());
		}
		changeOperation.set(operation);
	}

	/**
	 * Get the OpenJPA Entity Manager.
	 * 
	 * @return an <code>OpenJPAEntityManager</code> instance
	 */
	protected OpenJPAEntityManager getOpenJPAEntityManager() {
		return OpenJPAPersistence.cast(persistenceEngine.getEntityManager());
	}

	/**
	 * Returns the id for the current transaction from the managed runtime.
	 * @return The transaction id
	 */
	public String getTransactionId() {
		ManagedRuntime runtime = persistenceEngine.getBroker().getManagedRuntime();
		int transactionId = 0;
		try {
			Object key = runtime.getTransactionKey();
			transactionId = key.hashCode();
		} catch (Exception e) {
			LOG.error("Error trying to find transaction", e);
		}
		return String.valueOf(transactionId);
	}


	/**
	 * Get the map of metadata for this listener.
	 * 
	 * @param metadataMap the metadataMap to set
	 */
	public void setMetadataMap(final ThreadLocalMap<String, Object> metadataMap) {
		this.metadataMap = metadataMap;
	}


}