package com.elasticpath.service.search.impl;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.persistence.dao.IndexBuildStatusDao;

/**
 * Test case for <code>IndexBuildStatusServiceImpl</code>.
 */
public class IndexBuildStatusServiceImplTest {

	private IndexBuildStatusServiceImpl buildStatusService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private IndexBuildStatusDao mockBuildStatusDao;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		buildStatusService = new IndexBuildStatusServiceImpl();
		mockBuildStatusDao = context.mock(IndexBuildStatusDao.class);
		buildStatusService.setBuildStatusDao(mockBuildStatusDao);
	}

	/**
	 * Test for method IndexBuildStatusServiceImpl.getIndexBuildStatuses)().
	 */
	@Test
	public void testGetIndexBuildStatuses() {
		final List<IndexBuildStatus> result = new ArrayList<IndexBuildStatus>();
		context.checking(new Expectations() {
			{
				oneOf(mockBuildStatusDao).list();
				will(returnValue(result));
			}
		});
		assertSame(result, buildStatusService.getIndexBuildStatuses());
	}
}
