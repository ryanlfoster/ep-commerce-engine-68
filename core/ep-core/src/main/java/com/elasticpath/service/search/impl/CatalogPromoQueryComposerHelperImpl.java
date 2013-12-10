package com.elasticpath.service.search.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.search.CatalogPromoQueryComposerHelper;
import com.elasticpath.service.search.query.FilteredSearchCriteria;
import com.elasticpath.service.search.query.FilteredSearchCriteria.Relationship;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Default implementation of {@link CatalogPromoQueryComposerHelper}.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class CatalogPromoQueryComposerHelperImpl implements CatalogPromoQueryComposerHelper {
	
	private ElasticPath elasticPath;
	
	private CategoryService categoryService;

	/**
	 * Constructs a search criteria for affected products by the given collection of promotion
	 * rules.
	 * <p>
	 * Each rule has the following criteria (and is ORed with each other rule):
	 * </p>
	 * 
	 * <pre>
	 * [(cond, cond excep) AND/OR (cond, cond excep) ... ] AND [(act, act excep) AND (act, act excep) ... ]
	 * </pre>
	 * 
	 * <p>
	 * Actions are only added if they modify a specific product/sku price. All exceptions are
	 * being dealt with via filters on the filtered search criteria.
	 * </p>
	 * 
	 * @param promoRules a collection of promotion rules to construct criteria for
	 * @return search criteria that represents the given collection of promotion rules
	 */
	public FilteredSearchCriteria< ? > constructSearchCriteria(final Collection<Rule> promoRules) {
		FilteredSearchCriteria<FilteredSearchCriteria<FilteredSearchCriteria<ProductSearchCriteria>>> allRulesCriteria =
			getNewCriteriaInstance(ContextIdNames.FILTERED_SEARCH_CRITERIA);
		allRulesCriteria.setRelationship(Relationship.OR);
		allRulesCriteria.setFuzzySearchDisabled(true);
		
		for (Rule rule : promoRules) {
			if (rule.getRuleSet().getScenario() != RuleScenarios.CATALOG_BROWSE_SCENARIO) {
				continue;
			}
			
			// filter the actions so that we have only the minimum required criteria
			final Collection<RuleAction> actions = new ArrayList<RuleAction>(rule.getActions().size());
			for (RuleAction action : rule.getActions()) {
				if (changesCatalogViewProductPrice(action)) {
					actions.add(action);
				}
			}
			final String catalogCode = rule.getCatalog().getCode();
			FilteredSearchCriteria<ProductSearchCriteria> actionSearchCriteria = 
				createRuleElementSearchCriteria(actions, Relationship.AND, catalogCode);
			
			// now do the same for conditions, all conditions are used
			Relationship relationship;
			if (rule.getConditionOperator() == Rule.AND_OPERATOR) {
				relationship = Relationship.AND;
			} else {
				relationship = Relationship.OR;
			}

			// create the criteria for the entire rule
			FilteredSearchCriteria<FilteredSearchCriteria<ProductSearchCriteria>> ruleCriteria =
				getNewCriteriaInstance(ContextIdNames.FILTERED_SEARCH_CRITERIA);
			ruleCriteria.setRelationship(Relationship.AND);
			ruleCriteria.addCriteria(actionSearchCriteria);
			
			// The conditions may not be present in the rule, need to check if we need to add criteria for them
			if (CollectionUtils.isNotEmpty(rule.getConditions())) {
				FilteredSearchCriteria<ProductSearchCriteria> conditionSearchCriteria = 
					createRuleElementSearchCriteria(rule.getConditions(), relationship, catalogCode);
	
				ruleCriteria.addCriteria(conditionSearchCriteria);
			}
			
			// add to the criterias of rules
			allRulesCriteria.addCriteria(ruleCriteria);
		}
		if (allRulesCriteria.isEmpty()) {
			allRulesCriteria.setMatchAll(true);
		}
		return allRulesCriteria;
	}
	
	private FilteredSearchCriteria<ProductSearchCriteria> createRuleElementSearchCriteria(
			final Collection< ? extends RuleElement> elements, 
			final Relationship relationship, final String catalogCode) {
		final FilteredSearchCriteria<ProductSearchCriteria> searchCriteria = getNewCriteriaInstance(ContextIdNames.FILTERED_SEARCH_CRITERIA);
		searchCriteria.setRelationship(relationship);

		for (RuleElement element : elements) {
			if (!element.appliesInScenario(RuleScenarios.CATALOG_BROWSE_SCENARIO)) {
				// only care if it's in a browse scenario
				continue;
			}

			final ProductSearchCriteria specificCriteria = createSpecificElementCriteria(element, catalogCode);
			final Collection<ProductSearchCriteria> filters = createExceptionCriterias(element.getExceptions(), catalogCode);

			if (filters.isEmpty()) {
				searchCriteria.addCriteria(specificCriteria);
			} else {
				final ProductSearchCriteria[] conditionFiltersArr = new ProductSearchCriteria[filters.size()];
				searchCriteria.addCriteria(specificCriteria, filters.toArray(conditionFiltersArr));
			}
		}
		if (searchCriteria.isEmpty()) {
			searchCriteria.setMatchAll(true);
		}
		return searchCriteria;
	}
	
	/**
	 * Creates a {@link ProductSearchCriteria} which is used to search for the given rule element.
	 *
	 * @param element the input element
	 * @param catalogCode the catalog code 
	 * @return a search criteria to search for the given element
	 */
	@SuppressWarnings("fallthrough")
	protected ProductSearchCriteria createSpecificElementCriteria(final RuleElement element, final String catalogCode) {
		final ProductSearchCriteria conditionCriteria = getNewCriteriaInstance(ContextIdNames.PRODUCT_SEARCH_CRITERIA);
		conditionCriteria.setCatalogCode(catalogCode);
		
		switch (element.getElementType().getOrdinal()) {
		case RuleElementType.BRAND_CONDITION_ORDINAL:
			final String brandCode = getFirstParamValueWithKey(element.getParameters(), RuleParameter.BRAND_CODE_KEY);
			if ("ANY".equals(brandCode)) {
				conditionCriteria.setMatchAll(true);
			} else {
				conditionCriteria.setBrandCode(brandCode);
			}
			break;
        case RuleElementType.PRODUCT_CATEGORY_CONDITION_ORDINAL:
        	String code = getFirstParamValueWithKey(element.getParameters(), RuleParameter.CATEGORY_CODE_KEY);
			if ("0".equals(code)) {
				conditionCriteria.setMatchAll(true);
				break;
			}
			final Long uid = categoryService.findUidByCompoundGuid(code);
			
			final Set<Long> uids = new HashSet<Long>();
			uids.add(uid);
			conditionCriteria.setOnlyWithinDirectCategory(false);
			conditionCriteria.setDirectCategoryUid(uid);
			conditionCriteria.setAncestorCategoryUids(uids);
			break;
		case RuleElementType.PRODUCT_PERCENT_DISCOUNT_ACTION_ORDINAL:
		case RuleElementType.PRODUCT_AMOUNT_DISCOUNT_ACTION_ORDINAL:
			// these may contain exceptions which filter the given products
			conditionCriteria.setMatchAll(true);
			break;
		case RuleElementType.PRODUCT_CONDITION_ORDINAL:
			code = getFirstParamValueWithKey(element.getParameters(), RuleParameter.PRODUCT_CODE_KEY);
			if ("0".equals(code)) {
				conditionCriteria.setMatchAll(true);
				break;
			}
			conditionCriteria.setProductCode(code);
			break;
		case RuleElementType.CATALOG_SKU_AMOUNT_DISCOUNT_ACTION_ORDINAL:
		case RuleElementType.CATALOG_SKU_PERCENT_DISCOUNT_ACTION_ORDINAL:
			final String skuCode = getFirstParamValueWithKey(element.getParameters(), RuleParameter.SKU_CODE_KEY);
			if ("0".equals(skuCode)) {
				conditionCriteria.setMatchAll(true);
				break;
			}
			conditionCriteria.setProductSku(skuCode);
			break;
		case RuleElementType.CATALOG_CURRENCY_AMOUNT_DISCOUNT_ACTION_ORDINAL:
		case RuleElementType.CATALOG_CURRENCY_PERCENT_DISCOUNT_ACTION_ORDINAL:
			// don't care about the currency as indexers will update all currencies
			conditionCriteria.setMatchAll(true);
			break;
		default:
			// don't set match all here so that if we don't cover it, empty search criteria
			// exception will be thrown in the query composer (so it's easy to verify where the
			// bug has originated)
			// conditionCriteria.setMatchAll(true);
			// skip over other types of elements
			break;
		}
		
		return conditionCriteria;
	}
	
	private boolean changesCatalogViewProductPrice(final RuleAction action) {
		final String[] parameters = action.getParameterKeys();
		if (parameters != null) {
			for (String parameter : parameters) {
				if (RuleParameter.DISCOUNT_AMOUNT_KEY.equals(parameter)) {
					return true;
				} else if (RuleParameter.DISCOUNT_PERCENT_KEY.equals(parameter)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private <T extends SearchCriteria> T getNewCriteriaInstance(final String beanId) {
		T searchCriteria = elasticPath.getBean(beanId);
		// These aren't required for the search, but required by the service
		searchCriteria.setLocale(Locale.US);
		searchCriteria.setCurrency(Currency.getInstance(Locale.US));
		return searchCriteria;
	}

	private Collection<ProductSearchCriteria> createExceptionCriterias(final Collection<RuleException> exceptions, final String catalogCode) {
		final Collection<ProductSearchCriteria> exceptionCriterias = new ArrayList<ProductSearchCriteria>();
		for (RuleException exception : exceptions) {
			final ProductSearchCriteria searchCriteria = getNewCriteriaInstance(ContextIdNames.PRODUCT_SEARCH_CRITERIA);
			searchCriteria.setCatalogCode(catalogCode);

			exceptionCriterias.add(searchCriteria);
			
			switch (exception.getExceptionType().getOrdinal()) {
			case RuleExceptionType.CATEGORY_EXCEPTION_ORDINAL:
				String code = getFirstParamValueWithKey(exception.getParameters(), RuleParameter.CATEGORY_CODE_KEY);
				searchCriteria.setOnlyWithinDirectCategory(true);
				searchCriteria.setDirectCategoryUid(categoryService.findUidByCompoundGuid(code));
				break;
			case RuleExceptionType.PRODUCT_EXCEPTION_ORDINAL:
				code = getFirstParamValueWithKey(exception.getParameters(), RuleParameter.PRODUCT_CODE_KEY);
				searchCriteria.setProductCode(code);
				break;
			case RuleExceptionType.SKU_EXCEPTION_ORDINAL:
				searchCriteria.setProductSku(getFirstParamValueWithKey(exception.getParameters(), RuleParameter.SKU_CODE_KEY));
				break;
			default:
				// should never get here
				throw new EpUnsupportedOperationException("Not implemented.");
			}
		}
		
		return exceptionCriterias;
	}
	
	private String getFirstParamValueWithKey(final Collection<RuleParameter> parameters, final String key) {
		for (RuleParameter param : parameters) {
			if (param.getKey().equals(key)) {
				return param.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Sets the {@link ElasticPath} instance to use.
	 *
	 * @param elasticPath the {@link ElasticPath} instance to use
	 */
	public void setElasticPath(final ElasticPath elasticPath) {
		this.elasticPath = elasticPath;
	}
	
	/**
	 * Sets the category service.
	 * 
	 * @param categoryService the category service
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}
}
