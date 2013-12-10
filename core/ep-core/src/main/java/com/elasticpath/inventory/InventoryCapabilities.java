package com.elasticpath.inventory;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * A list of inventory capabilities.
 */
public class InventoryCapabilities extends AbstractExtensibleEnum<InventoryCapabilities> {
	
	/** */
	private static final long serialVersionUID = 1L;

	/** Ordinal static for inventory allocation tracked type. */
	public static final int INVENTORY_ALLOCATION_TRACKED_ORDINAL = 0;

	/** Declaration that inventory allocation is tracked. */
	public static final InventoryCapabilities INVENTORY_ALLOCATION_TRACKED = 
		new InventoryCapabilities(INVENTORY_ALLOCATION_TRACKED_ORDINAL, "INVENTORY_ALLOCATION_TRACKED");

	/** Ordinal static for inventory pre or back order limit type. */
	public static final int PRE_OR_BACK_ORDER_LIMIT_ORDINAL = 1;

	/** Declaration that pre or back order limit is supported. */
	public static final InventoryCapabilities PRE_OR_BACK_ORDER_LIMIT = 
		new InventoryCapabilities(PRE_OR_BACK_ORDER_LIMIT_ORDINAL, "PRE_OR_BACK_ORDER_LIMIT");

	/**
	 * Create a new enum value.
	 * 
	 * @param ordinal The unique ordinal value.
	 * @param name The named value for this extensible enum.
	 */
	protected InventoryCapabilities(final int ordinal, final String name) {
		super(ordinal, name, InventoryCapabilities.class);
	}
	
	@Override
	protected Class<InventoryCapabilities> getEnumType() {
		return InventoryCapabilities.class;
	}
}
