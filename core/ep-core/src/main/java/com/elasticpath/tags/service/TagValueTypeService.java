package com.elasticpath.tags.service;

import java.util.List;

import com.elasticpath.tags.domain.TagValueType;

/**
 * Service interface for TagValueType domain object.
 */
public interface TagValueTypeService {

	/**
	 * get all tag definitions.
	 * @return a list of tag value type.
	 */
	List<TagValueType> getTagValueTypes();

	/**
	 * Find a tag value type by its guid.
	 * @param guid the guid of the tag value type.
	 * @return the tag value type.
	 */
	TagValueType findByGuid(String guid);
	
}