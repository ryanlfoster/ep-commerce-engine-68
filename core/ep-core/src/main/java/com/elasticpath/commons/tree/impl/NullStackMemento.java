package com.elasticpath.commons.tree.impl;

import com.elasticpath.commons.tree.TraversalMemento;


/**
 * This class indicates that the Functor does not require any thing to be given to its children.
 */
public final class NullStackMemento implements TraversalMemento {
	private static NullStackMemento instance;
	
	private NullStackMemento() {
		// prevent public instantiation.
	}
	
	/**
	 * 
	 * @return A singleton instance.
	 */
	@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
	public static synchronized NullStackMemento getInstance() {
		if (instance == null) {
			instance = new NullStackMemento();
		}
		
		return instance;
	}
}
