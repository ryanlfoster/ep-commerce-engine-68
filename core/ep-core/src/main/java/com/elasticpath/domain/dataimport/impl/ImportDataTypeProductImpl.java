package com.elasticpath.domain.dataimport.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpProductInUseException;
import com.elasticpath.commons.exception.EpTooLongBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Defines fields and necessary information to import <code>Product</code>.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.TooManyMethods" })
public class ImportDataTypeProductImpl extends AbstractImportDataTypeImpl {

	private static final long serialVersionUID = 5000000001L;

	/**
	 * A prefix used in product import data type name.
	 */
	protected static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "Product";

	/**
	 * An import data type message key. Usually, key for a message before separator.
	 */
	protected static final String IMPORT_DATA_TYPE_MESSAGE_KEY = "ImportDataType_Product";

	/**
	 * A prefix used in product import field name.
	 */
	protected static final String PREFIX_OF_FIELD_NAME = "";

	private ProductType productType;

	private String typeName;

	private String guidFieldName;

	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = "importJobRunnerProduct";

	private static final String MSG_EXPECTING_A_PRODUCT = "Expecting a product.";

	private ValidatorUtils validatorUtils;

	private Utility utilityBean;

	@Override
	public void init(final Object object) {
		if (object == null) {
			throw new EpDomainException("Product type must not be null.");
		}

		if (!(object instanceof ProductType)) {
			throw new EpDomainException("Unknown object : " + object.getClass().getName());
		}

		productType = (ProductType) object;

		// Notice : the sequence of a field get created will be applied to the sequence that the field get displayed in the
		// import mapping page.
		// General
		createImportFieldGuid();
		createImportFieldDefaultCategory();
		createImportFieldStartDate();
		createImportFieldEndDate();
		createImportFieldHidden();
		createImportFieldBrand();
		createImportFieldImage();

		// Locale Dependant Fields
		createImportFieldDisplayName();
		createImportFieldUrl();
		createImportFieldTitle();
		createImportFieldKeyWords();
		createImportFieldDescription();

		createImportFieldTaxCode();
		createImportFieldMinOrderQty();
		createImportFieldReleaseDate();

		// Attribute
		createImportFieldsForProductAttributes();

		// If a product type can only have one sku, make all sku fields importable too
		if (!productType.isWithMultipleSkus()) {
			createImportFieldSkuCode();

			// sku option
			createImportFieldsForSkuOptions();

			// spec7
			createImportFieldShippable();
			createImportFieldSkuLength();
			createImportFieldSkuWidth();
			createImportFieldSkuHeight();
			createImportFieldSkuWeight();

			// Attribute
			createImportFieldsForProductSkuAttributes();

			// Digital Asset
			createImportFieldDigital();
			createImportFieldSkuFileName();
			createImportFieldSkuExpiryDays();
			createImportFieldSkuMaxDownloadTimes();
		}

		// Availability settings
		createImportFieldAvailabilityCriteria();
		createImportFieldPreBackOrderLimit();
		createImportFieldNotSoldSeperately();
	}

