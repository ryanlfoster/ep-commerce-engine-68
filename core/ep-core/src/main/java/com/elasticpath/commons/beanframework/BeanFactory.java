package com.elasticpath.commons.beanframework;


/**
 * Implementations can provide class instances based on an abstract name, which
 * allows BeanFactory clients to forego knowing the specific implementation of
 * they are dealing with - dealing with then through an interface or similar.
 */
public interface BeanFactory {

	/**
	 * Return an instance of the given bean name. 
	 * 
	 * @param <T> the type of the bean to return.
	 * @param name the 'friendly' name of the bean to return an instance of.
	 * @return an instance of the bean, or null if the bean name cannot be found.
	 */
	<T> T getBean(final String name);
	
	
	/**
	 * Return the <code>Class</code> object currently registered with the 
	 * specified <code>beanName</code>.
	 * 
	 * @param <T> the type of the bean to return.
	 * @param beanName the name of the bean to get the class for.
	 * @return the class object if the bean is registered, null otherwise.
	 */
	<T> Class<T> getBeanImplClass(final String beanName);
	
}
