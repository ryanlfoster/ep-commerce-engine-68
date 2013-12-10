package com.elasticpath.domain.dataimport.impl;

import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
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
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Defines fields and necessary information to import <code>ProductSku</code>.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyMethods" })
public class ImportDataTypeProductSkuImpl extends AbstractImportDataTypeImpl {

	private static final long serialVersionUID = 5000000001L;

	/**
	 * A prefix used in product import data type name.
	 */
	protected static final String PREFIX_OF_IMPORT_DATA_TYPE_NAME = "ProductSku";

	/**
	 * An import data type message key. Usually, key for a message before separator.
	 */
	protected static final String IMPORT_DATA_TYPE_MESSAGE_KEY = "ImportDataType_ProductSku";

	/**
	 * A prefix used in product import field name.
	 */
	protected static final String PREFIX_OF_FIELD_NAME = "";

	private String typeName;

	private String guidFieldName;

	private static final String IMPORT_JOB_RUNNER_BEAN_NAME = "importJobRunnerProductSku";

	private static final String MSG_EXPECTING_A_PRODUCT_SKU = "Expecting a product sku.";

	private ProductType productType;

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
		createImportFieldSkuCode();
		createImportFieldStartDate();
		createImportFieldEndDate();
		createImportFieldProduct();

		// sku option
		createImportFieldsForSkuOptions();

		// spec
		createImportFieldShippable();
		createImportFieldLength();
		createImportFieldWidth();
		createImportFieldHeight();
		createImportFieldWeight();
		createImportFieldImage();

		// Attribute
		createImportFieldsForAttributes();

