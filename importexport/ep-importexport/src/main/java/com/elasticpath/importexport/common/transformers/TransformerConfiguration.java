package com.elasticpath.importexport.common.transformers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * <code>TransformerConfiguration</code> designed for JAXB to load necessary properties for transformer from XML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TransformerConfiguration {

	@XmlElement(name = "type")
	private String className;

	/**
	 * Gets the full qualified class name for transformer.
	 *
	 * @return transformer class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the full qualified class name for transformer.
	 *
	 * @param className transformer class name
	 */
	public void setClassName(final String className) {
		this.className = className;
	}
}
