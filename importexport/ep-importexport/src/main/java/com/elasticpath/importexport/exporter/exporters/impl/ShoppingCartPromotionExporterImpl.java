package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.promotion.PromotionAdapter;
import com.elasticpath.importexport.common.dto.promotion.cart.PromotionDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.rules.RuleService;

/**
 * Prepares the list of promotion rules based on EPQL query and performs export.
 */
public class ShoppingCartPromotionExporterImpl extends AbstractExporterImpl<Rule, PromotionDTO, Long> {

	private RuleService ruleService;

	private List<Long> ruleUidPkList = Collections.emptyList();

	private ImportExportSearcher importExportSearcher;

	private PromotionAdapter promotionAdapter;

	private static final Logger LOG = Logger.getLogger(ShoppingCartPromotionExporterImpl.class);

	/**
	 * {@inheritDoc} throws RuntimeException can be thrown if rule UID list could not be initialized.
	 */
	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		ruleUidPkList = importExportSearcher.searchUids(getContext().getSearchConfiguration(), EPQueryType.PROMOTION);
		LOG.info("The UidPk list for " + ruleUidPkList.size() + " rules are retrieved from database.");		
	}

	@Override
	protected List<Long> getListExportableIDs() {
		final List<Long> promotionsToCheckIfAreDependent = new LinkedList<Long>();
		if (getContext().getDependencyRegistry().supportsDependency(Rule.class)) {
			promotionsToCheckIfAreDependent.addAll(getContext().getDependencyRegistry().getDependentUids(Rule.class)); // add the ones existent
		}
		promotionsToCheckIfAreDependent.addAll(ruleUidPkList);
		// find all promotions on which this ones depend on in a tree order
		final Set<Long> resultSet = retriveDependentRulesAndInsertThemBefore(promotionsToCheckIfAreDependent);
		
		// add initial promotions uids for export
		resultSet.addAll(ruleUidPkList);
		
		return Arrays.asList(resultSet.toArray(new Long[resultSet.size()]));
	}

	/**
	 * The dependent promotions should be exported before, 
	 * so at the import the depend will already be in the database.
	 * 
	 * @param ruleUidList - the rules that need to be chaked that are dependent on other promotions
	 *
	 * @return all the promotion that will be exported in a specific order
	 */
	private Set<Long> retriveDependentRulesAndInsertThemBefore(final List<Long> ruleUidList) {
		return ruleService.retrievePromotionDependencies(new LinkedHashSet<Long>(ruleUidList));
	}

	@Override
	protected List<Rule> findByIDs(final List<Long> subList) {
		// values retrieved in bulk from the database
		final List<Rule> retrivedFronDb = ruleService.findByUids(subList);
		
		// we have to order the values based on the values in subList, due to the promotion dependency 
		// which needs to have the dependent object after the non-depend ones
		final Comparator<Rule> comparator = new Comparator<Rule>() {
			@Override
			public int compare(final Rule rule1, final Rule rule2) {
				Integer postion1 = subList.indexOf(rule1.getUidPk());
				Integer position2 = subList.indexOf(rule2.getUidPk());
				return postion1.compareTo(position2);
			}			
		};

		Collections.sort(retrivedFronDb, comparator);
		return retrivedFronDb;
	}

	@Override
	protected DomainAdapter<Rule, PromotionDTO> getDomainAdapter() {
		return promotionAdapter;
	}

	@Override
	protected Class<? extends PromotionDTO> getDtoClass() {
		return PromotionDTO.class;
	}

	@Override
	public Class< ? >[] getDependentClasses() {
		return new Class< ? >[] {Rule.class};
	}

	@Override
	public JobType getJobType() {
		return JobType.PROMOTION;
	}

	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * @param importExportSearcher the ImportExportSearcher
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	/**
	 * @param promotionAdapter the promotionAdapter to set
	 */
	public void setPromotionAdapter(final PromotionAdapter promotionAdapter) {
		this.promotionAdapter = promotionAdapter;
	}

	@Override
	protected void addDependencies(final List<Rule> objects, final DependencyRegistry dependencyRegistry) {
		final Set<Long> dependentUids = new HashSet<Long>();
		for (Rule rule : objects) {
			dependentUids.add(rule.getUidPk());
		}
		dependencyRegistry.addUidDependencies(Rule.class, dependentUids);
	}
}
