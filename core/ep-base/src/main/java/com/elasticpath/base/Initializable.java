package com.elasticpath.base;

/**
 * Implemented by Domain objects that need to be able to init values for new 
 * instances separate from the default constructor.  This is an optimization to allow
 * new instance initialization to be separable from the default constructor, which is also
 * called from ORM tools when they load existing instances from persistence.
 */
public interface Initializable {
	
	/**
	 * Initialize object with default values when the object is created by the bean factory.
	 */
	void initialize();

}
