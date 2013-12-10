package com.elasticpath.domain.shopper;

import com.elasticpath.tags.TagSet;

/**
 * Provides access to {@link TagSet}.
 */
public interface TagSource {

	/**
	 * Retrieves a {@link TagSet} for use.
	 *
	 * @return a {@link TagSet}.
	 */
	TagSet getTagSet();
	
}
