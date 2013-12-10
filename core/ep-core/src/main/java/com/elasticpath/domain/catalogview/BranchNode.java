package com.elasticpath.domain.catalogview;

import java.util.Stack;

/**
 * Represents a branch node on a tree. A <code>BranchNode</code> can only have one parent
 * <code>BranchNode</code>. If a <code>BranchNode</code> has no parent, it's a root node.
 * 
 * @param <T> the type of branch node
 */
public interface BranchNode<T extends Comparable<T>> extends Comparable<T> {

	/**
	 * Get the path from the root node to this node on the tree.
	 * 
	 * @return a stack contains the path, the root node is on the top.
	 */
	Stack<T> getPath();

	/**
	 * Get node level. The root nodes will have level 1. <br>
	 * A node's level = it's parent node's level + 1
	 * 
	 * @return the category level
	 */
	int getLevel();
}
