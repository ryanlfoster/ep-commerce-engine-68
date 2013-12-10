package com.elasticpath.domain.order;

import com.elasticpath.domain.catalog.InventoryEventType;

/**
 * Allocation event type enumeration.
 */
public enum AllocationEventType {

	/**
	 * A new order has been placed and inventory is to be allocated to it.  
	 */
	ORDER_PLACED {
		@Override
		public InventoryEventType translateAllocationEvent(final int quantity) {
			return ORDER_ADJUSTMENT_ADDSKU.translateAllocationEvent(quantity);
		}
	},
	
	/**
	 * Order adjustment with new sku added before shipment is packed.  
	 */
	ORDER_ADJUSTMENT_ADDSKU {
		@Override
		public InventoryEventType translateAllocationEvent(final int quantity) {
			return InventoryEventType.STOCK_ALLOCATE;
		}
	},
	
	/**
	 * Order adjustment with sku removed before shipment is packed.  
	 */
	ORDER_ADJUSTMENT_REMOVESKU {
		@Override
		public InventoryEventType translateAllocationEvent(final int quantity) {
			return InventoryEventType.STOCK_DEALLOCATE;
		}
	},
	
	/**
	 * Order adjustment with sku quantity changed before shipment is packed.  
	 */
	ORDER_ADJUSTMENT_CHANGEQTY {
		@Override
		public InventoryEventType translateAllocationEvent(final int quantity) {
			if (quantity >= 0) {
				return InventoryEventType.STOCK_ALLOCATE;
			} 
			return InventoryEventType.STOCK_DEALLOCATE;
		}	
	},
	
	/**
	 * An Order has been canceled and quantity is to be deallocated.  
	 */
	ORDER_CANCELLATION {
		@Override
		public InventoryEventType translateAllocationEvent(final int quantity) {
			return ORDER_ADJUSTMENT_REMOVESKU.translateAllocationEvent(quantity);
		}
	},
	
	/**
	 * Order shipment has been released.
	 */
	ORDER_SHIPMENT_COMPLETED {
		@Override
		public InventoryEventType translateAllocationEvent(final int quantity) {
			return InventoryEventType.STOCK_RELEASE;
		}
	};

	/**
	 * Translates allocation event into the inventory type event.
	 * @param quantity quantity
	 * @return appropriate {@link InventoryEventType}
	 */
	public InventoryEventType translateAllocationEvent(final int quantity) {
		return InventoryEventType.UNKNOWN;
	}

}
