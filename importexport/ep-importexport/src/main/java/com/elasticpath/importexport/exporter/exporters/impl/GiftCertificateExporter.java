package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.payment.GiftCertificateTransaction;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.GiftCertificateService;

/**
 * Exporter for {@link com.elasticpath.domain.catalog.GiftCertificate} domain objects.
 */
public class GiftCertificateExporter  extends AbstractExporterImpl<GiftCertificate, GiftCertificateDTO, String> {
	
	private static final Logger LOG = Logger.getLogger(StoreExporter.class);
	
	private List<String> giftCertificateGuids;
	
	private ImportExportSearcher importExportSearcher;
	
	private GiftCertificateService giftCertificateService;
	
	private DomainAdapter<GiftCertificate, GiftCertificateDTO> giftCertificateAdapter;
	
	public JobType getJobType() {
		return JobType.GIFT_CERTIFICATE;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { GiftCertificate.class, GiftCertificateTransaction.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		giftCertificateGuids = new ArrayList<String>();
		giftCertificateGuids.addAll(
				getImportExportSearcher().searchGuids(
						getContext().getSearchConfiguration(),
						EPQueryType.GIFT_CERTIFICATE));
		
		LOG.info("The list for " + giftCertificateGuids.size() + " gift certificates retrieved from the database.");	
	}

	@Override
	protected Class<? extends GiftCertificateDTO> getDtoClass() {
		return GiftCertificateDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(GiftCertificate.class)) {
			giftCertificateGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(GiftCertificate.class));
		}

		return new ArrayList<String>(giftCertificateGuids);
	}

	@Override
	protected List<GiftCertificate> findByIDs(final List<String> subList) {
		Set <GiftCertificate> giftCertificates = new HashSet<GiftCertificate>();
		
		for (String giftCertificateGuid : subList) {
			GiftCertificate giftCertificate = findByGiftCertificateGuid(giftCertificateGuid);
			giftCertificates.add(giftCertificate);
		}
		
		return new ArrayList<GiftCertificate>(giftCertificates);
	}


	@Override
	protected DomainAdapter<GiftCertificate, GiftCertificateDTO> getDomainAdapter() {
		return giftCertificateAdapter;
	}

	/**
	 * Returns the {@link com.elasticpath.domain.catalog.GiftCertificate} with the given code.
	 * @param giftCertificateGuid the requested code.
	 * @return the {@link com.elasticpath.domain.catalog.GiftCertificate} requested.
	 * @throws EpServiceException if not found.
	 */
	protected GiftCertificate findByGiftCertificateGuid(final String giftCertificateGuid) {
		GiftCertificate giftCertificate = giftCertificateService.findByGuid(giftCertificateGuid);

		if (giftCertificate == null) {
			throw new EpServiceException("Gift certificate with code " + giftCertificateGuid + " not found.");
		}
		
		return giftCertificate;
	}
	
	/**
	 * @return the giftCertificateService
	 */
	protected GiftCertificateService getGiftCertificateService() {
		return giftCertificateService;
	}

	/**
	 * @param giftCertificateService the giftCertificateService to set
	 */
	public void setGiftCertificateService(final GiftCertificateService giftCertificateService) {
		this.giftCertificateService = giftCertificateService;
	}

	/**
	 * @return the giftCertificateAdapter
	 */
	protected DomainAdapter<GiftCertificate, GiftCertificateDTO> getGiftCertificateAdapter() {
		return giftCertificateAdapter;
	}

	/**
	 * @param giftCertificateAdapter the giftCertificateAdapter to set
	 */
	public void setGiftCertificateAdapter(final	DomainAdapter<GiftCertificate, GiftCertificateDTO> giftCertificateAdapter) {
		this.giftCertificateAdapter = giftCertificateAdapter;
	}
	
	private ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}
	
	/**
	 * @param importExportSearcher The ImportExportSearcher to use.
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}
}
