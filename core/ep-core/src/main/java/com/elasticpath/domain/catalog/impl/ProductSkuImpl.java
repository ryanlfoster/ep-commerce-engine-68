/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.ListenableObject;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.AttributeValueGroupFactory;
import com.elasticpath.domain.attribute.impl.AttributeValueGroupFactoryImpl;
import com.elasticpath.domain.attribute.impl.SkuAttributeValueFactoryImpl;
import com.elasticpath.domain.attribute.impl.SkuAttributeValueImpl;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.impl.AbstractListenableEntityImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.JpaAdaptorOfSkuOptionValueImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * <code>ProductSku</code> represents a variation of a merchandise product in Elastic Path. A <code>ProductSku</code> corresponds to a single
 */
@SuppressWarnings("PMD.ExcessiveImports")
@Entity
@Table(name = ProductSkuImpl.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = "SKUCODE"))
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = { @FetchAttribute(name = "skuCodeInternal"),
		@FetchAttribute(name = "productInternal"),
		@FetchAttribute(name = "attributeValueMap"),
		@FetchAttribute(name = "optionValueMap"),
		@FetchAttribute(name = "startDate"),
		@FetchAttribute(name = "endDate")
		}),
	@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, attributes = { 
			@FetchAttribute(name = "optionValueMap"), 
			@FetchAttribute(name = "productInternal") })
})
public class ProductSkuImpl extends AbstractListenableEntityImpl implements ProductSku, ListenableObject, DatabaseLastModifiedDate {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TPRODUCTSKU";

	private Date startDate;

	private Date endDate;

	private String skuCode;

	private Product product;

	private AttributeValueGroup attributeValueGroup;

	private Map<String, SkuOptionValue> optionValueMap = new HashMap<String, SkuOptionValue>();

	private String image;

	private boolean digitalProduct = false;
	
	private DigitalAsset digitalAsset;

	private boolean shippable = true;

	private Map<String, AttributeValue> attributeValueMap;

	private BigDecimal height;

	private BigDecimal width;

	private BigDecimal length;

	private BigDecimal weight;	

	private long uidPk;

	private int preOrBackOrderedQuantity;

	private Date lastModifiedDate;

	private String guid;
	
	private static final String NOT_NEGATIVE_CONSTRAINT = "field can not have negative value";

	/**
	 * Default constructor.
	 */
	public ProductSkuImpl() {
		super();
	}

	/**
	 * Get the start date that this product will become available to customers.
	 * 
	 * @return the start date and time
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
	 * @return the end date and time
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * Set the end date.
	 * 
	 * @param endDate the end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Get the product system name.
	 * 
	 * @return the product SKU
	 */
	@Transient
	public String getSkuCode() {
		return getSkuCodeInternal();
	}

	/**
	 * Set the SKU for this product variation. This triggers a property change event since other objects may need to know about a sku code change. In
	 * particular, the sku's product needs to update its HashMap of skuCodes to productSkus.
	 * 
	 * @param skuCode the SKU
	 */
	public void setSkuCode(final String skuCode) {
		final String oldSkuCode = getSkuCodeInternal();
		setSkuCodeInternal(skuCode);
		firePropertyChange("skuCode", oldSkuCode, skuCode);
	}

	/**
	 * Get the product system name.
	 * 
	 * @return the product SKU
	 */
	@Basic
	@Column(name = "SKUCODE")
	protected String getSkuCodeInternal() {
		return skuCode;
	}

	/**
	 * Set the SKU for this product variation.
	 * 
	 * @param skuCode the SKU
	 */
	protected void setSkuCodeInternal(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		super.initialize();

		if (getStartDate() == null) {
			setStartDate(new Date());
		}

		if (getAttributeValueMap() == null) {
			setAttributeValueMap(new HashMap<String, AttributeValue>());
		}

		if (getAttributeValueGroup() == null) {
			initializeAttributeValueGroup();
		}

		if (getOptionValueMap() == null) {
			setOptionValueMap(new HashMap<String, SkuOptionValue>());
		}
	}

