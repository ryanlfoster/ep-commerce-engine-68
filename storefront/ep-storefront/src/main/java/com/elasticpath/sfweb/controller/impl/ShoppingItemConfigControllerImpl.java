package com.elasticpath.sfweb.controller.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.sellingchannel.TemplateModelFactory;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.catalogview.ProductViewService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.controller.ProductConfigController;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBeanContainer;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.web.security.AbstractSecurityWizardFormController;
import com.elasticpath.web.security.EsapiServletUtils;

/**
 * Controller for displaying Products (ShoppingItems) and allowing their configuration - i.e. by specifying recipient email address for Gift
 * Certificate or constituent selection for Bundles. Allows for multiple pages to be used for this process. <br/>
 * <br/>
 * This Controller uses dynamic dispatch to allow behaviour to be customized based on the name of the product type related to the product. To use
 * this functionality sub-class {@code ProductConfigController} and include the class and the name of the product type to relate it to in the
 * productTypeNameControllerMap property of the shoppingItemConfigController definition in url-mapping.xml.
 */
public class ShoppingItemConfigControllerImpl extends AbstractSecurityWizardFormController {

	private SfRequestHelper requestHelper;
	private TemplateModelFactory templateModelFactory;
	private Map<String, ProductConfigController> productTypeNameControllerMap;
	private ProductViewService productViewService;
	private ProductSkuService productSkuService;
	private StoreProductLoadTuner productLoadTuner;
	private ProductConfigController defaultProductConfigController;
	private StoreConfig storeConfig;

	/**
	 * Return the name of the view for the specified page of this wizard form controller.
	 *
	 * @param request current HTTP request
	 * @param command the command object as returned by formBackingObject
	 * @param page the current page number
	 * @return the current page count
	 * @see #getPageCount
	 */
	@Override
	protected String getViewName(final HttpServletRequest request, final Object command, final int page) {
		// Multi-page configuration, i.e. for complex bundles, would be inserted here.
		final String productView = getTemplateName(request);
		return "catalog/product/" + productView;
	}

	private String getTemplateName(final HttpServletRequest request) {
		return getProductFromRequest(request).getTemplate();
	}

	private String getProductTypeName(final HttpServletRequest request) {
		Product product = getProductFromRequest(request);
		return product.getProductType().getName();
	}

	/**
	 * Return the number of wizard pages. Useful to check whether the last page has been reached.
	 *
	 * @param request current HTTP request
	 * @param command the command object as returned by formBackingObject
	 * @return the current page count
	 */
	@Override
	protected final int getPageCount(final HttpServletRequest request, final Object command) {
		return 1;
	}

	/**
	 * Retrieve a backing object for the current form from the given request.
	 * <p>
	 * The properties of the form object will correspond to the form field values in your form view. This object will be exposed in the model under
	 * the specified command name, to be accessed under that name in the view: for example, with a "spring:bind" tag. The default command name is
	 * "command".
	 * <p>
	 * Note that you need to activate session form mode to reuse the form-backing object across the entire form workflow. Else, a new instance of the
	 * command class will be created for each submission attempt, just using this backing object as template for the initial form.
	 *
	 * @param request current HTTP request
	 * @return the backing object
	 * @throws Exception in case of invalid state or arguments
	 * @see #setCommandName
	 * @see #setCommandClass
	 * @see #createCommand
	 */
	@Override
	protected Object formBackingObject(final HttpServletRequest request) throws Exception {
		final ProductConfigController configController = findProductConfigController(request);

		return configController.formBackingObject(request);
	}

	private ProductConfigController findProductConfigController(final HttpServletRequest request) {
		ProductConfigController configController = productTypeNameControllerMap.get(getProductTypeName(request));

		if (configController == null) {
			configController = defaultProductConfigController;
		}

		return configController;
	}

