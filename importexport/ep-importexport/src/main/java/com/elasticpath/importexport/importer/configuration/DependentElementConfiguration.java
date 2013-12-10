package com.elasticpath.importexport.importer.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;

/**
 * Dependent element configuration represents holder of elements with collection strategies.
 * Class is designed for JAXB and emulates the set of UI controls.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class DependentElementConfiguration {
	
	@XmlAttribute(name = "type", required = true)
	private DependentElementType dependentElementType;
	
	@XmlValue
	private CollectionStrategyType collectionStrategyType = CollectionStrategyType.CLEAR_COLLECTION;

	/**
	 * Gets the dependent element type.
	 * 
	 * @return the dependentElementType
	 */
	public DependentElementType getDependentElementType() {
		return dependentElementType;
	}

	/**
	 * Sets the dependent element type.
	 * 
	 * @param dependentElementType the dependentElementType to set
	 */
	public void setDependentElementType(final DependentElementType dependentElementType) {
		this.dependentElementType = dependentElementType;
	}

	/**
	 * Gets the collection strategy type.
	 * 
	 * @return the collectionStrategyType
	 */
	public CollectionStrategyType getCollectionStrategyType() {
		return collectionStrategyType;
	}

	/**
	 * Sets the collection strategy type.
	 * 
	 * @param collectionStrategyType the collectionStrategyType to set
	 */
	public void setCollectionStrategyType(final CollectionStrategyType collectionStrategyType) {
		this.collectionStrategyType = collectionStrategyType;
	}
}
