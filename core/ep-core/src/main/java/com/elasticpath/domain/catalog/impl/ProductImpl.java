/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl; //NOPMD -- ExcessivePublicCount

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.ElementType;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.ListenableObject;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.AttributeValueGroupFactory;
import com.elasticpath.domain.attribute.impl.AttributeValueGroupFactoryImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueFactoryImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.catalog.AttributeValueIsRequiredException;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.collections.impl.ProductSkus;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * <p>An implementation of the Product interface for OpenJPA database persistence.</p>
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.TooManyFields", "PMD.AvoidDuplicateLiterals",
					"PMD.CyclomaticComplexity", "PMD.TooManyMethods"
})
@Entity
@Table(name = ProductImpl.TABLE_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = { @FetchAttribute(name = "lastModifiedDate"),
	@FetchAttribute(name = "startDate"), @FetchAttribute(name = "endDate"), @FetchAttribute(name = "productCategories"),
	@FetchAttribute(name = "code"), @FetchAttribute(name = "salesCount"), @FetchAttribute(name = "productSkusInternal"),
	@FetchAttribute(name = "hidden"), @FetchAttribute(name = "minOrderQty"), @FetchAttribute(name = "notSoldSeparately"),
	@FetchAttribute(name = "localeDependantFieldsMap"), @FetchAttribute(name = "brand"), 
	@FetchAttribute(name = "attributeValueMap"), @FetchAttribute(name = "productType"), @FetchAttribute(name = "availabilityCriteriaInternal") }),
	@FetchGroup(name = FetchGroupConstants.PRODUCT_HASH_MINIMAL, attributes = { @FetchAttribute(name = "code") }),
	@FetchGroup(name = FetchGroupConstants.LINK_PRODUCT_CATEGORY, fetchGroups = { FetchGroupConstants.PRODUCT_HASH_MINIMAL },
			attributes = { @FetchAttribute(name = "productCategories") }),
	@FetchGroup(name = FetchGroupConstants.BUNDLE_CONSTITUENTS, attributes = { @FetchAttribute(name = "code") }),
	@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT , attributes = { 
			@FetchAttribute(name = "hidden"), 
			@FetchAttribute(name = "availabilityCriteriaInternal"),
			@FetchAttribute(name = "itemExpectedReleaseDate") })
})
public class ProductImpl extends AbstractLegacyEntityImpl implements Product, PropertyChangeListener {
	private static final String FK_COLUMN_NAME = "PRODUCT_UID";

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(ProductImpl.class);
	
	/** The name of the table & generator to use for persistence. */
	public static final String TABLE_NAME = "TPRODUCT";

	private Date startDate;

	private Date endDate;

	private Date lastModifiedDate;

	private ProductType productType;

	private Map<String, ProductSku> productSkus = new HashMap<String, ProductSku>();

	private Set<ProductCategory> productCategories = new HashSet<ProductCategory>();
	
	private Map<Locale, LocaleDependantFields> localeDependantFieldsMap = new HashMap<Locale, LocaleDependantFields>();

	private Brand brand;

	private ProductSku defaultSku;

	private String image;

	private boolean hidden;
	
	private boolean notSoldSeparately;

	private int salesCount;

	private int minOrderQty = 1;

	private AttributeValueGroup attributeValueGroup;

	private String code;

	private TaxCode taxCode;
	
	private Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
	
	private long uidPk;

	private Date expectedReleaseDate;
	
	private AvailabilityCriteria availabilityCriteria;
	
	private int preOrBackOrderLimit;
	

	/**
	 * Gets the unique identifier for this domain model object. Returns an int so that it can be accessed by Drools. Will likely remove this if
	 * rules don't use the uid.
	 * @return the unique identifier.
	 */
	@Transient
	public int getUidPkInt() {
		return (int) getUidPk();
	}

	/**
	 * Default constructor.
	 */
	public ProductImpl() {
		super();
	}

