package com.elasticpath.importexport.common.adapters.catalogs.helper;

import java.util.List;
import java.util.Set;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * The Helper for AttributeGroup management.
 */
public interface AttributeGroupHelper {

	/**
	 * Checks if attribute group exists.
	 * 
	 * @param attributeGroupAttributes a set of AttributeGroupAttributes
	 * @param groupToFind the AttributeGroupAttribute which we want to find
	 * @return true if attributeGroupAttributes contains groupToFind, method compares by attribute only.
	 */
	boolean isAttributeGroupExist(final Set<AttributeGroupAttribute> attributeGroupAttributes, final AttributeGroupAttribute groupToFind);

	/**
	 * Creates AssignedAttributes List.
	 * 
	 * @param attributeGroupAttributes the set of AttributeGroupAttribute
	 * @return List of assigned attributes names
	 */
	List<String> createAssignedAttributes(final Set<AttributeGroupAttribute> attributeGroupAttributes);

	/**
	 * Tries to find attribute with key.
	 * 
	 * @param attributeKey the key of the attribute
	 * @throws PopulationRuntimeException if attribute does not exist
	 * @return Attribute instance if attribute was found
	 */
	Attribute findAttribute(final String attributeKey) throws PopulationRuntimeException;

	/**
	 * Populate AttributeGroupAttributes with list of assigned attributes.
	 * 
	 * @param attributeGroupAttributes the set to populate
	 * @param assignedAttributes assigned attributes which is used to populate
	 * @param attributeType the bean type for creating attributes.
	 */
	void populateAttributeGroupAttributes(final Set<AttributeGroupAttribute> attributeGroupAttributes, 
										  final List<String> assignedAttributes, 
										  final String attributeType);

}