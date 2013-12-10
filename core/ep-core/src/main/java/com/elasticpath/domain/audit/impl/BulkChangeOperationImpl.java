/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.audit.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.audit.BulkChangeOperation;

/**
 * Bulk change operation class. 
 */
@Entity
@DiscriminatorValue("BULK")
@DataCache(enabled = false)
public class BulkChangeOperationImpl extends AbstractChangeOperationImpl implements BulkChangeOperation {

	private static final long serialVersionUID = 352891922921398952L;

	private String queryString;
	
	private String parameters;

	@Override
	@Basic
	@Column(name = "QUERY_STRING")
	public String getQueryString() {
		return queryString;
	}

	@Override
	public void setQueryString(final String queryString) {
		this.queryString = queryString;
	}

	@Override
	@Basic
	@Column(name = "QUERY_PARAMETERS")
	public String getParameters() {
		return parameters;
	}

	@Override
	public void setParameters(final String parameters) {
		this.parameters = parameters;
	}
	
}
