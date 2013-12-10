package com.elasticpath.importexport.common.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.elasticpath.importexport.common.types.PackageType;

/**
 * <code>PackagerConfiguration</code> designed for JAXB to load package type from XML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PackagerConfiguration {

	@XmlAttribute(name = "type")
	private PackageType type;
	
	@XmlAttribute(name = "packagename")
	private String packageName;

	/**
	 * Gets package type to configure packager or unpackager.
	 *
	 * @return package type 
	 */
	public PackageType getType() {
		return type;
	}
	
	/**
	 * Sets package type. 
	 *
	 * @param packageType package type
	 */
	public void setType(final PackageType packageType) {
		this.type = packageType;
	}
	
	/**
	 * Gets the name of file to be produced by packager.
	 * 
	 * @return the name of file presents the package
	 */
	public String getPackageName() {
		return packageName;
	}
	
	/**
	 * Sets the name of file to be produced by packager.
	 * 
	 * @param packageName the name of file presents the package
	 */
	public void setPackageName(final String packageName) {
		this.packageName = packageName;
	}
}
