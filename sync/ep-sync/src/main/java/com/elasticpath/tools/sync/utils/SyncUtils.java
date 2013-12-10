package com.elasticpath.tools.sync.utils;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.persistence.MapKey;

/**
 * Interface for Sync Utils.
 */
public interface SyncUtils {

	/**
	 * Constructs getter based on annotation and invokes it on the element to add.
	 * 
	 * @param annotation annotation containing information about required field
	 * @param elementToAdd object to invoke getter of
	 * @return object retrieved from the given element
	 */
	Object getMapKey(final MapKey annotation, final Object elementToAdd);

	/**
	 * Copies field from source to target using methods described by accessors.
	 * 
	 * @param source source object
	 * @param target target object
	 * @param accessors entry with getter and setter
	 */
	void invokeCopyMethod(final Object source, final Object target,
			final Entry<Method, Method> accessors);

	/**
	 * Sets value to the target using provided setter.
	 * 
	 * @param target target object to populate
	 * @param setterMethod setter to invoke on target
	 * @param value value object to set
	 */
	void invokeSetterMethod(final Object target, final Method setterMethod,
			final Object value);

	/**
	 * Retrieves field described by getter from source object.
	 * 
	 * @param source source to get field from
	 * @param getterMethod getter method
	 * @return retrieved field
	 */
	Object invokeGetterMethod(final Object source, final Method getterMethod);

	/**
	 * Tries to find a method with the specified name starting from the clazz up to Object along class hierarchy.
	 * 
	 * @param clazz Class to find method in or in its superclass
	 * @param methodName name of method to find
	 * @param parameterTypes method parameter types
	 * @return found method
	 *  throws SyncToolRuntimeException if the method with the specified name and parameter types can not be found.
	 */
	Method findDeclaredMethodWithFallback(final Class< ? > clazz, final String methodName, final Class< ? >... parameterTypes);

	/**
	 * Generates setter name based on getter name.
	 * 
	 * @param getterName string representation of getter method name
	 * @return appropriate setter method name
	 */
	String createSetterName(final String getterName);

	/**
	 * If the given class represents one of wrappers on primitive classes the method returns corresponding primitive class.
	 *  
	 * @param clazz class to convert
	 * @return primitive class corresponding to the given one or class itself if it is not primitive
	 */
	Class< ? > convertToPrimitive(final Class< ? > clazz);
	
	/**
	 * Invokes a post-load method. The method should not expect any parameters or return any value.
	 * 
	 * @param source source object to invoke method on
	 * @param postLoadMethod the method to invoke.
	 */
	void invokePostLoadMethod(final Object source, final Method postLoadMethod);
}
