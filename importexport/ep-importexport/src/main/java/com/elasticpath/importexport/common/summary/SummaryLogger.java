package com.elasticpath.importexport.common.summary;

import com.elasticpath.importexport.common.util.Message;

/**
 * Represents interface for handling error messages and comments.
 */
public interface SummaryLogger extends Summary {
	
	/**
	 * Adds new failure.

	 * @param failure string which describes a failure
	 */
	void addFailure(final Message failure);

	/**
	 * Adds new warning.

	 * @param warning string which describes a warning
	 */
	void addWarning(final Message warning);
	
	
	/**
 	 * Adds new comment.
 	 * 
	 * @param comment string which describes a comment
	 */
	void addComment(final Message comment);

}
