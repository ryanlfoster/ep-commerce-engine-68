/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.controller.impl;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.transaction.TransactionException;
import org.springframework.util.CollectionUtils;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.impl.BaseAmountFilterImpl;
import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.commons.enums.OperationEnum;
import com.elasticpath.commons.util.impl.JaxbUtils;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.ProductTypeDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.exception.runtime.MarshallingRuntimeException;
import com.elasticpath.importexport.common.marshalling.DefaultValidationEventHandler;
import com.elasticpath.importexport.common.marshalling.XMLUnmarshaller;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.changesetsupport.BusinessObjectDescriptorLocator;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.controller.ImportStage;
import com.elasticpath.importexport.importer.controller.ImportStageFailedException;
import com.elasticpath.importexport.importer.controller.RelatedObjectsResolver;
import com.elasticpath.importexport.importer.importers.Importer;
import com.elasticpath.importexport.importer.importers.ImporterFactory;
import com.elasticpath.importexport.importer.importers.impl.SavingManager;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.pricing.BaseAmountService;

/**
 * The stage that is responsible for adding objects to a change set.
 */
public class ChangeSetImportStageImpl implements ImportStage {

	private static final Logger LOG = Logger.getLogger(ChangeSetImportStageImpl.class);

	private static final int LOG_EVERY_N_ENTRIES = 100;

	private ImporterFactory importerFactory;

	private DefaultValidationEventHandler validationEventHandler;

	private SavingManager<? extends Persistable> savingManager;

	private ChangeSetService changeSetService;

	private TimeService timeService;

	private PersistenceEngine persistenceEngine;

	private BusinessObjectDescriptorLocator businessObjectDescriptorLocator;

	private ThreadLocalMap<String, Object> metadataMap;

	private String cmUserGuid;

	private Map<Class<? extends Dto>, RelatedObjectsResolver<? extends Persistable, ? extends Dto>> relatedObjectsResolvers;

	private String stageId;

	private final Set<String> multiSkuProductTypeNames = new HashSet<String>();

	private BaseAmountService baseAmountService;

	/**
	 * Adds the objects referenced by the entryToImport to a change set.
	 *
	 * @param entryToImport the entry to import
	 * @param context the import context
	 * @throws ConfigurationException on error
	 * @throws ImportStageFailedException when the stage failed to execute properly
	 */
	@Override
	public void execute(final InputStream entryToImport, final ImportContext context)
		throws ConfigurationException, ImportStageFailedException {

		XMLStreamReader streamReader = null;
		try {
			streamReader = XMLInputFactory.newInstance().createXMLStreamReader(entryToImport);
			streamReader.nextTag();
			streamReader.require(XMLStreamConstants.START_ELEMENT, null, null);
			final JobType jobType = JobType.getJobTypeByTag(streamReader.getLocalName());

			final Importer<? super Persistable, ? super Dto> importer = importerFactory.createImporter(jobType, context, savingManager);

			final Class<?>[] jaxbClasses = JaxbUtils.createClassArray(importer.getDtoClass(), importer.getAuxiliaryJaxbClasses());
			final XMLUnmarshaller unmarshaller = createUnmarshaller(jaxbClasses,
					context.getImportConfiguration().isXmlValidation(), importer.getSchemaPath());

			streamReader.nextTag();

			PersistenceSession session = getPersistenceEngine().getSharedPersistenceSession();

			int count = 0;
			while (streamReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
				final Transaction transaction = session.beginTransaction();
				int transactionVolume = 0;
				try {
					while (streamReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
						streamReader.require(XMLStreamConstants.START_ELEMENT, null, importer.getImportedObjectName());
						try {
							Dto unmarshalledObject = unmarshaller.unmarshall(streamReader);
							addObjectToChangeSetWithErrorHandling(unmarshalledObject, importer, context);
						} catch (MarshallingRuntimeException marshallingRuntimeException) {
							LOG.error(new Message("IE-30400", marshallingRuntimeException, jobType.toString(), validationEventHandler
									.getLastErrorStatus()));
							LOG.error(marshallingRuntimeException.getIEMessage());
							throw new ImportStageFailedException("IE-30400", marshallingRuntimeException);
						}

						skipNotStartElements(streamReader, importer.getImportedObjectName());
					}

					try {
						LOG.debug("Attempting Transaction Commit");
						transaction.commit();
						if (count++ % LOG_EVERY_N_ENTRIES == 0) {
							LOG.info("Added to change set " + count + " " + jobType.getTagName());
						}
					} catch (TransactionException exception) {
						LOG.error((new Message("IE-30404", exception, Integer.toString(transactionVolume))));
						throw new ImportStageFailedException("IE-30404", exception);
					} catch (Exception exc) {
						LOG.error(new Message("IE-30402", exc, jobType.toString(), importer.getStatusHolder().getImportStatus()));
						throw new ImportStageFailedException("IE-30402", exc);
					}
				} catch (Exception exception) {
					LOG.error(new Message("IE-30402", exception, jobType.toString(), importer.getStatusHolder().getImportStatus()));
					skipNotStartElements(streamReader, importer.getImportedObjectName());
					try {
						transaction.rollback();
					} catch (TransactionException ex) {
						LOG.error((new Message("IE-30405", ex, Integer.toString(transactionVolume))));
					}
					throw new ImportStageFailedException("IE-30403", exception);
				}
			}
			LOG.info("Added to change set " + count % LOG_EVERY_N_ENTRIES + " " + jobType.getTagName());
			session.close();

		} catch (XMLStreamException exception) {
			throw new ImportStageFailedException("IE-30403", exception);
		} finally {
			if (streamReader != null) {
				try {
					streamReader.close();
				} catch (XMLStreamException e) {
					LOG.error("Could not close xml stream reader", e);
				}
			}
		}
	}

