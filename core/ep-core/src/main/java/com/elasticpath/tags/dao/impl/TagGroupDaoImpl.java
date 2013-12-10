package com.elasticpath.tags.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.dao.TagGroupDao;
import com.elasticpath.tags.domain.TagGroup;

/**
 * A DAO implementation for {@link com.elasticpath.tags.domain.TagGroup} class.
 */
public class TagGroupDaoImpl implements TagGroupDao {
    private PersistenceEngine persistenceEngine;

    /**
     * Saves the given {@link com.elasticpath.tags.domain.TagGroup} object to DB.
     *
     * @param tagGroup a {@link com.elasticpath.tags.domain.TagGroup} to saved or updated
     * @return the saved {@link com.elasticpath.tags.domain.TagGroup}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public TagGroup saveOrUpdate(final TagGroup tagGroup) throws DataAccessException {
        return this.persistenceEngine.saveOrUpdate(tagGroup);
    }

    /**
     * Removes the given {@link com.elasticpath.tags.domain.TagGroup} object from DB.
     *
     * @param tagGroup a {@link com.elasticpath.tags.domain.TagGroup} to be removed
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public void remove(final TagGroup tagGroup) throws DataAccessException {
        this.persistenceEngine.delete(tagGroup);
    }

    /**
     * Gets all the {@link com.elasticpath.tags.domain.TagGroup}s in the system.
     *
     * @return a list of {@link com.elasticpath.tags.domain.TagGroup}
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public List<TagGroup> getTagGroups() throws DataAccessException {
        List<TagGroup> result = this.persistenceEngine.retrieveByNamedQuery("TAG_GROUP_ALL");

		if (result.isEmpty()) {
            //noinspection unchecked
            return new ArrayList<TagGroup>(0);
		}

        return result;
    }

    /**
     * Finds the {@link com.elasticpath.tags.domain.TagGroup} with given GUID. If it doesn't find, it returns null.
     *
     * @param guid the guid of a {@link com.elasticpath.tags.domain.TagGroup}
     * @return the {@link com.elasticpath.tags.domain.TagGroup} with given GUID
     * @throws org.springframework.dao.DataAccessException might throw a {@link org.springframework.dao.DataAccessException}
     */
    public TagGroup findByGuid(final String guid) throws DataAccessException {
		List<TagGroup> result = this.persistenceEngine.retrieveByNamedQuery("TAG_GROUP_BY_GUID", guid);

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