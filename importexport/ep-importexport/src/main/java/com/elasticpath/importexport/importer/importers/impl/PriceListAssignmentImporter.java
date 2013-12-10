package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.pricing.PriceListAssignmentAdapter;
import com.elasticpath.importexport.common.dto.pricing.PriceListAssignmentDTO;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.sellingcontext.SellingContextService;

/**
 * Importer for {@link PriceListAssignment}.
 */
public class PriceListAssignmentImporter extends AbstractImporterImpl<PriceListAssignment, PriceListAssignmentDTO> {

	private PriceListAssignmentAdapter priceListAssignmentAdapter;

	private PriceListAssignmentService priceListAssignmentService;
	
	private SellingContextService sellingContextService;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<PriceListAssignment, PriceListAssignmentDTO> savingStrategy) {
		super.initialize(context, savingStrategy);

		getSavingStrategy().setSavingManager(new SavingManager<PriceListAssignment>() {
			@Override
			public PriceListAssignment update(final PriceListAssignment obj) {
				return priceListAssignmentService.saveOrUpdate(obj);
			}

			@Override
			public void save(final PriceListAssignment obj) {
				update(obj);
			}
		});
	}

	@Override
	protected PriceListAssignment findPersistentObject(final PriceListAssignmentDTO dto) {
		return priceListAssignmentService.findByGuid(dto.getGuid());
	}

	@Override
	protected DomainAdapter<PriceListAssignment, PriceListAssignmentDTO> getDomainAdapter() {
		return priceListAssignmentAdapter;
	}

	@Override
	protected String getDtoGuid(final PriceListAssignmentDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected void setImportStatus(final PriceListAssignmentDTO pla) {
		getStatusHolder().setImportStatus("(" + pla.getName() + ")");
	}

	@Override
	public String getImportedObjectName() {
		return PriceListAssignmentDTO.ROOT_ELEMENT;
	}

	/**
	 * @param priceListAssignmentAdapter the priceListAssignmentAdapter to set
	 */
	public void setPriceListAssignmentAdapter(final PriceListAssignmentAdapter priceListAssignmentAdapter) {
		this.priceListAssignmentAdapter = priceListAssignmentAdapter;
	}

	/**
	 * @return the priceListAssignmentAdapter
	 */
	public PriceListAssignmentAdapter getPriceListAssignmentAdapter() {
		return priceListAssignmentAdapter;
	}

	/**
	 * @param priceListAssignmentService the priceListAssignmentService to set
	 */
	public void setPriceListAssignmentService(final PriceListAssignmentService priceListAssignmentService) {
		this.priceListAssignmentService = priceListAssignmentService;
	}

	/**
	 * @return the priceListAssignmentService
	 */
	public PriceListAssignmentService getPriceListAssignmentService() {
		return priceListAssignmentService;
	}
	
	/**
	 * Sets the selling  context service for selling context domain object.
	 * 
	 * @param sellingContextService value to set
	 */
	public void setSellingContextService(final SellingContextService sellingContextService) {
		this.sellingContextService = sellingContextService;
	}

	
	@Override
	protected CollectionsStrategy<PriceListAssignment, PriceListAssignmentDTO> getCollectionsStrategy() {
		return new PriceListAssignmentCollectionsStrategy();
	}

	@Override
	public Class<? extends PriceListAssignmentDTO> getDtoClass() {
		return PriceListAssignmentDTO.class;
	}

	/**
	 * Price List Assignment Condition collection strategy.
	 */
	private class PriceListAssignmentCollectionsStrategy implements CollectionsStrategy<PriceListAssignment, PriceListAssignmentDTO> {
		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}

		@Override
		public void prepareCollections(final PriceListAssignment domainObject, final PriceListAssignmentDTO dto) {
			
			SellingContext sellingContext = domainObject.getSellingContext();
			
			if (sellingContext != null) {
				sellingContextService.remove(sellingContext);
			}
			domainObject.setSellingContext(null);
		}
	}
}
