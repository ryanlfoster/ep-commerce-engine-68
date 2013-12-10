package com.elasticpath.importexport.common.dto.dynamiccontent;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.common.dto.Dto;

/**
 * DTO representation of a <code>ParameterValue</code> for use by import export. Used by <code>DynamicContentDTO<code>.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class ParameterValueDTO implements Dto {

	private static final long serialVersionUID = -413201214613775180L;

	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "parametername", required = true)
	private String parameterName;

	@XmlElement(name = "localizable", required = true)
	private boolean localizable;

	@XmlElement(name = "description")
	private String description;

	@XmlElementWrapper(name = "values")
	@XmlElement(name = "value")
	private List<DisplayValue> values;

	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * @param parameterName the parameterName to set
	 */
	public void setParameterName(final String parameterName) {
		this.parameterName = parameterName;
	}

	/**
	 * @param localizable the localizable to set
	 */
	public void setLocalizable(final boolean localizable) {
		this.localizable = localizable;
	}

	/**
	 * @return the localizable
	 */
	public boolean isLocalizable() {
		return localizable;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the values
	 */
	public List<DisplayValue> getValues() {
		if (values == null) {
			return Collections.emptyList();
		}
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(final List<DisplayValue> values) {
		this.values = values;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("parameterName", getParameterName())
			.append("guid", getGuid())
			.append("localizable", isLocalizable())
			.append("description", getDescription())
			.append("values", getValues())
			.toString();
	}
}
