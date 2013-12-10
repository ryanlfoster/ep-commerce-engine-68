/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.BusinessObjectMetadataResolver;
import com.elasticpath.service.changeset.BusinessObjectResolver;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.service.changeset.dao.ChangeSetDao;
import com.elasticpath.service.objectgroup.dao.BusinessObjectGroupDao;

/**
 * The default implementation of the {@link ChangeSetPolicy} which 
 * restricts an object to be part of no more than one change set.
 */
public class ChangeSetPolicyImpl implements ChangeSetPolicy {
	
	private static final Logger LOG = Logger.getLogger(ChangeSetPolicyImpl.class);

	private BusinessObjectResolver businessObjectResolver;
	
	private BusinessObjectGroupDao objectGroupDao;
	
	private ChangeSetDao changeSetDao;
	
	private List<ChangeSetDependencyResolver> changeSetDependentResolvers;
	
	private List<BusinessObjectMetadataResolver> metadataResolvers = new ArrayList<BusinessObjectMetadataResolver>();
	
	/**
	 * Set change set dependents.
	 *  
	 * @param changeSetDependentResolvers the list of change set dependent
	 */
	public void setChangeSetDependentResolvers(final List<ChangeSetDependencyResolver> changeSetDependentResolvers) {
		this.changeSetDependentResolvers = changeSetDependentResolvers;
	}

	/**
	 * Retrieves from the {@link BusinessObjectGroupDao} all the groups the group member takes part.
	 * 
	 * @param objectDescriptor the descriptor to use
	 * @return a collection of group IDs (change set GUIDs)
	 */
	public Collection<String> getObjectMembershipGuids(final BusinessObjectDescriptor objectDescriptor) {
		if (objectDescriptor == null) {
			throw new IllegalArgumentException("Business object descriptor cannot be <null>");
		}
		Collection<String> groupIds = objectGroupDao.findGroupIdsByDescriptor(objectDescriptor);
		return filterGroupIds(groupIds);
	}

	/**
	 * Finters group IDs by non-finalized states.
	 * 
	 * @param groupIds the group IDs to filter
	 * @return the new collection of non-finalized IDs
	 */
	protected Collection<String> filterGroupIds(final Collection<String> groupIds) {
		return changeSetDao.findAvailableChangeSets(groupIds, getNonFinalizedStates());
	}

	/**
	 * Returns a collection of change states that signifies a change set as non-closed.
	 * 
	 * @return a collection of state codes
	 */
	public Collection<ChangeSetStateCode> getNonFinalizedStates() {
		return Arrays.asList(ChangeSetStateCode.LOCKED, ChangeSetStateCode.OPEN);
	}

	/**
	 * Sets the DAO to handle business object groups.
	 * 
	 * @param objectGroupDao the DAO instance
	 */
	public void setBusinessObjectGroupDao(final BusinessObjectGroupDao objectGroupDao) {
		this.objectGroupDao = objectGroupDao;
	}

	/**
	 * Sets the DAO to handle change sets.
	 * 
	 * @param changeSetDao the DAO instance
	 */
	public void setChangeSetDao(final ChangeSetDao changeSetDao) {
		this.changeSetDao = changeSetDao;
	}

	/**
	 * Resolves the given object to a {@link BusinessObjectDescriptor}.
	 * 
	 * @param object The object to resolve.
	 * @return a BusinessObjectDescriptor of the given object, or null if not resolvable.
	 */
	public BusinessObjectDescriptor resolveObjectDescriptor(final Object object) {
		return businessObjectResolver.resolveObjectDescriptor(object);
	}


	/**
	 * Resolves the given object guid.
	 * 
	 * @param object The object to resolve.
	 * @return a BusinessObjectDescriptor of the given object, or null if not resolvable.
	 */
	public String resolveObjectGuid(final Object object) {
		return businessObjectResolver.resolveObjectGuid(object);
	}


	@Override
	public Class< ? > getObjectClass(final BusinessObjectDescriptor objectDescriptor) {
		return businessObjectResolver.getObjectClass(objectDescriptor);
	}

