package com.elasticpath.commons.tree.impl;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.tree.Functor;
import com.elasticpath.commons.tree.TraversalMemento;
import com.elasticpath.commons.tree.TreeNode;

/**
 * Tests the {@code PreOrderTreeTraverserImpl}.
 */
public class PreOrderTreeTraverserImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final PreOrderTreeTraverser<BasicTreeNode, TraversalMemento> traverser = new PreOrderTreeTraverser<BasicTreeNode, TraversalMemento>();

	@SuppressWarnings("unchecked")
	private final Functor<BasicTreeNode, TraversalMemento> functor = context.mock(Functor.class);

	/**
	 * Tests that a node with children has {@code processNode} called for each child.
	 */
	@Test
	public void testTraverseChildren() {
		final BasicTreeNode childNode1 = new BasicTreeNode();
		final BasicTreeNode childNode2 = new BasicTreeNode();
		final BasicTreeNode childNode3 = new BasicTreeNode();

		final List<BasicTreeNode> children = new ArrayList<BasicTreeNode>();
		children.add(childNode1);
		children.add(childNode2);
		children.add(childNode3);

		final BasicTreeNode sourceNode = new BasicTreeNode(children);

		final TraversalMemento parentStackMemento = context.mock(TraversalMemento.class, "parentStackMemento");
		
		final Sequence preOrder = context.sequence("preOrderSequence");
		
		context.checking(new Expectations() { {
			oneOf(functor).processNode(sourceNode, null, null, 0); will(returnValue(parentStackMemento)); inSequence(preOrder);
			oneOf(functor).processNode(childNode1, sourceNode, parentStackMemento, 1); inSequence(preOrder);
			oneOf(functor).processNode(childNode2, sourceNode, parentStackMemento, 1); inSequence(preOrder);
			oneOf(functor).processNode(childNode3, sourceNode, parentStackMemento, 1); inSequence(preOrder);
		} });
		
		traverser.traverseTree(sourceNode, null, null, functor, 0);
	}

	/**
	 * Tests that a tree with 3 levels each with one node has {@code processNode} called for each child.
	 */
	@Test
	public void testTraverseGrandChildren() {
		final BasicTreeNode grandchildNode = new BasicTreeNode();

		final List<BasicTreeNode> grandchildren = new ArrayList<BasicTreeNode>();
		grandchildren.add(grandchildNode);

		final BasicTreeNode childNode = new BasicTreeNode(grandchildren);

		final List<BasicTreeNode> children = new ArrayList<BasicTreeNode>();
		children.add(childNode);

		final BasicTreeNode sourceNode = new BasicTreeNode(children);

		final TraversalMemento parentStackMemento = context.mock(TraversalMemento.class, "parentStackMemento");
		final TraversalMemento childStackMemento = context.mock(TraversalMemento.class, "childStackMemento");

		final Sequence preOrder = context.sequence("preOrderSequence");

		context.checking(new Expectations() {
			{
				oneOf(functor).processNode(sourceNode, null, null, 0);
				will(returnValue(parentStackMemento));
				inSequence(preOrder);
				oneOf(functor).processNode(childNode, sourceNode, parentStackMemento, 1);
				will(returnValue(childStackMemento));
				inSequence(preOrder);
				oneOf(functor).processNode(grandchildNode, childNode, childStackMemento, 2);
				inSequence(preOrder);
			}
		});

		traverser.traverseTree(sourceNode, null, null, functor, 0);
	}

	/**
	 * Trivial implementation of {@link TreeNode} to facilitate unit testing.
	 */
	private static class BasicTreeNode implements TreeNode<BasicTreeNode> {
		private final List<BasicTreeNode> children;

		BasicTreeNode() {
			this.children = new ArrayList<BasicTreeNode>();
		}

		public BasicTreeNode(final List<BasicTreeNode> children) {
			this.children = children;
		}

		@Override
		public List<BasicTreeNode> getChildren() {
			return children;
		}

		@Override
		public void addChild(final BasicTreeNode child) {
			children.add(child);
		}
	}

}
