package com.elasticpath.service.search.solr;

import org.apache.solr.common.params.CommonParams;

/**
 * Constants for the EP Spelling handler.
 */
@SuppressWarnings("PMD.AvoidConstantsInterface")
public interface SpellingConstants {

	/**
	 * Parameters that can be passed to the EP spelling handler.
	 */
	interface SpellingParams {
		/** Query that is used. */
		String QUERY = CommonParams.Q;

		/** Suggestions for this locale. */
		String LOCALE = "locale";

		/** Commands to be sent. */
		String CMD = "cmd";

		/** Accuracy of spelling suggestions. */
		String ACCURACY = "accuracy";

		/** Number of spelling suggestions. */
		String NUM_SUGGESTIONS = "suggestionCount";
	}
	
	/**
	 * Commands that can be passed to the EP spelling handler.
	 */
	interface SpellingCmds {
		/** Command to rebuild the dictionary. */
		String REBUILD = "rebuild";
		
		/** Command to re-open a spellchecker. */
		String REOPEN = "reopen";
	}
}
