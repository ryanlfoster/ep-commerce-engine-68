package com.elasticpath.tools.sync.job.transaction;

/**
 * Transaction attributes.
 */
public enum TransactionAttribute {
	
	/**
	 * Use one transaction per job.
	 */
	JOB,
	
	/**
	 * Use one transaction per job entry.
	 */
	JOBENTRY,
	
	/**
	 * Use one transaction per job entry type.
	 */
	JOBENTRY_TYPE,
	
	/**
	 * Use one transaction group by job entry type and command.
	 */
	JOBENTRY_TYPE_AND_COMMAND,
	
	/**
	 * Use one transaction per given quantity of job entries.
	 */
	JOBENTRY_QTY
	
}
