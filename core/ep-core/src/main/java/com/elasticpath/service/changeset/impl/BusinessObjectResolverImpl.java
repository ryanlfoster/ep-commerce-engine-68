package com.elasticpath.service.changeset.impl;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.BusinessObjectResolver;
import com.elasticpath.service.changeset.ChangeSetPolicyException;
import com.elasticpath.service.changeset.ObjectGuidResolver;

/**
 * For use in resolving BusinessObjectDescriptors from Entities or DTOs.
 */
public class BusinessObjectResolverImpl implements BusinessObjectResolver {
	private static final Logger LOG = Logger.getLogger(BusinessObjectResolverImpl.class);

	private BidiMap objectTypes;

	private BidiMap secondaryObjectTypes;

	private BeanFactory beanFactory;

	private Map<String, ObjectGuidResolver> objectGuidResolvers;

	private ObjectGuidResolver defaultObjectGuidResolver;

	@Override
	public Class< ? > getObjectClass(final BusinessObjectDescriptor objectDescriptor) {
		if (objectDescriptor == null) {
			return null;
		}
		return (Class< ? >) getObjectTypes().getKey(objectDescriptor.getObjectType());
	}

	@Override
	public boolean isClassSupported(final Class< ? > clazz) {
		for (final Object interfaceClass : getObjectTypes().keySet()) {
			if (((Class< ? >) interfaceClass).isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<BusinessObjectDescriptor> resolveObjectDescriptor(final Set< ? > objects) {
		final Set<BusinessObjectDescriptor> businessObjectDescriptors = new LinkedHashSet<BusinessObjectDescriptor>();
		for (final Object obj : objects) {
			final BusinessObjectDescriptor businessObjectDescriptor = resolveObjectDescriptor(obj);
			if (businessObjectDescriptor != null) {
				businessObjectDescriptors.add(businessObjectDescriptor);
			}
		}
		return businessObjectDescriptors;
	}

	@Override
	public BusinessObjectDescriptor resolveObjectDescriptor(final Object object) throws ChangeSetPolicyException {
		if (object == null) {
			return null;
		}

		BusinessObjectDescriptor objectDescriptor = resolveObjectDescriptorGivenTypes(object, getObjectTypes());
		if (objectDescriptor == null) {
			objectDescriptor = resolveObjectDescriptorGivenTypes(object, getSecondaryObjectTypes());
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Object " + object.getClass().getName() + " resolved to: " + objectDescriptor);
		}

		return objectDescriptor;
	}

	private BusinessObjectDescriptor resolveObjectDescriptorGivenTypes(final Object object, final BidiMap localObjectTypes) {
		final Set<Class< ? >> resultSet = new HashSet<Class< ? >>();
		for (final Object key : localObjectTypes.keySet()) {
			final Class< ? > interfaceClass = (Class< ? >) key;

			final Class< ? > objectClass = object.getClass();
			if (interfaceClass.isAssignableFrom(objectClass)) {
				resultSet.add(interfaceClass);
			}
		}

		if (!resultSet.isEmpty()) {
			final SortedSet<Class< ? >> subClassFirstResultSet = getSubClassFirstSet(resultSet);
			final Class< ? > interfaceClass = subClassFirstResultSet.iterator().next();
			final Object value = localObjectTypes.get(interfaceClass);
			final String resolveGuid = resolveGuid(interfaceClass, object);
			if	(resolveGuid != null) {
				final BusinessObjectDescriptor objectDescriptor = beanFactory.getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
				objectDescriptor.setObjectIdentifier(resolveGuid);
				objectDescriptor.setObjectType(String.valueOf(value));
				return objectDescriptor;
			}
		}

		return null;
	}

	/**
	 * convert the set to a SortedSet and the sub class is before super class.
	 * 
	 * @param set the set
	 * @return the sorted set
	 */
	protected SortedSet<Class<?>> getSubClassFirstSet(final Set<Class<?>> set) {
		final TreeSet<Class<?>> treeSet = new TreeSet<Class<?>>(new SubClassFirstComparator());
		treeSet.addAll(set);
		return treeSet;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String resolveObjectGuid(final Object object) {
		String resolvedGuid = resolveGuidFromTypes(object, getObjectTypes().keySet());
		if (resolvedGuid == null) {
			resolvedGuid = resolveGuidFromTypes(object, getSecondaryObjectTypes().keySet());
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Object " + object.getClass().getName() + " resolved GUID: " + resolvedGuid);
		}

		return resolvedGuid;
	}

	/**
	 * Resolve the guid of an object given a list of possible types.
	 *
	 * @param object the object to resolve the guid for
	 * @param objectTypeKeys list of possible types
	 * @return the guid if resolved, null otherwise
	 */
	protected String resolveGuidFromTypes(final Object object, final Set<Class< ? >> objectTypeKeys) {
		for (final Object key : objectTypeKeys) {
			final Class< ? > interfaceClass = (Class< ? >) key;
			final Class< ? > objectClass = object.getClass();
			if (interfaceClass.isAssignableFrom(objectClass)) {
				return resolveGuid(interfaceClass, object);
			}
		}
		return null;
	}

	/**
	 *
	 * @param objectInterface
	 * @param object
	 * @return
	 */
	private String resolveGuid(final Class< ? > objectInterface, final Object object) {
		if (objectGuidResolvers == null) {
			return defaultObjectGuidResolver.resolveGuid(object);
		}

		final ObjectGuidResolver resolver = objectGuidResolvers.get(objectInterface.getName());

		if (resolver == null) {
			return defaultObjectGuidResolver.resolveGuid(object);
		}

		if (resolver.isSupportedObject(object)) {
			final String guid = resolver.resolveGuid(object);
			if (guid != null) {
				return guid;
			}

			throw new ChangeSetPolicyException("Cannot resolve GUID for object: " + object);
		}
		return null;
	}

	/**
	 * Getter for the object types map.
	 * 
	 * @return Object interface/type map.
	 */
	protected BidiMap getObjectTypes() {
		return objectTypes;
	}

	/**
	 * Set the object type map values. May be used in both directions.
	 * 
	 * @param objectTypes the object type map of type [[object type], [object class]]
	 */
	public void setObjectTypes(final Map<Class < ? >, String> objectTypes) {
		this.objectTypes = new DualHashBidiMap(objectTypes);
	}

	/**
	 * Getter for the secondary object types map.
	 * 
	 * @return Object interface/type map.
	 */
	protected BidiMap getSecondaryObjectTypes() {
		return secondaryObjectTypes;
	}

	/**
	 * Set the secondary object type map values. These will be used to go from class to
	 * type name, but not from type name to class.
	 * 
	 * @param secondaryObjectTypes the object type map of type [[object type], [object class]]
	 */
	public void setSecondaryObjectTypes(final Map<Class < ? >, String> secondaryObjectTypes) {
		this.secondaryObjectTypes = new DualHashBidiMap(secondaryObjectTypes);
	}

	/**
	 * Set a map of object guid resolvers.
	 *
	 * @param objectGuidResolvers the map of object guid resolvers
	 */
	public void setObjectGuidResolvers(final Map<String, ObjectGuidResolver> objectGuidResolvers) {
		this.objectGuidResolvers = objectGuidResolvers;
	}

	/**
	 * Set Default Object Guid Resolver.
	 * If none object guid resolver was found in the map of object guid resolvers,
	 * it will use the default object guid resolver to resolve the guid.
	 *
	 * @param defaultGuidResolver the instance of the default guid resolver
	 */
	public void setDefaultObjectGuidResolver(final ObjectGuidResolver defaultGuidResolver) {
		defaultObjectGuidResolver = defaultGuidResolver;
	}

	/**
	 * Return a reference to the {@link BeanFactory}.
	 * 
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * The comparator which make sure the sub class is before super class.
	 */
	private class SubClassFirstComparator implements Comparator<Class<?>>, Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 
		 */
		public int compare(final Class<?> class1, final Class<?> class2) {
			if (class2.isAssignableFrom(class1)) {
				return -1;
			}
			return 1;
		}

	}
}