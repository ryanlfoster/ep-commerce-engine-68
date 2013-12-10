/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.changeset.impl.ChangeSetImpl;
import com.elasticpath.domain.changeset.impl.ChangeSetObjectStatusImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectGroupMemberImpl;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectMetadataImpl;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.service.changeset.ChangeSetPolicyException;
import com.elasticpath.service.changeset.dao.ChangeSetDao;
import com.elasticpath.service.changeset.dao.ChangeSetMemberDao;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests for {@link com.elasticpath.service.changeset.impl.ChangeSetServiceImpl}.
 */
public class ChangeSetServiceImplTest {
	private static final String CHANGE_SET_GUID = "changeSetId1";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ChangeSetServiceImpl changeSetService;
	private ChangeSetMemberDao changeSetMemberDao;
	private ChangeSetDao changeSetDao;

	private BeanFactory beanFactory;
	private ChangeSetPolicy changeSetPolicy;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 *
	 * @throws java.lang.Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		changeSetService = new ChangeSetServiceImpl();

		beanFactory = context.mock(BeanFactory.class);
		changeSetPolicy = context.mock(ChangeSetPolicy.class);
		changeSetMemberDao = context.mock(ChangeSetMemberDao.class);
		changeSetDao = context.mock(ChangeSetDao.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		changeSetService.setChangeSetPolicy(changeSetPolicy);
		changeSetService.setChangeSetMemberDao(changeSetMemberDao);
		changeSetService.setChangeSetDao(changeSetDao);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests updateResolvedMetadata method.
	 * 
	 */
	@Test
	public void testUpdateResolvedMetadata() {
		final String changeSetGuid = "changeSetGuid";
		final Collection<BusinessObjectGroupMember> groupMembers = new ArrayList<BusinessObjectGroupMember>();
		
		BusinessObjectGroupMember businessObjectGroupMember1 = new BusinessObjectGroupMemberImpl();
		businessObjectGroupMember1.setObjectIdentifier("100000");
		businessObjectGroupMember1.setObjectType("Product");
		groupMembers.add(businessObjectGroupMember1);
		
		BusinessObjectGroupMember businessObjectGroupMember2 = new BusinessObjectGroupMemberImpl();
		businessObjectGroupMember2.setObjectIdentifier("100001");
		businessObjectGroupMember2.setObjectType("Product");
		groupMembers.add(businessObjectGroupMember2);

		final Map<String, String> map = new HashMap<String, String>();
		map.put("objectName", "test1");

		context.checking(new Expectations() { {
			
			oneOf(changeSetMemberDao).findGroupMembersByGroupId(changeSetGuid);
			will(returnValue(groupMembers));
			
			//process the first business object member
			oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
			will(returnValue(new BusinessObjectDescriptorImpl()));
			
			oneOf(changeSetPolicy).resolveMetaData(with(any(BusinessObjectDescriptor.class)));
			will(returnValue(new HashMap<String, String>()));

			//process the second business object member
			oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
			will(returnValue(new BusinessObjectDescriptorImpl()));
			
			oneOf(changeSetPolicy).resolveMetaData(with(any(BusinessObjectDescriptor.class)));
			will(returnValue(map));
			
			oneOf(changeSetMemberDao).findBusinessObjectMetadataByDescriptor(with(any(BusinessObjectDescriptor.class)));
			will(returnValue(new ArrayList<BusinessObjectMetadata>()));

			oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_METADATA);
			will(returnValue(new BusinessObjectMetadataImpl()));
			
			oneOf(changeSetMemberDao).addOrUpdateObjectMetadata(with(any(BusinessObjectMetadata.class)));
			will(returnValue(new BusinessObjectMetadataImpl()));
		} });

		changeSetService.updateResolvedMetadata(changeSetGuid);
	}	
	
