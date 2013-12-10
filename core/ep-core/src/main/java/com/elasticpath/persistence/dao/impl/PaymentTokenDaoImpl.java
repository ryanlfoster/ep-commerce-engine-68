/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.persistence.dao.impl;

import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.persistence.dao.PaymentTokenDao;

/**
 * Default implementation of {@link PaymentTokenDao}.
 */
public class PaymentTokenDaoImpl extends AbstractDaoImpl implements PaymentTokenDao {

	@Override
	public PaymentToken add(final PaymentToken paymentToken) throws EpServiceException {
		getPersistenceEngine().save(paymentToken);
		return paymentToken;
	}
	
	@Override
	public PaymentToken get(final long uidpk) throws EpServiceException {
		final List<PaymentToken> paymentTokens = getPersistenceEngine().retrieveByNamedQuery("FIND_PAYMENT_TOKEN_BY_UIDPK", uidpk);

		if (paymentTokens.isEmpty()) {
			return null;
		}
		
		return paymentTokens.get(0);
	}
}
