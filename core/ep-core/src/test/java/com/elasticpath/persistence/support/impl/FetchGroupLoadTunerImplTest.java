package com.elasticpath.persistence.support.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link FetchGroupLoadTunerImpl}.
 */
public class FetchGroupLoadTunerImplTest {

	private FetchGroupLoadTunerImpl fetchGroupLoadTunerImpl;
	
	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		fetchGroupLoadTunerImpl = new FetchGroupLoadTunerImpl();
	}
	
	/**
	 * Test method for {@link FetchGroupLoadTunerImpl#addFetchGroup(String...)}.
	 */
	@Test
	public void testAddFetchGroups() {
		final String fg1 = "234";
		final String fg2 = "sldjfsdklf";
		
		fetchGroupLoadTunerImpl.addFetchGroup(fg1, fg2);
		
		boolean containsFG1 = false;
		for (String fetchGroup : fetchGroupLoadTunerImpl) {
			if (fetchGroup.equals(fg1)) {
				containsFG1 = true;
			}
		}
		assertTrue(containsFG1);
		
		boolean containsFG2 = false;
		for (String fetchGroup : fetchGroupLoadTunerImpl) {
			if (fetchGroup.equals(fg2)) {
				containsFG2 = true;
			}
		}
		assertTrue(containsFG2);
	}
	
	/**
	 * Test method for {@link FetchGroupLoadTunerImpl#removeFetchGroup(String...)}.
	 */
	@Test
	public void testRemoveFetchGroups() {
		final String fg1 = "234";
		final String fg2 = "sldjfsdklf";
		
		fetchGroupLoadTunerImpl.addFetchGroup(fg1, fg2);
		fetchGroupLoadTunerImpl.removeFetchGroup(fg1);
		
		boolean containsFG1 = false;
		for (String fetchGroup : fetchGroupLoadTunerImpl) {
			if (fetchGroup.equals(fg1)) {
				containsFG1 = true;
			}
		}
		assertFalse(containsFG1);
		
		boolean containsFG2 = false;
		for (String fetchGroup : fetchGroupLoadTunerImpl) {
			if (fetchGroup.equals(fg2)) {
				containsFG2 = true;
			}
		}
		assertTrue(containsFG2);
	}
}