	/**
	 * Sets up and initializes the {@link AttributeValueGroup}.
	 */
	protected void initializeAttributeValueGroup() {
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
		return new AttributeValueGroupFactoryImpl(new SkuAttributeValueFactoryImpl());
	}

	/**
	 * Get the parent product corresponding to this SKU.
	 * 
	 * @return the parent <code>Product</code>
	 */
	@Transient
	public Product getProduct() {
		return getProductInternal();
	}

	/**
	 * Set the parent product of this SKU.
	 * This method maintains the bidirectional relationships between product and skus 
	 * so it cannot be used by the persistence layer to load an instance from the database. 
	 * The persistence layer should use setProductInternal instead.
	 * 
	 * @param newProduct the parent product
	 */
	public void setProduct(final Product newProduct) {
		// this.product = newProduct;

		/**
		 * This method maintain the bidirectional relationships between product and skus. So it cannot be used by the persistence layer to load an
		 * instance from the database. The persistence layer should use setProductInternal instead.
		 */
		if (newProduct == null) {
			setProductInternal(newProduct);
			return;
		}

		if (newProduct.equals(getProductInternal())) {
			return;
		}

		final Product oldProduct = this.getProduct();
		setProductInternal(newProduct);

		if (oldProduct != null) {
			oldProduct.removeSku(this);
		}

		if (!getProductInternal().getProductSkus().containsValue(this)) {
			getProductInternal().addOrUpdateSku(this);
		}

	}

	/**
	 * Set the parent product of this SKU. 
	 * This method doesn't maintain the bidirectional relationship between the given product and the
	 * sku. It won't add this sku to the given product's sku collection. This method is for the
	 * sole purpose of JPA.
	 * 
	 * @param product the parent product
	 */
	protected void setProductInternal(final Product product) {
		this.product = product;
	}

