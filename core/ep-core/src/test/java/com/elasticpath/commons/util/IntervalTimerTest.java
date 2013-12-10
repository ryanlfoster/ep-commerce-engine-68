package com.elasticpath.commons.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests the IntervalTimer class. 
 */
public class IntervalTimerTest {
	
	private static final int ONE_SECOND_AS_MS = 1000;
	private static final int TWO_SECONDS_AS_MS = 2000;
	
	/**
	 * Tests that an immediate call to hasIntervalPassed to a timer with a 1 second interval returns false.
	 */
	@Test
	public void testImmediateCallHasIntervalPassedReturnsFalse() {
		IntervalTimer intervalTimer = new IntervalTimer(ONE_SECOND_AS_MS);
		intervalTimer.setStartPointToNow();
		assertFalse("interval should not have elapsed.", intervalTimer.hasIntervalPassed());
	}

	/**
	 * Tests that a call to hasIntervalPassed to a timer with a 1 second interval returns true,
	 * if the current thread sleeps for 3 seconds before calling hasIntervalPassed.
	 * @throws InterruptedException if the Thread.sleep call gets interrupted (it wont)
	 */
	@Test
	public void testCallHasIntervalPassedReturnsTrueAfterWaitingLongerThanInterval() 
		throws InterruptedException {
		
		IntervalTimer intervalTimer = new IntervalTimer(ONE_SECOND_AS_MS);
		intervalTimer.setStartPointToNow();
		
		Thread.sleep(TWO_SECONDS_AS_MS); //give it a chance to elapse

		assertTrue("interval should have elapsed.", intervalTimer.hasIntervalPassed());
	}
	
	/**
	 * Tests that a call to hasIntervalPassed to a timer with a 1 second interval returns true,
	 * if the current thread sleeps for 3 seconds before calling hasIntervalPassed. 
	 * Then tests that a call to startTiming resets the countdown timer.
	 * @throws InterruptedException if the Thread.sleep call gets interrupted (it wont)
	 */
	@Test
	public void testImmediateCallHasIntervalPassedReturnsFalseOnPreviouslyElapsedTimer() 
		throws InterruptedException {
	
		IntervalTimer intervalTimer = new IntervalTimer(ONE_SECOND_AS_MS);
		intervalTimer.setStartPointToNow();
		
		Thread.sleep(TWO_SECONDS_AS_MS); //give it a chance to elapse
		
		assertTrue("interval should have elapsed.", intervalTimer.hasIntervalPassed());
		
		intervalTimer.setStartPointToNow(); //restart the interval clock

		assertFalse("interval should not have elapsed.", intervalTimer.hasIntervalPassed());
	}

}
