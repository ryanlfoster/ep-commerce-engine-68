package com.elasticpath.importexport.common.util;

/**
 * Operation timer class, records start and calculates elapsed time for the operation.
 */
public final class Timer {

	private long start;

	private Clock clock;

	/**
	 * Constructor, resets the timer.
	 */
	public Timer() {
		reset();
	}

	/**
	 * Resets the timer.
	 */
	public void reset() {
		start = getClock().currentTimeMillis();
	}

	/**
	 * Gives the millis time since the Timer was last reset.
	 * 
	 * @return millis time
	 */
	public Time getElapsedTime() {
		return new Time(getClock().currentTimeMillis() - start);
	}

	private Clock getClock() {
		if (clock == null) {
			clock = new SystemClock();
		}
		return clock;
	}

	/**
	 * Overrides the system clock.
	 * 
	 * @param clock clock
	 */
	public void setClock(final Clock clock) {
		this.clock = clock;
	}

	/**
	 * Clock interface.
	 */
	public interface Clock {

		/**
		 * Returns the current time in milliseconds since Jan 1 1970.
		 * 
		 * @return current time
		 */
		long currentTimeMillis();
	}

	/**
	 * System clock implementation.
	 */
	public static class SystemClock implements Clock {

		/**
		 * Returns the current time in milliseconds since Jan 1 1970.
		 * 
		 * @return current time
		 */
		public long currentTimeMillis() {
			return System.currentTimeMillis();
		}
	}

	/**
	 * Time representation.
	 */
	public static class Time {

		private static final int ONE_THOUSAND = 1000;

		private static final int SIXTY = 60;

		private final int secondsPart;

		private final int minutesPart;

		private final int hoursPart;

		/**
		 * Constructor.
		 * 
		 * @param millis elapsed time in milliseconds
		 */
		public Time(final long millis) {
			long tmp = millis;
			this.secondsPart = (int) (tmp / ONE_THOUSAND) % SIXTY;
			this.minutesPart = (int) (tmp / ONE_THOUSAND / SIXTY) % SIXTY;
			this.hoursPart = (int) (tmp / ONE_THOUSAND / SIXTY / SIXTY);
		}

		/**
		 * Returns hoursPart part.
		 * 
		 * @return hoursPart
		 */
		public int getHoursPart() {
			return hoursPart;
		}

		/**
		 * Returns minutesPart part.
		 * 
		 * @return minutesPart
		 */
		public int getMinutesPart() {
			return minutesPart;
		}

		/**
		 * Returns secondsPart part.
		 * 
		 * @return secondsPart
		 */
		public int getSecondsPart() {
			return secondsPart;
		}

		/**
		 * toString() method.
		 * 
		 * @return String
		 */
		public String toString() {
			StringBuilder time = new StringBuilder();
			time.append(hoursPart).append("h");
			time.append(minutesPart).append("m");
			time.append(secondsPart).append("s");
			return time.toString();
		}
	}

}
