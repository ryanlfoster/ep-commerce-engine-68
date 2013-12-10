package com.elasticpath.tools.sync.target;

import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * The factory which retrieves Dao adapters by business object type.
 */
public class DaoAdapterFactory {

	private static final Logger LOG = Logger.getLogger(DaoAdapterFactory.class);
	
	private Map<Class< ? >, DaoAdapter< ? super Persistable >> syncAdapters;

	/**
	 * Gets <code>SyncService</code> based on BusinessObjectType key.
	 * 
	 * @param businessObjectType a class representing businessObjectType
	 * @return sync service
	 * @throws SyncToolConfigurationException if the sync service can not be located which is misconfiguration.
	 */
	public DaoAdapter< ? super Persistable> getDaoAdapter(final Class< ? > businessObjectType) throws SyncToolConfigurationException {
		DaoAdapter< ? super Persistable > syncService = syncAdapters.get(businessObjectType);
		if (syncService == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Could not find adapter for %s, the available adapters are: %s", businessObjectType, syncAdapters));
			}
			throw new SyncToolConfigurationException("Unable to find Dao adapter for: " + businessObjectType);
		}
		return syncService;
	}

	/**
	 * @param syncAdapters the syncServices to set
	 */
	public void setSyncAdapters(final Map<Class< ? >, DaoAdapter< ? super Persistable>> syncAdapters) {
		this.syncAdapters = syncAdapters;
	}

}
