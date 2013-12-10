/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openjpa.persistence.jdbc.ForeignKeyAction;

import com.elasticpath.commons.util.security.StringEncrypter;
import com.elasticpath.domain.RecalculableObject;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.DigitalAssetImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.impl.AbstractItemData;
import com.elasticpath.domain.impl.AbstractShoppingItemImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.shoppingcart.impl.AbstractPriceCalculatorImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemRecurringPriceImpl;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Represents a quantity of SKUs in an Order.<br/>
 *
 * NOTE that the presence of the {@code DatabaseLastModifiedDate} means that whenever this object is saved or updated to the database
 * the lastModifiedDate will be set by the {@code LastModifiedPersistenceEngineImpl} if that class in configured in Spring.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessiveClassLength", "PMD.ExcessiveImports" })
@Entity
@Table(name = OrderSkuImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(
			name = FetchGroupConstants.ORDER_DEFAULT,
			attributes = {
					@FetchAttribute(name = "childOrderSkus", recursionDepth = -1),
					@FetchAttribute(name = "parent", recursionDepth = -1),
					@FetchAttribute(name = "displayName"),
					@FetchAttribute(name = "displaySkuOptions"),
					@FetchAttribute(name = "image"),
					@FetchAttribute(name = "productSkuInternal"),
					@FetchAttribute(name = "skuCode"),
					@FetchAttribute(name = "allocatedQuantityInternal"),
					@FetchAttribute(name = "recurringPrices"),
					@FetchAttribute(name = "unitPriceInternal")
			},
			postLoad = true
	),
	@FetchGroup(name = FetchGroupConstants.ORDER_INDEX, attributes = {
		@FetchAttribute(name = "shipment"),
		@FetchAttribute(name = "skuCode"),
		@FetchAttribute(name = "recurringPrices")
	}),
	@FetchGroup(name = FetchGroupConstants.ORDER_SEARCH, attributes = {
			@FetchAttribute(name = "skuCode"),
			@FetchAttribute(name =  "displayName"),
			@FetchAttribute(name = "recurringPrices")
	})
})

@DataCache(enabled = false)
public class OrderSkuImpl extends AbstractShoppingItemImpl implements OrderSku, RecalculableObject {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 7000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDERSKU";

	private static final String FK_COLUMN_NAME = "ORDERSKU_UID";

	private static final int DEFAULT_NUM_FRACTIONAL_DIGITS = 2;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private OrderShipment shipment;

	private Date createdDate;

	private CmUser lastModifiedBy;

	private String skuCode;

	private int returnableQuantity;

	private BigDecimal amount;

	private BigDecimal unitPrice;

	private String displayName;

	private String displaySkuOptions;

	private String image;

	private int weight;

	private String encryptedUidPk;

	private DigitalAsset digitalAsset;

	private String taxCode;

	private int allocatedQuantity;

	private long uidPk;

	private boolean recalculationEnabled = false;

	private int changedQuantityAllocated = 0;

	private int preOrBackOrderQuantity = 0;

	private Map<String, AbstractItemData> fieldValues = new HashMap<String, AbstractItemData>();

	private ProductSku productSku;

	private List<OrderSku> childOrderSkus = new ArrayList<OrderSku>();

	private OrderSku parent = null;

	private Set<ShoppingItemRecurringPrice> recurringPrices = new HashSet<ShoppingItemRecurringPrice>();

	/**
	 * Internal accessor used by JPA.
	 *
	 * @return the set of dependent order skus.
	 */
	@OneToMany(targetEntity = OrderSkuImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "parent")
	@OrderBy("ordering")
	@ElementDependent
	protected List<OrderSku> getChildOrderSkus() {
		return this.childOrderSkus;
	}

	/**
	 * Internal accessor used by JPA.
	 *
	 * @param orderSkus the set of dependent order skus.
	 */
	protected void setChildOrderSkus(final List<OrderSku> orderSkus) {
		this.childOrderSkus = orderSkus;
	}

