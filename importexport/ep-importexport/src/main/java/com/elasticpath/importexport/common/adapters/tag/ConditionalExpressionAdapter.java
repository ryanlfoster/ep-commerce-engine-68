package com.elasticpath.importexport.common.adapters.tag;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.tag.ConditionalExpressionDTO;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * Adapter used to transform <code>ConditionalExpression</code>
 * to  <code>ConditionalExpressionDTO</code> and vice versa.
 */
public class ConditionalExpressionAdapter extends AbstractDomainAdapterImpl<ConditionalExpression, ConditionalExpressionDTO> {

	@Override
	public ConditionalExpression createDomainObject() {
		return getBean(ContextIdNames.CONDITIONAL_EXPRESSION);
	}

	@Override
	public ConditionalExpressionDTO createDtoObject() {
		return new ConditionalExpressionDTO();
	}

	/**
	 * {@inheritDoc} Note: does not set Named field for target DTO.
	 */
	public void populateDTO(final ConditionalExpression source, final ConditionalExpressionDTO target) {
		target.setGuid(source.getGuid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setConditionString(source.getConditionString());
		target.setDictionaryGuid(source.getTagDictionaryGuid());
	}

	/**
	 * {@inheritDoc} Note: Tag Dictionary is assumed to exist.
	 */
	public void populateDomain(final ConditionalExpressionDTO source, final ConditionalExpression target) {
		target.setGuid(source.getGuid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setConditionString(source.getConditionString());
		target.setTagDictionaryGuid(source.getDictionaryGuid());
		target.setNamed(source.isNamed());
	}	
}

