/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.PaginationConfig;
import com.elasticpath.commons.pagination.Paginator;
import com.elasticpath.commons.pagination.PaginatorFactory;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetUserView;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserPermission;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.cmuser.impl.UserPermissionImpl;
import com.elasticpath.domain.cmuser.impl.UserRoleImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetMemberSortingField;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.cmuser.UserRoleService;
import com.elasticpath.service.objectgroup.dao.BusinessObjectGroupDao;

/**
 * Tests for ChangeSetServiceImpl.
 */
public class ChangeSetServiceImplTest extends BasicSpringContextTest {

	private static final String CREATION_DATE = "creationDate";

	private static final String USER_ID = "userId";

	@Autowired
	private ChangeSetService changeSetService;

	private ChangeSet changeSet;

	@Autowired
	private BusinessObjectGroupDao businessObjectGroupDao;

	@Autowired
	private ChangeSetManagementService changeSetManagementService;

	private static final DirectedSortingField [] ORDERING_FIELD = new DirectedSortingField [] { 
		new DirectedSortingField(ChangeSetMemberSortingField.OBJECT_ID, SortingDirection.ASCENDING) };

	private static final int PAGE_SIZE = 50;

	/**
	 * Sets up each test.
	 * 
	 * @throws Exception on failure.
	 */
	@Before
	public void setUp() throws Exception {
		// create a new change set
		changeSet = getBeanFactory().getBean(ContextIdNames.CHANGE_SET);
		changeSet.setName("sample changeset name");
		changeSet.setCreatedDate(new Date());
		changeSet.setCreatedByUserGuid(getTac().getPersistersFactory().getStoreTestPersister().getCmUser().getGuid());

		changeSetManagementService.add(changeSet);
	}

	/**
	 * Tests that adding a new change set works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testAddNewObjectToChangeSet() {
		BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier("productGuid2");
		objectDescriptor.setObjectType("Sku");

		changeSetService.addObjectToChangeSet(changeSet.getObjectGroupId(), objectDescriptor, null);

		Collection<BusinessObjectGroupMember> result = businessObjectGroupDao.findGroupMembersByGroupId(changeSet.getObjectGroupId());

		assertNotNull(result);
		assertEquals(1, result.size());
		BusinessObjectGroupMember resultObjMember = result.iterator().next();
		assertEquals(objectDescriptor.getObjectIdentifier(), resultObjMember.getObjectIdentifier());
		assertEquals(objectDescriptor.getObjectType(), resultObjMember.getObjectType());
	}
	/**
	 * Tests that adding a new change set works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testAddNewObjectWithMetaDataToChangeSet() {
		BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier("productGuid1");
		objectDescriptor.setObjectType("SKU");

		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(USER_ID, "user_2");
		metadata.put(CREATION_DATE, new Date().toString());

		changeSetService.addObjectToChangeSet(changeSet.getObjectGroupId(), objectDescriptor, metadata);

		Map<String, String> metadataMap = changeSetService.findChangeSetMemberMetadata(changeSet.getObjectGroupId(), objectDescriptor);

		assertNotNull(metadataMap);

		assertEquals("user_2", metadataMap.get(USER_ID));
		assertNotNull(metadataMap.get(CREATION_DATE));
	}

	/**
	 * Tests that adding a new change set works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testGetChangeSetByObjectDescriptor() {
		BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier("productGuid1");
		objectDescriptor.setObjectType("Product");

		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(USER_ID, "user_1");
		metadata.put(CREATION_DATE, new Date().toString());

		changeSetService.addObjectToChangeSet(changeSet.getObjectGroupId(), objectDescriptor, metadata);

		Map<String, String> metadataMap = changeSetService.findChangeSetMemberMetadata(changeSet.getObjectGroupId(), objectDescriptor);
		
		assertNotNull(metadataMap);

		assertEquals("user_1", metadataMap.get(USER_ID));
		assertNotNull(metadataMap.get(CREATION_DATE));
		
		ChangeSet retrievedChangeSet = changeSetService.findChangeSet(objectDescriptor);
		assertNotNull(retrievedChangeSet);
		assertEquals(changeSet.getObjectGroupId(), retrievedChangeSet.getObjectGroupId());
	}

	/**
	 * Tests that adding a new change set works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveObjectFromChangeSet() {
		BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier("productGuid1");
		objectDescriptor.setObjectType("Product");

		// adds object1
		changeSetService.addObjectToChangeSet(changeSet.getObjectGroupId(), objectDescriptor, null);

		// then make a change to the change set and verify the change
		BusinessObjectDescriptor objectDescriptor2 = new BusinessObjectDescriptorImpl();
		objectDescriptor2.setObjectIdentifier("productGuid2");
		objectDescriptor2.setObjectType("Product");

		// adds object2
		changeSetService.addObjectToChangeSet(changeSet.getObjectGroupId(), objectDescriptor2, null);

		// remove object1
		changeSetService.removeObjectFromChangeSet(changeSet.getObjectGroupId(), objectDescriptor);

		Collection<BusinessObjectGroupMember> result = businessObjectGroupDao.findGroupMembersByGroupId(changeSet.getObjectGroupId());

		assertEquals(1, result.size());
		BusinessObjectGroupMember resultObjMember = result.iterator().next();
		assertEquals(objectDescriptor2.getObjectIdentifier(), resultObjMember.getObjectIdentifier());
		assertEquals(objectDescriptor2.getObjectType(), resultObjMember.getObjectType());
	}
	
	/**
	 * Test add new user to change set.
	 */
	@DirtiesDatabase
	@Test
	public void testAddNewUserToChangeSet() {
				
		final ChangeSet newChangeSet = getBeanFactory().getBean(ContextIdNames.CHANGE_SET);
		newChangeSet.setName("add changeset " + System.currentTimeMillis());
		newChangeSet.setDescription("add change set desc");
		newChangeSet.setCreatedByUserGuid(getTac().getPersistersFactory().getStoreTestPersister().getCmUser().getGuid());
		
		changeSetManagementService.add(newChangeSet);
		
		// Add to the change set
		newChangeSet.addAssignedUser("userId123");			
		final ChangeSet updatedChangeSet = changeSetManagementService.update(newChangeSet, null);
					
		// Check number of assigned users in the change set
		assertEquals("The added users are the creator and the newly added one", 2, updatedChangeSet.getAssignedUserGuids().size());
		
		// Check this is the user we added
		Collection<String> changeSetUsers = updatedChangeSet.getAssignedUserGuids();		
		assertTrue(changeSetUsers.contains("userId123"));		
	}
	
