package com.elasticpath.importexport.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.importexport.common.util.Timer.Clock;

/**
 * Tests Timer class.
 */
public class TimerTest {

	private static final int ONE_SECOND = 1000;

	private static final int FIFTY_NINE_SECONDS = 59000;

	private static final int ONE_MINUTE = 60000;

	private static final int ONE_MINUTE_ONE_SECOND = 61000;

	private static final int FIFTY_NINE_MINUTES_FIFTY_NINE_SECONDS = 3599000;

	private static final int ONE_HOUR = 3600000;

	private static final int ONE_HOUR_ONE_MINUTE = 3660000;

	private static final int ONE_HOUR_ONE_MINUTE_ONE_SECOND = 3661000;

	private Timer timer;

	private MockClock clock;


	/**
	 * Sets up tests.
	 */
	@Before
	public void setUp() {
		timer = new Timer();
		clock = new MockClock();
		timer.setClock(clock);
		clock.setCurrentTimeMillis(0);
		timer.reset();
	}

	/**
	 * Tests one second elapsed.
	 */
	@Test
	public void test1Second() {
		clock.setCurrentTimeMillis(ONE_SECOND);
		assertEquals("0h0m1s", timer.getElapsedTime().toString());
	}

	/**
	 * Tests 59 seconds elapsed.
	 */
	@Test
	public void test59Seconds() {
		clock.setCurrentTimeMillis(FIFTY_NINE_SECONDS);
		assertEquals("0h0m59s", timer.getElapsedTime().toString());
	}

	/**
	 * Tests 1 minute elapsed.
	 */
	@Test
	public void test1Minute() {
		clock.setCurrentTimeMillis(ONE_MINUTE);
		assertEquals("0h1m0s", timer.getElapsedTime().toString());
	}

	/**
	 * Tests 1 minute 1 second elapsed.
	 */
	@Test
	public void testOneMinuteOneSecond() {
		clock.setCurrentTimeMillis(ONE_MINUTE_ONE_SECOND);
		assertEquals("0h1m1s", timer.getElapsedTime().toString());
	}

	/**
	 * Tests 59 minutes elapsed.
	 */
	@Test
	public void test59Minutes59Seconds() {
		clock.setCurrentTimeMillis(FIFTY_NINE_MINUTES_FIFTY_NINE_SECONDS);
		assertEquals("0h59m59s", timer.getElapsedTime().toString());
	}

	/**
	 * Tests 1 hour elapsed.
	 */
	@Test
	public void test1Hour() {
		clock.setCurrentTimeMillis(ONE_HOUR);
		assertEquals("1h0m0s", timer.getElapsedTime().toString());
	}

	/**
	 * Tests 1 hour 1 minute elapsed.
	 */
	@Test
	public void test1Hour1Minute() {
		clock.setCurrentTimeMillis(ONE_HOUR_ONE_MINUTE);
		assertEquals("1h1m0s", timer.getElapsedTime().toString());
	}

	/**
	 * Tests 1 hour 1 minute 1 second elapsed.
	 */
	@Test
	public void test1Hour1Minute1Second() {
		clock.setCurrentTimeMillis(ONE_HOUR_ONE_MINUTE_ONE_SECOND);
		assertEquals("1h1m1s", timer.getElapsedTime().toString());
	}

	/**
	 * Mock implementation.
	 */
	private static class MockClock implements Clock {

		private long millis;

		public long currentTimeMillis() {
			return millis;
		}

		public void setCurrentTimeMillis(final long millis) {
			this.millis = millis;
		}
	}
}
