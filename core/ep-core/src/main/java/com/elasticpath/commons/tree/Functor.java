package com.elasticpath.commons.tree;

/**
 * Function object called for each node when traversing a tree of {@code TreeNode}s.
 * @param <S> The type of the source node. Must inherit from {@code TreeNode}.
 * @param <M> The type of the memento. Must implement {@code TraversalMemento}.
 */
public interface Functor<S extends TreeNode< S >, M extends TraversalMemento> {

	/**
	 * Called for each node during tree traversal.
	 * 
	 * @param sourceNode
	 *            The {@code TreeNode} that we are currently visiting.
	 * @param parentNode
	 * 			  The {@code TreeNode} parent of the item we are visiting.
	 * @param traversalMemento
	 *            The result of calling this Functor on the parent of {@code SourceNode}>
	 * @param level 
	 * 			  The level of the node in the tree.
	 * @return A {@code TraversalMemento} representing the result of this traversal.
	 *         Will passed as {@code traversalMemento} when this method is called
	 *         on the children of {@code sourceNode}. Root information is gathered from the memento.
	 */
	M processNode(S sourceNode, S parentNode, M traversalMemento, int level);

}
