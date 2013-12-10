/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;
import com.elasticpath.service.shoppingcart.WishListService;

/**
 * <code>ViewWishListController</code> is the implementation of spring controller to load a static page.
 */
public class ViewWishListController extends AbstractEpControllerImpl {

	private String viewName;

	private static final Logger LOG = Logger.getLogger(ViewWishListController.class);

	private ShoppingItemAssembler shoppingItemAssembler;

	private ProductInventoryShoppingService productInventoryShoppingService;

	private WishListService wishListService;

	/**
	 * Return the ModelAndView for the configured static view page.
	 * 
	 * @param request -the current request.
	 * @param response -the current response.
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		LOG.debug("entering 'handleRequestInternal' method...");

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Store store = getRequestHelper().getStoreConfig().getStore();
		final WishList wishList = getWishListService().findOrCreateWishListWithPrice(customerSession);

		final Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("catalog", store.getCatalog());
		modelMap.put("warehouse", store.getWarehouse());
		modelMap.put("availabilityMap", getAvailabilityMap(wishList.getAllItems(), store.getWarehouse(), store));
		modelMap.put("wishListItems", wishList.getAllItems());

		return new ModelAndView(this.viewName, modelMap);
	}

	/**
	 * @param shopingItems list of shopping items
	 * @param warehouse the warehouse to look up
	 * @param store the store to check inventory
	 * @return the availability of shopping items from allocation service
	 */
	protected Map<Long, SkuInventoryDetails> getAvailabilityMap(final List<ShoppingItem> shopingItems, final Warehouse warehouse, final Store store) {
		final Map<Long, SkuInventoryDetails> availabilityMap = new HashMap<Long, SkuInventoryDetails>();
		for (ShoppingItem item : shopingItems) {
			final ShoppingItemDto dto = getShoppingItemAssembler().assembleShoppingItemDtoFrom(item);
			final SkuInventoryDetails skuInventoryDetails = getProductInventoryShoppingService().getSkuInventoryDetails(
					item.getProductSku(), store, dto);

			availabilityMap.put(item.getUidPk(), skuInventoryDetails);
		}
		return availabilityMap;
	}

	/**
	 * Sets the static view name.
	 * 
	 * @param viewName - the static view name.
	 */
	public final void setViewName(final String viewName) {
		this.viewName = viewName;
	}

	/**
	 * @param shoppingItemAssembler the shoppingItemAssembler to set
	 */
	public void setShoppingItemAssembler(final ShoppingItemAssembler shoppingItemAssembler) {
		this.shoppingItemAssembler = shoppingItemAssembler;
	}

	/**
	 * @return the shoppingItemAssembler
	 */
	public ShoppingItemAssembler getShoppingItemAssembler() {
		return shoppingItemAssembler;
	}

	/**
	 * @param productInventoryShoppingService the ProductInventoryShoppingService to set
	 */
	public void setProductInventoryShoppingService(final ProductInventoryShoppingService productInventoryShoppingService) {
		this.productInventoryShoppingService = productInventoryShoppingService;
	}

	/**
	 * @return the ProductInventoryShoppingService
	 */
	public ProductInventoryShoppingService getProductInventoryShoppingService() {
		return productInventoryShoppingService;
	}

	/**
	 * @param wishListService the wishListService to set
	 */
	public void setWishListService(final WishListService wishListService) {
		this.wishListService = wishListService;
	}

	/**
	 * @return the wishListService
	 */
	public WishListService getWishListService() {
		return wishListService;
	}

}
