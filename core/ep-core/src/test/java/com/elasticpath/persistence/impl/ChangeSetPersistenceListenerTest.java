package com.elasticpath.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.FlushModeType;

import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.enhance.StateManager;
import org.apache.openjpa.event.LifecycleEvent;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectGroupMemberImpl;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectMetadataImpl;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.changeset.dao.ChangeSetMemberDao;
import com.elasticpath.service.changeset.helper.ChangeSetHelper;
import com.elasticpath.service.objectgroup.dao.BusinessObjectGroupDao;

/**
 * Test for the {@code ChangeSetPersistenceListener}.
 */
public class ChangeSetPersistenceListenerTest {
	private static final String ADD_TO_CHANGE_SET_FLAG = "addToChangeSetFlag";
	private static final String STAGE2 = "stage2";
	private static final String ACTIVE_IMPORT_STAGE = "activeImportStage";
	private static final String TEST = "test";
	private static final String ACTION = "action";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private final BusinessObjectDescriptor businessObjectDescriptor = new BusinessObjectDescriptorImpl();
	private final ChangeSetMemberDao changeSetMemberDao = context.mock(ChangeSetMemberDao.class);
	private final OpenJPAEntityManager openJPAEntityManager = context.mock(OpenJPAEntityManager.class);
	private final ChangeSetHelper changeSetHelper = context.mock(ChangeSetHelper.class);
	private ChangeSetPersistenceListenerTestDouble listener;

	@SuppressWarnings("unchecked")
	private final ThreadLocalMap<String, Object> threadLocalMap = context.mock(ThreadLocalMap.class);

	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() {
		listener = new ChangeSetPersistenceListenerTestDouble();
		listener.setMetadataMap(threadLocalMap);
	}

	/**
	 * A test double for the listener. This returns the common mocks we require.
	 */
	class ChangeSetPersistenceListenerTestDouble extends ChangeSetPersistenceListener {
		@Override
		protected BusinessObjectDescriptor getBusinessObjectDescriptor(final Object source) {
			return businessObjectDescriptor;
		}

		@Override
		protected ChangeSetMemberDao getChangeSetMemberDao() {
			return changeSetMemberDao;
		}

		@Override
		protected OpenJPAEntityManager getOpenJPAEntityManager() {
			return openJPAEntityManager;
		}

		@Override
		protected String getChangeSetGuid() {
			return "test";
		}

		@Override
		protected ThreadLocalMap<String, Object> getMetadataMap() {
			return threadLocalMap;
		}

