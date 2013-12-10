package com.elasticpath.tags.service.impl;

import java.util.List;

import com.elasticpath.tags.dao.TagDefinitionDao;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;
import com.elasticpath.tags.service.TagDefinitionService;

/**
 *Implementation of TagDefinitionService interface.
 */
public class TagDefinitionServiceImpl implements TagDefinitionService {

	private TagDefinitionDao tagDefinitionDao;

	/**
	 * TagDefinitionDao injection method.
	 * @param tagDefinitionDao TagDefinitionDao.
	 */
	public void setTagDefinitionDao(final TagDefinitionDao tagDefinitionDao) {
		this.tagDefinitionDao = tagDefinitionDao;
	}

	@Override
	public void saveOrUpdate(final TagDefinition tagDefinition) {
		tagDefinitionDao.saveOrUpdate(tagDefinition);
		
	}

	@Override
	public List<TagDefinition> getTagDefinitions() {
		return tagDefinitionDao.getTagDefinitions();
	}

	@Override
	public TagDefinition findByGuid(final String guid) {
		return tagDefinitionDao.findByGuid(guid);
	}

	@Override
	public void delete(final TagDefinition tagDefinition) {
		tagDefinitionDao.remove(tagDefinition);
		
	}
	@Override
	public TagDefinition findByName(final String name) {
		return tagDefinitionDao.findByName(name);
	}

	@Override
	public List<TagDefinition> getTagDefinitionsByTagGroup(final TagGroup group) {
		return tagDefinitionDao.getTagDefinitionsByTagGroup(group);
	}
	
}