		// Digital Asset
		createImportFieldDigital();
		createImportFieldFileName();
		createImportFieldExpiryDays();
		createImportFieldMaxDownloadTimes();
	}

	private void createImportFieldProduct() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "productCode";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				return ((ProductSku) entity).getProduct().getGuid();
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!getUtilityBean().isValidGuidStr(value)) {
					throw new EpInvalidValueBindException(super.getName());
				}

				final Product product = service.findProductByGuid(value, false, false, true);

				((ProductSku) entity).setProduct(product);
			}

			@Override
			public void checkStringValue(final Object persistenceObject, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}
				if (!service.isProductGuidExist(value)) {
					throw new EpInvalidGuidBindException(importFieldName);
				}
			}
		});
	}

	private void createImportFieldSkuCode() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "skuCode";
		guidFieldName = importFieldName;
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), true, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				return ((ProductSku) entity).getSkuCode();
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					throw new EpNonNullBindException(super.getName());
				}

				if (!getUtilityBean().isValidGuidStr(value)) {
					throw new EpInvalidValueBindException(super.getName());
				}

				((ProductSku) entity).setSkuCode(value);
			}
		});
	}

	private void createImportFieldStartDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "enableDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {

				final Date date = ((ProductSku) entity).getStartDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtilityBean().getDefaultLocalizedDateFormat());
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					((ProductSku) entity).setStartDate(new Date());
				} else {
					((ProductSku) entity).setStartDate(ConverterUtils.string2Date(value, getUtilityBean().getDefaultLocalizedDateFormat()));
				}
			}

		});
	}

	private void createImportFieldEndDate() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "disableDate";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Date.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				final Date date = ((ProductSku) entity).getEndDate();
				if (date == null) {
					return GlobalConstants.NULL_VALUE;
				}

				return ConverterUtils.date2String(date, getUtilityBean().getDefaultLocalizedDateFormat());
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					((ProductSku) entity).setEndDate(null);
					return;
				}

				((ProductSku) entity).setEndDate(ConverterUtils.string2Date(value, getUtilityBean().getDefaultLocalizedDateFormat()));
			}
		});
	}

	private void createImportFieldWidth() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "width";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				return ObjectUtils.toString(((ProductSku) entity).getWidth(), GlobalConstants.NULL_VALUE);
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				((ProductSku) entity).setWidth(ConverterUtils.string2BigDecimal(value));
			}
		});
	}

	private void createImportFieldHeight() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "height";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				return ObjectUtils.toString(((ProductSku) entity).getHeight(), GlobalConstants.NULL_VALUE);
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				((ProductSku) entity).setHeight(ConverterUtils.string2BigDecimal(value));
			}
		});
	}

	private void createImportFieldLength() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "length";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				return ObjectUtils.toString(((ProductSku) entity).getLength(), GlobalConstants.NULL_VALUE);
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				((ProductSku) entity).setLength(ConverterUtils.string2BigDecimal(value));
			}
		});
	}

	private void createImportFieldWeight() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "weight";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				return ObjectUtils.toString(((ProductSku) entity).getWeight(), GlobalConstants.NULL_VALUE);
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				((ProductSku) entity).setWeight(ConverterUtils.string2BigDecimal(value));
			}
		});
	}

	private void createImportFieldFileName() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "fileName";

		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, String.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				ProductSku sku = (ProductSku) entity;
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
				ProductSku sku = (ProductSku) entity;
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

	private void createImportFieldExpiryDays() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "expiryDays";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {

				ProductSku sku = (ProductSku) entity;
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

				ProductSku sku = (ProductSku) entity;
				if (!sku.isDigital()) {
					return;
				}
				DigitalAsset digitalAsset = sku.getDigitalAsset();
				if (digitalAsset == null) {
					digitalAsset = getBean(ContextIdNames.DIGITAL_ASSET);

				}

				digitalAsset.setExpiryDays(ConverterUtils.string2Int(value));
				sku.setDigitalAsset(digitalAsset);
			}
		});
	}

	private void createImportFieldMaxDownloadTimes() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "maxDownloadTimes";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Integer.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {

				ProductSku sku = (ProductSku) entity;
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
				ProductSku sku = (ProductSku) entity;
				if (!sku.isDigital()) {
					return;
				}
				DigitalAsset digitalAsset = sku.getDigitalAsset();
				if (digitalAsset == null) {
					digitalAsset = getBean(ContextIdNames.DIGITAL_ASSET);

				}

				digitalAsset.setMaxDownloadTimes(ConverterUtils.string2Int(value));
				sku.setDigitalAsset(digitalAsset);
			}
		});
	}

	/**
	 * Populate sku attribute import fields based on the given product type.
	 *
	 * @param productType the product type
	 */
	private void createImportFieldsForAttributes() {
		final AttributeGroup attributeGroup = productType.getSkuAttributeGroup();

		for (AttributeGroupAttribute attributeGroupAttribute : attributeGroup.getAttributeGroupAttributes()) {
			final Attribute attribute = attributeGroupAttribute.getAttribute();
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
		final String attributeType = attribute.getAttributeType().toString();
		final String attributeKey = attribute.getKey();

		String name;
		if (locale == null) {
			name = PREFIX_OF_FIELD_NAME + attributeKey;
		} else {
			name = PREFIX_OF_FIELD_NAME + attributeKey + '(' + locale.toString() + ')';
		}
		addImportField(name, new AbstractImportFieldImpl(name, attributeType, attribute.isRequired(), false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				return ((ProductSku) entity).getAttributeValueGroup().getStringAttributeValue(attributeKey, locale);
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}
				AttributeValueGroup attributeValueGroup = ((ProductSku) entity).getAttributeValueGroup();
				attributeValueGroup.setStringAttributeValue(attribute, locale, value);
				AttributeValue attributeValue = attributeValueGroup.getAttributeValue(attributeKey, locale);

				getValidatorUtils().validateAttributeValue(attributeValue);
			}
		});
	}

	/**
	 * Populate sku option import fields based on the given product type.
	 *
	 * @param productType the product type
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
					return ((ProductSku) entity).getSkuOptionValue(skuOption).getOptionValueKey();
				}

				@Override
				public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
					if (checkNullValue(value)) {
						throw new EpNonNullBindException(super.getName());
					}
					((ProductSku) entity).setSkuOptionValue(skuOption, value);
				}
			});
		}
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
		if (!(object instanceof ProductSku)) {
			throw new EpBindException(MSG_EXPECTING_A_PRODUCT_SKU);
		}
	}

	private void createImportFieldImage() {
		final String importFieldImage = PREFIX_OF_FIELD_NAME + "image";
		addImportField(importFieldImage, new AbstractImportFieldImpl(importFieldImage, String.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object object) {
				return ((ProductSku) object).getImage();
			}

			@Override
			public void setStringValue(final Object object, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					((ProductSku) object).setImage(null);
				} else {
					validateTextLength(value);
					((ProductSku) object).setImage(value);
				}
			}
		});
	}

	private void createImportFieldShippable() {
		final String importFieldName = PREFIX_OF_FIELD_NAME + "shippable";
		addImportField(importFieldName, new AbstractImportFieldImpl(importFieldName, Boolean.class.toString(), false, false) {

			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getStringValue(final Object entity) {
				return String.valueOf(((ProductSku) entity).isShippable());
			}

			@Override
			public void setStringValue(final Object entity, final String value, final ImportGuidHelper service) {
				if (checkNullValue(value)) {
					return;
				}

				((ProductSku) entity).setShippable(ConverterUtils.string2Boolean(value));
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

				((ProductSku) entity).setDigital(ConverterUtils.string2Boolean(value));
			}
		});
	}

	@Override
	public void deleteEntity(final Entity entity) {
		ProductSku productSku = (ProductSku) entity;
		if (!productSku.getProduct().hasMultipleSkus()) {
			throw new EpUnsupportedOperationException("ProductSku from a SingleSku Product cannot be deleted.");
		}
		ProductSkuService productSkuService = getBean(ContextIdNames.PRODUCT_SKU_SERVICE);
		if (!productSkuService.canDelete(productSku)) {
			throw new EpServiceException("Unable to delete ProductSku code=" + productSku.getSkuCode()
					+ " because it has an uncompleted shipment.");
		}
		productSkuService.removeProductSkuTree(productSku.getUidPk());
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
