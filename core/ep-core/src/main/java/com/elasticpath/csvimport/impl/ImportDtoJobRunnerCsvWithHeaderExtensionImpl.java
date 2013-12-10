/**
 * 
 */
package com.elasticpath.csvimport.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.csvimport.CsvReadResult;
import com.elasticpath.csvimport.DtoCsvLineReaderWithHeaderExtension;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * A CSV import job runner with header extension support. 
 *
 */
public class ImportDtoJobRunnerCsvWithHeaderExtensionImpl extends ImportDtoJobRunnerCsvImpl<BaseAmountDTO, PriceListDescriptorDTO> {

	private static final Logger LOG = Logger.getLogger(ImportDtoJobRunnerCsvWithHeaderExtensionImpl.class);
	
	private PriceListDescriptorService priceListDescriptorService;
	
	private BaseAmountService baseAmountService;
	
	private boolean priceListValidated = false;
		
	@Override
	protected void readAndImport() {
		@SuppressWarnings("unchecked")
		DtoCsvLineReaderWithHeaderExtension<BaseAmountDTO, PriceListDescriptorDTO> reader = 
			(DtoCsvLineReaderWithHeaderExtension<BaseAmountDTO, PriceListDescriptorDTO>) getDtoCsvLineReader();
		
		reader.setInputStream(createInputStream());
		reader.setConfiguration(getCsvReaderConfiguration());
		
		ImportJobState finalImportJobState = ImportJobState.FINISHED;
		
		int totalRows = 0;
		try {
			reader.open();
			//read header row and parse it
			CsvReadResult<PriceListDescriptorDTO> headerReadResult = reader.readHeader();

			int badRowSize = headerReadResult.getBadRows().size();
			if (badRowSize != 0) {
				getImportJobStatusHandler().reportFailedRows(getImportJobProcessId(), badRowSize);
				getImportJobStatusHandler().reportBadRows(getImportJobProcessId(), 
						headerReadResult.getBadRows().toArray(new ImportBadRow[badRowSize]));
			}
			PriceListDescriptorDTO priceListDescriptorDTO = getCompletePriceListDescriptorDTO(headerReadResult);
			
			while (!reader.isInputStreamFinished()) {
				List<ImportBadRow> badRows = new ArrayList<ImportBadRow>();
				//read in the DTOs in chunks
				CsvReadResult<BaseAmountDTO> readResult = reader.readDtos(getChunkSize(), true);
				badRows.addAll(readResult.getBadRows());
				//convert the DTOs to domain objects and persist them
				List<ImportBadRow> badRowsReturned = getDependentDtoImporter().importDtos(readResult.getValidRows(), priceListDescriptorDTO);
				badRows.addAll(badRowsReturned);
				totalRows += readResult.getTotalRows();
				
				getImportJobStatusHandler().reportCurrentRow(getImportJobProcessId(), totalRows);
				getImportJobStatusHandler().reportFailedRows(getImportJobProcessId(), badRowsReturned.size());
				getImportJobStatusHandler().reportBadRows(getImportJobProcessId(), badRows.toArray(new ImportBadRow[badRows.size()]));
				
				if (!getImportJobStatusHandler().verifyImportJobFailedRows(getImportJobProcessId(), 
						getImportJobRequest().getMaxAllowedFailedRows())) {
					finalImportJobState = ImportJobState.FAILED;
					break;
				}

				if (getImportJobStatusHandler().isImportJobCancelled(getImportJobProcessId())) {
					finalImportJobState = ImportJobState.CANCELLED;
					break;
				}
			}
		} catch (Exception ex) {
			LOG.error("Import job runner failed", ex);
		} finally {
			reader.close();
			getImportJobStatusHandler().reportImportJobState(getImportJobProcessId(), finalImportJobState);
		}
	}

	/**
	 * @param headerReadResult
	 * @return
	 */
	private PriceListDescriptorDTO getCompletePriceListDescriptorDTO(final CsvReadResult<PriceListDescriptorDTO> headerReadResult) {
		PriceListDescriptorDTO priceListDescriptorDTO = headerReadResult.getValidRows().get(0).getDto();
		PriceListDescriptor priceListDescriptor = this.priceListDescriptorService.findByName(priceListDescriptorDTO.getName());
		priceListDescriptorDTO.setGuid(priceListDescriptor.getGuid());
		return priceListDescriptorDTO;
	}
	
	/**
	 * @return the baseAmountService
	 */
	private BaseAmountService getBaseAmountService() {
		return baseAmountService;
	}
	
	/**
	 * @param baseAmountService the baseAmountService to set
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}

	/**
	 * @param priceListDescriptorService the priceListDescriptorService to set
	 */
	public void setPriceListDescriptorService(final PriceListDescriptorService priceListDescriptorService) {
		this.priceListDescriptorService = priceListDescriptorService;
	}

