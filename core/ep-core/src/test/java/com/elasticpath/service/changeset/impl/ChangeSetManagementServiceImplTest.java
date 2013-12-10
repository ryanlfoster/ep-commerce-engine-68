package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetMutator;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.changeset.impl.ChangeSetImpl;
import com.elasticpath.domain.changeset.impl.ChangeSetMemberImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectGroupMemberImpl;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectMetadataImpl;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.service.changeset.dao.ChangeSetDao;
import com.elasticpath.service.changeset.dao.ChangeSetMemberDao;
import com.elasticpath.service.changeset.helper.ChangeSetHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for {@link ChangeSetManagementServiceImpl}.
 */
public class ChangeSetManagementServiceImplTest {

	private static final String GROUP_ID = "groupId1";

	private ChangeSetManagementServiceImpl changeSetManagementService;
	private ChangeSetMemberDao changeSetMemberDao;
	private TimeService timeService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
    
	private ChangeSetDao changeSetDao;
	private BeanFactory beanFactory;

	private ChangeSetHelper changeSetHelper;

	private ChangeSetPolicy changeSetPolicy;

	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Sets up a test case.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		changeSetManagementService = new ChangeSetManagementServiceImpl();
		changeSetDao = context.mock(ChangeSetDao.class);
		timeService = context.mock(TimeService.class);
		changeSetMemberDao = context.mock(ChangeSetMemberDao.class);
		changeSetHelper = context.mock(ChangeSetHelper.class);
		changeSetPolicy = context.mock(ChangeSetPolicy.class);
		
		changeSetManagementService.setChangeSetDao(changeSetDao);
		changeSetManagementService.setTimeService(timeService);
		changeSetManagementService.setChangeSetMemberDao(changeSetMemberDao);
		changeSetManagementService.setChangeSetHelper(changeSetHelper);
		changeSetManagementService.setChangeSetPolicy(changeSetPolicy); 
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that adding a change set involves generating a group ID 
	 * and adding the change set to the data store using the corresponding DAO.
	 */
	@Test
	public void testAddChangeSet() {
		final String groupId = "groupId1";
		final ChangeSet changeSet = new ChangeSetImpl();
		context.checking(new Expectations() { {

		    oneOf(timeService).getCurrentTime();
		    will(returnValue(new Date()));
		    
		    oneOf(changeSetMemberDao).generateChangeSetGroupId();
		    will(returnValue(groupId));
		    
		    oneOf(changeSetDao).add(changeSet);

		} });
		
		// set the mandatory creator
		changeSet.setCreatedByUserGuid("sampleGUID");
		
		// invoke the method to add
		changeSetManagementService.add(changeSet);
		assertEquals(groupId, changeSet.getObjectGroupId());
		
	}
	
	/**
	 * Tests that when removing a change set we call the DAO to do so and then
	 * we remove all the object group members.
	 */
	@Test
	public void testRemoveChangeSet() {
		final String objectGroupId = "sampleGroupId";
		
		context.checking(new Expectations() { {
			
		    oneOf(changeSetDao).remove(objectGroupId);
		    
		    oneOf(changeSetPolicy).canRemove(objectGroupId);
		    will(returnValue(true));
		} });
		
		changeSetManagementService.remove(objectGroupId);

	}	
	
	/**
	 * Tests that update() populates the change set with member objects.
	 */
	@Test
	public void testUpdateChangeSet() {
		final ChangeSet changeSet = new ChangeSetImpl();
		((ChangeSetMutator) changeSet).setObjectGroupId(GROUP_ID);
		
		final Collection<BusinessObjectMetadata> metadataCollection = new HashSet<BusinessObjectMetadata>();
		
		final BusinessObjectGroupMember member1 = new BusinessObjectGroupMemberImpl();
		
		final BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		metadata.setBusinessObjectGroupMember(member1);
		
		metadataCollection.add(metadata);
		
		context.checking(new Expectations() { {
			oneOf(changeSetPolicy).isChangeAllowed(GROUP_ID);
			will(returnValue(true));

		    oneOf(changeSetDao).update(changeSet);
		    will(returnValue(changeSet));

		    oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupId(GROUP_ID);
		    will(returnValue(metadataCollection));

		    oneOf(changeSetHelper).convertGroupMembersToChangeSetMembers(Arrays.asList(member1), metadataCollection);
		    will(returnValue(Arrays.asList(new ChangeSetMemberImpl())));
		    
		    oneOf(changeSetMemberDao).findGroupMembersByGroupId(GROUP_ID);
		    will(returnValue(Arrays.asList(member1)));
		} });
	
		final ChangeSet updatedChangeSet = changeSetManagementService.update(changeSet, null);
		
		assertEquals(1, updatedChangeSet.getMemberObjects().size());

	}
	
