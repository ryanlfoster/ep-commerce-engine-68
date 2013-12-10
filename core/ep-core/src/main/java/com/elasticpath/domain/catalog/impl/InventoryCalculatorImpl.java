package com.elasticpath.domain.catalog.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.common.dto.InventoryDetails;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.InventoryCalculator;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.catalog.ProductInventoryManagementService;

/**
 * Implements {@code InventoryCalculator}.
 */
public class InventoryCalculatorImpl implements InventoryCalculator {
	
	private BeanFactory beanFactory;
	
	@Override
	public InventoryDetails getInventoryDetails(
			final ProductInventoryManagementService productInventoryManagementService,
			final ProductSku productSku, final long warehouseUid) {
		InventoryDetails inventoryDetails = createInventoryDetails();
		
		int availableQuantityInStock = calculateAvailableQuantityInStock(
				productInventoryManagementService, productSku, warehouseUid); 
		
		inventoryDetails.setAvailableQuantityInStock(availableQuantityInStock);
		
		return inventoryDetails;
	}

	/**
	 * Creates an InventoryDetails object.
	 * @return the object.
	 */
	protected InventoryDetails createInventoryDetails() {
		return beanFactory.getBean(ContextIdNames.INVENTORY_DETAILS);
	}

	/**
	 * Calculate the inventory for a normal product and a bundle.
	 */
	private int calculateAvailableQuantityInStock(
			final ProductInventoryManagementService productInventoryManagementService, 
			final ProductSku productSku,
			final long warehouseUid) {
		
			Map<String, Integer> inventoryRequirementsMap = new HashMap<String, Integer>();
			addInventoryRequirementsToMap(productSku, inventoryRequirementsMap, 1);
			
			int rootInventoryAvailable = Integer.MAX_VALUE;
			if (inventoryRequirementsMap.isEmpty()) {
				return rootInventoryAvailable;
			}
			
			Map<String, InventoryDto> inventoryMap = productInventoryManagementService.getInventoriesForSkusInWarehouse(
					inventoryRequirementsMap.keySet(), warehouseUid);
			for (Map.Entry<String, Integer> mapEntry : inventoryRequirementsMap.entrySet()) {
				String skuCode = mapEntry.getKey();
				int inventoryRequired = mapEntry.getValue();
				InventoryDto skuInventory = inventoryMap.get(skuCode);
				int skuInventoryAvailable = 0;
				int itemInventoryAvailable = 0;
				if (skuInventory != null) {
					skuInventoryAvailable = skuInventory.getAvailableQuantityInStock();
				
					// Note that Java will round towards zero in this implicit conversion.
					itemInventoryAvailable = skuInventoryAvailable / inventoryRequired;
				}
				rootInventoryAvailable = Math.min(rootInventoryAvailable, itemInventoryAvailable);
			}
			
			return rootInventoryAvailable;
		}

	/**
	 * Adds the inventory requirements for {@productSku} and children to {@code inventoryRequirementsMap}.
	 * @param quantityPerUnit The quantity of this item per unit of the parent. This number is multiplied down the tree.
	 */
	private void addInventoryRequirementsToMap(final ProductSku productSku,
			final Map<String, Integer> inventoryRequirementsMap, final int quantityPerUnit) {
		if (productSku.getProduct() instanceof ProductBundle) {
			ProductBundle bundle = (ProductBundle) productSku.getProduct();
			for (BundleConstituent constituent : bundle.getConstituents()) {
				ConstituentItem child = constituent.getConstituent();
				
				addInventoryRequirementsToMap(child.getProductSku(), inventoryRequirementsMap, quantityPerUnit * constituent.getQuantity());
			}
		} else {
			if (productSku.getProduct().getAvailabilityCriteria() != AvailabilityCriteria.ALWAYS_AVAILABLE) {
				Integer currentInventory = inventoryRequirementsMap.get(productSku.getSkuCode());
				int newInventory;
				if (currentInventory == null) {
					newInventory = quantityPerUnit;
				} else {
					newInventory = currentInventory + quantityPerUnit; 
				}
				inventoryRequirementsMap.put(productSku.getSkuCode(), newInventory);
			}
		}
	}
	
	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
