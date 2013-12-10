package com.elasticpath.service.order;

import java.util.List;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.AllocationEventType;
import com.elasticpath.domain.order.AllocationResult;
import com.elasticpath.domain.order.AllocationStatus;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.service.EpPersistenceService;

/**
 * 
 */
public interface AllocationService extends EpPersistenceService {

	/**
	 * Retrieves the allocation status for this product SKU and store for the specified quantityToAllocate.
	 * 
	 * @param productSku the product SKU
	 * @param warehouseUid the warehouse UID
	 * @param quantityToAllocate the quantity to be checked
	 * @return {@link AllocationStatus}
	 */
	AllocationStatus getAllocationStatus(ProductSku productSku, long warehouseUid, int quantityToAllocate);

	/**
	 * Returns true if there is sufficient unallocated quantity in the inventory or on pre/back order.
	 * 
	 * @param productSku the product SKU
	 * @param warehouseUid the warehouse UID
	 * @param quantity the quantity of items to check for availability
	 * @return true if <code>quantity</code> items can be sold
	 */
	boolean hasSufficientUnallocatedQty(ProductSku productSku, long warehouseUid, int quantity);

	/**
	 * Returns the unallocated quantity in the inventory.
	 * 
	 * @param productSku the product SKU
	 * @param warehouseUid the warehouse UID
	 * @return the unallocated in-stock quantity
	 */
	int getUnallocatedQuantityInStock(ProductSku productSku, long warehouseUid);

	/**
	 * Get quantity awaiting allocation for given inventory.
	 * 
	 * @param skuCode the product SKU's skuCode
	 * @param warehouseUid the warehouse UID
	 * @return quantity awaiting allocation
	 */
	int getQuantityAwaitingAllocation(String skuCode, long warehouseUid);
	
	/**
	 *
	 * @param orderSku the order SKU
	 * @param eventType the event type
	 * @param eventOriginator the originator of this event 
	 * @param quantity the quantity
	 * @param reason the reason
	 * @return the allocation result
	 */
	AllocationResult processAllocationEvent(
			OrderSku orderSku, 
			AllocationEventType eventType,
			String eventOriginator,
			int quantity,
			String reason
			);
	
	/**
	 * Finds all the order SKUs referencing product SKU with <code>skuCode</code>
	 * that belong to an order having reference to a store that utilizes warehouse
	 * with  <code>warehouseUid</code>. The order SKUs have to belong to a shipment 
	 * with <code>shipmentStatus</code>.
	 * 
	 * @param skuCode the SKU code
	 * @param warehouseUid the warehouse UID
	 * @param shipmentStatus the shipment status 
	 * @return a list of order SKUs
	 */
	List<OrderSku> findOrderSkusWithCodeAndStatus(String skuCode, long warehouseUid, 
			OrderShipmentStatus shipmentStatus);


}
