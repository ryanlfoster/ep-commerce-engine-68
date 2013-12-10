package com.elasticpath.domain.dataimport;

import java.util.Map;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.persistence.api.Entity;

/**
 * Represents a import job.
 */
public interface ImportJob extends Entity {
	
	/**
	 * Returns the name of import job.
	 * 
	 * @return the name of the import job.
	 */
	String getName();

	/**
	 * Set the name of import job.
	 * 
	 * @param name the name to set
	 */
	void setName(String name);
	
	/**
	 * Returns the import csv data file name.
	 * 
	 * @return the import data file name.
	 */
	String getCsvFileName();

	/**
	 * Set the csv file name.
	 * 
	 * @param csvFileName the csv file name
	 */
	void setCsvFileName(String csvFileName);

	/**
	 * Returns the import csv data file column delimeter.
	 * 
	 * @return the import data file column delimeter.
	 */
	char getCsvFileColDelimeter();

	/**
	 * Set the csv file column delimeter.
	 * 
	 * @param colDelimeter the csv file column delimeter
	 */
	void setCsvFileColDelimeter(char colDelimeter);

	/**
	 * Returns the import csv data file text qualifier.
	 * 
	 * @return the import data file text qualifier.
	 */
	char getCsvFileTextQualifier();

	/**
	 * Set the csv file text qualifier.
	 * 
	 * @param textQualifier the text qualifier
	 */
	void setCsvFileTextQualifier(char textQualifier);

	/**
	 * Returns the import data type name.
	 * 
	 * @return the import data type name
	 */
	String getImportDataTypeName();

	/**
	 * Set the import data type name.
	 * 
	 * @param importDataTypeName the import data type name
	 */
	void setImportDataTypeName(String importDataTypeName);

	/**
	 * Returns the import type.
	 * 
	 * @return the import type
	 */
	ImportType getImportType();

	/**
	 * Set the import type.
	 * 
	 * @param importType the import type to set.
	 */
	void setImportType(ImportType importType);

	/**
	 * Returns the maximum allowable errors before teminating import.
	 * 
	 * @return the maximum allowable errors before teminate import
	 */
	int getMaxAllowErrors();

	/**
	 * Set the maximum allowable errors before teminating import.
	 * 
	 * @param maxAllowErrors the maximum allowable errors
	 */
	void setMaxAllowErrors(int maxAllowErrors);

	/**
	 * Set the mappings between import data type fields and csv file columns. Import data type field name will be the key and the csv file column
	 * number will be the value.
	 * 
	 * @param mappings the mappings to set
	 */
	void setMappings(Map<String, Integer> mappings);

	/**
	 * Returns the mappings between import data type fields and csv file columns. Import data type field name will be the key and the csv file column
	 * number will be the value.
	 * 
	 * @return the mappings between import data type fields and csv file columns
	 */
	Map<String, Integer> getMappings();

	/**
	 * Get the <code>Catalog</code> for this import job.
	 * 
	 * @return the <code>Catalog</code> that the import data belongs to.
	 */
	Catalog getCatalog();

	/**
	 * Set the <code>Catalog</code> for this import job.
	 * 
	 * @param catalog - the <code>Catalog</code> to import data into.
	 */
	void setCatalog(final Catalog catalog);
	
	/**
	 * Get the <code>Store</code> for this import job.
	 * 
	 * @return the <code>Store</code> that the import data belongs to.
	 */
	Store getStore();

	/**
	 * Set the <code>Store</code> for this import job.
	 * 
	 * @param store - the <code>Store</code> to import data into.
	 */
	void setStore(final Store store);
	
	/**
	 * Returns the warehouse of import job.
	 *
	 * @return the warehouse of the import job.
	 */
	Warehouse getWarehouse();

	/**
	 * Set the warehouse for import job.
	 *
	 * @param warehouse the warehouse
	 */
	void setWarehouse(Warehouse warehouse);
	
	/**
	 * @return the GUID of the PriceListDescriptor into which data is to be imported (if applicable)
	 */
	String getDependentPriceListGuid();
	
	/**
	 * Sets the GUID of the PriceListDescriptor into which data is to be imported (if applicable).
	 * @param priceListDescriptorGuid the GUID
	 */
	void setDependentPriceListGuid(String priceListDescriptorGuid);
}
