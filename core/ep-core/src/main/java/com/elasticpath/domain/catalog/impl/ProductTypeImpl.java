/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.impl.ProductTypeProductAttributeImpl;
import com.elasticpath.domain.attribute.impl.ProductTypeSkuAttributeImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Represents the type of a <code>Product</code>, which determines the set of attributes that it has. An example of a product type is "Shoe." Note
 * that this differs from a product category, which might also be called "Shoes" because this describes the characteristics of the product rather
 * than how they are displayed and organized in the store.
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
@Entity
@Table(name = ProductTypeImpl.TABLE_NAME)
@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = { @FetchAttribute(name = "productAttributeGroupAttributes"),
		@FetchAttribute(name = "name"), @FetchAttribute(name = "withMultipleSkus"), @FetchAttribute(name = "skuOptions") })
public class ProductTypeImpl extends AbstractLegacyEntityImpl implements ProductType {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TPRODUCTTYPE";

	private boolean multipleSkus;

	private Set<SkuOption> skuOptions = new HashSet<SkuOption>();

	private Set<AttributeGroupAttribute> productAttributeGroupAttributes = new HashSet<AttributeGroupAttribute>();

	private Set<AttributeGroupAttribute> skuAttributeGroupAttributes = new HashSet<AttributeGroupAttribute>();

	private String template;

	private String description;

	private String name;

	private AttributeGroup skuAttributeGroup;

	private TaxCode taxCode;

	private AttributeGroup productAttributeGroup;

	private Catalog catalog;

	private long uidPk;

	private boolean excludedFromDiscount;

	private String guid;

	/**
	 * The default constructor.
	 */
	public ProductTypeImpl() {
		super();
	}

	/**
	 * Returns <code>true</code> if the product type may have multiple skus.
	 *
	 * @return <code>true</code> if the product type has multiple skus
	 */
	@Basic
	@Column(name = "WITH_MULTIPLE_SKUS")
	public boolean isWithMultipleSkus() {
		return multipleSkus;
	}

	/**
	 * Sets the multiple sku flag.
	 *
	 * @param multipleSkuFlag sets it to <code>true</code> if the product type may have multiple skus.
	 */
	public void setWithMultipleSkus(final boolean multipleSkuFlag) {
		multipleSkus = multipleSkuFlag;
	}