	@Override
	public boolean isChangeAllowed(final String changeSetGuid) {
		final ChangeSet changeSet = this.changeSetDao.findByGuid(changeSetGuid);
		if (changeSet == null) {
			LOG.info("Change set with GUID: " + changeSetGuid + " could not be found");
			return true;
		}
		final ChangeSetStateCode stateCode = changeSet.getStateCode();
		
		return ObjectUtils.equals(ChangeSetStateCode.OPEN, stateCode);
	}

	@Override
	public boolean canRemove(final String guid) {
		return CollectionUtils.isEmpty(objectGroupDao.findGroupMembersByGroupId(guid));
	}

	@Override
	public boolean isClassSupported(final Class< ? > clazz) {
		return businessObjectResolver.isClassSupported(clazz);
	}

	/**
	 * @param businessObjectResolver the businessObjectResolver to set
	 */
	public void setBusinessObjectResolver(final BusinessObjectResolver businessObjectResolver) {
		this.businessObjectResolver = businessObjectResolver;
	}

	/**
	 * @return the businessObjectResolver
	 */
	public BusinessObjectResolver getBusinessObjectResolver() {
		return businessObjectResolver;
	}
	
	
	/**
	 * Get the dependent objects
	 * The method need convert the source object descriptor to its real object, such like product, sku or category...
	 * Then get the dependent objects for that real object
	 * Then convert the real dependent objects to business object descriptors 
	 * 
	 * @param object the business object descriptor
	 * @param objectClass the object class
	 * @return a set of business object descriptor of the dependent object
	 */
	public Set<BusinessObjectDescriptor> getDependentObjects(final BusinessObjectDescriptor object, final Class< ? > objectClass) {
		Object resolvedObject = null;
		for (ChangeSetDependencyResolver changeSetDependentResolver : changeSetDependentResolvers) {
			Object objectFound = changeSetDependentResolver.getObject(object, objectClass);
			if (objectFound != null && (resolvedObject == null
					|| resolvedObject.getClass().isAssignableFrom(objectFound.getClass()))) {
				resolvedObject = objectFound; 
			}
		}
		return getDependentObjects(resolvedObject);
	}

	@Override
	public Map<String, String> resolveMetaData(final BusinessObjectDescriptor objectDescriptor) {
		Map<String, String> metaData = new HashMap<String, String>();
		for (BusinessObjectMetadataResolver resolver : getMetadataResolvers()) {
			metaData.putAll(resolver.resolveMetaData(objectDescriptor));
		}
		return metaData;
	}

	/**
	 * Set the list of metadata resolvers.
	 * 
	 * @param metadataResolvers the metadataResolvers to set
	 */
	public void setMetadataResolvers(final List<BusinessObjectMetadataResolver> metadataResolvers) {
		this.metadataResolvers = metadataResolvers;
	}

	/**
	 * Get the list of metadata resolvers.
	 *
	 * @return the metadataResolvers
	 */
	public List<BusinessObjectMetadataResolver> getMetadataResolvers() {
		return metadataResolvers;
	}

	@Override
	public Set<BusinessObjectDescriptor> getDependentObjects(final Object object) {
		Set<BusinessObjectDescriptor> dependentBusinessObjects = new LinkedHashSet<BusinessObjectDescriptor>();
		for (ChangeSetDependencyResolver changeSetDependentResolver : changeSetDependentResolvers) {
			Set< ? > dependentObjects = changeSetDependentResolver.getChangeSetDependency(object);
			dependentBusinessObjects.addAll(resolveObjectDescriptor(dependentObjects));
		}
		return dependentBusinessObjects;
	}
	
	/**
	 * Resolves the given objects to a {@link BusinessObjectDescriptor}.
	 * 
	 * @param objects The set of objects to resolve.
	 * @return a BusinessObjectDescriptor of the given object, or null if not resolvable.
	 */
	public Set<BusinessObjectDescriptor> resolveObjectDescriptor(final Set< ? > objects) {
		return businessObjectResolver.resolveObjectDescriptor(objects);
	}
}
