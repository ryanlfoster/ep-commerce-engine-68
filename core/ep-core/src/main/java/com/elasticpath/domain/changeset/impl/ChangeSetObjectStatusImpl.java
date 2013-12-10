package com.elasticpath.domain.changeset.impl;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.changeset.ChangeSetObjectStatusMutator;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Change set status implementation.
 */
public class ChangeSetObjectStatusImpl implements ChangeSetObjectStatus, ChangeSetObjectStatusMutator {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private BusinessObjectDescriptor objectDescriptor;
	private Collection<String> changeSetGuids = new HashSet<String>();
	
	@Override
	public BusinessObjectDescriptor getObjectDescriptor() {
		return objectDescriptor;
	}

	@Override
	public boolean isAvailable(final String changeSetGuid) {
		if (objectDescriptor == null) {
			// object descriptor was not resolved or set, status must be unavailable
			return false;
		}
		
		if (changeSetGuids.isEmpty() || changeSetGuids.contains(changeSetGuid)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isMember(final String changeSetGuid) {
		return changeSetGuids != null && changeSetGuids.contains(changeSetGuid);
	}
	
	@Override
	public boolean isLocked() {
		return CollectionUtils.isNotEmpty(changeSetGuids);		
	}

	@Override
	public void setChangeSetGuids(final Collection<String> changeSetGuids) {
		this.changeSetGuids = new HashSet<String>(changeSetGuids);
	}

	@Override
	public void setObjectDescriptor(final BusinessObjectDescriptor objectDescriptor) {
		this.objectDescriptor = objectDescriptor;
	}

	@Override
	public String toString() {
		return objectDescriptor + " - member of: " + changeSetGuids;
	}
	
	
}
