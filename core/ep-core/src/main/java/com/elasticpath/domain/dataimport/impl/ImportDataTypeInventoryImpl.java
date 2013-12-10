package com.elasticpath.domain.dataimport.impl;

import java.util.Date;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.dataimport.ImportGuidHelper;

/**
 * Defines fields and necessary information to import <code>Inventory</code>.
 */
public class ImportDataTypeInventoryImpl extends AbstractImportDataTypeImpl {
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * A prefix used in product import data type name.
	 */
	protected static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "Inventory";

	/**
	 * An import data type message key. Usually, key for a message before separator.
	 */
	protected static final String IMPORT_DATA_TYPE_MESSAGE_KEY = "ImportDataType_Inventory";

	/**
	 * A prefix used in product import field name.
	 */
	protected static final String PREFIX_OF_FIELD_NAME = "";

	private String guidFieldName;

	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = "importJobRunnerInventory";

	private static final String MSG_EXPECTING_AN_INVENTORY = "Expecting an inventory.";

	/**
	 * This is value object. Doesn't hold meta object.
	 * 
	 * @param dummyObject not used, must give <code>null</code>.
	 */
	public void init(final Object dummyObject) {
		if (dummyObject != null) {
			throw new EpDomainException("You are not supposed to give any value to a dummy object.");
		}

		// Notice : the sequence of a field get created will be applied to the sequence that the field get displayed in the
		// import mapping page.

		// General
		createImportFieldProductSku();
		createImportFieldQuantityOnHand();
		createImportFieldReservedQuantity();
		createImportFieldReorderMinimum();
		createImportFieldReorderQuantity();
		createImportFieldRestockDate();
	}

	private void createImportFieldQuantityOnHand() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "quantityOnHand";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((Inventory) object).getQuantityOnHand());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				int quantityOnHand = ConverterUtils.string2Int(value);
				validateQuantityOnHand(quantityOnHand);
				((Inventory) object).setQuantityOnHand(quantityOnHand);
			}
			
			/**
			 * Since the validation logic is encoded in the domain object, create a 
			 * temp object for validation and then throw it away.
			 *
			 * @param quantityOnHand This is the physical number of units that the warehouse has in stock.
			 */
			protected void validateQuantityOnHand(final int quantityOnHand) {
				new InventoryImpl().setQuantityOnHand(quantityOnHand);
			}
		});
	}

	private void createImportFieldReservedQuantity() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "reservedQuantity";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((Inventory) object).getReservedQuantity());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				int reservedQuantity = ConverterUtils.string2Int(value);
				validateReservedQuantity(reservedQuantity);
				((Inventory) object).setReservedQuantity(reservedQuantity);
			}

			/**
			 * Since the validation logic is encoded in the domain object,
			 * create a temp object for validation and then throw it away.
			 *
			 * @param reservedQuantity This is the number of units that exist in the warehouse, but are not available for purchase.
			 */
			protected void validateReservedQuantity(final int reservedQuantity) {
				new InventoryImpl().setReservedQuantity(reservedQuantity);
			}
		});
	}

	private void createImportFieldReorderMinimum() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "reorderMinimum";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((Inventory) object).getReorderMinimum());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				int reorderMinimum = ConverterUtils.string2Int(value);
				validateReorderMinimum(reorderMinimum);
				((Inventory) object).setReorderMinimum(reorderMinimum);
			}

			/**
			 * Since the validation logic is encoded in the domain object,
			 * create a temp object for validation and then throw it away.
			 *
			 * @param reorderMinimum Once the quantity on hand goes below this number, then the item should be re-ordered.
			 */
			protected void validateReorderMinimum(final int reorderMinimum) {
				new InventoryImpl().setReorderMinimum(reorderMinimum);
			}
		});
	}

	private void createImportFieldProductSku() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "productSku";
		this.guidFieldName = importFieldName;
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, true) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((Inventory) object).getSkuCode();
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!getUtility().isValidGuidStr(value)) {
					throw new EpInvalidValueBindException(super.getName());
				}

				final ProductSku productSku = service.findProductSkuByGuid(value);
				
				if (productSku == null) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
				
				((Inventory) object).setSkuCode(productSku.getSkuCode());
				
			}

		});
	}

	private void createImportFieldReorderQuantity() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "reorderQuantity";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {
			
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;
			
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((Inventory) object).getReorderQuantity());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				//((Inventory) object).setReorderQuantity(ConverterUtils.string2Int(value));
				int reorderQuantity = ConverterUtils.string2Int(value);
				validateReorderQuantity(reorderQuantity);
				((Inventory) object).setReorderQuantity(reorderQuantity);
			}

			/**
			 * Since the validation logic is encoded in the domain object,
			 * create a temp object for validation and then throw it away.
			 *
			 * @param reorderQuantity This is the number of items a customer currently want to reorder, must be more than reorderMinimum.
			 */
			protected void validateReorderQuantity(final int reorderQuantity) {
				new InventoryImpl().setReorderQuantity(reorderQuantity);
			}
		});
	}

	private void createImportFieldRestockDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "restockDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {

			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				final Date date = ((Inventory) object).getRestockDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtility().getDefaultLocalizedDateFormat());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}
				((Inventory) object).setRestockDate(ConverterUtils.string2Date(value, getUtility().getDefaultLocalizedDateFormat()));
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
	 * Returns <code>true</code> if allowing to create entity.
	 * 
	 * @return <code>true</code> if allowing to create entity
	 */
	public boolean isEntityImport() {
		return true;
	}

	/**
	 * Returns <code>true</code> if this import data type imports value object.
	 * 
	 * @return <code>true</code> if this import data type imports value object
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
	 * Clear the value objects of the given entity.
	 * 
	 * @param entity the entity
	 */
	/*
	 * public void clearValueObjects(final Entity entity) { final ProductSku productSku = (ProductSku) entity; productSku.getInventory().clear(); //
	 * isn't implemented yet. }
	 */

	/**
	 * Check the type of the given persistence object.
	 * 
	 * @param object the persistence object
	 * @throws EpBindException -- in case the type doesn't match
	 */
	protected void typeCheck(final Object object) throws EpBindException {
		if (!(object instanceof Inventory)) {
			throw new EpBindException(MSG_EXPECTING_AN_INVENTORY);
		}
	}

	/**
	 * Delete entity.
	 * 
	 * @param entity for delete.
	 */
	public void deleteEntity(final Entity entity) {
		ProductInventoryManagementService pims = getBean(ContextIdNames.PRODUCT_INVENTORY_MANAGEMENT_SERVICE);
		InventoryDto inventoryDto = pims.assembleDtoFromDomain((Inventory) entity);
		pims.remove(inventoryDto);
	}

	@Override
	public String getPrefixOfName() {
		return PREFIX_OF_IMPORT_DATA_TYPE_NAME;
	}

}