	/**
	 * Wraps the {@code addObjectToChangeSet} call with error handling.
	 * @param dto the DTO
	 * @param importer the importer
	 * @param context the import context
	 */
	protected void addObjectToChangeSetWithErrorHandling(final Dto dto,
			final Importer<? super Persistable, ? super Dto> importer, final ImportContext context) {
		try {
			addObjectToChangeSet(dto, importer, context);
		} catch (Exception e) {
			LOG.error(new Message("IE-30406", e, getChangeSetGuid(), dto.getClass().getName(), e.getMessage()));
			throw new ImportStageFailedException("IE-30406", e, getChangeSetGuid(), dto.getClass().getName(), e.getMessage());
		}

	}

	private void skipNotStartElements(final XMLStreamReader streamReader, final String expectedTagName) throws XMLStreamException {
		while ((streamReader.getEventType() != XMLStreamConstants.START_ELEMENT || !streamReader.getLocalName().equals(expectedTagName))
				&& streamReader.hasNext()) {
			streamReader.next();
		}
	}

	private XMLUnmarshaller createUnmarshaller(final Class<?>[] importDtoClasses, final boolean xmlValidation,
			final String schemaPath) {
		XMLUnmarshaller unmarshaller = new XMLUnmarshaller(importDtoClasses);
		if (xmlValidation) {
			unmarshaller.initValidationParameters(schemaPath, validationEventHandler);
		}
		return unmarshaller;
	}

	private boolean isBaseAmountRetainCollection(final Dto dto, final ImportContext context) {
		boolean isBaseAmountRetainCollection = false;
		//this is a bit of a specific check that we dont want to fail a job
		//for so lets catch and log anything that can go wrong
		try {
			isBaseAmountRetainCollection = dto instanceof BaseAmountDTO
			&&
			ObjectUtils.equals(context.getImportConfiguration().getImporterConfiguration(JobType.PRICELISTDESCRIPTOR)
				.getCollectionStrategyType(DependentElementType.BASE_AMOUNTS), CollectionStrategyType.RETAIN_COLLECTION);
		} catch (Exception e) {
			LOG.error("Could not determine isBaseAmountRetainCollection", e);
		}
		return isBaseAmountRetainCollection;
	}

