/**
 * 
 */
package com.elasticpath.csvimport;

import com.elasticpath.domain.dataimport.ImportDataType;

/**
 * <p>Interface for {@code ImportDataType}s that represent DTO objects, which allows the
 * ImportDataType to actually return a prototype bean of the data type which is being imported
 * so that it can be populated by an {@code CsvImportFieldObjectMapper}. This interface was 
 * created to extend the ImportDataType interface when an enhanced, modular CSV import structure 
 * was designed.</p>
 * <p>The original CSV import architecture 
 * relied on the ImportDataType implementations to perform many duties, including:
 * defining {@code ImportField}s, defining the Spring identifier of the bean that would run import
 * jobs for the ImportDataType being described, running the logic associated with deleting, 
 * creating, and saving the data type (in some cases). Instantiation of a new prototype bean
 * was left to a special implementation of {@code ImportJobRunner} for that bean.</p>
 * <p>This enhanced implementation now has a more generic {@code ImportJobRunner} but each
 * bean being imported specifies its own {@code CsvImportFieldDtoMapper}, which is responsible for
 * mapping CSV rows (string arrays) into object prototype beans which are assumed to be simple
 * DTO objects. However, it has a reference to an ImportDataType but needs to instantiate the prototype 
 * bean that is to be populated from the CSV row. Since that bean cannot be obtained by Spring injection 
 * (ImportService already retrieves it from the {@code PrototypeBeanFactory} then it must be provided 
 * by the ImportDataType.</p>
 * 
 * @param <T> the type of the bean being imported
 */
public interface DtoImportDataType<T> extends ImportDataType {

	/**
	 * Gets an instance of the bean that is to be populated upon import.
	 * @return the prototype bean instance
	 */
	T getPrototypeDtoBean();
	
	/**
	 * Validate the bean as a whole after all fields are populated.
	 * @param dto the dto to populate
	 * @return the validated bean
	 * throws EpBindException if the bean cannot is not valid
	 */
	T validatePopulatedDtoBean(final T dto);
}
