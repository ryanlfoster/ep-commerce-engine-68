/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.changeset.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;

/**
 * Test cases for {@link ChangeSetObjectStatusImpl}.
 */
public class ChangeSetObjectStatusImplTest {

	private static final String CHANGE_SET1 = "changeSet1";
	private static final String CHANGE_SET2 = "changeSet2";
	
	private ChangeSetObjectStatusImpl changeSetObjectStatus;

	/**
	 *
	 * @throws java.lang.Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		changeSetObjectStatus = new ChangeSetObjectStatusImpl();

		// mandatory object descriptor
		BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		changeSetObjectStatus.setObjectDescriptor(objectDescriptor);
	}
	
	/**
	 * Test that status is "not available" when no object descriptor set. This is
	 * required since object mapping may have failed, resulting in a null object descriptor,
	 * resulting in a change set never being available.
	 */
	@Test
	public void testMandatoryObjectDescriptor() {
		changeSetObjectStatus.setChangeSetGuids(Arrays.asList(CHANGE_SET1));
		changeSetObjectStatus.setObjectDescriptor(null);
		
		assertTrue("Business object should be a member.", changeSetObjectStatus.isMember(CHANGE_SET1));
		assertTrue("Business object should be locked.", changeSetObjectStatus.isLocked());
		assertFalse("Business object should not be available.", changeSetObjectStatus.isAvailable(CHANGE_SET1));
	}

	/**
	 * Test that if an object is in a given change set the object 
	 * is a member and is available for that change set.
	 */
	@Test
	public void testStatusWhenInAGivenChangeSet() {
		changeSetObjectStatus.setChangeSetGuids(Arrays.asList(CHANGE_SET1));
		
		assertTrue("Business object should be a member.", changeSetObjectStatus.isMember(CHANGE_SET1));
		assertTrue("Business object should be locked.", changeSetObjectStatus.isLocked());
		assertTrue("Business object should be available.", changeSetObjectStatus.isAvailable(CHANGE_SET1));
	}

	/**
	 * Test that if an object is not in any change set the object is 
	 * not a member but is available for any change set.
	 */
	@Test
	public void testStatusWhenInNoChangeSet() {
		changeSetObjectStatus.setChangeSetGuids(Collections.<String>emptyList());
		
		assertFalse("Business object should not be a member.", changeSetObjectStatus.isMember(CHANGE_SET1));
		assertFalse("Business object should not be locked.", changeSetObjectStatus.isLocked());
		assertTrue("Business object should be available.", changeSetObjectStatus.isAvailable(CHANGE_SET1));
	}

	/**
	 * Test that if an object is in a specific change set the object 
	 * is not a member and is not available for any other change set.
	 */
	@Test
	public void testStatusWhenInDifferentChangeset() {
		changeSetObjectStatus.setChangeSetGuids(Arrays.asList(CHANGE_SET1));
		
		assertFalse("Business object should not be a member.", changeSetObjectStatus.isMember(CHANGE_SET2));
		assertTrue("Business object should be locked.", changeSetObjectStatus.isLocked());
		assertFalse("Business object should not be available.", changeSetObjectStatus.isAvailable(CHANGE_SET2));
	}

}
