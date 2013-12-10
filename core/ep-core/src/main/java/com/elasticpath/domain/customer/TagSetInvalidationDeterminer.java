package com.elasticpath.domain.customer;

/**
 * 
 * Determinate invalidation of cache which depends on tag set changes. 
 *
 */
public interface TagSetInvalidationDeterminer {
	
	/** 
	 * @param tagDefinitionGuid guid of tag definition.
	 * @return true if need to invalidate.
	 */
	boolean needInvalidate(final String tagDefinitionGuid);	

}
