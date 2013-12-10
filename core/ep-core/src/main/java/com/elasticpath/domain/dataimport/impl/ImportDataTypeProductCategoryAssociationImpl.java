package com.elasticpath.domain.dataimport.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.dataimport.ImportGuidHelper;

/**
 * Defines fields and necessary information to import <code>ProductCategory</code>.
 */
public class ImportDataTypeProductCategoryAssociationImpl extends AbstractImportDataTypeImpl {
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * A prefix used in product import data type name.
	 */
	protected static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "Product / Category Association";
	
	/**
	 * An import data type message key. Usually, key for a message before separator.
	 */
	protected static final String IMPORT_DATA_TYPE_MESSAGE_KEY = "ImportDataType_ProductCategoryAssociation";

	/**
	 * A prefix used in product import field name.
	 */
	protected static final String PREFIX_OF_FIELD_NAME = "";

	private String guidFieldName;

	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = "importJobRunnerProduct";

	private static final String MSG_EXPECTING_A_PRODUCT_CATEGORY_ASSOCATION = "Expecting a product category association.";

	/**
	 * Initialize the product price import type.
	 *
	 * @param dummyObject not used.
	 */
	public void init(final Object dummyObject) {
		if (dummyObject != null) {
			throw new EpDomainException("You are not supposed to give any value to a dummy object.");
		}

		// Notice : the sequence of a field get created will be applied to the sequence that the field get displayed in the
		// import mapping page.

		// General
		createImportFieldProductGuid();
		createImportFieldCategoryGuid();
		createImportFieldFeaturedProductOrder();
	}

	private void createImportFieldCategoryGuid() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "categoryCode";
		addImportField(importFieldName, new AbstractCatalogImportFieldImpl(importFieldName, String.class.toString(), true, true) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object productCategory) {
				return ((ProductCategoryImportBean) productCategory).getCategory().getGuid();
			}

			public void setStringValue(final Object productCategory, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				final Category category = service.findCategoryByGuidAndCatalogGuid(value, getCatalog().getGuid(), false, false);
				if (category == null) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
				((ProductCategoryImportBean) productCategory).setCategory(category);
			}

			@Override
			public void checkStringValue(final Object persistenceObject, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}
				if (!service.isCategoryGuidExist(value, getCatalog().getGuid())) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
			}
		});
	}

	private void createImportFieldProductGuid() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "productCode";
		this.guidFieldName = importFieldName;
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, true) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object productCategory) {
				throw new EpUnsupportedOperationException("Should never reach here.");
			}

			public void setStringValue(final Object productCategory, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!service.isProductGuidExist(value)) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
				// do nothing
			}
		});
	}

	private void createImportFieldFeaturedProductOrder() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "featuredProductOrder";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object productCategory) {
				return String.valueOf(((ProductCategoryImportBean) productCategory).getFeaturedProductOrder());
			}

			public void setStringValue(final Object productCategory, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				((ProductCategoryImportBean) productCategory).setFeaturedProductOrder(Integer.parseInt(value));
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
	 * Returns the meta object used to intialize the import data type.
	 *
	 * @return the meta object used to intialize the import data type
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
	 * Returns <code>true</code> if allowing to create entity.
	 *
	 * @return <code>true</code> if allowing to create entity
	 */
	public boolean isEntityImport() {
		return false;
	}

	/**
	 * Returns <code>true</code> if this import data type imports value object.
	 *
	 * @return <code>true</code> if this import data type imports value object
	 */
	public boolean isValueObjectImport() {
		return true;
	}

	/**
	 * Add or update the given value object to the given entity.
	 *
	 * @param entity the entity
	 * @param object the value object
	 */
	public void saveOrUpdate(final Entity entity, final Persistable object) {
		final Product srcProduct = (Product) entity;
		final ProductCategoryImportBean productCategory = (ProductCategoryImportBean) object;

		//For the purposes of this import, a ProductCategoryAssocation simply contains 
		//a Product, a Category, and a FeaturedProductOrder. Therefore, we need to add
		// the given category to the given product, then set the featured product order.
		srcProduct.addCategory(productCategory.getCategory());
		srcProduct.setFeaturedRank(productCategory.getCategory(), productCategory.getFeaturedProductOrder());
	}

	/**
	 * Create and return a new value object.
	 *
	 * @return a new value object
	 */
	public Persistable createValueObject() {
		return new ProductCategoryImportBean();
	}

	/**
	 * Clear the value objects of the given entity.
	 *
	 * @param entity the entity
	 */
	@Override
	public void clearValueObjects(final Entity entity) {
		final Product product = (Product) entity;
		product.removeAllCategories();
	}

	/**
	 * Check the type of the given persistence object.
	 *
	 * @param object the persistence object
	 * @throws EpBindException -- in case the type doesn't match
	 */
	@Override
	protected void typeCheck(final Object object) throws EpBindException {
		if (!(object instanceof ProductCategory)) {
			throw new EpBindException(MSG_EXPECTING_A_PRODUCT_CATEGORY_ASSOCATION);
		}
	}
	
	/**
	 * Delete entity.
	 * @param entity for delete.
	 */
	public void deleteEntity(final Entity entity) {
		throw new EpUnsupportedOperationException("Should never reach here.");
	}
	
	@Override
	public String getPrefixOfName() {
		return PREFIX_OF_IMPORT_DATA_TYPE_NAME;
	}

	@Override
	public List<ImportType> getSupportedImportTypes() {
		List<ImportType> importTypes = new ArrayList<ImportType>();
		importTypes.add(AbstractImportTypeImpl.INSERT_UPDATE_TYPE);
		importTypes.add(AbstractImportTypeImpl.DELETE_TYPE);
		return importTypes;
	}
	
	
}
