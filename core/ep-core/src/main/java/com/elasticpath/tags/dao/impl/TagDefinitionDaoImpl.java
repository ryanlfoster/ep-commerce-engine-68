package com.elasticpath.tags.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.dao.TagDefinitionDao;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;

/**
 * A DAO implementation for {@link com.elasticpath.tags.domain.TagDefinition} class.
 */
public class TagDefinitionDaoImpl implements TagDefinitionDao {
    private PersistenceEngine persistenceEngine;

    /**
     * Saves the given {@link com.elasticpath.tags.domain.TagDefinition} object to DB.
     *
     * @param tagDefinition a {@link com.elasticpath.tags.domain.TagDefinition} to saved or updated
     * @return the saved {@link com.elasticpath.tags.domain.TagDefinition}
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public TagDefinition saveOrUpdate(final TagDefinition tagDefinition) throws DataAccessException {
        return this.persistenceEngine.saveOrUpdate(tagDefinition);
    }

    /**
     * Removes the given {@link com.elasticpath.tags.domain.TagDefinition} object from DB.
     *
     * @param tagDefinition a {@link com.elasticpath.tags.domain.TagDefinition} to be removed
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public void remove(final TagDefinition tagDefinition) throws DataAccessException {
        this.persistenceEngine.delete(tagDefinition);
    }

    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagDefinition}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.TagDefinition}
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<TagDefinition> getTagDefinitions() throws DataAccessException {
        List<TagDefinition> result = this.persistenceEngine.retrieveByNamedQuery("TAG_DEFINITION_ALL");

		if (result.isEmpty()) {
            return Collections.emptyList();
		}

        return result;
    }

    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagDefinition}s in the system that has same tag dictionary guid.
     *
     * @param tagDictionaryGuid a tag dictionary guid
     * @return a list of {@link com.elasticpath.tags.domain.TagDefinition}
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<TagDefinition> getTagDefinitions(final String tagDictionaryGuid) throws DataAccessException {
		List<TagDefinition> result = this.persistenceEngine.retrieveByNamedQuery("TAG_DEFINITION_ALL_BY_TAG_DICTIONARY_GUID", tagDictionaryGuid);
    	
    	if (result.isEmpty()) {
    		return Collections.emptyList();
    	}
    	
    	return result;
    }
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.TagDefinition} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.TagDefinition}
     * @return the {@link com.elasticpath.tags.domain.TagDefinition} with given GUID
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public TagDefinition findByGuid(final String guid) throws DataAccessException {
		List<TagDefinition> result = this.persistenceEngine.retrieveByNamedQuery("TAG_DEFINITION_BY_GUID", guid);

        if (!result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }

    @Override
    public TagDefinition findByName(final String name) throws DataAccessException {
		List<TagDefinition> result = this.persistenceEngine.retrieveByNamedQuery("TAG_DEFINITION_BY_NAME", name);

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

    @Override
	public List<TagDefinition> getTagDefinitionsByTagGroup(final TagGroup group) {
		List<TagDefinition> result = this.persistenceEngine.retrieveByNamedQuery("TAG_DEFINITIONS_BY_GROUP_UIDPK", group.getUidPk());
		
		if (result == null) {
			return new ArrayList<TagDefinition>(0);
		}
		
		return result;
	}
    
}
