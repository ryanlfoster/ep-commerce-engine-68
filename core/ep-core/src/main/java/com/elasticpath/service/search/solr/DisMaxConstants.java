package com.elasticpath.service.search.solr;

/**
 * Parameters that can be passed to the EpDismaxHandler.
 */
@SuppressWarnings("PMD.AvoidConstantsInterface")
public interface DisMaxConstants {
	/** Boolean whether to do a fuzzy search. */
	String FUZZY = "fuzzy";

	/** Set of fields to use prefix queries on. */
	String PREFIX_FIELDS = "prefixFields";

	/** Integer specifying the prefix length of fuzzy searches, if enabled. */
	String MINIMUM_LENGTH = "prefixLength";

	/** Float specifying the minimum similarity of fuzzy searches, if enabled. */
	String MINIMUM_SIMILARITY = "minimumSimilarity";
}
