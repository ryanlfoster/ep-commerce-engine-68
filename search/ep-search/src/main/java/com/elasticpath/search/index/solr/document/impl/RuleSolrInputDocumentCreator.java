package com.elasticpath.search.index.solr.document.impl;

import java.util.Date;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.ConditionDSLBuilder;

/**
 * Creates a {@link SolrInputDocument} for an {@link Rule}.
 */
public class RuleSolrInputDocumentCreator extends AbstractDocumentCreatingTask<Rule> {

	// Info: lessThan and greaterThan operators come from
	// OperatorDelegate.groovy
	private static final String LESS_THAN = "lessThan";

	private static final String GREATER_THAN = "greaterThan";

	/**
	 * Used for get start / end date values for shopping cart promotions, that stored in selling context.
	 */
	private ConditionDSLBuilder conditionDSLBuilder;

	/**
	 * Creates a {@link SolrInputDocument} based on the {@link Rule}.
	 * 
	 * @return the {@link SolrInputDocument}.
	 */
	public SolrInputDocument createDocument() {
		if (getEntity() == null) {
			return null;
		}

		final SolrInputDocument document = new SolrInputDocument();
		addFieldToDocument(document, SolrIndexConstants.OBJECT_UID, getAnalyzer().analyze(String.valueOf(getEntity().getUidPk())));
		addFieldToDocument(document, SolrIndexConstants.PROMOTION_NAME, getAnalyzer().analyze(getEntity().getName()));
		addFieldToDocument(document, SolrIndexConstants.PROMOTION_STATE, getAnalyzer().analyze(String.valueOf(getEntity().isEnabled())));

		if (getEntity().getRuleSet() != null) {
			addFieldToDocument(document, SolrIndexConstants.PROMOTION_RULESET_UID, getAnalyzer().analyze(getEntity().getRuleSet().getUidPk()));
			addFieldToDocument(document, SolrIndexConstants.PROMOTION_RULESET_NAME, getAnalyzer().analyze(getEntity().getRuleSet().getName()));
		}

		addFieldToDocument(document, SolrIndexConstants.START_DATE, getAnalyzer().analyze(getRuleStartDate(getEntity())));
		addFieldToDocument(document, SolrIndexConstants.END_DATE, getAnalyzer().analyze(getRuleEndDate(getEntity())));

		if (getEntity().getCatalog() == null) {
			addFieldToDocument(document, SolrIndexConstants.STORE_CODE, getAnalyzer().analyze(
					getAnalyzer().analyze(getEntity().getStore().getCode())));
		} else {
			addFieldToDocument(document, SolrIndexConstants.CATALOG_UID, getAnalyzer().analyze(
					getAnalyzer().analyze(getEntity().getCatalog().getUidPk())));
			addFieldToDocument(document, SolrIndexConstants.CATALOG_CODE, getAnalyzer().analyze(
					getAnalyzer().analyze(getEntity().getCatalog().getCode())));
		}

		return document;
	}

	private Date getRuleStartDate(final Rule rule) {
		if (rule.getSellingContext() != null) {
			return getTimeConditionDates(rule).getFirst();
		}
		return rule.getStartDate();
	}

	private Date getRuleEndDate(final Rule rule) {
		if (rule.getSellingContext() != null) {
			return getTimeConditionDates(rule).getSecond();
		}
		return rule.getEndDate();
	}

	private Pair<Date, Date> getTimeConditionDates(final Rule rule) {

		final SellingContext sellingContext = rule.getSellingContext();
		if (sellingContext != null) {
			ConditionalExpression timeConditionalExpression = sellingContext.getCondition(TagDictionary.DICTIONARY_TIME_GUID);

			if (timeConditionalExpression != null) {
				Date startDate = null;
				Date endDate = null;

				LogicalOperator logicalOperator = getConditionDSLBuilder().getLogicalOperationTree(timeConditionalExpression.getConditionString());

				Set<Condition> conditions = logicalOperator.getConditions();
				for (Condition condition : conditions) {
					String operator = condition.getOperator();

					Date conditionDate = new Date();
					conditionDate.setTime((Long) condition.getTagValue());

					if (GREATER_THAN.equals(operator)) {
						startDate = conditionDate;
					}

					if (LESS_THAN.equals(operator)) {
						endDate = conditionDate;
					}
				}
				return new Pair<Date, Date>(startDate, endDate);
			}
		}
		return new Pair<Date, Date>(null, null);
	}

	/**
	 * @param conditionDSLBuilder the conditionDSLBuilder to set
	 */
	public void setConditionDSLBuilder(final ConditionDSLBuilder conditionDSLBuilder) {
		this.conditionDSLBuilder = conditionDSLBuilder;
	}

	/**
	 * @return the conditionDSLBuilder
	 */
	public ConditionDSLBuilder getConditionDSLBuilder() {
		return conditionDSLBuilder;
	}
}
