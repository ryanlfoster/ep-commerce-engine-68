package com.elasticpath.tags.dao;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;

import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Dao interface for all {@link com.elasticpath.tags.domain.TagDefinition} related data operations.
 */
public interface TagDefinitionDao {
    /**
     * Saves the given {@link com.elasticpath.tags.domain.TagDefinition} object to DB.
     *
     * @param tagDefinition a {@link com.elasticpath.tags.domain.TagDefinition} to saved or updated
     * @return the saved {@link com.elasticpath.tags.domain.TagDefinition}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    TagDefinition saveOrUpdate(TagDefinition tagDefinition) throws DataAccessException;

    /**
     * Removes the given {@link com.elasticpath.tags.domain.TagDefinition} object from DB.
     *
     * @param tagDefinition a {@link com.elasticpath.tags.domain.TagDefinition} to be removed
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    void remove(TagDefinition tagDefinition) throws DataAccessException;

    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagDefinition}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.TagDefinition}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<TagDefinition> getTagDefinitions() throws DataAccessException;

    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagDefinition}s in the system that has same tag dictionary guid.
     *
     * @param tagDictionaryGuid a tag dictionary guid
     * @return a list of {@link com.elasticpath.tags.domain.TagDefinition}
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<TagDefinition> getTagDefinitions(final String tagDictionaryGuid) throws DataAccessException;
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.TagDefinition} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.TagDefinition}
     * @return the {@link com.elasticpath.tags.domain.TagDefinition} with given GUID
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    TagDefinition findByGuid(String guid) throws DataAccessException;
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.TagDefinition} with given name. If it doesn't find, it returns null.
     *
     * @param name the name of a {@link com.elasticpath.tags.domain.TagDefinition}
     * @return the {@link com.elasticpath.tags.domain.TagDefinition} with given name
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    TagDefinition findByName(String name) throws DataAccessException;

    /**
     * Sets the persistence engine.
     *
     * @param persistenceEngine a peristence engine to be set
     */
    void setPersistenceEngine(PersistenceEngine persistenceEngine);
    
    /**
	 * Gets a list of tag definitions that belong to the specified group.
	 * @param group tag group
	 * @return a list of tag definitions
	 */
    List<TagDefinition> getTagDefinitionsByTagGroup(TagGroup group);
    
}
