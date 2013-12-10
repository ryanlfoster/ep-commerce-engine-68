package com.elasticpath.service.catalog.impl;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.domain.catalog.ItemCharacteristics;
import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.ItemConfigurationMemento.ItemConfigurationId;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.impl.ItemCharacteristicsImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ItemCharacteristicsService;
import com.elasticpath.service.catalog.ItemConfigurationService;
import com.elasticpath.service.catalog.ProductCharacteristicsService;
import com.elasticpath.service.catalog.ProductService;

/**
 * Determine item characteristics using other services.
 */
public class ItemCharacteristicsServiceImpl implements ItemCharacteristicsService {

	private ItemConfigurationService itemConfigurationService;
	private ProductService productService;
	private ShoppingItemDtoFactory shoppingItemDtoFactory;
	private ShoppingItemAssembler shoppingItemAssembler;
	private ProductCharacteristicsService productCharacteristicsService;
	
	@Override
	public ItemCharacteristics getItemCharacteristics(final ItemConfigurationId itemConfigurationId) {
		ItemConfiguration itemConfiguration = getItemConfigurationService().load(itemConfigurationId);
		return getItemCharacteristics(itemConfiguration);
	}

	/**
	 * Gets the item characteristics for the given configuration.
	 *
	 * @param itemConfiguration the item configuration
	 * @return the item characteristics
	 */
	protected ItemCharacteristics getItemCharacteristics(final ItemConfiguration itemConfiguration) {
		long productUid = getProductService().findUidBySkuCode(itemConfiguration.getSkuCode());
		Product product = getProductService().get(productUid);
		ShoppingItemDto shoppingItemDto = getShoppingItemDtoFactory().createDto(product, 1, itemConfiguration);
		ShoppingItem shoppingItem = getShoppingItemAssembler().createShoppingItem(shoppingItemDto);
		return createItemCharacteristics(shoppingItem);
	}	

	/**
	 * Creates the item characteristics from a shopping item.
	 *
	 * @param shoppingItem the shopping item
	 * @return the item characteristics
	 */
	protected ItemCharacteristics createItemCharacteristics(final ShoppingItem shoppingItem) {
		ItemCharacteristicsImpl characteristics = new ItemCharacteristicsImpl();
		characteristics.setShippable(shoppingItem.isShippable());
		ProductCharacteristics productCharacteristics = getProductCharacteristicsService().getProductCharacteristics(shoppingItem.getProductSku());
		characteristics.setMultipleConfigurations(productCharacteristics.hasMultipleConfigurations());
		return characteristics;
	}

	/**
	 * Gets the item configuration service.
	 *
	 * @return the item configuration service
	 */
	public ItemConfigurationService getItemConfigurationService() {
		return itemConfigurationService;
	}
	
	/**
	 * Sets the item configuration service.
	 *
	 * @param itemConfigurationService the new item configuration service
	 */
	public void setItemConfigurationService(final ItemConfigurationService itemConfigurationService) {
		this.itemConfigurationService = itemConfigurationService;
	}
	
	/**
	 * Gets the product service.
	 *
	 * @return the product service
	 */
	protected ProductService getProductService() {
		return productService;
	}
	
	/**
	 * Sets the product service.
	 *
	 * @param productService the new product service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}
	
	/**
	 * Gets the shopping item dto factory.
	 *
	 * @return the shopping item dto factory
	 */
	protected ShoppingItemDtoFactory getShoppingItemDtoFactory() {
		return shoppingItemDtoFactory;
	}
	
	/**
	 * Sets the shopping item dto factory.
	 *
	 * @param shoppingItemDtoFactory the new shopping item dto factory
	 */
	public void setShoppingItemDtoFactory(final ShoppingItemDtoFactory shoppingItemDtoFactory) {
		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
	}
	
	/**
	 * Gets the shopping item assembler.
	 *
	 * @return the shopping item assembler
	 */
	protected ShoppingItemAssembler getShoppingItemAssembler() {
		return shoppingItemAssembler;
	}
	
	/**
	 * Sets the shopping item assembler.
	 *
	 * @param shoppingItemAssembler the new shopping item assembler
	 */
	public void setShoppingItemAssembler(final ShoppingItemAssembler shoppingItemAssembler) {
		this.shoppingItemAssembler = shoppingItemAssembler;
	}

	/**
	 * Sets the product characteristics service.
	 *
	 * @param productCharacteristicsService the new product characteristics service
	 */
	public void setProductCharacteristicsService(final ProductCharacteristicsService productCharacteristicsService) {
		this.productCharacteristicsService = productCharacteristicsService;
	}

	/**
	 * Gets the product characteristics service.
	 *
	 * @return the product characteristics service
	 */
	protected ProductCharacteristicsService getProductCharacteristicsService() {
		return productCharacteristicsService;
	}


}