	/**
	 * @return the list of order skus as shopping items
	 */
	@Transient
	@Override
	protected List<ShoppingItem> getChildItemsInternal() {
		List<ShoppingItem> items = new ArrayList<ShoppingItem>();
		for (OrderSku orderSku : getChildOrderSkus()) {
			items.add(orderSku);
		}
		return items;
	}

	/**
	 *
	 * @param childItems the set of dependent cart items.
	 */
	@Transient
	@Override
	protected void setChildItemsInternal(final List<ShoppingItem> childItems) {
		this.getChildOrderSkus().clear();
		if (childItems != null) {
			for (ShoppingItem item : childItems) {
				OrderSkuImpl orderSku = (OrderSkuImpl) item;
				orderSku.setParent(this);
				getChildOrderSkus().add(orderSku);
			}
		}
	}

	@Override
	public void addChildItem(final ShoppingItem newChildItem) {
		OrderSkuImpl orderSku = (OrderSkuImpl) newChildItem;
		orderSku.setParent(this);
		getChildOrderSkus().add(orderSku);
	}

	@Override
	public void removeChildItem(final ShoppingItem childItem) {
		getChildOrderSkus().remove(childItem);
	}

	@Transient
	@Override
	public List<ShoppingItem> getBundleItems() {
		return Collections.unmodifiableList(getChildItemsInternal());
	}

	@Transient
	@Override
	public void setBundleItems(final List<ShoppingItem> bundleItems) {
		setChildItemsInternal(bundleItems);
	}

	/**
	 * Sets the {@code OrderItemData} - for JPA.
	 * @param itemData the cart item data
	 */
	protected void setItemData(final Map<String, AbstractItemData> itemData) {
		this.fieldValues = itemData;
	}

	/**
	 * Assigns {@code value} to {@code name}. Any previous value is replaced.
	 *
	 * @param name The name of the field to assign.
	 * @param value The value to assign to the field.
	 */
	public void setFieldValue(final String name, final String value) {
		OrderItemData data = new OrderItemData(name, value);
		getItemData().put(name, data);
	}

