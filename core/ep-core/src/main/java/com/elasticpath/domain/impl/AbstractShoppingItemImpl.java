/**
 * 
 */
package com.elasticpath.domain.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.Persistent;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shoppingcart.ShoppingCartVisitor;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.sellingchannel.ShoppingItemRecurringPriceAssembler;
import com.elasticpath.service.shoppingcart.impl.ItemPricing;

/**
 * Abstract implementation of {@link ShoppingItem} for JPA persistence.<br/>
 * 
 * NOTE that the presence of the {@code DatabaseLastModifiedDate} means that whenever this object is saved or updated to the database
 * the lastModifiedDate will be set by the {@code LastModifiedPersistenceEngineImpl} if that class in configured in Spring. 
 */
@MappedSuperclass
@DataCache(enabled = false)
@FetchGroups({
	@FetchGroup(
			name = FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS, 
			attributes = {
					@FetchAttribute(name = "listUnitPriceInternal"),
					@FetchAttribute(name = "saleUnitPriceInternal"),
					@FetchAttribute(name = "promotedUnitPriceInternal")
			}
	),
	@FetchGroup(
			name = FetchGroupConstants.ORDER_DEFAULT, 
			attributes = {
					@FetchAttribute(name = "listUnitPriceInternal"),
					@FetchAttribute(name = "saleUnitPriceInternal"),
					@FetchAttribute(name = "promotedUnitPriceInternal"),
					@FetchAttribute(name = "currency"),
					@FetchAttribute(name = "quantityInternal"),
					@FetchAttribute(name = "guid"),
					@FetchAttribute(name = "ordering")
			}
	),
	@FetchGroup(name = FetchGroupConstants.ORDER_SEARCH, attributes = {
			@FetchAttribute(name = "guid")
	})

})
public abstract class AbstractShoppingItemImpl extends AbstractLegacyEntityImpl implements ShoppingItem, DatabaseLastModifiedDate {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(AbstractShoppingItemImpl.class);

	private static final int DEFAULT_NUM_FRACTIONAL_DIGITS = 2;
	
	// quantity/pricing/currency must always be set together
	private int quantity = 0;
	
	private BigDecimal listUnitPrice = null, saleUnitPrice = null, promoUnitPrice = null;
	
	private Currency currency = null;
	
	private Price price = null;
	
	private BigDecimal discount = BigDecimal.ZERO;
	
	private BigDecimal tax = BigDecimal.ZERO;

	private String errorMessage;
	
	private Date lastModifiedDate;
	
	private int ordering;
	
	private transient ShoppingItemRecurringPriceAssembler shoppingItemRecurringPriceAssembler;

	private String guid; 
	
	
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
	 * Get the product SKU corresponding to this item in a cart.
	 * 
	 * @return a <code>ProductSKU</code>
	 */
	@Transient
	protected abstract ProductSku getProductSkuInternal();
	
	@Override
	@Transient
	public ProductSku getProductSku() {
		return getProductSkuInternal();
	}

	@Override
	public abstract void setProductSku(final ProductSku productSku);
	
	/**
	 * Set the quantity of this item as a primitive int. Required by Spring Validator.
	 * 
	 * @param quantity the quantity
	 */
	public void setQuantity(final int quantity) {
		if (quantity < 0) {
			throw new EpDomainException("Invalid item quantity: " + quantity);
		}
		setQuantityInternal(quantity);
	}

	/**
	 * Get the quantity of this item as a primitive int. Required by Spring Validator.
	 * 
	 * @return the quantity
	 */
	@Transient
	public int getQuantity() {
		return getQuantityInternal();
	}

