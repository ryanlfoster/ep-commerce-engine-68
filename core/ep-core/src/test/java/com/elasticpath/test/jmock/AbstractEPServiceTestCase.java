package com.elasticpath.test.jmock;


import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.FetchPlanHelper;

/**
 * A parent class for typical service tests that provides a mock <code>PersistenceEngine</code>
 * and a mock <code>FetchPlanHelper</code>.
 */
public abstract class AbstractEPServiceTestCase extends AbstractEPTestCase {


	private PersistenceEngine mockPersistenceEngine;
	
	private PersistenceEngine persistenceEngine;

	private FetchPlanHelper mockFetchPlanHelper;

	private FetchPlanHelper fetchPlanHelper;

	/**
	 * Sets up the mock persistence engine with no expectations.
	 * 
	 * @throws Exception if something goes wrong during set up.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		persistenceEngine = mockPersistenceEngine;

		mockFetchPlanHelper = context.mock(FetchPlanHelper.class);
		fetchPlanHelper = mockFetchPlanHelper;
	}
	
	/**
	 * Returns the <code>Mock</code> instance of the <code>PersistenceEngine</code>.
	 * 
	 * @return the <code>Mock</code> instance of the <code>PersistenceEngine</code>.
	 */
	protected PersistenceEngine getMockPersistenceEngine() {
		return mockPersistenceEngine;
	}
	
	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}
	
	/**
	 * @return the <code>Mock</code> instance of the <code>FetchPlanHelper</code>.
	 */
	protected FetchPlanHelper getMockFetchPlanHelper() {
		return mockFetchPlanHelper;
	}
	
	/**
	 * @return the <code>FetchPlanHelper</code>.
	 */
	protected FetchPlanHelper getFetchPlanHelper() {
		return fetchPlanHelper;
	}
	
	
}
