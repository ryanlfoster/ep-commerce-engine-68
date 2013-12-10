package com.elasticpath.domain.dataimport.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpTooLongBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Defines fields and necessary information to import <code>Category</code>.
 */
public class ImportDataTypeCategoryImpl extends AbstractImportDataTypeImpl {

	/**
	 * Parent category field name.
	 */
	public static final String PARENT_CATEGORY_CODE = "parentCategoryCode";

	private static final String MSG_EXPECTING_A_CATEGORY = "Expecting a category.";

	private static final long serialVersionUID = 5000000001L;

	/**
	 * A prefix used in product import data type name.
	 */
	protected static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "Category";

	/**
	 * An import data type message key. Usually, key for a message before separator.
	 */
	protected static final String IMPORT_DATA_TYPE_MESSAGE_KEY = "ImportDataType_Category";

	/**
	 * A prefix used in product import field name.
	 */
	public static final String PREFIX_OF_FIELD_NAME = "";

	private CategoryType categoryType;

	private String typeName;

	private String guidFieldName;

	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = ContextIdNames.IMPORT_JOB_RUNNER_CATEGORY;

	private ValidatorUtils validatorUtils;

	private Utility utility;

	/**
	 * Initialize the category import type.
	 *
	 * @param object the <code>CategoryType</code> instance used to initialize the import data type.
	 */
	@Override
	public void init(final Object object) {
		if (object == null) {
			throw new EpDomainException("Category type must be provided.");
		}

		if (!(object instanceof CategoryType)) {
			throw new EpDomainException("Unknown object : " + object.getClass().getName());
		}

		categoryType = (CategoryType) object;

		// Notice : the sequence of a field get created will be applied to the
		// sequence that the field get displayed in the
		// import mapping page.
		createImportFieldGuid();
		createImportFieldStartDate();
		createImportFieldEndDate();
		createImportFieldHidden();
		createImportFieldOrdering();
		createImportFieldParentCategory();

		// Locale dependent fields
		createImportFieldDisplayName();
		createImportFieldUrl();
		createImportFieldTitle();
		createImportFieldKeyWords();
		createImportFieldDescription();

		// Attributes
		createImportFieldsForAttributes();
	}

