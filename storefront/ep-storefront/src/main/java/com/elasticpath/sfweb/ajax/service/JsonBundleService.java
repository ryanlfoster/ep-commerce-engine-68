package com.elasticpath.sfweb.ajax.service;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.sfweb.ajax.bean.impl.JsonBundleItemBeanImpl;

/**
 * Provide the service to calculate and update the price for calculated bundle.
 * This service will be exposed as a dwr ajax service too. 
 * @author mren
 *
 */
public interface JsonBundleService {
	
	/**
	 * This method walks through the bundle tree and updates the price for each item according to the its skuCode.
	 * For a calculated bundle (it could be top level bundle or a nested bundle), it will recalculate the price.
	 * 
	 * @param originalBundle The original JsonBundleItemBeanImpl.
	 * @param request HttpServletRequest instance.
	 * @return Updated bundle.
	 */
	JsonBundleItemBeanImpl updateJsonBundle(JsonBundleItemBeanImpl originalBundle, HttpServletRequest request); 
	
	/**
	 * This method walks through the bundle tree and updates the price for each item according to the its skuCode.
	 * For a calculated bundle (it could be top level bundle or a nested bundle), it will recalculate the price.
	 * 
	 * @param originalBundle The original JsonBundleItemBeanImpl.
	 * @param shopper The customer session.
	 * @return Updated bundle.
	 */
	JsonBundleItemBeanImpl updateJsonBundleUsingSession(JsonBundleItemBeanImpl originalBundle, Shopper shopper); 
}
