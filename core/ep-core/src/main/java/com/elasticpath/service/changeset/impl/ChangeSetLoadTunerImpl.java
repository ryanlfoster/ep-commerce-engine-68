/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.changeset.impl;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;

/**
 *
 */
public class ChangeSetLoadTunerImpl extends AbstractEpDomainImpl implements ChangeSetLoadTuner {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private boolean loadingMemberObjects;
	private boolean loadingMemberObjectsMetadata;

	@Override
	public void setLoadingMemberObjects(final boolean value) {
		loadingMemberObjects = value;
	}

	@Override
	public boolean isLoadingMemberObjects() {
		return loadingMemberObjects;
	}

	@Override
	public boolean isLoadingMemberObjectsMetadata() {
		return loadingMemberObjectsMetadata;
	}

	@Override
	public void setLoadingMemberObjectsMetadata(final boolean value) {
		loadingMemberObjectsMetadata = value;
	}

	@Override
	public boolean contains(final LoadTuner loadTuner) {
		return false;
	}

	@Override
	public LoadTuner merge(final LoadTuner loadTuner) {
		return this;
	}

}
