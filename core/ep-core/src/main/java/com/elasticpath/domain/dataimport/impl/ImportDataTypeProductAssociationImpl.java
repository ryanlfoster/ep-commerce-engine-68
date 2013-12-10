package com.elasticpath.domain.dataimport.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.dataimport.ImportGuidHelper;

/**
 * Defines fields and necessary information to import <code>ProductAssociation</code>.
 */
public class ImportDataTypeProductAssociationImpl extends AbstractImportDataTypeImpl {
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private static final String SHOULD_NEVER_REACH_HERE = "Should never reach here.";

	/**
	 * A prefix used in product import data type name.
	 */
	protected static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "Product Association";
	
	/**
	 * An import data type message key. Usually, key for a message before separator.
	 */
	protected static final String IMPORT_DATA_TYPE_MESSAGE_KEY = "ImportDataType_ProductAssociation";

	/**
	 * A prefix used in product import field name.
	 */
	protected static final String PREFIX_OF_FIELD_NAME = "";

	private String guidFieldName;

	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = "importJobRunnerProductAssociation";

	private static final String MSG_EXPECTING_A_PRODUCT_ASSOCIATION = "Expecting a product association.";
	
	/** Source Product Code field name. */
	public static final String FIELD_NAME_SOURCE_PRODUCT_CODE = PREFIX_OF_FIELD_NAME + "sourceProductCode";
	/** Target Product Code field name. */
	public static final String FIELD_NAME_TARGET_PRODUCT_CODE = PREFIX_OF_FIELD_NAME + "targetProductCode";
	/** AssociationType field name. */
	public static final String FIELD_NAME_ASSOCIATION_TYPE = PREFIX_OF_FIELD_NAME + "associationType";

	/**
	 * Initialize the product import type.
	 * 
	 * @param dummyObject not used, must give <code>null</code>.
	 */
	public void init(final Object dummyObject) {
		if (dummyObject != null) {
			throw new EpDomainException("You are not supposed to give any value to a dummy object.");
		}

		// Notice: The fields are imported in the order in which they are
		//         created below.

		// General
		createImportFieldSourceProduct();
		createImportFieldTargetProduct();
		createImportFieldAssociationType();

		createImportFieldStartDate();
		createImportFieldEndDate();
		createImportFieldDefaultQty();
		createImportFieldSourceProductDependant();
		createImportFieldOrdering();
	}

