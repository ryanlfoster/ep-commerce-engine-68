/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.sellingchannel.director.impl;

import java.util.Comparator;
import java.util.Date;

import com.elasticpath.inventory.InventoryDto;

/**
 * Gets the worse case of the inventory.
 */
public class InventoryWorseCaseComparator implements Comparator<InventoryDto> {

	@Override
	@SuppressWarnings({ "PMD.NPathComplexity" })
	public int compare(final InventoryDto inventory1, final InventoryDto inventory2) {
		if (inventory1 == null && inventory2 == null) {
			return 0;
		}

		if (inventory1 != null && inventory2 == null) {
			return 1;
		}

		if (inventory1 == null && inventory2 != null) {
			return -1;
		}

		final Date date1 = inventory1.getRestockDate();
		final Date date2 = inventory2.getRestockDate();

		if (date1 == null && date2 == null) {
			return 0;
		}

		if (date1 != null && date2 == null) {
			return 1;
		}

		if (date1 == null && date2 != null) {
			return -1;
		}

		return date1.compareTo(date2);
	}
}
