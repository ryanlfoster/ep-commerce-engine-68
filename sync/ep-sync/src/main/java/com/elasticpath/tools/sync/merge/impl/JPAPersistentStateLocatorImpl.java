package com.elasticpath.tools.sync.merge.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Version;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.merge.MergeFilter;
import com.elasticpath.tools.sync.merge.PersistentStateLocator;
import com.elasticpath.tools.sync.utils.SyncUtils;

/**
 * Provides helper methods such as to lookup persistent state methods across a hierarchy.
 */
public class JPAPersistentStateLocatorImpl implements PersistentStateLocator {

	private static final Logger LOG = Logger.getLogger(JPAPersistentStateLocatorImpl.class);

	private SyncUtils syncUtils;
	
	private MergeFilter filter;

	@Override
	public <T extends Annotation> void extractPersistentStateAttributes(final Class< ? > clazz, final Map<Method, Method> basicAttributes,
			final Map<Method, Method> singleValuedAssociations, final Map<Method, Method> collectionValuedAssociations,
			final Set<Method> postLoadMethods)
			throws SyncToolRuntimeException {
		if (clazz == Object.class) {
			return;
		}
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (isStateField(method)) {
				addMethod(basicAttributes, method);
			} else if (isSingleField(method)) {
				addMethod(singleValuedAssociations, method);
			} else if (isCollectionField(method)) {
				addMethod(collectionValuedAssociations, method);
			}
			
			if (isPostLoad(method)) {
				postLoadMethods.add(method);
			}
		}
		extractPersistentStateAttributes(clazz.getSuperclass(), basicAttributes, singleValuedAssociations, collectionValuedAssociations,
				postLoadMethods);
	}
	
	/**
	 * Checks that the method should be called post load.
	 * 
	 * @param method the method to examine
	 * @return true if the method is marked for post load
	 */
	boolean isPostLoad(final Method method) {
		return checkAnnotation(method, PostLoad.class);
	}

	/**
	 * Checks that the method represents Collection-Valued association.
	 * 
	 * @param method method to examine
	 * @return true if the method represents Collection-Valued state field
	 */
	boolean isCollectionField(final Method method) {
		return (checkAnnotation(method, OneToMany.class) || checkAnnotation(method, ManyToMany.class));
	}

	/**
	 * Checks that the method represents Single-Valued association.
	 * 
	 * @param method method to examine
	 * @return true if the method represents Single-Valued state field
	 */
	boolean isSingleField(final Method method) {
		return (checkAnnotation(method, ManyToOne.class) || checkAnnotation(method, OneToOne.class));
	}

	/**
	 * Checks that the method represents basic state field.
	 * 
	 * @param method method to examine
	 * @return true if the method represents basic state field
	 */
	boolean isStateField(final Method method) {
		return checkAnnotation(method, Column.class)
			&& !checkAnnotation(method, Version.class) 
			&& !checkAnnotation(method, Id.class);
	}

	/**
	 * Adds a method to attributes.
	 * 
	 * @param attributes the Map of Methods.
	 * @param method the method to add.
	 * @throws SyncToolRuntimeException if there is an any problem with method
	 */
	void addMethod(final Map<Method, Method> attributes, final Method method) throws SyncToolRuntimeException {
  		if (!methodPermitted(method) || attributes.containsKey(method)) {
			return;
		}

		try {
			final Method setterMethod = syncUtils.findDeclaredMethodWithFallback(
					method.getDeclaringClass(), syncUtils.createSetterName(method.getName()), method.getReturnType());
			method.setAccessible(true);
			setterMethod.setAccessible(true);
			attributes.put(method, setterMethod);
		} catch (Exception e) {
			LOG.error("Can not find setter for method " + method.getName(), e);
			throw new SyncToolRuntimeException(e);
		}
	}

	/**
	 * Checks if method is permitted using filter.
	 * 
	 * @param method the method
	 * @return true if filter isMergePermitted is true for the method
	 */
	boolean methodPermitted(final Method method) {
		if (filter != null) {
			return filter.isMergePermitted(method.getDeclaringClass(), method.getName());
		}
		return true;
	}

	private <T extends Annotation> boolean checkAnnotation(final Method method, final Class<T> annotation) {
		return method.getAnnotation(annotation) != null;
	}

	/**
	 * @param syncUtils the syncUtils to set
	 */
	public void setSyncUtils(final SyncUtils syncUtils) {
		this.syncUtils = syncUtils;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(final MergeFilter filter) {
		this.filter = filter;
	}
}