	private StoreProduct getProductFromRequest(final HttpServletRequest request) {
		final Object object = request.getAttribute(WebConstants.REQUEST_BROWSED_PRODUCT);
		if (object != null) {
			return (StoreProduct) object;
		}

		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final StoreProduct product = productViewService.getProduct(getProductIdFromRequest(request), productLoadTuner, shoppingCart);

		// do not return products:
		// - that cannot be found
		// - that are not viewable
		// - that don't belong in the context of this running store
		if (product == null || !product.isDisplayable() || !product.isInCatalog(storeConfig.getStore().getCatalog())) {
			throw new ProductUnavailableException("Product is not available, product id:" + getProductIdFromRequest(request));
		}

		request.setAttribute(WebConstants.REQUEST_BROWSED_PRODUCT, product);
		return product;
	}

	/**
	 * Create a reference data map for the given request, consisting of bean name/bean instance pairs as expected by ModelAndView.
	 *
	 * @param request current HTTP request
	 * @param command form object with request parameters bound onto it
	 * @param errors validation errors holder
	 * @param page current wizard page
	 * @return a Map with reference data entries, or <code>null</code> if none
	 * @see #referenceData(HttpServletRequest, int)
	 * @see ModelAndView
	 */
	@Override
	protected Map<String, Object> referenceData(final HttpServletRequest request, final Object command, final Errors errors, final int page) {
		return createModel(request, command, errors, page);
	}

	private Map<String, Object> createModel(final HttpServletRequest request, final Object command, final Errors errors, final int page) {
		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();

		// Only update if the updatePage parameter is set to indicate which page to return to
		final String updatePage = getUpdatePageFromRequest(request);

		// TODO this code probably needs genericising to handle associated products.
		// Associated products are listed as HTTP parameters of the format cartItem[index].pId.

		// For now the template, giftCertificate.vm, sets the product code into both a hidden
		// field called cartItem[0].pId (for add to cart) and into another called pId so that
		// this code can run. The browsing controller provides the product code in a hidden field
		// called pID.

		final Long cartItemId = getCartItemIdFromRequest(request);

		final Map<String, Object> referenceData = templateModelFactory.createModel(shoppingCart, updatePage, cartItemId, getWarehouseFromRequest(),
				getCatalog(), getProductFromRequest(request));
		// add necessary data from the controller
		final ProductConfigController configController = findProductConfigController(request);
		referenceData.putAll(configController.referenceData(request, command, errors, page));

		return referenceData;
	}

	/**
	 * Returns the product ID from the request.
	 *
	 * @param request The http request.
	 * @return The product id.
	 */
	protected String getProductIdFromRequest(final HttpServletRequest request) {
		return requestHelper.getStringParameterOrAttribute(request, WebConstants.REQUEST_PID, null);
	}

	/**
	 * Returns the Warehouse based on the current store in the request.
	 *
	 * @return The warehouse.
	 */
	protected Warehouse getWarehouseFromRequest() {
		final StoreConfig storeConfig = requestHelper.getStoreConfig();
		final Store store = storeConfig.getStore();
		return store.getWarehouse();
	}

	/**
	 * Returns the update page in the request or "" if empty.
	 *
	 * @param request The request
	 * @return The update page.
	 */
	protected String getUpdatePageFromRequest(final HttpServletRequest request) {
		if (EsapiServletUtils.hasParameter(request, WebConstants.REQUEST_UPDATE)) {
			return request.getParameter(WebConstants.REQUEST_UPDATE);
		}
		return "";
	}

	/**
	 * Gets the catalog from information in the request. Primarily to be overridden by unit testing.
	 *
	 * @return The catalog.
	 */
	protected Catalog getCatalog() {
		return requestHelper.getStoreConfig().getStore().getCatalog();
	}

	/**
	 * Returns the cartItemId in the request or 0 if it cannot be found.
	 *
	 * @param request The request.
	 * @return The cartItemId.
	 */
	protected Long getCartItemIdFromRequest(final HttpServletRequest request) {
		if (!EsapiServletUtils.hasParameter(request, WebConstants.REQUEST_CART_ITEM_ID)) {
			return 0L;
		}
		return ServletRequestUtils.getLongParameter(request, WebConstants.REQUEST_CART_ITEM_ID, 0);
	}

