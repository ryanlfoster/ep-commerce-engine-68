package com.elasticpath.cmweb.jobs.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

/**
 * Tests {@link SearchTermsAggregatorJobImpl}.
 */
public class SearchTermsAggregatorJobImplTest {

	private static final int MILLIS_PER_SECOND = 1000;

	private final SearchTermsAggregatorJobImpl job = new SearchTermsAggregatorJobImpl();
	
	/**
	 * Tests {@link SearchTermsAggregatorJobImpl#getSafeLastAccessDate(Date)}.
	 */
	//CHECKSTYLE:OFF magic numbers are our friends!
	@Test
	public void testGetSafeLastAccessDate() {
		assertEquals(createDate(32, 0), job.getSafeLastAccessDate(createDate(33, 154)));
		assertEquals(createDate(4, 0), job.getSafeLastAccessDate(createDate(5, 1)));
		assertEquals(createDate(31, 0), job.getSafeLastAccessDate(createDate(32, 0)));
	}
	//CHECKSTYLE:ON
	
	private static Date createDate(final int seconds, final int millis) {
		long time = seconds * MILLIS_PER_SECOND + millis;
		return new Date(time);
	}

}
