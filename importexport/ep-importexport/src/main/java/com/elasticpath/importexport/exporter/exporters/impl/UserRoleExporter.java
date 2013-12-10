package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.cmuser.UserRoleDTO;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.cmuser.UserRoleService;

/**
 * Exports {@link UserRole} objects.
 */
public class UserRoleExporter extends AbstractExporterImpl<UserRole, UserRoleDTO, String> {
	
	private static final Logger LOG = Logger.getLogger(UserRoleExporter.class);
	private ImportExportSearcher importExportSearcher;
	private DomainAdapter<UserRole, UserRoleDTO> userRoleAdapter;
	private List<String> userRoleGuids;
	private UserRoleService userRoleService;
	
	public JobType getJobType() {
		return JobType.USER_ROLE;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { UserRole.class };
	}
	
	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		userRoleGuids = new ArrayList<String>();
		userRoleGuids.addAll(
				getImportExportSearcher().searchGuids(
							getContext().getSearchConfiguration(), EPQueryType.USER_ROLE));
		
		LOG.info("The list for " + userRoleGuids.size() + " UserRoles retrieved from the database.");
	}

	@Override
	protected Class<? extends UserRoleDTO> getDtoClass() {
		return UserRoleDTO.class;
	}
	
	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(UserRole.class)) {
			userRoleGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(UserRole.class));
		}
		return userRoleGuids;
	}
	
	
	@Override
	protected List<UserRole> findByIDs(final List<String> userRoleGuidList) {
		ArrayList<UserRole> retrievedUserRoles = new ArrayList<UserRole>();

		FetchGroupLoadTunerImpl loadTunerAll = new FetchGroupLoadTunerImpl();
		loadTunerAll.addFetchGroup(FetchGroupConstants.ALL);
		
		for (String currentUserRoleGuid : userRoleGuidList) {
			UserRole currentUserRole = userRoleService.findByGuid(currentUserRoleGuid);
			retrievedUserRoles.add(currentUserRole);
		}

		return retrievedUserRoles;
	}
	
	@Override
	protected DomainAdapter<UserRole, UserRoleDTO> getDomainAdapter() {
		return userRoleAdapter;
	}	
	
	/**
	 * @param userRoleAdapter the userRoleAdapter to set
	 */
	public void setUserRoleAdapter(final DomainAdapter<UserRole, UserRoleDTO> userRoleAdapter) {
		this.userRoleAdapter = userRoleAdapter;
	}

	/**
	 * @return the userRoleService
	 */
	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

	/**
	 * @param userRoleService the userRoleService to set
	 */
	public void setUserRoleService(final UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}
	
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

}