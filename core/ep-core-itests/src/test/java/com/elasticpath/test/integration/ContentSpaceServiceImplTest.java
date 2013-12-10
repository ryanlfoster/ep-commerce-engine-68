package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.contentspace.ContentSpaceService;
import com.elasticpath.service.targetedselling.impl.ContentSpaceQueryService;

/**
 * An integration test for {@link ContentSpaceQueryService}.
 */
public class ContentSpaceServiceImplTest extends BasicSpringContextTest {

	@Autowired
	private ContentSpaceService contentSpaceService;

	/**
	 * Assert that a ContentSpace can be found by its name.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByName() {
		List<ContentSpace> cs = persistContentSpaces();
		ContentSpace result = contentSpaceService.findByName(cs.get(0).getTargetId());
		assertEquals("The TargetId of the retrieved ContentSpace should equal the TargetId used in the query.",
				cs.get(0).getTargetId(), result.getTargetId());
		assertEquals("The GUID of the retrieved ContentSpace should equal the GUID used in the query.", cs.get(0).getGuid(), result.getGuid());
	}

	/**
	 * Assert that the ContentSpaceService throws an exception when trying to find a ContentSpace by name and the param is null.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindByNameWithNullParam() {
		contentSpaceService.findByName(null);
	}

	/**
	 * Assert that a ContentSpace can be found by its GUID.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByGuid() {
		List<ContentSpace> cs = persistContentSpaces();
		ContentSpace result = contentSpaceService.findByGuid(cs.get(0).getGuid());
		assertEquals("The GUID of the retrieved ContentSpace should equal the GUID used in the query.", cs.get(0).getGuid(), result.getGuid());
	}

	/**
	 * Assert that the ContentSpaceService throws an exception when trying to find a ContentSpace by GUID and the param is null.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindByGuidWithNullParam() {
		contentSpaceService.findByGuid(null);
	}

	/**
	 * Assert that all ContentSpaces can be found.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAll() {
		List<ContentSpace> cs = persistContentSpaces();
		List<ContentSpace> result = contentSpaceService.findAll();
		assertEquals("The found GUIDs should have size of 2.", 2, result.size());
		assertTrue("The found GUIDs should exist in the set of created GUIDs.", cs.containsAll(result));
	}

	/**
	 * Assert that a ContentSpace can be found given a wildcard name.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByNameLike() {
		List<ContentSpace> cs = persistContentSpaces();
		List<ContentSpace> result = contentSpaceService.findByNameLike("1");
		assertEquals("Should have found a result", 1, result.size());
		assertEquals("The actual GUID should be the same as the expected GUID.", cs.get(0).getGuid(), result.get(0).getGuid());
	}

	/**
	 * Assert that multiple ContentSpaces can be found given a wildcard name.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByNameLikeWithMultipleMatches() {
		List<ContentSpace> cs = persistContentSpaces();
		List<ContentSpace> result = contentSpaceService.findByNameLike("cs");
		assertEquals("The found GUIDs should have size of 2.", 2, result.size());
		assertTrue("The found GUIDs should exist in the set of created GUIDs.", cs.containsAll(result));
	}

	/**
	 * Assert that no ContentSpace will be returned if the wildcard name does not match any that exist.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByNameLikeWithoutMatch() {
		persistContentSpaces();
		List<ContentSpace> result = contentSpaceService.findByNameLike("3");
		assertEquals("Should have not found any results.", 0, result.size());
	}

	private List<ContentSpace> persistContentSpaces() {
		List<ContentSpace> cs = new ArrayList<ContentSpace>();
		cs.add(getTac().getPersistersFactory().getDynamicContentDeliveryTestPersister().persistContentSpace("cs_1", ""));
		cs.add(getTac().getPersistersFactory().getDynamicContentDeliveryTestPersister().persistContentSpace("cs_2", ""));
		return cs;
	}
}
