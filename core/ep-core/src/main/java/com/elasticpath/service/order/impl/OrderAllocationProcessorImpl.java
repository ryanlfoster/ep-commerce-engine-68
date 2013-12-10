/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.PostLoadRecalculate;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.service.catalog.InventoryListener;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.order.OrderAllocationProcessor;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.store.WarehouseService;

/**
 * Order allocation processor responsible for allocating all the quantities
 * that have not been allocated so far.
 */
public class OrderAllocationProcessorImpl extends AbstractEpDomainImpl implements OrderAllocationProcessor {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Inventory listener implementation.
	 */
	private final InventoryListener inventoryListener = new InventoryListener() {

		/**
		 * Notified on inventory change.
		 *
		 * @param skuCode the SKU code
		 * @param warehouseCode the warehouse code
		 */
		public void newInventory(final String skuCode, final String warehouseCode) {
			getOrderAllocationProcessor().processOutstandingOrders(skuCode, warehouseCode);
		}

	};

	private OrderService orderService;
	private ProductSkuService productSkuService;
	private WarehouseService warehouseService;
	private AllocationService allocationService;

	private ProductInventoryManagementService productInventoryManagementService;

	/**
	 * Allocates stock to outstanding orders that are awaiting inventory
	 * of the given SKU. Checks the SKU's inventory levels in the given warehouse to
	 * see if there is any quantity available, and allocates units to the waiting Orders.
	 *
	 * @param skuCode code for the SKU whose inventory should be checked
	 * @param warehouseCode code for the warehouse in which the SKU's inventory should be checked
	 */
	public void processOutstandingOrders(final String skuCode, final String warehouseCode) {
		long warehouseUidPk = getWarehouseUidFromCode(warehouseCode);
		ProductSku productSku = getProductSkuService().findBySkuCode(skuCode);
		int totalAvailableQuantity = getAvailableSkuQuantityInWarehouse(productSku, warehouseUidPk);

		int allocated = 0;
		Map<Long, Order> ordersToUpdate = new HashMap<Long, Order>();

		List<OrderSku> orderSkuList = getOrderSkusAwaitingInventory(skuCode, warehouseUidPk);

		// Ensure shipment recalculations are done as we just loaded the shipments (via the skus).
		for (OrderSku orderSku : orderSkuList) {
			((PostLoadRecalculate) orderSku.getShipment()).recalculateAfterLoad();
		}

		for (OrderSku orderSku : orderSkuList) {
			if (!orderSku.isAllocated()) {
				int quantityToAllocate =
					calculateAdditionalQuantityToAllocate(totalAvailableQuantity, orderSku.getQuantity(), orderSku.getAllocatedQuantity());
				// allocate as many units of the product sku as we can (up to the amount that were ordered) to the order sku
				orderSku.setAllocatedQuantity(orderSku.getAllocatedQuantity() + quantityToAllocate);
				allocated += quantityToAllocate;

				Order order = orderSku.getShipment().getOrder();
				//actually allocate the warehouse units of the sku to the orderSku's Order.
				allocateInventory(productSku, order, warehouseUidPk, quantityToAllocate);
				//keep track of the affected order so that we can save it later
				ordersToUpdate.put(order.getUidPk(), order);
				totalAvailableQuantity -= quantityToAllocate;
				if (totalAvailableQuantity == 0) {
					break;
				}
			}
		}
		// update the affected orders. This cannot be performed as we go because if a single order has multiple affected
		// OrderSkus then the persistence layer may complain that we're working with stale objects.
		for (Order order : ordersToUpdate.values()) {
			orderService.update(order);
		}

		deductPreOrBackOrderedQuantity(productSku, allocated);
	}

	/**
	 * Deducts the given quantity from the given ProductSku's PreOrder/BackOrder quantity.
	 * @param productSku the productSku to modify
	 * @param quantity the quantity that should be deducted
	 * @return the modified and persisted ProductSku
	 */
	ProductSku deductPreOrBackOrderedQuantity(final ProductSku productSku, final int quantity) {
		PreOrBackOrderDetails preOrBackOrderDetails = getProductInventoryManagementService().getPreOrBackOrderDetails(productSku.getSkuCode());
		productSku.setPreOrBackOrderedQuantity(preOrBackOrderDetails.getQuantity() - quantity);
		return getProductSkuService().saveOrUpdate(productSku);
	}

