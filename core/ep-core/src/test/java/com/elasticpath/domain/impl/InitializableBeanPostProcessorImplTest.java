package com.elasticpath.domain.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.Initializable;

/**
 * Tests for {@link InitializableBeanPostProcessorImpl}.
 */
public class InitializableBeanPostProcessorImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Ensure that {@link Initializable#initialize()} is called.
	 */
	@Test
	public void ensurePostProcessBeforeInitialization() throws Exception {
		final Initializable mockPersistence = context.mock(Initializable.class);

		context.checking(new Expectations() {
			{
				oneOf(mockPersistence).initialize();
			}
		});
		InitializableBeanPostProcessorImpl postProcessor = new InitializableBeanPostProcessorImpl();
		postProcessor.postProcessBeforeInitialization(mockPersistence, "arbitrary-bean-name");
	}
}
