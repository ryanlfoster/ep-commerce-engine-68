package com.elasticpath.domain.catalog;

import java.util.List;

/**
 * Represents the configuration of a product, containing picked items in a bundle as well as the SKU selection in a multi-SKU product.
 */
public interface ItemConfiguration extends Cloneable {

	/**
	 * Gets the children items of this item configuration.
	 *
	 * @return the constituents
	 */
	List<ItemConfiguration> getChildren();

	/**
	 * Gets an immediate child of this item configuration by the child's ID.
	 *
	 * @param childId the child identifier. For the case of a bundle, it will be the bundle constituent GUID.
	 * @return the child by id
	 */
	ItemConfiguration getChildById(String childId);

	/**
	 * Gets a ((great)grand)child of this item configuration given the path to it. The child can be at any
	 * level in the tree of children.
	 *
	 * @param childPath a list of child IDs leading to the child
	 * @return the child
	 */
	ItemConfiguration getChildByPath(List<String> childPath);

	/**
	 * Gets the SKU code of this item configuration.
	 *
	 * @return the SKU code
	 */
	String getSkuCode();

	/**
	 * Checks if is selected.
	 *
	 * @return true, if is selected
	 */
	boolean isSelected();
}
