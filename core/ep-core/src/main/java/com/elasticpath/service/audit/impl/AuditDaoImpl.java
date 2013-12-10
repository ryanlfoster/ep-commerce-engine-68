/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.audit.impl;

import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.audit.BulkChangeOperation;
import com.elasticpath.domain.audit.ChangeOperation;
import com.elasticpath.domain.audit.ChangeTransaction;
import com.elasticpath.domain.audit.ChangeTransactionMetadata;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.domain.audit.DataChanged;
import com.elasticpath.domain.audit.SingleChangeOperation;
import com.elasticpath.persistence.openjpa.JpaPersistenceEngine;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * DAO methods for Audit transactions, operations and data changed records.
 */
public class AuditDaoImpl implements AuditDao {

	private JpaPersistenceEngine persistenceEngine;
	
	private BeanFactory beanFactory;

	private static final String SETTING_CHANGESET_ENABLED = "COMMERCE/SYSTEM/CHANGESETS/enable";
	
	@Override
	public void persistDataChanged(final Persistable object, final String fieldName, final ChangeType changeType, 
			final String oldValue, final String newValue, final ChangeOperation operation) {

		// Start a separate transaction for writing data changed records
		Transaction transaction = getPersistenceEngine().getPersistenceSession().beginTransaction();
		
		DataChanged dataChanged = getBeanFactory().getBean("dataChanged");
		dataChanged.setObjectName(object.getClass().getName());
		dataChanged.setObjectUid(object.getUidPk());
		if (object instanceof Entity) {
			dataChanged.setObjectGuid(((Entity) object).getGuid());
		}
		dataChanged.setFieldName(fieldName);		
		dataChanged.setChangeType(changeType);
		dataChanged.setChangeOperation(operation);
		dataChanged.setFieldNewValue(newValue);
		dataChanged.setFieldOldValue(oldValue);
		
		getPersistenceEngine().saveOrMerge(dataChanged);
		transaction.commit();
	}

	@Override
	public ChangeTransaction persistChangeSetTransaction(final String transactionId, final Object persistable, final Map<String, Object> metadata) {
		ChangeTransaction csTransaction = getBeanFactory().getBean("changeTransaction");
		csTransaction.setTransactionId(transactionId);
		TimeService timeService = getBeanFactory().getBean("timeService");
		csTransaction.setChangeDate(timeService.getCurrentTime());

		getPersistenceEngine().save(csTransaction);
		
		processMetadata(csTransaction, persistable, metadata);
		
		return csTransaction;
	}

	/**
	 * Process any metadata that should be attached to the given transaction.
	 * 
	 * @param csTransaction the transaction to process metadata for
	 * @param persistable the object associated with the transaction
	 * @param metadata metadata to process.
	 */
	protected void processMetadata(final ChangeTransaction csTransaction, final Object persistable, final Map<String, Object> metadata) {
		// change sets must be enabled for the audit service to associate change set GUIDs with change operations
		if (isChangeSetsEnabled() && persistable != null) {
			// if the persistable object is associated with a change set, record the change set GUID
			ChangeSetService changeSetService = getBeanFactory().getBean("changeSetService");
			String changeSetGuid = changeSetService.findChangeSetGuid(persistable);
			if (changeSetGuid != null) {
				ChangeTransactionMetadata metadata1 = generateMetadata(csTransaction, "changeSetGuid", changeSetGuid);
				getPersistenceEngine().save(metadata1);
			}
		}
		
		// the user GUID is available as meta-data in the audit service (set by the cm client)
		// use it to record the userGuid associated with the change transaction
		String userGuid = (String) metadata.get("userGuid");
		if (userGuid != null) {
			ChangeTransactionMetadata metadata2 = generateMetadata(csTransaction, "userGuid", userGuid);
			getPersistenceEngine().save(metadata2);
		}
	}

	/**
	 * Generate a new <code>ChangeTransactionMetadata</code> object for the given transaction and key/value pair.
	 * 
	 * @param csTransaction the transaction to associate with the metadata
	 * @param metadataKey the metadata key
	 * @param metadataValue the metadata value
	 * @return a new <code>ChangeTransactionMetadata</code> object
	 */
	protected ChangeTransactionMetadata generateMetadata(final ChangeTransaction csTransaction, 
			final String metadataKey, final String metadataValue) {
		ChangeTransactionMetadata csTransactionMetadata = getBeanFactory().getBean("changeTransactionMetadata");
		csTransactionMetadata.setChangeTransaction(csTransaction);
		csTransactionMetadata.setMetadataKey(metadataKey);
		csTransactionMetadata.setMetadataValue(metadataValue);
		return csTransactionMetadata;
	}

	/**
     * Checks if change sets are enabled.
     * 
     * @return True, if change sets are enabled.
     */
    protected boolean isChangeSetsEnabled() {
    	boolean changeSetsEnabled = false;
    	
    	SettingsReader settingsReader = getBeanFactory().getBean("settingsService"); //$NON-NLS-1$
		SettingValue settingValue = settingsReader.getSettingValue(SETTING_CHANGESET_ENABLED);
		if (settingValue != null) {
			changeSetsEnabled = settingValue.getBooleanValue();
    	}

    	return changeSetsEnabled;
    }

	@Override
	public ChangeOperation persistSingleChangeOperation(final Persistable object, final ChangeType type, final ChangeTransaction csTransaction, 
			final int index) {
		SingleChangeOperation operation = getBeanFactory().getBean("singleChangeOperation");
		operation.setChangeTransaction(csTransaction);
		operation.setRootObjectName(object.getClass().getName());
		operation.setRootObjectUid(object.getUidPk());
		if (object instanceof Entity) {
			operation.setRootObjectGuid(((Entity) object).getGuid());
		}
		operation.setChangeType(type);
		operation.setOperationOrder(index);
		
		getPersistenceEngine().save(operation);
		return operation;
	}

	@Override
	public ChangeOperation persistBulkChangeOperation(final String queryString, final String parameters, final ChangeType changeType,
			final ChangeTransaction csTransaction, final int index) {
		BulkChangeOperation operation = getBeanFactory().getBean("bulkChangeOperation");
		operation.setChangeTransaction(csTransaction);
		operation.setChangeType(changeType);
		operation.setOperationOrder(index);
		operation.setQueryString(normalizeWhitespace(queryString));
		operation.setParameters(parameters);
		
		getPersistenceEngine().save(operation);
		return operation;
	}

	private String normalizeWhitespace(final String queryString) {
		// replace newlines and tab chars with a single space to make it easier to audit later
		return queryString.replaceAll("\\s+", " ");
	}

	/**
	 * Set the persistence engine.
	 * 
	 * @param persistenceEngine the persistenceEngine to set
	 */
	public void setPersistenceEngine(final JpaPersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Get the persistence engine.
	 * 
	 * @return the persistenceEngine
	 */
	public JpaPersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Set the bean factory.
	 * 
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Get the bean factory.
	 * 
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}


}