	/**
	 * Get the start date that this product will become available to customers.
	 * @return the start date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * Set the start date that this product will become valid.
	 *
	 * @param startDate the start date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get the end date. After the end date, the product will change to unavailable to customers.
	 *
	 * @return the end date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * Set the end date. Precondition: endDate is after the start date
	 *
	 * @param endDate the end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Returns the date when the product was last modified.
	 *
	 * @return the date when the product was last modified
	 */
	@Basic(optional = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * Set the date when the product was last modified.
	 *
	 * @param lastModifiedDate the date when the product was last modified
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	/**
	 * Returns true if the current date is within the start and end dates for this product.
	 *
	 * @param currentDate the current date
	 * @return true if the current date is within the start and end dates for this product
	 */
	@Transient
	public boolean isWithinDateRange(final Date currentDate) {
		if (currentDate.getTime() < getStartDate().getTime()) {
			return false;
		} else if (getEndDate() != null && currentDate.getTime() > getEndDate().getTime()) {
			return false;
		}
		return true;
	}

	@Override
	@Transient
	public boolean hasSkuWithinDateRange(final Date currentDate) {
		if (!hasMultipleSkus()) {
			return true;
		}
		boolean skuFound = false;
		for (ProductSku sku : getProductSkus().values()) {
			skuFound = skuFound || sku.isWithinDateRange(currentDate);
		}
		return skuFound;
	}

	/**
	 * Get the display template.
	 *
	 * @return the template
	 */
	@Transient
	public String getTemplate() {
		return this.getProductType().getTemplate();
	}

	/**
	 * Get the display template name of this product.
	 *
	 * @param defaultTemplate the default template name
	 * @return the template name if it is defined for this product, otherwise the given default template name
	 */
	public String getTemplateWithFallBack(final String defaultTemplate) {
		String templateName;
		if (this.getProductType().getTemplate() == null) {
			templateName = defaultTemplate;
		} else {
			templateName = this.getProductType().getTemplate();
		}
		return templateName;
	}

	/**
	 * Compares this product with the specified object for order.
	 *
	 * @param product the given object
	 * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 * @throws EpDomainException in case the given object is not a <code>Product</code>
	 */
	public int compareTo(final Product product) throws EpDomainException {
		// Compare object id
		if (this == product) {
			return 0;
		}
		// Compare non-zero uidPk
		if (this.getUidPk() != 0 && this.getUidPk() == product.getUidPk()) {
			return 0;
		}
		// Then compare guid
		return this.getGuid().compareTo(product.getGuid());
	}
	
	/**
	 * Get the master <code>Catalog</code> for this product.
	 * @return the master Catalog
	 */
	@Transient
	public Catalog getMasterCatalog() {
		for (Category category : this.getCategories()) {
			if (category.getCatalog().isMaster()) {
				return category.getCatalog();
			}
		}
		return null;
	}

	/**
	 * Get the product SKUs.
	 * This implementation calls getProductSkusInternal().
	 *
	 * @return an unmodifiable Map of the product's <code>ProductSku</code>s
	 */
	@Transient
	public Map<String, ProductSku> getProductSkus() {
		//getProductSkusInternal may return null if JPA has not loaded the product skus.
		Map<String, ProductSku> productSkus = this.getProductSkusInternal();
		if (productSkus == null) {
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(productSkus);
	}

	/**
	 * Set the variations of this product.
	 *
	 * @param productSkus the map of <code>ProductSku</code>s
	 */
	public void setProductSkus(final Map<String, ProductSku> productSkus) {
		if (productSkus == null) {
			getProductSkusInternal().clear();
		} else {
			setProductSkusInternal(productSkus);
		}
	}
	
	/**
	 * Get the product SKUs.
	 *
	 * @return the product's <code>ProductSku</code>s
	 */
	@OneToMany(targetEntity = ProductSkuImpl.class, mappedBy = "productInternal",
			   cascade = { CascadeType.ALL })
	@MapKey(name = "skuCodeInternal")
	@ElementDependent
	protected Map<String, ProductSku> getProductSkusInternal() {
		return this.productSkus;
	}

	/**
	 * Set the variations of this product.
	 *
	 * @param productSkus the map of <code>ProductSku</code>s
	 */
	protected void setProductSkusInternal(final Map<String, ProductSku> productSkus) {
		this.productSkus = productSkus;
		if (productSkus != null) {
			for (ProductSku productSku : productSkus.values()) {
				if (productSku != null) {
					//OpenJPA calls the setter passing a map with valid keys, but null values. 
					//It might be an OpenJPA bug, but looking for a couple of minutes in their JIRA, I couldn't find an issue.
					//when we are upgrading to OpenJPA 2.x, we should try to remove this null check.					
					((ListenableObject) productSku).addPropertyChangeListener(this, true);
				}
			}
		}
	}	

	/**
	 * Get the display template.
	 *
	 * @return the template
	 */
	@ManyToOne(targetEntity = ProductTypeImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "PRODUCT_TYPE_UID")
	public ProductType getProductType() {
		return this.productType;
	}

	/**
	 * Set the <code>ProductType</code>.
	 *
	 * @param productType the <code>ProductType</code>
	 */
	public void setProductType(final ProductType productType) {
		this.productType = productType;
	}

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		// don't call super or else setGuid will be init with a random guid.
		//super.initialize();
		if (this.getStartDate() == null) {
			this.setStartDate(new Date());
		}
		if (getAttributeValueGroup() == null) {
			initializeAttributeValueGroup();
		}
		if (getImage() == null) {
			setImage("");
		}
	}

	private void initializeAttributeValueGroup() {
		attributeValueGroup = getAttributeValueGroupFactory().createAttributeValueGroup(getAttributeValueMap());
	}

	/**
	 * Creates the factory used to create AttributeValueGroups (and AttributeValues).  Override this method
	 * in an extension class if you need to specify a custom implementation for AttributeValueGroupImpl or
	 * ProductAttributeValueImpl.
	 *
	 * @return the factory
	*/
	@Transient
	protected AttributeValueGroupFactory getAttributeValueGroupFactory() {
		return new AttributeValueGroupFactoryImpl(new ProductAttributeValueFactoryImpl());
	}

	/**
	 * Add this product to the specified category.
	 * Use as default if the catalog of the category being added doesn't have a default category yet.
	 * @param category the category
	 */
	public void addCategory(final Category category) {
		if (category != null) {
			
			// Check if category is already in collection
			for (ProductCategory existingCategory : getProductCategories()) {
				if (existingCategory != null && category.equals(existingCategory.getCategory())) {
					return;
				}
			}
			
			final ProductCategory productCategory = createProductCategory(category);
			getProductCategories().add(productCategory);
			if (this.getDefaultCategory(category.getCatalog()) == null) {
				productCategory.setDefaultCategory(true);
			}
		}
	}

	/**
	 * Factory method for creating product category value objects.  Override this method if you need to supply a different implementation
	 * class for ProductCategory in an extension project.
	 *
	 * @param category the category
	 * @return A brand new ProductCategory object.
	 */
	protected ProductCategory createProductCategory(final Category category) {
		final ProductCategory productCategory = new ProductCategoryImpl();
		productCategory.setCategory(category);
		productCategory.setProduct(this);

		return productCategory;
	}

	/**
	 * Gets all product category associations.
	 *
	 * @return all product category associations
	 */
	@OneToMany(targetEntity = ProductCategoryImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "product")
	@ElementDependent
	protected Set<ProductCategory> getProductCategories() {
		return productCategories;
	}

	/**
	 * Remove this product from the specified category.
	 *
	 * @param category the category
	 */
	public void removeCategory(final Category category) {
		if (category == null) {
			return;
		}
		if (category.equals(this.getDefaultCategoryInMasterCatalog())) {
			throw new EpDomainException("Cannot remove product from default category in master catalog.");
		}
//		if (category.isLinked()) {
//			throw new EpDomainException("Cannot remove product from a linked category. It can only be included/excluded.");
//		}
		List<Category> categoriesRemoved = new ArrayList<Category>();
		for (Iterator<ProductCategory> productCategoryIterator = this.getProductCategories().iterator(); productCategoryIterator.hasNext();) {
			ProductCategory productCategory = productCategoryIterator.next();
			Category currentCategory = productCategory.getCategory();
			if (currentCategory.equals(category) || isCategoryLinkedToMaster(currentCategory, category)) {
				productCategoryIterator.remove();
				categoriesRemoved.add(currentCategory);
			}
		}
		for (Category removedCategory : categoriesRemoved) {
			ensureProductHasDefaultCategory(removedCategory, categoriesRemoved);
		}
	}
	
	/**
	 * Ensures that another default category is selected. 
	 * Should be called right after a category is removed 
	 * from the list of product categories.
	 * 
	 * @param removedCategory the category that was removed
	 * @param categoriesRemoved the list of categories removed so far - none of these may become product default categories
	 */
	protected void ensureProductHasDefaultCategory(final Category removedCategory, final List<Category> categoriesRemoved) {
		Catalog catalog = removedCategory.getCatalog();
		ProductCategory productCategoryNominee = null;
		for (ProductCategory productCategory : getProductCategories()) {
			Category currentCategory = productCategory.getCategory();
			// workaround to fix ACELABEX-176. don't replace it with !categoriesRemoved.contains(removedCategory) clause. 
			boolean isRemovedCategory = !containsRemovedCategory(removedCategory, categoriesRemoved); 
			if (isRemovedCategory && catalog.equals(currentCategory.getCatalog())
					&& (productCategoryNominee == null 
							|| (productCategory.getCategory().isLinked()
									&& isDefaultCategory(productCategory.getCategory().getMasterCategory())))) {
				
				productCategoryNominee = productCategory;
			}
		}
		if (productCategoryNominee != null) {
			productCategoryNominee.setDefaultCategory(true);
		}
	}
	
	/*
	 * Returns true if categoriesRemoved list contains removedCategory.
	 * NOTICE: comparison performed by uidPk.
	 */
	private boolean containsRemovedCategory(final Category removedCategory, final List<Category> categoriesRemoved) {
		for (Category category : categoriesRemoved) {
		    if (category.getUidPk() == removedCategory.getUidPk()) {
			return true;
		    }
		}
		return false;
	}	

	/**
	 * Checks if a category is the default one for this product.
	 * 
	 * @param category the category to check
	 * @return true if default
	 */
	protected boolean isDefaultCategory(final Category category) {
		for (ProductCategory productCategory : getProductCategories()) {
			if (productCategory.isDefaultCategory()
					&& productCategory.getCategory().equals(category)) {
				return true;
			}
		}
		return false;
	}

	private boolean isCategoryLinkedToMaster(final Category categoryToCheck, final Category masterCategory) {
		if (!categoryToCheck.isLinked()) {
			return false;
		}
		if (masterCategory.equals(categoryToCheck.getMasterCategory())) {
			return true;
		}
		return false;
	}

	/**
	 * Removes this product from all categories except its default category in its master catalog.
	 */
	public void removeAllCategories() {
		//Cannot use enhanced for loop here because we call iterator.remove()
		for (Iterator<ProductCategory> productCategoryIterator = this.getProductCategories().iterator(); productCategoryIterator.hasNext();) {
			if (!productCategoryIterator.next().getCategory().equals(this.getDefaultCategoryInMasterCatalog())) {
				productCategoryIterator.remove();
			}
		}
	}
	
	/**
	 * Sets all product category associations.
	 *
	 * @param productCategories all product category associations
	 */
	protected void setProductCategories(final Set<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}

	/**
	 * Returns the default SKU corresponding to this product.
	 *
	 * @return the <code>ProductSku</code>
	 */
	@Transient
	public ProductSku getDefaultSku() {
		if (defaultSku == null && getProductSkus() != null && !getProductSkus().isEmpty()) {
			return getProductSkus().values().iterator().next();
		}
		return defaultSku;
	}

	/**
	 * Sets the default SKU for this product.
	 *
	 * @param defaultSku the default SKU
	 */
	public void setDefaultSku(final ProductSku defaultSku) {
		this.defaultSku = defaultSku;
		addOrUpdateSku(defaultSku);
	}

	/**
	 * Returns true if this product is in the category with the specified category uid or a child of that category.
	 *
	 * @param compoundCategoryGuid the compound category guid that contains catalog code and category code.
	 * @return true if the product is in the category
	 */
	@Transient
	public boolean isInCategory(final String compoundCategoryGuid) {
		for (ProductCategory productCategory : getProductCategories()) {
			final Category category = productCategory.getCategory();
			if (isInCategoryHierarchy(compoundCategoryGuid, category)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if this product is belong to (right under) this category.
	 *
	 * @param categoryUid the category uid to check if this product is right under it.
	 * @return true if the product is belong to the category
	 */
	@Transient
	public boolean isBelongToCategory(final long categoryUid) {
		for (ProductCategory productCategory : getProductCategories()) {
			final Category category = productCategory.getCategory();
			if (category.getUidPk() == categoryUid) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the specified category is in another category or one of its parents.
	 *
	 * @param searchCategoryCode the category code to search for in the category heirarchy
	 * @param categoryHierarchy the category hierarchy to search for the searchCategory
	 * @return true if searchCategory is found in categoryHierarchy
	 */
	private boolean isInCategoryHierarchy(final String searchCategoryCode, final Category categoryHierarchy) {
		if (categoryHierarchy == null) {
			return false;
		}
		if (searchCategoryCode.equals(categoryHierarchy.getCompoundGuid())) {
			return true;
		}
		return isInCategoryHierarchy(searchCategoryCode, categoryHierarchy.getParent());
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof ProductImpl)) {
			return false;
		}
		
		ProductImpl product = (ProductImpl) other;
		return ObjectUtils.equals(code, product.code);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.code);
	}

	/**
	 * Gets the all <code>LocaleDependantFields</code> as a map.
	 *
	 * @return the <code>LocaleDependantFields</code> map
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementType(value = ProductLocaleDependantFieldsImpl.class)
	@MapKey(name = "locale")
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = false)
	@ElementDependent
	@ElementForeignKey (name = "TPRODUCTLDF_IBFK_1")
	protected Map<Locale, LocaleDependantFields> getLocaleDependantFieldsMap() {
		return this.localeDependantFieldsMap;
	}

	/**
	 * Sets the all <code>LocaleDependantFields</code> with the given map.
	 *
	 * @param localeDependantFieldsMap the <code>LocaleDependantFields</code> map to set
	 */
	public void setLocaleDependantFieldsMap(final Map<Locale, LocaleDependantFields> localeDependantFieldsMap) {
		this.localeDependantFieldsMap = localeDependantFieldsMap;
	}
	
	/**
	 * Returns the {@link LocaleDependantFields} of the given locale (cannot be <code>null</code>).
	 * Falls back to the language and country (if variant present) then to the language (if
	 * country is present). If no field is defined for the given locale, optionally falls
	 * back to this Product's Master Catalog's default locale, for which the product is
	 * guaranteed to have values for its locale-dependent fields.
	 *
	 * @param locale the locale
	 * @param fallback whether to fall back to the Master Catalog's default locale if
	 * there is no LocaleDependantFields object for the given locale
	 * @return the {@link LocaleDependantFields} of the given non-null locale if it exists, or
	 * 	for the fallback locale if requested, or an empty LDF.
	 */
	public LocaleDependantFields getLocaleDependantFields(final Locale locale, final boolean fallback) {
		CatalogLocaleFallbackPolicyFactory factory = getBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY);
		return getLocaleDependantFields(factory.createProductLocaleFallbackPolicy(locale, fallback, this));
	}

	/**
	 * Creates an appropriate LocaleDependantFields value object for the given locale. Override this method if you need to change the implementation
	 * class in an extension project.
	 * 
	 * @param locale the Locale this LDF applies to
	 * @return a LocaleDependentFields object
	 */
	protected LocaleDependantFields createLocaleDependantFields(final Locale locale) {
		final LocaleDependantFields ldf = new ProductLocaleDependantFieldsImpl();
		ldf.setLocale(locale);
		ldf.setDisplayName(this.getCode());

		return ldf;
	}

	/**
	 * Returns the {@link LocaleDependantFields} of the given locale (cannot be <code>null</code>). Falls back to the language and country (if
	 * variant present) then to the language (if country is present). If no field is defined for the given locale, returns a fallback. This
	 * implementation will check that the fields returned have a product's Description filled in, and if not will attempt to get the Description from
	 * the containing Category. {@link LocaleDependantFields}.
	 * 
	 * @param locale the locale
	 * @return the {@link LocaleDependantFields} of the given non-null locale if it exists, otherwise an empty {@link LocaleDependantFields}
	 */
	public LocaleDependantFields getLocaleDependantFields(final Locale locale) {
		return this.getLocaleDependantFields(locale, true);
	}

	/**
	 * Returns the <code>LocaleDependantFields</code> of the given locale without fallback.
	 *
	 * @param locale the locale
	 * @return the <code>LocaleDependantFields</code> of the given locale
	 */
	public LocaleDependantFields getLocaleDependantFieldsWithoutFallBack(final Locale locale) {
		return this.getLocaleDependantFields(locale, false);
	}

	/**
	 * Sets the <code>LocaleDependantFields</code>.
	 *
	 * @param ldf the <code>LocaleDependantFields</code> instance to set.
	 */
	public void addOrUpdateLocaleDependantFields(final LocaleDependantFields ldf) {
		if (ldf.getLocale() == null) {
			throw new EpDomainException("Locale is not set.");
		}
		getLocaleDependantFieldsMap().put(ldf.getLocale(), ldf);
	}

	/**
	 * Adds or updates the given sku to the product. This method maintain the
	 * bidirectional relationships with <code>ProductSku</code>.
	 *
	 * @param productSku the sku to add or update
	 */
	public void addOrUpdateSku(final ProductSku productSku) {
		if (productSku != null) {
			this.getProductSkusInternal().put(productSku.getSkuCode(), productSku);
			((ListenableObject) productSku).addPropertyChangeListener(this, true);
			if (!this.equals(productSku.getProduct())) {
				productSku.setProduct(this);
			}
		}
	}

	/**
	 * Convenience method to get a product's display name given a locale.
	 * This method calls getLocaleDependantFields(locale).getDisplayName()
	 * and will fall back to the catalog's default locale if no fields are
	 * found for the given locale.
	 *
	 * @param locale the locale in which to return the display name
	 * @return the product's display name
	 * @deprecated This method must be replaced completely by getDisplayName(locale, fallback)
	 */
	@Deprecated
	public String getDisplayName(final Locale locale) {
		return this.getDisplayName(locale, true);
	}
	
	/**
	 * Retrieve a product's display name for the given locale, optionally
	 * falling back to the Product's Catalog's default locale if the name
	 * is not available in the given locale.
	 * 
	 * @param locale the locale in which to return the display name
	 * @param fallback true if should fall back to this Product's Master Catalog's
	 * default locale if the DisplayName is not available in the given locale
	 * @return the product's display name
	 */
	public String getDisplayName(final Locale locale, final boolean fallback) {
		String displayName = null;
		LocaleDependantFields ldf = this.getLocaleDependantFields(locale, fallback);
		if (ldf != null) {
			displayName = ldf.getDisplayName();
		}
		return displayName;
	}
	
	/**
	 * Returns the brand/manufacturer of the product.
	 *
	 * @return the brand/manufacturer of the product
	 */
	@ManyToOne(targetEntity = BrandImpl.class, cascade = {  CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "BRAND_UID")
	public Brand getBrand() {
		return brand;
	}

	/**
	 * Sets the brand/manufacturer of the product.
	 *
	 * @param brand the brand/manufacturer of the product
	 */
	public void setBrand(final Brand brand) {
		this.brand = brand;
	}

	/**
	 * Remove the given sku from the product.
	 *
	 * @param productSku the sku to remove
	 */
	public void removeSku(final ProductSku productSku) {
		this.getProductSkusInternal().remove(productSku.getSkuCode());
		if (productSku.getProduct() == this) {
			productSku.setProduct(null);
		}
	}

	@Override
	public ProductSku getSkuByGuid(final String guid) {
		return ProductSkus.skusFor(this).byGuid(guid);
	}
	
	/**
	 * Retrieve a SKU by its code.
	 *
	 * @param code the code of the SKU to retrieve
	 * @return the corresponding SKU, or null if not found
	 */
	public ProductSku getSkuByCode(final String code) {
		return getProductSkus().get(code);
	}
	
	/**
	 * Returns true if the product has multiple SKUs.
	 *
	 * @return true if the product has multiple SKUs.
	 */
	public boolean hasMultipleSkus() {
		if (this.getProductType() != null) {
			return this.getProductType().isWithMultipleSkus();
		}
		return false;
	}

	/**
	 * Get the product default image.
	 *
	 * @return the product default image
	 */
	@Basic
	@Column(name = "IMAGE")
	public String getImage() {
		return this.image;
	}

	/**
	 * Set the product default image.
	 *
	 * @param image the product default image
	 */
	public void setImage(final String image) {
		this.image = image;
	}

	/**
	 * Returns true if the product should not be displayed (e.g. in its category or as a search result).
	 *
	 * @return true if the product should not be displayed
	 */
	@Basic
	@Column(name = "HIDDEN")
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Set to true if the product should not be displayed.
	 *
	 * @param hidden true if the product should not be displayed
	 */
	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * Returns the total sales count of the product.
	 *
	 * @return the total sales count of the product
	 */
	@Basic
	@Column(name = "SALES_COUNT")
	public int getSalesCount() {
		return salesCount;
	}

	/**
	 * Sets the total sales count of the product.
	 *
	 * @param salesCount the total sales count of the product
	 */
	public void setSalesCount(final int salesCount) {
		this.salesCount = salesCount;
	}

	/**
	 * Get the attribute value group.
	 *
	 * @return the domain model's <code>AttributeValueGroup</code>
	 */
	@Transient
	public AttributeValueGroup getAttributeValueGroup() {
		if (this.attributeValueGroup == null) {
			initializeAttributeValueGroup();
		}
		return this.attributeValueGroup;
	}

	/**
	 * Set the attribute value group.
	 *
	 * @param attributeValueGroup the <code>AttributeValueGroup</code>
	 */
	public void setAttributeValueGroup(final AttributeValueGroup attributeValueGroup) {
		this.attributeValueGroup = attributeValueGroup;
		if (this.getAttributeValueGroup() != null) {
			this.setAttributeValueMap(attributeValueGroup.getAttributeValueMap());
		}
	}

	/**
	 * Returns the set of <code>Category</code>s containing this product.
	 *
	 * @return a set of <code>Category</code> objects (Not <code>ProductCategory</code>
	 */
	@Transient
	public Set<Category> getCategories() {
		final Set<Category> categories = new LinkedHashSet<Category>();
		for (ProductCategory productCategory : getProductCategories()) {
			categories.add(productCategory.getCategory());
		}
		return categories;
	}
	
	/**
	 * Returns the set of <code>Category</code>s from the given Catalog that contain
	 * this product.
	 * @param catalog the catalog in which the category should exist
	 * @return the set of Categories
	 */
	public Set<Category> getCategories(final Catalog catalog) {
		final Set<Category> categories = new LinkedHashSet<Category>();
		for (ProductCategory productCategory : getProductCategories()) {
			final Category category = productCategory.getCategory();
			if (category.getCatalog().getUidPk() == catalog.getUidPk()) {
				categories.add(category);
			}
		}
		return categories;
	}

	/**
	 * Sets the product's categories.
	 * Will add and remove from product categories according to new categories.
	 * This implementation calls removeCategory() and addCategory() and getDefaultCategory().
	 * @param newCategories new set of categories
	 * @throws EpDomainException if there is a default category and the set of new categories
	 * doesn't contain it.
	 */
	public void setCategories(final Set<Category> newCategories) {
		//fail fast - removeCategory() checks this as well.
		if (this.getDefaultCategory(this.getMasterCatalog()) != null && !newCategories.contains(this.getDefaultCategory(this.getMasterCatalog()))) {
			throw new EpDomainException("Cannot remove default: "
					+ this.getDefaultCategory(this.getMasterCatalog()).getCode()
					+ " from collection of categories in which this product exists.");
		}
		//Remove categories that are in the old list but not in the new list
		for (Category oldCategory : this.getCategories()) {
			if (!newCategories.contains(oldCategory)) {
				this.removeCategory(oldCategory);
			}
		}
		//Add categories that are in the new list but not in the old list
		for (Category newCategory : newCategories) {
			if (!this.getCategories().contains(newCategory)) {
				this.addCategory(newCategory);
			}
		}
	}
	
	/**
	 * Get the ProductCategory association object that contains the given Category.
	 *
	 * @param category the category to search for
	 * @return productCategory the <code>ProductCategory</code>
	 * @throws IllegalArgumentException if the product is not in the given category.
	 */
	protected ProductCategory getProductCategory(final Category category) {
		for (ProductCategory pc : this.getProductCategories()) {
			if (pc.getCategory().equals(category)) {
				return pc;
			}
		}
		throw new IllegalArgumentException("The product is not in the given category.");
	}

	/**
	 * Returns the product code.
	 *
	 * @return the product code
	 */
	@Basic(optional = false)
	@Column(name = "CODE", unique = true, nullable = false)
	public String getCode() {
		return this.code;
	}

	/**
	 * Sets the product code.
	 *
	 * @param code the product code
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Transient
	public String getGuid() {
		return this.getCode();
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.setCode(guid);
	}

	/**
	 * Returns a list of <code>AttributeValue</code>s with the given locale for all attributes of the product type which this product belonging
	 * to. If an attribute has a value, the value will be returned. Otherwise, a <code>null</code> value will be returned.
	 *
	 * @param locale the locale
	 * @return a list of <code>AttributeValue</code>s
	 * @see com.elasticpath.domain.attribute.AttributeValueGroup#getFullAttributeValues(AttributeGroup, Locale)
	 */
	public List<AttributeValue> getFullAttributeValues(final Locale locale) {
		final AttributeGroup attributeGroup = this.getProductType().getProductAttributeGroup();
		return this.getAttributeValueGroup().getFullAttributeValues(attributeGroup, locale);
	}

	/**
	 * Returns a list of <code>AttributeValue</code>s with the given locale for those attributes have values.
	 *
	 * @param locale the locale
	 * @return a list of <code>AttributeValue</code>s
	 * @see com.elasticpath.domain.attribute.AttributeValueGroup#getAttributeValues(AttributeGroup, Locale)
	 */
	public List<AttributeValue> getAttributeValues(final Locale locale) {
		final AttributeGroup attributeGroup = getProductType().getProductAttributeGroup();
		return getAttributeValueGroup().getAttributeValues(attributeGroup, locale);
	}

	/**
	 * Returns the <code>TaxCode</code> associated with this <code>Product</code>.
	 * @return the <code>TaxCode</code>
	 */
	@Transient
	public TaxCode getTaxCode() {
		if (this.getTaxCodePersistence() == null && this.getProductType() != null) {
			return this.getProductType().getTaxCode();
		}
		return this.getTaxCodePersistence();
	}

	/**
	 * Set the <code>TaxCode</code> associated with this <code>Product</code>.
	 * @param taxCode - the sales tax code for this product type, i.e. "Books".
	 */
	public void setTaxCode(final TaxCode taxCode) {
		this.setTaxCodePersistence(taxCode);
	}
	
	/**
	 * Returns the <code>TaxCode</code> associated with this <code>Product</code>.
	 * @return the <code>TaxCode</code>
	 */
	@ManyToOne(targetEntity = TaxCodeImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "TAX_CODE_UID")
	public TaxCode getTaxCodePersistence() {
		return this.taxCode;
	}

	/**
	 * Set the <code>taxCode</code> associated with this <code>Product</code>.
	 * @param taxCode - the sales tax code for this product type, i.e. "Books".
	 */
	public void setTaxCodePersistence(final TaxCode taxCode) {
		this.taxCode = taxCode;
	}

	/**
	 * Get the max featured product order from the productCateogiers of this product.
	 * @return the maximum featured product order.
	 */
	@Transient
	public int getMaxFeaturedProductOrder() {
		int maxFeaturedProductOrder = 0;
		for (ProductCategory currProductCategory : this.getProductCategories()) {
			if (currProductCategory.getFeaturedProductOrder() > maxFeaturedProductOrder) {
				maxFeaturedProductOrder = currProductCategory.getFeaturedProductOrder();
			}
		}
		return maxFeaturedProductOrder;
	}

	/**
	 * Get the minimum order quantity of the product.
	 * @return the minimum order quantity of the product.
	 */
	@Basic
	@Column(name = "MIN_QUANTITY")
	public int getMinOrderQty() {
		return this.minOrderQty;
	}

	/**
	 * Set the <code>MinOrderQty</code> associated with this <code>Product</code>.
	 *
	 * @param minOrderQty - the minimum order quantity of the product.
	 */
	public void setMinOrderQty(final int minOrderQty) {
		this.minOrderQty = minOrderQty;
	}
	/**
	 * Gets the map of LocalizedAttributeKey --> AttributeValue.
	 * @return a map of localized attribute key to attribute value
	 */
	@OneToMany(targetEntity = ProductAttributeValueImpl.class,
			   cascade = { CascadeType.ALL })
	@MapKey(name = "localizedAttributeKey")
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = false)
	@ElementDependent
	@ElementForeignKey(name = "TPRODUCTATTRIBUTEVALUE_IBFK_2")
	public Map<String, AttributeValue> getAttributeValueMap() {
		return attributeValueMap;
	}

	

	/**
	 * Set the attribute value map.
	 * @param attributeValueMap the map
	 */
	public void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap) {
		this.attributeValueMap = attributeValueMap;
	}

	/**
	 * Return the default <code>Category</code> of this product in the given <code>Catalog</code>.
	 * @param catalog the catalog for which you want the default category
	 * @return the default <code>Category</code>
	 */
	public Category getDefaultCategory(final Catalog catalog) {
		for (ProductCategory productCategory : this.getProductCategories()) {
			if (productCategory != null && catalog.equals(productCategory.getCategory().getCatalog()) && productCategory.isDefaultCategory()) {
				return productCategory.getCategory();
			}
		}
		return null;
	}

	/**
	 * Convenience method to get the default category in the product's
	 * master catalog.
	 * This implementation calls getMasterCatalog() to get the master catalog
	 * and then calls getDefaultCategory(masterCatalog).
	 * @return the primary category in which this Product lives in its Master catalog
	 */
	@Transient
	protected Category getDefaultCategoryInMasterCatalog() {
		return this.getDefaultCategory(this.getMasterCatalog());
	}
	
	/**
	 * Set the given category to be a default (primary) category of this product.
	 * If the category is not already attached to this product, it will be added.
	 * This method will ensure that the given category is the primary category for
	 * a product in the category's catalog, since a category only has one catalog.
	 * 
	 * @param category the category to be set as default category in its catalog
	 */
	public void setCategoryAsDefault(final Category category) {
		this.addCategory(category);
		for (ProductCategory productCategory : this.getProductCategories()) {
			if (category.getCatalog().equals(productCategory.getCategory().getCatalog())) {
				if (category.equals(productCategory.getCategory())) {
					productCategory.setDefaultCategory(true);
				} else {
					productCategory.setDefaultCategory(false);
				}
			}
		}
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS",
					pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
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

	@Override
	@Transient
	public AvailabilityCriteria getAvailabilityCriteria() {
		return getAvailabilityCriteriaInternal();
	}


	@Override
	public void setAvailabilityCriteria(final AvailabilityCriteria availabilityCriteria) {
		setAvailabilityCriteriaInternal(availabilityCriteria);
	}
		
	@Basic
	@Enumerated(EnumType.STRING)
	@Column(name = "AVAILABILITY_CRITERIA")
	private AvailabilityCriteria getAvailabilityCriteriaInternal() {
		if (availabilityCriteria == null) {
			availabilityCriteria = AvailabilityCriteria.ALWAYS_AVAILABLE;
		}
		return availabilityCriteria;
	}

	private void setAvailabilityCriteriaInternal(final AvailabilityCriteria availabilityCriteria) {
		this.availabilityCriteria = availabilityCriteria;
	}

	@Override
	@Transient
	public int getPreOrBackOrderLimit() {
		return getPreOrBackOrderLimitInternal();
	}

	@Override
	public void setPreOrBackOrderLimit(final int orderLimit) {
		setPreOrBackOrderLimitInternal(orderLimit);
	}

	@Basic
	@Column(name = "PRE_OR_BACK_ORDER_LIMIT")
	private int getPreOrBackOrderLimitInternal() {
		return preOrBackOrderLimit;
	}
		
	private void setPreOrBackOrderLimitInternal(final int orderLimit) {
		this.preOrBackOrderLimit = orderLimit;
	}

	@Override
	@Transient
	public Date getExpectedReleaseDate() {
		return getItemExpectedReleaseDate();
	}
	
	/**
	 * Returns the expectedReleaseDate for this item only without regard for any bundle constituents.
	 * @return the expected release date
	 */
	@Basic
	@Column(name = "EXPECTED_RELEASE_DATE")
	protected Date getItemExpectedReleaseDate() {
		return expectedReleaseDate;
	}

	@Override
	public void setExpectedReleaseDate(final Date expectedReleaseDate) {
		setItemExpectedReleaseDate(expectedReleaseDate);
	}

	protected void setItemExpectedReleaseDate(final Date expectedReleaseDate) {
		this.expectedReleaseDate = expectedReleaseDate;
	}

	@Override
	@Transient
	public boolean isInCatalog(final Catalog catalog) {
		return isInCatalog(catalog, false);
	}

	@Override
	@Transient
	public boolean isInCatalog(final Catalog catalog, final boolean checkForLinkedCategories) {
		for (ProductCategory productCategory : getProductCategories()) {
			Category category = productCategory.getCategory();
			
			if (category.getCatalog().getUidPk() == catalog.getUidPk()
					&& (!checkForLinkedCategories || !category.isLinked() || category.isIncluded())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Find the ProductSku within this Product that is defined by the given set of
	 * OptionValueKeys. e.g. "SMALL, GREEN". If no such Sku is found, returns null.
	 * @param optionValueKeysToFind the set of option value codes to search for
	 * @return the productSku that was found, or null if one was not found
	 */
	public ProductSku findSkuWithOptionValueCodes(final Collection<String> optionValueKeysToFind) {
		if (optionValueKeysToFind == null || optionValueKeysToFind.isEmpty()) {
			return null;
		}
		for (ProductSku productSku : this.getProductSkus().values()) {
			Collection<String> skuOptionValueKeys = productSku.getOptionValueKeys();
			if ((skuOptionValueKeys != null)
					&& (optionValueKeysToFind.size() == skuOptionValueKeys.size())
					&& (skuOptionValueKeys.containsAll(optionValueKeysToFind))) {
				return productSku;
			}
		}
		return null;
	}
	
	/**
	 * Get the catalog set of the product.
	 * @return the set of catalog
	 */
	@Transient
	public Set<Catalog> getCatalogs() {
		Set<Catalog> catalogs = new HashSet<Catalog>();
		
		for (ProductCategory productCategory : getProductCategories()) {
			catalogs.add(productCategory.getCategory().getCatalog());
		}
		return catalogs;
	}

	/**
	 * Sets the display name of this product.
	 * @param name the name
	 * @param locale the locale it is valid for
	 */
	public void setDisplayName(final String name, final Locale locale) {
		LocaleDependantFields localeDependantFields = getLocaleDependantFieldsWithoutFallBack(locale);
		localeDependantFields.setDisplayName(name);
		addOrUpdateLocaleDependantFields(localeDependantFields);
	}

	

	/**
	 * Callback method for property changes on any objects that are being listened to.
	 * 
	 * @param event the property change event
	 */
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equals("skuCode")) {
			final ProductSku productSku = getProductSkusInternal().remove(event.getOldValue());
			if (productSku != null) { // the old value does not exist in the sku list so skip adding the new one
				getProductSkusInternal().put((String) event.getNewValue(), productSku);
			}
		}
	}

	/**
	 * Sets the preferred rank of this product when it's in a collection of
	 * featured products in the given category.
	 * @param category the category in which the product may be featured. <b>This must
	 * be a Category that contains this Product</b>
	 * @param rank the preferred rank of this product in a collection of
	 * featured products.
	 * @throws IllegalArgumentException if the product doesn't exist in the given category.
	 */
	public void setFeaturedRank(final Category category, final int rank) {
		this.getProductCategory(category).setFeaturedProductOrder(rank);
	}

	/**
	 * Gets the preferred rank of this product when it's in a collection of
	 * featured products in the given category.
	 * @param category the category in which the product may be featured. <b>This must
	 * be a Category that contains this Product</b>
	 * @return the preferred rank of this product when featured in the given category
	 * @throws IllegalArgumentException if the product doesn't exist in the given category.
	 */
	public int getFeaturedRank(final Category category) {
		return this.getProductCategory(category).getFeaturedProductOrder();
	}

	@Override
	@Basic
	@Column(name = "NOT_SOLD_SEPARATELY")
	public boolean isNotSoldSeparately() {
		return notSoldSeparately;
	}
	
	/**
	 * @param nss true if product is not to be sold outside of bundles
	 */
	public void setNotSoldSeparately(final boolean nss) {
		this.notSoldSeparately = nss;
	}

	@Override

	public void validateRequiredAttributes(final Set<Locale> allLocales) {
		// validation for attribute 
		final AttributeValueGroup attributeValueGroup = getAttributeValueGroup();
		//reload the product type, because it might have been changed
		final Set<String> attributesThatAreMissingValues = new HashSet<String>();
		
		for (AttributeGroupAttribute attrGroup : getProductType().getProductAttributeGroupAttributes()) {

			//if attribute is not required go to the next one
			if (!attrGroup.getAttribute().isRequired()) {	
				continue;
			}

			if (attrGroup.getAttribute().isLocaleDependant()) {
				for (Locale locale : allLocales) {
					checkIfAttributeValueExistsForLocale(attributeValueGroup, attrGroup, locale, attributesThatAreMissingValues);
				}
			} else {
				checkIfAttributeValueExistsForLocale(attributeValueGroup, attrGroup, null, attributesThatAreMissingValues);
			}
		}
		if (!attributesThatAreMissingValues.isEmpty()) {
			throw new AttributeValueIsRequiredException(attributesThatAreMissingValues);
		}
	}


	private void checkIfAttributeValueExistsForLocale(final AttributeValueGroup attributeValueGroup, final AttributeGroupAttribute attrGroup,
			final Locale locale, final Set<String> attributesThatAreMissingValues) {
		final AttributeValue attrValue = attributeValueGroup.getAttributeValue(attrGroup.getAttribute().getKey(), locale);
		if (attrValue == null || StringUtils.isEmpty(attrValue.getStringValue())) {
			// update the type into the product in case of any missing attributes, to be able to use the updated type
			if (locale == null) {
				attributesThatAreMissingValues.add(attrGroup.getAttribute().getName());
			} else {
				attributesThatAreMissingValues.add(attrGroup.getAttribute().getName() + " - " + locale.getDisplayName());
			}
		}
	}

	@Override
	public LocaleDependantFields getLocaleDependantFields(final LocaleFallbackPolicy policy) {
		LocaleDependantFields ldf = null;
		for (Locale locale : policy.getLocales()) {
			ldf = getLocaleDependantFieldsMap().get(locale);
			if (ldf != null) {
				break;
			}
		}
		// create a new LDF
		if (ldf == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("LocaleDependantFields not available for Master Catalog for product " + this.getCode()
						+ " : Creating empty one");
			}
			ldf = createLocaleDependantFields(policy.getPrimaryLocale());
			ldf.setDisplayName(this.getCode());
		}
		return ldf;		
	}

}

