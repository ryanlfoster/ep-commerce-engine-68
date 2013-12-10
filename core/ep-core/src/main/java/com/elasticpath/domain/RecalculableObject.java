package com.elasticpath.domain;

/**
 * Represents an object that supports auto-recalculation.
 */
public interface RecalculableObject {

	/**
	 * Enables auto-recalculation. Can be used as a callback method for JPA so that recalculation is automatically enabled after loading the object
	 * from the database.
	 */
	void enableRecalculation();

	/**
	 * Enables auto-recalculation. Can be used as a callback method for JPA so that recalculation isn't triggered during merging of the object to the
	 * database.
	 */
	void disableRecalculation();
}