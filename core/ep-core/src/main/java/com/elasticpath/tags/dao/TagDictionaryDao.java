package com.elasticpath.tags.dao;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.domain.TagDictionary;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Dao interface for all {@link com.elasticpath.tags.domain.TagDictionary} related data operations.
 */
public interface TagDictionaryDao {
    /**
     * Saves the given {@link com.elasticpath.tags.domain.TagDictionary} object to DB.
     *
     * @param tagDictionary a {@link com.elasticpath.tags.domain.TagDictionary} to saved or updated
     * @return the saved {@link com.elasticpath.tags.domain.TagDictionary}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    TagDictionary saveOrUpdate(TagDictionary tagDictionary) throws DataAccessException;

    /**
     * Removes the given {@link com.elasticpath.tags.domain.TagDictionary} object from DB.
     *
     * @param tagDictionary a {@link com.elasticpath.tags.domain.TagDictionary} to be removed
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    void remove(TagDictionary tagDictionary) throws DataAccessException;

    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagDictionary}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.TagDictionary}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<TagDictionary> getTagDictionaries() throws DataAccessException;

    /**
     * Finds the {@link com.elasticpath.tags.domain.TagDictionary} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.TagDictionary}
     * @return the {@link com.elasticpath.tags.domain.TagDictionary} with given GUID
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    TagDictionary findByGuid(String guid) throws DataAccessException;

    /**
     * Sets the persistence engine.
     *
     * @param persistenceEngine a peristence engine to be set
     */
    void setPersistenceEngine(PersistenceEngine persistenceEngine);

}
