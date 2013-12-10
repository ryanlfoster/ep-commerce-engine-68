package com.elasticpath.tags.dao;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.domain.ConditionalExpression;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Description.
 * <p/>
 *
 * @author Isa Goksu
 * @version 0.1
 */
public interface ConditionalExpressionDao {
    /**
     * Saves the given {@link com.elasticpath.tags.domain.ConditionalExpression} object to DB.
     *
     * @param condition a {@link com.elasticpath.tags.domain.ConditionalExpression} to saved or updated
     * @return the saved {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    ConditionalExpression saveOrUpdate(ConditionalExpression condition) throws DataAccessException;

    /**
     * Removes the given {@link com.elasticpath.tags.domain.ConditionalExpression} object from DB.
     *
     * @param condition a {@link com.elasticpath.tags.domain.ConditionalExpression} to be removed
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    void remove(ConditionalExpression condition) throws DataAccessException;

    /**
     * Gets all the {@link com.elasticpath.tags.domain.ConditionalExpression}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<ConditionalExpression> getConditions() throws DataAccessException;

    /**
     * Gets all the {@link com.elasticpath.tags.domain.ConditionalExpression}s that belongs to a certain TagDictionary in the system.
     *
     * @param tagDictionaryGuid a {@link com.elasticpath.tags.domain.TagDictionary} GUID
     * @return a list of {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<ConditionalExpression> getConditions(String tagDictionaryGuid) throws DataAccessException;
    
    /**
     * Gets all named {@link com.elasticpath.tags.domain.ConditionalExpression}s in the system.
     *
     * @return a list of named {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<ConditionalExpression> getNamedConditions() throws DataAccessException;    
    
    /**
     * Gets all named {@link com.elasticpath.tags.domain.ConditionalExpression}s that belongs to a certain Tag Dictionary in the system.
     *
     * @param tagDictionaryGuid a {@link com.elasticpath.tags.domain.TagDictionary} GUID
     * @return a list of named {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<ConditionalExpression> getNamedConditions(final String tagDictionaryGuid) throws DataAccessException;
    
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.ConditionalExpression} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @return the {@link com.elasticpath.tags.domain.ConditionalExpression} with given GUID
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    ConditionalExpression findByGuid(String guid) throws DataAccessException;
    
    /**
     * Finds the named {@link com.elasticpath.tags.domain.ConditionalExpression} with given name. If it doesn't find, it returns null.
     *
     * @param name the name of a named {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @return the named {@link com.elasticpath.tags.domain.ConditionalExpression} with given name
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    ConditionalExpression findByName(String name) throws DataAccessException;
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.ConditionalExpression} with given name, tag dictionary, tag. 
     * Any parameter can get null as value.
     * @param name that name of {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @param tagDictionaryGuid the tag dictionary guid.
     * @param tag that tag in conditional string. 
     * @return a list of named {@link com.elasticpath.tags.domain.ConditionalExpression}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    List<ConditionalExpression> getNamedConditionsByNameTagDictionaryConditionTag(
    		String name,
    		String tagDictionaryGuid,
    		String tag) throws DataAccessException;
    
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
    List<ConditionalExpression> getNamedConditionsByNameTagDictionaryConditionTagSellingContext(
    		String name,
    		String tagDictionaryGuid,
    		String tag,
    		String sellingContextGuid
    		) throws DataAccessException;    

    /**
     * Sets the persistence engine.
     *
     * @param persistenceEngine a peristence engine to be set
     */
    void setPersistenceEngine(PersistenceEngine persistenceEngine);
}