	/**
	 * Set the quantity of this item as a primitive int. Required by Spring Validator.
	 * 
	 * @param quantity the quantity
	 */
	protected void setQuantityInternal(final int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Get the quantity of this item as a primitive int. Required by Spring Validator.
	 * 
	 * @return the quantity
	 */
	@Basic(optional = false)
	@Column(name = "QUANTITY", nullable = false)
	protected int getQuantityInternal() {
		return this.quantity;
	}
	
	/**
	 * Get the currency.
	 * 
	 * @return the Currency
	 */
	@Persistent
	@Column(name = "CURRENCY")
	@Externalizer("getCurrencyCode")
	@Factory("com.elasticpath.commons.util.impl.ConverterUtils.currencyFromString")
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Set the currency.
	 * 
	 * @param currency the currency to set
	 */
	protected void setCurrency(final Currency currency) {
		this.currency = currency;
	}
	
	/**
	 * Get the list unit price for this item.
	 * 
	 * @return the price
	 */
	@Basic
	@Column(name = "LIST_UNIT_PRICE", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	protected BigDecimal getListUnitPriceInternal() {
		return this.listUnitPrice;
	}
	
	/**
	 * Set the list unit price for this item.
	 * 
	 * @param price the list unit price to set
	 */
	protected void setListUnitPriceInternal(final BigDecimal price) {
		listUnitPrice = price;
	}

	/**
	 * Get the sale unit price for this item.
	 * 
	 * @return the price
	 */
	@Basic
	@Column(name = "SALE_UNIT_PRICE", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	protected BigDecimal getSaleUnitPriceInternal() {
		return this.saleUnitPrice;
	}
	
	/**
	 * Set the sale unit price for this item.
	 * 
	 * @param price the sale unit price to set
	 */
	protected void setSaleUnitPriceInternal(final BigDecimal price) {
		saleUnitPrice = price;
	}
	
	/**
	 * Get the catalog promoted unit price for this item.
	 * 
	 * @return the price
	 */
	@Basic
	@Column(name = "PROMO_UNIT_PRICE", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	protected BigDecimal getPromotedUnitPriceInternal() {
		return this.promoUnitPrice;
	}
	
	/**
	 * Set the catalog promoted unit price for this item.
	 * 
	 * @param price the catalog promoted unit price to set
	 */
	protected void setPromotedUnitPriceInternal(final BigDecimal price) {
		promoUnitPrice = price;
	}
	
	/** 
	 * @return the lowest unit price
	 *
	 * Calculated based on if the item is on sale and/or has had a catalog promotion applied
	 * Note: similar behavior as <code>Price</code> object but for the single tier for this item quantity
	 */
	@Transient
	protected BigDecimal findLowestUnitPrice() {
		BigDecimal lowestPrice = this.getListUnitPriceInternal();

		final BigDecimal salePrice = this.getSaleUnitPriceInternal();
		if (salePrice != null && salePrice.compareTo(lowestPrice) < 0) {
			lowestPrice = salePrice;
		}

		final BigDecimal promoPrice = this.getPromotedUnitPriceInternal();
		if (promoPrice != null && promoPrice.compareTo(lowestPrice) < 0) {
			lowestPrice = promoPrice;
		}
		//SUBS-29 If the item has no upfront price, but has recurring prices, the upfront price should be displayed as 0. 
		if (lowestPrice == null && hasRecurringPrice()) {
			lowestPrice = BigDecimal.ZERO;
		}
		return lowestPrice;
	}
	
	private boolean hasRecurringPrice() {
		return getPrice() != null && getPrice().getPricingScheme() != null  
			&& !getPrice().getPricingScheme().getSchedules(PriceScheduleType.RECURRING).isEmpty();
	}
	
	/**
	 * @return BigDecimal representing the total item price: (lowest unit price
	 *         * quantity) - discount
	 * @deprecated Call getPriceCalc().withCartDiscounts().getAmount() instead.
	 */
	@Transient
	@Deprecated
	protected BigDecimal calculateItemTotal() {
		BigDecimal unitPrice = findLowestUnitPrice();
		// SUBS-29 If the item has no upfront price, but has recurring prices,
		// the upfront price should be displayed as 0.
		if (unitPrice == null) {
			if (!hasRecurringPrice()) {
				return null;
			}
			unitPrice = BigDecimal.ZERO;
		}
		BigDecimal quantity = new BigDecimal(getQuantityInternal());
		BigDecimal total = unitPrice.multiply(quantity);
		BigDecimal discount = getDiscountInternal();
		if (discount != null) {
			if (discount.compareTo(total) > 0) {
				total = BigDecimal.ZERO;
			} else {
				total = total.subtract(discount);
			}
		}

		return round(total);
	}
	
	/**
	 * Get the discount applied to this item.
	 * 
	 * @return the discount
	 */
	@Basic
	@Column(name = "DISCOUNT_AMOUNT", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	protected BigDecimal getDiscountInternal() {
		return this.discount;
	}
	
	/**
	 * Set the discount applied to this item.
	 * 
	 * @param discount - the amount to discount this item
	 */
	protected void setDiscountInternal(final BigDecimal discount) {
		this.discount = discount;
	}

	/**
	 * Clear the discount applied to this item.
	 * 
	 */
	public void clearDiscount() {
		setDiscountInternal(null);
	}
	
	/**
	 * Apply discount to this item.
	 * 
	 * @param discount - the discount amount to apply
	 */
	@Transient
	public void applyDiscount(final BigDecimal discount) {
		if (!isDiscountable()) {
			return;
		}
		if (getDiscountInternal() == null || discount.compareTo(new BigDecimal(0D)) < 0) {
			setDiscountInternal(BigDecimal.ZERO);
			if (LOG.isDebugEnabled()) {
				LOG.warn("Attempt to set negative CartItem discount");
			}
		}
		// Store a rounded discount amount
		BigDecimal roundedDiscount = round(discount);
		setDiscountInternal(getDiscountInternal().add(roundedDiscount));
	}

	/**
	 * Rounds the amount half-up by the default number of fractional digits to use with this currency. If the currency
	 * is not defined, then the default amount ({@value #DEFAULT_NUM_FRACTIONAL_DIGITS}) of fractional digits will be
	 * used.
	 *
	 * @param amount The amount to round
	 * @return The rounded value.
	 */
	protected BigDecimal round(final BigDecimal amount) {
		int scale = DEFAULT_NUM_FRACTIONAL_DIGITS;
		Currency theCurrency = getCurrency();
		if (theCurrency != null) {
			scale = theCurrency.getDefaultFractionDigits();
		}

		return amount.setScale(scale, RoundingMode.HALF_UP);
	}

	@Override
	@Transient
	public boolean isDiscountable() {
		return !getProductType().isExcludedFromDiscount();
	}
	
	@Override
	@Transient
	public boolean canReceiveCartPromotion() {
		BigDecimal unitPrice = getListUnitPriceInternal();
		return isDiscountable() 
			&& (CollectionUtils.isEmpty(getRecurringPrices()) || (unitPrice != null && BigDecimal.ZERO.compareTo(unitPrice) < 0));
	}
	
	
	/**
	 * Returns flag that shows if this item is shippable vs. electronic
	 *
	 * @return <code>true</code> if this item is shippable, false otherwise
	 */
	@Transient
	public boolean isShippable() {
		boolean shippable = false;
		if (this.isBundle()) {
			for (ShoppingItem item : this.getBundleItems()) {
				if (item.isShippable()) {
					shippable = true;
					break;
				}
			}
		} else {
			shippable = this.getProductSku().isShippable();
		}
		
		return shippable;
	}

	
	@Transient
	private ProductType getProductType() {
		return getProductSku().getProduct().getProductType();
	}

	/**
	 * Get the discount applied to this item as a <code>Money</code> object.
	 * 
	 * @return the discount
	 */
	@Transient
	public Money getDiscount() {
		BigDecimal discountInternal = getDiscountInternal();
		if (discountInternal == null) {
			return makeMoney(BigDecimal.ZERO);
		}
		return makeMoney(discountInternal);
	}

	
	/**
	 * Get the tax applied to this item.
	 * 
	 * @return the tax
	 */
	@Basic
	@Column(name = "TAX_AMOUNT", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	protected BigDecimal getTaxInternal() {
		return this.tax;
	}
	
	/**
	 * Set the tax applied to this item.
	 * 
	 * @param tax - the amount of tax for item
	 */
	protected void setTaxInternal(final BigDecimal tax) {
		this.tax = tax;
	}
	
	@Override
	@Transient
	public Money getTax() {
		return makeMoney(getTaxInternal());
	}

	@Override
	@Transient
	public void setTax(final BigDecimal tax) {
		setTaxInternal(tax);
	}
	
	/**
	 * Get the amount as a <code>Money</code> object.
	 * 
	 * @param amount the amount
	 * @return a <code>Money</code> object representing the amount with the item currency
	 */
	@Transient
	protected Money makeMoney(final BigDecimal amount) {
		if (amount == null) {
			return null;
		}

		return MoneyFactory.createMoney(amount, getCurrency());
	}
	
	/** 
	 * @return the list unit price as a <code>Money</code> object.
	 */
	@Transient
	public Money getListUnitPrice() {
		return makeMoney(getListUnitPriceInternal());
	}
	
	/**
	 * @return the sale unit price as a <code>Money</code> object.  Can be null.
	 */
	@Transient
	public Money getSaleUnitPrice() {
		BigDecimal price = getSaleUnitPriceInternal();
		
		return makeMoney(price);
	}
	
	/**
	 * @return the catalog promoted price as a <code>Money</code> object.  Can be null.
	 */
	@Transient
	public Money getPromotedUnitPrice() {
		BigDecimal price = getPromotedUnitPriceInternal();
		if (price == null) {
			price = getSaleUnitPriceInternal();
			if (price == null) {
				price = getListUnitPriceInternal();
			}
		}
		return makeMoney(price);
	}
	
	/**
	 * @return the lowest unit price as a <code>Money</code> object
	 * Calculated based on if the item is on sale and/or has had a catalog promotion applied
	 * Note: identical behavior as <code>PriceTier</code> object
	 */
	@Transient
	public Money getLowestUnitPrice() {
		return makeMoney(findLowestUnitPrice());
	}
	
	/**
	 * Get the total amount as a <code>Money</code> object.
	 * 
	 * @return the total amount that this line item is worth (lowest price * quantity).
	 * @deprecated Call getPriceCalc().withCartDiscounts().getMoney() instead.
	 */
	@Transient
	@Deprecated
	public Money getTotal() {
		return makeMoney(calculateItemTotal());
	}
	
	/**
	 * Returns per unit portion of this order sku tax amount.
	 *
	 * @return Unit level tax.
	 */
	@Transient
	protected BigDecimal getUnitTax() {
		return getTax().getAmount().divide(
				new BigDecimal(getQuantity()), DEFAULT_NUM_FRACTIONAL_DIGITS, BigDecimal.ROUND_HALF_UP).setScale(2, RoundingMode.HALF_EVEN);
	}

	/**
	 * Set the price details on the shopping item.
	 * 
	 * @param quantity - the new quantity
	 * @param price - the new price (contains Currency)
	 */
	public void setPrice(final int quantity, final Price price) {

		this.price = price;
		setQuantity(quantity);
		setListUnitPriceInternal(null);
		setSaleUnitPriceInternal(null);
		setPromotedUnitPriceInternal(null);
		setCurrency(null);
		if (price != null) {
			setCurrency(price.getCurrency());
			Money money = price.getListPrice(getQuantity());
			if (money != null) {
				setListUnitPriceInternal(money.getAmountUnscaled());
			}
			money = price.getSalePrice(getQuantity());
			if (money != null) {
				setSaleUnitPriceInternal(money.getAmountUnscaled());
			}
			money = price.getComputedPrice(getQuantity());
			if (money != null) {
				setPromotedUnitPriceInternal(money.getAmountUnscaled());
			}
			if (price.getPricingScheme() != null 
					&& CollectionUtils.isNotEmpty(price.getPricingScheme().getRecurringSchedules())) {
				setRecurringPrices(getShoppingItemRecurringPriceAssembler().createShoppingItemRecurringPrices(price, getQuantity()));
			}
		}
	}
	
	@Override
	@Transient
	public Price getPrice() {
		if (this.price == null) {
			assemblePrice();
		}
		
		return price;
	}

	/**
	 * @return true if this item has other items that depend on it.
	 */
	@Transient
	public boolean hasBundleItems() {
		return !getBundleItems().isEmpty();
	}

	/**
	 * @param skuCode The GUID of the particular bundle SKU to be in the cart.
	 * @return true if this item has a bundle item that matches the supplied skuCode
	 */
	public boolean hasBundleItem(final String skuCode) {
		return getItemWithSku(skuCode, getBundleItems()) != null;
	}

	/**
	 * Find the shopping item by it's sku code. 
	 *
	 * @param skuCode the sku code
	 * @param items list of items to look through
	 * @return ShoppingItem 
	 */
	protected ShoppingItem getItemWithSku(final String skuCode, final List<ShoppingItem> items) {
		for (ShoppingItem item : items) {
			if (item.getProductSku().getSkuCode().equals(skuCode)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Specify that another item depends on this item.
	 * If the dependent item is already associated with this item, remove it and replace with the new item.
	 * This will ensure we get the correct dependent item quantity.
	 * 
	 * @param newChildItem the other, dependent item
	 */
	public void addChildItem(final ShoppingItem newChildItem) {
		getChildItemsInternal().add(newChildItem);
	}

	/**
	 * Get the set of dependent items.
	 * 
	 * @return the set of dependent items.
	 */
	@Transient
	public List<ShoppingItem> getBundleItems() {
		return getChildItemsInternal();
	}

	/**
	 * Set the set of dependent items.
	 * 
	 * @param bundleItems the set of dependent items.
	 */
	public void setBundleItems(final List<ShoppingItem> bundleItems) {
		this.getChildItemsInternal().clear();
		if (bundleItems != null) {
			for (ShoppingItem item : bundleItems) {
				getChildItemsInternal().add(item);
			}
		}
	}

	/**
	 * Internal accessor used by JPA.
	 * 
	 * @return the set of dependent cart items.
	 */
	@Transient
	protected abstract List<ShoppingItem> getChildItemsInternal();

	/**
	 * Internal accessor used by JPA.
	 * 
	 * @param childItems the set of dependent cart items.
	 */
	protected abstract void setChildItemsInternal(final List<ShoppingItem> childItems);

	/**
	 * @return min order quantity
	 */
	@Transient
	public int getMinOrderQty() {
		return this.getProductSku().getProduct().getMinOrderQty();
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * Set the date that this was last modified on.
	 * 
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	@Override
	@Transient
	public String getErrorMessage() {
		return this.errorMessage;
	}

	@Override
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * Sets field values.
	 * 
	 * @param itemFields item fields
	 */
	public void mergeFieldValues(final Map<String, String> itemFields) {
		if (itemFields != null) {
            for (Map.Entry<String, String> fieldEntry : itemFields.entrySet()) {
                setFieldValue(fieldEntry.getKey(), fieldEntry.getValue());
            }
        }
	}

	/**
	 * Accesses the field for {@code name} and returns the current value. If the field has not been set
	 * then will return null.
	 *
	 * @param name The name of the field.
	 * @return The current value of the field or null.
	 */
	@Transient
	public String getFieldValue(final String name) {
		AbstractItemData data = getItemData().get(name);
		if (data == null) {
			return null;
		}
		return data.getValue();
	}
	
	/**
	 * Internal JPA method to get Item Data.
	 * @return the item data
	 */
	@Transient
	protected abstract Map<String, AbstractItemData> getItemData();
	
	@Override
	@Transient
	public boolean isConfigurable() {
		final Product product = getProductSku().getProduct();
		return (GiftCertificate.KEY_PRODUCT_TYPE.equals(product
				.getProductType().getName()) || product instanceof ProductBundle);
	}
	
	/**
	 * @return map of all key/value data field pairs
	 */
	@Transient
	public Map<String, String> getFields() {
		Map<String, String> fields = new HashMap<String, String>();
		for (String key : getItemData().keySet()) {
			fields.put(key, getItemData().get(key).getValue());
		}
		return Collections.unmodifiableMap(fields);
	}
	
	@Override
	@Transient
	public String getProductTypeName() {
		// use the StoreProduct if it is set
		return getProductType().getName();
	}
	
	/**
	 * Sets the ordering of this shopping item.
	 * 
	 * @param ordering the ordering
	 */
	public void setOrdering(final int ordering) {
		this.ordering = ordering;
	}
	
	@Override
	@Basic
	@Column(name = "ORDERING")
	public int getOrdering() {
		return this.ordering;
	}
	
	@Override
	public void addChild(final ShoppingItem child) {
		this.addChildItem(child);
	}

	@Override
	@Transient
	public List<ShoppingItem> getChildren() {
		return getChildItemsInternal();
	}
	
	@Override
	@Transient
	public ItemPricing getLinePricing() {
		ItemPricing pricing = new ItemPricing(
				getLineValue(getLowestUnitPrice()), 
				getDiscount().getAmount(), 
				getQuantity());
		return pricing;
	}

	@Transient
	private BigDecimal getLineValue(final Money unitValue) {
		return unitValue.multiply(getQuantity()).getAmount();
	}
	
	@Override
	@Transient
	public boolean isBundle() {
		return this.getProductSku().getProduct() instanceof ProductBundle;
	}
	
	/**
	 * Creates the price field from the ShoppingItem fields: listUnitPrice, saleUnitPrice, promotedUnitPrice, and recurringPrices.  
	 */
	@PostLoad
	@PostUpdate
	protected void assemblePrice() {
		Price price = getBean(ContextIdNames.PRICE);
		price.setCurrency(getCurrency());
		boolean hasRecurringPrice = getRecurringPrices() != null && !getRecurringPrices().isEmpty();
		if (getListUnitPriceInternal() == null) {
			if (hasRecurringPrice) {
				price.setListPrice(makeMoney(BigDecimal.ZERO));
			}
		} else { 
			price.setListPrice(getListUnitPrice());
		}
		if (getSaleUnitPriceInternal() != null) {
			price.setSalePrice(getSaleUnitPrice());
		}
		if (getPromotedUnitPriceInternal() != null) {
			price.setComputedPrice(getPromotedUnitPrice());
		}
		
		getShoppingItemRecurringPriceAssembler().assemblePrice(price, getRecurringPrices());
		if (getListUnitPrice() != null && !(hasRecurringPrice && BigDecimal.ZERO.setScale(2).equals(getListUnitPrice().getAmount()))) {
			getShoppingItemRecurringPriceAssembler().assemblePrice(price, getRecurringPrices());
			PriceSchedule purchaseTime = getBean(ContextIdNames.PRICE_SCHEDULE);
			purchaseTime.setType(PriceScheduleType.PURCHASE_TIME);
			price.getPricingScheme().setPriceForSchedule(purchaseTime, price);
		}
		this.price = price;
	}

	/**
	 * Get the recurring prices. These items, in addition to the list, sale, and computed price available on this class will be used to
	 * persist the price object. 
	 *
	 * @return list of recurring prices
	 */
	protected abstract Set<ShoppingItemRecurringPrice> getRecurringPrices();

	
	/**
	 * Set the recurring prices. It will set the price object as dirty. 
	 *
	 * @param recurringPrices recurring prices to set.
	 */
	protected abstract void setRecurringPrices(final Set<ShoppingItemRecurringPrice> recurringPrices);


	/**
	 * Get the assembler to be used for conversion between the Price set to a ShoppingItem and the recurring charges on the database.
	 * 
	 * @return the assembler 
	 */
	@Transient
	protected ShoppingItemRecurringPriceAssembler getShoppingItemRecurringPriceAssembler() {
		if (shoppingItemRecurringPriceAssembler == null) {
			shoppingItemRecurringPriceAssembler = getBean(ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE_ASSEMBLER);
		}
		return shoppingItemRecurringPriceAssembler;
	}

	/**
	 * Set the assembler that is used to convert between {@link ShoppingItemRecurringPrice} and {@link Price}.
	 *
	 * @param shoppingItemRecurringPriceAssembler the assembler
	 */
	public void setShoppingItemRecurringPriceAssembler(final ShoppingItemRecurringPriceAssembler shoppingItemRecurringPriceAssembler) {
		this.shoppingItemRecurringPriceAssembler = shoppingItemRecurringPriceAssembler;
	}

	@Override
	@Transient
	public void accept(final ShoppingCartVisitor visitor) {
		visitor.visit(this);
		if (isBundle()) {
			for (ShoppingItem item : getBundleItems()) {
				item.accept(visitor);
			}
		}
	}
}
