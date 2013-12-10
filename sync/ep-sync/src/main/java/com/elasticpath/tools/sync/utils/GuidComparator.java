package com.elasticpath.tools.sync.utils;

import java.util.Comparator;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.merge.configuration.GuidLocator;

/**
 * Compares two entities based on their guid information delegating derivation of the guid information to <code>GuidRetriever</code>. 
 */
public class GuidComparator implements Comparator<Persistable> {

	private GuidLocator guidRetriever;
	
	@Override
	public int compare(final Persistable object1, final Persistable object2) {		
		try {
			return guidRetriever.locateGuid(object1).compareTo(guidRetriever.locateGuid(object2));
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException(e);
		}
	}
	
	/**
	 * @param guidRetriever the guidRetriever to set
	 */
	public void setGuidLocator(final GuidLocator guidRetriever) {
		this.guidRetriever = guidRetriever;
	}

}
