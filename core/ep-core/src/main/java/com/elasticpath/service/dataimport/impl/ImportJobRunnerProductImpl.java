package com.elasticpath.service.dataimport.impl;

import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.domain.dataimport.impl.ProductCategoryImportBean;
import com.elasticpath.persistence.openjpa.PersistenceInterceptor;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductCategoryAssociationImpl;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.base.exception.EpServiceException;

/**
 * An import runner to import products.
 */
public class ImportJobRunnerProductImpl extends AbstractImportJobRunnerImpl {
	private static final Logger LOG = Logger.getLogger(ImportJobRunnerProductImpl.class);
	
	/**
	 * Find the entity with the given guid.
	 * 
	 * @param guid the guid
	 * @return the entity with the given guid if it exists, otherwise <code>null</code>.
	 */
	@Override
	protected Entity findEntityByGuid(final String guid) {
		return getImportGuidHelper().findProductByGuid(guid, false, false, true);
	}

	/**
	 * Creates a new entity.
	 * 
	 * @param baseObject the base object might be used to determine entity type, such as <code>ProductType</code> etc.
	 * @return the newly created entity
	 */
	@Override
	protected Entity createNewEntity(final Object baseObject) {
		final Product product = getBean(ContextIdNames.PRODUCT);
		product.setProductType((ProductType) baseObject);
		return product;
	}

	/**
	 * Returns the commit unit.
	 * 
	 * @return the commit unit.
	 */
	@Override
	protected int getCommitUnit() {
		return ImportConstants.COMMIT_UNIT;
	}

	/**
	 * Update the entity before it get saved.
	 * 
	 * @param entity the entity to save
	 */
	@Override
	protected void updateEntityBeforeSave(final Entity entity) {
		if (entity instanceof PersistenceInterceptor) {
			((PersistenceInterceptor) entity).executeBeforePersistAction();
		}
		((Product) entity).setLastModifiedDate(getTimeService().getCurrentTime());
	}
	
	/**
	 * {@inheritDoc}
	 * <p>This implementation, when the import type is {@link AbstractImportTypeImpl.DELETE}
	 * AND the datatype being imported is the value object {@link ImportDataTypeProductCategoryAssociationImpl},
	 * will remove the {@code Category} specified in the field array from the {@code Product} Entity.</p>
	 */
	@Override
	protected void importOneRow(final String[] nextLine, final PersistenceSession session) throws EpServiceException {
		final String guid = readGuid(nextLine);
		Entity entity = loadEntityByGuid(guid);
		ImportType importType = this.getRequest().getImportType();
		if (importType.equals(AbstractImportTypeImpl.UPDATE_TYPE)) {
			entity = update(nextLine, session, guid, entity);
		}
		if (importType.equals(AbstractImportTypeImpl.INSERT_TYPE)) {
			entity = insert(nextLine, session, guid, entity);
		}
		if (importType.equals(AbstractImportTypeImpl.INSERT_UPDATE_TYPE)) {
			entity = insertAndUpdate(nextLine, session, guid, entity);
		}
		if (importType.equals(AbstractImportTypeImpl.DELETE_TYPE)) {
			if (getImportDataType() instanceof ImportDataTypeProductCategoryAssociationImpl) {
				removeCategoryFromProduct(session, entity, nextLine);
			} else {
				delete(session, guid, entity);
			}
		}
	}

	private void removeCategoryFromProduct(final PersistenceSession session, final Entity entity, final String[] nextLine) {
		ProductCategoryImportBean productCategoryAssociation = (ProductCategoryImportBean) getImportDataType().createValueObject();
		updateContent(nextLine, productCategoryAssociation);
		Product product = (Product) entity;
		Category categoryToRemove = productCategoryAssociation.getCategory();
		if (product.getDefaultCategory(product.getMasterCatalog()).equals(categoryToRemove)) {
			LOG.warn("Attempted to remove default (primary) category in master catalog code = " 
					+ product.getMasterCatalog().getCode() + " from product code=" + product.getCode() 
					+ " - not permitted.");
			
			return;
		}
		product.removeCategory(categoryToRemove);
		session.save(entity);
	}
	
	/**
	 * {@inheritDoc}
	 * This implementation overrides the "required" parameter on the given {@code ImportField} (setting to false)
	 * in the case that the current {@code ImportJob} is of type DELETE, to allow flexibility of the CSV input
	 * for deletion of {@code ProductAssociation}s. Introduces a dependency on the {@link ImportDataTypeProductAssociationImpl}
	 * class so that the field names can be referenced accurately. Does not perform validation for Delete jobs.
	 * Calls {@link #getImportJob()} to get the type of import job.
	 */
	@Override
	protected void validateChangeSetStatus(final String[] nextLine, final int rowNumber, final List<ImportFault> faults,
			final Persistable persistenceObject) {
		super.validateChangeSetStatus(nextLine, rowNumber, faults, persistenceObject);

		// This code is for importing product category associations (either with or without featured products)
		// and ensuring that the products themselves
		// are not in another change set.
		
		// Identify that we're importing product category associations  
		if (persistenceObject instanceof ProductCategory) {
			for (Entry<ImportField, Integer> entry : getMappings().entrySet()) {
				final ImportField importField = entry.getKey();
				final Integer colNum = entry.getValue();
			
				if ("productCode".equals(importField.getName())) {
					Product product = new ProductImpl();
					product.setCode(nextLine[colNum]);
					if (!checkChangeSetStatus(product, getRequest().getChangeSetGuid())) {
						// report error
						final ImportFault importFault = getImportFaultError();
						importFault.setCode("import.csvFile.badRow.unavailableForChangeSet");
						importFault.setArgs(new Object[] { rowNumber, getRequest().getChangeSetGuid() });
						faults.add(importFault);
					}
					}
				}
		}
	}
}
