/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.objectgroup.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * The default implementation of the descriptor.
 */
public class BusinessObjectDescriptorImpl implements BusinessObjectDescriptor {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String objectIdentifier;
	private String objectType;

	@Override
	public String getObjectIdentifier() {
		return objectIdentifier;
	}

	@Override
	public String getObjectType() {
		return objectType;
	}

	/**
	 *
	 * @param objectIdentifier the objectIdentifier to set
	 */
	public void setObjectIdentifier(final String objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}

	/**
	 *
	 * @param objectType the objectType to set
	 */
	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	/**
	 * Compares this object with the one passed
	 * by their object identifiers and types.
	 * 
	 * @param obj the object to compare with
	 * @return true if objects are equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof BusinessObjectDescriptor)) {
			return false;
		}
		BusinessObjectDescriptor otherDesc = (BusinessObjectDescriptor) obj;
		
		return new EqualsBuilder().append(getObjectIdentifier(), otherDesc.getObjectIdentifier()).
			append(getObjectType(), otherDesc.getObjectType()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getObjectIdentifier()).append(getObjectType()).toHashCode();
	}

	/**
	 * Returns object state string, for example: BusinessObjectDescriptorImpl[objectType=Product,objectIdentifier=123].
	 * 
	 * @return Object state as string.
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("objectType", getObjectType())
			.append("objectIdentifier", getObjectIdentifier())
	        .toString();
	}
	
	
}
