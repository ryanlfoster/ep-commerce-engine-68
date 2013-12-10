package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.beanframework.MessageSource;
import com.elasticpath.domain.catalog.AvailabilityException;
import com.elasticpath.domain.catalog.InsufficientInventoryException;
import com.elasticpath.domain.catalog.MinOrderQtyException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.inventory.InventoryCapabilities;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.shoppingcart.ShoppingCartEmptyException;
import com.elasticpath.service.shoppingcart.actions.CheckoutAction;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * CheckoutAction to validate whether there is sufficient stock available for physical products.
 */
public class StockCheckerCheckoutAction implements CheckoutAction {

	private AllocationService allocationService;

	private ProductInventoryShoppingService productInventoryShoppingService;

	private MessageSource messageSource;

	/**
	 * Enumeration representing text IDs for error messages.
	 */
	public static enum ErrorMessage {
		/** The inventory message Key. */
		INVENTORY("globals.cart.insufficientinventory"),
		/** The unavailable message Key. */
		UNAVAILABLE("globals.cart.unavailable");

		/**
		 * Constructs this enumeration.
		 *
		 * @param message the message key to be used for getting the real text
		 */
		ErrorMessage(final String message) {
			this.messageKey = message;
		}

		private final String messageKey;

		/**
		 * Gets the message key.
		 *
		 * @return the message key
		 */
		public String message() {
			return this.messageKey;
		}

		/**
		 * @param messageSource - the messageSource
		 * @param locale - the locale to retrieve from
		 * @return - the attached message from the properties file.
		 */
		public String getTextForMessage(final MessageSource messageSource, final Locale locale) {
			return messageSource.getMessage(this.messageKey, null, this.messageKey, locale);
		}
	}

	@Override
	public void execute(final CheckoutActionContext context) {
		final ShoppingCart shoppingCart = context.getShoppingCart();

		if (shoppingCart.getNumItems() <= 0) {
			throw new ShoppingCartEmptyException("Shopping cart must not be empty during checkout.", shoppingCart);
		}

		final Warehouse fulfillingWarehouse = getFulfillingWarehouse(shoppingCart);
		final TemporaryInventory inventory = new TemporaryInventory(fulfillingWarehouse, productInventoryShoppingService);

		for (final ShoppingItem shoppingItem : shoppingCart.getLeafShoppingItems()) {
			this.verifyInventory(shoppingItem, fulfillingWarehouse, shoppingCart.getLocale());
			this.verifyAvailability(shoppingItem, shoppingCart.getLocale());

			this.verifyCartItemInventory(shoppingItem, inventory, shoppingCart.getLocale());
		}
		for (final ShoppingItem shoppingItem : shoppingCart.getRootShoppingItems()) {
			if (shoppingItem.isBundle()) {
				continue;
			}
			this.verifyMinOrderQty(shoppingItem);
		}
	}

	/**
	 * Verifies that the cart item is still available for order.
	 *
	 * @param shoppingItem the cartItem to be checked
	 * @param locale of error if there is one
	 * @throws AvailabilityException if any of the items in the cart are no longer available
	 */
	protected void verifyAvailability(final ShoppingItem shoppingItem, final Locale locale) {
		if ((shoppingItem.getProductSku() != null) && !shoppingItem.getProductSku().isWithinDateRange()) {
			this.setCartItemErrorMessage(shoppingItem, ErrorMessage.UNAVAILABLE, locale);
			throw new AvailabilityException("Unavailable SKU code: " + shoppingItem.getProductSku().getSkuCode());
		}
	}

	/**
	 * Checks for each cart item.
	 *
	 * @param shoppingItem a shopping item
	 * @param inventory a temporary inventory object
	 * @param locale the locale
	 * @throws InsufficientInventoryException if there is not sufficient inventory
	 */
	protected void verifyCartItemInventory(final ShoppingItem shoppingItem, final TemporaryInventory inventory, final Locale locale) {
		if (!inventory.hasSufficient(shoppingItem)) {
			setCartItemErrorMessage(shoppingItem, ErrorMessage.INVENTORY, locale);
			throw new InsufficientInventoryException("PRODUCT_CODE:" + shoppingItem.getProductSku().getProduct().getCode() + "(SKU: "
					+ shoppingItem.getProductSku().getSkuCode() + ")");
		}
	}

	/**
	 * Verifies that there is sufficient inventory available to satisfy the order.
	 *
	 * @param shoppingItem the cart item to be checked
	 * @param warehouse the warehouse to check inventory
	 * @param locale the locale to be used
	 * @throws InsufficientInventoryException if there is not enough stock available
	 */
	protected void verifyInventory(final ShoppingItem shoppingItem, final Warehouse warehouse, final Locale locale) {
		if (!allocationService.hasSufficientUnallocatedQty(shoppingItem.getProductSku(), warehouse.getUidPk(), shoppingItem.getQuantity())) {
			this.setCartItemErrorMessage(shoppingItem, ErrorMessage.INVENTORY, locale);
			throw new InsufficientInventoryException(shoppingItem.getProductSku().getProduct().getDisplayName(locale)
					+ "(SKU: " + shoppingItem.getProductSku().getSkuCode()
					+ ", WAREHOUSE: " + warehouse.getCode() + ')');
		}
	}

