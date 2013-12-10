package com.elasticpath.tools.sync.merge.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.merge.BeanCreator;
import com.elasticpath.tools.sync.merge.MergeEngine;
import com.elasticpath.tools.sync.merge.PersistentStateLocator;
import com.elasticpath.tools.sync.merge.configuration.EntityFilter;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;
import com.elasticpath.tools.sync.merge.configuration.GuidLocator;
import com.elasticpath.tools.sync.merge.configuration.MergeBoundarySpecification;
import com.elasticpath.tools.sync.merge.configuration.ValueObjectMerger;
import com.elasticpath.tools.sync.utils.MethodComparator;
import com.elasticpath.tools.sync.utils.SyncUtils;

/**
 * Merges objects before save.
 */
public class MergeEngineImpl implements MergeEngine {

	private MergeBoundarySpecification mergeBoundarySpec;

	private EntityLocator entityLocator;

	private GuidLocator guidLocator;

	private BeanCreator beanCreator;

	private ValueObjectMerger valueObjectMerger;

	private PersistentStateLocator jpaPersistentStateLocator;

	private SyncUtils syncUtils;

	private Comparator<Persistable> guidComparator;

	private final ThreadLocal<CyclicDependencyManager> dependencyManagerThreadLocal = new ThreadLocal<CyclicDependencyManager>();

	private Map<String, EntityFilter> mergeFilters;

	/**
	 * Merge source on target.
	 *
	 * @param source source object
	 * @param target target object
	 * @throws SyncToolConfigurationException {@link SyncToolConfigurationException}
	 */
	public void processMerge(final Persistable source, final Persistable target) throws SyncToolConfigurationException {
		sanityCheck(source, target);
		initializeCyclicDependencyManager();

		mergeBoundarySpec.initialize(source.getClass());
		mergeInternal(source, target);
		// throw new RuntimeException();
	}

	private void initializeCyclicDependencyManager() {
		CyclicDependencyManager cyclicDependencyManager = dependencyManagerThreadLocal.get();
		if (cyclicDependencyManager == null) {
			cyclicDependencyManager = new CyclicDependencyManager();
			dependencyManagerThreadLocal.set(cyclicDependencyManager);
		}
		cyclicDependencyManager.clearDependencies();
	}

	private void sanityCheck(final Persistable source, final Persistable target) {
		if (!source.getClass().equals(target.getClass())) {
			throw new RuntimeException("Source and Target should be instances of the same class"); // NOPMD
		}
	}

	/**
	 * Implementation of merge algorithm source on target.
	 *
	 * @param source source object
	 * @param target target object
	 * @throws SyncToolConfigurationException in case of configuration error
	 */
	protected void mergeInternal(final Persistable source, final Persistable target) throws SyncToolConfigurationException {
		sanityCheck(source, target);
		dependencyManagerThreadLocal.get().registerProcessedObject(source, target);

		final Map<Method, Method> basicAttributes = new TreeMap<Method, Method>(new MethodComparator());
		final Map<Method, Method> singleValuedAssociations = new TreeMap<Method, Method>(new MethodComparator());
		final Map<Method, Method> collectionValuedAssociations = new TreeMap<Method, Method>(new MethodComparator());
		final Set<Method> postLoadMethods = new HashSet<Method>();
		jpaPersistentStateLocator.extractPersistentStateAttributes(source.getClass(), basicAttributes, singleValuedAssociations,
				collectionValuedAssociations, postLoadMethods);

		for (final Entry<Method, Method> basicAttribute : basicAttributes.entrySet()) {
			resolveBasicAttribute(source, target, basicAttribute);
		}
		for (final Entry<Method, Method> singleValuedAssociation : singleValuedAssociations.entrySet()) {
			resolveSingleValuedAssociation(source, target, singleValuedAssociation);
		}
		for (final Entry<Method, Method> collectionValuedAssociation : collectionValuedAssociations.entrySet()) {
			resolveCollectionValuedAssociation(source, target, collectionValuedAssociation);
		}

		for (final Method postLoadMethod : postLoadMethods) {
			syncUtils.invokePostLoadMethod(target, postLoadMethod);
		}
	}

	private void resolveBasicAttribute(final Persistable object, final Persistable target, final Entry<Method, Method> accessors) {
		syncUtils.invokeCopyMethod(object, target, accessors);
	}

