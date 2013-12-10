package com.elasticpath.importexport.exporter.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.importexport.common.exception.runtime.ExportRuntimeException;

/**
 * Register of dependent domain objects and assets. During export we should be able to guarantee that for every exported objects we export all
 * objects it depends on in case we use exporter for the last type of objects. <br>
 * Register is based on map and keeps UIDs or GUIDs of dependent objects to be exported by next exporter in the sequence. Asset files required for
 * exported objects are kept in separate list.
 */
public class DependencyRegistry {

	private final Map<Class< ? >, Set<Object>> dependentObjects = new HashMap<Class< ? >, Set<Object>>();

	private final Set<String> assetFileNames = new HashSet<String>();

	/**
	 * Constructor prepares keys and sets of UIDs as values for classes that should be exported.
	 * 
	 * @param supportedDependencies Classes that are supported dependent types
	 */
	public DependencyRegistry(final List<Class< ? >> supportedDependencies) {
		for (Class< ? > clazz : supportedDependencies) {
			dependentObjects.put(clazz, new HashSet<Object>());
		}
	}

	/**
	 * Adds dependent objects to be exported by appropriate exporter. If pair (clazz, set) doesn't exist then objects can't be exported in the future
	 * 
	 * @param clazz key - class of dependent domain objects which must be exported
	 * @param dependentUids UIDs of domain objects to be exported
	 */
	public void addUidDependencies(final Class< ? > clazz, final Set<Long> dependentUids) {
		checkExistence(clazz);
		dependentObjects.get(clazz).addAll(dependentUids);
	}

	/**
	 * Adds dependent object to be exported by appropriate exporter. If pair (clazz, set) doesn't exist then objects can't be exported in the future
	 * 
	 * @param clazz key - class of dependent domain object which must be exported
	 * @param dependentUid UID of domain object to be exported
	 */
	public void addUidDependency(final Class< ? > clazz, final Long dependentUid) {
		checkExistence(clazz);
		dependentObjects.get(clazz).add(dependentUid);
	}

	/**
	 * Adds dependent objects to be exported by appropriate exporter. If pair (clazz, set) doesn't exist then objects can't be exported in the future
	 * 
	 * @param clazz key - class of dependent domain objects which must be exported
	 * @param dependentGuids GUIDs of domain objects to be exported
	 */
	public void addGuidDependencies(final Class< ? > clazz, final Set<String> dependentGuids) {
		checkExistence(clazz);
		dependentObjects.get(clazz).addAll(dependentGuids);
	}

	/**
	 * Adds dependent object to be exported by appropriate exporter. If pair (clazz, set) doesn't exist then objects can't be exported in the future
	 * 
	 * @param clazz key - class of dependent domain object which must be exported
	 * @param dependentGuids GUIDs of domain object to be exported
	 */
	public void addGuidDependency(final Class< ? > clazz, final String dependentGuids) {
		checkExistence(clazz);
		dependentObjects.get(clazz).add(dependentGuids);
	}

	/*
	 * Verifies that clazz key exists in the map of dependent object UIDs.
	 */
	private void checkExistence(final Class< ? > clazz) {
		if (!dependentObjects.containsKey(clazz)) {
			throw new ExportRuntimeException("IE-20400", clazz.getCanonicalName());
		}
	}

	/**
	 * Retrieve UIDs of domain objects by class. Exporter responsible on export of these objects should take the set and merge it with objects he
	 * found analyzing search query
	 * 
	 * @param clazz key to find UIDs of domain objects to be exported
	 * @return set of domain objects' UIDs
	 * @throws ExportRuntimeException if clazz key doesn't exist in map
	 */
	public Set<Long> getDependentUids(final Class< ? > clazz) {
		if (!dependentObjects.containsKey(clazz)) {
			throw new ExportRuntimeException("IE-20401", clazz.getCanonicalName());
		}
		Set<Object> ids = dependentObjects.get(clazz);
		Set<Long> uids = new HashSet<Long>(ids.size());
		for (Object object : ids) {
			try {
				uids.add((Long) object);
			} catch (ClassCastException e) {
				throw new ExportRuntimeException("IE-20402", e, clazz.getCanonicalName());
			}
		}
		return uids;
	}

	/**
	 * Retrieve GUIDs of domain objects by class. Exporter responsible on export of these objects should take the set and merge it with objects he
	 * found analyzing search query
	 * 
	 * @param clazz key to find GUIDs of domain objects to be exported
	 * @return set of domain objects' FUIDs
	 * @throws ExportRuntimeException if clazz key doesn't exist in map
	 */
	public Set<String> getDependentGuids(final Class< ? > clazz) {
		if (!dependentObjects.containsKey(clazz)) {
			throw new ExportRuntimeException("IE-20401", clazz.getCanonicalName());
		}
		Set<Object> ids = dependentObjects.get(clazz);
		Set<String> uids = new HashSet<String>(ids.size());
		for (Object object : ids) {
			try {
				uids.add((String) object);
			} catch (ClassCastException e) {
				throw new ExportRuntimeException("IE-20403", e, clazz.getCanonicalName());
			}
		}
		return uids;
	}

	/**
	 * Check if objects of clazz class should be exported because other objects depend on them.
	 * 
	 * @param clazz class of objects' UIDs is used as a key
	 * @return true if pair (clazz, set) exists in map
	 */
	public boolean supportsDependency(final Class< ? > clazz) {
		return dependentObjects.containsKey(clazz);
	}

	/**
	 * Adds file name of asset required for exported object.
	 * 
	 * @param directory the directory of file, it can be null
	 * @param assetFileName file name of asset to be exported, in case assetFileName is null or empty string it will not be added.
	 */
	public void addAsset(final String directory, final String assetFileName) {
		String useDirectory = "";
		if (directory != null) {
			useDirectory = directory;
		}

		if (assetFileName != null && !"".equals(assetFileName)) {
			assetFileNames.add(useDirectory + "/" + assetFileName);
		}
	}

	/**
	 * Gets the set with file names of assets to be exported.
	 * 
	 * @return the set with asset file names
	 */
	public Set<String> getAssetFileNames() {
		return assetFileNames;
	}
}
