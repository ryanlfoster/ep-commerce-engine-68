package com.elasticpath.importexport.common.adapters.promotion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.promotion.rule.ActionDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.AndDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.BooleanComponentDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.ConditionDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.ConditionsDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.OrDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.RuleDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>RuleElement</code> and <code>RuleDTO</code> objects.
 */
public class RuleAdapter extends AbstractDomainAdapterImpl<Rule, RuleDTO> {

	private DomainAdapter<RuleElement, ConditionDTO> conditionAdapter;

	/** String constant for ShoppingCartPromotion type. */
	public static final String SHOPPING_CART_PROMOTION = "ShoppingCartPromotion";

	/** String constant for CatalogPromotion type. */
	public static final String CATALOG_PROMOTION = "CatalogPromotion";

	@Override
	public void populateDTO(final Rule source, final RuleDTO target) {
		target.setCode(source.getCode());

		target.setConditions(createConditionsDTO(source));
		target.setActions(createActionDTOList(source));
	}

	/**
	 * Creates populated ConditionsDTO from Rule eligibilities and conditions.
	 * 
	 * @param source rule containing conditions and eligibilities
	 * @return ConditionsDTO containing conditions and eligibilities
	 */
	ConditionsDTO createConditionsDTO(final Rule source) {
		final ConditionsDTO conditionsDto = new ConditionsDTO();

		final List<RuleCondition> conditions = new ArrayList<RuleCondition>(source.getConditions());
		final BooleanComponentDTO limitedConditionDTO = retrieveLimitedUsageCondition(conditions);
		final BooleanComponentDTO couponCodeDTO = retrieveCouponCodeCondition(conditions);

		if (!conditions.isEmpty()) {
			final BooleanComponentDTO eligibilitiesAndConditionsDTO = new AndDTO();
			eligibilitiesAndConditionsDTO.setComponents(Arrays.asList(createConditionComposition(conditions, source.getConditionOperator())));
			conditionsDto.setConditionsComponent(eligibilitiesAndConditionsDTO);
		}

		addCouponCondition(conditionsDto, couponCodeDTO);
		addLimitedCondition(conditionsDto, limitedConditionDTO);

		return conditionsDto;
	}

	private void addLimitedCondition(final ConditionsDTO conditionsDto, final BooleanComponentDTO limitedConditionDTO) {
		if (limitedConditionDTO != null) {
			final BooleanComponentDTO highLevelAndDTO = new AndDTO();
			highLevelAndDTO.setComponents(Arrays.asList(
					limitedConditionDTO,
					conditionsDto.getConditionsComposite()));
			conditionsDto.setConditionsComponent(highLevelAndDTO);
		}
	}

	private void addCouponCondition(final ConditionsDTO conditionsDto, final BooleanComponentDTO couponConditionDTO) {
		if (couponConditionDTO != null) {
			final BooleanComponentDTO highLevelAndDTO = new AndDTO();
			highLevelAndDTO.setComponents(Arrays.asList(
					couponConditionDTO,
					conditionsDto.getConditionsComposite()));
			conditionsDto.setConditionsComponent(highLevelAndDTO);
		}
	}

	/**
	 * Finds <code>RuleCondition</code> for limited usage and removes it from the list of conditions.
	 * 
	 * @param conditions the list of <code>RuleCondition</code> objects
	 * @return populated <code>ConditionDTO</code> based on limited usage condition if exists
	 */
	BooleanComponentDTO retrieveLimitedUsageCondition(final List<RuleCondition> conditions) {
		for (RuleCondition condition : conditions) {
			if (RuleElementType.LIMITED_USAGE_PROMOTION_CONDITION.equals(condition.getElementType())) {
				final ConditionDTO conditionDto = new ConditionDTO();
				conditionAdapter.populateDTO(condition, conditionDto);
				conditions.remove(condition);
				return conditionDto;
			}
		}
		return null;
	}

	/**
	 * Finds <code>RuleCondition</code> for coupon code and removes it from the list of conditions.
	 * 
	 * @param conditions the list of <code>RuleCondition</code> objects
	 * @return populated <code>ConditionDTO</code> based on coupon code condition if exists
	 */
	BooleanComponentDTO retrieveCouponCodeCondition(final List<RuleCondition> conditions) {
		for (RuleCondition condition : conditions) {
			if (RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION.equals(condition.getElementType())) {
				final ConditionDTO conditionDto = new ConditionDTO();
				conditionAdapter.populateDTO(condition, conditionDto);
				conditions.remove(condition);
				return conditionDto;
			}
		}
		return null;
	}

