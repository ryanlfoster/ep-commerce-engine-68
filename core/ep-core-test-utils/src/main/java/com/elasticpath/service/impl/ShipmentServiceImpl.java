package com.elasticpath.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.RecalculableObject;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.order.AllocationEventType;
import com.elasticpath.domain.order.AllocationResult;
import com.elasticpath.domain.order.AllocationStatus;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.testcontext.ShoppingTestData;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.ShipmentService;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.store.StoreService;

/**
 * @inheritDoc
 */
public class ShipmentServiceImpl implements ShipmentService {

	private static final String STRING_NEW_LINE = "\n"; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$

	private ElasticPath elasticPath;
	private AllocationService allocationService;
	private EventOriginatorHelper eventOriginatorHelper;
	private PriceLookupFacade priceLookupFacade;
	private ProductInventoryManagementService productInventoryManagementService;
	private StoreService storeService;
	
	@Override
	public OrderSku addItem(final PhysicalOrderShipment shipment, final ProductSku productSku, final CmUser cmUser) throws EpServiceException {
		// check if the product is digital and if yes throw exception and cancel the addition to order shipment
		if (!productSku.isShippable() && productSku.getDigitalAsset() != null) {
			throw new EpServiceException("Unable to add item. Digital goods cannot be added to existing orders.");
		}
		// if product sku exists in current shipment, then pop up a dialog to user
		if (isItemExists(shipment, productSku.getSkuCode())) {
			throw new EpServiceException("Unable to add item. Digital goods cannot be added to existing orders.");
		}

		Order order = shipment.getOrder();
		Store store = getStoreService().findStoreWithCode(order.getStoreCode());
		OrderSku orderSku = extractOrderSku(productSku, order, store, cmUser);
		shipment.addShipmentOrderSku(orderSku);
		handleQuantityAllocationWhenAddItem(orderSku, order, store);
		shipment.getOrder().setModifiedBy(getEventOriginator(cmUser));
		return orderSku;
	}

	@Override
	public OrderSku addItem(final PhysicalOrderShipment shipment, final Product product, final CmUser cmUser) throws EpServiceException {
		return addItem(shipment, product.getDefaultSku(), cmUser);
	}

	@Override
	public OrderSku addProductItem(final PhysicalOrderShipment shipment, final String productCode, final CmUser cmUser) throws EpServiceException {
		Product product = getProductByCode(productCode);
		return addItem(shipment, product, cmUser);
	}

	@Override
	public OrderSku addProductSkuItem(final PhysicalOrderShipment shipment, final String skuCode, final CmUser cmUser) throws EpServiceException {
		ProductSku productSku = getProductSkuBySkucode(skuCode);
		return addItem(shipment, productSku, cmUser);
	}

	@Override
	public void moveSkuToExistingShipment(final PhysicalOrderShipment fromOrderShipment, final PhysicalOrderShipment toOrderShipment,
			final String orderSku, final int quantityToMove, final CmUser cmUser) {
		moveSkuToExistingShipment(fromOrderShipment, toOrderShipment, getOrderSkuBySkuCode(fromOrderShipment, orderSku), quantityToMove, cmUser);
	}

	@Override
	public void moveSkuToExistingShipment(final PhysicalOrderShipment fromOrderShipment, final PhysicalOrderShipment toOrderShipment,
			final OrderSku orderSku, final int quantityToMove, final CmUser cmUser) {
		OrderSku newOrderSku = getElasticPath().getBean(ContextIdNames.ORDER_SKU);
		// get a copy of order sku to avoid JPA issue when deleting and adding order sku with same UIDPK
		newOrderSku.copyFrom(orderSku);
		newOrderSku.setQuantity(quantityToMove);
		final int quantityLeftInOldOrderSku = orderSku.getQuantity() - quantityToMove;

		boolean existOrderSku = false;
		for (OrderSku orderSkuItem : toOrderShipment.getShipmentOrderSkus()) {
			if (orderSkuItem.getSkuCode().equals(newOrderSku.getSkuCode())) {
				orderSkuItem.setQuantity(orderSkuItem.getQuantity() + newOrderSku.getQuantity());
				orderSkuItem.setChangedQuantityAllocated(orderSkuItem.getChangedQuantityAllocated() + newOrderSku.getChangedQuantityAllocated());
				orderSkuItem.setAllocatedQuantity(orderSkuItem.getAllocatedQuantity() + newOrderSku.getAllocatedQuantity());
				existOrderSku = true;
				break;
			}
		}
		if (!existOrderSku) {
			toOrderShipment.addShipmentOrderSku(newOrderSku);
		}
		newOrderSku.setShipment(toOrderShipment);

		orderSku.setQuantity(quantityLeftInOldOrderSku);

		fromOrderShipment.getOrder().setModifiedBy(getEventOriginator(cmUser));

	}

