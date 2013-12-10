package com.elasticpath.sellingchannel.director.impl;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.impl.InventoryDtoImpl;

/** */
public class InventoryWorseCaseComparatorTest {
	private static final Date OCT_TWENTY_EIGTH = createDate(2009, 10, 28);

	private static final Date OCT_TWENTY_SEVEN = createDate(2009, 10, 27);

	private static Date createDate(final int year, final int month, final int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, date);
		return calendar.getTime();
	}

	private final InventoryWorseCaseComparator comparator = new InventoryWorseCaseComparator();

	/** */
	@Test
	public void testCompareOneNullInventoryOneNonNullInventory() {
		InventoryDto inventory1 = null;
		InventoryDto inventory2 = new InventoryDtoImpl();

		int result = comparator.compare(inventory1, inventory2);
		assertEquals(-1, result);
	}

	/** */
	@Test
	public void testCompareTwoNonNullInventories() {
		InventoryDto inventory1 = new InventoryDtoImpl();
		inventory1.setRestockDate(OCT_TWENTY_EIGTH);

		InventoryDto inventory2 = new InventoryDtoImpl();
		inventory2.setRestockDate(OCT_TWENTY_SEVEN);

		int result = comparator.compare(inventory1, inventory2);
		assertEquals(1, result);
	}

	/** */
	@Test
	public void testCompareTwoNullInventories() {
		InventoryDto inventory1 = null;
		InventoryDto inventory2 = null;

		int result = comparator.compare(inventory1, inventory2);
		assertEquals(0, result);
	}

	/** */
	@Test
	public void testCompareTwoNullDate() {
		InventoryDto inventory1 = new InventoryDtoImpl();
		InventoryDto inventory2 = new InventoryDtoImpl();

		int result = comparator.compare(inventory1, inventory2);
		assertEquals(0, result);
	}

	/** */
	@Test
	public void testCompareOneNullDateOneNonNullDate() {
		InventoryDto inventory1 = new InventoryDtoImpl();
		InventoryDto inventory2 = new InventoryDtoImpl();
		inventory2.setRestockDate(OCT_TWENTY_EIGTH);

		int result = comparator.compare(inventory1, inventory2);
		assertEquals(-1, result);
	}

	/** */
	@Test
	public void testGetWorse() {
		InventoryDto inventory1 = new InventoryDtoImpl();
		inventory1.setRestockDate(OCT_TWENTY_EIGTH);

		InventoryDto inventory2 = new InventoryDtoImpl();
		inventory2.setRestockDate(OCT_TWENTY_SEVEN);

		InventoryDto result = InventoryWorseCaseUtil.getWorse(inventory1, inventory2);
		assertEquals(inventory1, result);
	}
}
