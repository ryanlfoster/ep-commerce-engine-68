package com.elasticpath.sellingchannel.director.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.commons.tree.Functor;
import com.elasticpath.commons.tree.TreeNode;
import com.elasticpath.commons.tree.impl.PreOrderTreeTraverser;
import com.elasticpath.commons.tree.impl.TreeNodeMemento;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.sellingchannel.director.OrderItemAssembler;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.store.StoreService;

/**
 * Default implementation of {@link OrderItemAssembler}.
 */
public class OrderItemAssemblerImpl implements OrderItemAssembler {

	private BundleIdentifier bundleIdentifier;
	
	private final PreOrderTreeTraverser<OrderSkuImplTreeNodeAdapter, TreeNodeMemento<OrderItemDto>> traverser 
		= new PreOrderTreeTraverser<OrderSkuImplTreeNodeAdapter, TreeNodeMemento<OrderItemDto>>();

	private ProductInventoryManagementService productInventoryManagementService;
	private StoreService storeService;
	
	@Override
	public OrderItemDto createOrderItemDto(final OrderSku orderSku, final OrderShipment shipment) {
		Store store = getStoreService().findStoreWithCode(shipment.getOrder().getStoreCode());

		TreeNodeMemento<OrderItemDto> rootMemento = traverser
				.traverseTree(new OrderSkuImplTreeNodeAdapter(orderSku, shipment, store, bundleIdentifier, productInventoryManagementService),
						null, null, new CopyFunctor(), 0);
		return rootMemento.getTreeNode();
	}
	
