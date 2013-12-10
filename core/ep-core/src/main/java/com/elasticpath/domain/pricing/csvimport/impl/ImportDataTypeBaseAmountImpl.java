/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.pricing.csvimport.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpRequiredDependentFieldsMissingBindException;
import com.elasticpath.commons.exception.EpSalePriceExceedListPriceException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.csvimport.DtoImportDataType;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.dataimport.impl.AbstractImportDataTypeImpl;
import com.elasticpath.domain.dataimport.impl.AbstractImportFieldImpl;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.dataimport.ImportGuidHelper;

/**
 * <p>Defines and provides access to {@code ImportField} objects,
 * allowing one to read {@link BaseAmountDTO}s.</p>
 * <p>Since BaseAmount import only supports INSERT_REPLACE there is no GUID field
 * defined, as INSERT_REPLACE will uniquely identify the BaseAmount using the 
 * ObjectType, ObjectGuid, and Quantity together with the GUID of the PriceListDescriptor 
 * into which the BaseAmount is being imported.</p>
 */
public class ImportDataTypeBaseAmountImpl extends AbstractImportDataTypeImpl implements DtoImportDataType<BaseAmountDTO> {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 7000000001L;
	
	/**
	 * A prefix used in the import data type name.
	 */
	protected static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "Base Amount";
	
	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = "importJobRunnerBaseAmount";

	private static final String MSG_EXPECTING_A_BASEAMOUNTDTO = "Expecting a BaseAmountDTO";

	private static final String IMPORT_DATA_TYPE_MESSAGE_KEY = "ImportDataType_BaseAmount";
	
	// Fields used in cross-field validation.
	private static final String SKU_CODE_IMPORT_FIELD_NAME = "skuCode";
	private static final String PRODUCT_CODE_IMPORT_FIELD_NAME = "productCode";
	private static final String TYPE_IMPORT_FIELD_NAME = "type";


	/**
	 * Creates the various {@code ImportField}s for importing {@code BaseAmount} objects
	 * and adds them to the list of import fields.
	 * @param baseObject not used - should be null.
	 */
	public void init(final Object baseObject) {
		if (baseObject != null) {
			throw new EpDomainException("This class should not be initialized with anything but a null object.");
		}
		
		// Notice : the field creation sequence is the sequence in which the
		// fields are displayed on the import mapping page.
		
		// required fields
		createImportFieldQuantity();
		createImportFieldObjectType();
		createImportFieldListValue();
		//optional fields
		createImportFieldProductCode();
		createImportFieldSkuCode();
		createImportFieldSaleValue();
	}
		
