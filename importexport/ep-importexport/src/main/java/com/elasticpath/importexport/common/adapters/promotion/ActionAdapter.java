package com.elasticpath.importexport.common.adapters.promotion;

import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.importexport.common.dto.promotion.cart.ActionDTO;

/**
 * Maps <code>RuleElement</code> onto <code>ActionDTO</code>.
 */
public class ActionAdapter extends AbstractElementAdapter<ActionDTO> {

	@Override
	public void populateDTO(final RuleElement source, final ActionDTO target) {
		target.setType(source.getType());

		target.setParameters(createElementParameterDTOList(source.getParameters()));
		target.setExceptions(createElementExceptionDTOList(source.getExceptions()));
	}

	@Override
	public void populateDomain(final ActionDTO source, final RuleElement target) {
		target.setType(source.getType());

		target.setParameters(createRuleParameterSet(source.getParameters()));
		target.setExceptions(createRuleExceptionSet(source.getExceptions()));		
	}

	@Override
	public ActionDTO createDtoObject() {
		return new ActionDTO();
	}
}
