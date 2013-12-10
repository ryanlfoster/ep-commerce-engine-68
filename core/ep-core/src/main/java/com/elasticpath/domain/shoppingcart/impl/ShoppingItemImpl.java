/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.impl.AbstractItemData;
import com.elasticpath.domain.impl.AbstractShoppingItemImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.shoppingcart.impl.AbstractPriceCalculatorImpl.TaxHandlingEnum;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * A {@code ShoppingItem} represents a quantity of SKUs in a shopping cart.<br/>
 * 
 * NOTE that the presence of the {@code DatabaseLastModifiedDate} means that whenever this object is saved or updated to the database
 * the lastModifiedDate will be set by the {@code LastModifiedPersistenceEngineImpl} if that class in configured in Spring. 
 */
@Entity
@Table(name =  ShoppingItemImpl.TABLE_NAME)
@DataCache(enabled = false)
@FetchGroups({
	@FetchGroup(
			name = FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS, 
			attributes = {
					@FetchAttribute(name = "childItemsInternal", recursionDepth = -1)
			}
	),
	@FetchGroup(
			name = FetchGroupConstants.ORDER_DEFAULT, 
			attributes = {
					@FetchAttribute(name = "recurringPrices")
			}
	)
})
public class ShoppingItemImpl extends AbstractShoppingItemImpl implements CartItem {	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 7000000001L;

//	private static final Logger LOG = Logger.getLogger(ShoppingItemImpl.class);
	
	private static final int SCALE = 10;
	
	private static final String FK_COLUMN_NAME = "CARTITEM_UID";

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCARTITEM";

	private long uidPk;

	private List<ShoppingItem> childItems = new ArrayList<ShoppingItem>();
	
	private Map<String, AbstractItemData> fieldValues = new HashMap<String, AbstractItemData>();
	
	private ProductSku productSku;
	
	private Long cartUid;

	private Set<ShoppingItemRecurringPrice> recurringPrices = new HashSet<ShoppingItemRecurringPrice>();
	
	private boolean taxInclusive;

