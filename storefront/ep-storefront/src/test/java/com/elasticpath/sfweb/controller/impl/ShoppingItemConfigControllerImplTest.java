package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalogview.ProductViewService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.controller.ProductConfigController;
import com.elasticpath.sfweb.util.impl.RequestHelperImpl;

/**
 * Tests the {@code ShoppingItemConfigControllerImpl}.
 */ 
public class ShoppingItemConfigControllerImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Tests that calling the dispatched methods on the controller will call the respective methods
	 * on the delegate class based on the templateName.
	 * 
	 * @throws Exception If any exception thrown.
	 */
	@Test
	public void testDynamicDispatch() throws Exception {
		ShoppingItemConfigControllerImpl controller = new ShoppingItemConfigControllerImpl();
		
		Map<String, ProductConfigController> map = new HashMap<String, ProductConfigController>();
		final ProductConfigController configController1 = context.mock(ProductConfigController.class, "configController1");
		final ProductConfigController configController2 = context.mock(ProductConfigController.class, "configController2");		
		map.put("type1", configController1);
		map.put("type2", configController2);
		controller.setProductTypeNameControllerMap(map);
		
		final ProductViewService productViewService = context.mock(ProductViewService.class);
		controller.setProductViewService(productViewService);
		
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		
		final StoreProductLoadTuner loadTuner = context.mock(StoreProductLoadTuner.class);
		controller.setStoreProductLoadTuner(loadTuner);
		final StoreProduct product = context.mock(StoreProduct.class);
		final ProductType productType = context.mock(ProductType.class);
		
		controller.setRequestHelper(new RequestHelperImpl() {
			@Override
			public CustomerSession getCustomerSession(final HttpServletRequest request) {
				return customerSession;
			}
		});
		
		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		controller.setStoreConfig(storeConfig);
		final Store store = new StoreImpl();
		final Catalog catalog = new CatalogImpl();
		store.setCatalog(catalog);
		
		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final ServletRequestDataBinder binder = new ServletRequestDataBinder("");
		final HttpServletResponse response = context.mock(HttpServletResponse.class);
		final String command = "foobar";
		final BindException errors = new BindException("", null);
		
		context.checking(new Expectations() { {
			allowing(request).getParameterNames(); will(returnEnumeration("pID"));
			allowing(request).getParameter("pID"); will(returnValue("productA"));
			allowing(request).getAttribute(WebConstants.REQUEST_BROWSED_PRODUCT); will(returnValue(null));
			allowing(request).setAttribute(WebConstants.REQUEST_BROWSED_PRODUCT, product);
			allowing(request).getMethod(); will(returnValue("POST"));
			
			allowing(customerSession).getShoppingCart(); 
			will(returnValue(shoppingCart));
			
			allowing(productViewService).getProduct("productA", loadTuner, shoppingCart); will(returnValue(product));
			allowing(product).getProductType(); will(returnValue(productType));
			allowing(productType).getName(); will(returnValue("type2"));
			allowing(product).isDisplayable(); will(returnValue(true));
			allowing(storeConfig).getStore(); will(returnValue(store));
			allowing(product).isInCatalog(catalog); will(returnValue(true));
			
			oneOf(configController2).formBackingObject(request);
			oneOf(configController2).initBinder(request, binder, true);
			oneOf(configController2).processFinish(request, response, command, errors);
		} });
		
		controller.formBackingObject(request);
		controller.initBinder(request, binder);
		controller.processFinish(request, response, command, errors);
		
	}
}
