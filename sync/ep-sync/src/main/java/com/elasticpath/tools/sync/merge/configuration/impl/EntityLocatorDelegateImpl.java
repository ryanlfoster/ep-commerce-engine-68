package com.elasticpath.tools.sync.merge.configuration.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;

/**
 *
 * Entity locator delegate class.
 *
 */
public class EntityLocatorDelegateImpl extends AbstractEntityLocator {

	private static final String AT_CONFIGURATION = " at configuration";
    private static final String COULD_NOT_FIND = "Could not find ";
    private List<EntityLocator> entityLocators;
	private static final Logger LOG = Logger.getLogger(EntityLocatorDelegateImpl.class);

	/**
	 * Set the list of entity locators.
	 *
	 * @param entityLocators the list of entity locators
	 */
	public void setEntityLocators(final List<EntityLocator> entityLocators) {
		this.entityLocators = entityLocators;
	}

	/**
	 * call the method on each locator and return the persistable instance found.
	 *
	 * @param guid guid of the target persistable
	 * @param clazz class of the target persistable
	 * @return the persistable instance found
	 */
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) {
	    LOG.debug("Trying to sanity check class of guid: " + guid);
        sanityCheck(clazz);
		for (final EntityLocator locator : entityLocators) {
			if (locator.isResponsibleFor(clazz)) {
				return locator.locatePersistence(guid, clazz);
			}
		}
		throw new SyncToolConfigurationException(COULD_NOT_FIND + clazz.getName() + AT_CONFIGURATION);
	}

	/**
	 * Call the method on each locator, specific for sorting, and return the persistable instance found.
	 *
	 * @param guid guid of the target persistable
	 * @param clazz class of the target persistable
	 * @return the persistable instance found
	 */
	@Override
	public Persistable locatePersistenceForSorting(final String guid, final Class< ? > clazz) {
	    LOG.debug("Trying to sanity check class of guid: " + guid);
        sanityCheck(clazz);
		for (final EntityLocator locator : entityLocators) {
			if (locator.isResponsibleFor(clazz)) {
				return locator.locatePersistenceForSorting(guid, clazz);
			}
		}
		throw new SyncToolConfigurationException(COULD_NOT_FIND + clazz.getName() + AT_CONFIGURATION);
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		for (final EntityLocator locator : entityLocators) {
			if (locator.isResponsibleFor(clazz)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean entityExists(final String guid, final Class< ? > clazz) {
	    LOG.debug("Trying to sanity check class of guid: " + guid);
        sanityCheck(clazz);
        for (final EntityLocator locator : entityLocators) {
            if (locator.isResponsibleFor(clazz)) {
                return locator.entityExists(guid, clazz);
            }
        }
        throw new SyncToolConfigurationException(COULD_NOT_FIND + clazz.getName() + AT_CONFIGURATION);


	}


    /**
     * call the method on each locator and return the persistable instance reference found.
     *
     * @param guid guid of the target persistable
     * @param clazz class of the target persistable
     * @throws SyncToolConfigurationException an exception if not found.
     * @return the persistable instance found
     */
    @Override
    public Persistable locatePersistentReference(final String guid, final Class<?> clazz) throws SyncToolConfigurationException {
        for (final EntityLocator locator : entityLocators) {
            if (locator.isResponsibleFor(clazz)) {
                return locator.locatePersistentReference(guid, clazz);
            }
        }
        throw new SyncToolConfigurationException(COULD_NOT_FIND + clazz.getName() + AT_CONFIGURATION);
    }

}
