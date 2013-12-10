package com.elasticpath.domain.dataimport.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerProfile;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Defines fields and necessary information to import <code>Customer</code>.
 */
public class ImportDataTypeCustomerImpl extends AbstractImportDataTypeImpl {
	private static final String MSG_EXPECTING_A_CUSTOMER = "Expecting a customer.";

	private static final long serialVersionUID = 5000000001L;

	/**
	 * A prefix used in product import data type name.
	 */
	protected static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "Customer";

	/**
	 * An import data type message key. Usually, key for a message before separator.
	 */
	protected static final String IMPORT_DATA_TYPE_MESSAGE_KEY = "ImportDataType_Customer";

	/**
	 * A prefix used in product import field name.
	 */
	public static final String PREFIX_OF_FIELD_NAME = "";

	private static final int MIN_PWD_LENGTH = 8;

	private String guidFieldName;

	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = "importJobRunnerCustomer";

	private ValidatorUtils validatorUtils;

	private Utility utilityBean;

	@Override
	public void init(final Object object) {
		if (object != null) {
			throw new EpDomainException("object in this case should be null.");
		}

		// Notice : the field creation sequence is the sequence in which the
		// fields
		// are displayed on the import mapping page.
		// required fields
		createImportFieldGuid();
		createImportFieldUserId();

		// optional fields
		createImportFieldStatus(); // enabled = 1 (default), disabled = 0
		createImportFieldCreationDate();
		createImportFieldPassword();

		createImportFieldsForCustomerProfiles();
	}

	/**
	 * Populate customer profiles import fields.
	 */
	private void createImportFieldsForCustomerProfiles() {
		AttributeService attributeService = getBean(ContextIdNames.ATTRIBUTE_SERVICE);
		final Map<String, Attribute> customerProfileAttributes = attributeService.getCustomerProfileAttributesMap();

		for (final Attribute attribute : customerProfileAttributes.values()) {
			createImportFieldForOneCustomerAttribute(attribute);
		}

	}

	private void createImportFieldForOneCustomerAttribute(final Attribute attribute) {
		if (attribute.isLocaleDependant()) {
			for (final Locale locale : getSupportedLocales()) {
				addImportFieldForOneCustomerAttribute(attribute, locale);
			}
		} else {
			addImportFieldForOneCustomerAttribute(attribute, null);
		}
	}