	/**
	 * Tests that adding a change set object involves adding a group member.
	 * Use a false resolveMetadata parameter to changeSetService.addObjectToChangeSet
	 * and make sure there is no call to changeSetPolice.resolveMetadata(...)
	 */
	@Test
	public void testAddObjectToChangeSetWithMetadataAndMetadataFlagFalse() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final BusinessObjectGroupMemberImpl groupMember = new BusinessObjectGroupMemberImpl();
		final BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		
		Map<String, String> objectMetadataDto = createObjectMetadata();
		
		context.checking(new Expectations() { {

			oneOf(changeSetPolicy).isChangeAllowed(CHANGE_SET_GUID);
			will(returnValue(true));

		    oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_GROUP_MEMBER);
		    will(returnValue(groupMember));

		    oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_METADATA);
		    will(returnValue(metadata));

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(new ChangeSetObjectStatusImpl()));

		    oneOf(changeSetMemberDao).add(groupMember, Arrays.asList(metadata));

		    oneOf(changeSetPolicy).getObjectMembershipGuids(objectDescriptor);
		    will(returnValue(Collections.emptyList()));
		    
		    oneOf(changeSetDao).findByGuid(CHANGE_SET_GUID);
		    will(returnValue(new ChangeSetImpl()));
		} });

		changeSetService.addObjectToChangeSet(CHANGE_SET_GUID, objectDescriptor, objectMetadataDto, false);
		
		assertEquals("Object should be added to change set with expected GUID.", CHANGE_SET_GUID, groupMember.getGroupId());
	}

	/**
	 *
	 * @return
	 */
	private Map<String, String> createObjectMetadata() {
		Map<String, String> objectMetadataDto = new HashMap<String, String>();
		objectMetadataDto.put("userId", "user_1");
		return objectMetadataDto;
	}	
	
	/**
	 * Tests that adding a change set object with no metadata processing.
	 * Use a false resolveMetadata parameter to changeSetService.addObjectToChangeSet
	 * and make sure there is no call to changeSetPolice.resolveMetadata(...)
	 */
	@Test
	public void testAddObjectToChangeAndNoMetadataIsInvoked() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final BusinessObjectGroupMemberImpl groupMember = new BusinessObjectGroupMemberImpl();
		final BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		
		Map<String, String> objectMetadataDto = createObjectMetadata();
		
		context.checking(new Expectations() { {

			oneOf(changeSetPolicy).isChangeAllowed(CHANGE_SET_GUID);
			will(returnValue(true));

		    oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_GROUP_MEMBER);
		    will(returnValue(groupMember));

		    oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_METADATA);
		    will(returnValue(metadata));

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(new ChangeSetObjectStatusImpl()));

		    oneOf(changeSetMemberDao).add(groupMember, Arrays.asList(metadata));

		    oneOf(changeSetPolicy).getObjectMembershipGuids(objectDescriptor);
		    will(returnValue(Collections.emptyList()));
		    
		    oneOf(changeSetDao).findByGuid(CHANGE_SET_GUID);
		    will(returnValue(new ChangeSetImpl()));

		    // resolveMetaData must never be called.
		    never(changeSetPolicy).resolveMetaData(objectDescriptor);
		} });

		changeSetService.addObjectToChangeSet(CHANGE_SET_GUID, objectDescriptor, objectMetadataDto, false);
		
		assertEquals("Object should be added to change set with expected GUID.", CHANGE_SET_GUID, groupMember.getGroupId());
	}	
	
	/**
	 * Tests that adding a change set object involves adding a group member.
	 */
	@Test
	public void testAddObjectToChangeSetWithAllRequiredParams() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final String changeSetGuid = "changeSetId2";
		final BusinessObjectGroupMemberImpl groupMember = new BusinessObjectGroupMemberImpl();
		
		context.checking(new Expectations() { {

			oneOf(changeSetPolicy).isChangeAllowed(changeSetGuid);
			will(returnValue(true));

		    oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_GROUP_MEMBER);
		    will(returnValue(groupMember));

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(new ChangeSetObjectStatusImpl()));

		    List<BusinessObjectMetadata> noMetadata = Collections.emptyList();
			oneOf(changeSetMemberDao).add(groupMember, noMetadata);
		    
		    oneOf(changeSetPolicy).getObjectMembershipGuids(objectDescriptor);
		    will(returnValue(Collections.emptyList()));
		    
		    oneOf(changeSetDao).findByGuid(changeSetGuid);
		    will(returnValue(new ChangeSetImpl()));
		    
		    oneOf(changeSetPolicy).resolveMetaData(objectDescriptor);
		    will(returnValue(Collections.emptyMap()));
		} });

		changeSetService.addObjectToChangeSet(changeSetGuid, objectDescriptor, null);
		
		assertEquals("Object should be added to change set with correct GUID.", changeSetGuid, groupMember.getGroupId());
	}
	
	/**
	 * Tests that adding a change set object involves adding a group member.
	 */
	@Test
	public void testAddObjectToChangeSetWithMetadata() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final BusinessObjectGroupMemberImpl groupMember = new BusinessObjectGroupMemberImpl();
		final BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		
		Map<String, String> objectMetadataDto = createObjectMetadata();
		
		context.checking(new Expectations() { {

			oneOf(changeSetPolicy).isChangeAllowed(CHANGE_SET_GUID);
			will(returnValue(true));

		    oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_GROUP_MEMBER);
		    will(returnValue(groupMember));

		    oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_METADATA);
		    will(returnValue(metadata));

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(new ChangeSetObjectStatusImpl()));

		    oneOf(changeSetMemberDao).add(groupMember, Arrays.asList(metadata));

		    oneOf(changeSetPolicy).getObjectMembershipGuids(objectDescriptor);
		    will(returnValue(Collections.emptyList()));
		    
		    oneOf(changeSetDao).findByGuid(CHANGE_SET_GUID);
		    will(returnValue(new ChangeSetImpl()));

		    oneOf(changeSetPolicy).resolveMetaData(objectDescriptor);
		    will(returnValue(Collections.emptyMap()));
		} });

		changeSetService.addObjectToChangeSet(CHANGE_SET_GUID, objectDescriptor, objectMetadataDto);
		
		assertEquals("Object should be added to change set with correct GUID.", CHANGE_SET_GUID, groupMember.getGroupId());
		assertEquals("Metadata key should have been set.", "userId", metadata.getMetadataKey());
		assertEquals("Metadata key should have been set.", "user_1", metadata.getMetadataValue());
	}

	/**
	 * Tests that adding a change set object with wrong parameters throws an exception.
	 */
	@Test
	public void testAddObjectToChangeSetWithParamsMissing() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		
		context.checking(new Expectations() { {

		    oneOf(changeSetDao).findByGuid(CHANGE_SET_GUID);
		    will(returnValue(new ChangeSetImpl()));
		} });
		
		try {
			changeSetService.addObjectToChangeSet(CHANGE_SET_GUID, null, null);
			fail("Expected exception not thrown");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
		
		try {
			changeSetService.addObjectToChangeSet(null, objectDescriptor, null);
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
		
		Object nullObject = null;
		try {
			changeSetService.addObjectToChangeSet(CHANGE_SET_GUID, nullObject, null);
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Tests that adding a business object instance to change set.
	 */
	@Test
	public void testAddBusinessObjectToChangeSetWithAllRequiredParams() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final String changeSetGuid = "changeSetId2";
		final BusinessObjectGroupMemberImpl groupMember = new BusinessObjectGroupMemberImpl();
		final Object object = new Object();
		
		context.checking(new Expectations() { {

			oneOf(changeSetPolicy).isChangeAllowed(changeSetGuid);
			will(returnValue(true));

		    oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_GROUP_MEMBER);
		    will(returnValue(groupMember));

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(new ChangeSetObjectStatusImpl()));

		    List<BusinessObjectMetadata> noMetadata = Collections.emptyList();
			oneOf(changeSetMemberDao).add(groupMember, noMetadata);
			
			oneOf(changeSetPolicy).resolveObjectDescriptor(object);
		    will(returnValue(objectDescriptor));
		    
		    oneOf(changeSetPolicy).getObjectMembershipGuids(objectDescriptor);
		    will(returnValue(Collections.emptyList()));
		    
		    oneOf(changeSetDao).findByGuid(changeSetGuid);
		    will(returnValue(new ChangeSetImpl()));

		    oneOf(changeSetPolicy).resolveMetaData(objectDescriptor);
		    will(returnValue(Collections.emptyMap()));
		} });

		changeSetService.addObjectToChangeSet(changeSetGuid, object, null);
		
		assertEquals("Object should be added to change set with correct GUID.", changeSetGuid, groupMember.getGroupId());
	}
	
	/**
	 * Tests adding an unknown business object instance to change set.
	 */
	@Test
	public void testAddUnknownBusinessObjectToChangeSet() {
		//final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final String changeSetGuid = "changeSetId2";
		//final BusinessObjectGroupMemberImpl groupMember = new BusinessObjectGroupMemberImpl();
		final Object object = new Object();
		
		context.checking(new Expectations() { {
		    
		    oneOf(changeSetPolicy).resolveObjectDescriptor(object);
		    will(returnValue(null));
		    
		} });

		try {
			changeSetService.addObjectToChangeSet(changeSetGuid, object, null);
			fail("Did not catch the expected exception");
		} catch (ChangeSetPolicyException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Tests adding a business object to the same change set.
	 */
	@Test
	public void testAddObjectToSameChangeSet() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final String changeSetGuid = "changeSetId3";
		
		final ChangeSetObjectStatus changeSetObjectStatus = new ChangeSetObjectStatusImpl();
		
		context.checking(new Expectations() { {

			oneOf(changeSetPolicy).isChangeAllowed(changeSetGuid);
			will(returnValue(true));

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(changeSetObjectStatus));

		    oneOf(changeSetPolicy).getObjectMembershipGuids(objectDescriptor);
		    will(returnValue(Arrays.asList(changeSetGuid)));

		    oneOf(changeSetDao).findByGuid(changeSetGuid);
		    will(returnValue(new ChangeSetImpl()));
		} });

		changeSetService.addObjectToChangeSet(changeSetGuid, objectDescriptor, null);
	}
	
	/**
	 * Tests adding a locked business object to a change set.
	 */
	@Test
	public void testAddLockedObjectToChangeSet() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final String changeSetGuid = "changeSetId3";
		
		final ChangeSetObjectStatus changeSetObjectStatus = new ChangeSetObjectStatusImpl();
		
		context.checking(new Expectations() { {

			oneOf(changeSetPolicy).isChangeAllowed(changeSetGuid);
			will(returnValue(true));

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(changeSetObjectStatus));

		    oneOf(changeSetPolicy).getObjectMembershipGuids(objectDescriptor);
		    will(returnValue(Arrays.asList("otherChangeSetId")));

		    oneOf(changeSetDao).findByGuid(changeSetGuid);
		    will(returnValue(new ChangeSetImpl()));
		} });

		try {
			changeSetService.addObjectToChangeSet(changeSetGuid, objectDescriptor, null);
			fail("Did not catch expected exception");
		} catch (ChangeSetPolicyException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Tests adding a business object to the same change set.
	 */
	@Test
	public void testChangeSetObjectStatusWithoutImplementMutator() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final String changeSetGuid = "changeSetId3";
		
		final ChangeSetObjectStatus changeSetObjectStatus = context.mock(ChangeSetObjectStatus.class);
		
		context.checking(new Expectations() { {

			oneOf(changeSetPolicy).isChangeAllowed(changeSetGuid);
			will(returnValue(true));

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(changeSetObjectStatus));

		    oneOf(changeSetDao).findByGuid(changeSetGuid);
		    will(returnValue(new ChangeSetImpl()));
		} });

		try {
			changeSetService.addObjectToChangeSet(changeSetGuid, objectDescriptor, null);
			fail("Did not catch the expected exception");
		} catch (EpDomainException e) {
			assertNotNull(e);
		}
	}


	/**
	 * Tests that removing a change set involves removing a group member.
	 */
	@Test
	public void testRemoveObjectFromChangeSet() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();

		context.checking(new Expectations() { {

			oneOf(changeSetPolicy).isChangeAllowed(CHANGE_SET_GUID);
			will(returnValue(true));

		    oneOf(changeSetMemberDao).removeByObjectDescriptor(objectDescriptor, CHANGE_SET_GUID);
		    
		} });
		
		changeSetService.removeObjectFromChangeSet(CHANGE_SET_GUID, objectDescriptor);
	}

	/**
	 * Tests that getStatus() returns non-null result in case we have no members.
	 */
	@Test
	public void testGetStatus() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		
		context.checking(new Expectations() { {

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(new ChangeSetObjectStatusImpl()));

		    oneOf(changeSetPolicy).getObjectMembershipGuids(objectDescriptor);
		    will(returnValue(Collections.emptyList()));
		} });

		ChangeSetObjectStatus status = changeSetService.getStatus(objectDescriptor);
		
		assertNotNull("A non-null status must be returned.", status);
	}
	
	/**
	 * Tests that getStatus(Object object) returns non-null result in case we have no object membership GUIDs.
	 */
	@Test
	public void testGetStatusOfObjectNoMembers() {
		final Object businessObject = new Object();
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		
		context.checking(new Expectations() { {

		    oneOf(changeSetPolicy).resolveObjectDescriptor(businessObject);
		    will(returnValue(objectDescriptor));

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(new ChangeSetObjectStatusImpl()));

		    oneOf(changeSetPolicy).getObjectMembershipGuids(objectDescriptor);
		    will(returnValue(Collections.emptyList()));
		    
		} });

		ChangeSetObjectStatus status = changeSetService.getStatus(businessObject);
		
		assertNotNull("A non-null status must be returned.", status);
	}
	
	/**
	 * Tests that getStatus(Object object) returns non-null result in case we have no business object descriptor type mapping.
	 */
	@Test
	public void testGetStatusOfObjectNoTypeMapping() {
		final Object businessObject = new Object();
		
		// no type mapping will result in a null descriptor
		final BusinessObjectDescriptor objectDescriptor = null;
		
		context.checking(new Expectations() { {

		    oneOf(changeSetPolicy).resolveObjectDescriptor(businessObject);
		    will(returnValue(objectDescriptor));

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(new ChangeSetObjectStatusImpl()));
		} });

		ChangeSetObjectStatus status = changeSetService.getStatus(businessObject);
		
		assertNotNull("A non-null status must be returned.", status);
	}
	
	/**
	 * Tests that getStatus(ObjectDescriptor object) returns non-null result in case we have no business object descriptor.
	 */
	@Test
	public void testGetStatusOfNoObjectDescriptor() {
		
		// no object descriptor
		final BusinessObjectDescriptor objectDescriptor = null;
		
		context.checking(new Expectations() { {

		    oneOf(beanFactory).getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
		    will(returnValue(new ChangeSetObjectStatusImpl()));

		} });

		ChangeSetObjectStatus status = changeSetService.getStatus(objectDescriptor);
		
		assertNotNull(status);
	}
	
	/**
	 * Tests find change set by business object descriptor.
	 */
	@Test
	public void testFindChangeSet() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final ChangeSet changeSet = new ChangeSetImpl();
		
		context.checking(new Expectations() { {
		    
		    oneOf(changeSetPolicy).getNonFinalizedStates();
		    will(returnValue(null));

		    oneOf(changeSetDao).findByObjectDescriptor(objectDescriptor, null);
		    will(returnValue(Arrays.asList(changeSet)));

		} });
		
		changeSetService.findChangeSet(objectDescriptor);
	}
	
	/**
	 * Tests find change set by business object.
	 */
	@Test
	public void testFindChangeSetByBusinessObject() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		final ChangeSet changeSet = new ChangeSetImpl();
		final Object object = new Object();
		
		context.checking(new Expectations() { {
		    
		    oneOf(changeSetPolicy).resolveObjectDescriptor(object);
		    will(returnValue(objectDescriptor));

		    oneOf(changeSetPolicy).getNonFinalizedStates();
		    will(returnValue(null));

		    oneOf(changeSetDao).findByObjectDescriptor(objectDescriptor, null);
		    will(returnValue(Arrays.asList(changeSet)));

		} });
		
		changeSetService.findChangeSet(object);
	}
	
	/**
	 * Tests find change set member meta data by business object descriptor.
	 */
	@Test
	public void testFindChangeSetMemberMetadata() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		
		final BusinessObjectMetadata businessObjectMetadata = new BusinessObjectMetadataImpl();
		businessObjectMetadata.setMetadataKey("key");
		businessObjectMetadata.setMetadataValue("value");
		final String changeSetGuid = "cs2";
		
		context.checking(new Expectations() { {
		    
		    oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupIdAndDescriptor(changeSetGuid, objectDescriptor);
		    will(returnValue(Arrays.asList(businessObjectMetadata)));

		} });
		
		changeSetService.findChangeSetMemberMetadata(changeSetGuid, objectDescriptor);
	}
	
	/**
	 * Tests find change set member meta data by business object.
	 */
	@Test
	public void testFindChangeSetMemberMetadataByBusinessObject() {
		final Object object = new Object();
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		
		final BusinessObjectMetadata businessObjectMetadata = new BusinessObjectMetadataImpl();
		businessObjectMetadata.setMetadataKey("key");
		businessObjectMetadata.setMetadataValue("value");
		final String changeSetGuid = "cs1";
		
		context.checking(new Expectations() { {
		    
			oneOf(changeSetPolicy).resolveObjectDescriptor(object);
			will(returnValue(objectDescriptor));
			
			oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupIdAndDescriptor(changeSetGuid, objectDescriptor);
		    will(returnValue(Arrays.asList(businessObjectMetadata)));

		} });
		
		changeSetService.findChangeSetMemberMetadata(changeSetGuid, object);
	}
	
	/**
	 * Tests that having invalid change set GUID results in an appropriate exception.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidChangeSetGuid() {
		final String changeSetGuid = "test1";
		context.checking(new Expectations() { {
		    oneOf(changeSetDao).findByGuid(changeSetGuid);
		    will(returnValue(null));
		} });
		
		changeSetService.addObjectToChangeSet("test1", null, null);
	}
	
	/**
	 * Test find change set for the business object which is not in excluded change set.
	 */
	@Test
	public void testFindChangeSetByBusinessObjectsAndExcludedChangeSet() {
		final BusinessObjectDescriptor obj1 = new BusinessObjectDescriptorImpl();
		
		Set<BusinessObjectDescriptor> objects = new LinkedHashSet<BusinessObjectDescriptor>();
		objects.add(obj1);
		
		final ChangeSetImpl excludedChangeSet = new ChangeSetImpl();
		excludedChangeSet.setObjectGroupId("excludedChangeSet");
		final ChangeSetImpl changeSet = new ChangeSetImpl();
		changeSet.setObjectGroupId("changeSet");
		
		context.checking(new Expectations() { {
		    
		    oneOf(changeSetPolicy).getNonFinalizedStates();
		    will(returnValue(null));

		    oneOf(changeSetDao).findByObjectDescriptor(obj1, null);
		    will(returnValue(Arrays.asList(changeSet)));

		} });
		
		Map<BusinessObjectDescriptor, ChangeSet> retMap = changeSetService.findChangeSet(objects, excludedChangeSet);
		
		assertNotNull(retMap);
		assertEquals("Expect one record in the returned map", 1, retMap.size());
		assertEquals("the correct change set is not found", changeSet, retMap.get(obj1));
		
		// the excluded change set was found and should not be added in the map for returning
		context.checking(new Expectations() { {
		    
		    oneOf(changeSetPolicy).getNonFinalizedStates();
		    will(returnValue(null));

		    oneOf(changeSetDao).findByObjectDescriptor(obj1, null);
		    will(returnValue(Arrays.asList(excludedChangeSet)));

		} });
		
		retMap = changeSetService.findChangeSet(objects, excludedChangeSet);
		
		assertNotNull(retMap);
		assertTrue("Expect empty map", retMap.isEmpty());
	}
}
