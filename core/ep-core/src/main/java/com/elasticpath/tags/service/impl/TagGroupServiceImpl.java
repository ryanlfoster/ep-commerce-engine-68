package com.elasticpath.tags.service.impl;


import java.util.List;

import com.elasticpath.tags.dao.TagGroupDao;
import com.elasticpath.tags.domain.TagGroup;
import com.elasticpath.tags.service.TagGroupService;

/**
 * Implementation of TagDictionaryService.
 */
public class TagGroupServiceImpl implements TagGroupService {

	private TagGroupDao tagGroupDao;

	@Override
	public void delete(final TagGroup tagGroup) {
		tagGroupDao.remove(tagGroup);
	}

	@Override
	public TagGroup findByGuid(final String guid) {
		return tagGroupDao.findByGuid(guid);
	}

	@Override
	public List<TagGroup> getTagGroups() {
		return tagGroupDao.getTagGroups();
	}
	
	@Override
	public void saveOrUpdate(final TagGroup tagGroup) {
		tagGroupDao.saveOrUpdate(tagGroup);
	}
	
	/**
	 * Mainly for Spring injection.
	 * 
	 * @param tagGroupDao TagGroupDao
	 */
	public void setTagGroupDao(final TagGroupDao tagGroupDao) {
		this.tagGroupDao = tagGroupDao;
	}
}
