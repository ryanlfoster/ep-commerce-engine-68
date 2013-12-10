package com.elasticpath.importexport.common.marshalling;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Fake Adapter for tests.
 */
@XmlRootElement(name = "fakeRelatedObject")
@XmlAccessorType(XmlAccessType.NONE)
public class FakeRelatedObjectDTO {

	/**
	 * Name.
	 */
	@XmlAttribute(name = "name", required = true)
	private String name;

	/**
	 * Id.
	 */
	@XmlAttribute(name = "id", required = true)
	private int identity;

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the identity.
	 * 
	 * @return the identity
	 */
	public int getIdentity() {
		return identity;
	}

	/**
	 * Sets the identity.
	 * 
	 * @param identity the identity to set
	 */
	public void setIdentity(final int identity) {
		this.identity = identity;
	}

	@Override
	public boolean equals(final Object object) { // NOPMD
		if (object == this) {
			return true;
		}
		if (!(object instanceof FakeRelatedObjectDTO)) {
			return false;
		}
		FakeRelatedObjectDTO object2 = (FakeRelatedObjectDTO) object;
	
		if (object2.identity != this.identity) {
			return false;
		}
		if (this.name == null) {
			if (object2.name != null) {
				return false;
			}
		} else {
			if (!this.name.equals(object2.name)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + identity;
		if (name == null) {
			result = prime * result;
		} else {
			result = prime * result + name.hashCode();
		}
		return result;
	}
}
