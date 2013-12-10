package com.elasticpath.service.dataimport.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.persistence.openjpa.PersistenceInterceptor;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCategoryImpl;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.base.exception.EpServiceException;

/**
 * An import runner to import categories.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ImportJobRunnerCategoryImpl extends AbstractImportJobRunnerImpl {
//	private static final Logger LOG = Logger.getLogger(ImportJobRunnerCategoryImpl.class);

	/**
	 * Find the entity with the given guid.
	 *
	 * @param guid the guid
	 * @return the entity with the given guid if it exists, otherwise <code>null</code>.
	 */
	@Override
	protected Entity findEntityByGuid(final String guid) {
		return getImportGuidHelper().findCategoryByGuidAndCatalogGuid(guid, 
				getImportJob().getCatalog().getGuid(), false, false);
	}

	/**
	 * Creates a new entity.
	 *
	 * @param baseObject the base object might be used to determine entity type, such as <code>CategoryType</code> etc.
	 * @return the newly created entity
	 */
	@Override
	protected Entity createNewEntity(final Object baseObject) {
		final Category category = getBean(ContextIdNames.CATEGORY);
		category.setCategoryType((CategoryType) baseObject);
		category.setCatalog(this.getImportJob().getCatalog());
		return category;
	}

	/**
	 * Validate the import job. This method overrides the parent one since category validation is more complex. In a category csv file, a child
	 * category may refer to a parent category that doesn't exist in db.
	 * 
	 * @param locale the locale of the results
	 * @return a list of <code>ImportBadRow</code>, or a empty list if there is no errors.
	 */
	@Override
	public List<ImportBadRow> validate(final Locale locale) {
		final CsvFileReader csvFileReader = getCsvFileReader();
		final List<ImportBadRow> importBadRows = new ArrayList<ImportBadRow>();

		// skip the title line
		csvFileReader.readNext();

		int rowNumber = 0;
		String[] nextLine;
		final SortedSet<String> guidsInCsvFile = new TreeSet<String>();
		final String parentCategoryFieldName = ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME 
			+ ImportDataTypeCategoryImpl.PARENT_CATEGORY_CODE;
		
		while ((nextLine = csvFileReader.readNext()) != null) {
			rowNumber++;
			final List<ImportFault> faults = new ArrayList<ImportFault>();

			String guid = null;
			if (getGuidColNumber() != null) {
				guid = nextLine[getGuidColNumber().intValue()];
				guidsInCsvFile.add(guid);
			}

			final Entity entity = createNewEntity(getBaseObject());

			for (Map.Entry<ImportField, Integer> entry : getMappings().entrySet()) {
				final ImportField importField = entry.getKey();
				final Integer colNum = entry.getValue();
				try {
					if (importField.getName().equals(parentCategoryFieldName)) {
						// Only if we can not find the parent category guid in the csv file,
						// we need to load it from database
						if (nextLine[colNum] != null && !guidsInCsvFile.contains(nextLine[colNum])) {
							checkField(nextLine[colNum], entity, importField);
						}
					} else {
						checkField(nextLine[colNum], entity, importField);
					}
				} catch (EpInvalidGuidBindException e) {
					final ImportFault importFault = getImportFaultWarning();
					importFault.setCode("import.csvFile.badRow.wrongGuid");
					importFault.setArgs(new Object[] { importField.getName(), importField.getType(), String.valueOf(colNum.intValue()),
							nextLine[colNum.intValue()] });
					faults.add(importFault);
				} catch (EpNonNullBindException e) {
					final ImportFault importFault = getImportFaultError();
					importFault.setCode("import.csvFile.badRow.notNull");
					importFault.setArgs(new Object[] { importField.getName(), importField.getType(), String.valueOf(colNum.intValue()),
							nextLine[colNum.intValue()] });
					faults.add(importFault);
				} catch (EpBindException e) {
					final ImportFault importFault = getImportFaultError();
					importFault.setCode("import.csvFile.badRow.bindError");
					importFault.setArgs(new Object[] { importField.getName(), importField.getType(), String.valueOf(colNum.intValue()),
							nextLine[colNum.intValue()] });
					faults.add(importFault);
				}
			}
			
			validateChangeSetStatus(nextLine, rowNumber, faults, entity);

			if (!faults.isEmpty()) {
				final ImportBadRow importBadRow = getBean(ContextIdNames.IMPORT_BAD_ROW);
				importBadRow.setRowNumber(rowNumber);
				importBadRow.setRow(nextLine[0]);
				importBadRow.addImportFaults(faults);
				importBadRows.add(importBadRow);
			}
		}
		csvFileReader.close();

		getImportJobStatusHandler().reportTotalRows(getImportJobProcessId(), rowNumber);
		return importBadRows;
	}

	/**
	 * Returns the commit unit.
	 *
	 * @return the commit unit.
	 */
	@Override
	protected int getCommitUnit() {
		// Category should be imported one by one because they are dependant to each other.
		return 1;
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
	 * Save the entity (Category), setting the <code>Catalog</code> first.
	 * @param session the persistence session
	 * @param entity the entity (Category) to persist
	 * @param persistenceObject the persistable object
	 * @return the updated entity.
	 */
	@Override
	protected Entity saveEntity(final PersistenceSession session, final Entity entity, final Persistable persistenceObject) {
		Category category = (Category) entity;
		Catalog currentCatalog = category.getCatalog();
		Catalog updatedCatalog = loadCatalogObject(session);
		// making explicit check to see if current category's catalog is different than the catalog this import is associated with, and throw
		// exception if not
		if ((currentCatalog == null) || (currentCatalog.getUidPk() == updatedCatalog.getUidPk())) {
			category.setCatalog(updatedCatalog);
			return super.saveEntity(session, entity, persistenceObject);
		} 
		throw new EpServiceException("Cannot save category because category code already exists in another catalog: " + category.getGuid());
	}
}