	/**
	 * Test add new user to change set.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveUserFromChangeSet() {
			
		final ChangeSet newChangeSet = getBeanFactory().getBean(ContextIdNames.CHANGE_SET);
		newChangeSet.setName("remove changeset " + System.currentTimeMillis());
		newChangeSet.setDescription("remove change set desc");
		newChangeSet.setCreatedByUserGuid(getTac().getPersistersFactory().getStoreTestPersister().getCmUser().getGuid());

		changeSetManagementService.add(newChangeSet);
		
		// Add to the change set
		newChangeSet.addAssignedUser("cmUserGuid");			
		ChangeSet updatedChangeSet = changeSetManagementService.update(newChangeSet, null);						

		assertEquals("Number of users in change set should be 2 (creator + the new user)", 
				     2, 
				     updatedChangeSet.getAssignedUserGuids().size());
		
		// Remove the user and update the change set
		updatedChangeSet.removeAssignedUser("cmUserGuid");		
		ChangeSet updatedChangeSetWithUserRemoved = changeSetManagementService.update(updatedChangeSet, null);
		
		// Check num users is zero
		assertEquals("Number of users in change set should be 1 (creator)", 1,
				updatedChangeSetWithUserRemoved.getAssignedUserGuids().size());
		
	}
	
	/**
	 * Tests loading of a change set member page and navigating through the adjacent pages.
	 */
	@DirtiesDatabase
	@Test
	public void testLoadChangeSetMemberPages() {
		final int totalElements = 133;
		final int totalPages = 3;
		final int lastPageElements = 33;
		final int pageNumber3 = 3;
		
		persistObjectMembers(0, totalElements);
		
		PaginatorFactory factory = getBeanFactory().getBean("paginatorFactory"); //$NON-NLS-1$
		PaginationConfig config = getBeanFactory().getBean("paginationConfig"); //$NON-NLS-1$;
		config.setObjectId(changeSet.getGuid());
		config.setPageSize(PAGE_SIZE);
		config.setSortingFields(ORDERING_FIELD);
		
		Paginator<ChangeSetMember> paginator = factory.createPaginator(ChangeSetMember.class, config); 
		// load page 1
		Page<ChangeSetMember> page1 = paginator.first();
		
		// verify parameters of page 1
		assertNotNull(page1);
		assertEquals(1, page1.getPageNumber());
		assertEquals(PAGE_SIZE, page1.getPageSize());
		assertEquals(1, page1.getPageStartIndex());
		assertEquals(PAGE_SIZE, page1.getPageEndIndex());
		assertTrue(ArrayUtils.isEquals(ORDERING_FIELD, page1.getOrderingFields()));
		Collection<ChangeSetMember> itemsPage1 = page1.getItems();
		assertNotNull(itemsPage1);
		assertEquals(totalPages, page1.getTotalPages());
		assertEquals(totalElements, page1.getTotalItems());
		assertEquals(PAGE_SIZE, itemsPage1.size());

		assertElementsInOrder(itemsPage1);
		
		paginator.next();
		// load page 3
		Page<ChangeSetMember> page3 = paginator.next();
		// verify parameters of page 3
		assertEquals(pageNumber3, page3.getPageNumber());
		assertEquals(PAGE_SIZE, page3.getPageSize());
		assertEquals(PAGE_SIZE * 2 + 1, page3.getPageStartIndex());
		assertEquals(totalElements, page3.getPageEndIndex());
		assertEquals(totalPages, page3.getTotalPages());
		assertEquals(totalElements, page3.getTotalItems());
		assertTrue(ArrayUtils.isEquals(ORDERING_FIELD, page3.getOrderingFields()));
		Collection<ChangeSetMember> itemsPage3 = page3.getItems();
		assertNotNull(itemsPage3);
		assertEquals(lastPageElements, itemsPage3.size());
		
		assertElementsInOrder(itemsPage3);
		
		// tries to load page 4 (should not exist)
		Page<ChangeSetMember> page4 = paginator.next();
		verifyPagesEqual(page3, page4);

		// tries to load page 0 (should not exist)
		paginator.first();
		Page<ChangeSetMember> page0 = paginator.previous();
		verifyPagesEqual(page1, page0);
	}

