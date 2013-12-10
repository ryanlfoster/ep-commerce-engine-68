/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.dataimport.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.persistence.openjpa.PersistenceInterceptor;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductAssociationImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceSession;
import org.apache.log4j.Logger;

/**
 * An import runner to handle product association CSV imports.
 */
public class ImportJobRunnerProductAssociationImpl extends AbstractImportJobRunnerImpl {
	
	private static final Logger LOG = Logger.getLogger(ImportJobRunnerProductAssociationImpl.class);
	
	private final Set<String> processedProductAssociationKey = new HashSet<String>();
	private int rowNumber = 0;
	
	/**
	 * Not used in this subclass.
	 * 
	 * @param guid ignored, operation not supported.
	 * @return nothing.
	 * @throws UnsupportedOperationException as it should never be called.
	 */
	@Override
	protected Entity findEntityByGuid(final String guid) {
		throw new UnsupportedOperationException("Should not be called");
	}

	/**
	 * Creates a new <code>ProductAssociation</code>.
	 * 
	 * @param baseObject ignored for product associations.
	 * @return the newly created entity
	 */
	@Override
	protected Entity createNewEntity(final Object baseObject) {
		ProductAssociation productAssociation = getBean(ContextIdNames.PRODUCT_ASSOCIATION);
		productAssociation.setGuid(new RandomGuidImpl().toString());
		productAssociation.setCatalog(this.getImportJob().getCatalog());
		return productAssociation;
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
	protected void checkField(final String fieldValue, final Persistable persistenceObject, final ImportField importField) {
		if (getImportJob().getImportType().equals(AbstractImportTypeImpl.DELETE_TYPE)) {
			updateRequiredFields();
		} else { //Don't validate DELETE jobs
			super.checkField(fieldValue, persistenceObject, importField);
		}
	}
	
	/**
	 * Imports one row of the import file into the database as a new <code>ProductAssociation</code>.
	 * Overridden to prevent Updates, Deletes. Updates are misleading since you cannot do an update
	 * of a ProductAssociation without wiping out all other ProductAssociations with the given
	 * source code.
	 * This implementation calls {@link addNewProductAssociation}.
	 * 
	 * @param nextLine the next line of the import file to import.
	 * @param session the persistence session to do the work.
	 * 
	 * @throws com.elasticpath.persistence.api.EpPersistenceException if there 
	 * 		   is an underlying persistable problem.
	 */
	@Override
	protected void importOneRow(final String[] nextLine, final PersistenceSession session) {
		rowNumber++;
		
		if (AbstractImportTypeImpl.UPDATE_TYPE.equals(getRequest().getImportType())) {
			throw new UnsupportedOperationException("Product association imports do not support UPDATE.");
		}
		if (AbstractImportTypeImpl.DELETE_TYPE.equals(getRequest().getImportType())) {
			updateRequiredFields();
			deleteProductAssociations(nextLine, session);
		} else {
			addNewProductAssociation(nextLine, session, readGuid(nextLine));
		}
	}
	
	/**
	 * Deletes product associations specified in the given array of input, from the catalog specified in the
	 * current import job.
	 * If only a source product code is supplied, all product associations with that source product are deleted.
	 * If a source product and target product are supplied, all associations matching the two will be deleted.
	 * If source, target, and association type are all supplied, then all matching assocations will be deleted.
	 * @param nextLine the array of input from a CSV line
	 * @param session the current persistence session
	 */
	@SuppressWarnings("PMD.ConfusingTernary") //I think it's clearer this way
	protected void deleteProductAssociations(final String[] nextLine, final PersistenceSession session) {
		//There's no point in creating a new ProductAssociation from the row as with the following commented code.
//		Entity newEntity = createNewEntity(null);
//		updateContent(nextLine, newEntity);
//		//Find the ids of product associations to delete
//		ProductAssociation paDto = (ProductAssociation) newEntity;
//		String sourceProductCode = paDto.getSourceProduct().getCode();
//		Product targetProduct = paDto.getTargetProduct();
//		String targetProductCode = null;
//		if (targetProduct != null) {
//			targetProductCode = targetProduct.getCode();
//		}
//		int associationType = paDto.getAssociationType();
		String sourceProductCode = getFieldValueFromLine(
				nextLine, ImportDataTypeProductAssociationImpl.FIELD_NAME_SOURCE_PRODUCT_CODE, getMappings());
		String targetProductCode = getFieldValueFromLine(
				nextLine, ImportDataTypeProductAssociationImpl.FIELD_NAME_TARGET_PRODUCT_CODE, getMappings());
		String associationTypeString = getFieldValueFromLine(
				nextLine, ImportDataTypeProductAssociationImpl.FIELD_NAME_ASSOCIATION_TYPE, getMappings());
		int associationType = 0;
		if (!StringUtils.isEmpty(associationTypeString)) {
			associationType = Integer.parseInt(associationTypeString);
		}
		String catalogCode = getImportJob().getCatalog().getCode();
		//delete the product associations
		if (targetProductCode != null && associationType != 0) {
			deleteProductAssociationsWithSourceTargetAssociationType(sourceProductCode, targetProductCode, associationType, catalogCode);
		} else if (targetProductCode != null && associationType == 0) {
			deleteProductAssociationsWithSourceTarget(sourceProductCode, targetProductCode, catalogCode);
		} else if (associationType != 0) {
			deleteProductAssociationsWithSourceAssociationType(sourceProductCode, associationType, catalogCode);
		} else {
			deleteProductAssociationsWithSource(sourceProductCode);
		}
	}
	
	private String getFieldValueFromLine(final String[] line, final String fieldName, final Map<ImportField, Integer> mappings) {
		for (Entry<ImportField, Integer> entry : mappings.entrySet()) {
			final ImportField importField = entry.getKey();
			// this has to happen before, otherwise
			final Integer colNum = entry.getValue();
			if (fieldName.equals(importField.getName()) && !StringUtils.isEmpty(line[colNum.intValue()])) {
				return line[colNum.intValue()];
			}
		}
		return null;
	}
	
	private void updateRequiredFields() {
		for (ImportField importField : getMappings().keySet()) {
			if (importField.getName().equals(ImportDataTypeProductAssociationImpl.FIELD_NAME_ASSOCIATION_TYPE) 
			|| importField.getName().equals(ImportDataTypeProductAssociationImpl.FIELD_NAME_TARGET_PRODUCT_CODE)) {
				importField.setRequired(false);
			}
		}
	}

	private void deleteProductAssociationsWithSource(final String sourceProductCode) {
		getImportGuidHelper().deleteProductAssociations(sourceProductCode, getImportJob().getCatalog().getCode());		
	}

	private void deleteProductAssociationsWithSourceTarget(
			final String sourceProductCode, final String targetProductCode, final String catalogCode) {
		this.getPersistenceEngine().executeNamedQuery("DELETE_PRODUCT_ASSOCIATIONS_BY_SOURCE_AND_TARGET_PRODUCT_CODE",
				sourceProductCode, targetProductCode, catalogCode);
	}

	private void deleteProductAssociationsWithSourceAssociationType(
			final String sourceProductCode, final int associationType, final String catalogCode) {
		this.getPersistenceEngine().executeNamedQuery("DELETE_PRODUCT_ASSOCIATIONS_BY_SOURCE_AND_ASSOC_TYPE",
				sourceProductCode, associationType, catalogCode);
	}
	
	private void deleteProductAssociationsWithSourceTargetAssociationType(
			final String sourceProductCode, final String targetProductCode, final int associationType, final String catalogCode) {
		this.getPersistenceEngine().executeNamedQuery("DELETE_PRODUCT_ASSOCIATIONS_BY_SOURCE_AND_TARGET_PRODUCT_CODE_AND_ASSOC_TYPE",
				sourceProductCode, targetProductCode, associationType, catalogCode);
	}

	/**
	 * Adds a new product association.  If this association is the first one for
	 * this source product (defined by being the first association in this import 
	 * run with the given sourceProductCode) then all existing product associations
	 * for the source product will be removed from the database before adding this
	 * new Product Association.
	 * 
	 * @param nextLine the next line of the import file to import.
	 * @param session the persistence session to do the work in.
	 * @param sourceProductCode the code for the source product.
	 * @return the newly persisted product association.
	 */
	protected Entity addNewProductAssociation(final String[] nextLine, final PersistenceSession session, final String sourceProductCode) {

		if (!isEntityAlreadyImported(sourceProductCode)) {
			// Remove existing associations for the source product in the
			// destination catalog, before importing any new ones.
			getImportGuidHelper().deleteProductAssociations(sourceProductCode, getImportJob().getCatalog().getCode());
			recordImportedEntityGuid(sourceProductCode);
		}
		
		Entity newEntity = createNewEntity(null);
		updateContent(nextLine, newEntity);
		
		String combinedKey = getCombinedKey((ProductAssociation) newEntity);
		
		if (processedProductAssociationKey.contains(combinedKey)) {
			reportDuplicateRow(nextLine);
			return newEntity;
		} else {
			processedProductAssociationKey.add(combinedKey);
			return saveEntity(session, newEntity, newEntity);
		} 
	}

	/**
	 * Report duplicate row.
	 *
	 * @param nextLine
	 */
	private void reportDuplicateRow(final String[] nextLine) {
		final ImportBadRow importBadRow = getBean(ContextIdNames.IMPORT_BAD_ROW);
		importBadRow.setRowNumber(rowNumber);
		importBadRow.setRow(nextLine[0]);

		final ImportFault importFault = getImportFaultError();
		importFault.setCode("import.csvFile.badRow.duplicateRow");
		importFault.setArgs(new String[] { "Row number", Integer.toString(rowNumber), nextLine[0] });

		importBadRow.addImportFault(importFault);
		
		this.getImportJobStatusHandler().reportBadRows(this.getImportJobProcessId(), importBadRow);
		this.getImportJobStatusHandler().reportFailedRows(this.getImportJobProcessId(), 1);
		
		LOG.warn(String.format("SKIP duplicate row %d: %s", rowNumber, nextLine[0]));
	}

	/**
	 * Build a combined key for a product association to check if its duplicate.
	 *
	 * @param productAssociation the product association for checking duplicate
	 */
	private String getCombinedKey(final ProductAssociation productAssociation) {
		
		return new StringBuilder()
			.append(productAssociation.getSourceProduct().getCode())
			.append(productAssociation.getTargetProduct().getCode())
			.append(productAssociation.getAssociationType())
			.append(productAssociation.getCatalog().getCode())
			.toString();
	}
	

	/**
	 * This method does nothing in order to avoid change set processing.
	 */
	@Override
	protected void prepareChangeSetProcessing() {
		// change set processing is not supported for the product associations import
	}
}
