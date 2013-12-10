package com.elasticpath.tags;

import java.io.Serializable;

/**
 * Tags instances are numeric or string values applied to tag definitions.
 * 
 * @see TagSet
 */
public class Tag implements Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
 
	private Object value;
	
	/**
	 * Constructor.
	 * 
	 * @param value string
	 */
	public Tag(final Object value) {
		this.value = value;
	}
	
	/**
	 * Default Constructor.
	 */
	public Tag() {
		//Nothing to do
	}


	/**
	 * @return string value of this tag
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @return hash of the tag value.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		if (value != null) {
			result = result + value.hashCode();
		}
		return result;
	}

	/**
	 * @param obj to compare
	 * @return true if string value of the other object equals this tag's string value
	 */
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Tag other = (Tag) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	/**
	 * @return string representation of the tag value
	 */
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Tag: {");
		if (this.value != null) {
			stringBuilder.append("value:[");
			stringBuilder.append(value);
			stringBuilder.append("], type:[");
			stringBuilder.append(value.getClass().getName());
			stringBuilder.append(']');
		}
		stringBuilder.append('}');
		return stringBuilder.toString();
	}

}