	/**
	 * Adds the object represented by the given DTO to a change set.
	 *
	 * @param dto the DTO
	 * @param importer the importer
	 * @param context the import context
	 */
	protected void addObjectToChangeSet(final Dto dto, final Importer<? super Persistable, ? super Dto> importer,
			final ImportContext context) {
		//you can not change the guid of an existing baseamount using a change set import for retain collection
		if (isBaseAmountRetainCollection(dto, context)) {
			// filters are set according to the TBASEAMOUNT_UNIQUE index constraint in db
			final BaseAmountFilter baseAmountFilter = new BaseAmountFilterImpl();
			baseAmountFilter.setPriceListDescriptorGuid(((BaseAmountDTO) dto).getPriceListDescriptorGuid());
			baseAmountFilter.setObjectGuid(((BaseAmountDTO) dto).getObjectGuid());
			baseAmountFilter.setObjectType(((BaseAmountDTO) dto).getObjectType());
			baseAmountFilter.setQuantity(((BaseAmountDTO) dto).getQuantity());

			final Collection<BaseAmount> foundByFilter = baseAmountService.findBaseAmounts(baseAmountFilter);
			for (final BaseAmount foundBaseAmount : foundByFilter) {
				if (!StringUtils.equals(((BaseAmountDTO) dto).getGuid(), foundBaseAmount.getGuid())) {
					LOG.error(new Message("IE-31012", ((BaseAmountDTO) dto).getGuid(), getChangeSetGuid()));
					return;
				}
			}
		}

		Collection<BusinessObjectDescriptor> allObjectDescriptors = new HashSet<BusinessObjectDescriptor>();
		BusinessObjectDescriptor mainObjectDescriptor = businessObjectDescriptorLocator.locateObjectDescriptor(dto);
		if (mainObjectDescriptor != null) {
			allObjectDescriptors.add(mainObjectDescriptor);
		}

		if (dto instanceof CatalogDTO) {
			CatalogDTO catalogDto = (CatalogDTO) dto;
			for (ProductTypeDTO productTypeDto : catalogDto.getProductTypes()) {
				if (productTypeDto.getMultiSku() != null) {
					multiSkuProductTypeNames.add(productTypeDto.getName());
				}
			}
		}

		allObjectDescriptors.addAll(resolveRelatedObjects(dto, importer));

		if (CollectionUtils.isEmpty(allObjectDescriptors)) {
			LOG.debug("No business object descriptor was returned for DTO instance: " + dto);
		} else {
			Map<String, String> metadata = new HashMap<String, String>();
			metadata.put("addedByUserGuid", cmUserGuid);
			metadata.put("dateAdded", getCurrentDateString());
			metadata.put("action", ChangeSetMemberAction.UNDEFINED.getName());

			for (BusinessObjectDescriptor objectDescriptor : allObjectDescriptors) {
				getChangeSetService().addObjectToChangeSet(getChangeSetGuid(), objectDescriptor, metadata);
			}
			context.getSummary().addAddedToChangeSetCount(allObjectDescriptors.size());
		}
	}

	@SuppressWarnings("unchecked")
	private <D extends Dto, P extends Persistable> Collection<BusinessObjectDescriptor> resolveRelatedObjects(final D dto,
			final Importer<P, D> importer) {
		final RelatedObjectsResolver<P, D> resolver =
				(RelatedObjectsResolver<P, D>) getRelatedObjectsResolvers().get(dto.getClass());

		if (resolver != null) {
			return resolver.resolveRelatedObjects(dto, importer, multiSkuProductTypeNames);
		}
		return Collections.emptyList();
	}

	private String getCurrentDateString() {
		return DateUtils.format(timeService.getCurrentTime().getTime(), "yyyyMMddHHmmssSSS");
	}

