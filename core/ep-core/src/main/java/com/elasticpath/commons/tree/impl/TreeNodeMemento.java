package com.elasticpath.commons.tree.impl;

import com.elasticpath.commons.tree.TraversalMemento;
import com.elasticpath.commons.tree.TreeNode;

/**
 * An implementation of a {@code StackMemento} which contains a {@code TreeNode}. For example, this is used for copy operations.
 * 
 * @param <T> The {@code TreeNode} which this class contains.
 */
public class TreeNodeMemento<T extends TreeNode< ? >> implements TraversalMemento {
	private T treeNode;

	/**
	 * Parameter constructor which sets the {@code treeNode}.
	 * 
	 * @param treeNode The {@code treeNode} to set.
	 */
	public TreeNodeMemento(final T treeNode) {
		this.treeNode = treeNode;
	}
	/**
	 * 
	 * @return the contained {@code TreeNode}
	 */
	public T getTreeNode() {
		return treeNode;
	}

	/**
	 * 
	 * @param treeNode The {@code TreeNode} to contain.
	 */
	public void setTreeNode(final T treeNode) {
		this.treeNode = treeNode;
	}
}
