package com.elasticpath.importexport.common.summary.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.summary.SummaryLogger;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.common.util.Timer;
import com.elasticpath.importexport.common.util.Timer.Time;

/**
 * The simple implementation of <code>Summary</code> interface that provides all basic operations.
 */
public class SummaryImpl implements SummaryLogger {

	private final List<Message> failures = new ArrayList<Message>();
	
	private final List<Message> warnings = new ArrayList<Message>();

	private final List<Message> comments = new ArrayList<Message>();

	private final Map<JobType, Integer> objectCounters = new TreeMap<JobType, Integer>();

	private final Timer timer = new Timer();
	
	private final Date startDate = new Date();

	private final List<Dto> failedDtos = new ArrayList<Dto>();
	
	private int addedToChangeSetCount;

	/**
	 * Adds new failure.
	 * 
	 * @param failure string which describes a failure
	 */
	public void addFailure(final Message failure) {
		failures.add(failure);
		if (failure.isJobTypeFailure()) {
			addToCounter(failure.getJobType(), -1);
		}
	}

	/**
	 * Adds new warning.

	 * @param warning string which describes a warning
	 */
	public void addWarning(final Message warning) {
		warnings.add(warning);
	}
	
	/**
	 * Adds new comment.
	 * 
	 * @param comment string which describes a comment
	 */
	public void addComment(final Message comment) {
		comments.add(comment);
	}
	
	/**
	 * Adds +1 to named counter.
	 * 
	 * @param counter JobType defines counters.
	 */
	public void addToCounter(final JobType counter) {		
		Integer count = getCount(counter);	
		this.objectCounters.put(counter, ++count);
	}
	
	@Override
	public void addToCounter(final JobType counter, final int qty) {
		Integer count = getCount(counter);
		this.objectCounters.put(counter, count + qty);
	}

	private Integer getCount(final JobType counter) {
		Integer count = this.objectCounters.get(counter);
		if (count == null) {
			count = Integer.valueOf(0);
		}
		return count;
	}

	
	@Override
	public Time getElapsedTime() {
		return timer.getElapsedTime();
	}
	
	@Override
	public String getStartDate() {
		return startDate.toString();
	}

	/**
	 * Sums and gets Total Number Of Objects.
	 *
	 * @return int Total Number Of Objects
	 */
	public int sumAllCounters() {
		int totalNumberOfObjects = 0;

		Iterator<Integer> iterator = objectCounters.values().iterator();

		while (iterator.hasNext()) {
			totalNumberOfObjects += iterator.next();
		}

		return totalNumberOfObjects;
	}

	@Override
	public List<Message> getFailures() {
		return new ArrayList<Message>(failures);
	}

	@Override
	public List<Message> getWarnings() {
		return new ArrayList<Message>(warnings);
	}
	
	@Override
	public List<Message> getComments() {
		return new ArrayList<Message>(comments);
	}

	@Override
	public Map<JobType, Integer> getCounters() {
		return objectCounters;
	}

	@Override
	public boolean failuresExist() {
		return !getFailures().isEmpty();
	}

	@Override
	public void addFailedDtos(final List<Dto> commitUnitDtos) {
		failedDtos.addAll(commitUnitDtos);
	}

	@Override
	public List<Dto> getFailedDtos() {
		return Collections.unmodifiableList(failedDtos);
	}

	@Override
	public void clearFailedDtos() {
		failedDtos.clear();
	}

	@Override
	public void addAddedToChangeSetCount(final int count) {
		this.addedToChangeSetCount += count;
	}

	@Override
	public int getAddedToChangeSetCount() {
		return this.addedToChangeSetCount;
	}
}
