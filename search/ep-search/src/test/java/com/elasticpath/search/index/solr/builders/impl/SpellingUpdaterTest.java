package com.elasticpath.search.index.solr.builders.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.apache.solr.client.solrj.SolrServer;

/**
 * Tests the behaviour of ProductIndexBuilder.SpellingUpdater.
 */
public class SpellingUpdaterTest {

	//in millis
	private static final int ONE_SECOND_AS_MS = 1000;
	private static final int TWO_SECONDS_AS_MS = 2000;
	private static final int THREE_SECONDS_AS_MS = 3000;
	
	
	/**
	 * Tests that a call to IntervalSpellingUpdater.updateSpelling method will return false if
	 * the initial 'wait to rebuild' interval has not elapsed.
	 */
	@Test
	public void testUpdateSpellingDoesNotExecuteBeforeInitialIntervalHasElapsed() {
		TestProductIndexBuilder mockProductIndexBuilder  = new TestProductIndexBuilder();
		mockProductIndexBuilder.createSpellingUpdater(ONE_SECOND_AS_MS);
		//'instant' call wont have waited long enough
		boolean willUpdate = mockProductIndexBuilder.getSpellingUpdater().rebuildSpellingIndex(null);
		
		assertFalse("interval should not have elapsed.", willUpdate);
	}
	
	/**
	 * Tests that a call to IntervalSpellingUpdater.updateSpelling method will return true
	 * once the interval has elapsed.
	 * @throws InterruptedException if the Thread.sleep call gets interrupted (it wont)
	 */
	@Test
	public void testUpdateSpellingDoesExecuteAfterInitialIntervalHasElapsed() throws InterruptedException {
		TestProductIndexBuilder testProductIndexBuilder  = new TestProductIndexBuilder();
		//pass zero so that IntervalTimer.hasIntervalPassed always returns true
		testProductIndexBuilder.createSpellingUpdater(0); 
		
		boolean willUpdate = testProductIndexBuilder.getSpellingUpdater().rebuildSpellingIndex(null);
		
		assertTrue("spelling index should rebuild after interval has elapsed.", willUpdate);
	}
	
	/**
	 * Tests that a call to IntervalSpellingUpdater.updateSpelling method will return true
	 * once the interval has elapsed, but that a further call to IntervalSpellingUpdater.updateSpelling
	 * will return false. 
	 * @throws InterruptedException if the Thread.sleep call gets interrupted (it wont)
	 */
	@Test
	public void testUpdateSpellingDoesNotExecuteAgainWhilstAlreadyExecuting() throws InterruptedException {
		TestProductIndexBuilder testProductIndexBuilder  = new TestProductIndexBuilder();
		//pass zero so that IntervalTimer.hasIntervalPassed always returns true
		testProductIndexBuilder.createSpellingUpdater(0);
		
		boolean spellingIndexRebuildStarted = testProductIndexBuilder.getSpellingUpdater().rebuildSpellingIndex(null);
		
		assertTrue("spelling index should rebuild after interval has elapsed.", spellingIndexRebuildStarted);
		//the TestSpellingUpdaterRunnable Thread should run for 5 seconds (see below) so 
		//the IntervalSpellingUpdater is not eligible to run.
		spellingIndexRebuildStarted = testProductIndexBuilder.getSpellingUpdater().rebuildSpellingIndex(null);
		assertFalse("spelling index should NOT rebuild again whilst already building", spellingIndexRebuildStarted);
	}
	
	
	/**
	 * Tests that a call to IntervalSpellingUpdater.updateSpelling method will return true
	 * once the interval has elapsed, waits enough time for the Thread to run (10 secs) and
	 * that a further call to IntervalSpellingUpdater.updateSpelling
	 * will return true. 
	 * @throws InterruptedException if the Thread.sleep call gets interrupted (it wont)
	 */
	@Test
	public void testUpdateSpellingDoesExecuteAgainAfterCompletingOnceAndIntervalElapsed() throws InterruptedException {
		TestProductIndexBuilder testProductIndexBuilder  = new TestProductIndexBuilder();
		//pass zero so that IntervalTimer.hasIntervalPassed always returns true
		testProductIndexBuilder.createSpellingUpdater(0);
		
		boolean spellingIndexRebuildStarted = testProductIndexBuilder.getSpellingUpdater().rebuildSpellingIndex(null);
		
		assertTrue("spelling index should rebuild after interval has elapsed.", spellingIndexRebuildStarted);
		
		Thread.sleep(THREE_SECONDS_AS_MS); //let the thread finish
		
		//the TestSpellingUpdaterRunnable Thread should run for 2 seconds (see below) so 
		//the IntervalSpellingUpdater is now eligible to run again
		spellingIndexRebuildStarted = testProductIndexBuilder.getSpellingUpdater().rebuildSpellingIndex(null);
		assertTrue("spelling index should rebuild after interval has elapsed and one complete run.", 
				spellingIndexRebuildStarted);
	}
	
	/**
	 * Enables testing of the internal SpellingUpdater.
	 */
	class TestProductIndexBuilder extends ProductIndexBuilder {

		private SpellingUpdater spellingUpdater;
		
		/**
		 * Creates a MockIntervalSpellingUpdater using the passed in interval.
		 * @param spellCheckingIndexRebuildInterval spellCheckingIndexRebuildInterval in millis
		 */
		protected void createSpellingUpdater(final int spellCheckingIndexRebuildInterval) {
			spellingUpdater =  new TestIntervalSpellingUpdater(spellCheckingIndexRebuildInterval);
		}

		/**
		 * gets the SpellingUpdater.
		 * @return the SpellingUpdater instance
		 */
		protected SpellingUpdater getSpellingUpdater() {
			return spellingUpdater;
		}

		/**
		 * Overwrites the createRunnable with a mocked out thread that just sleeps for 10 seconds.
		 * This allows us to unit test other methods in IntervalSpellingUpdater.
		 */
		class TestIntervalSpellingUpdater extends IntervalSpellingUpdater {

			/**
			 * .
			 * @param spellingUpdateInterval spellingUpdateInterval
			 */
			public TestIntervalSpellingUpdater(final long spellingUpdateInterval) {
				super(spellingUpdateInterval);
			}
			
			/**
			 * .
			 * @param server server (ignored)
			 * @return a TestSpellingUpdaterRunnable
			 */
			protected Runnable createSpellingUpdaterRunnable(final SolrServer server) {
				return new TestSpellingUpdaterRunnable();
			}
			
			/**
			 * Sleeps for 3 seconds, mimicing a running process such as an index rebuilding.
			 */
			private class TestSpellingUpdaterRunnable implements Runnable {

				/**
				 * Nothing to see in here. Move along
				 */
				public TestSpellingUpdaterRunnable() {
					//boo! told u not look in here
				}
				
				public void run() {
					try {
						Thread.sleep(TWO_SECONDS_AS_MS);
					} catch (InterruptedException e) {
						
					}
				}
			}
		
	}
	
}	
}
