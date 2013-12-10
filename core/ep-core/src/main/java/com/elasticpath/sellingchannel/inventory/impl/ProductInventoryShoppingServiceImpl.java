package com.elasticpath.sellingchannel.inventory.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.commons.util.capabilities.Capabilities;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.store.Store;
import com.elasticpath.inventory.InventoryCapabilities;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalogview.impl.InventoryMessage;

/**
 * Implementation of {@code ProductInventoryShoppingService}.
 * This implementation calculates the worst case inventory details in the case of bundles.
 */
public class ProductInventoryShoppingServiceImpl implements ProductInventoryShoppingService {
	
	private ProductInventoryManagementService productInventoryManagementService;
	
	@Override
	public SkuInventoryDetails getSkuInventoryDetails(final ProductSku productSku,
			final Store store, final ShoppingItemDto shoppingItemDto) {
		Product product = productSku.getProduct();
		boolean inBundle = product instanceof ProductBundle;
		
		Map<String, Integer> inventoryRequirementsMap = new HashMap<String, Integer>();
		addInventoryRequirementsToMap(product, inventoryRequirementsMap, shoppingItemDto.getQuantity(), inBundle);
		
		return getSkuInventoryDetailsForNode(productSku, store, product, 
				inventoryRequirementsMap, shoppingItemDto, inBundle, null);
	}
	
	@Override
	public SkuInventoryDetails getSkuInventoryDetails(final ProductSku productSku, final Store store) {
		Product product = productSku.getProduct();
		boolean inBundle = product instanceof ProductBundle;
		
		Set<String> skuCodesForInventory = new HashSet<String>();
		skuCodesForInventory.add(productSku.getSkuCode());
		
		Map<String, InventoryDto> skuInventoryMap = productInventoryManagementService.getInventoriesForSkusInWarehouse(
				skuCodesForInventory, store.getWarehouse().getUidPk());
		
		Map<String, Integer> inventoryRequirementsMap = new HashMap<String, Integer>();
		addInventoryRequirementsToMap(product, inventoryRequirementsMap, 1, inBundle); // assume single bundle (root qty = 1)
		
		return getSkuInventoryDetailsForNode(productSku, store, product, 
				inventoryRequirementsMap, null, inBundle, skuInventoryMap);
	}
	
	@Override
	public Map<String, SkuInventoryDetails> getSkuInventoryDetailsForAllSkus(
			final Product product, final Store store) {
		boolean inBundle = product instanceof ProductBundle;
		
		Set<String> skuCodesForInventory = getSkuCodesForInventoryLookup(product, store);
		
		Map<String, InventoryDto> skuInventoryMap = productInventoryManagementService.getInventoriesForSkusInWarehouse(
				skuCodesForInventory, store.getWarehouse().getUidPk());
		
		Map<String, SkuInventoryDetails> returnMap = getSkuInventoryDetailsMapForSkus(product, store, inBundle, skuInventoryMap);
		
		return returnMap;		
	}

	/**
	 * Creates a map of skucode to SkuInventoryDetails for each sku for the given product. 
	 *
	 * @param product  the product
	 * @param store the store
	 * @param inBundle whether or not the product is in bundle
	 * @param skuInventoryMap the sku inventory map
	 * @return the map of sku codes to inventory details for each sku
	 */
	Map<String, SkuInventoryDetails> getSkuInventoryDetailsMapForSkus(final Product product, final Store store, final boolean inBundle,
			final Map<String, InventoryDto> skuInventoryMap) {
		Map<String, Integer> inventoryRequirementsMap = new HashMap<String, Integer>();
		addInventoryRequirementsToMap(product, inventoryRequirementsMap, 1, inBundle); // assume single bundle (root qty = 1)
		
		Map<String, SkuInventoryDetails> returnMap = new HashMap<String, SkuInventoryDetails>();
		
		for (ProductSku sku : product.getProductSkus().values()) {
			SkuInventoryDetails inventoryDetails = getSkuInventoryDetailsForNode(sku, store, product, 
					inventoryRequirementsMap, null, inBundle, skuInventoryMap);
			returnMap.put(sku.getSkuCode(), inventoryDetails);
		}
		return returnMap;
	}
	