	@Override
	public List<ImportBadRow> validate(final Locale locale) {
		@SuppressWarnings("unchecked")
		DtoCsvLineReaderWithHeaderExtension<BaseAmountDTO, PriceListDescriptorDTO> reader = 
			(DtoCsvLineReaderWithHeaderExtension<BaseAmountDTO, PriceListDescriptorDTO>) getDtoCsvLineReader();

		reader.setInputStream(createInputStream());
		reader.setConfiguration(getCsvReaderConfiguration());
		reader.open();
		CsvReadResult<BaseAmountDTO> readResult = null;
		String priceListName = null;
		try {
			//read header row
			CsvReadResult<PriceListDescriptorDTO> headerReadResult = reader.readHeader();
			priceListName = headerReadResult.getValidRows().get(0).getDto().getName();
			//read the rest of the rows
			readResult = reader.readDtos(-1, true);
		} finally {
			reader.close();
		}
		List<ImportBadRow> badRows = new ArrayList<ImportBadRow>(0);
		if (readResult != null) {
			badRows = readResult.getBadRows();
			if (badRows.isEmpty()) {
				for (ImportValidRow< ? > validRow : readResult.getValidRows()) {
					validateChangeSetStatus(priceListName, validRow.getDto(), validRow.getRowNumber(), badRows);
				}
			}
		}
		return badRows;
	}
	
	/**
	 * Validate change set status for base amount.
	 * 
	 * @param priceListName the price list name
	 * @param object the object to check
	 * @param rowNumber the row number
	 * @param badRows the list of bad rows to use
	 */
	protected void validateChangeSetStatus(final String priceListName, final Object object, final int rowNumber, final List<ImportBadRow> badRows) {
		if (object instanceof BaseAmountDTO && ((BaseAmountDTO) object).getGuid() == null) {
			BaseAmountDTO dto = (BaseAmountDTO) object;
			dto.setPriceListDescriptorGuid(getGuidForPriceList(priceListName));
			String baseAmountGuid = getExistingGuidForDto(dto);
			dto.setGuid(baseAmountGuid);
		}
		
		super.validateChangeSetStatus(object, rowNumber, badRows);

		if (object instanceof BaseAmount && !isBaseAmountExist((BaseAmount) object)) {
			validatePriceListChangeSetStatusOnce(priceListName, badRows);
		}
	}

	/**
	 * Validation of whether a BaseAmount is in another change set are done on the basis of
	 * the GUID. This method checks whether the base amount being imported already exists, and
	 * if it does returns the existing GUID.
	 *
	 * @param dto the dto
	 * @return the GUID of the base amount if it already exists, null otherwise
	 */
	private String getExistingGuidForDto(final BaseAmountDTO dto) {
		BaseAmount existingBaseAmount = findBaseAmount(dto);
		if (existingBaseAmount != null) {
			return existingBaseAmount.getGuid();
		}
		return null;
	}
	
	/**
	 * Find the base amount. 
	 * 
	 * @param dto the base amount DTO which does not have a GUID.
	 * @return the base amount from database.
	 */
	protected BaseAmount findBaseAmount(final BaseAmountDTO dto) {
		BaseAmountFilter filter = getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		filter.setObjectGuid(dto.getObjectGuid());
		filter.setObjectType(dto.getObjectType());
		filter.setQuantity(dto.getQuantity());
		filter.setPriceListDescriptorGuid(dto.getPriceListDescriptorGuid());
		Collection<BaseAmount> baseAmounts = getBaseAmountService().findBaseAmounts(filter);
		if (CollectionUtils.isNotEmpty(baseAmounts)) {
			return baseAmounts.iterator().next();
		}
		return null;
	}
	
	/**
	 * Checks whether a BaseAmount matching the given baseAmount exists.
	 * 
	 * @param baseAmount the base amount to check
	 * @return true if the given BaseAmount exists
	 */
	protected boolean isBaseAmountExist(final BaseAmount baseAmount) {
		return getBaseAmountService().exists(baseAmount);
	}

	
	private String getGuidForPriceList(final String priceListName) {
		PriceListDescriptor priceList = priceListDescriptorService.findByName(priceListName);
		if (priceList != null) {
			return priceList.getGuid();
		}
		return null;
	}
	
	
	/**
	 * validate the price list change set status once.
	 * 
	 * @param priceListName the price list name
	 * @param badRows the bad rows
	 */
	protected void validatePriceListChangeSetStatusOnce(final String priceListName, final List<ImportBadRow> badRows) {
		if (priceListValidated) {
			return;
		}
		
		PriceListDescriptor priceListDescriptor = this.priceListDescriptorService.findByName(priceListName);
		
		// verify the change set status of the object in case change sets are enabled
		if (!checkChangeSetStatus(priceListDescriptor, getImportJobRequest().getChangeSetGuid())) {
			// verify the changeset status only if the verification of the line was successful
			ImportBadRow badRow = getBeanFactory().getBean(ContextIdNames.IMPORT_BAD_ROW);
			// report error
			final ImportFault importFault = getBeanFactory().getBean(ContextIdNames.IMPORT_FAULT);
			importFault.setCode("import.csvFile.badRow.priceListUnavailableForChangeSet");
			importFault.setArgs(new Object[] {priceListDescriptor.getName(), getImportJobRequest().getChangeSetGuid()});
			badRow.addImportFault(importFault);
			badRows.add(badRow);
		}
		
		priceListValidated = true;
	}
}
