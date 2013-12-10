package com.elasticpath.persistence.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import org.apache.openjpa.ee.ManagedRuntime;
import org.apache.openjpa.kernel.Broker;
import org.apache.openjpa.meta.FieldMetaData;
import org.apache.openjpa.meta.ValueMetaData;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.domain.audit.ChangeOperation;
import com.elasticpath.domain.audit.ChangeTransaction;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl;
import com.elasticpath.service.audit.impl.AuditDao;

/**
 * Unit test for the {@code AuditEntityListener} class.
 */
public class AuditEntityListenerTest {
	
	private AuditEntityListener auditEntityListener;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
			setThreadingPolicy(new Synchroniser());
		}
	};
	
	private AuditDao auditDao;

	private OpenJPAEntityManager openJPAEntityManager;
	
	private static final long UIDPK = 1000L;
	
	@SuppressWarnings("unchecked")
	private final ThreadLocalMap<String, Object> metadataMap = context.mock(ThreadLocalMap.class);

	/**
	 * Interface for mocking an Entity with a method that provides a way to get a mocked field
	 * to avoid calling the OpenJPA Reflection methods.
	 */
	protected interface EntityWithField extends Entity {
		/**
		 * Method for use in mock objects as an easy way to get a field.
		 * @return an object that represents a field
		 */
		Object getField();
	}
	
	/**
	 * Set up mocks and stubs required by all tests.
	 * 
	 * @throws java.lang.Exception in case of errors in test setup
	 */
	@Before
	public void setUp() throws Exception {
		
		auditDao = context.mock(AuditDao.class);
		openJPAEntityManager = context.mock(OpenJPAEntityManager.class);

		// Instantiate the object under test, mocking out unrequired parts.
		auditEntityListener = new AuditEntityListener() {
						
			@SuppressWarnings("unchecked")
			@Override
			protected <T> T getField(final Object object, final String fieldName) {
					return (T) ((EntityWithField) object).getField();
			}

			@Override
			protected OpenJPAEntityManager getOpenJPAEntityManager() {
				return openJPAEntityManager;
			}

		};
		auditEntityListener.setAuditDao(auditDao);
		auditEntityListener.setMetadataMap(metadataMap);
		
	}

	/**
	 * Test the rules for whether a Primary Key field is auditable.
	 */
	@Test
	public void testIsPrimaryKeyFieldAuditable() {
		final FieldMetaData fieldMetaData = context.mock(FieldMetaData.class);
		context.checking(new Expectations() {
			{
				allowing(fieldMetaData).getName();
					will(returnValue("fieldName"));
				allowing(fieldMetaData).isTransient();
					will(returnValue(false));
				allowing(fieldMetaData).isVersion();
					will(returnValue(false));
				oneOf(fieldMetaData).isPrimaryKey();
					will(returnValue(true));
			}
		});
		assertFalse("Primary key fields should not be auditable", auditEntityListener.isFieldAuditable(fieldMetaData, null));
	}
	
	/**
	 * Test the rules for whether a Version field is auditable.
	 */
	@Test
	public void testIsVersionFieldAuditable() {
		final FieldMetaData fieldMetaData = context.mock(FieldMetaData.class);
		context.checking(new Expectations() {
			{
				allowing(fieldMetaData).getName();
					will(returnValue("fieldName"));
				allowing(fieldMetaData).isPrimaryKey();
					will(returnValue(false));
				allowing(fieldMetaData).isTransient();
					will(returnValue(false));
				oneOf(fieldMetaData).isVersion();
					will(returnValue(true));
			}
		});
		assertFalse("Version fields should not be auditable", auditEntityListener.isFieldAuditable(fieldMetaData, null));
	}

	/**
	 * Test the rules for whether a transient field is auditable.
	 */
	@Test
	public void testIsTransientFieldAuditable() {
		final FieldMetaData fieldMetaData = context.mock(FieldMetaData.class);
		context.checking(new Expectations() {
			{
				allowing(fieldMetaData).getName();
					will(returnValue("fieldName"));
				allowing(fieldMetaData).isPrimaryKey();
					will(returnValue(false));
				allowing(fieldMetaData).isVersion();
					will(returnValue(false));
				oneOf(fieldMetaData).isTransient();
					will(returnValue(true));
			}
		});
		assertFalse("Transient fields should not be auditable", auditEntityListener.isFieldAuditable(fieldMetaData, null));
	}
	
	/**
	 * Test that non-collection objects are not identified as non-auditable collections.
	 */
	@Test
	public void testIsNonAuditableCollectionOrMapForNormalField() {
		final FieldMetaData fieldMetaData = context.mock(FieldMetaData.class);
		final Object nonCollectionField = new Object();
		assertFalse("Non-collections should be auditable", 
				auditEntityListener.isNonAuditableCollectionOrMap(fieldMetaData, nonCollectionField));
	}
	
	/**
	 * Test that collections of non-embedded basic objects are not audited.
	 */
	@Test
	public void testIsNonAuditableCollectionOrMapForBasicCollection() {
		final FieldMetaData fieldMetaData = context.mock(FieldMetaData.class);
		final ValueMetaData valueMetaData = context.mock(ValueMetaData.class);
		final Object collectionField = context.mock(Collection.class);
		context.checking(new Expectations() {
			{
				oneOf(fieldMetaData).getElement();
					will(returnValue(valueMetaData));
					
				oneOf(fieldMetaData).isEmbedded();
					will(returnValue(false));
					
				oneOf(valueMetaData).isDeclaredTypePC();
					will(returnValue(false));
			}
		});
		assertTrue("Collections of non-PC objects should not be auditable", 
				auditEntityListener.isNonAuditableCollectionOrMap(fieldMetaData, collectionField));
	}

	/**
	 * Test that collections of embedded basic objects are audited.
	 */
	@Test
	public void testIsNonAuditableCollectionOrMapForEmbeddedCollection() {
		final FieldMetaData fieldMetaData = context.mock(FieldMetaData.class);
		final ValueMetaData valueMetaData = context.mock(ValueMetaData.class);
		final Object collectionField = context.mock(Collection.class);
		context.checking(new Expectations() {
			{
				oneOf(fieldMetaData).getElement();
					will(returnValue(valueMetaData));
					
				oneOf(fieldMetaData).isEmbedded();
					will(returnValue(true));
					
				allowing(valueMetaData).isDeclaredTypePC();
					will(returnValue(false));
			}
		});
		assertFalse("Collections of embedded objects should be auditable", 
				auditEntityListener.isNonAuditableCollectionOrMap(fieldMetaData, collectionField));
	}

	/**
	 * Test that collections of persistable objects are audited.
	 */
	@Test
	public void testIsNonAuditableCollectionOrMapForPCCollection() {
		final FieldMetaData fieldMetaData = context.mock(FieldMetaData.class);
		final ValueMetaData valueMetaData = context.mock(ValueMetaData.class);
		final Object collectionField = context.mock(Collection.class);
		context.checking(new Expectations() {
			{
				oneOf(fieldMetaData).getElement();
					will(returnValue(valueMetaData));
					
				allowing(fieldMetaData).isEmbedded();
					will(returnValue(false));
					
				oneOf(valueMetaData).isDeclaredTypePC();
					will(returnValue(true));
			}
		});
		assertFalse("Collections of Persistence objects should be auditable", 
				auditEntityListener.isNonAuditableCollectionOrMap(fieldMetaData, collectionField));
	}

	/**
	 * Test that maps of persistable objects are audited.
	 */
	@Test
	public void testIsNonAuditableCollectionOrMapForPCMap() {
		final FieldMetaData fieldMetaData = context.mock(FieldMetaData.class);
		final ValueMetaData valueMetaData = context.mock(ValueMetaData.class);
		final Object mapField = context.mock(Map.class);
		context.checking(new Expectations() {
			{
				oneOf(fieldMetaData).getElement();
					will(returnValue(valueMetaData));
					
				allowing(fieldMetaData).isEmbedded();
					will(returnValue(false));
					
				oneOf(valueMetaData).isDeclaredTypePC();
					will(returnValue(true));
			}
		});
		assertFalse("Maps of Persistence objects should be auditable", 
				auditEntityListener.isNonAuditableCollectionOrMap(fieldMetaData, mapField));
	}
	
	/**
	 * Test that a CREATE change type records the new field value.
	 */
	@Test
	public void testRecordFieldChangedCreate() {
		final Entity entity = context.mock(Entity.class);
		final String field = "Create Field";
		final ChangeOperation operation = context.mock(ChangeOperation.class);
		
		auditEntityListener.setCurrentOperation(operation);
		
		context.checking(new Expectations() {
			{
				oneOf(auditDao).persistDataChanged(entity, "createField", ChangeType.CREATE, null, field, operation);
			}
		});
		auditEntityListener.recordFieldChanged(entity, "createField", field, ChangeType.CREATE);
	}
	
	/**
	 * Test that a DELETE change type records the old field value.
	 */
	@Test
	public void testRecordFieldChangedDelete() {
		final Entity entity = context.mock(Entity.class);
		final String field = "Delete Field";
		final ChangeOperation operation = context.mock(ChangeOperation.class);
		
		auditEntityListener.setCurrentOperation(operation);
		
		context.checking(new Expectations() {
			{
				oneOf(auditDao).persistDataChanged(entity, "deleteField", ChangeType.DELETE, field, null, operation);
			}
		});
		auditEntityListener.recordFieldChanged(entity, "deleteField", field, ChangeType.DELETE);
	}

	/**
	 * Test that changing collection members audits the references.
	 */
	@Test
	public void testRecordCollectionChangedUpdate() {
		final Entity entity = context.mock(Entity.class, "new");
		final EntityWithField oldEntity = context.mock(EntityWithField.class, "old");

		final Set<Persistable> newCollection = new HashSet<Persistable>();
		final Set<Persistable> oldCollection = new HashSet<Persistable>();
		final Entity unchangedMember = context.mock(Entity.class, "unchangedMember");
		final Entity newMember = context.mock(Entity.class, "newMember");
		final Entity removedMember = context.mock(Entity.class, "removedMember");
		oldCollection.add(unchangedMember);
		oldCollection.add(removedMember);
		newCollection.add(unchangedMember);
		newCollection.add(newMember);
		
		final String fieldName = "changedCollection";

		final ChangeOperation operation = context.mock(ChangeOperation.class);
		auditEntityListener.setCurrentOperation(operation);
		
		JpaPersistenceEngineImpl jpaPersistenceEngine = new JpaPersistenceEngineImpl();
		auditEntityListener.setPersistenceEngine(jpaPersistenceEngine);
		
		final EntityManager entityManager = context.mock(EntityManager.class);
		jpaPersistenceEngine.setEntityManager(entityManager);
		
		context.checking(new Expectations() {
			{
				oneOf(entity).getUidPk(); will(returnValue(UIDPK));

				oneOf(entityManager).find(entity.getClass(), UIDPK); will(returnValue(oldEntity));
				oneOf(oldEntity).getField(); will(returnValue(oldCollection));
					
				oneOf(removedMember).getGuid(); will(returnValue("OLDGUID"));
				oneOf(newMember).getGuid(); will(returnValue("NEWGUID"));
				
				oneOf(auditDao).persistDataChanged(entity, fieldName, ChangeType.DELETE, "OLDGUID", null, operation);
				oneOf(auditDao).persistDataChanged(entity, fieldName, ChangeType.CREATE, null, "NEWGUID", operation);
			}
		});
		auditEntityListener.recordCollectionChanged(entity, fieldName, newCollection, ChangeType.UPDATE);
	}
	
	/**
	 * Test that a UPDATE change type records the old and new field values.
	 */
	@Test
	public void testRecordFieldChangedUpdate() {
		final Entity entity = context.mock(Entity.class, "new");
		final EntityWithField oldEntity = context.mock(EntityWithField.class, "old");
		final String field = "Update Field";
		final String oldField = "Old Field";
		
		final ChangeOperation operation = context.mock(ChangeOperation.class);
		
		auditEntityListener.setCurrentOperation(operation);
		
		JpaPersistenceEngineImpl jpaPersistenceEngine = new JpaPersistenceEngineImpl();
		auditEntityListener.setPersistenceEngine(jpaPersistenceEngine);
		
		final EntityManager entityManager = context.mock(EntityManager.class);
		jpaPersistenceEngine.setEntityManager(entityManager);
		
		context.checking(new Expectations() {
			{
				oneOf(entity).getUidPk(); will(returnValue(UIDPK));
					
				oneOf(entityManager).find(entity.getClass(), UIDPK); will(returnValue(oldEntity));
				
				oneOf(oldEntity).getField(); will(returnValue(oldField));
				
				oneOf(auditDao).persistDataChanged(entity, "updateField", ChangeType.UPDATE, oldField, field, operation);
			}
		});
		
		auditEntityListener.recordFieldChanged(entity, "updateField", field, ChangeType.UPDATE);
	}
	
	/**
	 * Test that creating a new collection records new references.
	 */
	@Test
	public void testRecordCollectionChangedCreate() {
		final Entity entity = context.mock(Entity.class);
		final Persistable member = context.mock(Persistable.class);
		final Set<Persistable> collection = new HashSet<Persistable>();
		collection.add(member);
		final long memberUid = 12345L;

		final ChangeOperation operation = context.mock(ChangeOperation.class);
		
		auditEntityListener.setCurrentOperation(operation);
		
		context.checking(new Expectations() {
			{
				oneOf(member).getUidPk(); will(returnValue(memberUid));
				
				oneOf(auditDao).persistDataChanged(entity, "newCollection", ChangeType.CREATE, null, String.valueOf(memberUid), operation);
			}
		});
		auditEntityListener.recordCollectionChanged(entity, "newCollection", collection, ChangeType.CREATE);
	}

	/**
	 * Test that creating a new collection records old references.
	 */
	@Test
	public void testRecordCollectionChangedDelete() {
		final Entity entity = context.mock(Entity.class);
		final Set<Persistable> collection = new HashSet<Persistable>();
		final Persistable member = context.mock(Persistable.class);
		collection.add(member);
		final long memberUid = 12345L;
		
		final ChangeOperation operation = context.mock(ChangeOperation.class);

		auditEntityListener.setCurrentOperation(operation);
		
		context.checking(new Expectations() {
			{
				oneOf(member).getUidPk(); will(returnValue(memberUid));
				
				oneOf(auditDao).persistDataChanged(entity, "deletedCollection", ChangeType.DELETE, String.valueOf(memberUid), null, operation);
			}
		});
		auditEntityListener.recordCollectionChanged(entity, "deletedCollection", collection, ChangeType.DELETE);
	}
	
	/**
	 * Test that creating a new map records the new key=value pair values.
	 */
	@Test
	public void testRecordMapChangedCreate() {
		final Entity entity = context.mock(Entity.class);
		final Persistable member = context.mock(Persistable.class);
		final Map<Object, Persistable> map = new HashMap<Object, Persistable>();
		map.put("key", member);
		final long memberUid = 12345L;

		final ChangeOperation operation = context.mock(ChangeOperation.class);

		auditEntityListener.setCurrentOperation(operation);
		
		context.checking(new Expectations() {
			{
				oneOf(member).getUidPk(); will(returnValue(memberUid));
				
				oneOf(auditDao).persistDataChanged(entity, "newMap", ChangeType.CREATE, null, "key=" + memberUid, operation);
			}
		});
		auditEntityListener.recordMapChanged(entity, "newMap", map, ChangeType.CREATE);
	}

	/**
	 * Test that deleting a map records the old key=value pair values. 
	 */
	@Test
	public void testRecordMapChangedDelete() {
		final Entity entity = context.mock(Entity.class);
		final Persistable member = context.mock(Persistable.class);
		final Map<Object, Persistable> map = new HashMap<Object, Persistable>();
		map.put("key", member);
		final long memberUid = 12345L;

		final ChangeOperation operation = context.mock(ChangeOperation.class);

		auditEntityListener.setCurrentOperation(operation);
		
		JpaPersistenceEngineImpl jpaPersistenceEngine = new JpaPersistenceEngineImpl();
		auditEntityListener.setPersistenceEngine(jpaPersistenceEngine);
		
		context.checking(new Expectations() {
			{
				oneOf(member).getUidPk(); will(returnValue(memberUid));
				
				oneOf(auditDao).persistDataChanged(entity, "deletedMap", ChangeType.DELETE, "key=" + memberUid, null, operation);
			}
		});
		auditEntityListener.recordMapChanged(entity, "deletedMap", map, ChangeType.DELETE);
	}
	
	/**
	 * Test that updating a map records the removed and added key=value pair values. 
	 */
	@Test
	public void testRecordMapChangedUpdate() {
		final Entity entity = context.mock(Entity.class, "new");
		final EntityWithField oldEntity = context.mock(EntityWithField.class, "old");
		final String fieldName = "updatedMap";
		
		final Entity oldMember = context.mock(Entity.class, "oldMember");
		final Entity orphanMember = context.mock(Entity.class, "orphanMember");
		final Entity newMember = context.mock(Entity.class, "newMember");
		final Entity unchangedMember = context.mock(Entity.class, "unchangedMember");
		final Entity adoptedMember = context.mock(Entity.class, "adoptedMember");
		
		final Map<Object, Persistable> oldMap = new HashMap<Object, Persistable>();
		oldMap.put("oldKey", oldMember);
		oldMap.put("unchangedKey", unchangedMember);
		oldMap.put("changedKey", orphanMember);
		
		final Map<Object, Persistable> newMap = new HashMap<Object, Persistable>();
		newMap.put("unchangedKey", unchangedMember);
		newMap.put("changedKey", adoptedMember);
		newMap.put("newKey", newMember);
		
		final ChangeOperation operation = context.mock(ChangeOperation.class);
		auditEntityListener.setCurrentOperation(operation);
		
		JpaPersistenceEngineImpl jpaPersistenceEngine = new JpaPersistenceEngineImpl();
		auditEntityListener.setPersistenceEngine(jpaPersistenceEngine);
		
		final EntityManager entityManager = context.mock(EntityManager.class);
		jpaPersistenceEngine.setEntityManager(entityManager);
		
		context.checking(new Expectations() {
			{
				oneOf(entity).getUidPk(); will(returnValue(UIDPK));
				oneOf(entityManager).find(entity.getClass(), UIDPK); will(returnValue(oldEntity));
				oneOf(oldEntity).getField(); will(returnValue(oldMap));
				
				oneOf(oldMember).getGuid(); will(returnValue("OLDGUID"));
				oneOf(orphanMember).getGuid(); will(returnValue("ORPHAN"));
				oneOf(newMember).getGuid(); will(returnValue("NEWGUID"));
				oneOf(adoptedMember).getGuid(); will(returnValue("ADOPTEE"));
				
				oneOf(auditDao).persistDataChanged(entity, fieldName, ChangeType.CREATE, null, "newKey=NEWGUID", operation);
				oneOf(auditDao).persistDataChanged(entity, fieldName, ChangeType.DELETE, "oldKey=OLDGUID", null, operation);
				oneOf(auditDao).persistDataChanged(entity, fieldName, ChangeType.UPDATE, "changedKey=ORPHAN", "changedKey=ADOPTEE", operation);
			}
		});
		auditEntityListener.recordMapChanged(entity, fieldName, newMap, ChangeType.UPDATE);
	}
	
	/**
	 * Test that beginning a change operation stores the operation details.
	 */
	@Test
	public void testBeginOperation() {
		final Entity entity = new ProductSkuImpl();  //Any entity object
		final Object transactionKey = new Object();
		final Transaction transaction = context.mock(Transaction.class);
		final ChangeTransaction changeTransaction = context.mock(ChangeTransaction.class);
		
		final PersistenceSession persistenceSession = context.mock(PersistenceSession.class);
		
		final JpaPersistenceEngineImpl jpaPersistenceEngine = context.mock(JpaPersistenceEngineImpl.class);
		auditEntityListener.setPersistenceEngine(jpaPersistenceEngine);
		
		List<String> auditableClasses = new ArrayList<String>();
		auditableClasses.add("com.elasticpath.domain.catalog.impl.ProductSkuImpl");
		auditEntityListener.setAuditableClasses(auditableClasses);
		
		final Broker broker = context.mock(Broker.class);
		final ManagedRuntime managedRuntime = context.mock(ManagedRuntime.class);
		
		try {
			context.checking(new Expectations() {
				{	
					oneOf(jpaPersistenceEngine).getPersistenceSession(); will(returnValue(persistenceSession));
					oneOf(persistenceSession).beginTransaction(); will(returnValue(transaction));
					oneOf(openJPAEntityManager).getFlushMode(); will(returnValue(FlushModeType.AUTO));
					oneOf(openJPAEntityManager).setFlushMode(FlushModeType.COMMIT);
					
					oneOf(auditDao).persistChangeSetTransaction(String.valueOf(transactionKey.hashCode()), entity, metadataMap); 
						will(returnValue(changeTransaction));
					oneOf(auditDao).persistSingleChangeOperation(entity, ChangeType.CREATE, changeTransaction, 1);
					
					oneOf(transaction).commit();
					
					allowing(jpaPersistenceEngine).getBroker(); will(returnValue(broker));
					allowing(broker).getManagedRuntime(); will(returnValue(managedRuntime));
					allowing(managedRuntime).getTransactionKey(); will(returnValue(transactionKey));
					oneOf(openJPAEntityManager).setFlushMode(FlushModeType.AUTO);
				}
			});
		} catch (Exception e) {
			fail("Mock method should not have thrown an exception: " + e);
		}
		auditEntityListener.beginSingleOperation(entity,
				ChangeType.CREATE);
	}
}
