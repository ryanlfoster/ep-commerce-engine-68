package com.elasticpath.tags.dao.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.dao.TagDictionaryDao;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * A DAO implementation for {@link com.elasticpath.tags.domain.TagDictionary} class.
 */
public class TagDictionaryDaoImpl implements TagDictionaryDao {
    private PersistenceEngine persistenceEngine;

    /**
     * Saves the given {@link com.elasticpath.tags.domain.TagDictionary} object to DB.
     *
     * @param tagDictionary a {@link com.elasticpath.tags.domain.TagDictionary} to saved or updated
     * @return the saved {@link com.elasticpath.tags.domain.TagDictionary}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public TagDictionary saveOrUpdate(final TagDictionary tagDictionary) throws DataAccessException {
        return this.persistenceEngine.saveOrUpdate(tagDictionary);
    }

    /**
     * Removes the given {@link com.elasticpath.tags.domain.TagDictionary} object from DB.
     *
     * @param tagDictionary a {@link com.elasticpath.tags.domain.TagDictionary} to be removed
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public void remove(final TagDictionary tagDictionary) throws DataAccessException {
        this.persistenceEngine.delete(tagDictionary);
    }

    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagDictionary}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.TagDictionary}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<TagDictionary> getTagDictionaries() throws DataAccessException {
        List<TagDictionary> result = this.persistenceEngine.retrieveByNamedQuery("TAG_DICTIONARY_ALL");

		if (result.isEmpty()) {
            //noinspection unchecked
            return Collections.emptyList();
		}

        return result;
    }

    /**
     * Finds the {@link com.elasticpath.tags.domain.TagDictionary} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.TagDictionary}
     * @return the {@link com.elasticpath.tags.domain.TagDictionary} with given GUID
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public TagDictionary findByGuid(final String guid) throws DataAccessException {
		List<TagDictionary> result = this.persistenceEngine.retrieveByNamedQuery("TAG_DICTIONARY_BY_GUID", guid);

        if (!result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }

    /**
     * Sets the persistence engine.
     *
     * @param persistenceEngine a peristence engine to be set
     */
    public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
        this.persistenceEngine = persistenceEngine;
    }
}