package com.elasticpath.service.misc.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.service.misc.TimeService;
/**
 * Test the cached database server time service wrapper class. 
 */
public class CachedSynchingServerTimeServiceImplTest {

	@Rule
	public final JUnitRuleMockery mockery = new JUnitRuleMockery();

	private final TimeService timeService = mockery.mock(TimeService.class);

	/**
	 * Test that the cached database server time service accommodates for drift between app server and db server.
	 * If the time is off (drift) by 500000000 milliseconds, then subsequently returned time from the app server should be
	 * its system time + drift.
	 */
	@Test
	public void testCachedGetCurrentTime() {
		final long dbTime = 1000000000;
		final long drift = 500000000;
		
		//DB time
		mockery.checking(new Expectations() { {
			oneOf(timeService).getCurrentTime(); will(returnValue(new Date(dbTime)));
		} });
		
		//add drift to app server system time
		final long systemTime = dbTime + drift;
		//drift should be 10
		final CachedSyncingServerTimeServiceImpl cachedservice = new CachedSyncingServerTimeServiceImpl() {
			@Override
			public long getSystemTime() {
				return systemTime;
			}
		};
		
		cachedservice.setCacheTimeout(2);
		cachedservice.setWrappedTimeService(timeService);
		
		assertEquals(dbTime, cachedservice.getCurrentTime().getTime());

	}

}
