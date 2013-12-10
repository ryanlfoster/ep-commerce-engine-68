package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.importexport.common.dto.catalogs.AttributeDTO;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.CatalogService;

/**
 * This class is responsible for exporting attributes.
 */
public class AttributeDependentExporterImpl extends AbstractDependentExporterImpl<Attribute, AttributeDTO, CatalogDTO> {
	private AttributeService attributeService;
	private CatalogService catalogService;

	/** {@inheritDoc} */
	public List<Attribute> findDependentObjects(final long primaryObjectUid) {
		if (!catalogService.getCatalog(primaryObjectUid).isMaster()) {
			return Collections.emptyList();
		}
		if (getFilter().isFiltered(primaryObjectUid)) {
			return getByCatalog(primaryObjectUid);
		}

		List<Attribute> resultList = new ArrayList<Attribute>();

		Iterator<Long> iter = getContext().getDependencyRegistry().getDependentUids(Attribute.class).iterator();
		while (iter.hasNext()) {
			final Long uid = iter.next();
			Attribute attribute = attributeService.get(uid);
			if (attribute.getCatalog() != null && attribute.getCatalog().getUidPk() == primaryObjectUid || attribute.isGlobal()) {
				resultList.add(attribute);
				iter.remove();
			}
		}
		return resultList;
	}

	/**
	 * {@inheritDoc}
	 */
	public void bindWithPrimaryObject(final List<AttributeDTO> dependentDtoObjects, final CatalogDTO primaryDtoObject) {
		primaryDtoObject.setAttributes(dependentDtoObjects);
	}

	/**
	 * Returns the list of attributes that should be exported.
	 * 
	 * @param catalogUid The uid of the catalog.
	 * @return The list.
	 */
	List<Attribute> getByCatalog(final Long catalogUid) {
		List<Attribute> attributeList = new ArrayList<Attribute>(attributeService.findAllCatalogOrGlobalAttributes(catalogUid));
		List<Attribute> attributeResultList = new ArrayList<Attribute>();
		for (Attribute attribute : attributeList) {
			// there is no method to return only attributes for given catalog so we must filter the obtained
			// attributeList
			// The catalog is null for new global attributes
			if ((attribute.getCatalog() != null && attribute.getCatalog().getUidPk() == catalogUid) || attribute.getCatalog() == null) {
				attributeResultList.add(attribute);
			}
		}
		return attributeResultList;
	}

	public void setAttributeService(final AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}
}