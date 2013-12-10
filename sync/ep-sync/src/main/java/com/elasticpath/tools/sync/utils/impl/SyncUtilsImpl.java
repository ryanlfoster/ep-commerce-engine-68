package com.elasticpath.tools.sync.utils.impl;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.persistence.MapKey;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.utils.SyncUtils;

/**
 * Provides reflection based methods for data population.
 */
public class SyncUtilsImpl implements SyncUtils {
	private static final Logger LOG = Logger.getLogger(SyncUtilsImpl.class);
	
	private static final String IS_PREFIX = "is";

	private static final String SET_PREFIX = "set";

	private static final String GET_PREFIX = "get";

	@Override
	public Object getMapKey(final MapKey annotation, final Object elementToAdd) {
		String partOfMapKeyProperty = annotation.name();
		String mapKeyProperty = GET_PREFIX + partOfMapKeyProperty.substring(0, 1).toUpperCase()
				+ partOfMapKeyProperty.substring(1, partOfMapKeyProperty.length());

		// TODO: consider isProperty
		Method getter = null;
		try {
			getter = findDeclaredMethodWithFallback(elementToAdd.getClass(), mapKeyProperty);
			getter.setAccessible(true);
			return invokeGetterMethod(elementToAdd, getter);
		} catch (Exception e) {
			throw new SyncToolRuntimeException(e);
		}
	}

	@Override
	public void invokeCopyMethod(final Object source, final Object target, final Entry<Method, Method> accessors) {
		try {
			invokeSetterMethod(target, accessors.getValue(), invokeGetterMethod(source, accessors.getKey()));
		} catch (Exception exception) {
			LOG.error("Can not receive object using method" + accessors.getKey(), exception);
			throw new SyncToolRuntimeException(exception);
		}
	}

	@Override
	public void invokeSetterMethod(final Object target, final Method setterMethod, final Object value) {
		try {
			setterMethod.invoke(target, value);
		} catch (Exception exception) {
			LOG.error("Can not set value " + value, exception);
			throw new SyncToolRuntimeException(exception);
		}
	}

	@Override
	public Object invokeGetterMethod(final Object source, final Method getterMethod) {
		try {
			return getterMethod.invoke(source);
		} catch (Exception exception) {
			LOG.error("Can not get value ", exception);
			throw new SyncToolRuntimeException(exception);
		}
	}
	
	@Override
	public Method findDeclaredMethodWithFallback(final Class< ? > clazz,
			final String methodName, final Class< ? > ... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (Exception e) {
			Class< ? > superclass = clazz.getSuperclass();
			if (superclass == Object.class) {
				throw new SyncToolRuntimeException(e);
			}
			return findDeclaredMethodWithFallback(superclass, methodName, parameterTypes);
		}
	}

	@Override
	public String createSetterName(final String getterName) {
		if (getterName.startsWith(GET_PREFIX)) {
			return SET_PREFIX + getterName.substring(GET_PREFIX.length());
		}
		if (getterName.startsWith(IS_PREFIX)) {
			return SET_PREFIX + getterName.substring(IS_PREFIX.length());
		}
		return getterName;
	}

	@Override
	public Class< ? > convertToPrimitive(final Class< ? > clazz) {
		if (Boolean.class.equals(clazz)) {
			return boolean.class;
		}
		if (Integer.class.equals(clazz)) {
			return int.class;
		}
		if (Double.class.equals(clazz)) {
			return double.class;
		}
		if (Character.class.equals(clazz)) {
			return char.class;
		}
		return clazz;
	}

	@Override
	public void invokePostLoadMethod(final Object object, final Method postLoadMethod) {
		try {
			postLoadMethod.invoke(object);
		} catch (Exception exception) {
			LOG.error("Could not successfully run post-load method " + postLoadMethod.getName());
			throw new SyncToolRuntimeException(exception);
		}
	}
}
