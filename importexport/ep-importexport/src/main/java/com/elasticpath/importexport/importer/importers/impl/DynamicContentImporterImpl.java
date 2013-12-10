package com.elasticpath.importexport.importer.importers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.dynamiccontent.DynamicContentAdapter;
import com.elasticpath.importexport.common.adapters.dynamiccontent.ParameterValueAdapter;
import com.elasticpath.importexport.common.dto.dynamiccontent.DynamicContentDTO;
import com.elasticpath.importexport.common.dto.dynamiccontent.ParameterValueDTO;
import com.elasticpath.importexport.common.exception.runtime.ImportDuplicateAssociatedEntityRuntimeException;
import com.elasticpath.importexport.common.exception.runtime.ImportDuplicateEntityRuntimeException;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.contentspace.DynamicContentService;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ParameterValueRelation;

/**
 * An importer for {@link DynamicContent}.
 */
public class DynamicContentImporterImpl extends AbstractImporterImpl<DynamicContent, DynamicContentDTO> {

	private PersistenceEngine persistenceEngine;

	private DynamicContentAdapter dynamicContentAdapter;

	private ParameterValueAdapter parameterValueAdapter;

	private DynamicContentService dynamicContentService;

	private Set<String> processedParameterValueGuids;

	private QueryService<ParameterValue> parameterValueQueryService;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<DynamicContent, DynamicContentDTO> savingStrategy) {
		super.initialize(context, savingStrategy);

		getSavingStrategy().setSavingManager(new SavingManager<DynamicContent>() {

			@Override
			public DynamicContent update(final DynamicContent persistable) {
				return dynamicContentService.saveOrUpdate(persistable);
			}

			@Override
			public void save(final DynamicContent persistable) {
				update(persistable);
			}

		});

		setProcessedParameterValueGuids(new HashSet<String>());
	}

	@Override
	public boolean executeImport(final DynamicContentDTO object) {
		sanityCheck();

		setImportStatus(object);

		final DynamicContent obtainedDynamicContent = findPersistentObject(object);
		checkDuplicateGuids(object, obtainedDynamicContent);
		final DynamicContent dynamicContent = getSavingStrategy().populateAndSaveObject(obtainedDynamicContent, object);

		return dynamicContent != null;
	}

	/**
	 * {@inheritDoc} <br>
	 * Add parameterValue guid duplication checking for DynamicContent/DynamicContentDTOs.
	 */
	@Override
	protected void checkDuplicateGuids(final DynamicContentDTO object, final DynamicContent persistable) {
		final String dtoGuid = getDtoGuid(object);
		if (getSavingStrategy().isImportRequired(persistable)) {
			if (dtoGuid != null && getProcessedObjectGuids().contains(dtoGuid)) {
				throw new ImportDuplicateEntityRuntimeException("IE-30502", dtoGuid);
			}
			checkDuplicateParameterValueGuids(object, persistable);
			getProcessedObjectGuids().add(dtoGuid);
		}
	}

	/**
	 * Check for duplicate parameter value guids.
	 *
	 * @param object the object
	 * @param persistable the persistable
	 */
	protected void checkDuplicateParameterValueGuids(final DynamicContentDTO object, final DynamicContent persistable) {

		Map<String, ParameterValue> persistedParameterValues = findPersistedParameterValuesFromDynamicContentDTO(object);

		for (ParameterValueDTO item : object.getParameterValues()) {
			final String guid = item.getGuid();
			final ParameterValue parameterValue = persistedParameterValues.get(guid);
			if (getSavingStrategy().isImportRequired(parameterValue)) {
				if (guid != null && !isParameterValueAssociatedToDynamicContent(persistable, parameterValue)
						&& getProcessedParameterValueGuids().contains(guid)) {
					throw new ImportDuplicateAssociatedEntityRuntimeException("IE-30502", guid);
				}
				getProcessedParameterValueGuids().add(guid);
			}
		}
	}

	private Map<String, ParameterValue> findPersistedParameterValuesFromDynamicContentDTO(final DynamicContentDTO object) {
		List<String> guids = getParameterValueGuids(object);
		List<ParameterValue> parameterValues = findParameterValuesByGuids(guids);
		return convertParameterValueListToMap(parameterValues);
	}

	private List<String> getParameterValueGuids(final DynamicContentDTO object) {
		List<String> guids = new ArrayList<String>();
		for (ParameterValueDTO item : object.getParameterValues()) {
			guids.add(item.getGuid());
		}
		return guids;
	}

	private List<ParameterValue> findParameterValuesByGuids(final List<String> guids) {
		QueryCriteria<ParameterValue> criteria = CriteriaBuilder.criteriaFor(ParameterValue.class)
				.with(ParameterValueRelation.having().guids(guids))
				.returning(ResultType.ENTITY);

		QueryResult<ParameterValue> result = getParameterValueQueryService().query(criteria);

		return result.getResults();
	}

	private Map<String, ParameterValue> convertParameterValueListToMap(final List<ParameterValue> parameterValues) {
		Map<String, ParameterValue> results = new HashMap<String, ParameterValue>();
		for (ParameterValue item : parameterValues) {
			results.put(item.getGuid(), item);
		}
		return results;
	}

	/**
	 * Tests if this parameter value is associated with this DynamicContent instance, if so return true.
	 * If the dynamic content is null then return false.
	 *
	 * @param persistable the dynamic content instance to compare against
	 * @param parameterValue the parameter value
	 * @return true if the Parameter value is found within the dynamic content instance, false otherwise.
	 */
	private boolean isParameterValueAssociatedToDynamicContent(final DynamicContent persistable, final ParameterValue parameterValue) {
		if (persistable == null) {
			return false;
		}
		return persistable.getParameterValues().contains(parameterValue);
	}

	@Override
	protected DynamicContent findPersistentObject(final DynamicContentDTO dto) {
		return dynamicContentService.findByGuid(dto.getGuid());
	}

	@Override
	protected DomainAdapter<DynamicContent, DynamicContentDTO> getDomainAdapter() {
		return getDynamicContentAdapter();
	}

	@Override
	protected String getDtoGuid(final DynamicContentDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected void setImportStatus(final DynamicContentDTO object) {
		getStatusHolder().setImportStatus("(" + object.getName() + ")");
	}

	@Override
	public String getImportedObjectName() {
		return DynamicContentDTO.ROOT_ELEMENT;
	}

	protected Set<String> getProcessedParameterValueGuids() {
		return processedParameterValueGuids;
	}

	protected void setProcessedParameterValueGuids(final Set<String> processedParameterValueGuids) {
		this.processedParameterValueGuids = processedParameterValueGuids;
	}

	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	public DynamicContentAdapter getDynamicContentAdapter() {
		return dynamicContentAdapter;
	}

	public void setDynamicContentAdapter(final DynamicContentAdapter dynamicContentAdapter) {
		this.dynamicContentAdapter = dynamicContentAdapter;
	}

	public ParameterValueAdapter getParameterValueAdapter() {
		return parameterValueAdapter;
	}

	public void setParameterValueAdapter(final ParameterValueAdapter parameterValueAdapter) {
		this.parameterValueAdapter = parameterValueAdapter;
	}

	public DynamicContentService getDynamicContentService() {
		return dynamicContentService;
	}

	public void setDynamicContentService(final DynamicContentService dynamicContentService) {
		this.dynamicContentService = dynamicContentService;
	}

	public QueryService<ParameterValue> getParameterValueQueryService() {
		return parameterValueQueryService;
	}

	public void setParameterValueQueryService(final QueryService<ParameterValue> parameterValueQueryService) {
		this.parameterValueQueryService = parameterValueQueryService;
	}

	@Override
	public Class<? extends DynamicContentDTO> getDtoClass() {
		return DynamicContentDTO.class;
	}
}