		@Override
		public ChangeSetHelper getChangeSetHelper() {
			return changeSetHelper;
		}
	}
	/**
	 * Tests that when the beforePersist event is triggered and metadata does not already exist that
	 * the ChangeSetMemberDaoService is called with a correct metadata object.
	 */
	@Test
	public void testPersistCreate() {

		final BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		final BusinessObjectGroupMember businessObjectGroupMember = new BusinessObjectGroupMemberImpl();
		metadata.setBusinessObjectGroupMember(businessObjectGroupMember);
		ChangeSetPersistenceListener listener = new ChangeSetPersistenceListenerTestDouble() {
			@Override
			protected BusinessObjectMetadata createAndPopulateMetadata(final String keyAction,
					final ChangeType changeType, final BusinessObjectDescriptor objectDescriptor) {
				// This method is here so that we can verify that the dao is called with the return.
				if (ACTION.equals(keyAction) && changeType.equals(ChangeType.CREATE) && objectDescriptor.equals(businessObjectDescriptor)) {
					return metadata;
				}
				return null;
			}
		};
		listener.setMetadataMap(threadLocalMap);

		context.checking(new Expectations() { {
			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.COMMIT);
			oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupIdAndMetadataKey(TEST, ACTION);
			will(returnValue(Collections.emptyList()));

			oneOf(changeSetMemberDao).addOrUpdateObjectMetadata(metadata);

			oneOf(threadLocalMap).get(ACTIVE_IMPORT_STAGE);
			will(returnValue(STAGE2));

			oneOf(threadLocalMap).containsKey(ADD_TO_CHANGE_SET_FLAG);
			will(returnValue(false));

			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.AUTO);
		} });

		// Persisted object just to have something to work with.
		Product product = new ProductImpl();

		LifecycleEvent event = new LifecycleEvent(product, LifecycleEvent.BEFORE_PERSIST);
		listener.eventOccurred(event);

	}

	/**
	 * Tests that when the beforePersist event is triggered and metadata already exists that
	 * the ChangeSetMemberDaoService is called with a correct metadata object.
	 */
	@Test
	public void testPersistUpdate() {

		final BusinessObjectDescriptor businessObjectDescriptor = new BusinessObjectDescriptorImpl();
		final BusinessObjectGroupMember businessObjectGroupMember = new BusinessObjectGroupMemberImpl();

		// Using a mock so we can expect the set.
		final BusinessObjectMetadata metadata = context.mock(BusinessObjectMetadata.class);

		final List<BusinessObjectMetadata> metadataList = new ArrayList<BusinessObjectMetadata>();
		metadataList.add(metadata);

		context.checking(new Expectations() { {
			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.COMMIT);
			allowing(metadata).getMetadataKey(); will(returnValue(ACTION));
			allowing(metadata).getBusinessObjectGroupMember(); will(returnValue(businessObjectGroupMember));
			oneOf(metadata).setMetadataValue(ChangeSetMemberAction.ADD.getName());

			oneOf(metadata).getMetadataValue();
			will(returnValue(ChangeSetMemberAction.UNDEFINED.getName()));

			oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupIdAndMetadataKey(TEST, ACTION);
			will(returnValue(metadataList));

			oneOf(changeSetHelper).convertGroupMemberToDescriptor(businessObjectGroupMember);
			will(returnValue(businessObjectDescriptor));

			oneOf(changeSetMemberDao).addOrUpdateObjectMetadata(metadata);

			oneOf(threadLocalMap).get(ACTIVE_IMPORT_STAGE);
			will(returnValue(STAGE2));

			oneOf(threadLocalMap).containsKey(ADD_TO_CHANGE_SET_FLAG);
			will(returnValue(false));

			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.AUTO);
		} });

		// Persisted object just to have something to work with.
		Product product = new ProductImpl();

		LifecycleEvent event = new LifecycleEvent(product, LifecycleEvent.BEFORE_PERSIST);
		listener.eventOccurred(event);

	}

	/**
	 * Tests that when the beforeStore event is triggered, the source is persistent and metadata does not already exist that
	 * the ChangeSetMemberDaoService is called with a correct metadata object.
	 */
	@Test
	public void testStoreCreate() {

		final BusinessObjectDescriptor businessObjectDescriptor = new BusinessObjectDescriptorImpl();
		final BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		final BusinessObjectGroupMember businessObjectGroupMember = new BusinessObjectGroupMemberImpl();
		metadata.setBusinessObjectGroupMember(businessObjectGroupMember);

		ChangeSetPersistenceListener listener = new ChangeSetPersistenceListenerTestDouble() {

			@Override
			protected BusinessObjectMetadata createAndPopulateMetadata(final String keyAction,
					final ChangeType changeType, final BusinessObjectDescriptor objectDescriptor) {
				// This method is here so that we can verify that the dao is called with the return.
				if (ACTION.equals(keyAction) && changeType.equals(ChangeType.UPDATE) && objectDescriptor.equals(businessObjectDescriptor)) {
					return metadata;
				}
				return null;
			}
		};

		final PersistenceCapable object = context.mock(PersistenceCapable.class);
		final StateManager stateManager = context.mock(StateManager.class);

		context.checking(new Expectations() { {
			oneOf(object).pcGetStateManager(); will(returnValue(stateManager));
			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.COMMIT);

			oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupIdAndMetadataKey(TEST, ACTION);
			will(returnValue(Collections.emptyList()));

			oneOf(changeSetMemberDao).addOrUpdateObjectMetadata(metadata);

			oneOf(threadLocalMap).get(ACTIVE_IMPORT_STAGE);
			will(returnValue(STAGE2));

			oneOf(threadLocalMap).containsKey(ADD_TO_CHANGE_SET_FLAG);
			will(returnValue(false));

			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.AUTO);
		} });

		LifecycleEvent event = new LifecycleEvent(object, LifecycleEvent.BEFORE_STORE);
		listener.eventOccurred(event);

	}

	/**
	 * Tests that when the beforeAttach event is triggered, the source is persistent and metadata already exists that
	 * the ChangeSetMemberDaoService is called with a correct metadata object.
	 */
	@Test
	public void testStoreUpdate() {

		final BusinessObjectDescriptor businessObjectDescriptor = new BusinessObjectDescriptorImpl();
		final BusinessObjectGroupMember businessObjectGroupMember = new BusinessObjectGroupMemberImpl();

		// Using a mock so we can expect the set.
		final BusinessObjectMetadata metadata = context.mock(BusinessObjectMetadata.class);

		final List<BusinessObjectMetadata> metadataList = new ArrayList<BusinessObjectMetadata>();
		metadataList.add(metadata);

		final PersistenceCapable object = context.mock(PersistenceCapable.class);
		final StateManager stateManager = context.mock(StateManager.class);

		context.checking(new Expectations() { {
			oneOf(object).pcGetStateManager(); will(returnValue(stateManager));
			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.COMMIT);
			allowing(metadata).getMetadataKey(); will(returnValue(ACTION));
			allowing(metadata).getBusinessObjectGroupMember(); will(returnValue(businessObjectGroupMember));
			oneOf(metadata).setMetadataValue(ChangeSetMemberAction.EDIT.getName());

			oneOf(metadata).getMetadataValue();
			will(returnValue(ChangeSetMemberAction.UNDEFINED.getName()));

			oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupIdAndMetadataKey(TEST, ACTION);
			will(returnValue(metadataList));

			oneOf(changeSetHelper).convertGroupMemberToDescriptor(businessObjectGroupMember);
			will(returnValue(businessObjectDescriptor));

			oneOf(changeSetMemberDao).addOrUpdateObjectMetadata(metadata);

			oneOf(threadLocalMap).get(ACTIVE_IMPORT_STAGE);
			will(returnValue(STAGE2));

			oneOf(threadLocalMap).containsKey(ADD_TO_CHANGE_SET_FLAG);
			will(returnValue(false));

			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.AUTO);
		} });

		LifecycleEvent event = new LifecycleEvent(object, LifecycleEvent.BEFORE_STORE);
		listener.eventOccurred(event);

	}

	/**
	 * Tests that when the beforeDelete event is triggered and metadata does not already exist that
	 * the ChangeSetMemberDaoService is called with a correct metadata object.
	 */
	@Test
	public void testDeleteCreate() {

		final BusinessObjectDescriptor businessObjectDescriptor = new BusinessObjectDescriptorImpl();
		final BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		final BusinessObjectGroupMember businessObjectGroupMember = new BusinessObjectGroupMemberImpl();
		metadata.setBusinessObjectGroupMember(businessObjectGroupMember);

		ChangeSetPersistenceListener listener = new ChangeSetPersistenceListenerTestDouble() {

			@Override
			protected BusinessObjectMetadata createAndPopulateMetadata(final String keyAction,
					final ChangeType changeType, final BusinessObjectDescriptor objectDescriptor) {
				// This method is here so that we can verify that the dao is called with the return.
				if (ACTION.equals(keyAction) && changeType.equals(ChangeType.DELETE) && objectDescriptor.equals(businessObjectDescriptor)) {
					return metadata;
				}
				return null;
			}
		};


		context.checking(new Expectations() { {
			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.COMMIT);

			oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupIdAndMetadataKey(TEST, ACTION);
			will(returnValue(Collections.emptyList()));

			oneOf(changeSetMemberDao).addOrUpdateObjectMetadata(metadata);

			oneOf(threadLocalMap).get(ACTIVE_IMPORT_STAGE);
			will(returnValue(STAGE2));

			oneOf(threadLocalMap).containsKey(ADD_TO_CHANGE_SET_FLAG);
			will(returnValue(false));

			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.AUTO);
		} });

		// Persisted object just to have something to work with.
		Product product = new ProductImpl();

		LifecycleEvent event = new LifecycleEvent(product, LifecycleEvent.BEFORE_DELETE);
		listener.eventOccurred(event);

	}

	/**
	 * Tests that when the beforeDelete event is triggered and metadata already exists that
	 * the ChangeSetMemberDaoService is called with a correct metadata object.
	 */
	@Test
	public void testDeleteUpdate() {

		final BusinessObjectDescriptor businessObjectDescriptor = new BusinessObjectDescriptorImpl();

		// Using a mock so we can expect the set.
		final BusinessObjectMetadata metadata = context.mock(BusinessObjectMetadata.class);
		final BusinessObjectGroupMember businessObjectGroupMember = new BusinessObjectGroupMemberImpl();

		final List<BusinessObjectMetadata> metadataList = new ArrayList<BusinessObjectMetadata>();
		metadataList.add(metadata);

		context.checking(new Expectations() { {
			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.COMMIT);
			allowing(metadata).getMetadataKey(); will(returnValue(ACTION));
			allowing(metadata).getBusinessObjectGroupMember(); will(returnValue(businessObjectGroupMember));
			oneOf(metadata).setMetadataValue(ChangeSetMemberAction.DELETE.getName());

			oneOf(metadata).getMetadataValue();
			will(returnValue(ChangeSetMemberAction.UNDEFINED.getName()));

			oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupIdAndMetadataKey(TEST, ACTION);
			will(returnValue(metadataList));

			oneOf(changeSetHelper).convertGroupMemberToDescriptor(businessObjectGroupMember); will(returnValue(businessObjectDescriptor));

			oneOf(changeSetMemberDao).addOrUpdateObjectMetadata(metadata);

			oneOf(threadLocalMap).get(ACTIVE_IMPORT_STAGE);
			will(returnValue(STAGE2));

			oneOf(threadLocalMap).containsKey(ADD_TO_CHANGE_SET_FLAG);
			will(returnValue(false));

			oneOf(openJPAEntityManager).setFlushMode(FlushModeType.AUTO);
		} });

		// Persisted object just to have something to work with.
		Product product = new ProductImpl();

		LifecycleEvent event = new LifecycleEvent(product, LifecycleEvent.BEFORE_DELETE);
		listener.eventOccurred(event);

	}

	/**
	 * Tests that the createAndPopulateMetadata method creates the metadata object and populates it with the parameters.
	 */
	@Test
	public void testCreateAndPopulateMetadata() {
		final BusinessObjectGroupDao businessObjectGroupDao = context.mock(BusinessObjectGroupDao.class);
		ChangeSetPersistenceListener listener = new ChangeSetPersistenceListenerTestDouble() {
			@Override
			protected BusinessObjectGroupDao getBusinessObjectGroupDao() {
				return businessObjectGroupDao;
			}
		};

		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		final BusinessObjectMetadata metadata = context.mock(BusinessObjectMetadata.class);

		final BusinessObjectGroupMember businessObjectGroupMember = new BusinessObjectGroupMemberImpl();
		final BusinessObjectDescriptor businessObjectDescriptor = new BusinessObjectDescriptorImpl();

		context.checking(new Expectations() { {
			oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_METADATA); will(returnValue(metadata));
			oneOf(metadata).setMetadataKey(ACTION);
			oneOf(metadata).setMetadataValue("DELETE");
			allowing(businessObjectGroupDao)
						.findGroupMemberByObjectDescriptor(
								businessObjectDescriptor);
				will(returnValue(businessObjectGroupMember));
			oneOf(metadata).setBusinessObjectGroupMember(businessObjectGroupMember);
		} });

		listener.setBeanFactory(beanFactory);

		ChangeType changeType = ChangeType.DELETE;
		BusinessObjectMetadata actualMetadata = listener.createAndPopulateMetadata(ACTION, changeType, businessObjectDescriptor);

		assertEquals("Expect to see the object we created", metadata, actualMetadata);

	}

	/**
	 * Tests that when the beforePersist event is triggered but the object is not for a change set.
	 * This behaviour is expected for the other events but only tested for this one.
	 */
	@Test
	public void testPersistNotForChangeSet() {

		ChangeSetPersistenceListener listener = new ChangeSetPersistenceListener() {
			@Override
			protected BusinessObjectDescriptor getBusinessObjectDescriptor(final Object source) {
				return null;
			}
		};
		ThreadLocalMap<String, Object> threadLocalMap = new ThreadLocalMap<String, Object>();
		threadLocalMap.put("currentChangeSet", null);

		listener.setMetadataMap(threadLocalMap);
		// Persisted object just to have something to work with.
		Product product = new ProductImpl();

		LifecycleEvent event = new LifecycleEvent(product, LifecycleEvent.BEFORE_PERSIST);
		listener.eventOccurred(event);

		// If anything is called then this will fail.
	}

	/**
	 * Tests that having stage2 active makes the listener active.
	 */
	@Test
	public void testIsActive() {
		context.checking(new Expectations() { {
			oneOf(threadLocalMap).get(ACTIVE_IMPORT_STAGE);
			will(returnValue(STAGE2));
		} });
		assertTrue("Stage2 activates the listener", listener.isListenerActive());
	}

	/**
	 * Tests that resolving the action works in favour of the action with higher priority.
	 * The following use cases are considered as impossible and not tested: <br>
	 * | current state | new state | <br>
	 * | DELETE        | EDIT      | <br>
	 * | EDIT          | ADD       | <br>
	 */
	@Test
	public void testActionResolution() {

		checkResolution(ChangeSetMemberAction.ADD, ChangeSetMemberAction.ADD, ChangeSetMemberAction.ADD);

		checkResolution(ChangeSetMemberAction.DELETE, ChangeSetMemberAction.ADD, ChangeSetMemberAction.ADD);
		checkResolution(ChangeSetMemberAction.EDIT, ChangeSetMemberAction.DELETE, ChangeSetMemberAction.DELETE);
		checkResolution(ChangeSetMemberAction.ADD, ChangeSetMemberAction.EDIT, ChangeSetMemberAction.ADD);
		checkResolution(ChangeSetMemberAction.ADD, ChangeSetMemberAction.DELETE, ChangeSetMemberAction.DELETE);
	}

	/**
	 *
	 */
	private void checkResolution(final ChangeSetMemberAction currentAction,
			final ChangeSetMemberAction newAction, final ChangeSetMemberAction expectation) {
		ChangeSetMemberAction result = listener.resolveAction(currentAction, newAction);
		assertEquals(expectation, result);
	}
}
