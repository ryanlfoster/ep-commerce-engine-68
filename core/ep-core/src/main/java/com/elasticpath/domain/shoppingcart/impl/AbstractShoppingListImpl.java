/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.shoppingcart.impl;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;

import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingList;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Represents a collection of shopping items.
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DataCache(enabled = false)
@FetchGroups({
	@FetchGroup(
			name = FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS, 
			attributes = {
					@FetchAttribute(name = "guid")
			}
		)
	})
public abstract class AbstractShoppingListImpl extends AbstractLegacyEntityImpl implements ShoppingList {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private Shopper shopper;
	
	private long shopperUid;

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
	 *
	 * @param shopper the shopper to set
	 */
	public void setShopper(final Shopper shopper) {
		this.shopper = shopper;
		setShopperUid(shopper.getUidPk());
	}

	/**
	 *
	 * @return the shopper
	 */
	@Transient
	public Shopper getShopper() {
		return shopper;
	}

	/**
	 * @param shopperUid the shopperUid to set
	 */
	public void setShopperUid(final long shopperUid) {
		this.shopperUid = shopperUid;
	}

	/**
	 * @return the shopperUid
	 */
	@Basic
	@Column(name = "SHOPPER_UID")
	public long getShopperUid() {
		return shopperUid;
	}
	
}
