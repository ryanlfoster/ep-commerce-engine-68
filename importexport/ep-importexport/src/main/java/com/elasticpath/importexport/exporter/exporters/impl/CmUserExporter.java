package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.cmuser.CmUserDTO;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserPasswordHistoryItem;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Implements the methods required to export {@link CmUser} objects.
 */
public class CmUserExporter extends AbstractExporterImpl<CmUser, CmUserDTO, String> {

	private static final Logger LOG = Logger.getLogger(CmUserExporter.class);

	private CmUserService cmUserService;
	
	private ImportExportSearcher importExportSearcher;
	
	private List<String> cmUserGuids;

	private DomainAdapter<CmUser, CmUserDTO> cmUserAdapter;
	
	public JobType getJobType() {
		return JobType.CMUSER;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { CmUser.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		cmUserGuids = new ArrayList<String>();
		cmUserGuids.addAll(getImportExportSearcher().searchGuids(getContext().getSearchConfiguration(), 
				EPQueryType.CMUSER));
		
		LOG.info("Found " + cmUserGuids.size() + " CmUser objects for export.");		
	}	

	@Override
	protected Class<? extends CmUserDTO> getDtoClass() {
		return CmUserDTO.class;
	}
	
	@Override
	protected List<String> getListExportableIDs() {

		if (getContext().getDependencyRegistry().supportsDependency(CmUser.class)) {
			cmUserGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(CmUser.class));
		}
		return cmUserGuids;
	}	
	
	@Override
	protected List<CmUser> findByIDs(final List<String> cmUserGuidList) {
		ArrayList<CmUser> cmUsers = new ArrayList<CmUser>();

		FetchGroupLoadTunerImpl loadtunerAll = new FetchGroupLoadTunerImpl();
		loadtunerAll.addFetchGroup(FetchGroupConstants.ALL);
		
		for (String cmUserGuid : cmUserGuidList) {
			CmUser currentCmUser = cmUserService.findByGuid(cmUserGuid, loadtunerAll);
			cmUsers.add(currentCmUser);
		}

		return cmUsers;
	}	
	
	@Override
	protected void addDependencies(final List<CmUser> cmUsers, final DependencyRegistry dependencyRegistry) {

		for (CmUser cmUser : cmUsers) {

			if (dependencyRegistry.supportsDependency(UserRole.class)) {
				for (UserRole userRole : cmUser.getUserRoles()) {
					dependencyRegistry.addGuidDependency(UserRole.class, userRole.getGuid());
				}
			}
			
			if (dependencyRegistry.supportsDependency(Store.class)) {
				for (Store store : cmUser.getStores()) {
					dependencyRegistry.addUidDependency(Store.class, store.getUidPk());
				}
			}
			
			if (dependencyRegistry.supportsDependency(Warehouse.class)) {
				for (Warehouse warehouse : cmUser.getWarehouses()) {
					dependencyRegistry.addUidDependency(Warehouse.class, warehouse.getUidPk());
				}
			}

			if (dependencyRegistry.supportsDependency(Catalog.class)) {
				for (Catalog catalog : cmUser.getCatalogs()) {
					dependencyRegistry.addUidDependency(Catalog.class, catalog.getUidPk());
				}
			}

			if (dependencyRegistry.supportsDependency(UserPasswordHistoryItem.class)) {
				for (UserPasswordHistoryItem userPasswordHistoryItem : cmUser.getPasswordHistoryItems()) {
					dependencyRegistry.addUidDependency(UserPasswordHistoryItem.class, userPasswordHistoryItem.getUidPk());
				}
			}
		}
	}
	
	@Override
	protected DomainAdapter<CmUser, CmUserDTO> getDomainAdapter() {
		return cmUserAdapter;
	}	
	
	/**
	 * @param cmUserService the cmUserService to set
	 */
	public void setCmUserService(final CmUserService cmUserService) {
		this.cmUserService = cmUserService;
	}

	/**
	 * @return the cmUserService
	 */
	public CmUserService getCmUserService() {
		return cmUserService;
	}
	
	/**
	 * @param cmUserAdapter the cmUserAdapter to set
	 */
	public void setCmUserAdapter(final DomainAdapter<CmUser, CmUserDTO> cmUserAdapter) {
		this.cmUserAdapter = cmUserAdapter;
	}
	
	/**
	 * @return importExportSearcher
	 */
	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	/**
	 * Sets importexport searcher.
	 * 
	 * @param importExportSearcher importExportSearcher
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

}