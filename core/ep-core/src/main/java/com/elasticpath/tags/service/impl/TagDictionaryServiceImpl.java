package com.elasticpath.tags.service.impl;


import java.util.List;

import com.elasticpath.tags.dao.TagDictionaryDao;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.TagDictionaryService;

/**
 * Implementation of TagDictionaryService.
 * @author fwang
 *
 */
public class TagDictionaryServiceImpl implements TagDictionaryService {

	private TagDictionaryDao tagDictionaryDao;

	@Override
	public void delete(final TagDictionary tagDictionary) {
		tagDictionaryDao.remove(tagDictionary);
		
	}

	@Override
	public TagDictionary findByGuid(final String guid) {
		return tagDictionaryDao.findByGuid(guid);
	}

	@Override
	public List<TagDictionary> getTagDictionaries() {
		return tagDictionaryDao.getTagDictionaries();
	}
	
	@Override
	public void saveOrUpdate(final TagDictionary tagDictonary) {
		tagDictionaryDao.saveOrUpdate(tagDictonary);
	}
	
	/**
	 * Mainly for Spring injection.
	 * 
	 * @param tagDictionaryDao TagDictionaryDao
	 */
	public void setTagDictionaryDao(final TagDictionaryDao tagDictionaryDao) {
		this.tagDictionaryDao = tagDictionaryDao;
	}
}
