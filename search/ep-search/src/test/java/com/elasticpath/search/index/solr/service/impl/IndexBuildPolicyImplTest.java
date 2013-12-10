/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.search.index.solr.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang.time.DateUtils;

import com.elasticpath.search.index.solr.service.IndexBuildPolicyContext;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test cases for {@link IndexBuildPolicyImpl}.
 */
public class IndexBuildPolicyImplTest {

	private IndexBuildPolicyImpl indexPolicy;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TimeService timeService;

	private IndexBuildPolicyContext indexContext;

	private SettingsReader settingsReader;
	
	private SettingValue settingValue;
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() {
		indexPolicy = new IndexBuildPolicyImpl();
		
		timeService = context.mock(TimeService.class);
		settingsReader = context.mock(SettingsReader.class);
		indexContext = new IndexBuildPolicyContext();
		
		indexContext.setIndexType(IndexType.CATEGORY);
		
		settingValue = context.mock(SettingValue.class);
		
		indexPolicy.setTimeService(timeService);
		indexPolicy.setSettingsReader(settingsReader);
	}

	/**
	 * Tests the happy case of isOptimizationRequired() when no check has been performed.
	 * The expectation is that optimization won't be triggered on the first check.
	 */
	@Test
	public void testIsOptimizationRequired() {
		context.checking(new Expectations() { {
			oneOf(settingsReader).getSettingValue(IndexBuildPolicyImpl.SETTING_OPTIMIZATION_INTERVAL, IndexType.CATEGORY.getIndexName());
			will(returnValue(settingValue));
			oneOf(settingValue).getValue();
			will(returnValue("1"));
			
			exactly(2).of(timeService).getCurrentTime();
			will(returnValue(new Date()));
		} });
		
		assertFalse("Timeout has not elapsed. Should not be eligibile for optimization", 
				indexPolicy.isOptimizationRequired(indexContext));
	}

	/**
	 * Tests the happy case of isOptimizationRequired() when a check has already 
	 * been performed and another one is done some time after the timeout is expected to have elapsed.
	 */
	@Test
	public void testIsOptimizationRequiredWhenTimeoutHasElapsed() {
		context.checking(new Expectations() { {
			oneOf(settingsReader).getSettingValue(IndexBuildPolicyImpl.SETTING_OPTIMIZATION_INTERVAL, IndexType.CATEGORY.getIndexName());
			will(returnValue(settingValue));
			oneOf(settingValue).getValue();
			will(returnValue("1"));
			
			exactly(2).of(timeService).getCurrentTime();
			will(returnValue(DateUtils.addMinutes(new Date(), 0)));
		} });
		
		
		assertFalse("Timeout has not elapsed. Should not be eligibile for optimization", 
				indexPolicy.isOptimizationRequired(indexContext));
		
		// the next expectations are set in the future - 2 seconds after the first call
		context.checking(new Expectations() { {
			oneOf(settingsReader).getSettingValue(IndexBuildPolicyImpl.SETTING_OPTIMIZATION_INTERVAL, IndexType.CATEGORY.getIndexName());
			will(returnValue(settingValue));
			oneOf(settingValue).getValue();
			will(returnValue("1"));

			// first invocation is for the check
			oneOf(timeService).getCurrentTime();
			will(returnValue(DateUtils.addMinutes(new Date(), 2)));

			// the second is for setting the last last optimization check
			oneOf(timeService).getCurrentTime();
			will(returnValue(DateUtils.addMinutes(new Date(), 2)));
		} });
		assertTrue("Timeout must have elapsed. Should be eligibile for optimization", 
				indexPolicy.isOptimizationRequired(indexContext));
		
	}

}
