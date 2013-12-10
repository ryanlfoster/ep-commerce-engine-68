package com.elasticpath.tags.service.impl;

import java.util.List;

import com.elasticpath.tags.dao.TagValueTypeDao;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.TagValueTypeService;

/**
 *Implementation of TagValueTypeService interface.
 */
public class TagValueTypeServiceImpl implements TagValueTypeService {

	private TagValueTypeDao tagValueTypeDao;

	/**
	 * TagValueTypeDao injection method.
	 * @param tagValueTypeDao TagValueTypeDao.
	 */
	public void setTagValueTypeDao(final TagValueTypeDao tagValueTypeDao) {
		this.tagValueTypeDao = tagValueTypeDao;
	}

	@Override
	public List<TagValueType> getTagValueTypes() {
		return tagValueTypeDao.getTagValueTypes();
	}

	@Override
	public TagValueType findByGuid(final String guid) {
		return tagValueTypeDao.findByGuid(guid);
	}

	
}
