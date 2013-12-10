package com.elasticpath.importexport.common.manifest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The <code>Manifest</code> class is java representation of manifest.xml file that is responsible for storage names of files for import-export
 * operations and managing dependencies between them.
 */
@XmlRootElement(name = "manifest")
@XmlAccessorType(XmlAccessType.NONE)
public class Manifest {

	/**
	 * Represents the default name of manifest xml file.
	 */
	public static final String MANIFEST_XML = "manifest.xml";
	
	private static final String DEFAULT_VERSION = "6.0";

	@XmlElement(name = "version", required = true)
	private final String version;

	@XmlElement(name = "resource", required = true)
	private final List<String> resources;

	/**
	 * Constructs the empty object with default version.
	 */
	public Manifest() {
		version = DEFAULT_VERSION;
		resources = new ArrayList<String>();
	}

	/**
	 * Gets manifest version.
	 * 
	 * @return manifest version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the list of resources.
	 * 
	 * @return List of resources
	 */
	public List<String> getResources() {
		return resources;
	}

	/**
	 * Adds resource to manifest.
	 * 
	 * @param resource the resource string representation
	 */
	public void addResource(final String resource) {
		resources.add(resource);
	}
}
