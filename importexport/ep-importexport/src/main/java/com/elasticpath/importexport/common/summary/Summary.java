package com.elasticpath.importexport.common.summary;

import java.util.List;
import java.util.Map;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.common.util.Timer.Time;


/**
 * Interface for Summary object that contains statistics for import and export operations.
 */
public interface Summary {

	/**
	 * The name of summary file.
	 */
	String SUMMARY = "summary.log"; 
	
	/**
	 * Adds +1 to named counter.
	 *
	 * @param counter JobType defines counters.
	 */
	void addToCounter(final JobType counter);
	
	/**
	 * Adds +qty to named counter.
	 *
	 * @param counter JobType defines counters.
	 * @param qty the qty to add
	 */
	void addToCounter(final JobType counter, final int qty);

	
	/**
	 * Gets Elapsed Time of work.
	 * 
	 * @return Time
	 */
	Time getElapsedTime();
	
	/**
	 * Gets Start Date.
	 * 
	 * @return String which contains start Date
	 */	
	String getStartDate();

	/**
	 * Sums and gets Total Number Of Objects.
	 *
	 * @return int Total Number Of Objects
	 */
	int sumAllCounters();
	
	
	/**
	 * Gets List of Failures.
	 *
	 * @return List of Failure Strings
	 */
	List<Message> getFailures();
	
	/**
	 * Gets List of Warnings.
	 *
	 * @return List of Warnings Strings
	 */
	List<Message> getWarnings();
	
	/**
	 * Gets List of Comments.
	 *
	 * @return List of Comment Strings
	 */
	List<Message> getComments();
	
	/**
	 * Gets Map of Object Counters.
	 *
	 * @return Map with a Key=JobType and a Value=Count
	 */
	Map<JobType, Integer> getCounters();
	
	/**
	 * Check whether failures occurred during export job execution or not.
	 * 
	 * @return true if at least one failure occurred
	 */
	boolean failuresExist();

	/**
	 * Adds Dtos which where not imported by some reason.
	 * 
	 * @param commitUnitDtos a list contained failed Dtos
	 */
	void addFailedDtos(List<Dto> commitUnitDtos);
	
	/**
	 * Returns a list of failed Dtos.
	 * 
	 * @return the failedDtos
	 */
	List<Dto> getFailedDtos();
	
	/**
	 * Clears a list of failed Dtos.
	 */
	void clearFailedDtos();

	/**
	 * Adds to the total number of objects added to change set.
	 * 
	 * @param count the number of objects added to change set
	 */
	void addAddedToChangeSetCount(int count);

	/**
	 * The count of objects added to change set.
	 * 
	 * @return the number of objects added to change set
	 */
	int getAddedToChangeSetCount();
}