	/**
	 * Internal JPA method to get Item Data.
	 * @return the item data
	 */
	@OneToMany(targetEntity = ShoppingItemData.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "key")
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = false)
	@ElementForeignKey
	@ElementDependent
	@Override
	protected Map<String, AbstractItemData> getItemData() {
		return this.fieldValues;
	}
	
	/**
	 * Sets the {@code ShoppingItemData} - for JPA.
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
		ShoppingItemData data = new ShoppingItemData(name, value);
		getItemData().put(name, data);
	}
	
	@Override
	public void setProductSku(final ProductSku productSku) {
		if (getProductSku() != null
				&& productSku != null
				&& getProductSku().getProduct() != null
				&& productSku.getProduct() != null
				&& !getProductSku().getProduct().getGuid().equals(productSku
						.getProduct().getGuid())) {
				throw new IllegalArgumentException("The product sku may not be changed to a product sku for a differing product.");
		}
		setProductSkuInternal(productSku);
	}

	/**
	 * JPA product sku setter.
	 * @param productSku the productSku to set
	 */
	protected void setProductSkuInternal(final ProductSku productSku) {
		this.productSku = productSku;
	}
	
	/**
	 * Get the product SKU corresponding to this item in a cart.
	 * 
	 * @return a <code>ProductSKU</code>
	 */
	@Override
	@ManyToOne(targetEntity = ProductSkuImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "SKU_UID")
	@ForeignKey(name = "tcartitem_ibfk_1")
	protected ProductSku getProductSkuInternal() {
		return this.productSku;
	}
	
	/**
	 * Internal accessor used by JPA.
	 * 
	 * @return the set of dependent cart items.
	 */
	@OneToMany(targetEntity = ShoppingItemImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "PARENT_ITEM_UID")
	@ElementForeignKey
	@ElementDependent
	@OrderBy("ordering")
	@Override
	protected List<ShoppingItem> getChildItemsInternal() { // the set should be of CartItem implementors
		return this.childItems;
	}

	/**
	 * Internal accessor used by JPA.
	 * 
	 * @param childItems the set of dependent cart items.
	 */
	@Override
	protected void setChildItemsInternal(final List<ShoppingItem> childItems) {
		this.childItems = childItems;
	}
	
	
	/**
	 * Gets the cart uid.
	 * 
	 * @return the cart uid
	 */
	@Transient
	public Long getCartUid() {
		return cartUid;
	}
	
	/**
	 * Sets the cart uid.
	 * 
	 * @param cartUid the cart uid
	 */
	@Transient
	public void setCartUid(final Long cartUid) {
		this.cartUid = cartUid;
	}
	
	
	/**
	 * Get the set of dependent items.
	 * 
	 * @return the set of dependent items.
	 */
	@Transient
	@Override
	public List<ShoppingItem> getBundleItems() {
		List<ShoppingItem> bundleItems = new ArrayList<ShoppingItem>();
		for (ShoppingItem item : getChildItemsInternal()) {
			// filter out non-bundle items		
			if (isBundleItem(item)) {
				bundleItems.add(item);
			}			
		}
		return bundleItems;
	}

	private boolean isBundleItem(final ShoppingItem item) {
		return isBundle() && ((CartItem) item).getCartUid() == null;
	}
	
	/**
	 * Get the dependent shopping items for this item.
	 *
	 * @return list of dependent ShoppingItems
	 */
	@Transient
	public List<ShoppingItem> getDependentItems() {
		List<ShoppingItem> dependentItems = new ArrayList<ShoppingItem>();
		for (ShoppingItem item : getChildItemsInternal()) {
			//Bundle items are not in cart, but dependent items are
			if (((CartItem) item).getCartUid() != null) { 
				dependentItems.add(item);
			}
		}
		return dependentItems;
	}

	/**
	 * Check whether the item has a dependent item with the specified skuCode.
	 *
	 * @param skuCode the sku code
	 * @return true if skuCode is present in dependents
	 */
	@Transient
	public boolean hasDependentItem(final String skuCode) {
		return getItemWithSku(skuCode, getDependentItems()) != null;
	}

	/**
	 * Check whether this item has dependent items.
	 *
	 * @return true if there are dependent items.
	 */
	@Transient
	public boolean hasDependentItems() {
		return !getDependentItems().isEmpty();
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

	/**
	 * Return the hash code.
	 * 
	 * @return the hash code
	 */
    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Return{@code true} if the given object is a {@code ShoppingItemImpl} and is logically equal.
	 * 
	 * @param obj the object to compare
	 * @return <code>true</code> if the given object is equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ShoppingItemImpl)) {
			return false;
		}
		return super.equals(obj);
	}

	
	
	/**
	 * Get the recurring prices.
	 * @return the recurring prices
	 */
	@OneToMany(targetEntity = ShoppingItemRecurringPriceImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = true)
	@ElementForeignKey
	@ElementDependent
	@Override
	protected Set<ShoppingItemRecurringPrice> getRecurringPrices() {
		return recurringPrices;
	}

	@Override
	protected void setRecurringPrices(final Set<ShoppingItemRecurringPrice> recurringPrices) {
		this.recurringPrices = recurringPrices;
	}
	/**
	 * Is the site tax inclusive or not?  This property will be set whenever a cart item is created from a cart.
	 * @return true if site is tax inclusive, false if it is tax exclusive
	 */
	@Transient
	protected boolean isTaxInclusive() {
		return taxInclusive;
	}

	@Override
	public void setTaxInclusive(final boolean isTaxInclusive) {
		this.taxInclusive = isTaxInclusive;
	}	
	
	/**
	 * Price calculator implementation used by getPriceCalc() method for quantity-multiplied prices.
	 * 
	 * @author gdenning
	 */
	private class QuantityPriceCalculatorImpl extends AbstractPriceCalculatorImpl implements PriceCalculator {
		
		/**
		 * Initialize a PriceCalculator object.
		 */
		QuantityPriceCalculatorImpl() {
			super();
		}
		
		@Override
		public BigDecimal getAmount() {
			BigDecimal amount = findLowestUnitPrice();
			BigDecimal quantity = BigDecimal.valueOf(getQuantity());
			
			if (amount == null) {
				return null;
			}
			
			amount = findLowestUnitPrice().multiply(quantity);
			
			if (isIncludeCartDiscounts() && getDiscount() != null) {
				amount = amount.subtract(getDiscount().getAmount());
			}
			
			amount = addOrSustractTaxForPrice(amount, getTaxHandling(), getTax().getAmount());
			
			if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
				amount = BigDecimal.ZERO.setScale(2);
			}
			
			return roundAmountBasedOnTaxType(amount);
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
		 * Initialize a PriceCalculator.
		 */
		UnitPriceCalculatorImpl() {
			super();
		}
		
		@Override
		public BigDecimal getAmount() {
			BigDecimal amount = findLowestUnitPrice();
			BigDecimal quantity = BigDecimal.valueOf(getQuantity());
			
			if (amount == null) {
				return null;
			}
			
			// subtract non-coupon discount
			if (isIncludeCartDiscounts() && getQuantity() > 0 && getDiscount() != null) {
				BigDecimal nonCouponDiscountPerUnit = getDiscount().getAmount().divide(quantity, SCALE, BigDecimal.ROUND_HALF_UP);
				amount = amount.subtract(nonCouponDiscountPerUnit);
			}
			
			amount = addOrSustractTaxForPrice(amount, getTaxHandling(), getUnitTax());
			
			if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
				amount = BigDecimal.ZERO.setScale(2);
			}
			
			return roundAmountBasedOnTaxType(amount);
		}
		
		public Money getMoney() {
			return makeMoney(getAmount());
		}
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
	 * Round the Amount, based on the tax type. 
	 * @param amount - the values that needs rounding
	 * @return the value after rounding
	 */
	private BigDecimal roundAmountBasedOnTaxType(final BigDecimal amount) {
		// If the state uses tax inclusive pricing, they likely also require the use of ROUND_HALF_EVEN instead of ROUND_HALF_UP.
		if (taxInclusive) {
			return amount.setScale(getCurrency().getDefaultFractionDigits(), BigDecimal.ROUND_HALF_EVEN);
		} 
		
		return amount.setScale(getCurrency().getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Determine the price of item based on the taxes handling property and taxes type.
	 *
	 * @param amount - the price of the item
	 * @param itemTaxHandlingValue - how taxes are handled for this item
	 * @param taxAmount - the amount paid towards taxes for this item
	 * @return - the price of the item after taxes logic
	 */
	private BigDecimal addOrSustractTaxForPrice(final BigDecimal amount, final TaxHandlingEnum itemTaxHandlingValue, final BigDecimal taxAmount) {

		if (itemTaxHandlingValue != TaxHandlingEnum.USE_SITE_DEFAULTS) {
			// Tax inclusive pricing, but without tax.
			if (taxInclusive && itemTaxHandlingValue == TaxHandlingEnum.EXCLUDE) {
				return amount.subtract(taxAmount);
			}
			// Tax exclusive pricing, but with tax.
			if (!taxInclusive && itemTaxHandlingValue == TaxHandlingEnum.INCLUDE) {
				return amount.add(taxAmount);
			}
		}
		
		return amount;
	}
}
