/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.audit.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
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
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test that the methods of AuditDaoImpl persist and load the audit data as expected.
 */
public class AuditDaoImplTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private AuditDaoImpl auditDaoImpl;
	
	private JpaPersistenceEngine persistenceEngine;
	
	private BeanFactory beanFactory;

	private ChangeTransaction changeTransaction;

	private static final String TRANSACTION_ID = "1234";

	private static final long UIDPK = 1000L;

	private static final String GUID = "GUID";


	/**
	 * Set up required for all tests.
	 * 
	 * @throws java.lang.Exception in case of error setting up 
	 */
	@Before
	public void setUp() throws Exception {
		// Mock objects required for all tests
		beanFactory = context.mock(BeanFactory.class);
		changeTransaction = context.mock(ChangeTransaction.class);
		persistenceEngine = context.mock(JpaPersistenceEngine.class);
		
		auditDaoImpl = new AuditDaoImpl();
		auditDaoImpl.setBeanFactory(beanFactory);
		auditDaoImpl.setPersistenceEngine(persistenceEngine);
	}

	/**
	 * Test persisting data changed creates a new <code>DataChanged</code> object and saves it to the DB.
	 */
	@Test
	public void testPersistDataChanged() {
		final Entity entity = context.mock(Entity.class);
		final PersistenceSession persistenceSession = context.mock(PersistenceSession.class);
		final Transaction transaction = context.mock(Transaction.class);
		final ChangeOperation operation = context.mock(ChangeOperation.class);
		final DataChanged dataChanged = context.mock(DataChanged.class);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).getPersistenceSession(); will(returnValue(persistenceSession));
				oneOf(persistenceSession).beginTransaction(); will(returnValue(transaction));
				
				oneOf(entity).getUidPk(); will(returnValue(UIDPK));
				oneOf(entity).getGuid(); will(returnValue(GUID));
				
				oneOf(beanFactory).getBean("dataChanged"); will(returnValue(dataChanged));
				oneOf(dataChanged).setObjectName(entity.getClass().getName());
				oneOf(dataChanged).setObjectUid(UIDPK);
				oneOf(dataChanged).setObjectGuid(GUID);
				oneOf(dataChanged).setFieldName("changedField");
				oneOf(dataChanged).setChangeType(ChangeType.UPDATE);
				oneOf(dataChanged).setFieldOldValue("old value");
				oneOf(dataChanged).setFieldNewValue("new value");
				oneOf(dataChanged).setChangeOperation(operation);
				
				oneOf(persistenceEngine).saveOrMerge(dataChanged);
				oneOf(transaction).commit();
			}
		});
		auditDaoImpl.persistDataChanged(entity, "changedField", ChangeType.UPDATE, "old value", "new value", operation);
	}

	/**
	 * Test saving a transaction records the necessary data and metadata, including using the
	 * time service for the date.
	 */
	@Test
	public void testJoinChangeSetTransaction() {
		final Entity entity = context.mock(Entity.class);
		final TimeService timeService = context.mock(TimeService.class);
		final Date changeDate = new Date();
		final Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("userGuid", "USERGUID");
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean("changeTransaction");
					will(returnValue(changeTransaction));

				oneOf(beanFactory).getBean(ContextIdNames.TIME_SERVICE);
					will(returnValue(timeService));
				oneOf(timeService).getCurrentTime();
					will(returnValue(changeDate));
					
				oneOf(changeTransaction).setTransactionId(TRANSACTION_ID);
				oneOf(changeTransaction).setChangeDate(changeDate);
				
				oneOf(persistenceEngine).save(changeTransaction);
			}
		});
		assertEntityInChangeSet(entity, "CSGUID");
		assertAuditMetadata("changeSetGuid", "CSGUID");
		assertAuditMetadata("userGuid", "USERGUID");
		auditDaoImpl.persistChangeSetTransaction(TRANSACTION_ID, entity, metadata);
	}

	/**
	 * Test that persisting a single change operation persists the appropriate values.
	 */
	@Test
	public void testPersistSingleChangeOperation() {
		final Entity entity = context.mock(Entity.class);
		final SingleChangeOperation operation = context.mock(SingleChangeOperation.class);
		final int operationIndex = 5;
		
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean("singleChangeOperation");
					will(returnValue(operation));
					
				oneOf(entity).getUidPk();
					will(returnValue(UIDPK));
				oneOf(entity).getGuid();
					will(returnValue(GUID));
					
				oneOf(operation).setChangeType(ChangeType.CREATE);
				oneOf(operation).setOperationOrder(operationIndex);
				oneOf(operation).setRootObjectName(entity.getClass().getName());
				oneOf(operation).setRootObjectUid(UIDPK);
				oneOf(operation).setRootObjectGuid(GUID);
				oneOf(operation).setChangeTransaction(changeTransaction);
				
				oneOf(persistenceEngine).save(operation);
			}
		});
		auditDaoImpl.persistSingleChangeOperation(entity, ChangeType.CREATE,  changeTransaction, operationIndex);
	}

	/**
	 * Test that persisting a bulk change operation persists the appropriate values..
	 */
	@Test
	public void testPersistBulkChangeOperation() {
		final String queryString = "UPDATE SomeObjectImpl so SET so.field = null WHERE so.otherField = ?1";
		final String parameters = "[somevalue]";
		final BulkChangeOperation operation = context.mock(BulkChangeOperation.class);
		final int nextIndex = 2;
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean("bulkChangeOperation"); will(returnValue(operation));
				oneOf(operation).setQueryString(queryString);
				oneOf(operation).setParameters(parameters);
				oneOf(operation).setChangeType(ChangeType.UPDATE);
				oneOf(operation).setChangeTransaction(changeTransaction);
				oneOf(operation).setOperationOrder(nextIndex);
				
				oneOf(persistenceEngine).save(operation);
			}
		});
		auditDaoImpl.persistBulkChangeOperation(queryString, parameters, ChangeType.UPDATE, changeTransaction, nextIndex);
	}

	/**
	 * Expectations for when metadata should be recorded against the transaction.
	 * 
	 * @param key the key for the metdata
	 * @param value the metadata value
	 */
	public void assertAuditMetadata(final String key, final String value) {
		final ChangeTransactionMetadata metadata = context.mock(ChangeTransactionMetadata.class, key);
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean("changeTransactionMetadata");
					will(returnValue(metadata));
				oneOf(metadata).setChangeTransaction(changeTransaction);
				oneOf(metadata).setMetadataKey(key);
				oneOf(metadata).setMetadataValue(value);
				
				oneOf(persistenceEngine).save(metadata);
			}
		});

	}
	
	/**
	 * Expectations for when an entity should be part of a change set.
	 * 
	 * @param object the entity that should be part of a change set
	 * @param guid the guid of the change set
	 */
	public void assertEntityInChangeSet(final Persistable object, final String guid) {
		final SettingsReader settingsReader = context.mock(SettingsReader.class);
		final SettingValue settingValue = context.mock(SettingValue.class);
		final ChangeSetService changeSetService = context.mock(ChangeSetService.class);
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.SETTINGS_SERVICE);
					will(returnValue(settingsReader));
				oneOf(settingsReader).getSettingValue("COMMERCE/SYSTEM/CHANGESETS/enable");
					will(returnValue(settingValue));
				oneOf(settingValue).getBooleanValue();
					will(returnValue(true));
					
				oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_SERVICE);
					will(returnValue(changeSetService));
				oneOf(changeSetService).findChangeSetGuid(object);
					will(returnValue(guid));
			}
		});

	}
	
}
