/**
 * 
 */
package com.elasticpath.domain.pricing.csvimport.impl;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.csvimport.DtoImportDataType;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.dataimport.impl.AbstractImportDataTypeImpl;
import com.elasticpath.domain.dataimport.impl.AbstractImportFieldImpl;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.dataimport.ImportGuidHelper;

/**
 * A data type for BaseAmount import for header.
 *
 */
public class ImportDataTypeHeaderForBaseAmountImpl extends AbstractImportDataTypeImpl implements DtoImportDataType<PriceListDescriptorDTO> {

	private static final long serialVersionUID = 1L;

	private static final int THREE_ITEMS_3 = 3;

	private static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "Price List Descriptor";

	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = "importJobRunnerBaseAmount";

	private static final String MSG_EXPECTING = "Expecting a PriceListDescriptorDTO";

	private static final String IMPORT_DATA_TYPE_MESSAGE_KEY = "ImportDataType_BaseAmount";


	@Override
	public String getPrefixOfName() {
		return PREFIX_OF_IMPORT_DATA_TYPE_NAME;
	}

	@Override
	protected void sanityCheck() throws EpDomainException {
		// empty
	}

	@Override
	protected void typeCheck(final Object object) throws EpBindException {
		if (!(object instanceof PriceListDescriptorDTO)) {
			throw new EpBindException(MSG_EXPECTING);
		}
	}

	@Override
	public PriceListDescriptorDTO getPrototypeDtoBean() {
		return new PriceListDescriptorDTO();
	}

	/**
	 * Validate the bean as a whole after all fields are populated.
	 * @param dto the dto to populate
	 * @return the validated bean
	 * throws EpBindException if the bean is not valid
	 */
	public PriceListDescriptorDTO validatePopulatedDtoBean(final PriceListDescriptorDTO dto) {
		return dto;
	}


	@Override
	public Persistable createValueObject() {
		throw new EpUnsupportedOperationException("Should never reach here.");
	}

	@Override
	public void deleteEntity(final Entity entity) {
		throw new UnsupportedOperationException("Not used in this implementation.");
	}

	@Override
	public String getGuidFieldName() {
		return StringUtils.EMPTY;
	}

	@Override
	public String getImportJobRunnerBeanName() {
		return IMPORT_JOB_RUNNER_BEAN_NAME;
	}

	@Override
	public Object getMetaObject() {
		return null;
	}

	@Override
	public String getNameMessageKey() {
		return IMPORT_DATA_TYPE_MESSAGE_KEY;
	}

	@Override
	public void init(final Object baseObject) {
		if (baseObject != null) {
			throw new EpDomainException("This class should not be initialized with anything but a null object.");
		}
		addImportField("type", new DummyImportField("type", String.class.toString(), false, false));
		createImportFieldListPrice();
		createImportFieldSalePrice();
		addImportField("productCode", new DummyImportField("productCode", String.class.toString(), false, false));
		addImportField("skuCode", new DummyImportField("skuCode", String.class.toString(), false, false));
		addImportField("quantity", new DummyImportField("quantity", String.class.toString(), false, false));
	}

	/**
	 * Default init method for import fields initialization.
	 */
	public void init() {
		this.init(null);
	}

	@Override
	public boolean isEntityImport() {
		return true;
	}

	@Override
	public boolean isValueObjectImport() {
		return !isEntityImport();
	}

	@Override
	public void saveOrUpdate(final Entity entity, final Persistable object) {
		throw new EpUnsupportedOperationException("Should never reach here.");
	}


	private void createImportFieldListPrice() {

		final String importFieldName = "listPrice";

		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, false) {

			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				final PriceListDescriptorDTO dto = (PriceListDescriptorDTO) object;
				return importFieldName + "_" + dto.getName() + "_" + dto.getCurrencyCode();
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				verifyNotNull(value, super.getName());

				final String[] items = value.split("_");
				if (items.length != THREE_ITEMS_3) {
					throw new EpBindException("Wrong header name definition for [" + importFieldName + "] column");
				}

				((PriceListDescriptorDTO) object).setName(items[1]);
				((PriceListDescriptorDTO) object).setCurrencyCode(items[2]);
			}
		});
	}

	private void createImportFieldSalePrice() {

		final String importFieldName = "salePrice";

		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, false) {

			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				final PriceListDescriptorDTO dto = (PriceListDescriptorDTO) object;
				return importFieldName + "_" + dto.getName() + "_" + dto.getCurrencyCode();
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				verifyNotNull(value, super.getName());

				final String[] items = value.split("_");
				if (items.length != THREE_ITEMS_3) {
					throw new EpBindException("Wrong header name definition for [" + importFieldName + "] column");
				}

				((PriceListDescriptorDTO) object).setName(items[1]);
				((PriceListDescriptorDTO) object).setCurrencyCode(items[2]);
			}
		});
	}

	/** A dummy class for other non-meaning fields in header. */
	private class DummyImportField extends AbstractImportFieldImpl {

		private static final long serialVersionUID = 1L;

		public DummyImportField(final String name, final String type, final boolean required, final boolean primaryRequired) {
			super(name, type, required, primaryRequired);
		}
		public String getStringValue(final Object object) {
			return null;
		}
		public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
			// empty
		}
	}
}

