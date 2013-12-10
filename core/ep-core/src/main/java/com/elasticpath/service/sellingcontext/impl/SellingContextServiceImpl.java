/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.sellingcontext.impl;

import java.util.Iterator;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.sellingcontext.SellingContextRetrievalStrategy;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * Provide selling context related business service.
 */
public class SellingContextServiceImpl extends AbstractEpPersistenceServiceImpl 
	implements SellingContextService, SellingContextRetrievalStrategy {

	private static final String QUERY_FIND_ALL = "FIND_ALL_SELLING_CONTEXTS";
	private static final String QUERY_FIND_BY_GUID = "SELLING_CONTEXT_FIND_BY_GUID";
	private static final String QUERY_SELLING_CONTEXT_FIND_BY_NAMED_CONDITION_GUID = 
		"SELLING_CONTEXT_FIND_BY_NAMED_CONDITION_GUID";
	
	/**
	 * {@inheritDoc}
	 * @deprecated please use <code>getByGuid(final String sellingContextGuid)</code> instead.
	 */
	@Deprecated
	public Object getObject(final long uid) throws EpServiceException {
		return this.get(uid);
	}
	
	/**
	 * Get a persistent instance with the given id.
	 *
	 * @param uid the persistent instance uid
	 *
	 * @return the persistent instance if exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	private SellingContext get(final long sellingContextUid) throws EpServiceException {
		sanityCheck();
		if (sellingContextUid <= 0) {
			return null;
		}
		return getPersistentBeanFinder().get(
					ContextIdNames.SELLING_CONTEXT, sellingContextUid);
	}
	
	
	@Override
	public List<SellingContext> getByNamedConditionGuid(final String namedConditionGuid) throws EpServiceException {		
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery(QUERY_SELLING_CONTEXT_FIND_BY_NAMED_CONDITION_GUID, namedConditionGuid);
		
	}
	
	@Override
	public SellingContext getByGuid(final String sellingContextGuid) throws EpServiceException {
		sanityCheck();
		List<SellingContext> sellingContexts = getPersistenceEngine().retrieveByNamedQuery(QUERY_FIND_BY_GUID, sellingContextGuid);

		SellingContext sellingContext = null;
		if (sellingContexts.size() == 1) {
			sellingContext = sellingContexts.get(0);
		} else if (sellingContexts.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate objects with same guid exist -- " + sellingContextGuid);
		}
		return sellingContext;
	}


	@Override
	public void remove(final SellingContext sellingContext) throws EpServiceException {
		sanityCheck();
		if (sellingContext == null) {
			return;
		}
		SellingContext updatedSellingContext = unlinkNamedConditions(sellingContext);
		getPersistenceEngine().delete(updatedSellingContext);
	}
	
    /**
     * Remove named conditions from the selling context before deleting so they
     * won't be cascade deleted when the selling context is deleted.
     * 
	 * @param sellingContext entity that need to be unlinked from named conditions   
     */
    private SellingContext unlinkNamedConditions(final SellingContext sellingContext) {
    	Iterator<String> keyIterator = sellingContext.getConditions().keySet().iterator();
    	while (keyIterator.hasNext()) {
    		String conditionKey = keyIterator.next();
			ConditionalExpression conditionalExpression = sellingContext.getConditions().get(conditionKey);
			if (conditionalExpression.isNamed()) {
				keyIterator.remove();
			}
    	}
		return getPersistenceEngine().saveOrUpdate(sellingContext);
    }
	

	@Override
	public SellingContext saveOrUpdate(final SellingContext sellingContext) throws EpServiceException {
		sanityCheck();
		if (sellingContext == null) {
			return null;
		}
		return getPersistenceEngine().saveOrUpdate(sellingContext);
	}
	
	@Override
	public List<SellingContext> findAll() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery(QUERY_FIND_ALL);
	}
}