	/**
	 * Creates the {@link com.elasticpath.domain.dataimport.ImportField} for importing BaseAmount Object Type fields,
	 * and adds it to the list of {@code ImportField}s.
	 */
	void createImportFieldObjectType() {
		addImportField(TYPE_IMPORT_FIELD_NAME, new AbstractImportFieldImpl(TYPE_IMPORT_FIELD_NAME, String.class.toString(), true, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((BaseAmountDTO) object).getObjectType();
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				verifyNotNull(value, super.getName());
				if (BaseAmountObjectType.getObjectType(value) == null) {
					throw new EpBindException("Base amounts cannot be imported for this type.");
				}
				((BaseAmountDTO) object).setObjectType(value);
			}
		});
	}
	
		
	/**
	 * Creates the {@link com.elasticpath.domain.dataimport.ImportField} for importing BaseAmount ObjectGuid fields,
	 * and adds it to the list of {@code ImportField}s.
	 */
	void createImportFieldProductCode() {
		addImportField(PRODUCT_CODE_IMPORT_FIELD_NAME, 
				new AbstractImportFieldImpl(PRODUCT_CODE_IMPORT_FIELD_NAME, String.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((BaseAmountDTO) object).getProductCode();
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				((BaseAmountDTO) object).setProductCode(value);
			}
		});
	}

	
	/**
	 * Creates the {@link com.elasticpath.domain.dataimport.ImportField} for importing BaseAmount ObjectType fields,
	 * and adds it to the list of {@code ImportField}s.
	 */
	void createImportFieldSkuCode() {
		addImportField(SKU_CODE_IMPORT_FIELD_NAME, new AbstractImportFieldImpl(SKU_CODE_IMPORT_FIELD_NAME, String.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((BaseAmountDTO) object).getSkuCode();
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				((BaseAmountDTO) object).setSkuCode(value);
			}
		});
	}

	/**
	 * Creates the {@link com.elasticpath.domain.dataimport.ImportField} for importing BaseAmount Quantity fields,
	 * and adds it to the list of {@code ImportField}s.
	 */
	void createImportFieldQuantity() {
		final String importFieldName = "quantity";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((BaseAmountDTO) object).getQuantity());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				verifyNotNull(value, super.getName());
				verifyPositive(value);
				verifyInteger(value);
				try {
					((BaseAmountDTO) object).setQuantity(new BigDecimal(value));
				} catch (NumberFormatException nfe) {
					throw new EpBindException("value is not numeric", nfe);
				}
			}
		});
	}
	
	/**
	 * Creates the {@link com.elasticpath.domain.dataimport.ImportField} for importing BaseAmount ListValue fields,
	 * and adds it to the list of {@code ImportField}s.
	 */
	void createImportFieldListValue() {
		final String importFieldName = "listPrice";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((BaseAmountDTO) object).getListValue());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (!checkNullValue(value)) {
					verifyNonNegative(value);
					try {
						((BaseAmountDTO) object).setListValue(new BigDecimal(value));
					} catch (NumberFormatException nfe) {
						throw new EpBindException("value is not numeric", nfe);
					}
				}				
			}
		});
	}
	
	/**
	 * Creates the {@link com.elasticpath.domain.dataimport.ImportField} for importing BaseAmount SaleValue fields,
	 * and adds it to the list of {@code ImportField}s.
	 */
	void createImportFieldSaleValue() {
		final String importFieldName = "salePrice";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((BaseAmountDTO) object).getSaleValue());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (!checkNullValue(value)) {
					verifyNonNegative(value);
					try {
						((BaseAmountDTO) object).setSaleValue(new BigDecimal(value));
					} catch (NumberFormatException nfe) {
						throw new EpBindException("value is not numeric", nfe);
					}
					
					verifySalePriceNoMoreThanListPrice((BaseAmountDTO) object);
				}
			}
			
			private void verifySalePriceNoMoreThanListPrice(final BaseAmountDTO baseAmountDto) {
				BigDecimal saleValue = baseAmountDto.getSaleValue();
				BigDecimal listValue = baseAmountDto.getListValue();
				if (saleValue == null || listValue == null) {
					return;
				}
				
				if (saleValue.compareTo(listValue) > 0) {
					throw new EpSalePriceExceedListPriceException("Sale price " + saleValue + " should be no more than list price "
							+ listValue);
				}
			}
		});
	}
	
	@Override
	public String getPrefixOfName() {
		return PREFIX_OF_IMPORT_DATA_TYPE_NAME;
	}
	
	/**
	 * This implementation does nothing.
	 * 
	 * @throws EpDomainException never.
	 */
	@Override
	protected void sanityCheck() throws EpDomainException {
		// do nothing
	}

	/**
	 * Checks that the given object is a BaseAmountDTO.
	 * @param object the object to check
	 * @throws EpBindException if the object does not match the expected type
	 */
	@Override
	protected void typeCheck(final Object object) throws EpBindException {
		if (!(object instanceof BaseAmountDTO)) {
			throw new EpBindException(MSG_EXPECTING_A_BASEAMOUNTDTO);
		}
	}

	/**
	 * Add or update the given value object to the given entity. Used to load an imported
	 * ValueObject's associated Entity, attach the ValueObject, and persist the Entity.
	 * Not supported in this implementation because BaseAmount is not a value object,
	 * so needs no special logic to load its associated Entity before save. BaseAmount
	 * objects will be persisted by the ImportJobRunner.
	 * 
	 * @param entity unsupported
	 * @param object unsupported
	 * @throws EpUnsupportedOperationException always.
	 */
	public void saveOrUpdate(final Entity entity, final Persistable object) {
		throw new EpUnsupportedOperationException("Should never reach here.");
	}

	/**
	 * Create and return a new value object.
	 * 
	 * @return a new value object
	 */
	public Persistable createValueObject() {
		throw new EpUnsupportedOperationException("Should never reach here.");
	}

	/**
	 * Not used.
	 * @param entity not used
	 * @throws UnsupportedOperationException
	 */
	public void deleteEntity(final Entity entity) {
		throw new UnsupportedOperationException("Not used in this implementation.");
	}

	/**
	 * Gets the String name of the GUID field on the BaseAmountDTO object.
	 * This implementation returns an empty String because there the GUID field is
	 * irrelevant and we cannot import it.
	 * @return an empty String
	 */
	public String getGuidFieldName() {
		return StringUtils.EMPTY;
	}

	/**
	 * @return the Spring identifier of the implementation of the ImportJobRunner for BaseAmount objects.
	 */
	public String getImportJobRunnerBeanName() {
		return IMPORT_JOB_RUNNER_BEAN_NAME;
	}
	
	/**
	 * Returns the object used to initialize this ImportDataType.
	 * This implementation returns null, because BaseAmount imports require no meta-data
	 * to import them.
	 * 
	 * @return null
	 */
	public Object getMetaObject() {
		return null;
	}

	/**
	 * Returns the localization key for the name of this ImportDataType.
	 * Usually this is used in the UI for displaying the list of 
	 * object types that can to be imported.
	 * @return the localization key
	 */
	public String getNameMessageKey() {
		return IMPORT_DATA_TYPE_MESSAGE_KEY;
	}

	/**
	 * Returns <code>true</code> if this ImportDataType creates an Entity.
	 * This implementation does.
	 * 
	 * @return <code>true</code>.
	 */
	public boolean isEntityImport() {
		return true;
	}

	/**
	 * Returns {@code true} if this import data type imports a value object.
	 * This implementation does not.
	 * Calls {@link #isEntityImport()}.
	 * 
	 * @return {@code false}
	 */
	public boolean isValueObjectImport() {
		return !isEntityImport();
	}

	/**
	 * Gets an instance of the bean that is to be populated upon import.
	 * @return the prototype bean instance
	 */
	public BaseAmountDTO getPrototypeDtoBean() {
		return new BaseAmountDTO();
	}
	
	/**
	 * Validate the bean as a whole after all fields are populated.
	 * @param dto the dto to populate
	 * @return the validated bean
	 * throws EpBindException if the bean cannot is not valid
	 */
	public BaseAmountDTO validatePopulatedDtoBean(final BaseAmountDTO dto) {
		verifyRequiredCodes(dto);
		return dto;
	}

	private void verifyRequiredCodes(final BaseAmountDTO dto) {
		BaseAmountObjectType type = BaseAmountObjectType.getObjectType(dto.getObjectType());
		if (BaseAmountObjectType.PRODUCT.equals(type)
				&& StringUtils.isBlank(dto.getProductCode())) {
			throw new EpRequiredDependentFieldsMissingBindException(TYPE_IMPORT_FIELD_NAME, dto.getObjectType(), PRODUCT_CODE_IMPORT_FIELD_NAME);
		} else if (BaseAmountObjectType.SKU.equals(type)
				&& StringUtils.isBlank(dto.getSkuCode())) {
			throw new EpRequiredDependentFieldsMissingBindException(TYPE_IMPORT_FIELD_NAME, dto.getObjectType(), SKU_CODE_IMPORT_FIELD_NAME);
		}
	}
	
	/**
	 * @return only the import types supported by Price List imports.
	 */
	@Override
	public List <ImportType> getSupportedImportTypes() {
		List <ImportType> typeList = new ArrayList <ImportType>();
		typeList.add(AbstractImportTypeImpl.INSERT_UPDATE_TYPE);
		return typeList;
	}

}