	/**
	 * Returns the set of skus that require an inventory query.
	 * Allows all the inventory to be retrieved in one step.
	 * @param product The product to retrieve the inventory for.
	 * @param store The store
	 * @return The set of sku codes to retrieve inventory for.
	 */
	Set<String> getSkuCodesForInventoryLookup(final Product product,
			final Store store) {
		
		// leaving the shoppingItemDto handling out for now as it is only required
		// from the getAvailabilityMap method of some form controllers.
		// There is no known performance problem with the dto code path so leaving
		// as is for correctness.
		
		Set<String> returnSet = new HashSet<String>();
		
		if (product instanceof ProductBundle) { //use default skus
			ProductBundle bundle = (ProductBundle) product;
			for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
				
				ConstituentItem constituent = bundleConstituent.getConstituent();
				if (constituent.isProductSku()) {
					returnSet.add(constituent.getCode());
				} else {
					Set<String> constituentSet = getSkuCodesForInventoryLookup(constituent.getProduct(), store);
					returnSet.addAll(constituentSet);
				}
			}
		}
		
		for (ProductSku sku : product.getProductSkus().values()) {
			returnSet.add(sku.getSkuCode());
		}
		
		return returnSet;
	}

	/**
	 * Adds the inventory requirements for {@code Product} and children to {@code inventoryRequirementsMap}.
	 * 
	 * @param product the product
	 * @param inventoryRequirementsMap the map containing skucode to amount of inventory required.
	 * @param quantityRequired The required inventory calculated on the parent node. Multiplied by the bundle quantity
	 * and passed to the current node's children.
	 * @param inBundle True if in a bundle
	 */
	protected void addInventoryRequirementsToMap(final Product product,
			final Map<String, Integer> inventoryRequirementsMap, final int quantityRequired, final boolean inBundle) {
		
		if (product instanceof ProductBundle) {
			ProductBundle bundle = (ProductBundle) product;
			for (BundleConstituent constituent : bundle.getConstituents()) {
				//if this is the root, we don't want to get the qty, it should be 1 for the purposes of inventory calculation
				int constituentQuantity = 0;
				if (constituent.getQuantity() != null) {
					constituentQuantity = constituent.getQuantity();
				}
				ConstituentItem constituentItem = constituent.getConstituent();
				if (constituentItem.isProductSku()) {
					addInventoryRequirementsToMap(constituentItem.getProductSku(),
							inventoryRequirementsMap, quantityRequired * constituentQuantity, inBundle); 
				} else {
					addInventoryRequirementsToMap(constituentItem.getProduct(),
							inventoryRequirementsMap, quantityRequired * constituentQuantity, inBundle); 
				}
			}
		} else { //leaf node
			if (AvailabilityCriteria.ALWAYS_AVAILABLE == product.getAvailabilityCriteria()) {
				return;
			}
			for (ProductSku productSku : product.getProductSkus().values()) {
				addInventoryRequirementsToMap(productSku, inventoryRequirementsMap, quantityRequired, inBundle);
			}
		}
	}
	
	/**
	 * Adds the inventory requirements for {@code ProductSku} to {@code inventoryRequirementsMap}.
	 * 
	 * @param sku the product sku
	 * @param inventoryRequirementsMap the map containing skucode to amount of inventory required.
	 * @param quantityRequired The required inventory calculated on the parent node. Multiplied by the bundle quantity
	 * and passed to the current node's children.
	 * @param inBundle True if in a bundle
	 */
	protected void addInventoryRequirementsToMap(final ProductSku sku,
			final Map<String, Integer> inventoryRequirementsMap, final int quantityRequired, final boolean inBundle) {
		Integer currentRequiredAmount = inventoryRequirementsMap.get(sku.getSkuCode());
		Integer newRequiredAmount = getRequiredSkuQuantity(sku.getProduct().getMinOrderQty(), 
					currentRequiredAmount, quantityRequired, inBundle);
		inventoryRequirementsMap.put(sku.getSkuCode(), newRequiredAmount);
	}

	/**
	 * Get the total required amount. 
	 *
	 * @param minOrderQuantity product's minimum order quantity
	 * @param currentRequiredAmount amount already required
	 * @param newQuantityRequired the amount to add to current quantity required
	 * @param inBundle true if we're looking at a product in any level of a bundle
	 * @return the total required quantity
	 */
	protected int getRequiredSkuQuantity(final int minOrderQuantity, final Integer currentRequiredAmount, 
			final int newQuantityRequired, final boolean inBundle) {
		int newInventory = 0;
		int minOrderQty = minOrderQuantity;
		if (inBundle) {
			minOrderQty = 1;
		}
		if (currentRequiredAmount == null) {
			newInventory = newQuantityRequired;
		} else {
			newInventory = currentRequiredAmount + newQuantityRequired;  
		}
		
		return Math.max(newInventory, minOrderQty);
	}

	/**
	 * Creates a SkuInventoryDetailsForNode object based on product and porduct sku criterias which affects the calculation
	 * of inventory. 
	 * 
	 * @param productSku the product sku
	 * @param store the store
	 * @param product the product
	 * @param inventoryRequirementsMap the inventory requirement map
	 * @param shoppingItemDto the shopping item dto
	 * @param inBundle whether or not the product is in a bundle
	 * @param skuInventoryMap the map of sku codes to Inventory records. Can be null if {@code shoppingItemDto} provided.
	 * @return a SkuInventoryDetails populated for given product sku
	 */
	SkuInventoryDetails getSkuInventoryDetailsForNode(
			final ProductSku productSku, final Store store, final Product product,
			final Map<String, Integer> inventoryRequirementsMap,
			final ShoppingItemDto shoppingItemDto, final boolean inBundle,
			final Map<String, InventoryDto> skuInventoryMap) {
		
		SkuInventoryDetails worstInventoryDetails = null;

		// The dto path is not currently causing a performance problem so it has 
		// not been converted to a batch lookup.
		if (shoppingItemDto != null && shoppingItemDto.getConstituents().size() > 0) { //use selected skus
			int constituentIndex = 0;
			for (ShoppingItemDto childDto : shoppingItemDto.getConstituents()) {
				ConstituentItem item = ((ProductBundle) product).getConstituents().get(constituentIndex).getConstituent();
				SkuInventoryDetails childDetails;
				if (item.isProductSku()) {
					childDetails = getSkuInventoryDetailsForNode(item.getProductSku(),
							store, item.getProduct(), inventoryRequirementsMap, childDto, inBundle, skuInventoryMap);
				} else {
					childDetails = getSkuInventoryDetailsForNode(item.getProduct().getSkuByCode(childDto.getSkuCode()),
							store, item.getProduct(), inventoryRequirementsMap, childDto, inBundle, skuInventoryMap);
				}
				worstInventoryDetails = getWorst(childDetails, worstInventoryDetails);
				constituentIndex++;
			}
		} else if (product instanceof ProductBundle) { //use default skus
			ProductBundle bundle = (ProductBundle) product;
			for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
				
				ConstituentItem constituent = bundleConstituent.getConstituent();
				SkuInventoryDetails childDetails = getSkuInventoryDetailsForNode(
						constituent.getProductSku(), store, constituent.getProduct(),
						inventoryRequirementsMap, null, inBundle, skuInventoryMap);
				
				worstInventoryDetails = getWorst(childDetails, worstInventoryDetails);
			}
		} else {
			worstInventoryDetails = new SkuInventoryDetails();
			int requiredQuantity = 1;
			if (inventoryRequirementsMap.containsKey(productSku.getSkuCode())) {
				requiredQuantity = inventoryRequirementsMap.get(productSku.getSkuCode());
			} 
			
			NodeInventory nodeInventory = null;
			int quantityRequiringPreBackOrderAllocation = 0;
			if (product.getAvailabilityCriteria() != AvailabilityCriteria.ALWAYS_AVAILABLE) {
				InventoryDto skuInventory = null;
				if (skuInventoryMap == null) {
					skuInventory = productInventoryManagementService.getInventory(productSku.getSkuCode(), store.getWarehouse().getUidPk());
				} else {
					skuInventory = skuInventoryMap.get(productSku.getSkuCode());
				}
				nodeInventory = calculateAvailableQuantityInStock(productSku, requiredQuantity, skuInventory);
				worstInventoryDetails.setAvailableQuantityInStock(nodeInventory.getNodeInventoryAvailable());
				worstInventoryDetails.setStockDate(nodeInventory.getRestockDate());
				
				quantityRequiringPreBackOrderAllocation = calculateQuantityRequiringPreOrderBackOrder(
						requiredQuantity, productSku, store, skuInventory);
			}
			
			worstInventoryDetails.setAvailabilityCriteria(product.getAvailabilityCriteria());
			
			worstInventoryDetails.setMessageCode(getMessageCode(productSku, nodeInventory, inBundle, requiredQuantity));
			if (quantityRequiringPreBackOrderAllocation > 0) {
				worstInventoryDetails.setHasSufficientUnallocatedQty(
					calculateHasSufficientUnallocatedQty(quantityRequiringPreBackOrderAllocation, productSku, worstInventoryDetails));
			}
		}
		
		return worstInventoryDetails;
	}

	/**
	 * @param quantity quantity
	 * @param productSku productSku
	 * @param store store
	 * @param skuInventory The sku inventory
	 * @return quantity that can't be fulfilled from inventory in stock
	 */
	protected int calculateQuantityRequiringPreOrderBackOrder(
			final int quantity, final ProductSku productSku, final Store store,
			final InventoryDto skuInventory) {
		if (skuInventory == null) {
			return quantity;
		}
		return quantity - skuInventory.getAvailableQuantityInStock();
	}

	
	/**
	 * @param quantity the required quantity for allocation
	 * @param productSku the productSku
	 * @param skuInventoryDetails the skuInventoryDetails
	 * @return true if we can allocate (PRE/BACKORDER) the required quantity.
	 */
	@SuppressWarnings("fallthrough")
	private boolean calculateHasSufficientUnallocatedQty(final int quantity,
			final ProductSku productSku, final SkuInventoryDetails skuInventoryDetails) {
		if (quantity <= 0) {
			throw new EpDomainException("Invalid argument: cannot check for zero or negative quantity");
		}
		
		boolean result = false;
		switch (skuInventoryDetails.getMessageCode()) {
		case OUT_OF_STOCK:
		case OUT_OF_STOCK_WITH_RESTOCK_DATE:
			result = false;
			break;
		case AVAILABLE_FOR_BACKORDER:
		case AVAILABLE_FOR_PREORDER:
			PreOrBackOrderDetails preOrBackOrderDetails = productInventoryManagementService.getPreOrBackOrderDetails(productSku.getSkuCode());
			final boolean supportsPreOrBackOrderLimit = productInventoryManagementService.getInventoryCapabilities().supports(
					InventoryCapabilities.PRE_OR_BACK_ORDER_LIMIT);
			final boolean hasOrderLimit = preOrBackOrderDetails.getLimit() > 0;
			
			if (supportsPreOrBackOrderLimit && hasOrderLimit) {
				int unallocatedPreBackOrderQty = preOrBackOrderDetails.getLimit() - preOrBackOrderDetails.getQuantity();
				int neededInventoryQty = quantity - unallocatedPreBackOrderQty;
				if (neededInventoryQty > 0) {
					result = skuInventoryDetails.getAvailableQuantityInStock() > 0;
				} else {
					result = true;
				}
			} else {
				result = true;
			}
			break;
		default:
			result = true;
		}
		return result;
	}

	private SkuInventoryDetails getWorst(final SkuInventoryDetails aDetails,
			final SkuInventoryDetails bDetails) {
		
		// The first run through the pairwise comparisons will have a null for one argument.
		if (aDetails == null) {
			return bDetails;
		}
		if (bDetails == null) {
			return aDetails;
		}
		
		
		SkuInventoryDetailsComparable aComparable = new SkuInventoryDetailsComparable(aDetails);
		SkuInventoryDetailsComparable bComparable = new SkuInventoryDetailsComparable(bDetails);
		
		if (aComparable.getOrdinal() == bComparable.getOrdinal()) {
			if (aDetails.getMessageCode() == InventoryMessage.IN_STOCK) {
				//worst case is smallest inventory
				if (aDetails.getAvailableQuantityInStock() < bDetails.getAvailableQuantityInStock()) {
					return aDetails;
				}
				return bDetails;
			}
			//worst case is latest restock date
			if (aDetails.getStockDate() != null
					&& bDetails.getStockDate() != null
					&& (aDetails.getStockDate().compareTo(bDetails
							.getStockDate())) < 0) {
				return bDetails;
			}
			return aDetails;
		} else if (aComparable.getOrdinal() > bComparable.getOrdinal()) {
			return aDetails;
		}
		return bDetails;
	}

	
	/**
	 * Get message status code for the minimum order quantity.
	 * If we're checking for a product in a bundle, minimum order quantity is ignored and defaults to 1.
	 * 
	 * @param productSku sku
	 * @param nodeInventory inventory with availability and re-stock
	 * @param inBundle we're looking at a product in a bundle
	 * @param quantity the quantity requested (irrelevant if the sku is in a bundle)
	 * @return inventory availability message
	 */
	protected InventoryMessage getMessageCode(final ProductSku productSku, final NodeInventory nodeInventory, 
			final boolean inBundle, final int quantity) {
		
		Product product = productSku.getProduct();
		final int minOrderQuantity = product.getMinOrderQty();
		int requiredQuantity = Math.max(quantity, minOrderQuantity);
		if (inBundle) {
			requiredQuantity = 1;
		}
		
		InventoryMessage statusCode;

		// Note: Only valid on the leaf node.
		switch (product.getAvailabilityCriteria()) {
		case ALWAYS_AVAILABLE:
			statusCode = InventoryMessage.IN_STOCK;
			break;
		case AVAILABLE_WHEN_IN_STOCK:
			statusCode = handleInStockAvailability(requiredQuantity, nodeInventory, inBundle);
			break;
		case AVAILABLE_FOR_PRE_ORDER:
			statusCode = handlePreBackOrderAvailability(productSku, requiredQuantity, nodeInventory, 
					InventoryMessage.AVAILABLE_FOR_PREORDER, inBundle);
			break;
		case AVAILABLE_FOR_BACK_ORDER:
			statusCode = handlePreBackOrderAvailability(productSku, requiredQuantity, nodeInventory, 
					InventoryMessage.AVAILABLE_FOR_BACKORDER, inBundle);
			break;
		default:
			throw new EpDomainException("Unknown availability criteria");
		}
		return statusCode;
	}
	
	private InventoryMessage handleInStockAvailability(final int requiredQuantity, 
			final NodeInventory nodeInventory, final boolean inBundle) {
		InventoryMessage statusCode = InventoryMessage.IN_STOCK;
		
		if ((inBundle && !hasEnoughNodeInventory(requiredQuantity, nodeInventory)) 
				|| (!inBundle && !hasEnoughSkuInventory(requiredQuantity, nodeInventory))) {
			if (nodeInventory.getRestockDate() == null) {
				statusCode = InventoryMessage.OUT_OF_STOCK;
			} else {
				statusCode = InventoryMessage.OUT_OF_STOCK_WITH_RESTOCK_DATE;
			}
		}
		return statusCode;
	}
	
	private boolean hasEnoughSkuInventory(final int requiredQuantity, final NodeInventory nodeInventory) {
		return nodeInventory.getSkuInventoryAvailable() >= requiredQuantity;
	}

	private InventoryMessage handlePreBackOrderAvailability(final ProductSku productSku, final int requiredQuantity, 
			final NodeInventory nodeInventory, final InventoryMessage message, final boolean inBundle) {
		InventoryMessage statusCode = InventoryMessage.IN_STOCK;
		if ((inBundle && !hasEnoughNodeInventory(requiredQuantity, nodeInventory)) 
				|| (!inBundle && !hasEnoughSkuInventory(requiredQuantity, nodeInventory))) {
			 // required quantity is greater than the available qty on hand (already checked that)
			int quantityToCheck = requiredQuantity - nodeInventory.getNodeInventoryAvailable();
			if (isPreOrBackOrderLimitReached(quantityToCheck, productSku)) {
				statusCode = InventoryMessage.OUT_OF_STOCK;
			} else {
				statusCode = message;
			}
		}
		return statusCode;
	}
	
	/**
	 * Returns true if the order limit has been reached.
	 * 
	 * @param quantity the quantity to be checked
	 * @return true if limit is reached
	 */
	private boolean isPreOrBackOrderLimitReached(final int quantity, final ProductSku productSku) {
		final boolean supportsPreOrBackOrderLimit = productInventoryManagementService.getInventoryCapabilities().supports(
				InventoryCapabilities.PRE_OR_BACK_ORDER_LIMIT);
		PreOrBackOrderDetails preOrBackOrderDetails = productInventoryManagementService.getPreOrBackOrderDetails(productSku.getSkuCode());
		if (!supportsPreOrBackOrderLimit || preOrBackOrderDetails.getLimit() == 0) {
			// Unlimited quantity available.
			return false;
		}
		int orderedPlusToBeOrdered = preOrBackOrderDetails.getQuantity() + quantity;
		return orderedPlusToBeOrdered > preOrBackOrderDetails.getLimit();
	}
	
	/**
	 * Finds the quantity in stock.
	 */
	private boolean hasEnoughNodeInventory(final int requiredQuantity, final NodeInventory nodeInventory) {
		return nodeInventory.getNodeInventoryAvailable() >= requiredQuantity;
	}
	
	/**
	 * Calculates available quantity in stock for a product.
	 *  
	 * @param productSku
	 * @param skuInventory The inventory for the sku.
	 * @return
	 */
	private NodeInventory calculateAvailableQuantityInStock(final ProductSku productSku,
			final int inventoryRequired, final InventoryDto skuInventory) {
		Date restockDate = null;
		int nodeInventoryAvailable = 0;	
		int availableQuantityInStock = 0;
		if (skuInventory != null) {
			availableQuantityInStock = skuInventory.getAvailableQuantityInStock();
			
			// Note that Java will round towards zero in this implicit
			// conversion.
			nodeInventoryAvailable = availableQuantityInStock / inventoryRequired;
			
			Product product = productSku.getProduct();
			// The restock date should only be set if we have no stock
			if (nodeInventoryAvailable <= 0) {
				if (product.getAvailabilityCriteria() == AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK
						|| product.getAvailabilityCriteria() == AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER) {
					restockDate = skuInventory.getRestockDate();
				} else if (product.getAvailabilityCriteria() == AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER) {
					restockDate = product.getExpectedReleaseDate();
				}
			}
		}
		return new NodeInventory(nodeInventoryAvailable, availableQuantityInStock, restockDate);
	}
	
	@Override
	public PreOrBackOrderDetails getPreOrBackOrderDetails(final ProductSku productSku) {
		return getProductInventoryManagementService().getPreOrBackOrderDetails(productSku.getSkuCode());
	}
	
	@Override
	public InventoryDto getInventory(final InventoryKey inventoryKey) {
		return getProductInventoryManagementService().getInventory(inventoryKey.getSkuCode(), inventoryKey.getWarehouseUid());
	}
	
	@Override
	public Capabilities getInventoryCapabilities() {
		return productInventoryManagementService.getInventoryCapabilities();
	}
	
	/**
	 * Get the ProductInventoryManagementService.
	 * 
	 * @return The ProductInventoryManagementService.
	 */
	protected ProductInventoryManagementService getProductInventoryManagementService() {
		return productInventoryManagementService;
	}
	
	/**
	 * Set the ProductInventoryManagementService.
	 * 
	 * @param productInventoryManagementService The ProductInventoryManagementService.
	 */
	public void setProductInventoryManagementService(final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
	}

	/**
	 * Inner class that acts as a tuple for returning inventory and re-stock date.
	 */
	protected class NodeInventory {
		
		private final int nodeInventoryAvailable;
		private final int skuInventoryAvailable;
		private final Date restockDate;
		
		/**
		 * @param nodeInventoryAvailable the buy-able quantity within a bundle.
		 * @param skuInventoryAvailable the inventory available for the sku.
		 * @param restockDate date
		 */
		NodeInventory(final int nodeInventoryAvailable, final int skuInventoryAvailable, final Date restockDate) {
			this.nodeInventoryAvailable = nodeInventoryAvailable;
			this.restockDate = restockDate;
			this.skuInventoryAvailable = skuInventoryAvailable;
		}

		/**
		 * @return the nodeInventoryAvailable
		 */
		int getNodeInventoryAvailable() {
			return nodeInventoryAvailable;
		}

		/**
		 * @return the restockDate
		 */
		Date getRestockDate() {
			return restockDate;
		}
		
		/**
		 * @return the skuInventoryAvailable
		 */
		int getSkuInventoryAvailable() {
			return skuInventoryAvailable;
		}
	}
	
	/**
	 * Calculates the rank of a SkuInventoryDetails object, assuming that
	 * Out Of Stock is the highest rank, and always available is the lowest rank (0).
	 */
	protected class SkuInventoryDetailsComparable {
		
		private static final int OUT_OF_STOCK_ORDINAL = 5;
		private static final int OUT_OF_STOCK_WITH_RESTOCK_DATE_ORDINAL = 4;
		private static final int AVAILABLE_FOR_PREORDER_ORDINAL = 3;
		private static final int AVAILABLE_FOR_BACKORDER_ORDINAL = 2;
		private static final int INSTOCK_NOT_ALWAYS_AVAILABLE_ORDINAL = 1;
		private static final int INSTOCK_ALWAYS_AVAILABLE_ORDINAL = 0;
		private final SkuInventoryDetails inventory;
		
		/**
		 * @param inventory SkuInventoryDetails
		 */
		SkuInventoryDetailsComparable(final SkuInventoryDetails inventory) {
			this.inventory = inventory;
		}
		
		/**
		 * @return ordinality of the stock inventory message.
		 */
		int getOrdinal() {
			InventoryMessage message = inventory.getMessageCode();
			if (message == InventoryMessage.OUT_OF_STOCK) {
				return OUT_OF_STOCK_ORDINAL;
			} else if (message == InventoryMessage.OUT_OF_STOCK_WITH_RESTOCK_DATE) {
				return OUT_OF_STOCK_WITH_RESTOCK_DATE_ORDINAL;
			} else if (message == InventoryMessage.AVAILABLE_FOR_PREORDER) {
				return AVAILABLE_FOR_PREORDER_ORDINAL;
			} else if (message == InventoryMessage.AVAILABLE_FOR_BACKORDER) {
				return AVAILABLE_FOR_BACKORDER_ORDINAL;
			} else if (message == InventoryMessage.IN_STOCK 
					&& !inventory.getAvailabilityCriteria().equals(AvailabilityCriteria.ALWAYS_AVAILABLE)) {
				return INSTOCK_NOT_ALWAYS_AVAILABLE_ORDINAL;
			}
			return INSTOCK_ALWAYS_AVAILABLE_ORDINAL;
		}
	}
	
	@Override
	public Map<String, Map<String, SkuInventoryDetails>> getSkuInventoryDetailsForAllSkus(final List<Product> products, final Store store) {
		Map<String, Map<String, SkuInventoryDetails>> productSkuInventoryDetailsMap = new HashMap<String, Map<String, SkuInventoryDetails>>();

		Set<String> allProductsSkuCodes = new HashSet<String>();
		for (Product product : products) {
			allProductsSkuCodes.addAll(getSkuCodesForInventoryLookup(product, store));
		}

		Map<String, InventoryDto> skuInventoryMap = productInventoryManagementService.getInventoriesForSkusInWarehouse(
				allProductsSkuCodes, store.getWarehouse().getUidPk());

		for (Product product : products) {
			boolean inBundle = product instanceof ProductBundle;
			productSkuInventoryDetailsMap.put(product.getCode(), getSkuInventoryDetailsMapForSkus(product, store, inBundle, skuInventoryMap));
		}

		return productSkuInventoryDetailsMap;
	}
}