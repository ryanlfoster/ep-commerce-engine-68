package com.elasticpath.tools.sync.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The ConnectionConfiguration DTO.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "connectionconfiguration")
public class ConnectionConfiguration {

	/**
	 * A constant for a remote type of connection configuration.
	 */
	public static final String TYPE_LOCAL = "local";
	
	/**
	 * A constant for a remote type of connection configuration.
	 */
	public static final String TYPE_REMOTE = "remote";
	
	@XmlAttribute(required = true)
	private String type;

	@XmlElement(required = true)
	private String url;

	@XmlElement(required = true)
	private String login;

	@XmlElement(required = true)
	private String pwd;

	@XmlElement(required = false)
	private String driver;

	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is String
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 * 
	 * @param value allowed object is String
	 */
	public void setType(final String value) {
		this.type = value;
	}

	/**
	 * Gets the value of the url property.
	 * 
	 * @return possible object is String
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the value of the url property.
	 * 
	 * @param value allowed object is String
	 */
	public void setUrl(final String value) {
		this.url = value;
	}

	/**
	 * Gets the value of the login property.
	 * 
	 * @return possible object is String
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Sets the value of the login property.
	 * 
	 * @param value allowed object is String
	 */
	public void setLogin(final String value) {
		this.login = value;
	}

	/**
	 * Gets the value of the pwd property.
	 * 
	 * @return possible object is String
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * Sets the value of the pwd property.
	 * 
	 * @param value allowed object is String
	 */
	public void setPwd(final String value) {
		this.pwd = value;
	}

	/**
	 * Gets the value of the driver property.
	 * 
	 * @return possible object is String
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * Sets the value of the driver property.
	 * 
	 * @param value allowed object is String
	 */
	public void setDriver(final String value) {
		this.driver = value;
	}

	@Override
	public String toString() {
		return String.format("(type=%s, url=%s)", type, url);
	}
}