	private void createImportFieldStartDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "enableDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				final Date date = ((Category) object).getStartDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtilityBean().getDefaultLocalizedDateFormat());
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					((Category) object).setStartDate(new Date());
				} else {
					((Category) object).setStartDate(ConverterUtils.string2Date(value, getUtilityBean().getDefaultLocalizedDateFormat()));
				}
			}
		});
	}

	private void createImportFieldEndDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "disableDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				final Date date = ((Category) object).getEndDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtilityBean().getDefaultLocalizedDateFormat());
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					((Category) object).setEndDate(null);
					return;
				}

				((Category) object).setEndDate(ConverterUtils.string2Date(value, getUtilityBean().getDefaultLocalizedDateFormat()));
			}
		});
	}

	/**
	 * Only allow alphanumeric characters for category guid. <br>
	 * This requirement is due to the use of guid in seo, in combination with page number, brand and price filter notation,
	 * i.e. c90000003-pr100_200-b5.html <br>
	 * The "-" will be used as a url friendly separator.
	 */
	private void createImportFieldGuid() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "categoryCode";
		guidFieldName = importFieldName;
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, true) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((Category) object).getGuid();
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!StringUtils.isAlphanumeric(value)) {
					throw new EpInvalidGuidBindException(super.getName());
				}

				((Category) object).setGuid(value);
			}
		});
	}

	private void createImportFieldParentCategory() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + PARENT_CATEGORY_CODE;
		addImportField(importFieldName, new AbstractCatalogImportFieldImpl(importFieldName, String.class.toString(), true, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				final Category category = (Category) object;
				if (category.getParent() == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return category.getParent().getGuid();
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					((Category) object).setParent(null);
					return;
				}

				final Category parentCategory = service.findCategoryByGuidAndCatalogGuid(value, getCatalog().getGuid(), true, true);
				if (parentCategory == null) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
				// Set the parent without maintaining the bidirectional relationship as we are persisting
				// and not using the object afterwards.
				((Category) object).setParentOnly(parentCategory);
			}

			@Override
			public void checkStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}
				if (!service.isCategoryGuidExist(value, getCatalog().getGuid())) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
			}
		});
	}

	/**
	 * Populate the import field for display name.
	 */
	private void createImportFieldDisplayName() {
		for (final Locale locale : getSupportedLocales()) {
			final String name = PREFIX_OF_FIELD_NAME + "displayName(" + locale.toString() + ")";
			addImportField(name, new AbstractImportFieldImpl(name, String.class.toString(), true, false) {

				private static final long serialVersionUID = 5000000001L;

				@Override
				public String getStringValue(final Object object) {
					typeCheck(object);
					final LocaleDependantFields ldf = ((Category) object).getLocaleDependantFieldsWithoutFallBack(locale);
					return ldf.getDisplayName();
				}

				@Override
				public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
					typeCheck(object);
					if (checkNullValue(value)) {
						return;
					}

					validateTextLength(value);

					final LocaleDependantFields ldf = ((Category) object).getLocaleDependantFieldsWithoutFallBack(locale);
					ldf.setDisplayName(value);
					((Category) object).addOrUpdateLocaleDependantFields(ldf);
				}

			});
		}
	}

	/**
	 * Populate the import field for url.
	 */
	private void createImportFieldUrl() {
		for (final Locale locale : getSupportedLocales()) {
			final String name = PREFIX_OF_FIELD_NAME + "seoUrl(" + locale.toString() + ')';
			addImportField(name, new AbstractImportFieldImpl(name, String.class.toString(), false, false) {

				private static final long serialVersionUID = 5000000001L;

				@Override
				public String getStringValue(final Object object) {
					typeCheck(object);
					final LocaleDependantFields ldf = ((Category) object).getLocaleDependantFieldsWithoutFallBack(locale);
					return ldf.getUrl();
				}

				@Override
				public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
					typeCheck(object);
					if (checkNullValue(value)) {
						return;
					}

					validateTextLength(value);

					final LocaleDependantFields ldf = ((Category) object).getLocaleDependantFieldsWithoutFallBack(locale);
					ldf.setUrl(value);
					((Category) object).addOrUpdateLocaleDependantFields(ldf);
				}
			});
		}
	}

	/**
	 * Populate the import field for key words.
	 */
	private void createImportFieldKeyWords() {
		for (final Locale locale : getSupportedLocales()) {
			final String name = PREFIX_OF_FIELD_NAME + "seoKeyWords(" + locale.toString() + ")";
			addImportField(name, new AbstractImportFieldImpl(name, String.class.toString(), false, false) {

				private static final long serialVersionUID = 5000000001L;

				@Override
				public String getStringValue(final Object object) {
					typeCheck(object);
					final LocaleDependantFields ldf = ((Category) object).getLocaleDependantFieldsWithoutFallBack(locale);
					return ldf.getKeyWords();
				}

				@Override
				public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
					typeCheck(object);
					if (checkNullValue(value)) {
						return;
					}

					validateTextLength(value);

					final LocaleDependantFields ldf = ((Category) object).getLocaleDependantFieldsWithoutFallBack(locale);
					ldf.setKeyWords(value);
					((Category) object).addOrUpdateLocaleDependantFields(ldf);
				}
			});
		}
	}

	/**
	 * Populate the import field for title.
	 */
	private void createImportFieldTitle() {
		for (final Locale locale : getSupportedLocales()) {
			final String name = PREFIX_OF_FIELD_NAME + "seoTitle(" + locale.toString() + ')';
			addImportField(name, new AbstractImportFieldImpl(name, String.class.toString(), false, false) {

				private static final long serialVersionUID = 5000000001L;

				@Override
				public String getStringValue(final Object object) {
					typeCheck(object);
					final LocaleDependantFields ldf = ((Category) object).getLocaleDependantFieldsWithoutFallBack(locale);
					return ldf.getTitle();
				}

				@Override
				public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
					typeCheck(object);
					if (checkNullValue(value)) {
						return;
					}

					validateTextLength(value);

					final LocaleDependantFields ldf = ((Category) object).getLocaleDependantFieldsWithoutFallBack(locale);
					ldf.setTitle(value);
					((Category) object).addOrUpdateLocaleDependantFields(ldf);
				}
			});
		}
	}

	/**
	 * Populate the import field for title.
	 */
	private void createImportFieldDescription() {
		for (final Locale locale : getSupportedLocales()) {
			final String name = PREFIX_OF_FIELD_NAME + "seoDescription(" + locale.toString() + ")";
			addImportField(name, new AbstractImportFieldImpl(name, String.class.toString(), false, false) {

				private static final long serialVersionUID = 5000000001L;

				@Override
				public String getStringValue(final Object object) {
					typeCheck(object);
					final LocaleDependantFields ldf = ((Category) object).getLocaleDependantFieldsWithoutFallBack(locale);
					return ldf.getDescription();
				}

				@Override
				public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
					typeCheck(object);
					if (checkNullValue(value)) {
						return;
					}

					validateTextLength(value);

					final LocaleDependantFields ldf = ((Category) object).getLocaleDependantFieldsWithoutFallBack(locale);
					ldf.setDescription(value);
					((Category) object).addOrUpdateLocaleDependantFields(ldf);
				}
			});
		}
	}

	/**
	 * Populate the attribute import fields based on the given category type.
	 */
	private void createImportFieldsForAttributes() {

		for (final AttributeGroupAttribute categoryTypeAttribute : categoryType.getAttributeGroup().getAttributeGroupAttributes()) {
			final Attribute attribute = categoryTypeAttribute.getAttribute();
			createImportFieldForOneAttribute(attribute);
		}
	}

	private void createImportFieldForOneAttribute(final Attribute attribute) {
		if (attribute.isLocaleDependant()) {
			for (Locale locale : getSupportedLocales()) {
				addImportFieldForOneAttribute(attribute, locale);
			}
		} else {
			addImportFieldForOneAttribute(attribute, null);
		}
	}

	private void addImportFieldForOneAttribute(final Attribute attribute, final Locale locale) {
		final String attributeKey = attribute.getKey();
		final String attributeType = attribute.getAttributeType().toString();

		String name;
		if (locale == null) {
			name = PREFIX_OF_FIELD_NAME + attributeKey;
		} else {
			name = PREFIX_OF_FIELD_NAME + attributeKey + '(' + locale.toString() + ')';
		}
		addImportField(name, new AbstractImportFieldImpl(name, attributeType, attribute.isRequired(), false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((Category) object).getAttributeValueGroup().getStringAttributeValue(attributeKey, locale);
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}
				AttributeValueGroup attributeValueGroup = ((Category) object).getAttributeValueGroup();
				attributeValueGroup.setStringAttributeValue(attribute, locale, value);
				AttributeValue attributeValue = attributeValueGroup.getAttributeValue(attributeKey, locale);

				getValidatorUtils().validateAttributeValue(attributeValue);
			}
		});
	}

	@Override
	public String getTypeName() {
        sanityCheck();
        if (typeName == null) {
        	typeName = categoryType.getName();
        }
        return typeName;
	}

	@Override
	public String getNameMessageKey() {
		return IMPORT_DATA_TYPE_MESSAGE_KEY;
	}

	@Override
	public String getGuidFieldName() {
		return guidFieldName;
	}

	@Override
	protected void sanityCheck() throws EpDomainException {
		if (categoryType == null) {
			throw new EpDomainException("Category type is not set.");
		}
	}

	@Override
	public Object getMetaObject() {
		return categoryType;
	}

	@Override
	public String getImportJobRunnerBeanName() {
		return IMPORT_JOB_RUNNER_BEAN_NAME;
	}

	private void createImportFieldOrdering() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "ordering";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				return String.valueOf(((Category) object).getOrdering());
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				((Category) object).setOrdering(ConverterUtils.string2Int(value));
			}
		});
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

	private void createImportFieldHidden() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "storeVisible";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Boolean.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				final Category category = (Category) object;
				return String.valueOf(category.isHidden());
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				final Category category = (Category) object;
				category.setHidden(ConverterUtils.string2Boolean(value));
			}
		});
	}

	@Override
	protected void typeCheck(final Object object) throws EpBindException {
		if (!(object instanceof Category)) {
			throw new EpBindException(MSG_EXPECTING_A_CATEGORY);
		}
	}

	private void validateTextLength(final String value) {
		if (!getUtilityBean().checkShortTextMaxLength(value)) {
			throw new EpTooLongBindException(super.getName());
		}
	}

	@Override
	public void deleteEntity(final Entity entity) {
		CategoryService categoryService = getBean(ContextIdNames.CATEGORY_SERVICE);
		categoryService.removeCategoryTree(entity.getUidPk());
	}

	@Override
	public String getPrefixOfName() {
		return PREFIX_OF_IMPORT_DATA_TYPE_NAME;
	}

	@Override
	public void setSupportedLocales(final Collection<Locale> locales) {
		super.setSupportedLocales(locales);
		clearAllImportFields();
		init(categoryType);
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
		if (utility == null) {
			utility = getBean(ContextIdNames.UTILITY);
		}
		return utility;
	}

	protected void setUtilityBean(final Utility utility) {
		this.utility = utility;
	}
}
