package com.elasticpath.tags.service;

import java.util.List;

import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;

/**
 * Service interface for TagDefinition domain object.
 */
public interface TagDefinitionService {

	/**
	 * Create or update a TagDefinition.
	 * @param tagDefinition the tag definition.
	 */
	void saveOrUpdate(TagDefinition tagDefinition);

	/**
	 * get all tag definitions.
	 * @return a list of tag definitions.
	 */
	List<TagDefinition> getTagDefinitions();

	/**
	 * Find a tag definition by its guid.
	 * @param guid the guid of the tag definition.
	 * @return the tag definition.
	 */
	TagDefinition findByGuid(String guid);

	/**
	 * Delete the tag definition.
	 * @param tagDefinition tag definition.
	 */
	void delete(TagDefinition tagDefinition);
	
	/**
	 * Find a tag definition by its name.
	 * @param name the name of the tag definition.
	 * @return the tag definition.
	 */
	TagDefinition findByName(String name);
	
	/**
	 * Gets a list of tag definitions that belong to the specified group.
	 * @param group tag group
	 * @return a list of tag definitions
	 */
	List<TagDefinition> getTagDefinitionsByTagGroup(TagGroup group); 
	
}