	/**
	 *
	 * @param page
	 */
	private void verifyPagesEqual(final Page< ? > expectedPage, final Page< ? > actual) {
		assertNotNull("Expected result page should not be null", expectedPage);
		assertNotNull("Expected result page items should not be null", expectedPage.getItems());
		assertNotNull("Actual result page should not be null", actual);
		assertNotNull("Actual result page items should not be null", actual.getItems());
		
		assertEquals("Page numbers should be the same", expectedPage.getPageNumber(), actual.getPageNumber());
		assertEquals("Page end indices should be the same", expectedPage.getPageEndIndex(), actual.getPageEndIndex());
		assertEquals("Page start idices should be the same", expectedPage.getPageStartIndex(), actual.getPageStartIndex());
		assertEquals("Page sizes should be the same", expectedPage.getPageSize(), actual.getPageSize());
		assertEquals("The items size should be the same", expectedPage.getItems().size(), actual.getItems().size());
		assertEquals("The items should be the same", expectedPage.getItems(), actual.getItems());
		assertEquals("Page total numbers should be the same", expectedPage.getTotalItems(), actual.getTotalItems());
		assertEquals("Page total pages should be the same", expectedPage.getTotalPages(), actual.getTotalPages());
	}

	/**
	 *
	 * @param items
	 */
	private void assertElementsInOrder(final Collection<ChangeSetMember> items) {
		ChangeSetMember[] elements;
		elements = items.toArray(new ChangeSetMember[items.size()]);
		for (int i = 1; i < elements.length; i++) {
			String elemN1objectIdentifier = elements[i - 1].getBusinessObjectDescriptor().getObjectIdentifier();
			String elemN2objectIdentifier = elements[i].getBusinessObjectDescriptor().getObjectIdentifier();
			assertTrue(elemN1objectIdentifier.compareTo(elemN2objectIdentifier) < 0);
		}
	}

