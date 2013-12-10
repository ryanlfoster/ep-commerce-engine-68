package com.elasticpath.tools.sync.merge.configuration.impl;


import java.util.Map;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.merge.configuration.GuidLocator;

/**
 * Provides mapping information how to get a method name of an entity which uniquely identifies the entity. 
 * This may be guid, code, name, etc. depending on the entity.  
 */
public class GuidLocatorImpl implements GuidLocator {

	private Map<Class< ? >, String> guidMethodNames;

	@Override
	public String locateGuid(final Persistable object) throws SyncToolConfigurationException {
		String guidMethodName = null;

		try { 
			guidMethodName = guidMethodNames.get(object.getClass());
			if (guidMethodName == null) {
				throw new SyncToolConfigurationException("Unable to retrieve GUID information for the entity: " + object);
			}
			return (String) object.getClass().getMethod(guidMethodName).invoke(object);
		} catch (Exception exception) {
			throw new SyncToolConfigurationException("Unable to invoke a method, retrieving GUID information: "
					+ object + ", method: " + guidMethodName, exception);
		}			
	}

	@Override
	public boolean canQualifyByGuid(final Class< ? > clazz) {
		return guidMethodNames.containsKey(clazz);
	}

	/**
	 * @param guidMethodNames the guidMethodNames to set
	 */
	public void setGuidMethodNames(final Map<Class< ? >, String> guidMethodNames) {
		this.guidMethodNames = guidMethodNames;
	}
}
