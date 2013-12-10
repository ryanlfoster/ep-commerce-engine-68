package com.elasticpath.sellingchannel.presentation.impl;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.commons.tree.Functor;
import com.elasticpath.commons.tree.impl.PreOrderTreeTraverser;
import com.elasticpath.commons.tree.impl.TreeNodeMemento;
import com.elasticpath.sellingchannel.presentation.OrderItemPresentationBean;
import com.elasticpath.sellingchannel.presentation.OrderItemPresentationBeanMapper;


/**
 * Default implementation of {@link OrderItemPresentationBeanMapper}.
 */
public class OrderItemPresentationBeanMapperImpl implements OrderItemPresentationBeanMapper {
	/** functor instance. necessary for testing.**/
	private CopyFunctor functor = new CopyFunctor();
	
	/**
	 * Sets the functor. Necessary for testing.
	 * @param functor functor to set
	 */
	protected void setFunctor(final CopyFunctor functor) {
		this.functor = functor;
	}
	
	private final PreOrderTreeTraverser<OrderItemDto, OrderItemDocumentMapperStackMemento> traverser 
		= new PreOrderTreeTraverser<OrderItemDto, OrderItemDocumentMapperStackMemento>();

	@Override
	public OrderItemPresentationBean mapFrom(final OrderItemDto orderItemDto) {
		OrderItemDocumentMapperStackMemento rootMemento = traverser.traverseTree(orderItemDto, null, null, functor, 0);
		return rootMemento.getTreeNode();
	}
	
	/**
	 * Functor for use with {@code PreOrderTreeTraverser}. Copies the {@code OrderItemDto} tree to an {@code OrderItemFormBean} tree.
	 */
	public static class CopyFunctor implements Functor<OrderItemDto, OrderItemDocumentMapperStackMemento> {
		@Override
		public OrderItemDocumentMapperStackMemento processNode(final OrderItemDto sourceNode, final OrderItemDto parentNode,
				final OrderItemDocumentMapperStackMemento parentStackMemento, final int level) {
			
			OrderItemPresentationBean destDocument = new OrderItemPresentationBeanImpl();
			
			destDocument.setLevel(level);
			destDocument.setDisplayName(sourceNode.getDisplayName());
			destDocument.setImage(sourceNode.getImage());
			destDocument.setDisplaySkuOptions(sourceNode.getDisplaySkuOptions());
			destDocument.setSkuCode(sourceNode.getSkuCode());
			destDocument.setAllocated(sourceNode.isAllocated());
			destDocument.setProductSku(sourceNode.getProductSku());
			destDocument.setDigitalAsset(sourceNode.getDigitalAsset());
			destDocument.setEncryptedUidPk(sourceNode.getEncryptedUidPk());
			destDocument.setListPriceMoney(sourceNode.getListPrice());
			destDocument.setUnitPriceMoney(sourceNode.getUnitPrice());
			destDocument.setPrice(sourceNode.getPrice());
			destDocument.setUnitLessThanList(sourceNode.isUnitLessThanList());
			destDocument.setDollarSavingsMoney(sourceNode.getDollarSavings());
			destDocument.setTotalMoney(sourceNode.getTotal());
			destDocument.setInventory(sourceNode.getInventory());
			destDocument.setCalculatedBundle(sourceNode.isCalculatedBundle());
			destDocument.setCalculatedBundleItem(sourceNode.isCalculatedBundleItem());
			setViewFlags(sourceNode, destDocument);
			
			if (parentStackMemento == null) {
				// Root node: This is the quantity the customer asked for.
				destDocument.setQuantity(sourceNode.getQuantity());
				return new OrderItemDocumentMapperStackMemento(destDocument, sourceNode.getQuantity());
			}
			
			parentStackMemento.getTreeNode().addChild(destDocument);
			
			// Child nodes: the quantity on the DTO is the shippable quantity but we only want to display
			// per bundle quantity.
			destDocument.setQuantity(sourceNode.getQuantity() / parentStackMemento.getShippableQuantity());
			
			// By returning the *parent* instead of the new child the *parent* will be passed to the 
			// invocation of this functor for subsequent children. This means that the parent will
			// always be the same node - i.e. the one representing the root of the tree.
			return new OrderItemDocumentMapperStackMemento(parentStackMemento.getTreeNode(), sourceNode.getQuantity());			
		}

		/**
		 * Sets custom view flags for the order item presentation form bean.
		 * 
		 * @param sourceNode order item DTO
		 * @param destDocument order item presentation bean
		 */
		protected void setViewFlags(final OrderItemDto sourceNode, final OrderItemPresentationBean destDocument) {
			setViewFlag(destDocument, "gc", isEmailable(sourceNode));
		}

		/**
		 * Sets the view flag.
		 * 
		 * @param destDocument  order item presentation bean
		 * @param flagName flag name
		 * @param value flag value
		 */
		protected void setViewFlag(final OrderItemPresentationBean destDocument, final String flagName, final boolean value) {
				destDocument.addViewFlag(flagName, value);
		}

		private boolean isEmailable(final OrderItemDto sourceNode) {
			return isGC(sourceNode);
		}

		/**
		 * Checks if the sourceNode is of type GC. 
		 * @param sourceNode order item dto instance
		 * @return true, if sourceNode is of type GC
		 */
		protected boolean isGC(final OrderItemDto sourceNode) {
			return "Gift Certificates".equals(sourceNode.getProductSku().getProduct().getProductType().getName());
		}

	}
	
	/**
	 * StackMemento for communicating the root OrderItemDocument and the shippable quantity from parent to children.
	 */
	private static class OrderItemDocumentMapperStackMemento extends TreeNodeMemento<OrderItemPresentationBean> {

		private final int shippableQuantity;
		
		public OrderItemDocumentMapperStackMemento(final OrderItemPresentationBean treeNode, final int shippableQuantity) {
			super(treeNode);
			this.shippableQuantity = shippableQuantity;
		}

		public int getShippableQuantity() {
			return shippableQuantity;
		}
		
	}
}