	/**
	 * Gets the available options for configuring SKUs of this product.
	 *
	 * @return the set of options for configuring SKUs of this product or null if there are no configurable options
	 */
	@ManyToMany(targetEntity = SkuOptionImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinTable(name = "TPRODUCTTYPESKUOPTION", joinColumns = { @JoinColumn(name = "PRODUCT_TYPE_UID") },
			   inverseJoinColumns = { @JoinColumn(name = "SKU_OPTION_UID") })
	public Set<SkuOption> getSkuOptions() {
		return skuOptions;
	}

	/**
	 * Gets the available options for configuring SKUs of this product. Updates the default SKU option values based on the values specified in a
	 * given default SKU.
	 *
	 * @param defaultSku the SKU whose option values are to be the defaults for the given options
	 * @return the set of options for configuring SKUs of this product or null if there are no configurable options
	 */
	public Set<SkuOption> getSkuOptions(final ProductSku defaultSku) {
		for (final SkuOption currSkuOption : this.getSkuOptions()) {
			for (final SkuOptionValue currOptionValue : currSkuOption.getOptionValues()) {
				for (final SkuOptionValue defaultOptionValue : defaultSku.getOptionValues()) {
					if (defaultOptionValue.getOptionValueKey().equals(currOptionValue.getOptionValueKey())) {
						currSkuOption.setDefaultOptionValue(currOptionValue);
					}
				}
			}
		}
		return this.getSkuOptions();
	}

	@Override
	public List<SkuOption> getSortedSkuOptionListForRecurringItems(final ProductSku defaultSku) {
		final List<SkuOption> sortedSkuOptionList = new ArrayList<SkuOption>(this.getSkuOptions(defaultSku));
		final Comparator<SkuOption> comparator = new Comparator<SkuOption>() {
			public int compare(final SkuOption skuOption1, final SkuOption skuOption2) {
				if (skuOption1.getOptionKey().equals(SkuOption.FREQUENCY_OPTION_KEY)) {
					return 1;
				}
				if (skuOption2.getOptionKey().equals(SkuOption.FREQUENCY_OPTION_KEY)) {
					return -1;
				}
				final Locale defaultLocale = ProductTypeImpl.this.getCatalog().getDefaultLocale();
				return skuOption1.getDisplayName(defaultLocale, true).compareTo(skuOption2.getDisplayName(defaultLocale, true));
			}

		};
		java.util.Collections.sort(sortedSkuOptionList, comparator);
		return sortedSkuOptionList;
	}

	@Override
	public List<SkuOption> getSortedSkuOptionList(final ProductSku defaultSku) {

		//FIXME
		//Temporary fix for [MSC-6955]. Get the sorted SKU option to allowing the SF have correct (sorted) sku option list.
		//We may need to enhance the SKUOPTION domain model to have ordering ability.
		final List<SkuOption> sortedSkuOptionList = new ArrayList<SkuOption>(this.getSkuOptions(defaultSku));
		java.util.Collections.sort(sortedSkuOptionList, new Comparator<SkuOption>() {
			public int compare(final SkuOption skuOption1, final SkuOption skuOption2) {
				final Locale defaultLocale = ProductTypeImpl.this.getCatalog().getDefaultLocale();
				return skuOption1.getDisplayName(defaultLocale, true).compareTo(skuOption2.getDisplayName(defaultLocale, true));
			}

		});
		return sortedSkuOptionList;
	}

	/**
	 * Sets the available options for configuring SKUs of this product.
	 *
	 * @param skuOptions the set of available options for configuring SKUs of this product
	 */
	public void setSkuOptions(final Set<SkuOption> skuOptions) {
		this.skuOptions = skuOptions;
	}

	/**
	 * Sets the product attribute group.
	 *
	 * @param productAttributeGroup the product attribute group.
	 */
	public void setProductAttributeGroup(final AttributeGroup productAttributeGroup) {
		this.productAttributeGroup = productAttributeGroup;
		if (productAttributeGroup == null) {
			setProductAttributeGroupAttributes(null);
		} else {
			setProductAttributeGroupAttributes(productAttributeGroup.getAttributeGroupAttributes());
		}
	}

	/**
	 * Returns the product attribute group.
	 *
	 * @return the product attribute group
	 */
	@Transient
	public AttributeGroup getProductAttributeGroup() {
		if (productAttributeGroup == null) {
			productAttributeGroup = getBean(ContextIdNames.ATTRIBUTE_GROUP);
		}
		productAttributeGroup.setAttributeGroupAttributes(getProductAttributeGroupAttributes());
		return productAttributeGroup;
	}

	/**
	 * Get the product type name.
	 *
	 * @return the product type name
	 */
	@Basic
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the product type name.
	 *
	 * @param name the product type name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the product type description.
	 *
	 * @return the product type description
	 */
	@Transient
	public String getDescription() {
		return description;
	}

	/**
	 * Set the product type description.
	 *
	 * @param description the product type description
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Returns the display template of this category type.
	 *
	 * @return the template
	 */
	@Basic
	@Column(name = "TEMPLATE")
	public String getTemplate() {
		return template;
	}

	/**
	 * Set the template.
	 *
	 * @param template the template
	 */
	public void setTemplate(final String template) {
		this.template = template;
	}

	/**
	 * Sets the product sku attribute group.
	 *
	 * @param skuAttributeGroup the product attribute group.
	 */
	public void setSkuAttributeGroup(final AttributeGroup skuAttributeGroup) {
		this.skuAttributeGroup = skuAttributeGroup;
		if (skuAttributeGroup == null) {
			setSkuAttributeGroupAttributes(null);
		} else {
			setSkuAttributeGroupAttributes(skuAttributeGroup.getAttributeGroupAttributes());
		}
	}

	/**
	 * Returns the product sku attribute group.
	 *
	 * @return the product sku attribute group
	 */
	@Transient
	public AttributeGroup getSkuAttributeGroup() {
		if (skuAttributeGroup == null) {
			skuAttributeGroup = getBean(ContextIdNames.ATTRIBUTE_GROUP);
		}
		skuAttributeGroup.setAttributeGroupAttributes(getSkuAttributeGroupAttributes());
		return skuAttributeGroup;
	}

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		super.initialize();
		if (getSkuAttributeGroup() == null) {
			AttributeGroup attributeGroup = getBean(ContextIdNames.ATTRIBUTE_GROUP);
			setSkuAttributeGroup(attributeGroup);
		}
	}

	/**
	 * Returns the <code>TaxCode</code> associated with this <code>ProductType</code>.
	 *
	 * @return the <code>TaxCode</code>
	 */
	@ManyToOne(targetEntity = TaxCodeImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "TAX_CODE_UID")
	public TaxCode getTaxCode() {
		return taxCode;
	}

