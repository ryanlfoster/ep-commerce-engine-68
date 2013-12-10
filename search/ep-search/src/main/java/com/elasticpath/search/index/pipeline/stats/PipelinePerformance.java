package com.elasticpath.search.index.pipeline.stats;

import java.util.Set;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * Keeps track of the various performance metrics for a single {@code IndexingPipeline}. It is held by the one and only {@code IndexingStatistics}
 * implementor. There are two types of numbers that can be kept track of: counters and statistics. Counters are generally only incremented (and
 * backed by {@code AtomicLong}). Statistics are done in a sliding-window sense and the min/avg/max is displayed. These are backed by
 * {@code DescriptiveStatistics}.
 */
public interface PipelinePerformance {

	/**
	 * There is a {@code DescriptiveStatistics} associated with each key. This method will add the specified value. Note that
	 * {@code DescriptiveStatistics} is "windowed" and the oldest value will be dropped when appropriate. The {@code DescriptiveStatistics} will be
	 * created if it does not exist.
	 * <p>
	 * This method must be thread safe.
	 * <p>
	 * 
	 * @param key a String used to identify the statistics being kept.
	 * @param value A meaningful number which could be averaged/mean/max/min'd/etc..
	 */
	void addValue(String key, double value);

	/**
	 * Exposes the underlying {@code DescriptiveStatistics} for the given key.
	 * 
	 * @param key A key.
	 * @return A {@code DescriptiveStatistics}, probably a {@code SynchronizedDescriptiveStatistics}.
	 */
	DescriptiveStatistics getDescriptiveStatistics(String key);

	/**
	 * Return all the keys in the {@code PipelinePerformance} which have {@code DescriptiveStatistics}.
	 * 
	 * @return a unique set of keys
	 */
	Set<String> getDescriptiveStatisticsKeys();

	/**
	 * Add the given value to the named counter. If the counter does not exist, it is created and initialized to the given value. You can forcefully
	 * initialize a counter by passing in 0 for value.
	 * 
	 * @param key to identify the counter you're looking for. It is unique per {@code IndexingPipeline}.
	 * @param value how much to increment the specified counter by. Please don't pass negative numbers here.
	 */
	void addCount(String key, long value);

	/**
	 * Return all the keys in the {@code PipelinePerformance} which are counters.
	 * 
	 * @return unique set of keys
	 */
	Set<String> getCounterKeys();

	/**
	 * Return a read-only value of the current counter.
	 * <p>
	 * This code will {@code NullPointerException} if the given counter doesn't exist. {@see #addCount(String, long)} on how to initialize counters
	 * you may want.
	 * 
	 * @param key the counter's name
	 * @return the value of the current counter.
	 */
	long getCounter(String key);

	/**
	 * Erase all the collected values <b>and the associated keys</b>.
	 */
	void reset();

}
