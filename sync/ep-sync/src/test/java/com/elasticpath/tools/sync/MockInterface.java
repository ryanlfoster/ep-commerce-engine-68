package com.elasticpath.tools.sync;

/**
 * Sometimes one method of tested class is called from another one.
 * For that purpose tested class can be extended and mock method can be provided
 * but some mechanism is needed to ensure that such method was called during a test.
 * JMock provides the bes instrument for building expectations. This interface serves
 * possibility of building JMock expectations for internal class' methods called
 * inside of its method under a test.
 */
public interface MockInterface {

	/**
	 * Put this method into overridden method and build appropriate expectations.
	 * 
	 * @param parameter parameter to check
	 * @return object to return using expectations if required
	 */
	Object method(Object parameter);
}