	private void createImportFieldOrdering() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "ordering";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((ProductAssociation) object).getOrdering());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				((ProductAssociation) object).setOrdering(ConverterUtils.string2Int(value));
			}
		});

	}

	private void createImportFieldSourceProductDependant() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "sourceProductDependant";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Boolean.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((ProductAssociation) object).isSourceProductDependent());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				((ProductAssociation) object).setSourceProductDependent(ConverterUtils.string2Boolean(value));
			}
		});
	}

	private void createImportFieldDefaultQty() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "defaultQuantity";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((ProductAssociation) object).getDefaultQuantity());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				((ProductAssociation) object).setDefaultQuantity(ConverterUtils.string2Int(value));
			}
		});
	}
	
	private void createImportFieldAssociationType() {
		addImportField(FIELD_NAME_ASSOCIATION_TYPE, new AbstractImportFieldImpl(FIELD_NAME_ASSOCIATION_TYPE, String.class.toString(), true, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((ProductAssociation) object).getAssociationType());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					if (isRequired()) {
						throw new EpNonNullBindException(super.getName());
					}
					return;
				}

				final int associationType = ConverterUtils.string2Int(value);
				final ProductAssociation productAssociation = (ProductAssociation) object;
				if (!productAssociation.isValidAssociationType(associationType)) {
					throw new EpInvalidValueBindException(super.getName());
				}
				((ProductAssociation) object).setAssociationType(associationType);
			}
		});
	}

	private void createImportFieldTargetProduct() {
		addImportField(FIELD_NAME_TARGET_PRODUCT_CODE, 
				new AbstractImportFieldImpl(FIELD_NAME_TARGET_PRODUCT_CODE, String.class.toString(), true, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((ProductAssociation) object).getTargetProduct().getGuid();
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					if (isRequired()) {
						throw new EpNonNullBindException(super.getName());
					}
					return;
				}

				final Product product = service.findProductByGuid(value, false, false, true);
				if (product == null) {
					throw new EpInvalidGuidBindException(FIELD_NAME_TARGET_PRODUCT_CODE);
				}
				((ProductAssociation) object).setTargetProduct(product);
			}

			@Override
			public void checkStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					if (isRequired()) {
						throw new EpNonNullBindException(super.getName());
					}
					return;
				}
				if (!service.isProductGuidExist(value)) {
					throw new EpInvalidGuidBindException(FIELD_NAME_TARGET_PRODUCT_CODE);
				}
			}
		});
	}

	private void createImportFieldSourceProduct() {
		this.guidFieldName = FIELD_NAME_SOURCE_PRODUCT_CODE;
		addImportField(FIELD_NAME_SOURCE_PRODUCT_CODE, 
				new AbstractImportFieldImpl(FIELD_NAME_SOURCE_PRODUCT_CODE, String.class.toString(), true, true) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				throw new EpUnsupportedOperationException(SHOULD_NEVER_REACH_HERE);
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!service.isProductGuidExist(value)) {
					throw new EpInvalidGuidBindException(FIELD_NAME_SOURCE_PRODUCT_CODE);
				}
				
				final Product product = service.findProductByGuid(value, false, false, true);
				if (product == null) {
					throw new EpInvalidGuidBindException(FIELD_NAME_SOURCE_PRODUCT_CODE);
				}
				((ProductAssociation) object).setSourceProduct(product);

			}
		});
	}

	private void createImportFieldStartDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "enableDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				final Date date = ((ProductAssociation) object).getStartDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtility().getDefaultLocalizedDateFormat());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					((ProductAssociation) object).setStartDate(new Date());
				} else {
					((ProductAssociation) object).setStartDate(ConverterUtils.string2Date(value, getUtility().getDefaultLocalizedDateFormat()));
				}
			}
		});
	}

	private void createImportFieldEndDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "disableDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				final Date date = ((ProductAssociation) object).getEndDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtility().getDefaultLocalizedDateFormat());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					((ProductAssociation) object).setEndDate(null);
					return;
				}

				((ProductAssociation) object).setEndDate(ConverterUtils.string2Date(value, getUtility().getDefaultLocalizedDateFormat()));
			}
		});
	}
	
	/**
	 * Returns import data type name message key. This is a key for a message before separator.
	 * 
	 * @return message key.
	 */
	public String getNameMessageKey() {
		return IMPORT_DATA_TYPE_MESSAGE_KEY;
	}

	/**
	 * Return the field name for guid. If it doesn't exist, return <code>null</code>.
	 * 
	 * @return the field name for guid
	 */
	public String getGuidFieldName() {
		return this.guidFieldName;
	}

	/**
	 * Do a sanity check.
	 * 
	 * @throws EpDomainException in case the sanity check fails.
	 */
	@Override
	protected void sanityCheck() throws EpDomainException {
		// do nothing
	}

	/**
	 * Returns the meta object used to initialize the import data type.
	 * 
	 * @return the meta object used to initialize the import data type
	 */
	public Object getMetaObject() {
		return null;
	}

	/**
	 * Returns the import job runner bean name.
	 * 
	 * @return the import job runner bean name.
	 */
	public String getImportJobRunnerBeanName() {
		return IMPORT_JOB_RUNNER_BEAN_NAME;
	}

	/**
	 * Returns <code>true</code> as product associations are entities.
	 * 
	 * @return <code>true</code> as product associations are entities
	 */
	public boolean isEntityImport() {
		return !isValueObjectImport();
	}

	/**
	 * Returns <code>false</code> as product associations are not value objects.
	 * 
	 * @return <code>false</code> as product associations are not value objects.
	 */
	public boolean isValueObjectImport() {
		return false;
	}

	/**
	 * Add or update the given value object to the given entity.
	 * 
	 * @param entity the entity
	 * @param object the value object
	 */
	public void saveOrUpdate(final Entity entity, final Persistable object) {
		throw new UnsupportedOperationException(SHOULD_NEVER_REACH_HERE);
	}

	/**
	 * Create and return a new value object.
	 * 
	 * @return a new value object
	 */
	public Persistable createValueObject() {
		throw new UnsupportedOperationException(SHOULD_NEVER_REACH_HERE);
	}

	/**
	 * Clear the value objects of the given entity.
	 * 
	 * @param entity the entity
	 */
	@Override
	public void clearValueObjects(final Entity entity) {
		throw new UnsupportedOperationException(SHOULD_NEVER_REACH_HERE);
	}

	/**
	 * Check the type of the given persistence object.
	 * 
	 * @param object the persistence object
	 * @throws EpBindException -- in case the type doesn't match
	 */
	@Override
	protected void typeCheck(final Object object) throws EpBindException {
		if (!(object instanceof ProductAssociation)) {
			throw new EpBindException(MSG_EXPECTING_A_PRODUCT_ASSOCIATION);
		}
	}
	
	/**
	 * Delete entity.
	 * @param entity for delete.
	 */
	public void deleteEntity(final Entity entity) {
		throw new UnsupportedOperationException(SHOULD_NEVER_REACH_HERE);
	}
	
	@Override
	public String getPrefixOfName() {
		return PREFIX_OF_IMPORT_DATA_TYPE_NAME;
	}
	
	@Override
	public List<ImportType> getSupportedImportTypes() {
		List<ImportType> importTypes = new ArrayList<ImportType>();
		importTypes.add(AbstractImportTypeImpl.INSERT_TYPE);
		importTypes.add(AbstractImportTypeImpl.DELETE_TYPE);
		importTypes.add(AbstractImportTypeImpl.INSERT_UPDATE_TYPE);
		return importTypes;
	}
}