	private void addImportFieldForOneCustomerAttribute(final Attribute attribute, final Locale locale) {
		final String attributeKey = attribute.getKey();
		String name;
		if (locale == null) {
			name = PREFIX_OF_FIELD_NAME + attributeKey;
		} else {
			name = PREFIX_OF_FIELD_NAME + attributeKey + '(' + locale.toString() + ')';
		}
		final String attributeType = attribute.getAttributeType().toString();
		addImportField(name, new AbstractImportFieldImpl(name, attributeType, attribute.isRequired(), false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object customer) {
				// customer profile attribute is not locale dependent for now
				return ((Customer) customer).getCustomerProfile().getStringProfileValue(attributeKey);
			}

			@Override
			public void setStringValue(final Object customer, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				final CustomerProfile customerProfile = ((Customer) customer).getCustomerProfile();
				customerProfile.setStringProfileValue(attributeKey, value);

				Map<String, AttributeValue> profileValueMap = customerProfile.getProfileValueMap();
				((Customer) customer).setProfileValueMap(profileValueMap);
				AttributeValue attributeValue = profileValueMap.get(attributeKey);

				getValidatorUtils().validateAttributeValue(attributeValue);
			}
		});
	}

	private void createImportFieldGuid() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "guid";
		guidFieldName = importFieldName;
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, true) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((Customer) object).getGuid();
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!getUtilityBean().isValidGuidStr(value)) {
					throw new EpInvalidGuidBindException(super.getName());
				}

				((Customer) object).setGuid(value);
			}
		});
	}

	private void createImportFieldUserId() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "userId";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((Customer) object).getUserId();
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				((Customer) object).setUserId(value);
			}
		});
	}

	private void createImportFieldPassword() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "password";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((Customer) object).getClearTextPassword());
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}
				if (value.length() < MIN_PWD_LENGTH) {
					throw new EpInvalidValueBindException(super.getName() + " - Passwords must be a minimum of 8 characters.");
				}
				((Customer) object).setClearTextPassword(value);
			}
		});
	}

	private void createImportFieldStatus() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "status";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf((((Customer) object).getStatus()));
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				if (!StringUtils.isAlphanumeric(value)) {
					throw new EpInvalidValueBindException(super.getName());
				}

				int status = ConverterUtils.string2Int(value);

				if (status != Customer.STATUS_ACTIVE && status != Customer.STATUS_DISABLED && status != Customer.STATUS_PENDING_APPROVAL) {
					throw new EpInvalidValueBindException(super.getName() + " - Status can only be 1, 2, or 3.");
				}

				((Customer) object).setStatus(status);
			}
		});
	}

	private void createImportFieldCreationDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "creationDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				final Date date = ((Customer) object).getCreationDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtilityBean().getDefaultLocalizedDateFormat());
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}
				((Customer) object).setCreationDate(ConverterUtils.string2Date(value, getUtilityBean().getDefaultLocalizedDateFormat()));
			}
		});
	}

	@Override
	public String getGuidFieldName() {
		return guidFieldName;
	}

	@Override
	public boolean isEntityImport() {
		return true;
	}

	@Override
	public boolean isValueObjectImport() {
		return false;
	}

	@Override
	public void saveOrUpdate(final Entity entity, final Persistable object) {
		throw new EpUnsupportedOperationException("Should never reach here.");
	}

	@Override
	public Persistable createValueObject() {
		throw new EpUnsupportedOperationException("Should never reach here.");
	}

	@Override
	protected void typeCheck(final Object object) throws EpBindException {
		if (!(object instanceof Customer)) {
			throw new EpBindException(MSG_EXPECTING_A_CUSTOMER);
		}
	}

	@Override
	protected void sanityCheck() throws EpDomainException {
		// do nothing
	}

	@Override
	public Object getMetaObject() {
		return null;
	}

	@Override
	public String getImportJobRunnerBeanName() {
		return IMPORT_JOB_RUNNER_BEAN_NAME;
	}

	@Override
	public String getNameMessageKey() {
		return IMPORT_DATA_TYPE_MESSAGE_KEY;
	}

	@Override
	public void deleteEntity(final Entity entity) {
		CustomerService customerService = getBean(ContextIdNames.CUSTOMER_SERVICE);
		customerService.remove(((Customer) entity));
	}

	@Override
	public String getPrefixOfName() {
		return PREFIX_OF_IMPORT_DATA_TYPE_NAME;
	}

	@Override
	public void setSupportedLocales(final Collection<Locale> locales) {
		super.setSupportedLocales(locales);
		clearAllImportFields();
		init(null);
	}

	/**
	 * Gets the validator utils.
	 *
	 * @return the validator utils
	 */
	protected ValidatorUtils getValidatorUtils() {
		if (validatorUtils == null) {
			validatorUtils = getBean(ContextIdNames.VALIDATOR_UTILS);
		}
		return validatorUtils;
	}

	protected void setValidatorUtils(final ValidatorUtils validatorUtils) {
		this.validatorUtils = validatorUtils;
	}

	/**
	 * Gets the utility bean.
	 *
	 * @return the utility bean
	 */
	protected Utility getUtilityBean() {
		if (utilityBean == null) {
			utilityBean = getBean(ContextIdNames.UTILITY);
		}
		return utilityBean;
	}

	protected void setUtilityBean(final Utility utilityBean) {
		this.utilityBean = utilityBean;
	}
}
