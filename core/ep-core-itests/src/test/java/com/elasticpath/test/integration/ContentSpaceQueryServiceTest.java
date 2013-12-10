package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.targetedselling.impl.ContentSpaceQueryService;

/**
 * An integration test for {@link ContentSpaceQueryService}.
 */
public class ContentSpaceQueryServiceTest extends BasicSpringContextTest {

	@Autowired
	@Qualifier("contentSpaceQueryService")
	private QueryService<ContentSpace> queryService;

	/**
	 * Test expectations when no content spaces exist.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllGuidsWhenNoneExist() {
		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class).returning(ResultType.GUID);

		QueryResult<String> result = queryService.query(criteria);
		assertEquals("The found GUIDs should have size of 0.", 0, result.getResults().size());
	}

	/**
	 * Test finding all content space guids.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllGuids() {
		Set<String> guids = new HashSet<String>();
		guids.add(getTac().getPersistersFactory().getDynamicContentDeliveryTestPersister().persistContentSpace("cs_1", "").getGuid());
		guids.add(getTac().getPersistersFactory().getDynamicContentDeliveryTestPersister().persistContentSpace("cs_2", "").getGuid());
		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class).returning(ResultType.GUID);

		QueryResult<String> result = queryService.query(criteria);
		assertEquals("The found GUIDs should have size of 2.", 2, result.getResults().size());
		assertTrue("The found GUIDs should exist in the set of created GUIDs.", guids.containsAll(result.getResults()));
	}

}
