package com.elasticpath.inventory.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;

/**
 * Converts domain and dto objects.
 */
public class InventoryDtoAssembler {

	private static final String INVENTORY_ASSEMBLER_ERROR = "Inventory assembler error.";

	/**
	 *
	 * @param <T> Key.
	 * @param inventoryMap map of domain
	 * @return map of dto.
	 */
	public <T> Map<T, InventoryDto> assembleDtoMapFromDomain(
			final Map<T, Inventory> inventoryMap) {

		Map<T, InventoryDto> res = new java.util.HashMap<T, InventoryDto>();
		for (Map.Entry<T, Inventory> e : inventoryMap.entrySet()) {
			InventoryDto dto = assembleDtoFromDomain(e.getValue());
			res.put(e.getKey(), dto);
		}
		return res;
	}

	/**
	 * Assembler.
	 *
	 * @param inventory The Inventory domain object. If null then returns null.
	 * @return The dto object.
	 */
	public InventoryDto assembleDtoFromDomain(final Inventory inventory) {
		if (inventory == null) {
			return null;
		}

		InventoryDtoImpl dto = new InventoryDtoImpl();
		try {
			PropertyUtils.copyProperties(dto, inventory);
		} catch (IllegalAccessException e) {
			throw new EpSystemException(INVENTORY_ASSEMBLER_ERROR, e);
		} catch (InvocationTargetException e) {
			throw new EpSystemException(INVENTORY_ASSEMBLER_ERROR, e);
		} catch (NoSuchMethodException e) {
			throw new EpSystemException(INVENTORY_ASSEMBLER_ERROR, e);
		}
		return dto;
	}

	/**
	 * Assembler.
	 *
	 * @param inventory The Inventory domain object. If null then returns null.
	 * @param inventoryJournalRollup The inventoryJournalRollup. If null then returns null.
	 * @return The dto object.
	 */
	public InventoryDto assembleDtoFromDomain(final Inventory inventory, final InventoryJournalRollup inventoryJournalRollup) {
		if (inventory == null || inventoryJournalRollup == null) {
			return null;
		}

		InventoryDtoImpl dto = (InventoryDtoImpl) assembleDtoFromDomain(inventory);
		dto.setAllocatedQuantity(dto.getAllocatedQuantity() + inventoryJournalRollup.getAllocatedQuantityDelta());
		dto.setQuantityOnHand(dto.getQuantityOnHand() + inventoryJournalRollup.getQuantityOnHandDelta());
		return dto;
	}

	/**
	 *
	 * @param inventoryDto dto object.
	 * @return doamin object.
	 */
	public Inventory assembleDomainFromDto(final InventoryDto inventoryDto) {
		if (inventoryDto == null) {
			return null;
		}

		Inventory domain = new InventoryImpl();
		try {
			PropertyUtils.copyProperties(domain, inventoryDto);
		} catch (IllegalAccessException e) {
			throw new EpSystemException(INVENTORY_ASSEMBLER_ERROR, e);
		} catch (InvocationTargetException e) {
			throw new EpSystemException(INVENTORY_ASSEMBLER_ERROR, e);
		} catch (NoSuchMethodException e) {
			throw new EpSystemException(INVENTORY_ASSEMBLER_ERROR, e);
		}
		return domain;
	}

	/**
	 * Copy fields from inventory dto to domain object.
	 * @param domain Inventory domain object.
	 * @param dto Inventory dto object.
	 */
	public void copyFieldsFromDtoToDomain(final Inventory domain, final InventoryDto dto) {
		domain.setReorderMinimum(dto.getReorderMinimum());
		domain.setReorderQuantity(dto.getReorderQuantity());
		domain.setReservedQuantity(dto.getReservedQuantity());
		domain.setRestockDate(dto.getRestockDate());
	}
}
