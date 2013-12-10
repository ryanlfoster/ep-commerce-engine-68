package com.elasticpath.tags.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.elasticpath.tags.domain.TagOperator;

/**
 * Dao interface for all {@link com.elasticpath.tags.domain.TagOperator} related data operations.
 */
public interface TagOperatorDao {

    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagOperator}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.TagOperator}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<TagOperator> getTagOperators() throws DataAccessException;
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.TagOperator} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.TagOperator}
     * @return the {@link com.elasticpath.tags.domain.TagOperator} with given GUID
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    TagOperator findByGuid(String guid) throws DataAccessException;
    
}