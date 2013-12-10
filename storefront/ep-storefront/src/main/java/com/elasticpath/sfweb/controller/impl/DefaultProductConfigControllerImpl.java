/**
 *
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.exception.InvalidBundleSelectionException;
import com.elasticpath.commons.exception.InvalidBundleTreeStructureException;
import com.elasticpath.commons.exception.InvalidBundleTreeStructureForItemInCartException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.CartItem;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.sfweb.ajax.bean.JsonBundleItemBean;
import com.elasticpath.sfweb.ajax.bean.impl.JsonBundleItemBeanImpl;
import com.elasticpath.sfweb.ajax.service.JsonBundleFactory;
import com.elasticpath.sfweb.ajax.service.JsonBundleService;
import com.elasticpath.sfweb.controller.ProductConfigController;
import com.elasticpath.sfweb.controller.ShoppingItemDtoMapper;
import com.elasticpath.sfweb.controller.ShoppingItemFormBeanContainerFactory;
import com.elasticpath.sfweb.exception.EpWebException;
import com.elasticpath.sfweb.formbean.CartFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBeanContainer;
import com.elasticpath.sfweb.formbean.validator.ShoppingItemValidator;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.web.security.EsapiServletUtils;

/**
 * .
 */
public class DefaultProductConfigControllerImpl implements ProductConfigController {

	private static final String TREE_STRUCTURE_CHANGED_ITEM_INCART = "errors.productBundle.treeStructure.changed.itemIncart";
	private static final String INVALID_SELECTION = "errors.productBundle.invalidSelection";
	private static final String TREE_STRUCTURE_CHANGED = "errors.productBundle.treeStructure.changed";
	private static final Logger LOG = Logger.getLogger(DefaultProductConfigControllerImpl.class);

	private ShoppingItemFormBeanContainerFactory cartFormBeanFactory;
	private SfRequestHelper requestHelper;
	private ShoppingItemAssembler shoppingItemAssembler;
	private String successView;
	private ShoppingItemDtoMapper shoppingItemDtoMapper;
	private CartDirector cartDirector;
	private String checkoutView;
	private ShoppingItemValidator shoppingItemValidator;
	private BeanFactory beanFactory;
	private PriceLookupFacade priceLookupFacade;
	private ProductSkuService productSkuService;
	private JsonBundleFactory jsonBundleFactory;
	private JsonBundleService jsonBundleService;

	@Override
	public Object formBackingObject(final HttpServletRequest request) throws Exception {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final ShoppingItem existingShoppingItem = getExistingShoppingItem(request);
		final StoreProduct product = (StoreProduct) request.getAttribute(WebConstants.REQUEST_BROWSED_PRODUCT);
		ShoppingItemFormBeanContainer formBean = null;
		try {
			if (existingShoppingItem == null) {
				formBean = cartFormBeanFactory.createCartFormBean(product, 1, shoppingCart);
			} else {
				final ShoppingItemDto existingShoppingItemDto = shoppingItemAssembler.assembleShoppingItemDtoFrom(existingShoppingItem);
				formBean = cartFormBeanFactory.createCartFormBean(existingShoppingItemDto, shoppingCart, false);
			}
		} catch (InvalidBundleTreeStructureException e) {
			if (getExistingShoppingItem(request) != null) {
				request.setAttribute(WebConstants.ERROR_CODE, TREE_STRUCTURE_CHANGED_ITEM_INCART);
				throw new InvalidBundleTreeStructureForItemInCartException(e.getMessage(), e);
			}
				request.setAttribute(WebConstants.ERROR_CODE, TREE_STRUCTURE_CHANGED);
			throw e;
		}
		return formBean;
	}

	@Override
	public Map<String, Object> referenceData(final HttpServletRequest request, final Object command, final Errors errors, final int page) {
		final Map<String, Object> model = new HashMap<String, Object>();
		final CartFormBean cartFormBean = (CartFormBean) command;
		model.put("formBeanSelectionRuleMap", getSelectionRuleMap(cartFormBean));
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShopper().getCurrentShoppingCart();
		model.put("recentlyViewedProducts", shoppingCart.getViewHistory().getViewedProducts());
		model.put("cartProducts", shoppingCart.getCartProducts());
		addBundleInfoToRequest(request, cartFormBean, errors, model);
		return model;
	}

