package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.service.contentspace.impl.ParameterValueQueryService;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;

/**
 * An integration test for {@link ParameterValueQueryService}.
 */
public class ParameterValueQueryServiceTest extends BasicSpringContextTest {

	@Autowired
	@Qualifier("parameterValueQueryService")
	private QueryService<ParameterValue> queryService;

	/**
	 * Test expectations when no parameter values exist.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllGuidsWhenNoneExist() {
		QueryCriteria<ParameterValue> criteria = CriteriaBuilder.criteriaFor(ParameterValue.class).returning(ResultType.GUID);

		QueryResult<String> result = queryService.query(criteria);
		assertEquals("The found GUIDs should have size of 0.", 0, result.getResults().size());
	}

	/**
	 * Test finding all parameter value guids.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllGuids() {
		DynamicContent dynamicContent = getTac().getPersistersFactory().getDynamicContentDeliveryTestPersister().persistDynamicContent("dn1", "WRAPPER_ID");
		dynamicContent = getTac().getPersistersFactory().getDynamicContentDeliveryTestPersister().persistDynamicContentParameter(dynamicContent , "pn1" , "pv1", "en_US");
		dynamicContent = getTac().getPersistersFactory().getDynamicContentDeliveryTestPersister().persistDynamicContentParameter(dynamicContent, "pn2", "pv2", "en_US");
		Set<String> guids = getParameterValueGuids(dynamicContent);
		QueryCriteria<ParameterValue> criteria = CriteriaBuilder.criteriaFor(ParameterValue.class).returning(ResultType.GUID);

		QueryResult<String> result = queryService.query(criteria);
		assertEquals("The found GUIDs should have size of 2.", 2, result.getResults().size());
		assertTrue("The found GUIDs should exist in the set of created GUIDs.", guids.containsAll(result.getResults()));
	}

	private Set<String> getParameterValueGuids(final DynamicContent dynamicContent) {
		Set<String> result = new HashSet<String>();
		for (ParameterValue item : dynamicContent.getParameterValues()) {
			result.add(item.getGuid());
		}

		return result;
	}

}
