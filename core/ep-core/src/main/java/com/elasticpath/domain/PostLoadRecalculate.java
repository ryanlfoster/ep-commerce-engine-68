package com.elasticpath.domain;


/**
 * Defines classes that may need some recalculation after load.
 */
public interface PostLoadRecalculate {

	/**
	 * Perform recalculations after loading the object.
	 */
	void recalculateAfterLoad();

}