	/**
	 * Add the Json bundle representation.
	 *
	 * @param request the http request
	 * @param cartFormBean the cart form bean with shopping items representing a bundle
	 * @param errors validation errors holder
	 * @param model - the model
	 */
	void addBundleInfoToRequest(final HttpServletRequest request, final ShoppingItemFormBeanContainer cartFormBean, final Errors errors,
								final Map<String, Object> model) {
		final ShoppingItemFormBean shoppingItemFormBean = cartFormBean.getCartItems().get(0);
		final Product wrappedProduct = shoppingItemFormBean.getProduct().getWrappedProduct();
		if (wrappedProduct instanceof ProductBundle) {
			JsonBundleItemBean jsonBundleBean = jsonBundleFactory.createJsonBundleFromShoppingItemFormBean(shoppingItemFormBean);
			try {
				jsonBundleService.updateJsonBundle((JsonBundleItemBeanImpl) jsonBundleBean, request);
			} catch (InvalidBundleTreeStructureException e) {
				if (getExistingShoppingItem(request) != null) {
					request.setAttribute(WebConstants.ERROR_CODE, TREE_STRUCTURE_CHANGED_ITEM_INCART);
					throw e;
				}
				errors.reject(TREE_STRUCTURE_CHANGED, "The structure of the has product changed. Please refresh page.");
			}
			final String jsonBundleString = jsonBundleFactory.serialize(jsonBundleBean);

			//request.setAttribute("jsonBundle", jsonBundleString);
			model.put("jsonBundle", jsonBundleString);

			// this is used to initially render price string in velocity,
			// to avoid flashing when price string is updated by ajax code.
			//request.setAttribute("aggregatedPrices", jsonBundleBean.getAggregatedPrices());
			model.put("jsonBundle", jsonBundleString);
		}
	}

