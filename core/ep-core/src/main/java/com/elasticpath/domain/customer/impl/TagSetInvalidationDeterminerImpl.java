package com.elasticpath.domain.customer.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.domain.customer.TagSetInvalidationDeterminer;
import com.elasticpath.tags.dao.TagDictionaryDao;
import com.elasticpath.tags.domain.TagDefinition;

/**
 * Default implementation of {@link TagSetInvalidationDeterminer}.
 */
public class TagSetInvalidationDeterminerImpl implements TagSetInvalidationDeterminer, Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20091023L;
	
	private Set<String> tagGuids = null;
	
	private List<String> tagDictionaries;
	
	private TagDictionaryDao tagDictionaryDao;
	
	
	/**
	 * Set the tag dictionary DAO.
	 * @param tagDictionaryDao dao to set.
	 */
	public void setTagDictionaryDao(final TagDictionaryDao tagDictionaryDao) {
		this.tagDictionaryDao = tagDictionaryDao;
	}

	/**
	 * Set the tag dictionaries, that can invalidate price list stack.
	 * @param tagDictionaries list of dictionaries to set.
	 */
	public void setTagDictionaries(final List<String> tagDictionaries) {
		this.tagDictionaries = tagDictionaries;
	}
	
	private Set<String> getTagGuids() {
		if (tagGuids == null) {
			tagGuids = new HashSet<String>();
			if (tagDictionaries != null) {
				for (String dictionary : tagDictionaries) {
					Set<TagDefinition> tags = tagDictionaryDao.findByGuid(dictionary).getTagDefinitions();
					for (TagDefinition tag : tags) {
						tagGuids.add(tag.getGuid());					
					}
				}	
			}			
		}
		return tagGuids;
	}

	@Override
	public boolean needInvalidate(final String key) {
		return getTagGuids().contains(key);
	}

}