	/**
	 * Set the <code>TaxCode</code> associated with this <code>ProductType</code>.
	 *
	 * @param taxCode - the sales tax code for this product type, i.e. "Books".
	 */
	public void setTaxCode(final TaxCode taxCode) {
		this.taxCode = taxCode;
	}

	/**
	 * Get the set of product attribute group attributes.
	 *
	 * @return the attributes
	 */
	@OneToMany(targetEntity = ProductTypeProductAttributeImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "PRODUCT_TYPE_UID", nullable = false)
	@ElementForeignKey(name = "TPRODUCTTYPEATTRIBUTE_IBFK_2")
	@ElementDependent
	public Set<AttributeGroupAttribute> getProductAttributeGroupAttributes() {
		return productAttributeGroupAttributes;
	}

	/**
	 * Set the product attribute group attributes.
	 *
	 * @param productAttributeGroupAttributes the set of attributes
	 */
	public void setProductAttributeGroupAttributes(final Set<AttributeGroupAttribute> productAttributeGroupAttributes) {
		this.productAttributeGroupAttributes = productAttributeGroupAttributes;
	}

	/**
	 * Get the set of sku attribute group attributes.
	 *
	 * @return the set of attributes
	 */
	@OneToMany(targetEntity = ProductTypeSkuAttributeImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "PRODUCT_TYPE_UID", nullable = false)
	@ElementForeignKey(name = "TPRODUCTTYPESKUATTRIBUTE_IBFK_2")
	@ElementDependent
	public Set<AttributeGroupAttribute> getSkuAttributeGroupAttributes() {
		return skuAttributeGroupAttributes;
	}

	/**
	 * Set the sku attribute group attributes.
	 *
	 * @param skuAttributeGroupAttributes the set of attributes
	 */
	public void setSkuAttributeGroupAttributes(final Set<AttributeGroupAttribute> skuAttributeGroupAttributes) {
		this.skuAttributeGroupAttributes = skuAttributeGroupAttributes;
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Get the catalog that this product type belongs to.
	 * @return the catalog
	 */
	@ManyToOne(optional = false, targetEntity = CatalogImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "CATALOG_UID", nullable = false)
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Set the catalog that this product type belongs to.
	 * @param catalog the catalog to set
	 */
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Adds or updates a sku option.
	 *
	 * @param skuOption the sku option to update
	 */
	public void addOrUpdateSkuOption(final SkuOption skuOption) {
		getSkuOptions().remove(skuOption);
		getSkuOptions().add(skuOption);
	}

	/**
	 * Returns marker if product of this type should be excluded from discount calculation.
	 *
	 * @return  <code>true</code> if item should be excluded from discount calculation, false otherwise
	 */
	@Basic
	@Column(name = "EXCLUDE_FROM_DISCOUNT",  nullable = false)
	public boolean isExcludedFromDiscount() {
		return excludedFromDiscount;
	}

	/**
	 * Sets marker if product of this type should be excluded from discount calculation.
	 *
	 * @param excludeFromDiscount  <code>true</code> if item should be excluded from discount calculation, false otherwise
	 */
	public void setExcludedFromDiscount(final boolean excludeFromDiscount) {
		excludedFromDiscount = excludeFromDiscount;
	}

	@Override
	@Transient
	public void removeSkuOptionByGuid(final String skuOptionGuid) {
		final SkuOption skuOption = findSkuOptionByGuid(skuOptionGuid);
		if (skuOption != null) {
			getSkuOptions().remove(skuOption);
		}
	}

	@Override
	@Transient
	public SkuOption findSkuOptionByGuid(final String skuOptionGuid) {
		SkuOption skuOption = null;
		final Set<SkuOption> skuOptions = getSkuOptions();
		for (final SkuOption item : skuOptions) {
			if (skuOptionGuid.equals(item.getGuid())) {
				skuOption = item;
				break;
			}
		}
		return skuOption;
	}

	@Override
	@Transient
	public void removeSkuAttributeByKey(final String skuAttributeKey) {
		final Attribute attribute = findSkuAttributeByKey(skuAttributeKey);
		if (attribute != null) {
			getSkuAttributeGroup().getAttributeGroupAttributes().remove(attribute);
		}
	}

	@Override
	@Transient
	public Attribute findSkuAttributeByKey(final String skuAttributeKey) {
		Attribute attribute = null;
		final AttributeGroup attributeGroup = getSkuAttributeGroup();
		final Set<AttributeGroupAttribute> agas = attributeGroup.getAttributeGroupAttributes();
		for (final AttributeGroupAttribute item : agas) {
			if (skuAttributeKey.equals(item.getAttribute().getKey())) {
				attribute = item.getAttribute();
				break;
			}
		}
		return attribute;
	}

}