	/**
	 * @param cartFormBean form bean to retrieve info
	 * @return populated map with form bean map to selection rule
	 */
	protected Map<String, Integer> getSelectionRuleMap(final ShoppingItemFormBeanContainer cartFormBean) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		// First item is the form bean for bundle page
		ShoppingItemFormBean rootBean = cartFormBean.getCartItems().get(0);
		addPath(map, rootBean);
		for (ShoppingItemFormBean constituentBean : rootBean.getConstituents()) {
			addPath(map, constituentBean);
		}
		return map;
	}

	/**
	 * @param map of form bean paths to selection rule
	 * @param formBean ShoppingItemFormBean with paths
	 */
	protected void addPath(final Map<String, Integer> map, final ShoppingItemFormBean formBean) {
		StoreProduct storeProduct = formBean.getProduct();
		Product wrappedProduct = storeProduct.getWrappedProduct();
		if (wrappedProduct instanceof ProductBundle) {
			ProductBundle bundle = (ProductBundle) wrappedProduct;
			if (bundle.getSelectionRule() == null) {
				map.put(formBean.getPath(), 0);
			} else {
				map.put(formBean.getPath(), bundle.getSelectionRule().getParameter());
			}
		}
	}

	/**
	 * Retrieves the existing shopping item. Returns null if it cannot find.
	 *
	 * @param request the {@link HttpServletRequest}
	 * @return a shopping item
	 */
	ShoppingItem getExistingShoppingItem(final HttpServletRequest request) {
		if (!EsapiServletUtils.hasParameter(request, WebConstants.REQUEST_CART_ITEM_ID)) {
			return null;
		}
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final long cartItemId = getRequestHelper().getLongParameter(request, WebConstants.REQUEST_CART_ITEM_ID, 0L);
		if (cartItemId != 0) {
			return shoppingCart.getCartItemById(cartItemId);
		}
		return null;
	}

	/**
	 * {@inheritDoc} <br>
	 * Called before form binding, allowing interception of the command object.
	 */
	public void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder, final boolean isFormSubmission) {
		Object target = binder.getTarget();
		boolean isCartFormBean = target instanceof CartFormBean;
		if (isFormSubmission && isCartFormBean) {
			ShoppingItemFormBeanContainer formBean = (ShoppingItemFormBeanContainer) target;
			if (formBean.getCartItems().size() >= 1) {
				resetShoppingItemFormBeanSelections(formBean.getCartItems().get(0));
			}
		}
	}

	/**
	 * Reset selections to false.<br>
	 * Done on update cart item form submission to clear existing selections and bind to new form values.
	 *
	 * @param rootSIFormBean {@linkplain ShoppingItemFormBean}
	 */
	protected void resetShoppingItemFormBeanSelections(final ShoppingItemFormBean rootSIFormBean) {
		List<ShoppingItemFormBean> formBeans = rootSIFormBean.getConstituents();
		for (ShoppingItemFormBean siBean : formBeans) {
			siBean.setSelected(false);
		}
	}

	@Override
	public ModelAndView processFinish(final HttpServletRequest request, final HttpServletResponse response, final Object command,
			final BindException errors) throws Exception {
		return this.update(request, command);
	}

	private ModelAndView update(final HttpServletRequest request, final Object command) {
		LOG.debug("entering 'update' method...");

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final CartFormBean cartFormBean = (CartFormBean) command;

		try {
			final ShoppingItem parentItem = addParentItem(cartFormBean, shoppingCart);
			addDependentItems(cartFormBean, shoppingCart, parentItem);
			addAssociations(cartFormBean, shoppingCart);
		} catch (ProductNotPurchasableException e) {
			request.setAttribute(WebConstants.ERROR_CODE, "product.unpurchasable");
			throw new EpWebException(e.getMessage(), e);
		} catch (ProductUnavailableException e) {
			request.setAttribute(WebConstants.ERROR_CODE, "product.unpurchasable");
			LOG.warn("Sku unavailable");
			throw new EpWebException(e.getMessage(), e);
		} catch (InvalidBundleTreeStructureException e) {
			request.setAttribute(WebConstants.ERROR_CODE, TREE_STRUCTURE_CHANGED);
			LOG.warn("InvalidBundleTreeStructureException");
			throw new EpWebException(e.getMessage(), e);
		} catch (InvalidBundleSelectionException e) {
			request.setAttribute(WebConstants.ERROR_CODE, INVALID_SELECTION);
			LOG.warn("InvalidBundleSelectionException");
			throw new EpWebException(e.getMessage(), e);
		}

		ShoppingCart savedShoppingCart = cartDirector.saveShoppingCart(shoppingCart);
		customerSession.setShoppingCart(savedShoppingCart);

		if (EsapiServletUtils.hasParameter(request, WebConstants.REQUEST_UPDATE)
				&& WebConstants.REQUEST_UPDATE_BILLING_REVIEW.equals(request.getParameter(WebConstants.REQUEST_UPDATE))) {
			return new ModelAndView(checkoutView);
		}
		return new ModelAndView(successView);
	}

	private ShoppingItem addParentItem(final CartFormBean cartFormBean, final ShoppingCart shoppingCart) {
		ShoppingItem parentItem;
		final ShoppingItemFormBean parentFormBean = cartFormBean.getRootItem();
		final ShoppingItemDto shoppingItemDto = shoppingItemDtoMapper.mapFrom(parentFormBean);
		if (parentFormBean.isForUpdate()) {
			parentItem = cartDirector.updateCartItem(shoppingCart, parentFormBean.getUpdateShoppingItemUid(), shoppingItemDto);
		} else {
			parentItem = cartDirector.addItemToCart(shoppingCart, shoppingItemDto, null);
		}
		return parentItem;
	}

	private void addDependentItems(final CartFormBean cartFormBean, final ShoppingCart shoppingCart, final ShoppingItem parentItem) {
		final ShoppingItemFormBean parentFormBean = cartFormBean.getRootItem();
		if (parentItem != null && parentFormBean.isForUpdate()) {
			// FIXME: move this code to CartDirector
			// remove existing dependents, we will add back newly selected dependents below
			final List<ShoppingItem> dependents = ((CartItem) parentItem).getDependentItems();
			parentItem.getChildren().removeAll(dependents);
			shoppingCart.getCartItems().removeAll(dependents);
		}
		for (ShoppingItemFormBean dependentFormBean : cartFormBean.getDependentItems()) {
			if (dependentFormBean.getQuantity() > 0) {
				dependentFormBean.setQuantity(parentItem.getQuantity());
				final ShoppingItemDto dependentItemDto = shoppingItemDtoMapper.mapFrom(dependentFormBean);
				cartDirector.addItemToCart(shoppingCart, dependentItemDto, parentItem);
			}
		}
	}

	private void addAssociations(final CartFormBean cartFormBean, final ShoppingCart shoppingCart) {
		for (ShoppingItemFormBean associatedFormBean : cartFormBean.getAssociatedItems()) {
			if (associatedFormBean.getQuantity() > 0) {
				// associatedFormBean.setQuantity(1); // BB-1942, don't force to one, conflicting requirements?
				final ShoppingItemDto associatedItemDto = shoppingItemDtoMapper.mapFrom(associatedFormBean);
				cartDirector.addItemToCart(shoppingCart, associatedItemDto, null);
			}
		}
	}

	/**
	 * Gets the requestHelper instance.
	 *
	 * @return - the current requestHelper instance.
	 */
	private SfRequestHelper getRequestHelper() {
		return this.requestHelper;
	}

	/**
	 * Sets the requestHelper instance.
	 *
	 * @param requestHelper -the requesthelper instance.
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

	/**
	 * Sets the cart update form bean factory.
	 *
	 * @param factory the cart update form bean factory
	 */
	public void setCartFormBeanFactory(final ShoppingItemFormBeanContainerFactory factory) {
		this.cartFormBeanFactory = factory;
	}

	/**
	 * @param shoppingItemAssembler The ShoppingItemAssembler.
	 */
	public void setShoppingItemAssembler(final ShoppingItemAssembler shoppingItemAssembler) {
		this.shoppingItemAssembler = shoppingItemAssembler;
	}

	/**
	 * @param successView the successView to set
	 */
	public void setSuccessView(final String successView) {
		this.successView = successView;
	}

	/**
	 * @param shoppingItemDtoMapper the shoppingItemDtoMapper to set
	 */
	public void setShoppingItemDtoMapper(final ShoppingItemDtoMapper shoppingItemDtoMapper) {
		this.shoppingItemDtoMapper = shoppingItemDtoMapper;
	}

	/**
	 * @param cartDirector the cartDirector to set
	 */
	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	/**
	 * @param checkoutView the checkoutView to set
	 */
	public void setCheckoutView(final String checkoutView) {
		this.checkoutView = checkoutView;
	}

	/**
	 * @param shoppingItemValidator the validator to set
	 */
	public void setShoppingItemValidator(final ShoppingItemValidator shoppingItemValidator) {
		this.shoppingItemValidator = shoppingItemValidator;
	}

	@Override
	public void validate(final ShoppingItemFormBean shoppingItemFormBean, final Errors errors, final int shoppingItemIndex) {
		final ShoppingItemDto shoppingItemDto = shoppingItemDtoMapper.mapFrom(shoppingItemFormBean);
		try {
			cartDirector.validateShoppingItemDto(shoppingItemDto);
		} catch (InvalidBundleTreeStructureException e) {
			errors.reject(TREE_STRUCTURE_CHANGED, "The structure of the has product changed. Please refresh page.");
		} catch (InvalidBundleSelectionException e) {
			errors.reject(INVALID_SELECTION, "Your selection is not valid.");
		}
		if (shoppingItemValidator != null) {
			shoppingItemValidator.validate(shoppingItemFormBean, errors, shoppingItemIndex);
		}
	}

	/**
	 * Set the bean factory.
	 *
	 * @param beanFactory is the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the bean factory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @return the price lookup facade
	 */
	public PriceLookupFacade getPriceLookupFacade() {
		return priceLookupFacade;
	}

	/**
	 * @param priceLookupFacade the price lookup facade
	 */
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}

	/**
	 * @return the product sku service
	 */
	public ProductSkuService getProductSkuService() {
		return productSkuService;
	}

	/**
	 * @param productSkuService the product sku service
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	/**
	 * @param jsonBundleFactory JsonBundleFactory
	 */
	public void setJsonBundleFactory(final JsonBundleFactory jsonBundleFactory) {
		this.jsonBundleFactory = jsonBundleFactory;
	}

	/**
	 * @param jsonBundleService JsonBundleService
	 */
	public void setJsonBundleService(final JsonBundleService jsonBundleService) {
		this.jsonBundleService = jsonBundleService;
	}
}