	private void resolveSingleValuedAssociation(final Persistable source, final Persistable target, final Entry<Method, Method> accessors)
			throws SyncToolConfigurationException {

		final Method getterMethod = accessors.getKey();
		final Method setterMethod = accessors.getValue();

		final Persistable sourceValue = (Persistable) syncUtils.invokeGetterMethod(source, getterMethod);
		Persistable targetValue = (Persistable) syncUtils.invokeGetterMethod(target, getterMethod);

		if (sourceValue == null) { // sets null value
			syncUtils.invokeSetterMethod(target, setterMethod, sourceValue);
			return;
		}

		final CyclicDependencyManager cyclicDependencyManager = dependencyManagerThreadLocal.get();
		if (cyclicDependencyManager.isCyclicDependency(sourceValue)) {
			syncUtils.invokeSetterMethod(target, setterMethod, cyclicDependencyManager.getTargetReference(sourceValue));
			return;
		}

		if (mergeBoundarySpec.stopMerging(sourceValue.getClass())) {
			if (targetValue == null || guidComparator.compare(targetValue, sourceValue) != 0) {
				syncUtils.invokeSetterMethod(target, setterMethod, retrieveFreshReference(sourceValue));
			}
			return;
		}

		if (targetValue == null) {
			targetValue = createNewPersistence(target, setterMethod, sourceValue);
		}

		// TODO: consider cascadeType annotation and don't prolong merge operation even if the entity is not in stop list,
		// since update will cause JPA exception at em.merge()
		mergeInternal(sourceValue, targetValue);
	}

	private Persistable createNewPersistence(final Persistable target, final Method setterMethod, final Persistable sourceValue) {
		final Persistable newTargetValue = beanCreator.createBean(sourceValue.getClass());
		syncUtils.invokeSetterMethod(target, setterMethod, newTargetValue);
		return newTargetValue;
	}

	/**
	 * Retrieves fresh reference (for instance, brand is changed in product).
	 *
	 * @param sourceValue domain object to find in target database
	 * @return fresh value
	 * @throws SyncToolRuntimeException if dependency on source value cannot be satisfied
	 */
	Persistable retrieveFreshReference(final Persistable sourceValue) throws SyncToolRuntimeException {
		final Persistable retrievedObject = entityLocator.locatePersistentReference(sourceValue);
		if (retrievedObject == null) {
			throw new SyncToolRuntimeException("Cannot retrieve entity at target environment corresponding to source environment: "
					+ sourceValue.getClass() + " with code: " + guidLocator.locateGuid(sourceValue));
		}
		return retrievedObject;
	}

	@SuppressWarnings("unchecked")
	private void resolveCollectionValuedAssociation(final Object source, final Object target, final Entry<Method, Method> accessors)
			throws SyncToolConfigurationException {

		final Method getterMethod = accessors.getKey();
		final Method setterMethod = accessors.getValue();

		final Object sourceCollectionOrMap = syncUtils.invokeGetterMethod(source, getterMethod);
		Object targetCollectionOrMap = syncUtils.invokeGetterMethod(target, getterMethod);

		if (sourceCollectionOrMap == null) {
			// Nullify target's collection
			syncUtils.invokeSetterMethod(target, setterMethod, sourceCollectionOrMap);
			return;
		}

		if (targetCollectionOrMap == null) {
			targetCollectionOrMap = createEmptyCollectionOrMap(target, setterMethod, sourceCollectionOrMap);
		}

		if (shouldNotMergeCollection(getterMethod)) {
			refreshCollection(getterMethod, sourceCollectionOrMap, targetCollectionOrMap);
			return;
		}

		if (sourceCollectionOrMap instanceof Map) {
			mergeMap(getterMethod, (Map<?, ?>) sourceCollectionOrMap, (Map<Object, Object>) targetCollectionOrMap);
		} else if (sourceCollectionOrMap instanceof Collection) {
			mergeCollection((Collection<?>) sourceCollectionOrMap, (Collection<Object>) targetCollectionOrMap);
		} else {
			throw new SyncToolRuntimeException("Unexpected collection type: " + sourceCollectionOrMap);
		}
	}

	private Object createEmptyCollectionOrMap(final Object target, final Method setterMethod, final Object sourceCollectionOrMap) {
		Object targetCollectionOrMap = null;
		if (sourceCollectionOrMap instanceof List<?>) {
			targetCollectionOrMap = new ArrayList<Object>();
		} else if (sourceCollectionOrMap instanceof Set<?>) {
			targetCollectionOrMap = new HashSet<Object>();
		} else if (sourceCollectionOrMap instanceof Map<?, ?>) {
			targetCollectionOrMap = new HashMap<Object, Object>();
		}
		syncUtils.invokeSetterMethod(target, setterMethod, targetCollectionOrMap);
		return targetCollectionOrMap;
	}

