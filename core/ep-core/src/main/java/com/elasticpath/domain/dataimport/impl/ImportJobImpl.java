/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportMapping;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * A default implementation of <code>ImportJob</code>.
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
@Entity
@Table(name = ImportJobImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ImportJobImpl extends AbstractEntityImpl implements ImportJob {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TIMPORTJOB";

	private String name;

	private String csvFileName;

	private String importDataTypeName;

	private ImportType importType;

	private int importTypeId;

	private int maxAllowErrors;

	private Map<String, Integer> mappings;

	private Map<String, ImportMapping> metaMappings;

	private long uidPk;

	private Catalog catalog;

	private Store store;

	private Warehouse warehouse;
	
	private String dependentObjectGuid;
	
	private static final char DEFAULT_TEXT_QUALIFIER = '"';
	
	private static final char DEFAULT_COLUMN_DELIMITER = ' ';

	private char colDelimeter = DEFAULT_COLUMN_DELIMITER;

	private char textQualifier = DEFAULT_TEXT_QUALIFIER;

	private String guid;

	/**
	 * Returns the name of import job.
	 * 
	 * @return the name of the import job.
	 */
	@Basic
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name of import job.
	 * 
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Returns the import csv data file name.
	 * 
	 * @return the import data file name.
	 */
	@Basic
	@Column(name = "CSV_FILE_NAME", nullable = false)
	public String getCsvFileName() {
		return this.csvFileName;
	}

	/**
	 * Set the csv file name.
	 * 
	 * @param csvFileName the csv file name
	 */
	public void setCsvFileName(final String csvFileName) {
		this.csvFileName = csvFileName;
	}

	/**
	 * Returns the import csv data file column delimeter.
	 * 
	 * @return the import data file column delimeter.
	 */
    @Persistent
	@Column(name = "COL_DELIMETER", length = 1)
    @Externalizer("com.elasticpath.persistence.Char2StringConverter.char2String")
    @Factory("com.elasticpath.persistence.Char2StringConverter.string2CharForColumnDelimiter")
	public char getCsvFileColDelimeter() {
		return this.colDelimeter;
	}

	/**
	 * Set the csv file column delimeter.
	 * 
	 * @param colDelimeter the csv file column delimeter
	 */
	public void setCsvFileColDelimeter(final char colDelimeter) {
		this.colDelimeter = colDelimeter;
	}

	/**
	 * Returns the import csv data file text qualifier.
	 * 
	 * @return the import data file text qualifier.
	 */
    @Persistent
	@Column(name = "TEXT_QUALIFIER", length = 1)
    @Externalizer("com.elasticpath.persistence.Char2StringConverter.char2String")
    @Factory("com.elasticpath.persistence.Char2StringConverter.string2CharForTextQualifier")
	public char getCsvFileTextQualifier() {
		return this.textQualifier;
	}

	/**
	 * Set the csv file text qualifier.
	 * 
	 * @param textQualifier the text qualifier
	 */
	public void setCsvFileTextQualifier(final char textQualifier) {
		this.textQualifier = textQualifier;
	}

	/**
	 * Returns the import data type.
	 * 
	 * @return the import data type.
	 */
	@Basic
	@Column(name = "DATA_TYPE_NAME", nullable = false)
	public String getImportDataTypeName() {
		return this.importDataTypeName;
	}

	/**
	 * Set the import data type name.
	 * 
	 * @param importDataTypeName the import data type name
	 */
	public void setImportDataTypeName(final String importDataTypeName) {
		this.importDataTypeName = importDataTypeName;
	}

	/**
	 * Returns the import type.
	 * 
	 * @return the import type
	 */
	@Transient
	public ImportType getImportType() {
		return this.importType;
	}

	/**
	 * Set the import type.
	 * 
	 * @param importType the import type to set.
	 */
	public void setImportType(final ImportType importType) {
		this.importType = importType;
		this.setImportTypeId(importType.getTypeId());
	}

	/**
	 * Returns the maximum allowable errors before teminating import.
	 * 
	 * @return the maximum allowable errors before teminate import
	 */
	@Basic
	@Column(name = "MAX_ALLOW_ERRORS", nullable = false)
	public int getMaxAllowErrors() {
		return this.maxAllowErrors;
	}

	/**
	 * Set the maximum allowable errors before teminating import.
	 * 
	 * @param maxAllowErrors the maximum allowable errors
	 */
	public void setMaxAllowErrors(final int maxAllowErrors) {
		this.maxAllowErrors = maxAllowErrors;
	}

	/**
	 * Set the mappings between import data type fields and csv file columns. Import data type field name will be the key and the csv file column
	 * number will be the value.
	 * 
	 * @param mappings the mappings to set
	 */
	public void setMappings(final Map<String, Integer> mappings) {
		this.mappings = mappings;

		// Remove deleted mappings
		for (Iterator<Map.Entry<String, ImportMapping>> i = getMetaMappings().entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, ImportMapping> entry = i.next();
			if (!this.mappings.containsKey(entry.getKey())) {
				i.remove();
			}
		}

		// Update/Insert mappings
		ImportMapping importMapping;
		for (Map.Entry<String, Integer> entry : this.mappings.entrySet()) {
			if (getMetaMappings().containsKey(entry.getKey())) {
				importMapping = getMetaMappings().get(entry.getKey());
			} else {
				importMapping = new ImportMappingImpl();
				importMapping.setName(entry.getKey());
			}
			if (!entry.getValue().equals(importMapping.getColNumber())) {
				importMapping.setColNumber(entry.getValue());
				getMetaMappings().put(entry.getKey(), importMapping);
			}
		}

	}

	/**
	 * Returns the mappings between import data type fields and csv file columns. Import data type field name will be the key and the csv file column
	 * number will be the value.
	 * 
	 * @return the mappings between import data type fields and csv file columns
	 */
	@Transient
	public Map<String, Integer> getMappings() {
		return this.mappings;
	}

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		super.initialize();
		if (this.importType == null) {
			this.importType = AbstractImportTypeImpl.INSERT_UPDATE_TYPE;
			this.setImportTypeId(this.importType.getTypeId());
		}
	}

	/**
	 * Returns the import type id.
	 * 
	 * @return the import type id
	 */
	@Basic
	@Column(name = "IMPORT_TYPE", nullable = false)
	public int getImportTypeId() {
		return importTypeId;
	}

	/**
	 * Sets the import type id.
	 * 
	 * @param importTypeId the import type id
	 */
	public void setImportTypeId(final int importTypeId) {
		this.importTypeId = importTypeId;
		this.importType = AbstractImportTypeImpl.getInstance(importTypeId);
	}

	/**
	 * Returns the meta mappings.
	 * 
	 * @return the meta mappings
	 */
	@OneToMany(targetEntity = ImportMappingImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "name")
	@ElementJoinColumn(name = "IMPORT_JOB_UID", nullable = false)
	@ElementForeignKey(name = "TIMPORTMAPPINGS_IBFK_1")
	@ElementDependent
	public Map<String, ImportMapping> getMetaMappings() {
		if (metaMappings == null) {
			metaMappings = new HashMap<String, ImportMapping>();
		}		
		return metaMappings;
	}

	/**
	 * Sets the meta mappings.
	 * 
	 * @param metaMappings the meta mappings
	 */
	public void setMetaMappings(final Map<String, ImportMapping> metaMappings) {
		this.metaMappings = metaMappings;
		if (metaMappings != null) {
			this.mappings = new TreeMap<String, Integer>();
			for (final Map.Entry<String, ImportMapping> entry : metaMappings.entrySet()) {
				final ImportMapping importMapping = entry.getValue();
				this.mappings.put(importMapping.getName(), importMapping.getColNumber());
			}
		}
	}

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID", nullable = false, length = GUID_LENGTH)
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Get the <code>Catalog</code> for this import job.
	 * 
	 * @return the <code>Catalog</code> that the import data belongs to.
	 */
	@ManyToOne(targetEntity = CatalogImpl.class, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "CATALOG_UID")
	public Catalog getCatalog() {
		return this.catalog;
	}

	/**
	 * Set the <code>Catalog</code> for this import job.
	 * 
	 * @param catalog - the <code>Catalog</code> to import data into.
	 */
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Returns the warehouse of import job.
	 * 
	 * @return the warehouse of the import job.
	 */
	@ManyToOne(targetEntity = WarehouseImpl.class, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "WAREHOUSE_UID")
	public Warehouse getWarehouse() {
		return warehouse;
	}

	/**
	 * Set the warehouse for import job.
	 * 
	 * @param warehouse the warehouse
	 */
	public void setWarehouse(final Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	/**
	 * Get the <code>Store</code> for this import job.
	 * 
	 * @return the <code>Store</code> that the import data belongs to.
	 */
	@ManyToOne(targetEntity = StoreImpl.class, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "STORE_UID")
	public Store getStore() {
		return this.store;
	}

	/**
	 * Set the <code>Store</code> for this import job.
	 * 
	 * @param store - the <code>Store</code> to import data into.
	 */
	public void setStore(final Store store) {
		this.store = store;
	}

	/**
	 * @return the GUID of the dependent object into which data is to be imported (if applicable)
	 */
	@Basic
	@Column(name = "DEPENDENT_OBJ_GUID", nullable = true, length = GUID_LENGTH)
	public String getDependentPriceListGuid() {
		return this.dependentObjectGuid;
	}
	
	/**
	 * Sets the GUID of the dependent object into which data is to be imported (if applicable).
	 * @param dependentObjectGuid the GUID
	 */
	public void setDependentPriceListGuid(final String dependentObjectGuid) {
		this.dependentObjectGuid = dependentObjectGuid;
	}
	
	/**
	 * Returns a string representation of this object.
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImportJob[Name: ");
		builder.append(getName());
		builder.append(", Type ID: " + getImportTypeId());
		builder.append(", CSV File: " + getCsvFileName());
		builder.append(", Delimiters (col, text): " + getCsvFileColDelimeter() + ", " + getCsvFileTextQualifier());

		builder.append(", Import Type: " + getImportType());
		builder.append("]");
		
		return builder.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ImportJobImpl)) {
			return false;
		}
		return super.equals(obj);
	}

	@Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
	public int hashCode() {
		return super.hashCode();
	}

	
}
