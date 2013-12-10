package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectGroupMemberImpl;
import com.elasticpath.service.changeset.ChangeSetMemberSortingField;
import com.elasticpath.service.objectgroup.dao.BusinessObjectGroupDao;

/**
 * Integration JUnit test for the implementation of {@link BusinessObjectGroupDao}.
 */
public class BusinessObjectGroupDaoImplTest extends BasicSpringContextTest {

	private static final DirectedSortingField [] ORDERING_FIELD = new DirectedSortingField [] { 
		new DirectedSortingField(ChangeSetMemberSortingField.OBJECT_ID, SortingDirection.ASCENDING) };

	private static final DirectedSortingField [] TYPE_ORDERING_FIELD = new DirectedSortingField [] { 
		new DirectedSortingField(ChangeSetMemberSortingField.OBJECT_TYPE, SortingDirection.ASCENDING) };

	private static final String GROUP_ID1 = "test";
	private static final int NUMBER_4 = 4;
	private static final int NUMBER_3 = 3;
	private static final int NUMBER_6 = 6;
	@Autowired
	private BusinessObjectGroupDao businessObjectGroupDao;

	@Before
	public void setUp() throws Exception {
		createSampleTestData();
	}
	
	/**
	 * Tests that retrieving object group members by objectId gets us the right objects.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveObjectMembersFromIndex0SortByObjectId() {
		Collection<BusinessObjectGroupMember> result = businessObjectGroupDao.
			findGroupMembersByGroupId(GROUP_ID1, 0, 2, ORDERING_FIELD);
		assertEquals("Expects the full number of requested entries to be returned", 2, result.size());
		Iterator<BusinessObjectGroupMember> resultIter = result.iterator();
		assertEquals("Expected first entry to be testGuid1", "testGuid1", resultIter.next().getGuid());
		assertEquals("Expected first entry to be testGuid2", "testGuid2", resultIter.next().getGuid());
	}

	/**
	 * Tests that retrieving object group members by objectId gets us the right objects even though
	 * the required max number is over the limit of available objects.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveObjectMembersFromIndex4SortByObjectId() {
		Collection<BusinessObjectGroupMember> result = businessObjectGroupDao.
			findGroupMembersByGroupId(GROUP_ID1, NUMBER_4, NUMBER_4, ORDERING_FIELD);
		assertEquals("Expects only one result entry", 1, result.size());

		Iterator<BusinessObjectGroupMember> resultIter = result.iterator();
		assertEquals("The first result entry should be the last but one ordered by object ID", "testGuid5", resultIter.next().getGuid());
	}

	/**
	 * Tests that even though the initial number is more than the available entries
	 * the result will be an empty collection.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveObjectMembersFromIndex6SortByObjectId() {
		Collection<BusinessObjectGroupMember> result = businessObjectGroupDao.
			findGroupMembersByGroupId(GROUP_ID1, NUMBER_6, NUMBER_4, ORDERING_FIELD);
		assertEquals("Expects no results", 0, result.size());
	}

	/**
	 * Tests that retrieving object group members by objectType gets us the right objects.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveObjectMembersFromIndex0SortByObjectType() {
		Collection<BusinessObjectGroupMember> result = businessObjectGroupDao.
			findGroupMembersByGroupId(GROUP_ID1, 0, NUMBER_4, TYPE_ORDERING_FIELD);
		assertEquals("Expects 4 result entries", NUMBER_4, result.size());

		Iterator<BusinessObjectGroupMember> resultIter = result.iterator();
		
		assertEquals("Sorting by object type should bring 'apple' in first place", "apple", resultIter.next().getObjectType());
	}

	/**
	 * Tests that retrieving object group members by objectType gets us the right objects even though
	 * the required max number is over the limit of available objects.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveObjectMembersFromIndex3SortByObjectType() {
		Collection<BusinessObjectGroupMember> result = businessObjectGroupDao.
			findGroupMembersByGroupId(GROUP_ID1, NUMBER_3, NUMBER_3, TYPE_ORDERING_FIELD);
		assertEquals("Expects 2 result entries", 2, result.size());

		Iterator<BusinessObjectGroupMember> resultIter = result.iterator();
		
		assertEquals("Sorting by object type should bring 'pear' in third place", "pear", resultIter.next().getObjectType());
	}
	
	/**
	 * Creates a sample list of business object group members into the data store.
	 */
	private void createSampleTestData() {
		// save entries in mixed order
		String[][] entries = new String[][] { 
					{GROUP_ID1, "testGuid2", "objectId2", "apple" }, 
					{GROUP_ID1, "testGuid4", "objectId4", "pear" }, 
					{GROUP_ID1, "testGuid1", "objectId1", "banana" }, 
					{GROUP_ID1, "testGuid3", "objectId3", "orange" }, 
					{GROUP_ID1, "testGuid5", "objectId5", "strawberry" }, 
				};
		for (String[] entry : entries) {
			BusinessObjectGroupMember objectGroupMember = new BusinessObjectGroupMemberImpl();
			objectGroupMember.setGroupId(entry[0]);
			objectGroupMember.setGuid(entry[1]);
			objectGroupMember.setObjectIdentifier(entry[2]);
			objectGroupMember.setObjectType(entry[NUMBER_3]);
			businessObjectGroupDao.addGroupMember(objectGroupMember);
		}
	}
}