	/**
	 * Internal JPA method to get Item Data.
	 * @return the item data
	 */
	@Override
	@OneToMany(targetEntity = OrderItemData.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "key")
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = false)
	@ElementForeignKey
	@ElementDependent
	protected Map<String, AbstractItemData> getItemData() {
		return this.fieldValues;
	}

	@Transient
	@Override
	public Map<String, String> getFields() {
		Map<String, String> fields = new HashMap<String, String>();
		for (String key : getItemData().keySet()) {
			fields.put(key, getItemData().get(key).getValue());
		}
		return Collections.unmodifiableMap(fields);
	}

	/**
	 * Get the date that this order was created on.
	 *
	 * @return the created date
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		if (this.createdDate == null) {
			createdDate = new Date();
		}
		return this.createdDate;
	}

	/**
	 * Set the date that the order is created.
	 *
	 * @param createdDate the start date
	 */
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Get the CM user who last modifed this order sku.
	 *
	 * @return the CM user
	 */
	@Transient
	public CmUser getLastModifiedBy() {
		return this.lastModifiedBy;
	}

	/**
	 * Set the CM User who last modified this order sku.
	 *
	 * @param modifiedBy the CM user
	 */
	public void setLastModifiedBy(final CmUser modifiedBy) {
		this.lastModifiedBy = modifiedBy;
	}

	/**
	 * {@inheritDoc}
	 * FIXME: Ensure that the product sku isn't for a different product than the
	 * PRODUCT that's set on this OrderSKU. Actually, the Product/ProductUID
	 * should not be set/saved on the OrderSku - that setter should be only on the CartItem interface (when one exists)
	 */
	@Override
	public void setProductSku(final ProductSku productSku) {
		setProductSkuInternal(productSku);
	}

	/**
	 * JPA product sku setter.
	 * @param productSku the productSku to set
	 */
	protected void setProductSkuInternal(final ProductSku productSku) {
		this.productSku = productSku;
	}

	@Override
	@Transient
	public ProductSku getProductSku() {
		return getProductSkuInternal();
	}

	/**
	 * Get the product SKU corresponding to this item in a cart.
	 *
	 * @return a <code>ProductSKU</code>
	 */
	@Override
	@ManyToOne(targetEntity = ProductSkuImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@Column(name = "PRODUCT_SKU_UID")
	@ForeignKey(name = "TORDERSKU_IBFK_3", updateAction = ForeignKeyAction.NULL)
	public ProductSku getProductSkuInternal() {
		return this.productSku;
	}

	/**
	 * Get the productSku code.
	 *
	 * @return the productSku code
	 */
	@Basic
	@Column(name = "SKUCODE")
	public String getSkuCode() {
		return this.skuCode;
	}

	@Override
	public void setSkuCode(final String code) {
		this.skuCode = code;
	}

	/**
	 * Set the quantity of this item as a primitive int. Required by Spring Validator.
	 *
	 * @param quantity the quantity
	 */
	@Override
	public void setQuantity(final int quantity) {
		super.setQuantity(quantity);
		recalculate();
	}

	/**
	 * Get the amount for this sku (Price * Quantity).
	 *
	 * @return the amount
	 * @deprecated Use getInvoiceItemAmount() instead.
	 */
	@Basic
	@Column(name = "AMOUNT", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	@Deprecated
	protected BigDecimal getAmount() {
		return this.amount;
	}

	/**
	 * Set the amount for this sku (Price * Quantity).
	 *
	 * @param amount the amount
	 */
	protected void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	@Transient
	@Override
	@Deprecated
	public Money getTotal() {
		return makeMoney(getInvoiceItemAmount());
	}

	/**
	 * Get the unit price for this sku.
	 *
	 * @return the price
	 */
	@Basic
	@Column(name = "UNIT_PRICE", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	protected BigDecimal getUnitPriceInternal() {
		return unitPrice;
	}

	/**
	 * Set the unit price for this sku.
	 *
	 * @param price the price
	 */
	protected void setUnitPriceInternal(final BigDecimal price) {
		unitPrice = price;
	}

	/**
	 * Get the unit price for this sku.
	 *
	 * @return the price
	 */
	@Transient
	public BigDecimal getUnitPrice() {
		return getUnitPriceInternal();
	}

	/**
	 * Get the unit price as a <code>Money</code> object.
	 *
	 * @return a <code>Money</code> object representing the unit price
	 * @deprecated Call getUnitPriceCalc().getMoney() instead.
	 */
	@Transient
	@Deprecated
	public Money getUnitPriceMoney() {
		return makeMoney(getUnitPrice());
	}

	/**
	 * Set the unit price for this sku.
	 *
	 * @param price the price
	 */
	@Transient
	public void setUnitPrice(final BigDecimal price) {
		setUnitPriceInternal(price);
		recalculate();
	}

	/**
	 *
	 * @param discount the discount to set
	 */
	@Transient
	public void setDiscountBigDecimal(final BigDecimal discount) {
		setDiscountInternal(discount);
		recalculate();
	}

	/**
	 * @return the BigDecimal discount amount
	 */
	@Transient
	public BigDecimal getDiscountBigDecimal() {
		return getDiscountInternal();
	}

	/**
	 * Get the product's display name.
	 *
	 * @return the product's display name.
	 */
	@Basic
	@Column(name = "DISPLAY_NAME")
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Set the product's display name.
	 *
	 * @param displayName the product's display name
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Get the product's option values for display.
	 *
	 * @return the product's option values for display.
	 */
	@Basic
	@Column(name = "DISPLAY_SKU_OPTIONS")
	public String getDisplaySkuOptions() {
		return this.displaySkuOptions;
	}

	/**
	 * Set the product's option values for display.
	 *
	 * @param displaySkuOptions the product's option values for display
	 */
	public void setDisplaySkuOptions(final String displaySkuOptions) {
		this.displaySkuOptions = displaySkuOptions;
	}

	/**
	 * Get the product's image path.
	 *
	 * @return the product's image path.
	 */
	@Basic
	@Column(name = "IMAGE")
	public String getImage() {
		return this.image;
	}

	/**
	 * Set the product's image path.
	 *
	 * @param image the product's image path
	 */
	public void setImage(final String image) {
		this.image = image;
	}

	/**
	 * Returns the shipping weight.
	 *
	 * @return the shipping weight.
	 */
	@Basic
	@Column(name = "WEIGHT")
	public int getWeight() {
		return this.weight;
	}

	/**
	 * Sets the shipping weight.
	 *
	 * @param weight the shipping weight to set.
	 */
	public void setWeight(final int weight) {
		this.weight = weight;
	}

	/**
	 * Calculates the <code>MoneyImpl</code> savings if the price has a discount.
	 *
	 * @return the price savings as a <code>MoneyImpl</code>
	 */
	@Transient
	public Money getDollarSavingsMoney() {
		return makeMoney(getSavings());
	}

	/**
	 * Calculates the <code>BigDecimal</code> savings if any.
	 *
	 * @return the price savings as a <code>BigDecimal</code>
	 */
	@Transient
	public BigDecimal getSavings() {
		BigDecimal savings = BigDecimal.ZERO;
		if (getListUnitPrice() != null) {
			final BigDecimal goodsItemTotal = getListUnitPrice().getAmountUnscaled().multiply(new BigDecimal(getQuantity()));
			if (goodsItemTotal.compareTo(getInvoiceItemAmount()) > 0) {
				savings = goodsItemTotal.subtract(getInvoiceItemAmount());
			} else {
				savings = BigDecimal.ZERO;
			}
		}
		return savings;
	}

	/**
	 * Get the encrypted uidPk string.
	 *
	 * @return the encrypted uidPk string
	 */
	@Transient
	public String getEncryptedUidPk() {
		if (encryptedUidPk == null || encryptedUidPk.length() == 0) {
			StringEncrypter stringEncrypter = getBean("digitalAssetStringEncrypter");
			encryptedUidPk = stringEncrypter.encrypt(String.valueOf(getUidPk()));

		}
		return encryptedUidPk;

	}

	/**
	 * Gets the digital asset belong to this order SKU.
	 *
	 * @return the digital asset belong to this order SKU
	 */
	@ManyToOne(targetEntity = DigitalAssetImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "DIGITAL_ASSET_UID")
	public DigitalAsset getDigitalAsset() {
		return this.digitalAsset;
	}

	/**
	 * Sets the digital asset.
	 *
	 * @param digitalAsset the digital asset
	 */
	public void setDigitalAsset(final DigitalAsset digitalAsset) {
		this.digitalAsset = digitalAsset;
	}

	/**
	 * Gets the tax code for this order SKU.
	 *
	 * @return the tax code for this order SKU.
	 */
	@Basic
	@Column(name = "TAXCODE", nullable = false)
	public String getTaxCode() {
		return this.taxCode;
	}

	/**
	 * Sets the tax code for this order SKU.
	 *
	 * @param taxCode the tax code for this order SKU.
	 */
	public void setTaxCode(final String taxCode) {
		this.taxCode = taxCode;
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
	@Transient
	public int getReturnableQuantity() {
		return returnableQuantity;
	}

	@Override
	public void setReturnableQuantity(final int returnableQuantity) {
		this.returnableQuantity = returnableQuantity;
	}

	/**
	 * Copy order sku from another <CODE>OrderSku</CODE>.
	 *
	 * @param orderSku order sku which <CODE>this</CODE> copies from
	 */
	public void copyFrom(final OrderSku orderSku) {
		copyFrom(orderSku, true);
	}

	/**
	 * Copy order sku from another <CODE>OrderSku</CODE>.
	 *
	 * @param orderSku order sku which <CODE>this</CODE> copies from
	 * @param setPricingInfo if true, copies the pricing info
	 */
	public void copyFrom(final OrderSku orderSku, final boolean setPricingInfo) {
		if (orderSku.isBundle()) {
			// FIXME: remove once we prove bundles are not part of shipments, just the constituents
			throw new IllegalArgumentException();
		}
		if (orderSku.getParent() != null) {
			orderSku.getParent().addChildItem(this);
		}
		this.setAllocatedQuantity(orderSku.getAllocatedQuantity());
		this.setCreatedDate(orderSku.getCreatedDate());
		this.setDigitalAsset(orderSku.getDigitalAsset());
		this.setDisplayName(orderSku.getDisplayName());
		this.setDisplaySkuOptions(orderSku.getDisplaySkuOptions());
		this.setImage(orderSku.getImage());
		this.setLastModifiedBy(orderSku.getLastModifiedBy());
		this.setLastModifiedDate(orderSku.getLastModifiedDate());
		this.setDiscountBigDecimal(orderSku.getDiscount().getAmountUnscaled());
		this.setProductSku(orderSku.getProductSku());
		this.setSkuCode(orderSku.getSkuCode());
		// sets quantity, currency and prices
		if (setPricingInfo) {
			this.setPrice(orderSku.getQuantity(), orderSku.getPrice());
		}
		// FIXME: um... er... um... we just changed the quantity, and price, I'm sure the tax needs recalculating
		this.setTax(orderSku.getTax().getAmountUnscaled());
		if (setPricingInfo) {
			this.setUnitPrice(orderSku.getUnitPrice());
		}
		this.setWeight(orderSku.getWeight());
		this.setTaxCode(orderSku.getTaxCode());
		copyDataFieldsFrom(orderSku);
	}

	private void copyDataFieldsFrom(final OrderSku orderSku) {
		for (String key : orderSku.getFields().keySet()) {
			this.setFieldValue(key, orderSku.getFieldValue(key));
		}
	}

	/**
	 * @return the shipment that this order sku is part of
	 */
	@ManyToOne(targetEntity = AbstractOrderShipmentImpl.class, fetch = FetchType.EAGER,
				cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "ORDER_SHIPMENT_UID")
	@ForeignKey(name = "TORDERSKU_IBFK_1")
	public OrderShipment getShipment() {
		return shipment;
	}

	/**
	 * @param shipment the shipment to set
	 */
	public void setShipment(final OrderShipment shipment) {
		this.shipment = shipment;
	}

	/**
	 * Recalculate taxes and totals when modifications are made to the shipment.
	 */
	private void recalculate() {
		if (isRecalculationEnabled()) {
			BigDecimal amount = getInvoiceItemAmount();
			BigDecimal oldAmount = getAmount();
			setAmount(amount);
			firePropertyChange("amount", oldAmount, amount); //$NON-NLS-1$
		}
	}

	@Override
	@Transient
	@Deprecated
	public BigDecimal getInvoiceItemAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		if (getUnitPrice() != null) {
			amount = getUnitPrice().multiply(new BigDecimal(getQuantity()));
			final BigDecimal discount = getDiscountInternal();
			if (discount != null) {
				if (amount.compareTo(discount) == -1) {
					amount = BigDecimal.ZERO;
				} else {
					amount = amount.subtract(discount);
				}
			}
		}
		return amount;
	}

	@Override
	@PostLoad
	@PostUpdate
	public void enableRecalculation() {
		recalculationEnabled = true;
		recalculate();
	}

	@Override
	@PreUpdate
	public void disableRecalculation() {
		recalculationEnabled = false;
	}

	/**
	 * @return true if recalculation is enabled and necessary data is loaded, otherwise false
	 */
	@Transient
	protected boolean isRecalculationEnabled() {
		return recalculationEnabled;
	}

	/**
	 * JPA accessor for the allocated quantity.
	 *
	 * @return the allocated quantity
	 */
	@Basic
	@Column(name = "ALLOCATED_QUANTITY")
	protected int getAllocatedQuantityInternal() {
		return this.allocatedQuantity;
	}

	/**
	 * JPA accessor for setting the allocated quantity.
	 *
	 * @param qty the quantity to allocate.
	 */
	protected void setAllocatedQuantityInternal(final int qty) {
		this.allocatedQuantity = qty;
	}

	@Override
	@Transient
	public int getAllocatedQuantity() {
		return this.getAllocatedQuantityInternal();
	}

	@Override
	public void setAllocatedQuantity(final int qty) {
		int oldQty = this.allocatedQuantity;
		this.setAllocatedQuantityInternal(qty);
		firePropertyChange("orderInventoryAllocation", oldQty, qty);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return
	 */
	@Transient
	public boolean isAllocated() {
		return this.getQuantity() <= this.getAllocatedQuantity() + this.getChangedQuantityAllocated();
	}

	/**
	 * Get the changedQuantityAllocated. changedQuantityAllocated is the amount of allocated quantity increased/decreased based on quantityAllocated
	 *
	 * @return <CODE>changedQuantityAllocated</CODE>
	 */
	@Transient
	public int getChangedQuantityAllocated() {
		return this.changedQuantityAllocated;
	}

	/**
	 * Set the changedQuantityAllocated. changedQuantityAllocated is the amount of allocated quantity increased/decreased based on quantityAllocated
	 *
	 * @param changedQuantityAllocated the amount of allocated quantity increased/decreased upon quanittyAllocated
	 */
	public void setChangedQuantityAllocated(final int changedQuantityAllocated) {
		this.changedQuantityAllocated = changedQuantityAllocated;
	}

	/**
	 * Used for splitting shipment. get preOrBackOrderQuantity from this order sku
	 *
	 * @return preOrBackOrderQuantity
	 */
	@Transient
	public int getPreOrBackOrderQuantity() {
		return this.preOrBackOrderQuantity;
	}

	/**
	 * Used for splitting shipment. set preOrBackOrderQuantity for this order sku
	 *
	 * @param preOrBackOrderQuantity preOrBackOrderQuantity
	 */
	public void setPreOrBackOrderQuantity(final int preOrBackOrderQuantity) {
		this.preOrBackOrderQuantity = preOrBackOrderQuantity;
	}

	/**
	 * Gets the locale dependent sku option representation.
	 *
	 * @param locale the locale
	 * @return comma separated string
	 */
	public String getDisplaySkuOptions(final Locale locale) {
		if (getProductSku() != null && getProductSku().getOptionValues().size() > 0) {
			final StringBuffer skuOptionValues = new StringBuffer();
			for (final Iterator<SkuOptionValue> optionValueIter = getProductSku().getOptionValues().iterator(); optionValueIter.hasNext();) {
				final SkuOptionValue currOptionValue = optionValueIter.next();
				skuOptionValues.append(currOptionValue.getDisplayName(locale, true));
				if (optionValueIter.hasNext()) {
					skuOptionValues.append(", ");
				}
			}
			return skuOptionValues.toString();
		}
		return getDisplaySkuOptions();
	}

    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public int hashCode() {
    	return super.hashCode();
    }

    @Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof OrderSkuImpl)) {
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		addPropertyChangeListener(listener, true);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener, final boolean replace) {
		if (replace) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		addPropertyChangeListener(propertyName, listener, true);
	}

	@Override
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener, final boolean replace) {
		if (replace) {
			propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
		}
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Notify listeners of a property change.
	 *
	 * @param propertyName the name of the property that is being changed
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 *  @return the parent if this is a dependent item, otherwise null
	*/
	@ManyToOne(targetEntity = OrderSkuImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
												  fetch = FetchType.EAGER)
	@JoinColumn(name = "PARENT_ITEM_UID")
	@ForeignKey
	public OrderSku getParent() {
		return this.parent;
	}

	/**
	 * @param parent the parent OrderSku
	*/
	protected void setParent(final OrderSku parent) {
		this.parent = parent;
	}

	/**
	 *  @return the root if this is a dependent item, otherwise null
	*/
	@Transient
	public OrderSku getRoot() {
		OrderSku orderSku = getParent();
		while (orderSku != null && orderSku.getParent() != null) {
			orderSku = orderSku.getParent();
		}
		return orderSku;
	}

	/**
	 * Get the property change support object.
     *
	 * @return the propertyChangeSupport
	 */
	@Transient
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	/**
	 *	Set the property change support object.
	 *
	 * @param propertyChangeSupport the propertyChangeSupport to set
	 */
	public void setPropertyChangeSupport(final PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}

	@Override
	@OneToMany(targetEntity = ShoppingItemRecurringPriceImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = true)
	@ElementForeignKey
	@ElementDependent
	protected Set<ShoppingItemRecurringPrice> getRecurringPrices() {
		if (recurringPrices == null) {
			recurringPrices = new HashSet<ShoppingItemRecurringPrice>();
		}
		return recurringPrices;
	}

	@Override
	protected void setRecurringPrices(final Set<ShoppingItemRecurringPrice> recurringPrices) {
		this.recurringPrices = recurringPrices;
	}

	@Override
	@Transient
	public PriceCalculator getPriceCalc() {
		return new QuantityPriceCalculatorImpl();
	}

	@Override
	@Transient
	public PriceCalculator getUnitPriceCalc() {
		return new UnitPriceCalculatorImpl();
	}

	/**
	 * Price calculator implementation used by getPriceCalc() method for quantity-multiplied prices.
	 *
	 * @author gdenning
	 */
	private class QuantityPriceCalculatorImpl extends AbstractPriceCalculatorImpl implements PriceCalculator {

		/**
		 * Default constructor.
		 */
		QuantityPriceCalculatorImpl() {
			super();
		}

		@Override
		public BigDecimal getAmount() {
			BigDecimal amount = BigDecimal.ZERO.setScale(2);
			BigDecimal quantity = BigDecimal.valueOf(getQuantity());

			// We intentionally use getUnitPrice instead of findLowestUnitPrice because we want to use the persisted value.
			amount = getUnitPrice();

			if (amount == null) {
				return null;
			}
			amount = amount.multiply(quantity);

			if (isIncludeCartDiscounts() && getDiscount() != null) {
				amount = amount.subtract(getDiscount().getAmount());
			}

			if (getTaxHandling() != TaxHandlingEnum.USE_SITE_DEFAULTS) {
				// Tax inclusive pricing, but without tax.
				if (getShipment().isInclusiveTax() && getTaxHandling() == TaxHandlingEnum.EXCLUDE) {
					amount = amount.subtract(getTax().getAmount());
				}
				// Tax exclusive pricing, but with tax.
				if (!getShipment().isInclusiveTax() && getTaxHandling() == TaxHandlingEnum.INCLUDE) {
					amount = amount.add(getTax().getAmount());
				}
			}

			if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
				amount = BigDecimal.ZERO.setScale(2);
			}

			return amount;
		}

		public Money getMoney() {
			return makeMoney(getAmount());
		}
	}

	/**
	 * Price calculator implementation used by getUnitPriceCalc() method for unit prices.
	 *
	 * @author gdenning
	 */
	private class UnitPriceCalculatorImpl extends AbstractPriceCalculatorImpl implements PriceCalculator {
		/**
		 * Initialize a PriceCalculator object.
		 */
		UnitPriceCalculatorImpl() {
			super();
		}

		@Override
		public BigDecimal getAmount() {
			BigDecimal amount = BigDecimal.ZERO.setScale(2);
			BigDecimal quantity = BigDecimal.valueOf(getQuantity());

			// We intentionally use getUnitPrice instead of findLowestUnitPrice because we want to use the persisted value.
			amount = getUnitPrice();

			if (amount == null) {
				return null;
			}

			if (isIncludeCartDiscounts() && getQuantity() > 0 && getDiscount() != null) {
				BigDecimal discountAmount = getDiscount().getAmount();
				BigDecimal discount = discountAmount.divide(quantity, DEFAULT_NUM_FRACTIONAL_DIGITS, RoundingMode.HALF_EVEN)
						.setScale(2, RoundingMode.HALF_EVEN);
				amount = amount.subtract(discount);
			}

			if (getTaxHandling() != TaxHandlingEnum.USE_SITE_DEFAULTS) {
				// Tax inclusive pricing, but without tax.
				if (getShipment().isInclusiveTax() && getTaxHandling() == TaxHandlingEnum.EXCLUDE) {
					amount = amount.subtract(getUnitTax());
				}
				// Tax exclusive pricing, but with tax.
				if (!getShipment().isInclusiveTax() && getTaxHandling() == TaxHandlingEnum.INCLUDE) {
					amount = amount.add(getUnitTax());
				}
			}

			if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
				amount = BigDecimal.ZERO.setScale(2);
			}

			return amount;
		}

		public Money getMoney() {
			return makeMoney(getAmount());
		}
	}
}