	/**
	 * Recursively fills logical DTO composition from the set of <code>RuleElement</code> objects.
	 * 
	 * @param ruleElements set of <code>RuleElement</code> domain objects
	 * @param makeConjunction compose components with AND operator if true, with OR otherwise
	 * @return logical composition of <code>ConditionDTO</code> objects united by OR and AND operators
	 */
	BooleanComponentDTO createConditionComposition(final List< ? extends RuleElement> ruleElements, final boolean makeConjunction) {

		if (ruleElements.isEmpty()) {
			throw new PopulationRuntimeException("IE-10707");
		}

		if (ruleElements.size() == 1) {
			return retrieveCondition(ruleElements);
		} else if (ruleElements.size() == 2) {
			final BooleanComponentDTO compositionDto = getBooleanClauseDTO(makeConjunction);
			compositionDto.setComponents(Arrays.asList(retrieveCondition(ruleElements), retrieveCondition(ruleElements)));
			return compositionDto;
		}

		final BooleanComponentDTO compositionDto = getBooleanClauseDTO(makeConjunction);

		final BooleanComponentDTO conditionDto = retrieveCondition(ruleElements);

		compositionDto.setComponents(Arrays.asList(conditionDto,
				createConditionComposition(ruleElements, makeConjunction)));

		return compositionDto;
	}

	/**
	 * Transforms first <code>RuleElement</code> into <code>ConditionDTO</code> and removes it from the set .
	 * 
	 * @param ruleElements the set of <code>RuleElement</code> objects
	 * @return populated <code>ConditionDTO</code> object
	 */
	BooleanComponentDTO retrieveCondition(final List< ? extends RuleElement> ruleElements) {
		final ConditionDTO conditionDto = new ConditionDTO();
		final RuleElement condition = ruleElements.get(0);
		ruleElements.remove(0);
		conditionAdapter.populateDTO(condition, conditionDto);
		return conditionDto;
	}

	/**
	 * Creates <code>BooleanComponentDTO</code> by logical operator.
	 * 
	 * @param makeConjunction makes conjunction operator AND if true, OR otherwise
	 * @return <code>BooleanComponentDTO</code> instance
	 */
	BooleanComponentDTO getBooleanClauseDTO(final boolean makeConjunction) {
		if (makeConjunction) {
			return new AndDTO();
		}
		return new OrDTO();
	}

	/**
	 * Creates populated RuleElementDTO List from the Set of RuleElements. TODO: when domain object allow multiple promotions for one rule this
	 * adapter will be changed to support it.
	 * 
	 * @param source set of RuleElements
	 * @return List of RuleElementDTO
	 */
	List<ActionDTO> createActionDTOList(final Rule source) {
		final ActionDTO actionDto = new ActionDTO();
		
		if (source.getRuleSet().getScenario() == RuleScenarios.CART_SCENARIO) {
			actionDto.setType(SHOPPING_CART_PROMOTION);
		} else {
			actionDto.setType(CATALOG_PROMOTION);
		}
		actionDto.setCode(source.getCode());

		return Arrays.asList(actionDto);
	}

	@Override
	public void populateDomain(final RuleDTO source, final Rule target) {
		final BooleanComponentDTO composite = source.getConditions().getConditionsComposite();
		populateRuleElements(composite.getComponents().get(0), target);
		if (composite.getComponents().size() > 1) {
				populateRuleElements(composite.getComponents().get(1), target);
		}
	}

	/**
	 * Delegate population of rule elements to <code>BooleanComponentDTO</code> composite.
	 * 
	 * @param source boolean operator represented by DTO component
	 * @param target <code>Rule</code> instance to populate
	 */
	void populateRuleElements(final BooleanComponentDTO source, final Rule target) {
		target.setConditionOperator(source.getCompositeOperator());
		source.populateDomainObject(target, conditionAdapter);
	}

	/**
	 * Sets ConditionAdapter.
	 * 
	 * @param conditionAdapter the ConditionAdapter instance
	 */
	public final void setConditionAdapter(final DomainAdapter<RuleElement, ConditionDTO> conditionAdapter) {
		this.conditionAdapter = conditionAdapter;
	}

	@Override
	public RuleDTO createDtoObject() {
		return new RuleDTO();
	}
}