	@Override
	public void moveSkuToNewShipment(final PhysicalOrderShipment fromOrderShipment, final String orderSku, final int quantityToMove,
			final OrderAddress orderAddress, final ShippingServiceLevel shippingServiceLevel, final CmUser cmUser) {

		moveSkuToNewShipment(fromOrderShipment, getOrderSkuBySkuCode(fromOrderShipment, orderSku), quantityToMove, orderAddress,
				shippingServiceLevel, cmUser);
	}

	@Override
	public void moveSkuToNewShipment(final PhysicalOrderShipment fromOrderShipment, final OrderSku orderSku, final int quantityToMove,
			final OrderAddress orderAddress, final ShippingServiceLevel shippingServiceLevel, final CmUser cmUser) {
		PhysicalOrderShipment newShipment = getElasticPath().getBean(ContextIdNames.PHYSICAL_ORDER_SHIPMENT);

		OrderAddress newOrderAddress = getElasticPath().getBean(ContextIdNames.ORDER_ADDRESS);
		newOrderAddress.copyFrom(orderAddress);
		newShipment.setShipmentAddress(newOrderAddress);
		newShipment.setShippingServiceLevelGuid(shippingServiceLevel.getGuid());
		newShipment.setCarrier(shippingServiceLevel.getCarrier());
		// TODO: the following deprecated method was copied from MoveItemDialog.
		newShipment.setServiceLevel(shippingServiceLevel.getDisplayName(fromOrderShipment.getOrder().getLocale(), true));

		OrderSku newOrderSku = getElasticPath().getBean(ContextIdNames.ORDER_SKU);
		// get a copy of order sku to avoid JPA issue when deleting and adding order sku with same UIDPK
		newOrderSku.copyFrom(orderSku);
		newOrderSku.setQuantity(quantityToMove);
		final int quantityLeftInOldOrderSku = orderSku.getQuantity() - quantityToMove;

		allocateQuantity(orderSku, newOrderSku, quantityToMove);
		orderSku.setQuantity(quantityLeftInOldOrderSku);

		newShipment.addShipmentOrderSku(newOrderSku);

		newShipment.setCreatedDate(new Date());
		final Money shippingCost = shippingServiceLevel.calculateRegularPriceShippingCost(newShipment.getShipmentOrderSkus(),
				fromOrderShipment.getOrder().getCurrency());
		newShipment.setShippingCost(shippingCost.getAmount());
		newShipment.setBeforeTaxShippingCost(BigDecimal.ZERO);
		newShipment.setSubtotalDiscount(BigDecimal.ZERO);
		newShipment.setStatus(fromOrderShipment.getShipmentStatus());

		if (quantityLeftInOldOrderSku == 0) {
			fromOrderShipment.removeShipmentOrderSku(orderSku);
		}

		fromOrderShipment.getOrder().addShipment(newShipment);

		fromOrderShipment.getOrder().setModifiedBy(getEventOriginator(cmUser));
	}

	@Override
	public void removeItem(final PhysicalOrderShipment shipment, final OrderSku orderSku, final CmUser cmUser) {
		shipment.removeShipmentOrderSku(orderSku);
		shipment.getOrder().setModifiedBy(getEventOriginator(cmUser));

		if (shipment.getShipmentOrderSkus().size() == 0) {
			shipment.setStatus(OrderShipmentStatus.CANCELLED);
		}
	}

	@Override
	public void removeProductSkuItem(final PhysicalOrderShipment shipment, final String skuCode, final CmUser cmUser) {
		OrderSku orderSku = getOrderSkuBySkuCode(shipment, skuCode);
		removeItem(shipment, orderSku, cmUser);
	}
	
	@Override
	public void removeProductItem(final PhysicalOrderShipment shipment, final String productCode, final CmUser cmUser) {
		OrderSku orderSku = getOrderSkuByProductCode(shipment, productCode);
		removeItem(shipment, orderSku, cmUser);
	}

