package com.elasticpath.test.db;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.service.misc.TimeService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test that the time service is able to get a time from the DB.
 */
public class TimeServiceTest extends DbTestCase {

	@Autowired
	private TimeService timeService;

	/**
	 * Test product category save.
	 */
	@DirtiesDatabase
	@Test
	public void testProductCategorySave() {
		final Date date = timeService.getCurrentTime();
		assertNotNull(date);
	}
}