	/**
	 * Tests loading of a change set member page and navigating through the adjacent pages.
	 */
	@DirtiesDatabase
	@Test
	public void testLoadChangeSetMemberPagesWhileNewElementsAreBeingAdded() {
		final int number50 = 50;
		final int number133 = 133;
		final int number3 = 3;
		final int number68 = 68;
		final int number201 = 201;
		final int number150 = 150;
		final int number5 = 5;
		final int number4 = 4;
		final int number200 = 200;

		persistObjectMembers(0, number133);
		
		PaginatorFactory factory = getBeanFactory().getBean("paginatorFactory"); //$NON-NLS-1$
		PaginationConfig config = getBeanFactory().getBean("paginationConfig"); //$NON-NLS-1$;
		config.setObjectId(changeSet.getGuid());
		config.setPageSize(PAGE_SIZE);
		config.setSortingFields(ORDERING_FIELD);
		
		Paginator<ChangeSetMember> paginator = factory.createPaginator(ChangeSetMember.class, config); 

		// load page 1
		Page<ChangeSetMember> page1 = paginator.first();
		
		// verify parameters of page 1
		assertNotNull(page1);
		assertEquals(1, page1.getPageNumber());
		assertEquals(PAGE_SIZE, page1.getPageSize());
		assertEquals(1, page1.getPageStartIndex());
		assertEquals(PAGE_SIZE, page1.getPageEndIndex());
		assertTrue(ArrayUtils.isEquals(ORDERING_FIELD, page1.getOrderingFields()));
		Collection<ChangeSetMember> itemsPage1 = page1.getItems();
		assertNotNull(itemsPage1);
		assertEquals(number3, page1.getTotalPages());
		assertEquals(number133, page1.getTotalItems());
		assertEquals(PAGE_SIZE, itemsPage1.size());

		assertElementsInOrder(itemsPage1);

		persistObjectMembers(number133, number68);

		paginator.next();
		// load page 3
		Page<ChangeSetMember> page3 = paginator.next();
		// verify parameters of page 3
		assertEquals(number3, page3.getPageNumber());
		assertEquals(PAGE_SIZE, page3.getPageSize());
		assertEquals(PAGE_SIZE * 2 + 1, page3.getPageStartIndex());
		assertEquals(number150, page3.getPageEndIndex());
		assertEquals(number5, page3.getTotalPages());
		assertEquals(number201, page3.getTotalItems());
		assertTrue(ArrayUtils.isEquals(ORDERING_FIELD, page3.getOrderingFields()));
		Collection<ChangeSetMember> itemsPage3 = page3.getItems();
		assertNotNull(itemsPage3);
		assertEquals(number50, itemsPage3.size());
		
		assertElementsInOrder(itemsPage3);
		
		// tries to load page 4
		Page<ChangeSetMember> page4 = paginator.next();
		assertNotNull(page4);
		// verify parameters of page 4
		assertEquals(number4, page4.getPageNumber());
		assertEquals(PAGE_SIZE, page4.getPageSize());
		assertEquals(PAGE_SIZE * number3 + 1, page4.getPageStartIndex());
		assertEquals(number200, page4.getPageEndIndex());
		assertEquals(number5, page4.getTotalPages());
		assertEquals(number201, page4.getTotalItems());
		assertTrue(ArrayUtils.isEquals(ORDERING_FIELD, page4.getOrderingFields()));
		Collection<ChangeSetMember> itemsPage4 = page4.getItems();
		assertNotNull(itemsPage4);
		assertEquals(number50, itemsPage4.size());

		Page<ChangeSetMember> page5 = paginator.last();
		assertNotNull(page5);

		assertEquals(1, page5.getItems().size());

		Page<ChangeSetMember> page6 = paginator.next();
		verifyPagesEqual(page5, page6);

	}

	/**
	 *
	 * @param totalElements
	 */
	private void persistObjectMembers(final int startFrom, final int totalElements) {
		// create test object members
		for (int index = startFrom; index < startFrom + totalElements; index++) {
			BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
			objectDescriptor.setObjectIdentifier("id" + index);
			objectDescriptor.setObjectType("myObjectType");
			Map<String, String> metadata = new HashMap<String, String>();
			changeSetService.addObjectToChangeSet(changeSet.getGuid(), objectDescriptor, metadata);
		}
	}
	
	/**
	 * Tests that getting available users retrieves the users with correct permissions.
	 */
	@DirtiesDatabase
	@Test
	public void testAvailableUsers() {
		CmUserService cmUserService = getBeanFactory().getBean("cmUserServiceWithFakeEmail");
		UserRoleService userRoleService = getBeanFactory().getBean(ContextIdNames.USER_ROLE_SERVICE);	
		
		CmUser nonSuperUser = new CmUserImpl();
		nonSuperUser = new CmUserImpl();
		nonSuperUser.setUserName("testUser");
		nonSuperUser.setEmail("my@email.com");
		nonSuperUser.setPassword("pass1");
		nonSuperUser.setCreationDate(new Date());
		nonSuperUser.initialize();
		UserRole userRole = new UserRoleImpl();
		userRole.setName("userRole1");
		userRole.setGuid("guid34343434");
		
		UserPermission permission = new UserPermissionImpl();
		permission.setAuthority("permission-id");
		
		userRole.addUserPermission(permission);
		
		nonSuperUser.addUserRole(userRoleService.add(userRole));
		
		cmUserService.add(nonSuperUser);
		
		Collection<ChangeSetUserView> users = changeSetService.getAvailableUsers("permission-id");

		assertEquals("Expected the admin user and the non-superuser", 2, users.size());

		users = changeSetService.getAvailableUsers("non-existent-permission");
		
		assertEquals("Expected only the admin super user as no permissions should match the argument", 1, users.size());
	}

}