	/**
	 * Tests that get() populates the change set with member objects.
	 */
	@Test
	public void testGetChangeSet() {
		final ChangeSet changeSet = new ChangeSetImpl();
		((ChangeSetMutator) changeSet).setObjectGroupId(GROUP_ID);
		
		final Collection<BusinessObjectMetadata> metadataCollection = new HashSet<BusinessObjectMetadata>();
		
		final BusinessObjectGroupMember member1 = new BusinessObjectGroupMemberImpl();
		
		BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		metadata.setBusinessObjectGroupMember(member1);
		
		metadataCollection.add(metadata);
		
		context.checking(new Expectations() { {		    
		    oneOf(changeSetDao).findByGuid(GROUP_ID);
		    will(returnValue(changeSet));

		    oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupId(GROUP_ID);
		    will(returnValue(metadataCollection));

		    oneOf(changeSetHelper).convertGroupMembersToChangeSetMembers(Arrays.asList(member1), metadataCollection);
		    will(returnValue(Arrays.asList(new ChangeSetMemberImpl())));
		    
		    oneOf(changeSetMemberDao).findGroupMembersByGroupId(GROUP_ID);
		    will(returnValue(Arrays.asList(member1)));
		} }); 
	
		final ChangeSet changeSet1 = changeSetManagementService.get(GROUP_ID, null);
		
		assertEquals(1, changeSet1.getMemberObjects().size());

	}
	
	/**
	 * Tests find all change sets.
	 */
	@Test
	public void testFindAllChangeSets() {
		
		context.checking(new Expectations() { {		    
		    oneOf(changeSetDao).findAllChangeSets();
		    will(returnValue(Collections.emptyList()));
		} });
		
		changeSetManagementService.findAllChangeSets(null);
	}
	
	/**
	 * Tests find all change sets by user Guid.
	 */
	@Test
	public void testFindAllChangeSetsByUserGuid() {
		final String userGuid = "userGuid";
		
		context.checking(new Expectations() { {		    
		    oneOf(changeSetDao).findAllChangeSetsByUserGuid(userGuid);
		    will(returnValue(Collections.emptyList()));
		} });
		
		changeSetManagementService.findAllChangeSetsByUserGuid(userGuid, null);
	}
	
	/**
	 * Tests that if a change set GUID does not point to a real change set an exception will be thrown.
	 */
	@Test(expected = EpServiceException.class)
	public void testUpdateStateWithNonExistingGuid() {
		final String changeSetGuid = "changeSetGuid";
		
		context.checking(new Expectations() { {		    
		    oneOf(changeSetDao).findByGuid(changeSetGuid);
		    will(returnValue(null));
		} });
		
		changeSetManagementService.updateState(changeSetGuid, ChangeSetStateCode.LOCKED, null);
		
	}

	/**
	 * Tests that if a change set will be updated when updateState() is invoked.
	 */
	@Test
	public void testUpdateState() {
		final String changeSetGuid = "changeSetGuid";
		final ChangeSetImpl changeSet = context.mock(ChangeSetImpl.class);
		
		context.checking(new Expectations() { {
			oneOf(changeSet).setStateCode(ChangeSetStateCode.LOCKED);
			
			allowing(changeSet).getGuid();
			will(returnValue(changeSetGuid));
			
			oneOf(changeSet).setMemberObjects(Collections.<ChangeSetMember>emptyList());
			
		    oneOf(changeSetDao).findByGuid(changeSetGuid);
		    will(returnValue(changeSet));

		    oneOf(changeSetDao).update(changeSet);
		    will(returnValue(changeSet));
		    
		    oneOf(changeSetMemberDao).findGroupMembersByGroupId(changeSetGuid);
		    will(returnValue(Collections.emptyList()));
		    
		    oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupId(changeSetGuid);
		    will(returnValue(Collections.emptyList()));

		    oneOf(changeSetHelper).convertGroupMembersToChangeSetMembers(
					Collections.<BusinessObjectGroupMember>emptyList(),
					Collections.<BusinessObjectMetadata>emptyList());
		    will(returnValue(Arrays.<BusinessObjectGroupMember>asList()));

		} });
		
		changeSetManagementService.updateState(changeSetGuid, ChangeSetStateCode.LOCKED, null);

	}

	/**
	 * Tests that if the arguments are not specified an {@link IllegalArgumentException} will be thrown.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateStateNullArguments() {
		changeSetManagementService.updateState(null, null, null);
	}
	
}
