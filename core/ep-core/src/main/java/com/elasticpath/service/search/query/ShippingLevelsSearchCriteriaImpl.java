package com.elasticpath.service.search.query;

import java.io.Serializable;


/**
 * Represents criteria for shipping service levels filtering.
 */
public class ShippingLevelsSearchCriteriaImpl implements Serializable {
	/**
	 * Active state.
	 */
	public static final int STATE_ACTIVE = 1;
	/**
	 *  Inactive state.
	 */
	public static final int STATE_INACTIVE = 2;
	/**
	 * Undefined state.
	 */
	public static final int STATE_UNDEFINED = 0;

	private Long storeUid;

	private Long shippingRegionUid;

	private int state = STATE_UNDEFINED;

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	public Long getStoreUid() {
		return storeUid;
	}

	public void setStoreUid(final Long storeUid) {
		this.storeUid = storeUid;
	}

	public Long getShippingRegionUid() {
		return shippingRegionUid;
	}

	public void setShippingRegionUid(final Long shippingRegionUid) {
		this.shippingRegionUid = shippingRegionUid;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state value
	 */
	public int getState() {
		return this.state;
	}

	/**
	 * Sets the shipping service state.
	 *
	 * @param state the state value
	 */
	public void setState(final int state) {
		this.state = state;
	}

}
