package com.elasticpath.service.dataimport.impl;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.persistence.api.Entity;

/**
 * An import runner to import products.
 */
public class ImportJobRunnerProductSkuImpl extends AbstractImportJobRunnerImpl {
	/**
	 * Find the entity with the given guid.
	 * 
	 * @param guid the guid
	 * @return the entity with the given guid if it exists, otherwise <code>null</code>.
	 */
	protected Entity findEntityByGuid(final String guid) {
		return getImportGuidHelper().findProductSkuByGuid(guid);
	}

	/**
	 * Creates a new entity.
	 * 
	 * @param baseObject the base object might be used to determine entity type, such as <code>ProductType</code> etc.
	 * @return the newly created entity
	 */
	protected Entity createNewEntity(final Object baseObject) {
		return getBean(ContextIdNames.PRODUCT_SKU);
	}

	/**
	 * Returns the commit unit.
	 * 
	 * @return the commit unit.
	 */
	protected int getCommitUnit() {
		return ImportConstants.COMMIT_UNIT;
	}

	/**
	 * Update the entity before it get saved.
	 * 
	 * @param entity the entity to save
	 */
	protected void updateEntityBeforeSave(final Entity entity) {
		((ProductSku) entity).getProduct().setLastModifiedDate(getTimeService().getCurrentTime());		
	}
}