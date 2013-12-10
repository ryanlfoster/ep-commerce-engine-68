package com.elasticpath.importexport.common.dto.cmimportjob;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.common.dto.Dto;

/**
 * Contains mapping between XML and TagCondition domain object. Designed for JAXB.
 */
@XmlRootElement(name = CmImportMappingDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class CmImportMappingDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "mapping";
	
	@XmlElement(name = "col_number", required = true)
	private int colNumber;

	@XmlElement(name = "import_field_name", required = true)
	private String importFieldName;

	public int getColNumber() {
		return colNumber;
	}

	public void setColNumber(final int colNumber) {
		this.colNumber = colNumber;
	}

	public String getImportFieldName() {
		return importFieldName;
	}

	public void setImportFieldName(final String importFieldName) {
		this.importFieldName = importFieldName;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ObjectUtils.hashCode(colNumber); 
		result = prime * result + ObjectUtils.hashCode(importFieldName); 
		
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		CmImportMappingDTO other = (CmImportMappingDTO) obj;
		
		return ObjectUtils.equals(colNumber, other.colNumber) 
			&& ObjectUtils.equals(importFieldName, other.importFieldName);
	}

}
