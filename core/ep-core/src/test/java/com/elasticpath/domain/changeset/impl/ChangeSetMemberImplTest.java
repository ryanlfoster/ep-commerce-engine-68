/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.changeset.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link ChangeSetMemberImpl}.
 */
public class ChangeSetMemberImplTest {

	private ChangeSetMemberImpl changeSetMember;

	/**
	 *
	 * @throws java.lang.Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		changeSetMember = new ChangeSetMemberImpl();
	}

	/**
	 * Tests that getMetadata() always returns a non-null map value.
	 */
	@Test
	public void testGetMetadata() {
		changeSetMember.setMetadata(null);
		assertNotNull("Metadata map is expected to always be a non-null value", changeSetMember.getMetadata());
	}

}