	@Override
	public void updateItemQuantity(final PhysicalOrderShipment shipment, final String skuCode, final int quantity, final CmUser cmUser)
			throws EpServiceException {
		OrderSku newOrderSku = getOrderSkuBySkuCode(shipment, skuCode);

		OrderSku oldOrderSku = elasticPath.getBean(ContextIdNames.ORDER_SKU);
		oldOrderSku.setQuantity(newOrderSku.getQuantity());
		oldOrderSku.setChangedQuantityAllocated(newOrderSku.getChangedQuantityAllocated());

//		newOrderSku.setPrice(quantity, newOrderSku.getPrice()); //bug is here - setting Price that has no tier for the given qty
		newOrderSku.setQuantity(quantity); //TODO: Should actually be setting the PriceTier at the same time. Fix when quantity method removed.
		
		final Order order = shipment.getOrder();
		final Store store = getStoreService().findStoreWithCode(order.getStoreCode());
		final long warehouseUid = store.getWarehouse().getUidPk();

		final int newlyEnteredQuantity = newOrderSku.getQuantity();
		final int quantityToAllocate = newlyEnteredQuantity - newOrderSku.getAllocatedQuantity();
		final int quantityToAllocateInShipment = getTotalIncreasedQuantitySinceLastSave(newOrderSku.getSkuCode(), order)
				- getTotalPreOrBackOrderQuantity(newOrderSku.getSkuCode(), order);

		if (quantityToAllocateInShipment <= 0) {
			newOrderSku.setChangedQuantityAllocated(quantityToAllocate - newOrderSku.getPreOrBackOrderQuantity());
		} else {

			final AllocationStatus allocationStatus = allocationService.getAllocationStatus(newOrderSku.getProductSku(), warehouseUid,
					quantityToAllocateInShipment);

			final int availableQtyInStock = getAvailableQuantityInStock(newOrderSku, warehouseUid);
			switch (allocationStatus) {
			case ALLOCATED_IN_STOCK:
				newOrderSku.setChangedQuantityAllocated(quantityToAllocate);
				break;
			case AWAITING_ALLOCATION:
				// in AWAITING_ALLOCATION status, some items are not available in stock, the shipment cannot be shipped
				// until those items are acquired in inventory

				final int totalAvailableCanbeAllocatedQuantity = availableQtyInStock;
				final int alreadyAllocatedQuantity = getAllocatedInStockQty(newOrderSku, warehouseUid)
						+ getTotalChangedQuantityAllocated(newOrderSku.getSkuCode(), order);
				final int alreadyAllocatedQuantityExceptCurrentOrderSku = alreadyAllocatedQuantity - newOrderSku.getChangedQuantityAllocated();
				newOrderSku.setChangedQuantityAllocated(totalAvailableCanbeAllocatedQuantity - alreadyAllocatedQuantityExceptCurrentOrderSku);

				break;
			case NOT_ALLOCATED:
				final int availableQuantityCanbeAddedIntoCurrentOrderSku = availableQtyInStock
						+ getTotalPreOrBackOrderQuantity(newOrderSku.getSkuCode(), order)
						- getTotalIncreasedQuantitySinceLastSave(newOrderSku.getSkuCode(), order) + newOrderSku.getQuantity();
				if (availableQuantityCanbeAddedIntoCurrentOrderSku == 0) {
					throw new EpServiceException("Unable to change SKU quantity for the following SKU:" + STRING_NEW_LINE + STRING_NEW_LINE
							+ newOrderSku.getSkuCode() + STRING_NEW_LINE + STRING_NEW_LINE + "Out of stock.");
				}
				newOrderSku.setQuantity(oldOrderSku.getQuantity());
				newOrderSku.setChangedQuantityAllocated(oldOrderSku.getChangedQuantityAllocated());
				throw new EpServiceException("Unable to change SKU quantity for the following SKU:" + STRING_NEW_LINE + STRING_NEW_LINE
						+ newOrderSku.getSkuCode() + STRING_NEW_LINE + STRING_NEW_LINE + "Only" + SPACE
						+ (availableQuantityCanbeAddedIntoCurrentOrderSku) + SPACE + "items are available in stock.");
				// convert back to original ordersku
			default:
				// nothing to do
			}
		}
		((RecalculableObject) newOrderSku).enableRecalculation();
	}

