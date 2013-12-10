package com.elasticpath.commons.tree;

import java.util.List;

/**
 * Represents an n-ary tree.
 * 
 * @param <T> A class which implements {@code TreeNode}. Used to define the type of the child.
 */
public interface TreeNode <T extends TreeNode< ? >> {

	/**
	 * 
	 * 
	 * @return The children of this node. May not return null. Leaf nodes should return an empty list.
	 */
	List< T > getChildren();

	/**
	 * 
	 * @param child The child to add to this node.
	 */
	void addChild(T child);

}
