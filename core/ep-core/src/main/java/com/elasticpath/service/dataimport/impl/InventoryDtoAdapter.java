package com.elasticpath.service.dataimport.impl;

import java.util.Date;

import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.impl.InventoryDtoImpl;

/**
 * Adapts an InventoryDto to Inventory allowing InventoryDto to act like Inventory.
 * 
 */
@SuppressWarnings("serial")
public class InventoryDtoAdapter implements Inventory {

	@SuppressWarnings("PMD")
	private InventoryDtoImpl dto;

	/**
	 * Adapts a dto to an entity.
	 * @param dto The dto.
	 */
	public InventoryDtoAdapter(final InventoryDto dto) {
		this.dto = (InventoryDtoImpl) dto;
	}

	public InventoryDto getDto() {
		return dto;
	}

	@Override
	public String getGuid() {
		return null;
	}

	@Override
	public void setGuid(final String guid) {
		notPersistable();
	}

	private void notPersistable() {
		throw new UnsupportedOperationException("InventoryDtoAdapter is not persistable");
	}

	@Override
	public long getUidPk() {
		return 0;
	}

	@Override
	public void setUidPk(final long uidPk) {
		notPersistable();
	}

	@Override
	public boolean isPersisted() {
		return false;
	}

	@Override
	public void initialize() {
		notPersistable();
	}
	
	@Override
	public int getQuantityOnHand() {
		return dto.getQuantityOnHand();
	}

	@Override
	public void setQuantityOnHand(final int quantityOnHand) {
		dto.setQuantityOnHand(quantityOnHand);
	}

	@Override
	public int getReservedQuantity() {
		return dto.getReservedQuantity();
	}

	@Override
	public void setReservedQuantity(final int reservedQuantity) {
		dto.setReservedQuantity(reservedQuantity);
	}

	@Override
	public int getReorderMinimum() {
		return dto.getReorderMinimum();
	}

	@Override
	public void setReorderMinimum(final int reorderMinimum) {
		dto.setReorderMinimum(reorderMinimum);
	}

	@Override
	public void setRestockDate(final Date restockDate) {
		dto.setRestockDate(restockDate);
	}

	@Override
	public Date getRestockDate() {
		return dto.getRestockDate();
	}

	@Override
	public int getReorderQuantity() {
		return dto.getReorderQuantity();
	}

	@Override
	public void setReorderQuantity(final int reorderQuantity) {
		dto.setReorderQuantity(reorderQuantity);
	}

	@Override
	public Long getWarehouseUid() {
		return dto.getWarehouseUid();
	}

	@Override
	public void setWarehouseUid(final Long warehouseUid) {
		dto.setWarehouseUid(warehouseUid);
	}

	@Override
	public String getSkuCode() {
		return dto.getSkuCode();
	}

	@Override
	public void setSkuCode(final String skuCode) {
		dto.setSkuCode(skuCode);
	}

	@Override
	public int getAllocatedQuantity() {
		return dto.getAllocatedQuantity();
	}

	@Override
	public void setAllocatedQuantity(final int allocated) {
		dto.setAllocatedQuantity(allocated);
	}

}