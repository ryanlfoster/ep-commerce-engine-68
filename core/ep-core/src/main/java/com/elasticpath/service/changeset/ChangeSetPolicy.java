/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Allows for abstracting the rules behind change sets so that this
 * is the only entry point of logic for managing 
 * the state of objects being in or outside of a change set.
 */
public interface ChangeSetPolicy extends BusinessObjectResolver {

	/**
	 * Gets all change set GUIDs that the given object belongs to.
	 * 
	 * @param objectDescriptor the object descriptor
	 * @return a collection of change list GUIDs
	 */
	Collection<String> getObjectMembershipGuids(BusinessObjectDescriptor objectDescriptor);
	
	/**
	 * Verifies whether a change set can be changed.
	 * 
	 * @param changeSetGuid the change set GUID
	 * @return true if the change set is allowed to be modified
	 */
	boolean isChangeAllowed(String changeSetGuid);

	/**
	 * Returns a collection of state codes that determine a change set as a non-finalized.
	 * 
	 * @return a collection of non-finalized states
	 */
	Collection<ChangeSetStateCode> getNonFinalizedStates();
	
	/**
	 * Checks whether a change set could be removed from the system.
	 * 
	 * @param guid the change set GUID
	 * @return true if a change set could be removed
	 */
	boolean canRemove(String guid);
	
	/**
	 * Resolve metadata for the given business object.
	 * 
	 * @param objectDescriptor the descriptor of the business object to resolve metadata for
	 * @return a metadata map.
	 */
	Map<String, String> resolveMetaData(final BusinessObjectDescriptor objectDescriptor);

	/**
	 * Get the dependent objects.
	 * 
	 * @param object the source object
	 * @return a set of objects which the source object depends on
	 */
	Set<BusinessObjectDescriptor> getDependentObjects(Object object);

	/**
	 * Get the dependent objects.  
	 * 
	 * @param object the business object
	 * @param objectClass the class type of business object
	 * @return a set of objects which the business object depends on
	 */
	Set<BusinessObjectDescriptor> getDependentObjects(final BusinessObjectDescriptor object, final Class< ? > objectClass);

}
