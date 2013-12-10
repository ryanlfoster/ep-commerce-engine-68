/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.order.AllocationResult;
import com.elasticpath.service.catalog.ProductInventoryManagementService;

/**
 * InventoryEventType defines what kind of changes happened to the <code>Inventory</code>.
 * These event types are also used to indicate to the inventory service the
 * type of operation that is to be performed on a given Inventory object.
 * 
 */
public enum InventoryEventType {
	
	/**
	 * This event type is used for legacy data for which the
	 * type of the inventory event is unknown. This value should
	 * not be used by Java clients.   
	 */
	UNKNOWN,
	
	/**
	 * This type is used to indicate that new stock has been received
	 * through the stock upload feature.   
	 */
	STOCK_RECEIVED,
	
	/**
	 * Stock adjustment manually.  
	 */
	STOCK_ADJUSTMENT {
		@Override
		public int preProcessInventoryCommand(final ProductInventoryManagementService service, final ProductSku productSku, final long warehouseUid,
				final int quantity, final int allocatedQuantity) {
			if (quantity < 0) {
				return handlePreOrBackOrderDeallocation(service, productSku, -quantity);
			}
			return quantity;
		}	
	},

	/**
	 * Stock allocation.  
	 */
	STOCK_ALLOCATE {
		@Override
		public int preProcessInventoryCommand(final ProductInventoryManagementService service, final ProductSku productSku, final long warehouseUid,
				final int quantity, final int allocatedQuantity) {
			final Product product = productSku.getProduct();			
			return product.getAvailabilityCriteria().handlePreBackOrderStockAllocation(service,	productSku, warehouseUid, quantity);
		}
		
		@Override
		public void postProcessInventoryCommand(final ProductInventoryManagementService service, final ProductSku productSku, final int quantity, 
				final AllocationResult allocationResult) {
			int inventoryQtyAllocated = 0;
			
			if (allocationResult.getInventoryResult() != null) {
				inventoryQtyAllocated = allocationResult.getInventoryResult().getQuantity();
			}
			final int preBackOrderQty = quantity - inventoryQtyAllocated;
			
			productSku.getProduct().getAvailabilityCriteria().handlePreOrBackOrderAllocation(service, productSku, preBackOrderQty);
			
			allocationResult.setQuantityAllocatedOnPreOrBackOrder(preBackOrderQty);
		}
	},

	/**
	 * Stock deallocation.  
	 */
	STOCK_DEALLOCATE {
		@Override
		public int preProcessInventoryCommand(final ProductInventoryManagementService service, final ProductSku productSku, final long warehouseUid,
				final int quantity, final int allocatedQuantity) {
			return Math.min(quantity, allocatedQuantity);
		}
		
		@Override
		public void postProcessInventoryCommand(final ProductInventoryManagementService service, final ProductSku productSku, final int quantity, 
				final AllocationResult allocationResult) {
			
			int preBackOrderQty = 0;
			if (allocationResult.getInventoryResult() != null) {
				preBackOrderQty = quantity - allocationResult.getInventoryResult().getQuantity();
			}
			// quantity might be negative as well if deallocation comes from order 
			handlePreOrBackOrderDeallocation(service, productSku, Math.max(0, preBackOrderQty));
		}
	},
	
	/**
	 * Stock release. 
	 */
	STOCK_RELEASE;
	
	/**
	 * Deallocates as much as possible quantity from the pre/back ordered quantity field.
	 * 
	 * @param service product inventory management service
	 * @param productSku product sku
	 * @param warehouseUid warehouse uid
	 * @param quantity quantity
	 * @param allocatedQuantity allocated qty
	 * @return the quantity that could not be allocated
	 */
	public int preProcessInventoryCommand(final ProductInventoryManagementService service, final ProductSku productSku, final long warehouseUid, 
			final int quantity, final int allocatedQuantity) {
		return quantity;
	}

	/**
	 * Allocates or deallocates inventory.
	 * 
	 * @param service product inventory management service
	 * @param productSku product sku
	 * @param quantity quantity
	 * @param allocationResult allocation result
	 */
	public void postProcessInventoryCommand(final ProductInventoryManagementService service, final ProductSku productSku, final int quantity, 
			final AllocationResult allocationResult) {
		//do nothing by default
	}
	
	/**
	 * Deallocates as much quantity as possible for pre/back ordered products. If the quantity to be deallocated
	 * is more than the quantity on pre/back order then only the quantity that is possible to be deallocated is 
	 * processed. The remainder is always returned.
	 *  
	 * @return the remainder from the calculation. Could be negative in case there was not enough qty to deallocate
	 */
	private static int handlePreOrBackOrderDeallocation(final ProductInventoryManagementService service, final ProductSku productSku, 
			final int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("Cannot deallocate negative quantity.");
		}
		final PreOrBackOrderDetails preOrBackOrderDetails = service.getPreOrBackOrderDetails(productSku.getSkuCode());
		int deallocateRemainder = preOrBackOrderDetails.getQuantity() - quantity;
		if (deallocateRemainder < 0) {
			productSku.setPreOrBackOrderedQuantity(0); // set pre|back order quantity to zero as it has all been deallocated
			deallocateRemainder = -deallocateRemainder; // set it to positive integer
		} else {
			productSku.setPreOrBackOrderedQuantity(deallocateRemainder);
		}
		return deallocateRemainder;
	}
	
}
