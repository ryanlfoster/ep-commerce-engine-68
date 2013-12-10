package com.elasticpath.domain.attribute.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Transient;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Represents a default implementation of <code>AttributeGroup</code>.
 */
public class AttributeGroupImpl extends AbstractPersistableImpl implements AttributeGroup {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private Set<AttributeGroupAttribute> attributeGroupAttributes = new TreeSet<AttributeGroupAttribute>();

	private long uidPk;

	/**
	 * Get the set of attributes associated with this product type.
	 *
	 * @return the set of attributes
	 */
	public Set<AttributeGroupAttribute> getAttributeGroupAttributes() {
		return this.attributeGroupAttributes;
	}

	/**
	 * Set the set of attributes associated with this product type.
	 *
	 * @param attributes the attributes to set
	 */
	public void setAttributeGroupAttributes(final Set<AttributeGroupAttribute> attributes) {
		this.attributeGroupAttributes = attributes;
	}

	/**
	 * Add an attribute to belong to this product type.
	 *
	 * @param attributeGroupAttribute the product type attribute to add.
	 */
	public void addAttributeGroupAttribute(final AttributeGroupAttribute attributeGroupAttribute) {
		this.attributeGroupAttributes.add(attributeGroupAttribute);
	}

	/**
	 * Compares attribute sets on this and attribute group attribute set before an update and returns a set of attributes that have been deleted.
	 *
	 * @param before attributeGroupAttribute set before an update.
	 * @return set of attributes that have been deleted.
	 */
	public Set<Attribute> getRemovedAttributes(final Set<AttributeGroupAttribute> before) {
		Set<Attribute> returnSet = new HashSet<Attribute>();
		if (before != null && !before.isEmpty()) {
			for (AttributeGroupAttribute attributeGroupAttribute : before) {
				Attribute attBefore = attributeGroupAttribute.getAttribute();
				if (this.attributeGroupAttributes == null || this.attributeGroupAttributes.isEmpty() || !containsAttributeKey(attBefore)) {
					returnSet.add(attBefore);
					continue;
				}
			}
		}
		return returnSet;
	}

	private boolean containsAttributeKey(final Attribute att) {
		for (AttributeGroupAttribute attributeGroupAttribute : this.attributeGroupAttributes) {
			Attribute thisAtt = attributeGroupAttribute.getAttribute();
			if (thisAtt.getKey().equals(att.getKey())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Transient
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
}
