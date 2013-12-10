package com.elasticpath.domain.pricing.csvimport.impl;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.csvimport.DtoImportDataType;
import com.elasticpath.csvimport.impl.CsvImportFieldObjectMapperImpl;

/**
 * <p>Overrides {@link CsvImportFieldObjectMapperImpl} to provide an instance of the {@link ImportDataTypeBaseAmountDto} object
 * as defined in the {@link ContextIdNames} instead of requiring that the {@code ImportDataType} be set via Spring
 * injection.</p>
 * <p>Ideally this class would be unnecessary, and {@code ImportDataTypeBaseAmountDto} would be defined in a 
 * Spring configuration and injected into the {@code CsvImportFieldDtoMapperImpl}; however, due to the 
 * {@link com.elasticpath.service.dataimport.impl.ImportServiceImpl} retrieving instances of {@code ImportDataType}
 * programatically form Spring rather than via injection this class has to do the same.</p>
 */
public class CsvImportFieldBaseAmountDtoMapperImpl extends CsvImportFieldObjectMapperImpl<BaseAmountDTO> {

	/**
	 * Gets the {@link DtoImportDataType} for BaseAmountDTOs that is configured in {@link ContextIdNames},
	 * initializes it to create the {@link com.elasticpath.domain.dataimport.ImportField}s, and returns it.
	 * @return the DtoImportDataType for BaseAmountDTOs
	 */
	@Override
	public DtoImportDataType<BaseAmountDTO> getDtoImportDataType() {
		DtoImportDataType<BaseAmountDTO> type = getBeanFactory().getBean(ContextIdNames.IMPORT_DATA_TYPE_BASEAMOUNT);
		type.init(null);
		return type;
	}
}
