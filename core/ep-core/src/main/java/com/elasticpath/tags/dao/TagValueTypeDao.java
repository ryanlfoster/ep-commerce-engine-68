package com.elasticpath.tags.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.elasticpath.tags.domain.TagValueType;

/**
 * Dao interface for all {@link com.elasticpath.tags.domain.TagValueType} related data operations.
 */
public interface TagValueTypeDao {
	
	/**
     * Saves the given {@link com.elasticpath.tags.domain.TagValueType} object to DB.
     *
     * @param tagValueType a {@link com.elasticpath.tags.domain.TagValueType} to saved or updated
     * @return the saved {@link com.elasticpath.tags.domain.TagValueType}
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    TagValueType saveOrUpdate(TagValueType tagValueType) throws DataAccessException;

    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagValueType}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.TagValueType}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<TagValueType> getTagValueTypes() throws DataAccessException;
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.TagValueType} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.TagValueType}
     * @return the {@link com.elasticpath.tags.domain.TagValueType} with given GUID
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    TagValueType findByGuid(String guid) throws DataAccessException;
    
    /**
     * Removes the given {@link com.elasticpath.tags.domain.TagValueType} object from DB.
     *
     * @param tagValueType a {@link com.elasticpath.tags.domain.TagValueType} to be removed
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    void remove(TagValueType tagValueType) throws DataAccessException;    
    
}