package com.elasticpath.domain.attribute.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * This is a default implementation of <code>AttributeGroupAttribute</code>.
 */
@MappedSuperclass
@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = { @FetchAttribute(name = "attribute") })
public class AttributeGroupAttributeImpl extends AbstractPersistableImpl implements AttributeGroupAttribute {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private int ordering;

	private Attribute attribute;

	private long uidPk;

	/**
	 * Get the ordering number.
	 * 
	 * @return the ordering number
	 */
	@Basic
	@Column(name = "ORDERING")
	public int getOrdering() {
		return this.ordering;
	}

	/**
	 * Get the Attribute.
	 * 
	 * @return the Attribute
	 */
	@OneToOne(targetEntity = AttributeImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "ATTRIBUTE_UID")
	@ForeignKey
	public Attribute getAttribute() {
		return this.attribute;
	}

	/**
	 * Set the Attribute.
	 * 
	 * @param attribute the Attribute to set
	 */
	public void setAttribute(final Attribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * Set the ordering number.
	 * 
	 * @param ordering the ordering number
	 */
	public void setOrdering(final int ordering) {
		this.ordering = ordering;
	}

	/**
	 * Compares this instance with the given instance for ordering.
	 * 
	 * @param other the given object
	 * @return a negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the specified object.
	 * @throws EpDomainException if the given object is not a <code>AttributeGroupAttribute</code>
	 */
	public int compareTo(final AttributeGroupAttribute other) throws EpDomainException {

		if (this == other) {
			return 0;
		}

		// Compare ordering first
		if (this.getOrdering() < other.getOrdering()) {
			return -1;
		} else if (this.getOrdering() > other.getOrdering()) {
			return 1;
		} else {
			// Then compare the attribute default ordering.
			return this.getAttribute().compareTo(other.getAttribute());
		}
	}

	/**
	 * Return the hash code.
	 * 
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (this.attribute == null) {
			result = prime * result;
		} else {
			result = prime * result + this.attribute.hashCode();
		}

		result = prime * result + this.ordering;
		return result;
	}

	/**
	 * Return <code>true</code> if the given object is <code>AttributeGroupAttribute</code> and is logically equal.
	 * 
	 * @param obj the object to compare
	 * @return <code>true</code> if the given object is equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AttributeGroupAttributeImpl)) {
			return false;
		}
		final AttributeGroupAttributeImpl other = (AttributeGroupAttributeImpl) obj;
		if (this.attribute == null) {
			if (other.attribute != null) {
				return false;
			}
		} else if (!this.attribute.equals(other.attribute)) {
			return false;
		}
		if (this.ordering != other.ordering) {
			return false;
		}
		return true;
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
	
    /**
     * String representation of this object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
            final StringBuffer sbf = new StringBuffer();
            sbf.append("AttributeGroupAttribute -> Attribute: ").append(this.getAttribute());
            return sbf.toString();
    }

}
