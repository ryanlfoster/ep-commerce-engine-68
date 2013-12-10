/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.changeset;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * Change set load tuner used to fine tune the loading of a change set.
 */
public interface ChangeSetLoadTuner extends LoadTuner {

	/**
	 * Sets the value of whether member object should be loaded.
	 * 
	 * @param value the value to set
	 */
	void setLoadingMemberObjects(boolean value);

	/**
	 * Verifies whether loading of member objects is enabled.
	 * 
	 * @return true if member objects should be loaded
	 */
	boolean isLoadingMemberObjects();

	/**
	 * Sets the value of whether member object metadata should be loaded.
	 * 
	 * @param value the value to set
	 */
	void setLoadingMemberObjectsMetadata(boolean value);

	/**
	 * Verifies whether loading of member objects metadata is enabled.
	 * 
	 * @return true if member objects should be loaded
	 */
	boolean isLoadingMemberObjectsMetadata();
	
}
