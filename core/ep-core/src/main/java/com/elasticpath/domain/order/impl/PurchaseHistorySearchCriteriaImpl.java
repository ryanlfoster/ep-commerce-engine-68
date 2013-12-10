/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order.impl;

import java.util.Date;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.order.PurchaseHistorySearchCriteria;

/**
 *  Represents criteria used when searching for a customer's purchase history.
 */
public class PurchaseHistorySearchCriteriaImpl extends AbstractEpDomainImpl implements PurchaseHistorySearchCriteria {
	/** Serial version id. */
	private static final long serialVersionUID = 6000000001L;
	
	private Date fromDate;
	private Date toDate;
	private String storeCode;
	private String userId;
	
	@Override
	public Date getFromDate() {
		return this.fromDate;
	}

	@Override
	public String getStoreCode() {
		return this.storeCode;
	}

	@Override
	public Date getToDate() {
		return this.toDate;
	}

	 @Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public void setFromDate(final Date fromDate) {
		this.fromDate = fromDate;
	}

	@Override
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;

	}

	@Override
	public void setToDate(final Date toDate) {
		this.toDate = toDate;
	}

	@Override
	public void setUserId(final String userId) {
		this.userId = userId;
	}

}