	private void createImportFieldImage() {

		final String importFieldImage = PREFIX_OF_FIELD_NAME + "image";
		addImportField(importFieldImage, new AbstractImportFieldImpl(importFieldImage, String.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object product) {
				return ((Product) product).getImage();
			}

			@Override
			public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
				validateTextLength(value);

				if (checkNullValue(value)) {
					((Product) product).setImage(null);
				} else {
					((Product) product).setImage(value);
				}
			}
		});
	}

	private void createImportFieldTaxCode() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "taxCode";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object product) {
				return ((Product) product).getTaxCode().getCode();
			}

			@Override
			public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				TaxCodeService taxCodeService = getBean(ContextIdNames.TAX_CODE_SERVICE);
				TaxCode taxCode = taxCodeService.findByCode(value);
				((Product) product).setTaxCode(taxCode);
			}

			@Override
			public void checkStringValue(final Object persistenceObject, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				if (!service.isTaxCodeExist(value)) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
			}
		});
	}

	private void createImportFieldMinOrderQty() {
		final String importFieldImage = PREFIX_OF_FIELD_NAME + "minOrderQty";
		addImportField(importFieldImage, new AbstractImportFieldImpl(importFieldImage, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object product) {
				return String.valueOf(((Product) product).getMinOrderQty());
			}

			@Override
			public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				((Product) product).setMinOrderQty(ConverterUtils.string2Int(value));
			}
		});
	}

	private void createImportFieldReleaseDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "expectedReleaseDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object product) {
				final Date date = ((Product) product).getExpectedReleaseDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtilityBean().getDefaultLocalizedDateFormat());
			}

			@Override
			public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				((Product) product).setExpectedReleaseDate(ConverterUtils.string2Date(value, getUtilityBean().getDefaultLocalizedDateFormat()));
			}
		});
	}

	private void createImportFieldStartDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "enableDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object product) {
				final Date date = ((Product) product).getStartDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtilityBean().getDefaultLocalizedDateFormat());
			}

			@Override
			public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					((Product) product).setStartDate(new Date());
				} else {
					((Product) product).setStartDate(ConverterUtils.string2Date(value, getUtilityBean().getDefaultLocalizedDateFormat()));
				}
			}
		});
	}

	private void createImportFieldEndDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "disableDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object product) {
				final Date date = ((Product) product).getEndDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtilityBean().getDefaultLocalizedDateFormat());
			}

			@Override
			public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					((Product) product).setEndDate(null);
					return;
				}

				((Product) product).setEndDate(ConverterUtils.string2Date(value, getUtilityBean().getDefaultLocalizedDateFormat()));
			}
		});
	}

	private void createImportFieldGuid() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "productCode";
		guidFieldName = importFieldName;
		addImportField(importFieldName, new AbstractCatalogImportFieldImpl(importFieldName, String.class.toString(), true, true) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object product) {
				return ((Product) product).getGuid();
			}

			@Override
			public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!getUtilityBean().isValidGuidStr(value)) {
					throw new EpInvalidGuidBindException(super.getName());
				}

				// Prevent changing the product guid in case-insensitive databases. The product guid is used in the productCategory hashCode so
				// modifying the guid will result in an inconsistent hashSet of productCategories, resulting in duplicate productCategory references.
				// For case-sensitive databases this is not a problem since an existing product will only be found if the guid matches exactly.
				if (((Product) product).getGuid() == null || ((Product) product).getGuid().equals("")) {
					((Product) product).setGuid(value);
				}
			}

			@Override
			public void checkStringValue(final Object persistenceObject, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}
				if (service.isProductGuidExist(value)) {
					final Product existingProduct = service.findProductByGuid(value, true, false, false);
					if (existingProduct.getMasterCatalog().getUidPk() != getCatalog().getUidPk()) {
						throw new EpInvalidValueBindException("Cannot import product in catalog[code="
								+ existingProduct.getMasterCatalog().getCode()
								+ "]. The product already exists in another catalog[code="
								+ getCatalog().getCode() + "]");
					}
				}
			}
		});
	}

	private void createImportFieldDefaultCategory() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "defaultCategoryCode";
		addImportField(importFieldName, new ImportFieldProductDefaultCategory(importFieldName, String.class.toString(), true, false));
	}

	/**
	 * ImportField class implementation for a product's default category.
	 */
	protected class ImportFieldProductDefaultCategory extends AbstractCatalogImportFieldImpl {

		private static final long serialVersionUID = 5000000001L;

		/**
		 * The default constructor.
		 *
		 * @param importFieldName the name of the import field
		 * @param type the type of the import field
		 * @param required set it to <code>true</code> if the import field is required
		 * @param primaryRequired set it to <code>true</code> if the import field is a required primary field
		 */
		public ImportFieldProductDefaultCategory(
				final String importFieldName, final String type, final boolean required, final boolean primaryRequired) {
			super(importFieldName, type, required, primaryRequired);
		}

		/**
		 * @param product the product
		 * @return the Guid of the given product's primary category in the product's master catalog.
		 */
		@Override
		public String getStringValue(final Object product) {
			return ((Product) product).getDefaultCategory(productType.getCatalog()).getGuid();
		}

		/**
		 * Sets the product's primary category in the product's master catalog to the category with
		 * the given GUID.
		 * If there are any categories linked to the primary category, those categories will be added
		 * to the product as well.
		 * @param product the product
		 * @param value the GUID of the category to set as the primary category
		 * @param service the helper service to interface with the persistence layer
		 */
		@Override
		public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
			if (checkNullValue(value)) {
				throw new EpNonNullBindException(super.getName());
			}

			final Category defaultCategory = getCategory(service, value);
			if (defaultCategory == null) {
				throw new EpInvalidGuidBindException(getName());
			}
			((Product) product).setCategoryAsDefault(defaultCategory);

			// If there are categories linked to the default category, we need to add those categories to this product as well
			final List<Category> linkedCategories = getLinkedCategories(service, defaultCategory);
			for (Category currCategory : linkedCategories) {
				((Product) product).addCategory(currCategory);
			}
		}

		/**
		 * Uses the given service to find the category having the given guid.
		 * Returns a minimally populated category.
		 * @param service the helper service to lookup the category
		 * @param categoryGuid the guid of the category
		 * @return the found category
		 */
		protected Category getCategory(final ImportGuidHelper service, final String categoryGuid) {
			return service.findCategoryByGuidAndCatalogGuid(categoryGuid, getCatalog().getGuid(), false, false);
		}

		/**
		 * Uses the given service to find the given master category's linked categories.
		 * @param service the helper service to lookup the linked categories
		 * @param masterCategory the master category that may have linked categories
		 * @return the linked categories, or an empty list if none exist
		 */
		protected List<Category> getLinkedCategories(final ImportGuidHelper service, final Category masterCategory) {
			List<Category> linkedCategories = service.findLinkedCategories(masterCategory.getUidPk());
			if (linkedCategories == null) {
				linkedCategories = Collections.emptyList();
			}
			return linkedCategories;
		}

		/**
		 * Checks that the given GUID is not null and that a category with the given GUID exists.
		 * @param persistenceObject not used
		 * @param categoryGuid a Category GUID
		 * @param service the helper service to interface with the persistence layer
		 */
		@Override
		public void checkStringValue(final Object persistenceObject, final String categoryGuid, final ImportGuidHelper service) {
			if (checkNullValue(categoryGuid)) {
				throw new EpNonNullBindException(super.getName());
			}
			if (!service.isCategoryGuidExist(categoryGuid, getCatalog().getGuid())) {
				throw new EpInvalidGuidBindException(getName());
			}
		}
	}

	/**
	 * Populate product attribute import fields based on the given product type.
	 */
	protected void createImportFieldsForProductAttributes() {
		final AttributeGroup attributeGroup = productType.getProductAttributeGroup();

		for (AttributeGroupAttribute attributeGroupAttribute : attributeGroup.getAttributeGroupAttributes()) {
			final Attribute attribute = attributeGroupAttribute.getAttribute();
			createImportFieldForOneProductAttribute(attribute);
		}

	}

	private void createImportFieldForOneProductAttribute(final Attribute attribute) {
		if (attribute.isLocaleDependant()) {
			for (Locale locale : getSupportedLocales()) {
				addImportFieldForOneProductAttribute(attribute, locale);
			}
		} else {
			addImportFieldForOneProductAttribute(attribute, null);
		}
	}

	private void addImportFieldForOneProductAttribute(final Attribute attribute, final Locale locale) {
		final String attributeKey = attribute.getKey();
		String name;
		boolean requiredField;
		if (locale == null) {
			name = PREFIX_OF_FIELD_NAME + attributeKey;
			requiredField = attribute.isRequired();
		} else {
			name = PREFIX_OF_FIELD_NAME + attributeKey + '(' + locale.toString() + ')';
			requiredField = attribute.isRequired() && locale.equals(getRequiredLocale());
		}
		final String attributeType = attribute.getAttributeType().toString();
		addImportField(name, new AbstractImportFieldImpl(name, attributeType, requiredField, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object product) {
				return ((Product) product).getAttributeValueGroup().getStringAttributeValue(attributeKey, locale);
			}

			@Override
			public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				AttributeValueGroup attributeValueGroup = ((Product) product).getAttributeValueGroup();
				attributeValueGroup.setStringAttributeValue(attribute, locale, value);
				AttributeValue attributeValue = attributeValueGroup.getAttributeValue(attributeKey, locale);

				getValidatorUtils().validateAttributeValue(attributeValue);
			}

		});
	}

	/**
	 * Populate sku attribute import fields based on the given product type.
	 */
	private void createImportFieldsForProductSkuAttributes() {
		final AttributeGroup attributeGroup = productType.getSkuAttributeGroup();

		for (AttributeGroupAttribute attributeGroupAttribute : attributeGroup.getAttributeGroupAttributes()) {
			final Attribute attribute = attributeGroupAttribute.getAttribute();
			createImportFieldForOneSkuAttribute(attribute);
		}
	}

	private void createImportFieldForOneSkuAttribute(final Attribute attribute) {
		if (attribute.isLocaleDependant()) {
			for (Locale locale : productType.getCatalog().getSupportedLocales()) {
				addImportFieldForOneSkuAttribute(attribute, locale);
			}
		} else {
			addImportFieldForOneSkuAttribute(attribute, null);
		}
	}

	private void addImportFieldForOneSkuAttribute(final Attribute attribute, final Locale locale) {
		final String attributeKey = attribute.getKey();
		final String attributeType = attribute.getAttributeType().toString();
		String name;
		boolean requiredField;
		if (locale == null) {
			name = PREFIX_OF_FIELD_NAME + attributeKey;
			requiredField = attribute.isRequired();
		} else {
			name = PREFIX_OF_FIELD_NAME + attributeKey + '(' + locale.toString() + ')';
			requiredField = attribute.isRequired() && locale.equals(getRequiredLocale());
		}

		addImportField(name, new AbstractImportFieldImpl(name, attributeType, requiredField, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final ProductSku sku = ((Product) entity).getDefaultSku();
				if (sku == null) {
					return GlobalConstants.NULL_VALUE;
				}
				return sku.getAttributeValueGroup().getStringAttributeValue(attributeKey, locale);
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);
				AttributeValueGroup attributeValueGroup = sku.getAttributeValueGroup();
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
			typeName = productType.getName();
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
		if (productType == null) {
			throw new EpDomainException("Product type is not set.");
		}
	}

	@Override
	public Object getMetaObject() {
		return productType;
	}

	@Override
	public String getImportJobRunnerBeanName() {
		return IMPORT_JOB_RUNNER_BEAN_NAME;
	}

	private void createImportFieldSkuCode() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "skuCode";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final ProductSku sku = ((Product) entity).getDefaultSku();
				if (sku == null) {
					return GlobalConstants.NULL_VALUE;
				}
				return sku.getSkuCode();
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!getUtilityBean().isValidGuidStr(value)) {
					throw new EpInvalidValueBindException(super.getName());
				}

				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);
				sku.setSkuCode(value);
				sku.setGuid(value);
				// Add sku into map after the skucode has been set.
				product.addOrUpdateSku(sku);
			}
		});
	}

	private void createImportFieldSkuWidth() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "width";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final ProductSku sku = ((Product) entity).getDefaultSku();
				if (sku == null) {
					return GlobalConstants.NULL_VALUE;
				}
				return ObjectUtils.toString(sku.getWidth(), GlobalConstants.NULL_VALUE);
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);
				sku.setWidth(ConverterUtils.string2BigDecimal(value));
			}
		});
	}

	private void createImportFieldSkuHeight() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "height";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final ProductSku sku = ((Product) entity).getDefaultSku();
				if (sku == null) {
					return GlobalConstants.NULL_VALUE;
				}
				return ObjectUtils.toString(sku.getHeight(), GlobalConstants.NULL_VALUE);
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);
				sku.setHeight(ConverterUtils.string2BigDecimal(value));
			}
		});
	}

	private void createImportFieldSkuLength() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "length";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final ProductSku sku = ((Product) entity).getDefaultSku();
				if (sku == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ObjectUtils.toString(sku.getLength(), GlobalConstants.NULL_VALUE);
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);
				sku.setLength(ConverterUtils.string2BigDecimal(value));
			}
		});
	}

	private void createImportFieldSkuWeight() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "weight";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final ProductSku sku = ((Product) entity).getDefaultSku();
				if (sku == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ObjectUtils.toString(sku.getWeight(), GlobalConstants.NULL_VALUE);
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);
				sku.setWeight(ConverterUtils.string2BigDecimal(value));
			}
		});
	}

	/**
	 * Populate the import field for display name.
	 */
	private void createImportFieldDisplayName() {
		for (final Locale locale : getSupportedLocales()) {
			final String name = PREFIX_OF_FIELD_NAME + "displayName(" + locale.toString() + ')';
			final boolean requiredField = locale.equals(getRequiredLocale());
			addImportField(name, new AbstractImportFieldImpl(name, String.class.toString(), requiredField, false) {

				private static final long serialVersionUID = 5000000001L;

				@Override
				public String getStringValue(final Object product) {
					final LocaleDependantFields ldf = ((Product) product).getLocaleDependantFieldsWithoutFallBack(locale);
					return ldf.getDisplayName();
				}

				@Override
				public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
					if (requiredField && checkNullValue(value)) {
						throw new EpNonNullBindException(super.getName());
					}

					if (!getUtilityBean().checkShortTextMaxLength(value)) {
						throw new EpTooLongBindException(super.getName());
					}

					final LocaleDependantFields ldf = ((Product) product).getLocaleDependantFieldsWithoutFallBack(locale);
					ldf.setDisplayName(value);
					((Product) product).addOrUpdateLocaleDependantFields(ldf);
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
				public String getStringValue(final Object product) {
					final LocaleDependantFields ldf = ((Product) product).getLocaleDependantFieldsWithoutFallBack(locale);
					return ldf.getUrl();
				}

				@Override
				public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
					if (checkNullValue(value)) {
						return;
					}

					validateTextLength(value);

					final LocaleDependantFields ldf = ((Product) product).getLocaleDependantFieldsWithoutFallBack(locale);
					ldf.setUrl(value);
					((Product) product).addOrUpdateLocaleDependantFields(ldf);
				}
			});
		}
	}

	/**
	 * Populate the import field for key words.
	 */
	private void createImportFieldKeyWords() {
		for (final Locale locale : getSupportedLocales()) {
			final String name = PREFIX_OF_FIELD_NAME + "seoKeyWords(" + locale.toString() + ')';
			addImportField(name, new AbstractImportFieldImpl(name, String.class.toString(), false, false) {

				private static final long serialVersionUID = 5000000001L;

				@Override
				public String getStringValue(final Object product) {
					final LocaleDependantFields ldf = ((Product) product).getLocaleDependantFieldsWithoutFallBack(locale);
					return ldf.getKeyWords();
				}

				@Override
				public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
					if (checkNullValue(value)) {
						return;
					}

					validateTextLength(value);

					final LocaleDependantFields ldf = ((Product) product).getLocaleDependantFieldsWithoutFallBack(locale);
					ldf.setKeyWords(value);
					((Product) product).addOrUpdateLocaleDependantFields(ldf);
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
				public String getStringValue(final Object product) {
					final LocaleDependantFields ldf = ((Product) product).getLocaleDependantFieldsWithoutFallBack(locale);
					return ldf.getTitle();
				}

				@Override
				public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
					if (checkNullValue(value)) {
						return;
					}

					validateTextLength(value);

					final LocaleDependantFields ldf = ((Product) product).getLocaleDependantFieldsWithoutFallBack(locale);
					ldf.setTitle(value);
					((Product) product).addOrUpdateLocaleDependantFields(ldf);
				}
			});
		}
	}

	/**
	 * Populate the import field for title.
	 */
	private void createImportFieldDescription() {
		for (final Locale locale : getSupportedLocales()) {
			final String name = PREFIX_OF_FIELD_NAME + "seoDescription(" + locale.toString() + ')';
			addImportField(name, new AbstractImportFieldImpl(name, String.class.toString(), false, false) {

				private static final long serialVersionUID = 5000000001L;

				@Override
				public String getStringValue(final Object product) {
					final LocaleDependantFields ldf = ((Product) product).getLocaleDependantFieldsWithoutFallBack(locale);
					return ldf.getDescription();
				}

				@Override
				public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
					if (checkNullValue(value)) {
						return;
					}

					if (!getUtilityBean().checkShortTextMaxLength(value)) {
						throw new EpTooLongBindException(super.getName());
					}

					final LocaleDependantFields ldf = ((Product) product).getLocaleDependantFieldsWithoutFallBack(locale);
					ldf.setDescription(value);
					((Product) product).addOrUpdateLocaleDependantFields(ldf);
				}
			});
		}
	}

	private void createImportFieldBrand() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "brandCode";
		addImportField(importFieldName, new AbstractCatalogImportFieldImpl(importFieldName, String.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final Product product = (Product) entity;
				if (product == null) {
					return GlobalConstants.NULL_VALUE;
				}
				return ((Product) entity).getBrand().getGuid();
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				final Product product = (Product) entity;
				if (checkNullValue(value)) {
					product.setBrand(null);
					return;
				}

				final Brand brand = service.findBrandByGuidAndCatalogGuid(value, getCatalog().getGuid());
				if (brand == null) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
				product.setBrand(brand);
			}

			@Override
			public void checkStringValue(final Object persistenceObject, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				if (!service.isBrandGuidExist(value, getCatalog().getGuid())) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
			}

			@Override
			public boolean isRequired() {
				return true;
			}
		});
	}

	/**
	 * Populate sku option import fields based on the given product type.
	 */
	private void createImportFieldsForSkuOptions() {
		final Set<SkuOption> skuOptions = productType.getSkuOptions();
		for (final SkuOption skuOption : skuOptions) {
			final String type = java.lang.String.class.toString();
			final String name = PREFIX_OF_FIELD_NAME + skuOption.getOptionKey();
			addImportField(name, new AbstractImportFieldImpl(name, type, true, false) {

				private static final long serialVersionUID = 5000000001L;

				@Override
				public String getStringValue(final Object entity) {
					final ProductSku sku = ((Product) entity).getDefaultSku();
					if (sku == null) {
						return GlobalConstants.NULL_VALUE;
					}
					return sku.getSkuOptionValue(skuOption).getOptionValueKey();
				}

				@Override
				public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
					if (checkNullValue(value)) {
						throw new EpNonNullBindException(super.getName());
					}

					final Product product = (Product) entity;
					final ProductSku sku = getProductSku(product);
					sku.setSkuOptionValue(skuOption, value);
				}
			});
		}
	}

	/**
	 * Returns the default product sku of the given product. If no product sku exists, creates and returns a new one.
	 *
	 * @param product the product
	 * @return the default product sku of the product. If no product sku exists, creates and returns a new one.
	 */
	protected ProductSku getProductSku(final Product product) {
		ProductSku sku = product.getDefaultSku();
		if (sku == null) {
			sku = getBean(ContextIdNames.PRODUCT_SKU);
			sku.initialize();
			product.setDefaultSku(sku);
		}

		return sku;
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
			public String getStringValue(final Object entity) {
				final Product product = (Product) entity;
				return String.valueOf(product.isHidden());
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				final Product product = (Product) entity;
				product.setHidden(ConverterUtils.string2Boolean(value));
			}
		});
	}

	@Override
	protected void typeCheck(final Object object) throws EpBindException {
		if (!(object instanceof Product)) {
			throw new EpBindException(MSG_EXPECTING_A_PRODUCT);
		}
	}

	private void createImportFieldShippable() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "shippable";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Boolean.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final ProductSku sku = ((Product) entity).getDefaultSku();
				if (sku == null) {
					return GlobalConstants.NULL_VALUE;
				}
				return String.valueOf(sku.isShippable());
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);
				boolean shippable = ConverterUtils.string2Boolean(value);
				sku.setShippable(shippable);
				sku.setDigital(!shippable);
			}
		});
	}

	private void createImportFieldDigital() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "digital";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Boolean.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				return String.valueOf(((ProductSku) entity).isDigital());
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);

				boolean digital = ConverterUtils.string2Boolean(value);
				sku.setDigital(digital);
				sku.setShippable(!digital);
			}
		});
	}

	private void createImportFieldSkuFileName() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "fileName";

		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final ProductSku sku = ((Product) entity).getDefaultSku();
				if (sku == null || sku.getDigitalAsset() == null) {
					return GlobalConstants.NULL_VALUE;
				}
				return sku.getDigitalAsset().getFileName();
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				validateTextLength(value);

				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);
				if (!sku.isDigital()) {
					return;
				}

				DigitalAsset digitalAsset = sku.getDigitalAsset();
				if (digitalAsset == null) {
					digitalAsset = getBean(ContextIdNames.DIGITAL_ASSET);
				}

				digitalAsset.setFileName(value);

				sku.setDigitalAsset(digitalAsset);
			}
		});
	}

	private void createImportFieldSkuExpiryDays() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "expiryDays";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final ProductSku sku = ((Product) entity).getDefaultSku();
				if (sku == null || sku.getDigitalAsset() == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return String.valueOf(sku.getDigitalAsset().getExpiryDays());
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);

				DigitalAsset digitalAsset = sku.getDigitalAsset();
				if (!sku.isDigital()) {
					return;
				}
				if (digitalAsset == null) {
					digitalAsset = getBean(ContextIdNames.DIGITAL_ASSET);
				}
				digitalAsset.setExpiryDays(ConverterUtils.string2Int(value));

				sku.setDigitalAsset(digitalAsset);
			}
		});
	}

	private void createImportFieldSkuMaxDownloadTimes() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "maxDownloadTimes";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final ProductSku sku = ((Product) entity).getDefaultSku();
				if (sku == null || sku.getDigitalAsset() == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return String.valueOf(sku.getDigitalAsset().getMaxDownloadTimes());
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				final Product product = (Product) entity;
				final ProductSku sku = getProductSku(product);

				DigitalAsset digitalAsset = sku.getDigitalAsset();
				if (!sku.isDigital()) {
					return;
				}
				if (digitalAsset == null) {
					digitalAsset = getBean(ContextIdNames.DIGITAL_ASSET);
				}
				digitalAsset.setMaxDownloadTimes(ConverterUtils.string2Int(value));

				sku.setDigitalAsset(digitalAsset);
			}
		});
	}

	private void createImportFieldAvailabilityCriteria() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "availabilityCriteria";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				typeCheck(object);
				return ((Product) object).getAvailabilityCriteria().toString();
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				typeCheck(object);
				if (checkNullValue(value)) {
					return;
				}

				((Product) object).setAvailabilityCriteria(AvailabilityCriteria.valueOf(value));
			}
		});
	}

	private void createImportFieldPreBackOrderLimit() {
		final String importFieldImage = PREFIX_OF_FIELD_NAME + "preBackOrderLimit";
		addImportField(importFieldImage, new AbstractImportFieldImpl(importFieldImage, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object product) {
				return String.valueOf(((Product) product).getPreOrBackOrderLimit());
			}

			@Override
			public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				((Product) product).setPreOrBackOrderLimit(ConverterUtils.string2Int(value));
			}
		});
	}

	private void createImportFieldNotSoldSeperately() {
		final String importFieldImage = PREFIX_OF_FIELD_NAME + "notSoldSeparately";
		addImportField(importFieldImage, new AbstractImportFieldImpl(importFieldImage, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object product) {
				return String.valueOf(((Product) product).isNotSoldSeparately());
			}

			@Override
			public void setStringValue(final Object product, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				((Product) product).setNotSoldSeparately(ConverterUtils.string2Boolean(value));
			}
		});
	}

	@Override
	public void deleteEntity(final Entity entity) {
		ProductService productService = getBean(ContextIdNames.PRODUCT_SERVICE);
		Product productToBeDeleted = ((Product) entity);
		if (productService.canDelete(productToBeDeleted)) {
			productService.removeProductTree(entity.getUidPk());
		} else {
			throw new EpProductInUseException("Product with code: "
					+ productToBeDeleted.getCode()
					+ " either has a sku in an active order or is in a bundle so cannot be deleted.");
		}
	}

	@Override
	public String getPrefixOfName() {
		return PREFIX_OF_IMPORT_DATA_TYPE_NAME;
	}

	@Override
	public void setSupportedLocales(final Collection<Locale> locales) {
		super.setSupportedLocales(locales);
		clearAllImportFields();
		init(productType);
	}

	@Override
	public void setSupportedCurrencies(final Collection<Currency> currencies) {
		super.setSupportedCurrencies(currencies);
		clearAllImportFields();
		init(productType);
	}

	/**
	 * {@inheritDoc} <br>
	 * Then clears all import fields and recreates them.
	 */
	@Override
	public void setRequiredLocale(final Locale locale) {
		super.setRequiredLocale(locale);
		clearAllImportFields();
		init(productType);
	}

	/**
	 * {@inheritDoc} <br>
	 * Then clears all import fields and recreates them.
	 */
	@Override
	public void setRequiredCurrency(final Currency currency) {
		super.setRequiredCurrency(currency);
		clearAllImportFields();
		init(productType);
	}

	private void validateTextLength(final String value) {
		if (!getUtilityBean().checkShortTextMaxLength(value)) {
			throw new EpTooLongBindException(super.getName());
		}
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
