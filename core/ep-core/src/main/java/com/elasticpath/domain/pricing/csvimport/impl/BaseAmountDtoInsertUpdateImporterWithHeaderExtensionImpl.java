/**
 * 
 */
package com.elasticpath.domain.pricing.csvimport.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.csvimport.DependentDtoImporter;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.BaseAmountInvalidException;
import com.elasticpath.base.exception.EpServiceException;

/**
 * An insert/update importer with header extension support(PriceListDescriptorDTO) for BaseAmount.
 *
 */
public class BaseAmountDtoInsertUpdateImporterWithHeaderExtensionImpl extends AbstractBaseAmountDtoInsertUpdateImporter 
				implements DependentDtoImporter<BaseAmountDTO, PriceListDescriptorDTO> {

	@Override
	public List<ImportBadRow> importDtos(final List<ImportValidRow<BaseAmountDTO>> validRows, final PriceListDescriptorDTO priceListDescriptorDTO) {

		if (priceListDescriptorDTO == null) {
			throw new EpServiceException("PriceListDescriptor was not specified.");
		}
		
		List<ImportBadRow> badRows = new ArrayList<ImportBadRow>();
		for (ImportValidRow<BaseAmountDTO> row : validRows) {
			BaseAmountDTO dto = row.getDto();
			try {
				dto.setPriceListDescriptorGuid(priceListDescriptorDTO.getGuid());
				
				BaseAmount baseAmount = getAssembler().assembleDomain(dto);
				BaseAmount existBaseAmount = findBaseAmount(baseAmount);
				if (existBaseAmount == null) {
					insert(baseAmount);
				} else {
					existBaseAmount.setListValue(baseAmount.getListValue());
					existBaseAmount.setSaleValue(baseAmount.getSaleValue());
					update(existBaseAmount);
				}

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
