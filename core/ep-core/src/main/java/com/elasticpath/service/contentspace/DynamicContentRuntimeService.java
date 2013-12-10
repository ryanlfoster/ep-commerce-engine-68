package com.elasticpath.service.contentspace;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.tags.TagSet;

/**
 * Runtime service that provides the resolution functionality for dynamic content 
 * deliveries for the content spaces provided given the preconditions defines within tage set.
 */
public interface DynamicContentRuntimeService {

	/**
	 * resolve dynamic content deliveries for the content spaces provided given the preconditions 
	 * defines within tage set.
	 *
	 * @param tagSet the preconditions
	 * @param contentSpaceName the content space for which to resolve the deliveries
	 * @return resolved dynamic content for the given content space
	 * @throws DynamicContentResolutionException is throws in case of errors (usually when resolution does 
	 *         not return a result
	 */
	DynamicContent resolve(TagSet tagSet, String contentSpaceName) throws DynamicContentResolutionException;

}