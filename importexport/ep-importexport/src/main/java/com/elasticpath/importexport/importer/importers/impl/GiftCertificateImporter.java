package com.elasticpath.importexport.importer.importers.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO;
import com.elasticpath.common.dto.giftcertificate.GiftCertificateTransactionDTO;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.payment.GiftCertificateTransaction;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.payment.GiftCertificateTransactionService;
/**
 * Importer for {@link com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO} and its associated domain class.
 */
public class GiftCertificateImporter extends AbstractImporterImpl<GiftCertificate, GiftCertificateDTO> {

	private DomainAdapter<GiftCertificate, GiftCertificateDTO> giftCertificateAdapter;

	private DomainAdapter<GiftCertificateTransaction, GiftCertificateTransactionDTO> giftCertificateTransactionAdapter;
	
	private GiftCertificateService giftCertificateService;  
	
	private GiftCertificateTransactionService giftCertificateTransactionService;
	private SavingStrategy<GiftCertificateTransaction, GiftCertificateTransactionDTO> giftCertificateTransactionSavingStrategy;

	private SavingManager<GiftCertificateTransaction> giftCertificateTransactionSavingManager;

	@Override
	public String getImportedObjectName() {
		return GiftCertificateDTO.ROOT_ELEMENT;
	}

	/**
	 * Gets the guid from the DTO.
	 * @param dto the {@link com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO} to get the guid from.
	 * @return the DTO guid.
	 */
	@Override
	protected String getDtoGuid(final GiftCertificateDTO dto) {
		return dto.getGuid();
	}

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<GiftCertificate, GiftCertificateDTO> savingStrategy) {
		super.initialize(context, savingStrategy);

		giftCertificateTransactionSavingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT_OR_UPDATE,
				getGiftCertificateTransactionSavingManager());
		giftCertificateTransactionSavingStrategy.setDomainAdapter(giftCertificateTransactionAdapter);
	}

	@Override
	public boolean executeImport(final GiftCertificateDTO giftCertificateDTO) {
		setImportStatus(giftCertificateDTO);		
		final GiftCertificate persistedGiftCertificate = findPersistentObject(giftCertificateDTO);
		checkDuplicateGuids(giftCertificateDTO, persistedGiftCertificate);
		getSavingStrategy().setDomainAdapter(giftCertificateAdapter);

		final GiftCertificate savedGiftCertificate = getSavingStrategy().
				populateAndSaveObject(persistedGiftCertificate, giftCertificateDTO);
	
		// if savedGiftCertificate == null it means that this GiftCertificate was not imported due to the import strategy configuration
		if (savedGiftCertificate == null) {
			return false;
		}

		saveGiftCertificateTransactions(giftCertificateDTO, savedGiftCertificate);	
		return true;
	}

	/**
	 * Saves the {@link com.elasticpath.domain.payment.GiftCertificateTransaction}s.
	 * Note that GiftCertificateTransaction have no unique identifiers, and thus all transactions 
	 * associated with a gift certificate must be replaced instead of merged. 
	 * @param giftCertificateDTO the {@link com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO} to import
	 * @param giftCertificate the {@link com.elasticpath.domain.catalog.GiftCertificate} to verify them against.
	 */
	private void saveGiftCertificateTransactions(final GiftCertificateDTO giftCertificateDTO, final GiftCertificate persistedGiftCertificate) {
		
		if (giftCertificateDTO.getGiftCertificateTransactions() != null) {
			for (GiftCertificateTransactionDTO giftCertificateTransactionDTO : giftCertificateDTO.getGiftCertificateTransactions()) {

				String giftCertificateGuidFromTransactionDto = giftCertificateTransactionDTO.getGiftCertificateGuid();
				String giftCertificateGuidFromCertificateDto = giftCertificateDTO.getGuid();

				if (!giftCertificateGuidFromTransactionDto.equals(giftCertificateGuidFromCertificateDto)) {
					throw new EpServiceException("GiftCertificate guid (" + giftCertificateGuidFromTransactionDto + ") " 
							+ "and GiftCertificate guid from transaction (" + giftCertificateGuidFromCertificateDto + ") do not match.");
				}

				if (dtoIsNotAlreadyPersisted(giftCertificateTransactionDTO, persistedGiftCertificate)) {
					giftCertificateTransactionSavingStrategy.populateAndSaveObject(giftCertificateTransactionAdapter.createDomainObject(),
							giftCertificateTransactionDTO);
				}

			}
		
		}
	}
	
	/**
	 * Checks if a copy of the the current {@link com.elasticpath.common.dto.giftcertificate.GiftCertificateTransactionDTO} is already 
	 * saved with the specified {@link com.elasticpath.domain.catalog.GiftCertificate}.
	 * @param giftCertificateTransactionDTO the transaction DTO to check
	 * @param giftCertificate the giftCertificate  
	 * @return true if the DTO is not already present, false otherwise.
	 */
	protected boolean dtoIsNotAlreadyPersisted(final GiftCertificateTransactionDTO giftCertificateTransactionDTO, 
			final GiftCertificate giftCertificate) {

		List <GiftCertificateTransaction> retrievedGiftCertificateTransactions = 
			giftCertificateTransactionService.getGiftCertificateTransactions(giftCertificate); 
		
		for (GiftCertificateTransaction currentTransaction : retrievedGiftCertificateTransactions) {
			if (transactionMatchesDto(currentTransaction, giftCertificateTransactionDTO)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Compares all fields in a {@link com.elasticpath.domain.payment.GiftCertificateTransaction} and a
	 * {@link com.elasticpath.common.dto.giftcertificate.GiftCertificateTransactionDTO}.
	 * @param transaction the gift certificate transaction 
	 * @param transactionDTO the gift certificate transaction DTO
	 * @return true if all fields are identical, false otherwise.
	 */
	protected boolean transactionMatchesDto(final GiftCertificateTransaction transaction, final GiftCertificateTransactionDTO transactionDTO) {

		String giftCertificateGuidFromTransaction = transaction.getGiftCertificate().getGuid();
		String giftCertificateGuidFromDto = transactionDTO.getGiftCertificateGuid();

		if (!giftCertificateGuidFromTransaction.equals(giftCertificateGuidFromDto)) {
			return false;
		}
		
		BigDecimal transactionAmount = transaction.getAmount();
		BigDecimal dtoAmount = transactionDTO.getAmount();

		if (!transactionAmount.equals(dtoAmount)) {
			return false;
		}
		
		String codeFromTransaction = transaction.getAuthorizationCode();
		String codeFromDto = transactionDTO.getAuthorizationCode();
		
		if (!codeFromTransaction.equals(codeFromDto)) {
			return false;
		}

		Date transactionCreationDate = transaction.getCreatedDate();
		Date dtoCreationDate = transactionDTO.getCreationDate();
		
		if (!transactionCreationDate.equals(dtoCreationDate)) {
			return false;
		}
		
		String transactionType = transaction.getTransactionType();
		String transactionTypeFromDto =  transactionDTO.getTransactionType();
		
		if (!transactionType.equals(transactionTypeFromDto)) {
			return false;
		}
		
		return true;
		
	}
	
	@Override
	protected DomainAdapter<GiftCertificate, GiftCertificateDTO> getDomainAdapter() {
		return giftCertificateAdapter;
	}

	@Override
	protected GiftCertificate findPersistentObject(final GiftCertificateDTO dto) {
		return giftCertificateService.findByGuid(dto.getGuid());
	}

	@Override
	protected void setImportStatus(final GiftCertificateDTO object) {
		getStatusHolder().setImportStatus("(" + object.getGuid() + ")");		
	}

	/**
	 * @return the giftCertificateAdapter
	 */
	public DomainAdapter<GiftCertificate, GiftCertificateDTO> getGiftCertificateAdapter() {
		return giftCertificateAdapter;
	}

	/**
	 * @param giftCertificateAdapter the giftCertificateAdapter to set
	 */
	public void setGiftCertificateAdapter(final DomainAdapter<GiftCertificate, GiftCertificateDTO> giftCertificateAdapter) {
		this.giftCertificateAdapter = giftCertificateAdapter;
	}

	/**
	 * @return the giftCertificateService
	 */
	public GiftCertificateService getGiftCertificateService() {
		return giftCertificateService;
	}

	/**
	 * @param giftCertificateService the giftCertificateService to set
	 */
	public void setGiftCertificateService(final GiftCertificateService giftCertificateService) {
		this.giftCertificateService = giftCertificateService;
	}

	/**
	 * @return the giftCertificateTransactionService
	 */
	public GiftCertificateTransactionService getGiftCertificateTransactionService() {
		return giftCertificateTransactionService;
	}

	/**
	 * @param giftCertificateTransactionService the giftCertificateTransactionService to set
	 */
	public void setGiftCertificateTransactionService(final GiftCertificateTransactionService giftCertificateTransactionService) {
		this.giftCertificateTransactionService = giftCertificateTransactionService;
	}

	/**
	 * @return the giftCertificateTransactionAdapter
	 */
	public DomainAdapter<GiftCertificateTransaction, GiftCertificateTransactionDTO> getGiftCertificateTransactionAdapter() {
		return giftCertificateTransactionAdapter;
	}

	/**
	 * @param giftCertificateTransactionAdapter the giftCertificateTransactionAdapter to set
	 */
	public void setGiftCertificateTransactionAdapter(
			final DomainAdapter<GiftCertificateTransaction, GiftCertificateTransactionDTO> giftCertificateTransactionAdapter) {
		this.giftCertificateTransactionAdapter = giftCertificateTransactionAdapter;
	}

	@Override
	public Class<? extends GiftCertificateDTO> getDtoClass() {
		return GiftCertificateDTO.class;
	}	

	protected SavingManager<GiftCertificateTransaction> getGiftCertificateTransactionSavingManager() {
		return giftCertificateTransactionSavingManager;
	}

	public void setGiftCertificateTransactionSavingManager(final SavingManager<GiftCertificateTransaction> giftCertificateTransactionSavingManager) {
		this.giftCertificateTransactionSavingManager = giftCertificateTransactionSavingManager;
	}
}
