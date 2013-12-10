/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.cmuser.impl;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.CmUserSession;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * The default implementation of <code>CmUserSession</code>.
 */
public class CmUserSessionImpl extends AbstractEpDomainImpl implements CmUserSession {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private CmUser cmUser;

	private ImportJob importJob;

	/**
	 * Returns the <code>CmUser</code> instance.
	 *
	 * @return the <code>CmUser</code> instance
	 */
	public CmUser getCmUser() {
		return cmUser;
	}

	/**
	 * Sets the <code>CmUser</code> instance.
	 *
	 * @param cmUser the <code>CmUser</code> instance.
	 */
	public void setCmUser(final CmUser cmUser) {
		this.cmUser = cmUser;
	}

	/**
	 * Returns the <code>ImportJob</code> instance.
	 *
	 * @return the <code>ImportJob</code> instance.
	 */
	public ImportJob getImportJob() {
		return importJob;
	}

	/**
	 * Sets the <code>ImportJob</code> instance.
	 *
	 * @param importJob the <code>ImportJob</code> instance.
	 */
	public void setImportJob(final ImportJob importJob) {
		this.importJob = importJob;
	}

}
