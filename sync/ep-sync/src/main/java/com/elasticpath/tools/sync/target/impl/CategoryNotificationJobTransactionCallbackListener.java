package com.elasticpath.tools.sync.target.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.target.JobUnitTransactionCallbackListener;

/**
 * A callback listener that also acts as a method productDaoReplacer for calls to productDao updateLastModifiedTimes.
 */
public class CategoryNotificationJobTransactionCallbackListener implements JobUnitTransactionCallbackListener {

    private ProductDao productDao;
    private ProductDaoReplacer productDaoReplacer;

    @Override
    @SuppressWarnings("unchecked")
	public void preCommitHook() {
		final Method replacedMethod = productDaoReplacer.getReplacedMethod();
		final Object invokingStructure = productDaoReplacer.getInvokingObject();
        if (replacedMethod == null) {
            // no categories in this job unit
            return;
        }

        final List<Long> listOfAffectedUids = new ArrayList<Long>();
        listOfAffectedUids.addAll(productDaoReplacer.getAffectedCategoryUids());
        List<Long> productUidsToUpdate;
        try {
            productUidsToUpdate = (List<Long>) replacedMethod.invoke(invokingStructure, listOfAffectedUids);
        } catch (final Exception e) {
            throw new SyncToolRuntimeException("Category Notification Callback listener could not batch notify category updated.", e);
        }

        getProductDao().updateLastModifiedTimes(productUidsToUpdate);
        productDaoReplacer.getAffectedCategoryUids().clear();
    }

    /**
     *
     * @return the productDao
     */
    public ProductDao getProductDao() {
        return productDao;
    }

    /**
     *
     * @param productDao the productDao to set
     */
    public void setProductDao(final ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     *
     * @return the productDaoReplacer
     */
    public ProductDaoReplacer getProductDaoReplacer() {
        return productDaoReplacer;
    }

    /**
     *
     * @param productDaoReplacer the productDaoReplacer to set
     */
    public void setProductDaoReplacer(final ProductDaoReplacer productDaoReplacer) {
        this.productDaoReplacer = productDaoReplacer;
    }

}
