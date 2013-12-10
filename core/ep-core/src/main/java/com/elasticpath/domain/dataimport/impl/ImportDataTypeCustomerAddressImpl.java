package com.elasticpath.domain.dataimport.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.dataimport.ImportGuidHelper;

/**
 * Defines fields and necessary information to import <code>Customer</code>.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ImportDataTypeCustomerAddressImpl extends AbstractImportDataTypeImpl {
	private static final String MSG_EXPECTING_A_CUSTOMER = "Expecting a customer.";

	private static final Logger LOG = Logger.getLogger(ImportDataTypeCustomerAddressImpl.class);

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * A prefix used in product import data type name.
	 */
	protected static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "Customer Address";

	/**
	 * An import data type message key. Usually, key for a message before separator.
	 */
	protected static final String IMPORT_DATA_TYPE_MESSAGE_KEY = "ImportDataType_CustomerAddress";

	/**
	 * A prefix used in product import field name.
	 */
	public static final String PREFIX_OF_FIELD_NAME = "";

	private String guidFieldName;

	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = "importJobRunnerCustomer";

	/**
	 * Initialize the category import type.
	 * 
	 * @param object not used
	 */
	public void init(final Object object) {
		if (object != null) {
			throw new EpDomainException("object in this case should be null.");
		}

		// Notice : the field creation sequence is the sequence in which the fields
		// are displayed on the import mapping page.
		// required fields
		createImportFieldGuid();
		createImportFieldCustomerGuid();
		// optional fields
		createImportFieldFirstName();
		createImportFieldLastName();
		createImportFieldPhoneNumber();
		createImportFieldFaxNumber();
		createImportFieldStreet1();
		createImportFieldStreet2();
		createImportFieldCity();
		createImportFieldSubCountry();
		createImportFieldCountry();
		createImportFieldZipPostalCode();
		createImportFieldCommercialFlag(); // false by default
	}

	private void createImportFieldGuid() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "guid";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, true) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((CustomerAddress) object).getGuid();
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!getUtility().isValidGuidStr(value)) {
					throw new EpInvalidGuidBindException(super.getName());
				}

				((CustomerAddress) object).setGuid(value);
			}
		});
	}

	private void createImportFieldCustomerGuid() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "customerGuid";
		this.guidFieldName = importFieldName; // The guidFieldName is the customerGuid since this is a value object
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				throw new EpUnsupportedOperationException("Should never reach here.");
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!service.isCustomerGuidExist(value)) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
				// do nothing
			}
		});
	}

	private void createImportFieldFirstName() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "firstName";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((CustomerAddress) object).getFirstName());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				// if (!getUtility().isAlphaNumeric(value)) {
				// throw new EpInvalidValueBindException(super.getName());
				// }

				((CustomerAddress) object).setFirstName(value);
			}
		});
	}

	private void createImportFieldLastName() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "lastName";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((CustomerAddress) object).getLastName());
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				// if (!getUtility().isAlphaNumeric(value)) {
				// throw new EpInvalidValueBindException(super.getName());
				// }

				((CustomerAddress) object).setLastName(value);
			}
		});
	}

	private void createImportFieldPhoneNumber() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "phoneNumber";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf((((CustomerAddress) object).getPhoneNumber()));
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				((CustomerAddress) object).setPhoneNumber(value);
			}
		});
	}

	private void createImportFieldFaxNumber() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "faxNumber";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf((((CustomerAddress) object).getFaxNumber()));
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				((CustomerAddress) object).setFaxNumber(value);
			}
		});
	}

	private void createImportFieldStreet1() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "street1";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf((((CustomerAddress) object).getStreet1()));
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				((CustomerAddress) object).setStreet1(value);
			}
		});
	}

	private void createImportFieldStreet2() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "street2";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf((((CustomerAddress) object).getStreet2()));
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				((CustomerAddress) object).setStreet2(value);
			}
		});
	}

	private void createImportFieldCity() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "city";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf((((CustomerAddress) object).getCity()));
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				((CustomerAddress) object).setCity(value);
			}
		});
	}

	private void createImportFieldSubCountry() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "subCountry";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf((((CustomerAddress) object).getSubCountry()));
			}

			@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				if (!StringUtils.isAlphanumeric(value)) {
					throw new EpInvalidValueBindException(super.getName());
				}

				Geography geography = getBean(ContextIdNames.GEOGRAPHY);
				boolean valid = false;
				for (String countryCode : geography.getCountryCodes()) {
					valid |= geography.getSubCountryCodes(countryCode).contains(value);
				}
				if (!valid) {
					throw new EpInvalidValueBindException(super.getName() + "Invalid SubCountry Code");
				}

				((CustomerAddress) object).setSubCountry(value);
			}
		});
	}

	private void createImportFieldCountry() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "country";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf((((CustomerAddress) object).getCountry()));
			}

			@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				if (!StringUtils.isAlphanumeric(value)) {
					throw new EpInvalidValueBindException(super.getName());
				}

				Geography geography = getBean(ContextIdNames.GEOGRAPHY);
				if (!geography.getCountryCodes().contains(value)) {
					throw new EpInvalidValueBindException(super.getName() + "Invalid Country Code");
				}

				((CustomerAddress) object).setCountry(value);
			}
		});
	}

	private void createImportFieldZipPostalCode() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "zipOrPostalCode";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf((((CustomerAddress) object).getZipOrPostalCode()));
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				if (!getUtility().isValidZipPostalCode(value)) {
					throw new EpInvalidValueBindException(super.getName());
				}

				((CustomerAddress) object).setZipOrPostalCode(value);
			}
		});
	}

	private void createImportFieldCommercialFlag() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "commercialAddress";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {
			/**
			 * Serial version ID.
			 */
			private static final long serialVersionUID = 5000000001L;

			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf((((CustomerAddress) object).isCommercialAddress()));
			}

			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					((CustomerAddress) object).setCommercialAddress(false);
					return;
				}

				((CustomerAddress) object).setCommercialAddress(ConverterUtils.string2Boolean(value));
			}
		});
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
	 * Add or update this CustomerAddress in the Customer object.
	 * 
	 * @param entity the entity
	 * @param object the value object
	 */
	public void saveOrUpdate(final Entity entity, final Persistable object) {
		final String methodSignature = "ImportDataTypeCustomerAddressImpl.saveOrUpdate(Entity, ValueObject)";
		final Customer srcCustomer = (Customer) entity;
		final CustomerAddress importedAddress = (CustomerAddress) object;

		// Find out if this address already exists
		CustomerAddress existingAddress = srcCustomer.getAddressByGuid(importedAddress.getGuid());
		// add it
		if (existingAddress == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodSignature + " adding CustomerAddress guid=" + importedAddress.getGuid());
			}
			srcCustomer.addAddress(importedAddress);
			return;
		}
		// update it
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodSignature + " updating CustomerAddress guid=" + existingAddress.getGuid());
		}
		
		updateAddress(importedAddress, existingAddress);
	}

	/**
	 * Updates the address fields from the importedAddress to the existingAddress.
	 * 
	 * @param importedAddress the imported address
	 * @param existingAddress the existing in the system address
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void updateAddress(final CustomerAddress importedAddress, final CustomerAddress existingAddress) {
		
		if (importedAddress.getFirstName() != null) {
			existingAddress.setFirstName(importedAddress.getFirstName());
		}
		if (importedAddress.getLastName() != null) {
			existingAddress.setLastName(importedAddress.getLastName());
		}
		if (importedAddress.getCity() != null) {
			existingAddress.setCity(importedAddress.getCity());
		}
		if (importedAddress.getCountry() != null) {
			existingAddress.setCountry(importedAddress.getCountry());
		}
		if (importedAddress.getFaxNumber() != null) {
			existingAddress.setFaxNumber(importedAddress.getFaxNumber());
		}
		if (importedAddress.getPhoneNumber() != null) {
			existingAddress.setPhoneNumber(importedAddress.getPhoneNumber());
		}
		if (importedAddress.getSubCountry() != null) {
			existingAddress.setSubCountry(importedAddress.getSubCountry());
		}
		if (importedAddress.getStreet1() != null) {
			existingAddress.setStreet1(importedAddress.getStreet1());
		}
		if (importedAddress.getStreet2() != null) {
			existingAddress.setStreet2(importedAddress.getStreet2());
		}
		if (importedAddress.getZipOrPostalCode() != null) {
			existingAddress.setZipOrPostalCode(importedAddress.getZipOrPostalCode());
		}
		existingAddress.setCommercialAddress(importedAddress.isCommercialAddress());
	}

	/**
	 * Clear the value objects of the given entity.
	 * 
	 * @param entity the entity
	 */
	public void clearValueObjects(final Entity entity) {
		// customer addresses won't be cleared as
		// they have to be updated in case ImportType.UPDATE is used
	}

	/**
	 * Create and return a new value object.
	 * 
	 * @return a new value object
	 */
	public Persistable createValueObject() {
		return getBean(ContextIdNames.CUSTOMER_ADDRESS);
	}

	/**
	 * Check the type of the given persistence object.
	 * 
	 * @param object the persistence object
	 * @throws EpBindException -- in case the type doesn't match
	 */
	protected void typeCheck(final Object object) {
		if (!(object instanceof CustomerAddress)) {
			throw new EpBindException(MSG_EXPECTING_A_CUSTOMER);
		}
	}

	/**
	 * Do a sanity check.
	 * 
	 * @throws EpDomainException in case the sanity check fails.
	 */
	protected void sanityCheck() {
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
	 * Returns import data type name message key. This is a key for a message before separator.
	 * 
	 * @return message key.
	 */
	public String getNameMessageKey() {
		return IMPORT_DATA_TYPE_MESSAGE_KEY;
	}

	/**
	 * Delete entity.
	 * 
	 * @param entity for delete.
	 */
	public void deleteEntity(final Entity entity) {
		throw new EpUnsupportedOperationException("Should never reach here.");
	}

	@Override
	public String getPrefixOfName() {
		return PREFIX_OF_IMPORT_DATA_TYPE_NAME;
	}
}
