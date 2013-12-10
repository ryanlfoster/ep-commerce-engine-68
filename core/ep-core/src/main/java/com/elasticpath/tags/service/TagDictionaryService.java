package com.elasticpath.tags.service;

import java.util.List;

import com.elasticpath.tags.domain.TagDictionary;

/**
 * The service interface for TagDictionary domain object.
 *
 */
public interface TagDictionaryService {
	
	/**
	 * Save or update the TagDictionary object.
	 * 
	 * @param tagDictonary tagDictionary.
	 */
	void saveOrUpdate(TagDictionary tagDictonary);
	
	
	/**
	 * Find TagDictionaries by their name.
	 * 
	 * @param guid the guid of the TagDictionary.
	 * @return a TagDictionary
	 */
	TagDictionary findByGuid(String guid);
	
	
	/**
	 * List TagDictionaries.
	 * 
	 * @return a list of TagDictionaries
	 */
	List<TagDictionary> getTagDictionaries();
	
	
	/**
	 * Delete tagDictionary by its instance.
	 * 
	 * @param tagDictionary tagDicationary.
	 */
	void delete(TagDictionary tagDictionary);

}
