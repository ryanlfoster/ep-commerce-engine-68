package com.elasticpath.importexport.common.dto.products;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data for attributes objects.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class AttributeValuesDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "key", required = true)
	private String key;

	@XmlElement(name = "value", required = true)
	private List<DisplayValue> values;
	
	/**
	 * Gets the attribute key.
	 * 
	 * @return the attribute key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the attribute key.
	 * 
	 * @param key the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Gets the attribute values.
	 * 
	 * @return the values
	 */
	public List<DisplayValue> getValues() {
		if (values == null) {
			return Collections.emptyList();
		}
		return values;
	}

	/**
	 * Sets the attribute values.
	 * 
	 * @param values the values to set
	 */
	public void setValues(final List<DisplayValue> values) {
		this.values = values;
	}

}
