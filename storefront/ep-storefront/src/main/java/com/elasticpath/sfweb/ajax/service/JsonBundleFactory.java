package com.elasticpath.sfweb.ajax.service;

import com.elasticpath.sfweb.ajax.bean.JsonBundleItemBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * Factory class to create, serialize and deserialize json bundle object.
 * @author mren
 *
 */
public interface JsonBundleFactory {

	/**
	 * Creates JsonBundleItemBean from ShoppingItemFormBean.
	 * ShoppingItemFormBean has a flat structure, where root holds all children and descendants. 
	 * JsonBundleItemBean has a nested tree structure. 
	 * @param rootShoppingItem root shopping item.
	 * @return converted json bundle.
	 */
	JsonBundleItemBean createJsonBundleFromShoppingItemFormBean(
			final ShoppingItemFormBean rootShoppingItem);

	/**
	 * serialize to json text.
	 * @param jsonBundle JsonBundleItemDto object.
	 * @return text representation of json bundle.
	 */
	String serialize(final JsonBundleItemBean jsonBundle);

	/**
	 * Deserialize json text into Java object. 
	 * @param bundleText text representation of json bundle.
	 * @param jsonBundleClass the implementing class reference
	 * @return JsonBundleItemBean object.
	 */
	JsonBundleItemBean deserialize(String bundleText, Class<? extends JsonBundleItemBean> jsonBundleClass);

}