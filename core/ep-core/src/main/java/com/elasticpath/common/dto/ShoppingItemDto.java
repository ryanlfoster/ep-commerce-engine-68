package com.elasticpath.common.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.misc.Money;

/**
 * Represents a snapshot of a {@code ProductBundle} which contains MultiSKU constituent items by looking at
 * user's SKU selection.
 * 
 * A ShoppingItemDto is a node in a possible tree of ShoppingItemDtos, and it is a mirror representation
 * of its corresponding {@code ProductBundle}.
 */
public class ShoppingItemDto implements Dto {

	private static final long serialVersionUID = 1L;

	private String skuCode;
	private final List<ShoppingItemDto> constituents = new ArrayList<ShoppingItemDto>();
	private int quantity;
	private Map<String, String> itemFields;

	private long shoppingItemUidPk;
	private String productCode;

	private Price price;
	private Money total;
	private boolean selected = true;

	private boolean productSkuConstituent;

	/**
	 * Returns the product code for the related product.
	 * @return the product code.
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * 
	 * @param productCode The product code to set.
	 */
	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}



	/**
	 * SKU Code of the current node.
	 * 
	 * @param skuCode
	 *            the sku code to be set
	 * @param quantity
	 *            the quantity of the item. Can be zero for bundle constituents.
	 */
	public ShoppingItemDto(final String skuCode, final int quantity) {
		this.skuCode = skuCode;
		this.quantity = quantity;
		itemFields = Collections.emptyMap();
	}



	/**
	 * Adds given {@code ShoppingItemDto} representing a bundle constituent to the child nodes.
	 * 
	 * @param child The ShoppingItemDto to be added.
	 */
	public void addConstituent(final ShoppingItemDto child) {
		constituents.add(child);
	}

	/**
	 * Gets all constituents.
	 * 
	 * @return list of {@link ShoppingItemDto}.
	 */
	public List<ShoppingItemDto> getConstituents() {
		return Collections.unmodifiableList(constituents);
	}

	/**
	 * @return the SKU code
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * @param skuCode The skuCode to set
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * @param quantity The quantity to set.
	 */
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return The quantity.
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param itemFields The item fields to set.
	 */
	public void setItemFields(final Map<String, String> itemFields) {
		this.itemFields = itemFields;
	}

	/**
	 * @return The itemFields
	 */
	public Map<String, String> getItemFields() {
		return itemFields;
	}

	/**
	 * @return The uidPk of the existing shopping item or null if this is a new shopping item.
	 */
	public long getShoppingItemUidPk() {
		return shoppingItemUidPk;
	}

	/**
	 * @param shoppingItemUidPk The uidPk of the existing shopping item.
	 */
	public void setShoppingItemUidPk(final long shoppingItemUidPk) {
		this.shoppingItemUidPk = shoppingItemUidPk;
	}

	/**
	 * Getter for {@link Price} of the {@link com.elasticpath.domain.shoppingcart.ShoppingItem}.
	 * @return {@link Price}.
	 */
	public Price getPrice() {
		return price;
	}

	/**
	 * Setter for {@link Price} of the {@link com.elasticpath.domain.shoppingcart.ShoppingItem}.
	 * @param price {@link Price}.
	 */
	public void setPrice(final Price price) {
		this.price = price;
	}

	/**
	 * Getts for the total of the {@link com.elasticpath.domain.shoppingcart.ShoppingItem}.
	 * @return money {@link Money}.
	 */
	public Money getTotal() {
		return total;
	}

	/**
	 * Setter for the total of the {@link com.elasticpath.domain.shoppingcart.ShoppingItem}.
	 * @param total {@link Money}.
	 */
	public void setTotal(final Money total) {
		this.total = total;
	}

	/**
	 * 
	 * @param selected if true then this item has been selected by the user.
	 */
	public void setSelected(final boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return true if this item has been selected by the user.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set productSkuConstituent.
	 * @param productSkuConstituent the flag
	 */
	public void setProductSkuConstituent(final boolean productSkuConstituent) {
		this.productSkuConstituent = productSkuConstituent;
	}

	/**
	 * @return <code>true</code> if this dto belongs to a bundle constituent which is a specific sku of a multi sku product.
	 */
	public boolean isProductSkuConstituent() {
		return productSkuConstituent;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder
			.append(price)
			.append(productCode)
			.append(productSkuConstituent)
			.append(quantity)
			.append(selected)
			.append(shoppingItemUidPk)
			.append(skuCode)
			.append(total);
		for (ShoppingItemDto constituentDto : constituents) {
			builder.append(constituentDto);
		}
		for (Map.Entry<String, String> entry : itemFields.entrySet()) {
			builder.append(entry.getKey()).append(entry.getValue());
		}
		return builder.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ShoppingItemDto)) {
			return false;
		}
		ShoppingItemDto other = (ShoppingItemDto) obj;
		return new EqualsBuilder()
			.append(constituents, other.constituents)
			.append(itemFields, other.itemFields)
			.append(price, other.price)
			.append(productCode, other.productCode)
			.append(productSkuConstituent, other.productSkuConstituent)
			.append(quantity, other.quantity)
			.append(selected, other.selected)
			.append(shoppingItemUidPk, other.shoppingItemUidPk)
			.append(skuCode, other.skuCode)
			.append(total, other.total)
			.isEquals();
	}
}
