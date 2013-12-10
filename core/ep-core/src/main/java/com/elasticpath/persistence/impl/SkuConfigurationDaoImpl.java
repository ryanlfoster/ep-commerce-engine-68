/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.persistence.SkuConfigurationDao;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * This DAO encapsulates dynamically generated HQL queries used by the <code>SkuConfigurationService</code>.
 */
public class SkuConfigurationDaoImpl implements SkuConfigurationDao {

	private static final Logger LOG = Logger.getLogger(SkuConfigurationDaoImpl.class);

	private PersistenceEngine persistenceEngine;

	/**
	 * Finds a UID corresponding to a SKU with the given set of option value codes.
	 *
	 * @param productUid the UID of the product to search for matching SKUs
	 * @param optionValueCodes a List of Strings corresponding to the option value codes of the desired SKU
	 * @return the UID of the SKU with all the specified optionValueCodes or 0 if no such SKU is found.
	 */
	public long getSkuWithMatchingOptionValues(final long productUid, final List<String> optionValueCodes) {
		/*
		 * final StringBuffer sbf = new StringBuffer("Select sku.uidPk from ProductSku as sku inner join sku.optionValueMap as optVal where ("); for
		 * (int i = 0; i < optionValueCodes.size(); i++) { sbf.append("optVal.optionValueKey = '").append((String) optionValueCodes.get(i)).append("'
		 * "); if (i < optionValueCodes.size() - 1) { sbf.append("or "); } } sbf.append(") and sku.product.uidPk= " + productUid + " group by
		 * sku.uidPk having count(sku.uidPk)= ").append(optionValueCodes.size()); final List results =
		 * getPersistenceEngine().retrieve(sbf.toString()); long skuUid = 0; if (results.size() > 0) { skuUid = ((Long) results.get(0)).longValue(); }
		 */

		if (optionValueCodes == null || optionValueCodes.isEmpty()) {
			return 0;
		}

		final StringBuffer sbf = new StringBuffer(
				"SELECT sku.uidPk, COUNT(sku.uidPk) FROM ProductSkuImpl AS sku INNER JOIN sku.optionValueMap AS optVal WHERE (");

		for (int i = 0; i < optionValueCodes.size(); i++) {
			sbf.append("optVal.skuOptionValue.optionValueKey = '").append(optionValueCodes.get(i)).append("' ");
			if (i < optionValueCodes.size() - 1) {
				sbf.append("OR ");
			}
		}
		sbf.append(") and sku.productInternal.uidPk= " + productUid + " GROUP BY sku.uidPk");

		final List<Object[]> results = getPersistenceEngine().retrieve(sbf.toString());
		long skuUid = 0;
		if (!results.isEmpty()) {
			final Iterator<Object[]> resultsIterator = results.iterator();
			while (resultsIterator.hasNext()) {
				final Object[] item = resultsIterator.next();
				if (((Long) item[1]) == optionValueCodes.size()) {
					skuUid = (Long) item[0];
					break;
				}
			}

			// skuUid = ((Long) results.get(0)).longValue();
		}

		return skuUid;
	}

	/**
	 * Sets the persistence engine.
	 *
	 * @param persistenceEngine the persistence engine to set.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Persistence engine initialized ... " + persistenceEngine);
		}
	}

	/**
	 * Returns the persistence engine.
	 *
	 * @return the persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return this.persistenceEngine;
	}

}
