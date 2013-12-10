package com.elasticpath.tags.dao.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.dao.TagOperatorDao;
import com.elasticpath.tags.domain.TagOperator;

/**
 * A DAO implementation for {@link com.elasticpath.tags.domain.TagOperator} class.
 */
public class TagOperatorDaoImpl implements TagOperatorDao {
    
	private PersistenceEngine persistenceEngine;


    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagOperator}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.TagOperator}
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<TagOperator> getTagOperators() throws DataAccessException {
        List<TagOperator> result = this.persistenceEngine.retrieveByNamedQuery("TAG_OPERATOR_ALL");

		if (result.isEmpty()) {
            return Collections.emptyList();
		}

        return result;
    }
    
    /**
     * Finds the {@link com.elasticpath.tags.domain.TagOperator} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.TagOperator}
     * @return the {@link com.elasticpath.tags.domain.TagOperator} with given GUID
     * @throws DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public TagOperator findByGuid(final String guid) throws DataAccessException {
		List<TagOperator> result = this.persistenceEngine.retrieveByNamedQuery("TAG_OPERATOR_BY_GUID", guid);

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
