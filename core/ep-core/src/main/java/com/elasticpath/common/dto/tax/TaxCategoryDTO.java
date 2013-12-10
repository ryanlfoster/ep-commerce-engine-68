package com.elasticpath.common.dto.tax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.common.dto.PropertyDTO;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;

/**
 * JAXB DTO for Tax Categories.
 */
@XmlRootElement(name = "category")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class TaxCategoryDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "fieldMatch", required = true)
	private TaxCategoryTypeEnum fieldMatchType;

	@XmlElementWrapper(name = "localized_properties")
	@XmlElement(name = "localized_property")
	private List<PropertyDTO> localizedProperties = new ArrayList<PropertyDTO>();

	@XmlElementWrapper(name = "regions")
	@XmlElement(name = "region")
	private final Set<TaxRegionDTO> regions = new HashSet<TaxRegionDTO>();

	public String getName() {
		return name;
	}

	public TaxCategoryTypeEnum getFieldMatchType() {
		return fieldMatchType;
	}

	public List<PropertyDTO> getLocalizedProperties() {
		return localizedProperties;
	}

	public Set<TaxRegionDTO> getRegions() {
		return regions;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setFieldMatchType(final TaxCategoryTypeEnum fieldMatch) {
		this.fieldMatchType = fieldMatch;
	}

	public void setLocaliedProperties(final List<PropertyDTO> localizedProperties) {
		this.localizedProperties = localizedProperties;
	}

}
