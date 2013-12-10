/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.inventory.InventoryCapabilities;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.catalog.ProductInventoryManagementService;

/**
 * Product availability type sets whether a product could be on sale 
 * depending on the available quantities in stock.
 * 
 * Note: Changing the name of these enums should lead a change in the velocity templates. 
 */
public enum AvailabilityCriteria {
	
	/**
	 * A product is available only if there is in stock quantity.  
	 */
	AVAILABLE_WHEN_IN_STOCK {
		@Override
		public boolean hasSufficientUnallocatedQty(final ProductInventoryManagementService service, final ProductSku productSku, 
				final long warehouseUid, final int quantity) {
			return service.hasSufficientInventory(productSku, warehouseUid, quantity);
		}
		
		@Override
		public boolean hasSufficientInventory(final InventoryDto inventoryDto, final int quantity) {
			return inventoryDto.getAvailableQuantityInStock() >= quantity;		
		}
	},
	
	/**
	 * A product is available for pre-order even if there is no stock availability.
	 * Usually a date when the product is going to be launched should be set.
	 */
	AVAILABLE_FOR_PRE_ORDER {
		@Override
		public int handlePreOrBackOrderAllocation(final ProductInventoryManagementService service, final ProductSku productSku, final int quantity) {
			return AVAILABLE_FOR_BACK_ORDER.handlePreOrBackOrderAllocation(service, productSku, quantity);
		}
		
		@Override
		public boolean hasSufficientUnallocatedQty(final ProductInventoryManagementService service, final ProductSku productSku, 
				final long warehouseUid, final int quantity) {
			return AVAILABLE_FOR_BACK_ORDER.hasSufficientUnallocatedQty(service, productSku, warehouseUid, quantity);
		}
		
		@Override
		public int handlePreBackOrderStockAllocation(final ProductInventoryManagementService service, final ProductSku productSku, 
				final long warehouseUid, final int quantity) {
			return AVAILABLE_FOR_BACK_ORDER.handlePreBackOrderStockAllocation(service, productSku, warehouseUid, quantity);
		}
		
		@Override
		public boolean hasSufficientInventory(final InventoryDto inventoryDto, final int quantity) {
			return AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK.hasSufficientInventory(inventoryDto, quantity);		
		}
	}, 
	
	/**
	 * The product is available for sale even if quantities have been exhausted. 
	 */
	AVAILABLE_FOR_BACK_ORDER {
		@Override
		public int handlePreOrBackOrderAllocation(final ProductInventoryManagementService service, final ProductSku productSku, final int quantity) {
			final boolean supportsPreOrBackOrderLimit = service.getInventoryCapabilities().supports(InventoryCapabilities.PRE_OR_BACK_ORDER_LIMIT);
			final PreOrBackOrderDetails preOrBackOrderDetails = service.getPreOrBackOrderDetails(productSku.getSkuCode());
			final int quantityToPreOrBackOrder = preOrBackOrderDetails.getQuantity() + quantity;
			final boolean hasOrderLimit = preOrBackOrderDetails.getLimit() > 0;
			// positive
			if (supportsPreOrBackOrderLimit && hasOrderLimit && quantityToPreOrBackOrder > preOrBackOrderDetails.getLimit()) {
				throw new InsufficientInventoryException("Order limit reached");
			}
			productSku.setPreOrBackOrderedQuantity(quantityToPreOrBackOrder);
			
			return quantity;
		}
		
		@Override
		public boolean hasSufficientUnallocatedQty(final ProductInventoryManagementService service, final ProductSku productSku, 
				final long warehouseUid, final int quantity) {
			final boolean supportsPreOrBackOrderLimit = service.getInventoryCapabilities().supports(InventoryCapabilities.PRE_OR_BACK_ORDER_LIMIT);
			final PreOrBackOrderDetails preOrBackOrderDetails = service.getPreOrBackOrderDetails(productSku.getSkuCode());
			final boolean hasOrderLimit = preOrBackOrderDetails.getLimit() > 0;
			
			if (supportsPreOrBackOrderLimit && hasOrderLimit) {
				final int unallocatedPreBackOrderQty = preOrBackOrderDetails.getLimit() - preOrBackOrderDetails.getQuantity();
				final int neededInventoryQty = quantity - unallocatedPreBackOrderQty;
				if (neededInventoryQty > 0) {
					return service.hasSufficientInventory(productSku, warehouseUid, neededInventoryQty);
				} 
				return true;				
			} 
			return true;
		}
		
		@Override
		public int handlePreBackOrderStockAllocation(final ProductInventoryManagementService service, final ProductSku productSku, 
				final long warehouseUid, final int quantity) {
			final int availableQtyInStock = service.getAvailableInStockQty(productSku, warehouseUid);
			if (availableQtyInStock <= quantity) {
				return availableQtyInStock;
			}
			return quantity;
		}
		
		@Override
		public boolean hasSufficientInventory(final InventoryDto inventoryDto, final int quantity) {
			return AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK.hasSufficientInventory(inventoryDto, quantity);		
		}

	},
	
	
	/**
	 * The product does not have an order limit. 
	 */
	ALWAYS_AVAILABLE {
		@Override
		public boolean hasSufficientUnallocatedQty(final ProductInventoryManagementService service, final ProductSku productSku, 
				final long warehouseUid, final int quantity) {
			return true;
		}
		
		@Override
		public boolean hasSufficientInventory(final InventoryDto inventoryDto, final int quantity) {
			return true;
		}
	};
	
	/**
	 * Handles pre- or back-order allocation.
	 * 
	 * @param service {@link ProductInventoryManagementService} instance
	 * @param sku product sku to allocate
	 * @param qty quantity to allocate
	 * @return allocated qty
	 */
	public int handlePreOrBackOrderAllocation(final ProductInventoryManagementService service, final ProductSku sku, final int qty) {
		return 0;
	}
	
	/**
	 * Checks the amount of unallocated pre/back order limit (if applicable) and inventory state of the given product SKU.
	 * 
	 * @param service {@link ProductInventoryManagementService} instance
	 * @param productSku product sku to allocate
	 * @param warehouseUid warehouse ID
	 * @param quantity qty to check
	 * @return true if sku has enough unallocated qty in the given warehouse. False otherwise
	 */
	public boolean hasSufficientUnallocatedQty(final ProductInventoryManagementService service, final ProductSku productSku, 
			final long warehouseUid, final int quantity) {
		return false;
	}
	
	/**
	 * If pre/back order product there has to be made calculation on the available products in stock in
	 * orde to avoid insufficient inventory exception when we have enough pre/back order limit.
	 * 
	 * @param service {@link ProductInventoryManagementService} instance
	 * @param productSku product sku to allocate
	 * @param warehouseUid warehouse ID
	 * @param quantity qty to check
	 * 
	 * @return the quantity to be allocated in the inventory
	 */
	public int handlePreBackOrderStockAllocation(final ProductInventoryManagementService service, final ProductSku productSku, 
			final long warehouseUid, final int quantity) {
		return quantity;
	}
	
	/**
	 * Determines if there's enough Inventory.
	 * 
	 * @param inventoryDto inventory dto
	 * @param quantity qty to allocate
	 * @return true if enough, otherwise - false
	 */
	public boolean hasSufficientInventory(final InventoryDto inventoryDto, final int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("Cannot check for negative quantity");
		}
		return false;
	}
}
