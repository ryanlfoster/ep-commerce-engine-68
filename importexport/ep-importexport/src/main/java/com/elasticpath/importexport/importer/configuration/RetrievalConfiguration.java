package com.elasticpath.importexport.importer.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.importexport.common.types.TransportType;

/**
 * RetrievalConfiguration contains preferences for Retrieval Method's creation and initialization.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RetrievalConfiguration {

	@XmlElement(name = "method")
	private TransportType method;

	@XmlElement(name = "source")
	private String source;

	/**
	 * Gets Retrieval Method's transport type.
	 *
	 * @return MethodType
	 */
	public TransportType getMethod() {
		return method;
	}
	
	/**
	 * Sets Retrieval Method's transport type.
	 * 
	 * @param method Method Type
	 */
	public void setMethod(final TransportType method) {
		this.method = method;
	}

	/**
	 * Gets the Source to retrieve from.
	 *
	 * @return string representation of source
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * Sets the Source to retrieve from.
	 * 
	 * @param source string representation
	 */
	public void setSource(final String source) {
		this.source = source;
	}
}
