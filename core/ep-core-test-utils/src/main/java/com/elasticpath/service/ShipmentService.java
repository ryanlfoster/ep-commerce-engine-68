package com.elasticpath.service;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShippingServiceLevel;

/**
 * Provide Shipments-related business operations such as add, remove and move shipping items.
 */
public interface ShipmentService {

	/**
	 * Adds an item of productSku with minimal order quantity to the shipment.
	 * 
	 * @param shipment shipment to be used
	 * @param productSku sku to add
	 * @param cmUser user
	 * @return order sku.
	 * @throws EpServiceException in case of any errors
	 */
	OrderSku addItem(PhysicalOrderShipment shipment, ProductSku productSku, CmUser cmUser) throws EpServiceException;

	/**
	 * Adds an item of product with minimal order quantity to the shipment.
	 * 
	 * @param shipment shipment to be used
	 * @param product product to add
	 * @param cmUser user
	 * @return order sku.
	 * @throws EpServiceException in case of any errors
	 */
	OrderSku addItem(PhysicalOrderShipment shipment, Product product, CmUser cmUser) throws EpServiceException;

	/**
	 * Adds an item of productSku which have the same skuCode with minimal order quantity to the shipment.
	 * 
	 * @param shipment shipment to be used
	 * @param skuCode code of the sku to be added
	 * @param cmUser user
	 * @return order sku.
	 * @throws EpServiceException in case of any errors
	 */
	OrderSku addProductSkuItem(PhysicalOrderShipment shipment, String skuCode, CmUser cmUser) throws EpServiceException;

	/**
	 * Adds default productSku from product with minimal order quantity to the shipment.
	 * 
	 * @param shipment shipment to be used
	 * @param productCode product code
	 * @param cmUser user
	 * @return order sku.
	 * @throws EpServiceException in case of any errors
	 */
	OrderSku addProductItem(PhysicalOrderShipment shipment, String productCode, CmUser cmUser) throws EpServiceException;

	/**
	 * Removes an item which contains productSku from the shipment.
	 * 
	 * @param shipment shipment to be used
	 * @param orderSku sku to be removed
	 * @param cmUser CM User
	 */
	void removeItem(PhysicalOrderShipment shipment, OrderSku orderSku, CmUser cmUser);

	/**
	 * Removes an item which contains productSku with skuCode from the shipment.
	 * 
	 * @param shipment shipment to be used
	 * @param skuCode sku to be removed
	 * @param cmUser user
	 */
	void removeProductSkuItem(PhysicalOrderShipment shipment, String skuCode, CmUser cmUser);

	/**
	 * Removes an item which contains product with productCode from the shipment.
	 * 
	 * @param shipment shipment to be used
	 * @param productCode code of the product
	 * @param cmUser user
	 */
	void removeProductItem(PhysicalOrderShipment shipment, String productCode, CmUser cmUser);

	/**
	 * Updates an item quantity of productSku with skuCode.
	 * 
	 * @param shipment shipment to be used
	 * @param skuCode sku code
	 * @param quantity new quantity value
	 * @param cmUser user
	 * @throws EpServiceException in case of any errors
	 */
	void updateItemQuantity(PhysicalOrderShipment shipment, String skuCode, int quantity, CmUser cmUser) throws EpServiceException;

	/**
	 * Moves the specified sku of the specified quantity from the source shipment to the shipment.
	 * 
	 * @param fromOrderShipment shipment to be used
	 * @param toOrderShipment new order shipment
	 * @param orderSku sku to be moved
	 * @param qty quantity to be moved.
	 * @param cmUser user
	 * @throws EpServiceException in case of any errors
	 */
	void moveSkuToExistingShipment(PhysicalOrderShipment fromOrderShipment, PhysicalOrderShipment toOrderShipment, String orderSku, int qty,
			CmUser cmUser);

	/**
	 * Moves the specified sku of the specified quantity from the source shipment to the shipment.
	 * 
	 * @param fromOrderShipment shipment to be used
	 * @param toOrderShipment new order shipment
	 * @param orderSku sku to be moved
	 * @param qty quantity to be moved
	 * @param cmUser user
	 * @throws EpServiceException in case of any errors
	 */
	void moveSkuToExistingShipment(PhysicalOrderShipment fromOrderShipment, PhysicalOrderShipment toOrderShipment, OrderSku orderSku, int qty,
			CmUser cmUser);

	/**
	 * Moves the specified sku of the specified quantity from the source shipment to the new shipment.
	 * 
	 * @param fromOrderShipment shipment to be used
	 * @param orderSku sku to be moved
	 * @param qty quantity to be moved
	 * @param orderAddress order address
	 * @param shippingServiceLevel shipping service level
	 * @param cmUser user
	 * @throws EpServiceException in case of any errors
	 */
	void moveSkuToNewShipment(PhysicalOrderShipment fromOrderShipment, String orderSku, int qty, OrderAddress orderAddress,
			ShippingServiceLevel shippingServiceLevel, CmUser cmUser);

	/**
	 * Moves the specified sku of the specified quantity from the source shipment to the new shipment.
	 * 
	 * @param fromOrderShipment shipment to be used
	 * @param orderSku sku to be moved
	 * @param qty quantity to be moved
	 * @param orderAddress order address
	 * @param shippingServiceLevel shipping service level
	 * @param cmUser user
	 * @throws EpServiceException in case of any errors
	 */
	void moveSkuToNewShipment(PhysicalOrderShipment fromOrderShipment, OrderSku orderSku, int qty, OrderAddress orderAddress,
			ShippingServiceLevel shippingServiceLevel, CmUser cmUser);

	/**
	 * Iterates through all order's shipments and then through a shipment's skus. Processes allocation for the skus.
	 * 
	 * @param order order to be allocated.
	 * @param cmUser cm user is being saving the order.
	 */
	void proceedQuantityAllocation(Order order, CmUser cmUser);
}