	/**
	 * Persist the given order.
	 * @param order the order to persist
	 * @return the persisted / updated order
	 */
	Order updateOrder(final Order order) {
		return getOrderService().update(order);
	}

	/**
	 *
	 * @param sku the sku
	 * @param warehouseUidPk the warehouse UID
	 * @return the available quantity of the given Sku in the given warehouse
	 */
	int getAvailableSkuQuantityInWarehouse(final ProductSku sku, final long warehouseUidPk) {
		return getProductInventoryManagementService().getAvailableInStockQty(sku, warehouseUidPk);
	}

	/**
	 * Get the warehouse UIDPK from the warehouse code.
	 * @param warehouseCode the code
	 * @return the UIDPK
	 */
	long getWarehouseUidFromCode(final String warehouseCode) {
		return getWarehouseService().findByCode(warehouseCode).getUidPk();
	}

	/**
	 * Gets a collection of all OrderSkus that are awaiting inventory allocation.
	 * @param skuCode the sku code
	 * @param warehouseUidPk the warehouse UIDPK
	 * @return the OrderSkus that are awaiting inventory allocation
	 */
	List<OrderSku> getOrderSkusAwaitingInventory(final String skuCode, final long warehouseUidPk) {
		List<OrderSku> orderSkuList = allocationService.findOrderSkusWithCodeAndStatus(skuCode, warehouseUidPk,
				OrderShipmentStatus.AWAITING_INVENTORY);
		return orderSkuList;
	}

	/**
	 * Calculate how many units should be allocated given how many are available to be allocated,
	 * how many have already been allocated, and how many have been requested.
	 * @param availableQuantity the available quantity
	 * @param quantityOrdered the quantity requested
	 * @param quantityAlreadyAllocated the quantity already allocated
	 * @return the additional quantity that should be allocated
	 */
	int calculateAdditionalQuantityToAllocate(final int availableQuantity, final int quantityOrdered, final int quantityAlreadyAllocated) {
		return Math.min(availableQuantity, quantityOrdered - quantityAlreadyAllocated);
	}

	/**
	 * Allocates a unit quantity of Warehouse's inventory of a ProductSku an Order.
	 *
	 * @param productSku the productSku
	 * @param order the order that's being updated
	 * @param warehouseUid the warehouse
	 * @param allocated the quantity to be allocated
	 */
	void allocateInventory(final ProductSku productSku, final Order order, final long warehouseUid, final int allocated) {
		getProductInventoryManagementService().processInventoryUpdate(productSku,
				warehouseUid,
				InventoryEventType.STOCK_ALLOCATE,
				"System",
				allocated,
				order,
				"Allocating inventory to an outstanding order");

	}

	private OrderAllocationProcessor getOrderAllocationProcessor() {
		return getBean(ContextIdNames.ORDER_ALLOCATION_PROCESSOR);
	}

	/**
	 * Sets the {@link ProductInventoryManagementService} and Registers an inventory listener to the service.
	 *
	 * @param productInventoryManagementService the service implementation.
	 */
	public void setProductInventoryManagementService(final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
		productInventoryManagementService.registerInventoryListener(inventoryListener);
	}

	/**
	 * Set the order service.
	 *
	 * @param orderService the order service
	 */
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * Set the allocation service.
	 *
	 * @param allocationService the <code>allocationService</code> instance.
	 */
	public void setAllocationService(final AllocationService allocationService) {
		this.allocationService = allocationService;
	}

	/**
	 * Set the product SKU service.
	 *
	 * @param productSkuService the <code>productSkuService</code> instance.
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	/**
	 * Set the warehouse service.
	 *
	 * @param warehouseService the <code>warehouseService</code> instance.
	 */
	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	/**
	 * @return the orderService
	 */
	public OrderService getOrderService() {
		return orderService;
	}

	/**
	 * @return the productSkuService
	 */
	public ProductSkuService getProductSkuService() {
		return productSkuService;
	}

	/**
	 * @return the warehouseService
	 */
	public WarehouseService getWarehouseService() {
		return warehouseService;
	}

	/**
	 * @return the inventoryService
	 */
	public ProductInventoryManagementService getProductInventoryManagementService() {
		return productInventoryManagementService;
	}

}