	/**
	 *
	 * @return the change set service instance
	 */
	protected ChangeSetService getChangeSetService() {
		return changeSetService;
	}

	/**
	 *
	 * @param changeSetService the changeSetService to set
	 */
	public void setChangeSetService(final ChangeSetService changeSetService) {
		this.changeSetService = changeSetService;
	}

	/**
	 *
	 * @param cmUserGuid cm user guid
	 */
	public void setCmUserGuid(final String cmUserGuid) {
		this.cmUserGuid = cmUserGuid;
	}

	/**
	 *
	 * @return the changeSetGuid
	 */
	public String getChangeSetGuid() {
		return (String) metadataMap.get("changeSetGuid");
	}

	/**
	 *
	 * @return the importerFactory
	 */
	public ImporterFactory getImporterFactory() {
		return importerFactory;
	}

	/**
	 *
	 * @param importerFactory the importerFactory to set
	 */
	public void setImporterFactory(final ImporterFactory importerFactory) {
		this.importerFactory = importerFactory;
	}

	/**
	 *
	 * @return the validationEventHandler
	 */
	public DefaultValidationEventHandler getValidationEventHandler() {
		return validationEventHandler;
	}

	/**
	 *
	 * @param validationEventHandler the validationEventHandler to set
	 */
	public void setValidationEventHandler(final DefaultValidationEventHandler validationEventHandler) {
		this.validationEventHandler = validationEventHandler;
	}

	/**
	 *
	 * @return the savingManager
	 */
	public SavingManager<? extends Persistable> getSavingManager() {
		return savingManager;
	}

	/**
	 *
	 * @param savingManager the savingManager to set
	 */
	public void setSavingManager(final SavingManager<? extends Persistable> savingManager) {
		this.savingManager = savingManager;
	}

	/**
	 * Set metadata map.
	 * @param threadLocalMap the thread local map
	 */
	public void setMetadataMap(final ThreadLocalMap<String, Object> threadLocalMap) {
		metadataMap = threadLocalMap;
	}

	/**
	 *
	 * @param timeService the time service.
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 *
	 * @return the persistenceEngine
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 *
	 * @param persistenceEngine the persistenceEngine to set
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	@Override
	@SuppressWarnings("PMD.PositionLiteralsFirstInComparisons")
	public boolean isActive() {
		return OperationEnum.OPERATIONAL.equals(metadataMap.get("changeSetOperation"));
	}

	/**
	 *
	 * @param businessObjectDescriptorLocator the businessObjectDescriptorLocator to set
	 */
	public void setBusinessObjectDescriptorLocator(final BusinessObjectDescriptorLocator businessObjectDescriptorLocator) {
		this.businessObjectDescriptorLocator = businessObjectDescriptorLocator;
	}

	/**
	 *
	 * @return the businessObjectDescriptorLocator
	 */
	public BusinessObjectDescriptorLocator getBusinessObjectDescriptorLocator() {
		return businessObjectDescriptorLocator;
	}

	/**
	 *
	 * @return the relatedObjectsResolvers
	 */
	public Map<Class<? extends Dto>, RelatedObjectsResolver<? extends Persistable, ? extends Dto>> getRelatedObjectsResolvers() {
		return relatedObjectsResolvers;
	}

	/**
	 *
	 * @param relatedObjectsResolvers the relatedObjectsResolvers to set
	 */
	public void setRelatedObjectsResolvers(
			final Map<Class<? extends Dto>, RelatedObjectsResolver<? extends Persistable, ? extends Dto>> relatedObjectsResolvers) {
		this.relatedObjectsResolvers = relatedObjectsResolvers;
	}

	@Override
	public String getName() {
		return "Change Set Import Stage";
	}

	@Override
	public String getId() {
		return stageId;
	}

	/**
	 *
	 * @param stageId the stageId to set
	 */
	public void setId(final String stageId) {
		this.stageId = stageId;
	}

	/**
	 * @param baseAmountService the baseAmountService
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}

}
