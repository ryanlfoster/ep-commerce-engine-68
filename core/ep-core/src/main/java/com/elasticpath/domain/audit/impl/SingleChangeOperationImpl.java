/**
 *
 */
package com.elasticpath.domain.audit.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.audit.SingleChangeOperation;

/**
 * An operation that results in a set of changes.
 *
 */
@Entity
@DiscriminatorValue("SINGLE")
@DataCache(enabled = false)
public class SingleChangeOperationImpl extends AbstractChangeOperationImpl implements SingleChangeOperation {

	private static final long serialVersionUID = -1654026539953917590L;

	private String rootObjectName;

	private long rootObjectUid;

	private String rootObjectGuid;

	@Override
	@Basic
	@Column(name = "ROOT_OBJECT_NAME")
	public String getRootObjectName() {
		return rootObjectName;
	}

	@Override
	public void setRootObjectName(final String rootObjectName) {
		this.rootObjectName = rootObjectName;
	}

	@Override
	@Basic
	@Column(name = "ROOT_OBJECT_UID")
	public long getRootObjectUid() {
		return rootObjectUid;
	}

	@Override
	public void setRootObjectUid(final long rootObjectUid) {
		this.rootObjectUid = rootObjectUid;
	}

	@Override
	@Basic
	@Column(name = "ROOT_OBJECT_GUID")
	public String getRootObjectGuid() {
		return rootObjectGuid;
	}

	@Override
	public void setRootObjectGuid(final String rootObjectGuid) {
		this.rootObjectGuid = rootObjectGuid;
	}

}