package com.elasticpath.search.index.pipeline.stats.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;

import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;

/**
 * A basic, thread-safe implementation of {@code PipelinePerformance} which collects various performance information for a given
 * {@code IndexingPipeline}.
 */
public class PipelinePerformanceImpl implements PipelinePerformance {

	private static final int WINDOW_SIZE = 50;

	private ConcurrentHashMap<String, DescriptiveStatistics> perfData = new ConcurrentHashMap<String, DescriptiveStatistics>();

	private ConcurrentHashMap<String, AtomicLong> counterData = new ConcurrentHashMap<String, AtomicLong>();

	/**
	 * We keep track of keys separately so that we can return them in the order they were added.
	 */
	private Set<String> perfKeys = Collections.synchronizedSet(new LinkedHashSet<String>());

	/**
	 * We keep track of keys separately so that we can return them in the order they were added.
	 */
	private Set<String> counterKeys = Collections.synchronizedSet(new LinkedHashSet<String>());

	@Override
	public void addValue(final String key, final double value) {
		if (perfData.putIfAbsent(key, new SynchronizedDescriptiveStatistics(WINDOW_SIZE)) == null) {
			perfKeys.add(key);
		}
		perfData.get(key).addValue(value);
	}

	@Override
	public DescriptiveStatistics getDescriptiveStatistics(final String key) {
		return perfData.get(key);
	}

	@Override
	public Set<String> getDescriptiveStatisticsKeys() {
		return this.perfKeys;
	}

	@Override
	public void reset() {
		this.perfData = new ConcurrentHashMap<String, DescriptiveStatistics>();
		this.counterData = new ConcurrentHashMap<String, AtomicLong>();
		this.perfKeys = Collections.synchronizedSet(new LinkedHashSet<String>());
		this.counterKeys = Collections.synchronizedSet(new LinkedHashSet<String>());
	}

	@Override
	public void addCount(final String key, final long value) {
		if (counterData.putIfAbsent(key, new AtomicLong(value)) == null) {
			counterKeys.add(key);
		} else {
			counterData.get(key).addAndGet(value);
		}
	}

	@Override
	public Set<String> getCounterKeys() {
		return this.counterKeys;
	}

	@Override
	public long getCounter(final String key) {
		return this.counterData.get(key).get();
	}

}
