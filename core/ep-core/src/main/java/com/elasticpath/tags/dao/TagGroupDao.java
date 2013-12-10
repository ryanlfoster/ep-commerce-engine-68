package com.elasticpath.tags.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.domain.TagGroup;

/**
 * Dao interface for all {@link com.elasticpath.tags.domain.TagGroup} related data operations.
 */
public interface TagGroupDao {
    /**
     * Saves the given {@link com.elasticpath.tags.domain.TagGroup} object to DB.
     *
     * @param tagGroup a {@link com.elasticpath.tags.domain.TagGroup} to saved or updated
     * @return the saved {@link com.elasticpath.tags.domain.TagGroup}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    TagGroup saveOrUpdate(TagGroup tagGroup) throws DataAccessException;

    /**
     * Removes the given {@link com.elasticpath.tags.domain.TagGroup} object from DB.
     *
     * @param tagGroup a {@link com.elasticpath.tags.domain.TagGroup} to be removed
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    void remove(TagGroup tagGroup) throws DataAccessException;

    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagGroup}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.TagGroup}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<TagGroup> getTagGroups() throws DataAccessException;

    /**
     * Finds the {@link com.elasticpath.tags.domain.TagGroup} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.TagGroup}
     * @return the {@link com.elasticpath.tags.domain.TagGroup} with given GUID
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    TagGroup findByGuid(String guid) throws DataAccessException;

    /**
     * Sets the persistence engine.
     *
     * @param persistenceEngine a peristence engine to be set
     */
    void setPersistenceEngine(PersistenceEngine persistenceEngine);

}
