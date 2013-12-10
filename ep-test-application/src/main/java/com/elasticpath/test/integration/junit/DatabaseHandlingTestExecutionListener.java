package com.elasticpath.test.integration.junit;

import com.elasticpath.test.integration.DirtiesDatabase;
import org.apache.log4j.Logger;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * JUnit framework hook to execute the {@link DirtiesDatabase} annotation.
 */
public class DatabaseHandlingTestExecutionListener extends AbstractTestExecutionListener {
	private static final Logger LOG = Logger.getLogger(DatabaseHandlingTestExecutionListener.class);

	@Override
	public void afterTestMethod(final TestContext testContext) throws Exception {
		boolean classDirtiesDatabase = testContext.getTestClass().isAnnotationPresent(DirtiesDatabase.class);
		boolean methodDirtiesDatabase = testContext.getTestMethod().isAnnotationPresent(DirtiesDatabase.class);

		if (LOG.isDebugEnabled()) {
			LOG.debug("After test method: context [" + testContext + "], class dirties database ["
					+ classDirtiesDatabase + "], method dirties database ["
					+ methodDirtiesDatabase + "].");
		}

		if (methodDirtiesDatabase || classDirtiesDatabase) {
            DatabaseTestExecutionListenerHelper.resetDatabase(testContext);
		}
	}

	@Override
	public void beforeTestClass(final TestContext testContext) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Performing database reset for test context [" + testContext + "].");
		}

        DatabaseTestExecutionListenerHelper.resetDatabase(testContext);
	}
}