	@Override
	public void proceedQuantityAllocation(final Order order, final CmUser cmUser) {
		for (final PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			for (final OrderSku orderSku : shipment.getShipmentRemovedOrderSku()) {
				orderSku.setShipment(shipment);
				allocationService.processAllocationEvent(orderSku, AllocationEventType.ORDER_ADJUSTMENT_REMOVESKU, "CM User: " 
						+ cmUser.getUidPk(), 
						orderSku.getAllocatedQuantity(), null);
				orderSku.setShipment(null);
			}

			for (final OrderSku orderSku : shipment.getShipmentOrderSkus()) {
				final int quantityIntentToAllocate = orderSku.getQuantity() - orderSku.getAllocatedQuantity() - orderSku.getPreOrBackOrderQuantity();
				AllocationResult result = allocationService.processAllocationEvent(orderSku, AllocationEventType.ORDER_ADJUSTMENT_CHANGEQTY,
						"CM User: " + cmUser.getUidPk(), //$NON-NLS-1$
						quantityIntentToAllocate, null);
				// TODO: this has to be changed so that it is handled in the model
				orderSku.setChangedQuantityAllocated(0);
				if (quantityIntentToAllocate > 0) {
					final int quantityAvailableToAllocate = result.getQuantityAllocatedInStock();
					orderSku.setAllocatedQuantity(orderSku.getAllocatedQuantity() + quantityAvailableToAllocate);
				} else if (quantityIntentToAllocate < 0) {
					orderSku.setAllocatedQuantity(orderSku.getQuantity());
				}
			}
		}
	}

	private OrderSku extractOrderSku(final ProductSku productSku, final Order order, final Store store, final CmUser cmUser) {
		OrderSku orderSku = elasticPath.getBean(ContextIdNames.ORDER_SKU);
		orderSku.setCreatedDate(new Date());
		orderSku.setDigitalAsset(productSku.getDigitalAsset());
		orderSku.setDisplayName(productSku.getProduct().getDisplayName(order.getLocale()));
		if (productSku.getOptionValues().size() > 0) {
			final StringBuffer skuOptionValues = new StringBuffer();
			for (final Iterator<SkuOptionValue> optionValueIter = productSku.getOptionValues().iterator(); optionValueIter.hasNext();) {
				final SkuOptionValue currOptionValue = optionValueIter.next();
				skuOptionValues.append(currOptionValue.getDisplayName(order.getLocale(), true));
				if (optionValueIter.hasNext()) {
					skuOptionValues.append(", "); //$NON-NLS-1$
				}
			}
			orderSku.setDisplaySkuOptions(skuOptionValues.toString());
		}

		// FIXME:
		// orderSku.setGiftCertificate(productSku.getGiftCertificate());
		orderSku.setImage(productSku.getImage());

		orderSku.setLastModifiedBy(cmUser);
		// FIXME: May not be creating customerSession correctly (missing Shopper).
		CustomerSession session = ShoppingTestData.getInstance().getCustomerSession();
		session.setCurrency(order.getCurrency());
		Price price = priceLookupFacade.getPromotedPriceForSku(productSku, store, session.getShopper(), new HashSet<Long>());
		if (price == null) {
			throw new EpServiceException("There is no price for the product with productSku code:" + STRING_NEW_LINE + productSku.getSkuCode()
					+ SPACE + "in the currency:" + SPACE + order.getCurrency().getCurrencyCode() + STRING_NEW_LINE + "and catalog:" + SPACE
					+ store.getCatalog().getName());
		}

		orderSku.setProductSku(productSku);
		orderSku.setSkuCode(productSku.getSkuCode());
		orderSku.setPrice(productSku.getProduct().getMinOrderQty(), price);
		orderSku.setUnitPrice(orderSku.getLowestUnitPrice().getAmountUnscaled());
		
		if (productSku.getWeight() != null) {
			orderSku.setWeight(productSku.getWeight().intValue());
		}
		orderSku.setTaxCode(productSku.getProduct().getTaxCode().getCode());
		return orderSku;
	}

