package com.elasticpath.importexport.common.marshalling;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Fake Adapter for tests.
 */
@XmlRootElement(name = "fakeObject")
@XmlAccessorType(XmlAccessType.NONE)
public class FakeObjectDTO { 

	/**
	 * Name.
	 */
	@XmlAttribute(name = "name", required = true)
	private String name;

	/**
	 * Code.
	 */
	@XmlElement(name = "code", required = true)
	private String code;

	/**
	 * Related object.
	 */
	@XmlElementWrapper(name = "relation")
	@XmlElement(name = "relatedObject", required = true)
	private List<FakeRelatedObjectDTO> fakeRelatedObjectList = new ArrayList<FakeRelatedObjectDTO>();

	/**
	 * Fill target object properties.
	 * 
	 * @param target target object
	 */
	public void fillTarget(final FakeObjectDTO target) {
		// TODO Auto-generated method stub
	}

	/**
	 * Populate object from source.
	 * 
	 * @param source source object.
	 */
	public void populateFromSource(final FakeObjectDTO source) {
		// TODO Auto-generated method stub
	}

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
	 * Gets the code.
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 * 
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets the fakeRelatedObjectList.
	 * 
	 * @return the fakeRelatedObjectList
	 */
	public List<FakeRelatedObjectDTO> getFakeRelatedObjectList() {
		return fakeRelatedObjectList;
	}

	/**
	 * Sets the related objects.
	 * 
	 * @param fakeRelatedObjectList related objects
	 */
	public void setFakeRelatedObjectList(final List<FakeRelatedObjectDTO> fakeRelatedObjectList) {
		this.fakeRelatedObjectList = fakeRelatedObjectList;
	}
	
	@Override
	public boolean equals(final Object object) {  // NOPMD
		if (object == this) {
			return true;
		}
		if (!(object instanceof FakeObjectDTO)) {
		    return false;
		}
		FakeObjectDTO object2 = (FakeObjectDTO) object;
		
		if (!eq(object2.code, this.code)) {
			return false;
		}
		if (!eq(object2.name, this.name)) {
			return false;
		}
		if (object2.fakeRelatedObjectList == null) {
			if (this.fakeRelatedObjectList != null) {
				return false;
			}
		} else {
			if (!object2.fakeRelatedObjectList.containsAll(this.fakeRelatedObjectList)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean eq(final String str1, final String str2) { // NOPMD
		if (str1 == null) {
			return str2 == null;
		}
		return str1.equals(str2);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (code == null) {
			result = prime * result;
		} else {
			result = prime * result + code.hashCode();
		}
		if (fakeRelatedObjectList == null) {
			result = prime * result;
		} else {
			result = prime * result + fakeRelatedObjectList.hashCode();
		}
		if (name == null) {
			result = prime * result;
		} else {
			result = prime * result + name.hashCode();
		}
		return result;
	}
	
	
}