	/**
	 * 
	 * @param bundleIdentifier The BundleIdentifier bean.
	 */
	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}
	
	/**
	 * 
	 * @param productInventoryManagementService to set.
	 */
	public void setProductInventoryManagementService(
			final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * Functor for use with {@code PreOrderTreeTraverser}. Copies the {@code OrderSku} tree to an {@code OrderItemDto} tree.
	 */
	static class CopyFunctor implements Functor<OrderSkuImplTreeNodeAdapter, TreeNodeMemento<OrderItemDto>> {
		@Override
		public TreeNodeMemento<OrderItemDto> processNode(final OrderSkuImplTreeNodeAdapter sourceNode, final OrderSkuImplTreeNodeAdapter parentNode,
				final TreeNodeMemento<OrderItemDto> parentStackMemento, final int level) {
			OrderItemDto destDto = new OrderItemDto();
			
			if (parentStackMemento == null) {
				sourceNode.copyToParent(destDto);
			} else {
				sourceNode.copyToChild(destDto);
				parentStackMemento.getTreeNode().addChild(destDto);
			}
			return new TreeNodeMemento<OrderItemDto>(destDto);
		}
	}
	
	/**
	 * Adapter that allows the getChildren/addChild methods to be used on OrderSku even though they exist,
	 * with different returns types, in the base class ShoppingItem.
	 */
	static class OrderSkuImplTreeNodeAdapter implements TreeNode<OrderSkuImplTreeNodeAdapter> {

		private final BundleIdentifier bundleIdentifier;
		private final OrderSku orderSku;
		private final OrderShipment shipment;
		private final Store store;
		private final ProductInventoryManagementService productInventoryManagementService;

		/**
		 * Normal parameter constructor.
		 *
		 * @param orderSku The sku to wrap.
		 * @param shipment The order shipment to filter by
		 * @param store the order's store
		 * @param bundleIdentifier The BundleIdentifier bean.
		 * @param productInventoryManagementService The Inventory Service.
		 */
		public OrderSkuImplTreeNodeAdapter(final OrderSku orderSku, final OrderShipment shipment, final Store store,
										   final BundleIdentifier bundleIdentifier,
										   final ProductInventoryManagementService productInventoryManagementService) {
			this.bundleIdentifier = bundleIdentifier;
			this.orderSku = orderSku;
			this.store = store;
			this.shipment = shipment;
			this.productInventoryManagementService = productInventoryManagementService;
		}
		
		/**
		 * Copies the fields of the {@code OrderSku} to the {@code destDto}.
		 * @param destDto The dto to copy to.
		 */
		public void copyToParent(final OrderItemDto destDto) {
			copyToChild(destDto);	
		}
		
		/**
		 * Copies the fields of the {@code OrderSku} to the {@code destDto}.
		 * @param destDto The dto to copy to.
		 */
		public void copyToChild(final OrderItemDto destDto) {
			destDto.setDigitalAsset(orderSku.getDigitalAsset());
			destDto.setDisplayName(orderSku.getDisplayName());
			destDto.setEncryptedUidPk(orderSku.getEncryptedUidPk());
			destDto.setImage(orderSku.getImage());
			destDto.setDisplaySkuOptions(orderSku.getDisplaySkuOptions());
			destDto.setAllocated(orderSku.isAllocated());
			destDto.setProductSku(orderSku.getProductSku());
			destDto.setSkuCode(orderSku.getSkuCode());
			destDto.setQuantity(orderSku.getQuantity());
			destDto.setIsBundle(orderSku.isBundle());
			destDto.setCalculatedBundle(bundleIdentifier.isCalculatedBundle(orderSku.getProductSku()));
			if (orderSku.getParent() == null) {
				destDto.setCalculatedBundleItem(false);
			} else {
				destDto.setCalculatedBundleItem(bundleIdentifier.isCalculatedBundle(orderSku.getParent().getProductSku()));
			}
			
			// required to for setAmount() to be called (used buy dollar savings)
			// FIXME: this should be done when order is created!
			((OrderSkuImpl) orderSku).enableRecalculation();
			
			destDto.setListPrice(orderSku.getListUnitPrice());
			destDto.setUnitPrice(orderSku.getUnitPriceMoney());
			destDto.setPrice(orderSku.getPrice());
			destDto.setDollarSavings(orderSku.getDollarSavingsMoney());
			destDto.setTotal(orderSku.getTotal());
			
			destDto.setInventory(getInventory(orderSku.getSkuCode(), getWarehouseUidPk()));
		}

		private InventoryDto getInventory(final String skuCode, final Long warehouseUid) {
			return productInventoryManagementService.getInventory(skuCode, warehouseUid);
		}

		/**
		 * Gets the warehouse uidpk.
		 * @return warehouse uidpk.
		 */
		protected long getWarehouseUidPk() {
			return store.getWarehouse().getUidPk();
		}

		@Override
		public void addChild(final OrderSkuImplTreeNodeAdapter child) {
			orderSku.addChildItem(child.orderSku);
		}

		@Override
		public List<OrderSkuImplTreeNodeAdapter> getChildren() {
			if (orderSku.isBundle()) {
				List<OrderSkuImplTreeNodeAdapter> orderSkuList = new ArrayList<OrderSkuImplTreeNodeAdapter>(orderSku.getBundleItems().size());
				for (ShoppingItem shoppingItem : orderSku.getBundleItems()) {
					OrderSku candidateSku = (OrderSku) shoppingItem;
					if (hasChildrenInShipment(candidateSku, this.shipment) 
							|| isInShipment(candidateSku, this.shipment)) {
						orderSkuList.add(new OrderSkuImplTreeNodeAdapter(candidateSku, shipment, store, bundleIdentifier,
								productInventoryManagementService));
					} 
				}
				return orderSkuList;
			}
			
			return Collections.emptyList();
		}

		private boolean hasChildrenInShipment(final ShoppingItem item, final OrderShipment shipment) {
			boolean result = false;
			if (item.isBundle()) {
				for (ShoppingItem shoppingItem : item.getBundleItems()) {
					OrderSku candidateSku = (OrderSku) shoppingItem;
					if (candidateSku.isBundle()) {
						result = result || hasChildrenInShipment(candidateSku, shipment);
					} else {
						result = result || isInShipment(candidateSku, shipment);
					}
					if (result) {
						break;
					}
				}
			}
			return result;
		}

		private boolean isInShipment(final OrderSku candidateSku, final OrderShipment shipment) {
			return candidateSku.getShipment() != null && candidateSku.getShipment().getUidPk() == shipment.getUidPk();
		}
	}
}
