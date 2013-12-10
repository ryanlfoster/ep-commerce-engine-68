package com.elasticpath.tags.service.impl;

import java.util.List;

import com.elasticpath.tags.dao.TagOperatorDao;
import com.elasticpath.tags.domain.TagOperator;
import com.elasticpath.tags.service.TagOperatorService;

/**
 *Implementation of TagDefinitionService interface.
 */
public class TagOperatorServiceImpl implements TagOperatorService {

	private TagOperatorDao tagOperatorDao;

	/**
	 * TagOperatorDao injection method.
	 * @param tagOperatorDao TagOperatorDao.
	 */
	public void setTagOperatorDao(final TagOperatorDao tagOperatorDao) {
		this.tagOperatorDao = tagOperatorDao;
	}

	@Override
	public List<TagOperator> getTagOperators() {
		return tagOperatorDao.getTagOperators();
	}

	@Override
	public TagOperator findByGuid(final String guid) {
		return tagOperatorDao.findByGuid(guid);
	}
	
	
}
