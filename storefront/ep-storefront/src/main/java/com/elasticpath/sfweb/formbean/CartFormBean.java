package com.elasticpath.sfweb.formbean;

import java.util.List;

/**
 * Form bean for collecting information from the ShoppingCartController.
 * Note that only quantity is required at present because shipping estimation and
 * code application use different mechanisms.
 */
public interface CartFormBean extends ShoppingItemFormBeanContainer {

	/**
	 * Get the root item of the form bean. This is the main item being added or updated.
	 * @return the root item
	 */
	ShoppingItemFormBean getRootItem();
	
	/**
	 * The associated items are all the items which are connected to the root item by
	 * marketing associations, except items which are dependent.
	 * @return list of associated items
	 */
	List<ShoppingItemFormBean> getAssociatedItems();
	
	/**
	 * The dependent items are items which are associated by a marketing association and
	 * are dependent. Warranty associations currently.
	 * @return the dependent items
	 */
	List<ShoppingItemFormBean> getDependentItems();
	
}