	/**
	 * Verifies that the quantity of items in the cart item are not below the minimum order quantity.
	 *
	 * @param shoppingItem the cartItem to be checked
	 * @throws MinOrderQtyException if any of the items in the cart are no longer available
	 */
	protected void verifyMinOrderQty(final ShoppingItem shoppingItem) {
		if (shoppingItem.getQuantity() < shoppingItem.getMinOrderQty()) {
			throw new MinOrderQtyException("SKU: " + shoppingItem.getProductSku().getSkuCode());
		}
	}

	private Warehouse getFulfillingWarehouse(final ShoppingCart shoppingCart) {
		return shoppingCart.getStore().getWarehouse();
	}

	/**
	 * Sets the error message to the cart item.
	 *
	 * @param shoppingItem the cart item
	 * @param error the error message
	 * @param locale of the message
	 */
	protected void setCartItemErrorMessage(final ShoppingItem shoppingItem, final ErrorMessage error, final Locale locale) {
		String cartErrorMessage = "";
		if (shoppingItem.getErrorMessage() != null) {
			cartErrorMessage = shoppingItem.getErrorMessage();
		}
		final String errorMessage = cartErrorMessage
		+ error.getTextForMessage(messageSource, locale);
		shoppingItem.setErrorMessage(errorMessage);
	}

	protected AllocationService getAllocationService() {
		return allocationService;
	}

	public void setAllocationService(final AllocationService allocationService) {
		this.allocationService = allocationService;
	}

	protected ProductInventoryShoppingService getProductInventoryShoppingService() {
		return productInventoryShoppingService;
	}

	public void setProductInventoryShoppingService(final ProductInventoryShoppingService productInventoryShoppingService) {
		this.productInventoryShoppingService = productInventoryShoppingService;
	}

	protected MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}

/**
 * Tracks total deductions from inventory for all cart items rather than
 * checking each item individually, ensuring there is inventory for all the
 * items in the cart together.  If each item was checked individually then
 * if 2 entries for the same low-stocked SKU existed, one stand-alone and one
 * as a dependent item, then they could both be cleared for inventory when in
 * fact there may only be enough inventory for one entry.
 */
class TemporaryInventory {
	/**
	 * Temporary inventory map. Maps SKU code to the number of items available in inventory.
	 */
	private final Map<String, Integer> temporaryInventoryMap = new HashMap<String, Integer>();

	/** The warehouse that will be fulfilling the inventory requests. */
	private final Warehouse fulfillingWarehouse;

	private final ProductInventoryShoppingService productInventoryShoppingService;

	/**
	 * Create an instance that will fulfill the inventory using the specified
	 * warehouse.
	 * 
	 * @param warehouse the warehouse that will fulfill the inventory.
	 * @param productInventoryShoppingService The inventory service to use for inventory retrieval.
	 */
	public TemporaryInventory(final Warehouse warehouse, final ProductInventoryShoppingService productInventoryShoppingService) {
		this.fulfillingWarehouse = warehouse;
		this.productInventoryShoppingService = productInventoryShoppingService;
	}

	/**
	 *
	 * @param shoppingItem - the cart item to check
	 * @return true if there is enough inventory, false otherwise
	 */
	@SuppressWarnings("fallthrough")
	public boolean hasSufficient(final ShoppingItem shoppingItem) {
		final ProductSku productSku = shoppingItem.getProductSku();
		final Product product = productSku.getProduct();
		int preOrBackOrderQtyLeft = 0;

		switch (product.getAvailabilityCriteria()) {
		case ALWAYS_AVAILABLE:
			return true;
		case AVAILABLE_FOR_BACK_ORDER:
		case AVAILABLE_FOR_PRE_ORDER:
			final boolean supportsPreOrBackOrderLimit = productInventoryShoppingService.getInventoryCapabilities().supports(
					InventoryCapabilities.PRE_OR_BACK_ORDER_LIMIT);
			final PreOrBackOrderDetails preOrBackOrderDetails = productInventoryShoppingService.getPreOrBackOrderDetails(productSku);

			if (supportsPreOrBackOrderLimit && product.getPreOrBackOrderLimit() > 0) {
				preOrBackOrderQtyLeft = preOrBackOrderDetails.getLimit() - preOrBackOrderDetails.getQuantity();
			} else {
				// unlimited qty and therefore we can set the amount left to the qty of the cart item
				// in order to get in stock result from the temp inventory check
				preOrBackOrderQtyLeft = shoppingItem.getQuantity();
			}
			break;
		default:
			// do nothing
		}

		Integer inventoryQty = this.temporaryInventoryMap.get(productSku.getSkuCode());
		if (inventoryQty == null) {
			InventoryKey key = new InventoryKey(productSku.getSkuCode(), fulfillingWarehouse.getUidPk());
			final InventoryDto newInventory = productInventoryShoppingService.getInventory(key);
			if (newInventory != null) {
				inventoryQty = newInventory.getAvailableQuantityInStock();
				this.temporaryInventoryMap.put(productSku.getSkuCode(), inventoryQty);
			}
		}

		if (inventoryQty == null) {
			inventoryQty = Integer.valueOf(0);
		}

		inventoryQty += preOrBackOrderQtyLeft;

		this.temporaryInventoryMap.put(productSku.getSkuCode(), inventoryQty - shoppingItem.getQuantity());

		if (this.temporaryInventoryMap.get(productSku.getSkuCode()) < 0) {
			return false;
		}

		return true;
	}
}
