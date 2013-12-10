package com.elasticpath.importexport.importer.configuration;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Dependent element list configuration represents list of dependent elements.
 * Class is designed for JAXB and emulates the set of UI controls.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class DependentElementListConfiguartion {
	
	@XmlElement(name = "dependentelement", required = false, defaultValue = "CLEAR_COLLECTION")
	private List<DependentElementConfiguration> dependentElemenList;
	
	/**
	 * Gets the dependent element list.
	 * 
	 * @return the dependentElemenList
	 */
	public List<DependentElementConfiguration> getDependentElemenList() {
		if (dependentElemenList == null) {
			return Collections.emptyList();
		}
		return dependentElemenList;
	}

	/**
	 * Sets the dependent element list.
	 * 
	 * @param dependentElemenList the dependentElemenList to set
	 */
	public void setDependentElemenList(final List<DependentElementConfiguration> dependentElemenList) {
		this.dependentElemenList = dependentElemenList;
	}

}
