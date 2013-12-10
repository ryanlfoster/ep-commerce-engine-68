package com.elasticpath.importexport.common.types;

/**
 * Job Type determines type of optional export/import operations.
 * Each Job Type reflects type of complex object to be processed.
 * So appropriate classes can be used for execution of the job according to its type.
 */
public enum OptionalJobType {

	/** Optional job type for Assets. */
	ASSETS(JobType.ASSETS),
	
	/** Optional job type for Inventory. */
	INVENTORY(JobType.INVENTORY),
	
	/** Optional job type for Pricing. */
	PRICING(JobType.PRICING),
	
	/** Optional job type for ProductAssociations. */
	PRODUCTASSOCIATION(JobType.PRODUCTASSOCIATION),

	/** Job Type for Condition Rules. */
	CONDITIONRULE(JobType.CONDITIONRULE),
	
	/** Job Type for Coupon Set. */
	COUPONSET(JobType.COUPONSET),
	
	/** Job Type for Base Amount. */
	BASEAMOUNT(JobType.BASEAMOUNT);
	
	private JobType jobType;
	
	private OptionalJobType(final JobType jobType) {
		this.jobType = jobType;
	}
	
	/**
	 * Gets the job type.
	 * 
	 * @return the job type
	 */
	public JobType getJobType() {
		return jobType;
	}
}
