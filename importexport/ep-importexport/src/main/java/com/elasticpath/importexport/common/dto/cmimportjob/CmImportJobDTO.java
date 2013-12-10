package com.elasticpath.importexport.common.dto.cmimportjob;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * JAXB DTO for ImportJob (and their mappings).
 */
@XmlRootElement(name = CmImportJobDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })

public class CmImportJobDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/** Root XML element name. */
	public static final String ROOT_ELEMENT = "cmimportjob";
	
	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "name", required = true)
	private String name;
	
	@XmlElement(name = "csv_file_name", required = true)
	private String csvFileName;
	
	@XmlElement(name = "col_delimeter")
	private String colDelimeter;
	
	@XmlElement(name = "text_qualifier")
	private String textQualifier;

	@XmlElement(name = "data_type_name", required = true)
	private String importDataTypeName;
	
	@XmlElement(name = "dependent_price_list_guid")
	private String dependentObjectGuid;
	
	@XmlElement(name = "import_type_id", required = true)
	private int importTypeId;
	
	@XmlElement(name = "max_allow_errors", required = true)
	private int maxAllowErrors;
	
	@XmlElement(name = "catalogue_guid")
	private String catalogueGuid;
	
	@XmlElement(name = "store_guid")
	private String storeGuid;
	
	@XmlElement(name = "warehouse_guid")
	private String warehouseGuid;
	
	@XmlElementWrapper(name = "mappings")
	@XmlElement(name = "mapping")
	private Set<CmImportMappingDTO> mappings = new HashSet<CmImportMappingDTO>();

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getCsvFileName() {
		return csvFileName;
	}

	public void setCsvFileName(final String csvFileName) {
		this.csvFileName = csvFileName;
	}

	public String getColDelimeter() {
		return colDelimeter;
	}

	public void setColDelimeter(final String colDelimeter) {
		this.colDelimeter = colDelimeter;
	}
	
	public String getTextQualifier() {
		return textQualifier;
	}

	public void setTextQualifier(final String textQualifier) {
		this.textQualifier = textQualifier;
	}

	public String getDataTypeName() {
		return importDataTypeName;
	}

	public void setDataTypeName(final String dataTypeName) {
		this.importDataTypeName = dataTypeName;
	}

	public String getDependentObjGuid() {
		return dependentObjectGuid;
	}

	public void setDependentObjGuid(final String dependentObjGuid) {
		this.dependentObjectGuid = dependentObjGuid;
	}
	
	public int getImportTypeId() {
		return importTypeId;
	}

	public void setImportType(final int importTypeId) {
		this.importTypeId = importTypeId;
	}

	public int getMaxAllowErrors() {
		return maxAllowErrors;
	}

	public void setMaxAllowErrors(final int maxAllowErrors) {
		this.maxAllowErrors = maxAllowErrors;
	}

	public String getCatalogueGuid() {
		return catalogueGuid;
	}

	public void setCatalogueGuid(final String catalogueGuid) {
		this.catalogueGuid = catalogueGuid;
	}
	
	public String getStoreGuid() {
		return storeGuid;
	}

	public void setStoreGuid(final String storeGuid) {
		this.storeGuid = storeGuid;
	}

	public String getWarehouseGuid() {
		return warehouseGuid;
	}

	public void setWarehouseGuid(final String warehouseGuid) {
		this.warehouseGuid = warehouseGuid;
	}

	public Set<CmImportMappingDTO> getImportMappingDto() {
		return mappings;
	}

	public void setImportMappingDto(final Set<CmImportMappingDTO> importMappingDto) {
		this.mappings = importMappingDto;
	}

}
