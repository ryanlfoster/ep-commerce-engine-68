package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.Collections;
import java.util.List;

import com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO;
import com.elasticpath.common.dto.giftcertificate.GiftCertificateTransactionDTO;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.payment.GiftCertificateTransaction;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.payment.GiftCertificateTransactionService;

/**
 * This class is responsible for exporting {@link GiftCertificateTransaction}s.
 */
public class GiftCertificateTransactionDependentExporter extends
		AbstractDependentExporterImpl<GiftCertificateTransaction, GiftCertificateTransactionDTO, GiftCertificateDTO> {

	private GiftCertificateTransactionService giftCertificateTransactionService;
	
	private GiftCertificateService giftCertificateService;

	/**
	 * Returns objects dependent on the primary object.
	 * @param primaryObjectUid uid of the primary object.
	 * @return list of {@link com.elasticpath.domain.payment.GiftCertificateTransaction}s dependent on the primary object.
	 */
	public List<GiftCertificateTransaction> findDependentObjects(final long primaryObjectUid) {
		GiftCertificate giftCertificate = giftCertificateService.get(primaryObjectUid);
		
		if (giftCertificate == null) {
			throw new EpServiceException("No gift certificate found with uid: " + Long.toString(primaryObjectUid));
		}

		List<GiftCertificateTransaction> giftCertificateTransactions = 
				giftCertificateTransactionService.getGiftCertificateTransactions(giftCertificate);
		
		if (giftCertificateTransactions == null) {
			return Collections.emptyList();
		}
		
		return giftCertificateTransactions;
	}

	@Override
	public void bindWithPrimaryObject(final List<GiftCertificateTransactionDTO> dependentDtoObjects,
			final GiftCertificateDTO primaryDtoObject) {
		primaryDtoObject.setGiftCertificateTransactions(dependentDtoObjects);
	}

	/**
	 * @return the giftCertificateTransactionService
	 */
	protected GiftCertificateTransactionService getGiftCertificateTransactionService() {
		return giftCertificateTransactionService;
	}

	/**
	 * @param giftCertificateTransactionService the giftCertificateTransactionService to set
	 */
	public void setGiftCertificateTransactionService(
			final GiftCertificateTransactionService giftCertificateTransactionService) {
		this.giftCertificateTransactionService = giftCertificateTransactionService;
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
}
