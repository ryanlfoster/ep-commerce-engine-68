package com.elasticpath.tags.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.dao.ConditionalExpressionDao;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * A DAO implementation for {@link com.elasticpath.tags.domain.ConditionalExpression} class.
 */
public class ConditionalExpressionDaoImpl implements ConditionalExpressionDao {
    private PersistenceEngine persistenceEngine;

    /**
     * Saves the given {@link com.elasticpath.tags.domain.ConditionalExpression} object to DB.
     *
     * @param condition a {@link com.elasticpath.tags.domain.ConditionalExpression} to saved or updated
     * @return the saved {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public ConditionalExpression saveOrUpdate(final ConditionalExpression condition) throws DataAccessException {
        return this.persistenceEngine.saveOrUpdate(condition);
    }

    /**
     * Removes the given {@link com.elasticpath.tags.domain.ConditionalExpression} object from DB.
     *
     * @param condition a {@link com.elasticpath.tags.domain.ConditionalExpression} to be removed
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public void remove(final ConditionalExpression condition) throws DataAccessException {
        this.persistenceEngine.delete(condition);
    }

    /**
     * Gets all the {@link com.elasticpath.tags.domain.ConditionalExpression}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<ConditionalExpression> getConditions() throws DataAccessException {
        List<ConditionalExpression> result = this.persistenceEngine.retrieveByNamedQuery("CONDITION_ALL");

		if (result.isEmpty()) {
            //noinspection unchecked
            return new ArrayList<ConditionalExpression>();
		}

        return result;
    }
    
    /**
     * Gets all named {@link com.elasticpath.tags.domain.ConditionalExpression}s in the system.
     *
     * @return a list of named {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<ConditionalExpression> getNamedConditions() throws DataAccessException {
        List<ConditionalExpression> result = this.persistenceEngine.retrieveByNamedQuery("CONDITION_ALL_NAMED");

		if (result.isEmpty()) {
            //noinspection unchecked
            return new ArrayList<ConditionalExpression>();
		}

        return result;
    }
    

    /**
     * Gets all the {@link com.elasticpath.tags.domain.ConditionalExpression}s that belongs to a certain Tag Dictionary in the system.
     *
     * @param tagDictionaryGuid a {@link com.elasticpath.tags.domain.TagDictionary} GUID
     * @return a list of {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<ConditionalExpression> getConditions(final String tagDictionaryGuid) throws DataAccessException {
		List<ConditionalExpression> result =
				this.persistenceEngine.retrieveByNamedQuery("CONDITION_ALL_BY_TAG_DICTIONARY", tagDictionaryGuid);
    	
    	if (result.isEmpty()) {
    		//noinspection unchecked
    		return new ArrayList<ConditionalExpression>();
    	}
    	
    	return result;
    }
    
    
    /**
     * Gets all named {@link com.elasticpath.tags.domain.ConditionalExpression}s that belongs to a certain Tag Dictionary in the system.
     *
     * @param tagDictionaryGuid a {@link com.elasticpath.tags.domain.TagDictionary} GUID
     * @return a list of named {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<ConditionalExpression> getNamedConditions(final String tagDictionaryGuid) throws DataAccessException {
		List<ConditionalExpression> result =
				this.persistenceEngine.retrieveByNamedQuery("CONDITION_ALL_NAMED_BY_TAG_DICTIONARY", tagDictionaryGuid);
    	
    	if (result.isEmpty()) {
    		//noinspection unchecked
    		return new ArrayList<ConditionalExpression>();
    	}
    	
    	return result;
    }
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.ConditionalExpression} with given name, tag dictionary, tag. 
     * Any parameter can get null as value.
     * @param name that name of {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @param tagDictionaryGuid the tag dictionary guid.
     * @param tag that tag in conditional string. 
     * @return a list of named {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<ConditionalExpression> getNamedConditionsByNameTagDictionaryConditionTag(
    		final String name,
    		final String tagDictionaryGuid,
    		final String tag) throws DataAccessException {
    	
    	String tagString = tag;
    	if (tagString != null && tagString.trim().length() > 0) {
    		tagString = ' ' + tag;
    	}
		List<ConditionalExpression> result =
				this.persistenceEngine.retrieveByNamedQuery("CONDITION_NAMED_BY_NAME_DICT_TAG",
						prepareStringForSQLLikeOperation(name),
						tagDictionaryGuid,
						prepareStringForSQLLikeOperation(tagString));
    	if (result.isEmpty()) {
    		//noinspection unchecked
    		return new ArrayList<ConditionalExpression>();
    	}
    	return result;
    }
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.ConditionalExpression} with given name, tag dictionary, tag. 
     * Any parameter can get null as value.
     * @param name that name of {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @param tagDictionaryGuid the tag dictionary guid.
     * @param tag that tag in conditional string.
     * @param sellingContextGuid the name of dynamic content delivery. 
     * @return a list of named {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<ConditionalExpression> getNamedConditionsByNameTagDictionaryConditionTagSellingContext(
    		final String name,
    		final String tagDictionaryGuid,
    		final String tag,
    		final String sellingContextGuid
    		) throws DataAccessException {
    	
    	List<ConditionalExpression> result = null;
    	
    	if (null == sellingContextGuid) {
    		result = getNamedConditionsByNameTagDictionaryConditionTag(name, tagDictionaryGuid, tag);
    	} else {
        	String tagString = tag;
        	if (tagString != null && tagString.trim().length() > 0) {
        		tagString = ' ' + tag;
        	}
			result =
					this.persistenceEngine.retrieveByNamedQuery("CONDITION_NAMED_BY_NAME_DICT_TAG_SELLINGCONTEXT",
							prepareStringForSQLLikeOperation(name),
							tagDictionaryGuid,
							prepareStringForSQLLikeOperation(tagString),
							sellingContextGuid);
        	if (result.isEmpty()) {
        		//noinspection unchecked
        		result =  new ArrayList<ConditionalExpression>();
        	}    		
    	}
    	return result;
    }

	private String prepareStringForSQLLikeOperation(final String stringFromUI) {
		String stringForQuery = stringFromUI;	
		if (stringForQuery != null) {
			stringForQuery = "%" + stringFromUI + "%";
		}
		return stringForQuery;
	}
    
    
    
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.ConditionalExpression} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @return the {@link com.elasticpath.tags.domain.ConditionalExpression} with given GUID
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public ConditionalExpression findByGuid(final String guid) throws DataAccessException {
		List<ConditionalExpression> result = this.persistenceEngine.retrieveByNamedQuery("CONDITION_BY_GUID", guid);

        if (!result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }
    
    /**
     * Finds the named {@link com.elasticpath.tags.domain.ConditionalExpression} with given name. If it doesn't find, it returns null.
     *
     * @param name the name of a named {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @return the named {@link com.elasticpath.tags.domain.ConditionalExpression} with given name
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public ConditionalExpression findByName(final String name) throws DataAccessException {
		List<ConditionalExpression> result = this.persistenceEngine.retrieveByNamedQuery("CONDITION_BY_NAME", name);

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