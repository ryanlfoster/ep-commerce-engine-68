/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tags.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.ConditionBuilder;
import com.elasticpath.tags.service.TagDefinitionService;

/**
 * Builds proper {@link Condition} by looking up the {@link TagDefinition} and its data type.
 */
public class ConditionBuilderImpl implements ConditionBuilder {
	
	private static final Logger LOG = Logger.getLogger(ConditionBuilderImpl.class);
	
	private TagDefinitionService tagDefinitionService;
		
	private static final int DEFAULT_CACHE_TIMEOUT = 60000;
	
	private SimpleTimeoutCache<String, TagDefinition> allTags = new SimpleTimeoutCache<String, TagDefinition>(DEFAULT_CACHE_TIMEOUT);
	
	
	/**
	 * Builds the {@link Condition} object by using given arguments. Downcast the value according to {@link TagDefinition}
	 * data type.
	 * 
	 * @param tagDefinitionName the name of the {@link TagDefinition}
	 * @param operator the operator
	 * @param value the value of the condition - right operand
	 * @return the built Condition (returns null if any of the parameters supplied are null)
	 * @throws IllegalArgumentException if any of the arguments is null or if java type cannot
	 *                                  be looked up for the tag definition or if value provided
	 *                                  does not match the java type in tag definition
	 */
	public Condition build(final String tagDefinitionName, final String operator, final Object value)
		throws IllegalArgumentException {
		
		if (tagDefinitionName == null || operator == null || value == null) {
			String message = "Condition [" + tagDefinitionName + "," + operator + ",";
			if (value == null) {
				message += "null";
			} else {
				message += "(" + value.getClass().getName() + ") " + value.toString();
			}
			message += "] cannot be created since all fields are mandatory and cannot be null";
			LOG.error(message);
				
			throw new IllegalArgumentException(message);
		}
		
		final TagDefinition tagDefinition = lookUpTagDefinition(tagDefinitionName);

		if (tagDefinition == null || tagDefinition.getValueType() == null 
				|| StringUtils.isBlank(tagDefinition.getValueType().getJavaType())) {
			final String message = "TagDefinition [" + tagDefinitionName + "] is missing a java type or contains an invalid java type";
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
		
		final String javaType = tagDefinition.getValueType().getJavaType();
		if (value.getClass().getName().equals(javaType)) {
			return new Condition(tagDefinition, operator, value);
		}
		
		final String message = "Condition [" + tagDefinitionName + "," + operator 
			+ ",(" + value.getClass().getName() + ") " + value.toString() + "] cannot be created since java type of value is wrong (require '" 
			+  javaType + "')";
		LOG.error(message);
				
		throw new IllegalArgumentException(message);

	}

	private TagDefinition lookUpTagDefinition(final String tagDefinitionName) {
		if (allTags.get(tagDefinitionName) == null) {
			cacheAllTags();
		}
		return allTags.get(tagDefinitionName);
		
	}
	
	private void cacheAllTags() {
		List<TagDefinition> allTagsInDb = getAllTagDefinitions();
		
		allTags = new SimpleTimeoutCache<String, TagDefinition>(DEFAULT_CACHE_TIMEOUT);
		
		for (TagDefinition tagDefinition : allTagsInDb) {
			allTags.put(tagDefinition.getGuid(), tagDefinition);
		}
		
	}
	
	private List<TagDefinition> getAllTagDefinitions() {
		return tagDefinitionService.getTagDefinitions();
	}

	/**
	 * Setter injection.
	 * 
	 * @param tagDefinitionService the tag definition service
	 */
	public void setTagDefinitionService(final TagDefinitionService tagDefinitionService) {
		this.tagDefinitionService = tagDefinitionService;
	}
	
	/**
	 * Set the cache timeout for tag definitions.
	 * @param timeout time in milliseconds
	 */
	public void setTagDefinitionCacheTimeout(final long timeout) {
		allTags.setTimeout(timeout);
	}
}
