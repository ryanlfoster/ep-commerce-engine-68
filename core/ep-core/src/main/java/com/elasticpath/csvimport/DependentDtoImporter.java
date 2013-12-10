/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport;

import java.util.List;

import com.elasticpath.domain.dataimport.ImportBadRow;


/**
 * Imports DTOs that are dependent on some other objects in the system,
 * which can be identified by a GUID.
 * 
 * @param <T> the type of DTO in the ImportValidRow objects (e.g. BaseAmountDTO).
 * @param <V> a type of object, which may have an dependent information, eq master object GUID or more complex data 
 */
public interface DependentDtoImporter<T, V> {

	/**
	 * Imports the DTOs from the given valid import rows as dependencies of the object specified.
	 * @param validRows the valid rows containing the DTOs to import
	 * @param dependentInfo an additional info of the object on which the DTOs are dependent
	 * @return a list of bad rows, or an empty collection if none are found
	 */
	List<ImportBadRow> importDtos(final List<ImportValidRow<T>> validRows, final V dependentInfo);
}