package com.elasticpath.importexport.exporter.exporters;

import java.util.List;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.persistence.api.Persistable;

/**
 * This class is responsible for support processing export dependent objects during export operation.
 * 
 * @param <DOMAIN> the dependent domain object that should be exported
 * @param <DTO> the dto object that is corresponded to {@code DOMAIN} object
 * @param <PARENT> parent {@link Dto} which this exporter is dependent on
 */
public interface DependentExporter<DOMAIN extends Persistable, DTO extends Dto, PARENT extends Dto> {
	/**
	 * Finds the dependent objects by primary object uid. 
	 * 
	 * @param primaryObjectUid the primary object uid
	 * @return the list of dependent objects
	 */
	List<DOMAIN> findDependentObjects(long primaryObjectUid);

	/**
	 * Gets the domain adapter for dependent object type.
	 * 
	 * @return the appropriate domain adapter
	 */
	DomainAdapter<DOMAIN, DTO> getDomainAdapter();

	/**
	 * Initializes the exporter with the given {@link ExportContext}.
	 * 
	 * @param context {@link ExportContext} to use for exporting
	 * @param filter {@link DependentExporterFilter} which can be used for filtering
	 * @throws ConfigurationException in case of errors
	 */
	void initialize(ExportContext context, DependentExporterFilter filter) throws ConfigurationException;

	/**
	 * Binds the list of dependent dto objects with the primary dto object.
	 * 
	 * @param dependentDtoObjects the list of dependent dto objects
	 * @param primaryDtoObject the primary dto object
	 */
	void bindWithPrimaryObject(List<DTO> dependentDtoObjects, PARENT primaryDtoObject);
}
