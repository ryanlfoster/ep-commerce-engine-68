/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKeyAction;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * WishList.
 */
@Entity
@Table(name = WishListImpl.TABLE_NAME)
@DataCache(enabled = false)
@FetchGroups({
	@FetchGroup(
			name = FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS, 
			attributes = {
					@FetchAttribute(name = "allItems", recursionDepth = -1)
			}
		)
	})
public class WishListImpl extends AbstractShoppingListImpl implements WishList {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TWISHLIST";

	private long uidPk;
	
	/** List of ALL the objects in this Cart. */
	private List<ShoppingItem> allItems = new ArrayList<ShoppingItem>();

	/** Code of the {@link com.elasticpath.domain.store.Store} that this {@link WishList} belongs to, if any. */
	private String storeCode;
	
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
	 * Get all the items in the shopping cart, including the
	 * ShoppingCartItems, WishListItems, GiftCertificateItems.
	 * 
	 * @return all the items in the shopping cart
	 */
	@OneToMany(targetEntity = ShoppingItemImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)	
	@ElementJoinColumn(name = "WISHLIST_UID", updatable = false)
	@ElementForeignKey(updateAction = ForeignKeyAction.CASCADE)
	@ElementDependent
	@OrderBy("ordering")
	public List<ShoppingItem> getAllItems() {
		return allItems;
	}

	/**
	 * Set the items in the cart after loading a saved cart from the database.
	 * Since JPA can't separate the items between ShoppingCartItem, GiftCertificateItem,
	 * and WishListItem, we need to set them by ourself.
	 * 
	 * @param allItems the allItems to set
	 */
	public void setAllItems(final List<ShoppingItem> allItems) {
		this.allItems = allItems;
	}

	@Override
	public void addAllItems(final List<ShoppingItem> items) {
		if (CollectionUtils.isNotEmpty(items)) {
			for (ShoppingItem shoppingItem : items) {
				addItem(shoppingItem);
			}
		}
	}
	
	@Override
	public ShoppingItem addItem(final ShoppingItem item) {
		if (isNotContained(item)) {
			ShoppingItem shoppingItem = getBean(ContextIdNames.SHOPPING_ITEM);
			shoppingItem.setProductSku(item.getProductSku());
			getAllItems().add(shoppingItem);
		}
		return item;
	}
	
	/**
	 * if the item is already in the current wish list.
	 * 
	 * @param item the item
	 * @return true if the item is already in the current wish list
	 */
	//TODO need junit here...
	protected boolean isNotContained(final ShoppingItem item) {
		Collection<?> currentSkus = CollectionUtils.collect(getAllItems(), new Transformer() {
			public Object transform(final Object input) {
				return ((ShoppingItem) input).getProductSku().getSkuCode();
			}
		});
		return !currentSkus.contains(item.getProductSku().getSkuCode());
	}

	@Override
	public void removeItem(final String skuCode) {
		int indexOfRemovedItem = -1; 
		for (int i = 0; i < getAllItems().size(); i++) {
			ShoppingItem item = getAllItems().get(i);
			if (item.getProductSku().getSkuCode().equals(skuCode)) {
				indexOfRemovedItem = i;
				break;
			}
		}
		if (indexOfRemovedItem >= 0) {
			getAllItems().remove(indexOfRemovedItem);
		}
	}

	@Override
	public void removeItem(final long wishListItemUid) {
		int indexOfRemovedItem = -1; 
		for (int i = 0; i < getAllItems().size(); i++) {
			ShoppingItem item = getAllItems().get(i);
			if (item.getUidPk() == wishListItemUid) {
				indexOfRemovedItem = i;
				break;
			}
		}
		if (indexOfRemovedItem >= 0) {
			getAllItems().remove(indexOfRemovedItem);
		}
	}
	
	@Override
	@Column(name = "STORECODE")
	public String getStoreCode() {
		return this.storeCode;
	}

	@Override
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}		
	
}
