package com.elasticpath.tags.service;

import java.util.List;

import com.elasticpath.tags.domain.TagGroup;

/**
 * The service interface for TagGroup domain object.
 *
 */
public interface TagGroupService {
	
	/**
	 * Save or update the TagGroup object.
	 * 
	 * @param tagGroup tagGroup.
	 */
	void saveOrUpdate(TagGroup tagGroup);
	
	
	/**
	 * Find TagGroups by their name.
	 * 
	 * @param guid the guid of the TagGroup.
	 * @return a TagGroup
	 */
	TagGroup findByGuid(String guid);
	
	
	/**
	 * List TagGroups.
	 * 
	 * @return a list of TagGroups
	 */
	List<TagGroup> getTagGroups();
	
	
	/**
	 * Delete tagGroup by its instance.
	 * 
	 * @param tagGroup tagGroup.
	 */
	void delete(TagGroup tagGroup);

}