	/**
	 * Template method for custom validation logic for individual pages.
	 * <p>
	 * Implementations will typically call fine-granular validateXXX methods of this instance's validator, combining them to validation of the
	 * corresponding pages. The validator's default <code>validate</code> method will not be called by a wizard form controller!
	 * </p>
	 *
	 * @param command form object with the current wizard state
	 * @param errors validation errors holder
	 * @param page number of page to validate
	 * @see org.springframework.validation.Validator#validate
	 */
	@Override
	protected void validatePage(final Object command, final Errors errors, final int page) {

		ProductConfigController controller;

		ShoppingItemFormBeanContainer cartFormBean = (ShoppingItemFormBeanContainer) command;
		int shoppingItemIndex = 0;

		for (ShoppingItemFormBean shoppingItemFormBean : cartFormBean.getCartItems()) {

			if (shoppingItemFormBean.getQuantity() <= 0) {
				continue;
			}

			final String productSkuCode = shoppingItemFormBean.getSkuCode();
			final ProductSku productSku = this.productSkuService.findBySkuCode(productSkuCode);
			final String productTypeName = productSku.getProduct().getProductType().getName();

			controller = this.productTypeNameControllerMap.get(productTypeName);

			if (controller == null) {
				controller = defaultProductConfigController;
			}

			controller.validate(shoppingItemFormBean, errors, shoppingItemIndex++);
		}
	}

	/**
	 * Called when _FINISH exists in the HTTP parameters. Handles adding the item to a cart.
	 *
	 * @param request {@inheritDoc}
	 * @param response {@inheritDoc}
	 * @param command {@inheritDoc}
	 * @param errors {@inheritDoc}
	 * @throws Exception {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	protected ModelAndView processFinish(final HttpServletRequest request, final HttpServletResponse response, final Object command,
			final BindException errors) throws Exception {
		ProductConfigController configController = findProductConfigController(request);

		return configController.processFinish(request, response, command, errors);
	}

	/**
	 * Initial the big decimal binder.
	 *
	 * @param request the request
	 * @param binder the binder
	 * @throws Exception in case of invalid state or arguments
	 */
	@Override
	protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) throws Exception {
		binder.setAutoGrowNestedPaths(false);
		super.initBinder(request, binder);

		ProductConfigController configController = findProductConfigController(request);
		configController.initBinder(request, binder, isFormSubmission(request));
	}

	/**
	 * Sets the template model factory to allow access to the business layer delegate.
	 *
	 * @param templateModelFactory The factory
	 */
	public void setTemplateModelFactory(final TemplateModelFactory templateModelFactory) {
		this.templateModelFactory = templateModelFactory;
	}

	/**
	 * Sets the requestHelper instance.
	 *
	 * @param requestHelper - the request helper instance.
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

	/**
	 * Gets the requestHelper instance.
	 *
	 * @return - the current requestHelper instance.
	 */
	protected SfRequestHelper getRequestHelper() {
		return requestHelper;
	}

	/**
	 * Sets the productTypeNameControllerMap to allow dynamic dispatch based on product type.
	 *
	 * @param map The map to set.
	 */
	public void setProductTypeNameControllerMap(final Map<String, ProductConfigController> map) {
		productTypeNameControllerMap = map;
	}

	/**
	 * Sets the <code>ProductViewService</code>.
	 *
	 * @param productViewService the product view service
	 */
	public void setProductViewService(final ProductViewService productViewService) {
		this.productViewService = productViewService;
	}

	/**
	 * Sets the {@code StoreProductLoadTuner}.
	 *
	 * @param loadTuner The load tuner.
	 */
	public void setStoreProductLoadTuner(final StoreProductLoadTuner loadTuner) {
		this.productLoadTuner = loadTuner;
	}

	/**
	 * @param defaultProductConfigController the defaultProductConfigController to set
	 */
	public void setDefaultProductConfigController(final ProductConfigController defaultProductConfigController) {
		this.defaultProductConfigController = defaultProductConfigController;
	}

	/**
	 * @param productSkuService the productSkuService to set
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	/**
	 * Sets the store configuration that provides context for the actions of this service.
	 *
	 * @param storeConfig the store configuration.
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}
}
