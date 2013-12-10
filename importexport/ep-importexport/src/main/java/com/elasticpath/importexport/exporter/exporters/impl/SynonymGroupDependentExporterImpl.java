package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.SynonymGroupDTO;
import com.elasticpath.service.search.SynonymGroupService;

/**
 * This class is responsible for exporting {@link SynonymGroup}s.
 */
public class SynonymGroupDependentExporterImpl extends AbstractDependentExporterImpl<SynonymGroup, SynonymGroupDTO, CatalogDTO> {
	private SynonymGroupService sysnonymGroupService;

	/** {@inheritDoc} */
	public List<SynonymGroup> findDependentObjects(final long primaryObjectUid) {
		if (getFilter().isFiltered(primaryObjectUid)) {
			return getByCatalog(primaryObjectUid);
		}

		List<SynonymGroup> resultList = new ArrayList<SynonymGroup>();

		Iterator<Long> iter = getContext().getDependencyRegistry().getDependentUids(SynonymGroup.class).iterator();
		while (iter.hasNext()) {
			final Long uid = iter.next();
			SynonymGroup skuOption = (SynonymGroup) sysnonymGroupService.getObject(uid);
			if (skuOption.getCatalog().getUidPk() == primaryObjectUid) {
				resultList.add(skuOption);
				iter.remove();
			}
		}
		return resultList;
	}

	private List<SynonymGroup> getByCatalog(final long primaryObjectUid) {
		return new ArrayList<SynonymGroup>(sysnonymGroupService.findAllSynonymGroupForCatalog(primaryObjectUid));
	}

	/** {@inheritDoc} */
	public void bindWithPrimaryObject(final List<SynonymGroupDTO> dependentDtoObjects, final CatalogDTO primaryDtoObject) {
		primaryDtoObject.setSynonymGroups(dependentDtoObjects);
	}

	public void setSynonymGroupService(final SynonymGroupService synonymGroupService) {
		this.sysnonymGroupService = synonymGroupService;
	}
}