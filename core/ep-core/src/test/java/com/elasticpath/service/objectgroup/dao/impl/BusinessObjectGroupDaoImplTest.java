package com.elasticpath.service.objectgroup.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.service.catalog.ProductSkuOrderingField;
import com.elasticpath.service.changeset.ChangeSetMemberSortingField;

/**
 * A test case for {@link BusinessObjectGroupDaoImpl}.
 */
public class BusinessObjectGroupDaoImplTest {

	private static final DirectedSortingField [] ORDERING_FIELD = new DirectedSortingField [] { 
		new DirectedSortingField(ChangeSetMemberSortingField.OBJECT_ID, SortingDirection.ASCENDING) };
	private BusinessObjectGroupDaoImpl objectGroupDao;
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() {
		objectGroupDao = new BusinessObjectGroupDaoImpl();
	}
	
	/**
	 * Validates that a null sorting field leads to an expected exception.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindGroupMembersByGroupIdWithInvalidSortingField() {
		objectGroupDao.findGroupMembersByGroupId("groupId", 1, 1, null);
	}

	/**
	 * Validates that a null group ID leads to an expected exception.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFindGroupMembersByGroupIdWithInvalidGroupId() {
		objectGroupDao.findGroupMembersByGroupId(null, 1, 1, ORDERING_FIELD);
	}

	/**
	 * Tests that when GroupId is null we get the expected exception with the message formatted correctly. 
	 */
	@Test
	public void testFindGroupMembersByGroupIdNull1OrderingField() {
		BusinessObjectGroupDaoImpl dao = new BusinessObjectGroupDaoImpl();
		
		boolean expectedExceptionOccurred = false;
		try {
			dao.findGroupMembersByGroupId(null, 0, 0, ORDERING_FIELD);
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Expect the message in this format",
					"Null-value/zero argument: groupId=null, sortingField=OBJECTIDENTIFIER, ASC",
					e.getMessage());
			expectedExceptionOccurred = true;
		}
		assertTrue("Expect an IllegalArgumentException", expectedExceptionOccurred);
	}
	
	/**
	 * Tests that when GroupId is null we get the expected exception with the message formatted correctly. 
	 */
	@Test
	public void testFindGroupMembersByGroupIdNull2OrderingFields() {
		BusinessObjectGroupDaoImpl dao = new BusinessObjectGroupDaoImpl();
		
		boolean expectedExceptionOccurred = false;
		try {
			DirectedSortingField [] twoOrderingFields = new DirectedSortingField [] { 
					new DirectedSortingField(ChangeSetMemberSortingField.OBJECT_ID, SortingDirection.ASCENDING),
					new DirectedSortingField(ProductSkuOrderingField.SKU_CODE, SortingDirection.DESCENDING) };
			dao.findGroupMembersByGroupId(null, 0, 0, twoOrderingFields);
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Expect the message in this format",
					"Null-value/zero argument: groupId=null, sortingField=OBJECTIDENTIFIER, ASC,"
					+ "SKUCODEINTERNAL, DESC",
					e.getMessage());
			expectedExceptionOccurred = true;
		}
		assertTrue("Expect an IllegalArgumentException", expectedExceptionOccurred);
	}
	
	/**
	 * Tests that when ordering field is null we get the expected exception with the message formatted correctly. 
	 */
	@Test
	public void testFindGroupMembersByOrderingFieldNull() {
		BusinessObjectGroupDaoImpl dao = new BusinessObjectGroupDaoImpl();
		
		boolean expectedExceptionOccurred = false;
		try {
			dao.findGroupMembersByGroupId("group", 0, 0, null);
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Expect the message in this format",
					"Null-value/zero argument: groupId=group, sortingField=null",
					e.getMessage());
			expectedExceptionOccurred = true;
		}
		assertTrue("Expect an IllegalArgumentException", expectedExceptionOccurred);
	}
}
