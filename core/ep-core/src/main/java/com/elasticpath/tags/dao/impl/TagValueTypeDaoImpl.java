package com.elasticpath.tags.dao.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.dao.TagValueTypeDao;
import com.elasticpath.tags.domain.TagValueType;

/**
 * A DAO implementation for {@link com.elasticpath.tags.domain.TagValueType} class.
 */
public class TagValueTypeDaoImpl implements TagValueTypeDao {
    
	private PersistenceEngine persistenceEngine;
	
	/**
     * Saves the given {@link com.elasticpath.tags.domain.TagValueType} object to DB.
     *
     * @param tagValueType a {@link com.elasticpath.tags.domain.TagValueType} to saved or updated
     * @return the saved {@link com.elasticpath.tags.domain.TagValueType}
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public TagValueType saveOrUpdate(final TagValueType tagValueType) throws DataAccessException {
        return this.persistenceEngine.saveOrUpdate(tagValueType);
    }	


    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagValueType}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.TagValueType}
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<TagValueType> getTagValueTypes() throws DataAccessException {
        List<TagValueType> result = this.persistenceEngine.retrieveByNamedQuery("TAG_VALUE_TYPE_ALL");

		if (result.isEmpty()) {
            return Collections.emptyList();
		}

        return result;
    }
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.TagValueType} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.TagValueType}
     * @return the {@link com.elasticpath.tags.domain.TagValueType} with given GUID
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public TagValueType findByGuid(final String guid) throws DataAccessException {
		List<TagValueType> result = this.persistenceEngine.retrieveByNamedQuery("TAG_VALUE_TYPE_BY_GUID", guid);

        if (!result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }
    
    /**
     * Removes the given {@link com.elasticpath.tags.domain.TagValueType} object from DB.
     *
     * @param tagValueType a {@link com.elasticpath.tags.domain.TagValueType} to be removed
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public void remove(final TagValueType tagValueType) throws DataAccessException {
    	this.persistenceEngine.delete(tagValueType);
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
