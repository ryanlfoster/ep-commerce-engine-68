package com.elasticpath.importexport.exporter.exporters.impl;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.persistence.api.Persistable;

/**
 * This class is responsible for support processing export dependent objects during export operation.
 * 
 * @param <DOMAIN> the dependent domain object that should be exported
 * @param <DTO> the dto object that is corresponded to {@code DOMAIN} object
 * @param <PARENT> parent {@link Dto} this exporter is dependent upon
 * @deprecated this interface has moved, use {@link com.elasticpath.importexport.exporter.exporters.DependentExporter}
 *             instead
 */
@Deprecated
public interface DependentExporter<DOMAIN extends Persistable, DTO extends Dto, PARENT extends Dto> extends
		com.elasticpath.importexport.exporter.exporters.DependentExporter<DOMAIN, DTO, PARENT> {
}
