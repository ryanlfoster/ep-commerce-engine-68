package com.elasticpath.commons.tree.impl;

import com.elasticpath.commons.tree.Functor;
import com.elasticpath.commons.tree.TraversalMemento;
import com.elasticpath.commons.tree.TreeNode;

/**
 * Traverses a tree of {@code TreeNode} elements using pre-order traversal - the node is visited and then
 * each child is visited in order. Every visit invokes the {@code Functor}. The {@code Functor} is expected
 * to create destination node which is passed into the {@code processNode} method for each of the current node's
 * children. <br/>
 * 
 * Note that this algorithm is designed with copying trees in mind. It is possible this is not a generic traverser
 * but is a copy traverser instead.
 * 
 * @param <S> The type of the source node. Must inherit from {@code TreeNode}.
 * @param <M> The type of the stack memento. Must implement {@code StackMemento}.
 */
public class PreOrderTreeTraverser<S extends TreeNode< S >, M extends TraversalMemento> {

	/**
	 * {@code sourceNode} and {@code parentDestNode} need not, and probably
	 * should not, be implemented by the same class. This method allows one tree
	 * to be traversed while creating another.
	 * 
	 * @param sourceNode
	 *            The node that will be traversed on this invocation. The
	 *            initial call should set this to the root of the tree.
	 * @param parentNode
	 * 			  the parent of the current node in the tree.
	 * @param traversalMemento
	 *            The result of calling {@code processNode} on the parent of
	 *            {@code sourceNode}. The initial call should set this to null.
	 * @param functor
	 *            The functor that should be called for every node.
	 * @param level
	 * 			  The level of the current sourceNode in the tree. The initial call should set this to 0.
	 * @return The result of calling the functor on the root of the tree.
	 */
	public M traverseTree(final S sourceNode, final S parentNode, final M traversalMemento, final Functor< S, M> functor, final int level) {
		int thisLevel = level;
		M thisParentStackMemento = traversalMemento;
		// This is the root of the tree so need to traverse first.
		if (parentNode == null) {
			thisParentStackMemento = functor.processNode(sourceNode, null, traversalMemento, thisLevel);
			thisLevel++;  // Since this is the parent, the children are the next level down.
		}
		
		for (S sourceChild : sourceNode.getChildren()) {
			M childStackMemento = functor.processNode(sourceChild, sourceNode, thisParentStackMemento, thisLevel); 
			traverseTree(sourceChild, sourceNode, childStackMemento, functor, thisLevel + 1);
		}
		
		return thisParentStackMemento;
	}
}