	/**
	 * Merges two collections of entities or value objects.
	 * For entities the algorithm updates each object from target collection,
	 * for value objects it deletes old and puts new into target collection.
	 *
	 * @param sourceCollection
	 * @param targetCollection
	 * @throws SyncToolConfigurationException
	 */
	private void mergeCollection(final Collection<?> sourceCollection, final Collection<? super Object> targetCollection)
			throws SyncToolConfigurationException {

		final Collection<Object> newObjects = new ArrayList<Object>();

		final CollectionElementsRemoveManager removeManager = new CollectionElementsRemoveManager(targetCollection);

		for (final Object sourceValue : sourceCollection) {
			final Persistable newPersistence = mergeCollectionElements(removeManager, (Persistable) sourceValue, targetCollection,
					isMergeable((Persistable) sourceValue));
			if (newPersistence != null) {
				newObjects.add(newPersistence);
			}
		}

		if (!newObjects.isEmpty() && valueObjectMerger.isMergeRequired(newObjects.iterator().next().getClass())) {
			for (final Object newObject : newObjects) {
				if (!targetCollection.add(newObject)) {
					updateValueObjectInCollection(targetCollection, newObject);
				}
			}
		} else {
			targetCollection.addAll(newObjects);
		}

		removeManager.removeSurplusObjectsFromCollection(targetCollection);
	}

	/**
	 * Check whether the sourceEntry can be merged or not based on a configured filter
	 * class.
	 *
	 * @param sourceEntry the entry to check
	 * @return true if filter returns true or no filter configured
	 */
	boolean isMergeable(final Persistable sourceEntry) {
		final String className = sourceEntry.getClass().getName();
		return !(mergeFilters.containsKey(className) && mergeFilters.get(className).isFiltered(sourceEntry));
	}

	/**
	 * Finds object equal to new object in collection and updates it.
	 *
	 * @param targetCollection collection to update an object in
	 * @param newObject object to take fields from for update
	 */
	void updateValueObjectInCollection(final Collection<?> targetCollection, final Object newObject) {
		for (final Object targetObject : targetCollection) {
			if (targetObject.equals(newObject)) {
				valueObjectMerger.merge(newObject, targetObject);
				return;
			}
		}
	}

	private void mergeMap(final Method getterMethod, final Map<?, ?> sourceMap, final Map<? super Object, ? super Object> targetMap)
			throws SyncToolConfigurationException {
		final Map<Object, Object> newObjects = new HashMap<Object, Object>();

		final CollectionElementsRemoveManager removeManager = new CollectionElementsRemoveManager(targetMap.values());

		for (final Object sourceEntry : sourceMap.values()) {
			final Persistable elementToAdd = mergeCollectionElements(removeManager, (Persistable) sourceEntry, targetMap.values(),
					isMergeable((Persistable) sourceEntry));
			if (elementToAdd != null) {
				final Object key = syncUtils.getMapKey(getterMethod.getAnnotation(MapKey.class), elementToAdd);
				newObjects.put(key, elementToAdd);
			}
		}
		targetMap.putAll(newObjects);
		removeManager.removeSurplusObjectsFromMap(targetMap);
	}

	/**
	 * Refreshes references in the <code>targetCollectionOrMap</code> using corresponding references from <code>sourceCollectionOrMap</code>.
	 *
	 * @param getterMethod getter method
	 * @param sourceCollectionOrMap source container
	 * @param targetCollectionOrMap target container
	 * @throws SyncToolConfigurationException in case of unsupported container type
	 */
	@SuppressWarnings("unchecked")
	void refreshCollection(final Method getterMethod, final Object sourceCollectionOrMap, final Object targetCollectionOrMap)
			throws SyncToolConfigurationException {
		if (sourceCollectionOrMap instanceof Map) {
			final Map<Object, Object> targetMap = (Map<Object, Object>) targetCollectionOrMap;
			targetMap.clear();

			final Map<Object, Object> sourceMap = (Map<Object, Object>) sourceCollectionOrMap;
			for (final Entry<Object, Object> entry : sourceMap.entrySet()) {
				final Persistable retrieveFreshReference = retrieveFreshReference((Persistable) entry.getValue());
				targetMap.put(syncUtils.getMapKey(getterMethod.getAnnotation(MapKey.class), retrieveFreshReference), retrieveFreshReference);
			}
		} else if (sourceCollectionOrMap instanceof Collection) {
			final Collection<Object> targetCollection = (Collection<Object>) targetCollectionOrMap;
			targetCollection.clear();

			final Collection<Object> sourceCollection = (Collection<Object>) sourceCollectionOrMap;
			for (final Object object : sourceCollection) {
				targetCollection.add(retrieveFreshReference((Persistable) object));
			}
		} else {
			throw new SyncToolRuntimeException("Unexpected collection type: " + sourceCollectionOrMap);
		}
	}

