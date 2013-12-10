package com.elasticpath.service.changeset;

import java.util.Set;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * The interface of change set dependency resolver.
 */
public interface ChangeSetDependencyResolver {

	/**
	 * Get change set dependency. 
	 * For example, category depends on parent category
	 * product depends on parent category
	 * product also depends on another product via a merchandising association
	 * 
	 * @param object the object which is used to get the change set dependency object 
	 * @return a list of dependent object
	 * @since 6.2.2
	 */
	Set< ? > getChangeSetDependency(Object object);

	/**
	 * Get the real object by business object and its type.
	 * For example, return the product entity if the business object type is Product
	 * 
	 * @param object the business object
	 * @param objectClass the class of business object 
	 * @return the real object
	 */
	Object getObject(BusinessObjectDescriptor object, Class< ? > objectClass);
}
