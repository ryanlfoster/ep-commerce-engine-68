/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.rules.csvimport.impl;

import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.csvimport.DtoImportDataType;
import com.elasticpath.domain.dataimport.impl.AbstractImportFieldImpl;
import com.elasticpath.service.dataimport.ImportGuidHelper;

/**
 * The import data type for the csv file which contains coupon code and email. 
 */
public class ImportDataTypeCouponCodeEmailImpl extends ImportDataTypeCouponCodeImpl implements DtoImportDataType<CouponUsageModelDto> {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 7000000001L;
	
	/**
	 * A prefix used in the import data type name.
	 */
	protected static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "Coupon Code And Email";
	
	private static final String EMAIL_ADDRESS_IMPORT_FIELD_NAME = "emailAddress";
	
	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = "importJobRunnerCouponCodeEmail";
	

	@Override
	public void init(final Object baseObject) {
		super.init(baseObject);
	
		//optional fields
		createImportFieldEmailAddress();
	}
		
	/**
	 * Creates the {@link com.elasticpath.domain.dataimport.ImportField} for importing BaseAmount ObjectGuid fields,
	 * and adds it to the list of {@code ImportField}s.
	 */
	void createImportFieldEmailAddress() {
		addImportField(EMAIL_ADDRESS_IMPORT_FIELD_NAME, 
				new AbstractImportFieldImpl(EMAIL_ADDRESS_IMPORT_FIELD_NAME, String.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((CouponUsageModelDto) object).getEmailAddress();
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				((CouponUsageModelDto) object).setEmailAddress(value);
			}
		});
	}
	
	/**
	 * Validate the bean as a whole after all fields are populated.
	 * @param dto the dto to populate
	 * @return the validated bean
	 * throws EpBindException if the bean cannot is not valid
	 */
	public CouponUsageModelDto validatePopulatedDtoBean(final CouponUsageModelDto dto) {
		//if validation fails, the import will not be executed. 
		return dto;
	}
	

	
	@Override
	public String getPrefixOfName() {
		return PREFIX_OF_IMPORT_DATA_TYPE_NAME;
	}
	
	/**
	 * @return the Spring identifier of the implementation of the ImportJobRunner for BaseAmount objects.
	 */
	public String getImportJobRunnerBeanName() {
		return IMPORT_JOB_RUNNER_BEAN_NAME;
	}

}
