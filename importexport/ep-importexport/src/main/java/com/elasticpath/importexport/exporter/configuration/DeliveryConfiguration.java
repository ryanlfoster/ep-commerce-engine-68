package com.elasticpath.importexport.exporter.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.importexport.common.types.TransportType;

/**
 * DeliveryConfiguration contains preferences for Delivery Method's creation and initialization.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DeliveryConfiguration {

	@XmlElement(name = "method", required = true)
	private TransportType method;

	@XmlElement(name = "target", required = true)
	private String target;

	/**
	 * Gets delivery method type to create appropriate Delivery Method according to.
	 *
	 * @return delivery method type represented by TransportType
	 */
	public TransportType getMethod() {
		return method;
	}

	/**
	 * Sets delivery method type to define Delivery Method used in export job.
	 *
	 * @param method MethodType.
	 */
	public void setMethod(final TransportType method) {
		this.method = method;
	}

	/**
	 * Gets full target access path to deliver into.
	 *
	 * @return string representation of the full target access path
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Sets full target access path to deliver into.
	 *
	 * @param target string representation of the full target access path
	 */
	public void setTarget(final String target) {
		this.target = target;
	}
}