	/**
	 * Determines if in provided collection all elements should be updated or merged.
	 *
	 * @param method getting annotation for collection to check
	 * @return true if collection objects should be updated without deep merge (i.e. object's class in the boundary)
	 */
	boolean shouldNotMergeCollection(final Method method) {
		final OneToMany oneToManyAssociation = method.getAnnotation(OneToMany.class);
		Class< ? > targetEntity = null;

		if (oneToManyAssociation == null) {
			final ManyToMany manyToManyAssociation = method.getAnnotation(ManyToMany.class);
			if (manyToManyAssociation != null) {
				targetEntity = manyToManyAssociation.targetEntity();
			}
		} else {
			targetEntity = oneToManyAssociation.targetEntity();
		}

		if (targetEntity == null) {
			throw new SyncToolRuntimeException("Can't find OneToMany or ManyToMany for collection-value association");
		}

		return mergeBoundarySpec.stopMerging(targetEntity);
	}

	private Persistable mergeCollectionElements(final CollectionElementsRemoveManager removeManager, final Persistable sourceValue,
			final Collection< ? > targetCollection, final boolean isMergable) throws SyncToolConfigurationException {

		if (guidLocator.canQualifyByGuid(sourceValue.getClass())) {
			for (final Object targetValue : targetCollection) {
				if (guidComparator.compare((Persistable) targetValue, sourceValue) == 0 && isMergable) {
					mergeInternal(sourceValue, (Persistable) targetValue);
					removeManager.removeIdenticalObject(targetValue);
					return null;
				}
			}
		}

		if (isMergable) {
			final Persistable newTargetValue = beanCreator.createBean(sourceValue.getClass());
			mergeInternal(sourceValue, newTargetValue);
			removeManager.removeEqualObject(newTargetValue);
			return newTargetValue;
		}
		final Persistable persistable = entityLocator.locatePersistence(sourceValue);
		if (persistable == null) {
			throw new SyncToolRuntimeException("No entity on target for unmergable persistable: " + sourceValue.toString());
		}
		return persistable;
	}

	/**
	 * @param mergeBoundarySpec the mergeBoundarySpec to set
	 */
	public void setMergeBoundarySpecification(final MergeBoundarySpecification mergeBoundarySpec) {
		this.mergeBoundarySpec = mergeBoundarySpec;
	}

	/**
	 * @param entityLocator the entityLocator to set
	 */
	public void setEntityLocator(final EntityLocator entityLocator) {
		this.entityLocator = entityLocator;
	}

	/**
	 * @param guidLocator the guidLocator to set
	 */
	public void setGuidLocator(final GuidLocator guidLocator) {
		this.guidLocator = guidLocator;
	}

	/**
	 * @param beanCreator the beanCreator to set
	 */
	public void setBeanCreator(final BeanCreator beanCreator) {
		this.beanCreator = beanCreator;
	}

	/**
	 * @param jpaPersistentStateLocator the jpaPersistentStateLocator to set
	 */
	public void setJpaPersistentStateLocator(final PersistentStateLocator jpaPersistentStateLocator) {
		this.jpaPersistentStateLocator = jpaPersistentStateLocator;
	}

	/**
	 * @param syncUtils the syncUtils to set
	 */
	public void setSyncUtils(final SyncUtils syncUtils) {
		this.syncUtils = syncUtils;
	}

	/**
	 * @param guidComparator the guidComparator to set
	 */
	public void setGuidComparator(final Comparator<Persistable> guidComparator) {
		this.guidComparator = guidComparator;
	}

	/**
	 * @param valueObjectMerger value object merger to update specific value objects
	 */
	public void setValueObjectMerger(final ValueObjectMerger valueObjectMerger) {
		this.valueObjectMerger = valueObjectMerger;
	}

	/**
	 * @param mergeFilters merge filters mapping from String rep of class name to filter
	 */
	public void setMergeFilters(final Map<String, EntityFilter> mergeFilters) {
		this.mergeFilters = mergeFilters;
	}

	/**
	 * Get the merge filter for the specified class name string.
	 *
	 * @param clazz the class string
	 * @return the EntityFilter
	 */
	EntityFilter getMergeFilter(final String clazz) {
		return mergeFilters.get(clazz);
	}


}
