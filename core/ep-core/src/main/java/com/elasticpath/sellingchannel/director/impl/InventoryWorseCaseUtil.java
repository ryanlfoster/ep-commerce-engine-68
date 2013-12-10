/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.sellingchannel.director.impl;

import com.elasticpath.inventory.InventoryDto;

/**
 * Inventory Worse Case Util.
 */
public final class InventoryWorseCaseUtil {

	private static final InventoryWorseCaseComparator INSTANCE = new InventoryWorseCaseComparator();

	/**
	 * Static util.
	 */
	private InventoryWorseCaseUtil() {
	}

	/**
	 * Gets the worse case of two inventories.
	 * @param inventory1 {@link com.elasticpath.inventory.InventoryDto}
	 * @param inventory2 {@link com.elasticpath.inventory.InventoryDto}
	 * @return the worse {@link com.elasticpath.inventory.InventoryDto}
	 */
	public static InventoryDto getWorse(final InventoryDto inventory1, final InventoryDto inventory2) {
		final int result = INSTANCE.compare(inventory1, inventory2);
		if (result > 0) {
			return inventory1;
		}

		return inventory2;
	}
}