	private void handleQuantityAllocationWhenAddItem(final OrderSku orderSku, final Order order, final Store store) {
		// this method gets called before quantity is binded to domain model, so add 1 to reflect new added order sku
		final int quantityToAllocateInShipment = getTotalIncreasedQuantitySinceLastSave(orderSku.getSkuCode(), order) + 1;
		if (quantityToAllocateInShipment <= 0) {
			orderSku.setChangedQuantityAllocated(1);
		} else {
			Warehouse warehouse = store.getWarehouse();
			final AllocationStatus allocationStatus = allocationService.getAllocationStatus(orderSku.getProductSku(), warehouse.getUidPk(),
					quantityToAllocateInShipment);
			if (allocationStatus == AllocationStatus.ALLOCATED_IN_STOCK) {
				orderSku.setChangedQuantityAllocated(1);
			} else if (allocationStatus == AllocationStatus.NOT_ALLOCATED) {
				throw new EpServiceException("Unable to change SKU quantity for the following SKU:" + orderSku.getSkuCode() + "out of stock.");
			}
		}
	}

	/**
	 * Increased quantity since last save refers to the amount of quantity newly added to the currently order sku since last save. For each order sku
	 * increasedQuantitySinceLastSave = orderSku.getQuantity - orderSku.getAllocatedQuantity Summing up (quantity - allocated quantity) for all the
	 * orderSku with the same skuCode in the order, you will get totalIncreasedQauntitySinceLastSave
	 */
	private int getTotalIncreasedQuantitySinceLastSave(final String skuCode, final Order order) {
		int totalIncreasedQuantitySinceLastSave = 0;
		for (final PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			for (final OrderSku orderSkuItem : shipment.getShipmentOrderSkus()) {
				if (orderSkuItem.getSkuCode().equals(skuCode)) {
					totalIncreasedQuantitySinceLastSave += (orderSkuItem.getQuantity() - orderSkuItem.getAllocatedQuantity());
				}
			}
		}
		return totalIncreasedQuantitySinceLastSave;
	}

	private int getTotalPreOrBackOrderQuantity(final String skuCode, final Order order) {
		int totalPreOrBackOrderQuantity = 0;
		for (PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			for (OrderSku orderSkuItem : shipment.getShipmentOrderSkus()) {
				if (orderSkuItem.getSkuCode().equals(skuCode)) {
					totalPreOrBackOrderQuantity += orderSkuItem.getPreOrBackOrderQuantity();
				}
			}
		}
		return totalPreOrBackOrderQuantity;
	}

	private int getAvailableQuantityInStock(final OrderSku orderSku, final long warehouseUid) {
		final InventoryDto inventoryDto = productInventoryManagementService.getInventory(orderSku.getProductSku().getSkuCode(), warehouseUid);
		if (inventoryDto != null) {
			return inventoryDto.getAvailableQuantityInStock();
		}
		return 0;
	}

	private int getAllocatedInStockQty(final OrderSku orderSku, final long warehouseUid) {
		final InventoryDto inventoryDto = productInventoryManagementService.getInventory(orderSku.getProductSku().getSkuCode(), warehouseUid);
		if (inventoryDto != null) {
			return inventoryDto.getAllocatedQuantity();
		}
		return 0;
	}

	/**
	 * Sum up quantityAllocated for all the orderSku with the same skuCode in the order.
	 */
	private int getTotalChangedQuantityAllocated(final String skuCode, final Order order) {
		int totalChangedQuantityAllocated = 0;
		for (final PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			for (final OrderSku orderSkuItem : shipment.getShipmentOrderSkus()) {
				if (orderSkuItem.getSkuCode().equals(skuCode)) {
					totalChangedQuantityAllocated += orderSkuItem.getChangedQuantityAllocated();
				}
			}
		}
		return totalChangedQuantityAllocated;
	}

	/**
	 * Whether an item with productSku exists in the shipment.
	 * 
	 * @param shipment the shipment to check
	 * @param skuCode the sku code
	 * @return true if item exists.
	 */
	private boolean isItemExists(final PhysicalOrderShipment shipment, final String skuCode) {
		for (final OrderSku orderSkuItem : shipment.getShipmentOrderSkus()) {
			if (orderSkuItem.getSkuCode().equals(skuCode)) {
				return true;
			}
		}
		return false;
	}

	private ProductSku getProductSkuBySkucode(final String skuCode) {
		ProductSkuService productSkuService = elasticPath.getBean(ContextIdNames.PRODUCT_SKU_SERVICE);
		return productSkuService.findBySkuCode(skuCode);
	}

