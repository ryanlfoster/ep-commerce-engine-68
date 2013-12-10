package com.elasticpath.importexport.common.adapters.promotion;

import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.importexport.common.dto.promotion.rule.ConditionDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br> 
 * It is responsible for data transformation between <code>RuleElement</code> and <code>RuleElementDTO</code> objects.
 */
public class ConditionAdapter extends AbstractElementAdapter<ConditionDTO> {

	@Override
	public void populateDTO(final RuleElement source, final ConditionDTO target) {
		target.setKind(source.getKind());
		target.setType(source.getType());

		target.setParameters(createElementParameterDTOList(source.getParameters()));
		target.setExceptions(createElementExceptionDTOList(source.getExceptions()));		
	}

	@Override
	public void populateDomain(final ConditionDTO source, final RuleElement target) {
		// ruleAction.setKind() should not be used
		if (source.getKind() == null || !source.getKind().equals(target.getKind())) {
			throw new PopulationRuntimeException("IE-10701", source.getType());
		}

		target.setParameters(createRuleParameterSet(source.getParameters()));
		target.setExceptions(createRuleExceptionSet(source.getExceptions()));
	}

	@Override
	public ConditionDTO createDtoObject() {
		return new ConditionDTO();
	}
}