	/**
	 * Get the parent product of this SKU.
	 * 
	 * This is annotated as ReadOnly for JPA to avoid persistence issues with the bidirectional relationship.
	 * JPA will take care of cascading the save or update down through the other side (Product)
	 * 
	 * @return the parent product
	 */
	@ManyToOne(targetEntity = ProductImpl.class, fetch = FetchType.EAGER, 
			cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "PRODUCT_UID", nullable = false)
	@ForeignKey
	protected Product getProductInternal() {
		return this.product;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof ProductSkuImpl)) {
			return false;
		}
		
		ProductSkuImpl sku = (ProductSkuImpl) other;
		return ObjectUtils.equals(skuCode, sku.skuCode);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(skuCode);
	}

	/**
	 * Initialize the full attribute set based on the product variation type.
	 */
	protected void initializeFullAttributeValues() {
		// TODO: design and implement
		throw new EpDomainException("Not implemented yet.");
	}

	/**
	 * Get the available values for this SKU option.
	 * (e.g. the values for blue, 40 inches)
	 * 
	 * @return an ordered collection of <code>SkuOptionValue</code>s. 
	 */
	@Transient
	public Collection<SkuOptionValue> getOptionValues() {
		//Ordered collection of sku option values that is ordered based on the ascending order of the keys of OptionValueMap. 
		return new TreeMap<String, SkuOptionValue>(this.getOptionValueMap()).values();
	}

	/**
	 * Get the option value codes for this SKU (e.g. "Color", "Size", etc).
	 * 
	 * @return a set of strings of the option value codes
	 */
	@Transient
	public Set<String> getOptionValueCodes() {
		return this.getOptionValueMap().keySet();
	}
	
	/**
	 * Get the set of SkuOptionValue keys for this ProductSku's SkuOptionValues.
	 * (e.g. if the SkuOptionValues for this ProductSku are for Color=Red,
	 * Size=Large then the keys might be "RED" and "L").
	 * 
	 * This implementation calls getOptionValues().
	 * 
	 * @return the set of option value keys for this ProductSku's SkuOptionValues.
	 */
	@Transient
	public Set<String> getOptionValueKeys() {
		Set<String> keys = new HashSet<String>();
		for (SkuOptionValue skuOptionValue : this.getOptionValues()) {
			keys.add(skuOptionValue.getOptionValueKey());
		}
		return keys;
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
	}

	/**
	 * Gets the available configuration option values for this SKU,
	 * mapped by the option key (e.g. "Color" to a SkuOptionValue for "Red") 
	 * 
	 * @return a map of <code>SkuOptionValue</code>s
	 */
	@OneToMany(targetEntity = JpaAdaptorOfSkuOptionValueImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "optionKey")
	@ElementJoinColumn(name = "PRODUCT_SKU_UID", nullable = false)
	@ElementForeignKey(name = "TPRODUCTSKUOPTIONVALUE_IBFK_1")
	public Map<String, SkuOptionValue> getOptionValueMap() {
		return this.optionValueMap;
	}

	/**
	 * Sets the available configuration option values for this SKU.
	 * 
	 * @param optionValueMap the map of <code>SkuOptionValue</code>s.
	 */
	public void setOptionValueMap(final Map<String, SkuOptionValue> optionValueMap) {
		this.optionValueMap = optionValueMap;
	}

	/**
	 * Sets the sku option value to the one corresponding given value code.
	 * 
	 * @param skuOption the sku option
	 * @param valueCode the sku option value code
	 * @throws EpInvalidValueBindException in case the given value code is not defined in the given <code>SkuOption</code>
	 */
	public void setSkuOptionValue(final SkuOption skuOption, final String valueCode) throws EpInvalidValueBindException {
		if (!skuOption.contains(valueCode)) {
			throw new EpInvalidValueBindException("The given value code is not defined.");
		}
		if (getOptionValueMap().containsKey(skuOption.getOptionKey())) {
			JpaAdaptorOfSkuOptionValueImpl adaptor = (JpaAdaptorOfSkuOptionValueImpl) getOptionValueMap().get(skuOption.getOptionKey());
			adaptor.setSkuOptionValue(skuOption.getOptionValue(valueCode));
		} else {
			JpaAdaptorOfSkuOptionValueImpl adaptor = getBean(
					ContextIdNames.SKU_OPTION_VALUE_JPA_ADAPTOR);
			adaptor.setOptionKey(skuOption.getOptionKey());
			adaptor.setSkuOptionValue(skuOption.getOptionValue(valueCode));
	
			this.getOptionValueMap().put(skuOption.getOptionKey(), adaptor);
		}
	}

	/**
	 * Returns the value of the given <code>SkuOption</code>. Returns <code>null</code> if the value is not defined.
	 * 
	 * @param skuOption the sku option
	 * @return the value of the given <code>SkuOption</code>. <code>null</code> if the value is not defined.
	 */
	public SkuOptionValue getSkuOptionValue(final SkuOption skuOption) {
		return this.getOptionValueMap().get(skuOption.getOptionKey());
	}

	/**
	 * Get the sku default image. Returns the product default image if no sku image exists
	 * 
	 * @return the sku default image
	 */
	@Transient
	public String getImage() {
		if (((getImageInternal() == null) || (getImageInternal().length() <= 0)) && getProduct() != null) {
			return getProduct().getImage();
		}
		return getImageInternal();
	}

	/**
	 * Set the sku default image.
	 * 
	 * @param image the sku default image
	 */
	public void setImage(final String image) {
		setImageInternal(image);
	}

	/**
	 * Get the sku default image. Returns the product default image if no sku image exists
	 * 
	 * @return the sku default image
	 */
	@Basic
	@Column(name = "IMAGE")
	private String getImageInternal() {
		return this.image;
	}

	/**
	 * Set the sku default image.
	 * 
	 * @param image the sku default image
	 */
	private void setImageInternal(final String image) {
		this.image = image;
	}

	@Override
	@Basic
	@Column(name = "DIGITAL", nullable = false)
	public boolean isDigital() {
		return digitalProduct;
	}

	@Override
	public void setDigital(final boolean digitalProduct) {
		this.digitalProduct = digitalProduct;
	}
	
	@Override
	@Transient	
	public boolean isDownloadable() {
		return digitalAsset != null;		
	}
	
	
	/**
	 * Gets the digital asset belong to this product SKU.
	 * 
	 * @return the digital asset belong to this product SKU
	 */
	@Transient
	public DigitalAsset getDigitalAsset() {
		if (isDownloadable()) {
			return getDigitalAssetInternal();			
		}
		return null;
	}
	
	/**
	 * Sets the digital asset.
	 * 
	 * @param digitalAsset the digital asset
	 */
	public void setDigitalAsset(final DigitalAsset digitalAsset) {
		setDigitalAssetInternal(digitalAsset);		
	}
	
	/**
	 * Gets the digital asset belong to this product SKU.
	 * 
	 * @return the digital asset belong to this product SKU
	 */
	@ManyToOne(targetEntity = DigitalAssetImpl.class, cascade = { CascadeType.ALL })
	@JoinColumn(name = "DIGITAL_ASSET_UID")
	@ForeignKey(name = "TPRODUCTSKU_IBFK_3")
	protected DigitalAsset getDigitalAssetInternal() {
		return this.digitalAsset;
	}
	
	/**
	 * Sets the digital asset.
	 * 
	 * @param digitalAsset the digital asset
	 */
	protected void setDigitalAssetInternal(final DigitalAsset digitalAsset) {
		this.digitalAsset = digitalAsset;
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
	 * True if this SKU is shippable (i.e. is a physical good which requires shipping).
	 * 
	 * @return true if this SKU is shippable
	 */
	@Basic
	@Column(name = "SHIPPABLE")
	public boolean isShippable() {
		return this.shippable;
	}

	/**
	 * Sets if this SKU is shippable (i.e. is a physical good which requires shipping).
	 * 
	 * @param shippable the shippable flag for the SKU
	 */
	public void setShippable(final boolean shippable) {
		this.shippable = shippable;
	}

	/**
	 * Get the attribute value map.
	 * 
	 * @return the map
	 */
	@OneToMany(targetEntity = SkuAttributeValueImpl.class, cascade = { CascadeType.ALL })
	@MapKey(name = "localizedAttributeKey")
	@ElementJoinColumn(name = "PRODUCT_SKU_UID", nullable = false)
	@ElementDependent
	@ElementForeignKey(name = "TPRODUCTSKUATTRIBUTEVALUE_IBFK_1")
	public Map<String, AttributeValue> getAttributeValueMap() {
		return attributeValueMap;
	}

	/**
	 * Set the attribute value map.
	 * 
	 * @param attributeValueMap the map
	 */
	public void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap) {
		this.attributeValueMap = attributeValueMap;
	}

	/**
	 * Returns the height.
	 * 
	 * @return the height.
	 */
	@Basic
	@Column(name = "HEIGHT")
	public BigDecimal getHeight() {
		return this.height;
	}

	/**
	 * Sets the height.
	 * 
	 * @param height the height to set.
	 */
	public void setHeight(final BigDecimal height) {
		if (height != null && BigDecimal.ZERO.compareTo(height) > 0) {
			throw new EpDomainException("height " + NOT_NEGATIVE_CONSTRAINT); 
		}
		this.height = height;
	}

	/**
	 * Returns the width.
	 * 
	 * @return the width.
	 */
	@Basic
	@Column(name = "WIDTH")
	public BigDecimal getWidth() {
		return this.width;
	}

	/**
	 * Sets the width.
	 * 
	 * @param width the width to set.
	 */
	public void setWidth(final BigDecimal width) {
		if (width != null && BigDecimal.ZERO.compareTo(width) > 0) {
			throw new EpDomainException("width " + NOT_NEGATIVE_CONSTRAINT); 
		}
		this.width = width;
	}

	/**
	 * Returns the length.
	 * 
	 * @return the length.
	 */
	@Basic
	@Column(name = "LENGTH")
	public BigDecimal getLength() {
		return this.length;
	}

	/**
	 * Sets the length.
	 * 
	 * @param length the length to set.
	 */
	public void setLength(final BigDecimal length) {
		if (length != null && BigDecimal.ZERO.compareTo(length) > 0) {
			throw new EpDomainException("length " + NOT_NEGATIVE_CONSTRAINT); 
		}
		this.length = length;
	}

	/**
	 * Returns the weight.
	 * 
	 * @return the weight.
	 */
	@Basic
	@Column(name = "WEIGHT")
	public BigDecimal getWeight() {
		return this.weight;
	}

	/**
	 * Sets the weight.
	 * 
	 * @param weight the weight to set.
	 */
	public void setWeight(final BigDecimal weight) {
		if (weight != null && BigDecimal.ZERO.compareTo(weight) > 0) {
			throw new EpDomainException("weight " + NOT_NEGATIVE_CONSTRAINT); 
		}
		this.weight = weight;
	}

	/**
	 * Gets the display name.
	 * 
	 * @param locale the Locale
	 * @return String
	 */
	public String getDisplayName(final Locale locale) {
		
		final Iterator<SkuOptionValue> valuesIterator = getOptionValues().iterator();
		final StringBuilder skuConfigurationCode = new StringBuilder();
		for (int i = 0; valuesIterator.hasNext(); i++) {
			if (i >= 1) {
				skuConfigurationCode.append(", ");
			}
			final SkuOptionValue skuOptionValue = valuesIterator.next();
			if (skuOptionValue != null) {
				skuConfigurationCode.append(skuOptionValue.getDisplayName(locale, true));
			}
		}
		return skuConfigurationCode.toString();
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
		return this.uidPk;
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
	public List<AttributeValue> getFullAttributeValues(final Locale locale) {
		final AttributeGroup attributeGroup = this.getProduct().getProductType().getSkuAttributeGroup();
		return this.getAttributeValueGroup().getFullAttributeValues(attributeGroup, locale);
	}

	@Override
	@Transient	
	public boolean isWithinDateRange(final Date currentDate) {
		Date startDate = this.getStartDate();
		Date endDate = this.getEndDate();
		if (startDate != null && currentDate.getTime() < startDate.getTime()) {
			return false;
		} else if (endDate != null && currentDate.getTime() > endDate.getTime()) {
			return false;
		}		
		return true;
	}

	/**
	 * Returns true if the current date is within the start and end dates for this product SKU.
	 *
	 * @return true if the current date is within the start and end dates for this product SKU.
	 */
	@Transient	
	public boolean isWithinDateRange() {
		return isWithinDateRange(new Date());
	}

	/**
	 * Update a sku option to the parent product type.
	 * 
	 * @param skuOption the sku option to add/update
	 */
	public void addOrUpdateSkuOption(final SkuOption skuOption) {
		ProductType productType = getProduct().getProductType();
		productType.addOrUpdateSkuOption(skuOption);
	}

	/**
	 * Returns the pre or back ordered quantity.
	 * 
	 * @return int
	 */
	@Basic
	@Column(name = "PRE_OR_BACK_ORDERED_QUANTITY")
	public int getPreOrBackOrderedQuantity() {
		return preOrBackOrderedQuantity;
	}
	
	/**
	 * Sets the pre or back ordered quantity.
	 * 
	 * @param orderedQuantity the ordered quantity
	 */
	public void setPreOrBackOrderedQuantity(final int orderedQuantity) {
		this.preOrBackOrderedQuantity = orderedQuantity;
	}
	
	@Override
	@Basic(optional = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	@Override
	@Transient
	public Date getEffectiveStartDate() {
		return (Date) ObjectUtils.max(getStartDate(), getProduct().getStartDate());
	}
	
	@Override
	@Transient
	public Date getEffectiveEndDate() {
		Date effectiveEndDate = (Date) ObjectUtils.min(getEndDate(), getProduct().getEndDate());
		if (effectiveEndDate != null && effectiveEndDate.compareTo(getEffectiveStartDate()) < 0) {
			effectiveEndDate = getEffectiveStartDate();
		}
		return effectiveEndDate;
	}
}
