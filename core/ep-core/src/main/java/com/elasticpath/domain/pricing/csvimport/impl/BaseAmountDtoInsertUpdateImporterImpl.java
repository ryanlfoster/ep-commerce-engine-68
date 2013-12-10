/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.pricing.csvimport.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.csvimport.DependentDtoImporter;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.BaseAmountInvalidException;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Imports {@link BaseAmountDTO}s and associates them with a particular {@link com.elasticpath.domain.pricing.PriceListDescriptor}.
 * This implementation does an INSERT_AND_UPDATE of the DTOs; if the {@code BaseAmount} corresponding
 * to a given DTO in the associated PriceList doesn't exist then one will be created, otherwise it will
 * be updated. 
 */
public class BaseAmountDtoInsertUpdateImporterImpl extends AbstractBaseAmountDtoInsertUpdateImporter 
			implements DependentDtoImporter<BaseAmountDTO, String> {

	/**
	 * Imports the {@code BaseAmountDTO}s from the given valid import rows into the PriceListDescriptor identified
	 * by the given GUID. Implements the INSERT_AND_UPDATE strategy.

	 * @param validRows the valid rows containing the DTOs to import
	 * @param priceListDescriptorGuid the GUID of the PriceListDescriptor into which the DTOs should be imported
	 * @return a list of bad rows
	 */
	public List<ImportBadRow> importDtos(final List<ImportValidRow<BaseAmountDTO>> validRows, final String priceListDescriptorGuid) {
		if (priceListDescriptorGuid == null) {
			throw new EpServiceException("PriceListDescriptor GUID was not specified in the import job.");
		}
		List<ImportBadRow> badRows = new ArrayList<ImportBadRow>();
		for (ImportValidRow<BaseAmountDTO> row : validRows) {
			BaseAmountDTO dto = row.getDto();
			BaseAmount baseAmount = null;
			try {
				dto.setPriceListDescriptorGuid(priceListDescriptorGuid);
				baseAmount = assembleDomainFromDto(dto);
				insertOrReplace(baseAmount);
			} catch (BaseAmountInvalidException ex) {
				Errors errors = ex.getErrors();
				if (errors != null) {
					for (Object errorObject : errors.getFieldErrors()) {
						FieldError fieldError = (FieldError) errorObject;
						ImportFault importFault = createImportFault(fieldError.getCode(), fieldError.getRejectedValue());
						ImportBadRow importBadRow = createImportBadRow(row.getRow(), row.getRowNumber(), importFault);
						badRows.add(importBadRow);
					}
				}
			} catch (Exception ex) {
				final ImportFault fault = createImportFault(dto);
				ImportBadRow badRow = createImportBadRow(row.getRow(), row.getRowNumber(), fault);
				badRows.add(badRow);
			}
		}
		return badRows;
	}
}
