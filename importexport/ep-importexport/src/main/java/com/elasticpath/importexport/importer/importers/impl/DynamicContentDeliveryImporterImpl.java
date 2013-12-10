package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.targetedselling.DynamicContentDeliveryDTO;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * An Importer for DCD.
 */
public class DynamicContentDeliveryImporterImpl extends AbstractImporterImpl<DynamicContentDelivery, DynamicContentDeliveryDTO> {

	private DomainAdapter<DynamicContentDelivery, DynamicContentDeliveryDTO> dcdAdapter;
	private DynamicContentDeliveryService dcdService;
	private SellingContextService sellingContextService;

	@Override
	public void initialize(final ImportContext context,
			final SavingStrategy<DynamicContentDelivery, DynamicContentDeliveryDTO> savingStrategy) {
		super.initialize(context, savingStrategy);

		getSavingStrategy().setSavingManager(new SavingManager<DynamicContentDelivery>() {

			@Override
			public DynamicContentDelivery update(final DynamicContentDelivery dcd) {
				SellingContext sellingContext = dcd.getSellingContext();
				
				if (sellingContext != null) {
					dcd.setSellingContext(sellingContextService.saveOrUpdate(sellingContext));
				}
				
				return dcdService.saveOrUpdate(dcd);
			}

			@Override
			public void save(final DynamicContentDelivery obj) {
				update(obj);
			}
		});
	}
	
	@Override
	protected CollectionsStrategy<DynamicContentDelivery, DynamicContentDeliveryDTO> getCollectionsStrategy() {
		return new DCDCollectionsStrategy();
	}
	
	/**
	 */
	private class DCDCollectionsStrategy implements CollectionsStrategy<DynamicContentDelivery, DynamicContentDeliveryDTO> {

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}

		@Override
		public void prepareCollections(final DynamicContentDelivery domainObject, final DynamicContentDeliveryDTO dto) {
			SellingContext sellingContext = domainObject.getSellingContext();
			if (sellingContext != null) {
				// This solves the problem of orphaned conditions.
				sellingContextService.remove(sellingContext);
			}
			domainObject.setSellingContext(null);
		}
	}
	
	@Override
	public boolean executeImport(final DynamicContentDeliveryDTO object) {
		sanityCheck();
		setImportStatus(object);

		final DynamicContentDelivery obtainedDCD = findPersistentObject(object);
		checkDuplicateGuids(object, obtainedDCD);
		final DynamicContentDelivery dcd = getSavingStrategy().populateAndSaveObject(obtainedDCD, object);

		return dcd != null;
	}

	public String getImportedObjectName() {
		return DynamicContentDeliveryDTO.ROOT_ELEMENT;
	}

	@Override
	protected DynamicContentDelivery findPersistentObject(final DynamicContentDeliveryDTO dcd) {
		return getDynamicContentDeliveryService().findByGuid(dcd.getGuid());
	}

	@Override
	protected DomainAdapter<DynamicContentDelivery, DynamicContentDeliveryDTO> getDomainAdapter() {
		return getDcdAdapter();
	}

	@Override
	protected String getDtoGuid(final DynamicContentDeliveryDTO dcd) {
		return dcd.getGuid();
	}

	@Override
	protected void setImportStatus(final DynamicContentDeliveryDTO dcd) {
		getStatusHolder().setImportStatus("(" + dcd.getName() + ")");
	}

	/**
	 * @param dcdService the dcdService to set
	 */
	public void setDynamicContentDeliveryService(final DynamicContentDeliveryService dcdService) {
		this.dcdService = dcdService;
	}

	/**
	 * @return the dcdService
	 */
	public DynamicContentDeliveryService getDynamicContentDeliveryService() {
		return dcdService;
	}

	/**
	 * @param dcdAdapter the dcdAdapter to set
	 */
	public void setDcdAdapter(final DomainAdapter<DynamicContentDelivery, DynamicContentDeliveryDTO> dcdAdapter) {
		this.dcdAdapter = dcdAdapter;
	}

	/**
	 * @return the dcdAdapter
	 */
	public DomainAdapter<DynamicContentDelivery, DynamicContentDeliveryDTO> getDcdAdapter() {
		return dcdAdapter;
	}

	/**
	 * Sets the selling context service.
	 * 
	 * @param sellingContextService selling context service
	 */
	public void setSellingContextService(final SellingContextService sellingContextService) {
		this.sellingContextService = sellingContextService;
	}

	/**
	 * @return The selling context service.
	 */
	public SellingContextService getSellingContextService() {
		return sellingContextService;
	}
	
	@Override
	public Class<? extends DynamicContentDeliveryDTO> getDtoClass() {
		return DynamicContentDeliveryDTO.class;
	}
}