	private Product getProductByCode(final String productCode) {
		ProductService productService = elasticPath.getBean(ContextIdNames.PRODUCT_SERVICE);
		return productService.findByGuid(productCode);
	}

	private OrderSku getOrderSkuBySkuCode(final PhysicalOrderShipment shipment, final String skuCode) {
		for (final OrderSku orderSkuItem : shipment.getShipmentOrderSkus()) {
			if (orderSkuItem.getSkuCode().equals(skuCode)) {
				return orderSkuItem;
			}
		}
		throw new EpServiceException("Unable to find orderSku with skuCode:" + skuCode + "in the shipment.");
	}

	private OrderSku getOrderSkuByProductCode(final PhysicalOrderShipment shipment, final String productCode) {
		for (final OrderSku orderSkuItem : shipment.getShipmentOrderSkus()) {
			if (orderSkuItem.getProductSku().getProduct().getCode().equals(productCode)) {
				return orderSkuItem;
			}
		}
		throw new EpServiceException("Unable to find orderSku with productCode:" + productCode + "in the shipment.");
	}

	/**
	 * @return
	 */
	private EventOriginator getEventOriginator(final CmUser cmUser) {
		return eventOriginatorHelper.getCmUserOriginator(cmUser);
	}

	private void allocateQuantity(final OrderSku oldOrderSku, final OrderSku newOrderSku, final int quantityToMove) {
		final int quantityLeft = oldOrderSku.getQuantity() - quantityToMove;
		if (quantityLeft <= oldOrderSku.getAllocatedQuantity()) {
			newOrderSku.setAllocatedQuantity(oldOrderSku.getAllocatedQuantity() - quantityLeft);
			newOrderSku.setChangedQuantityAllocated(oldOrderSku.getChangedQuantityAllocated());
			oldOrderSku.setQuantity(quantityLeft);
			oldOrderSku.setAllocatedQuantity(quantityLeft);
			oldOrderSku.setChangedQuantityAllocated(0);
		} else if (quantityLeft <= oldOrderSku.getAllocatedQuantity() + oldOrderSku.getChangedQuantityAllocated()) {
			newOrderSku.setAllocatedQuantity(0);
			newOrderSku.setChangedQuantityAllocated(oldOrderSku.getChangedQuantityAllocated() - (quantityLeft - oldOrderSku.getAllocatedQuantity()));
			oldOrderSku.setQuantity(quantityLeft);
			oldOrderSku.setAllocatedQuantity(oldOrderSku.getAllocatedQuantity());
			oldOrderSku.setChangedQuantityAllocated(quantityLeft - oldOrderSku.getAllocatedQuantity());
		} else {
			newOrderSku.setAllocatedQuantity(0);
			newOrderSku.setChangedQuantityAllocated(0);
			oldOrderSku.setQuantity(quantityLeft);
			oldOrderSku.setAllocatedQuantity(oldOrderSku.getAllocatedQuantity());
			oldOrderSku.setChangedQuantityAllocated(oldOrderSku.getChangedQuantityAllocated());
		}
	}

	/**
	 * @param facade the {@link PriceLookupFacade} to set
	 */
	public void setPriceLookupFacade(final PriceLookupFacade facade) {
		this.priceLookupFacade = facade;
	}

	/**
	 * @return the priceLookupService
	 */
	public PriceLookupFacade getPriceLookupFacade() {
		return priceLookupFacade;
	}

	/**
	 * Sets the {@link ProductInventoryManagementService}.
	 * 
	 * @param productInventoryManagementService the product inventory management service
	 */
	public void setProductInventoryManagementService(final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
	}

	/**
	 * @param allocationService the allocationService to set
	 */
	public void setAllocationService(final AllocationService allocationService) {
		this.allocationService = allocationService;
	}

	/**
	 * @param eventOriginatorHelper the eventOriginatorHelper to set
	 */
	public void setEventOriginatorHelper(final EventOriginatorHelper eventOriginatorHelper) {
		this.eventOriginatorHelper = eventOriginatorHelper;
	}

	public ElasticPath getElasticPath() {
		return elasticPath;
	}

	public void setElasticPath(final ElasticPath elasticPath) {
		this.elasticPath = elasticPath;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}
}
