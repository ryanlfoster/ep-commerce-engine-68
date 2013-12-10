package com.elasticpath.tags.service.impl;

import java.util.List;

import com.elasticpath.tags.dao.ConditionalExpressionDao;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;

/**
 *Implementation of TagConditionService. 
 *
 */
public class TagConditionServiceImpl implements TagConditionService {

	private ConditionalExpressionDao tagConditionDao;


	@Override
	public void delete(final ConditionalExpression condition) {
		tagConditionDao.remove(condition);
		
	}

	@Override
	public ConditionalExpression findByGuid(final String guid) {
		return tagConditionDao.findByGuid(guid);
	}

	@Override
	public ConditionalExpression findByName(final String name) {
		return tagConditionDao.findByName(name);
	}

	@Override
	public ConditionalExpression saveOrUpdate(final ConditionalExpression condition) {
		return tagConditionDao.saveOrUpdate(condition);		
	}
	
	@Override
	public List<ConditionalExpression> getTagConditions() {
		return tagConditionDao.getConditions();
	}
	
	@Override
	public List<ConditionalExpression> getNamedTagConditions() {
		return tagConditionDao.getNamedConditions();
	}
	
	@Override	
	public List<ConditionalExpression> getNamedConditions(final String tagDictionaryGuid) {
		return tagConditionDao.getNamedConditions(tagDictionaryGuid);
	}
	
	
	/**
	 *Tag coditionDao injection method.
	 * @param tagConditionDao tag condition.
	 */
	public void setTagConditionDao(final ConditionalExpressionDao tagConditionDao) {
		this.tagConditionDao = tagConditionDao;
	}
	
	@Override	
	public List<ConditionalExpression> getNamedConditionsByNameTagDictionaryConditionTag(
			final String name,
			final String tagDictionaryGuid,
			final String tag) {
		
		return tagConditionDao.getNamedConditionsByNameTagDictionaryConditionTag(name, tagDictionaryGuid, tag);
		
	}
	
	@Override	
	public List<ConditionalExpression> getNamedConditionsByNameTagDictionaryConditionTagSellingContext(
			final String name,
			final String tagDictionaryGuid,
			final String tag,
			final String sellingContextGuid
    		) {
		return tagConditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext(name, tagDictionaryGuid, tag, sellingContextGuid);
	}
	

	

}
