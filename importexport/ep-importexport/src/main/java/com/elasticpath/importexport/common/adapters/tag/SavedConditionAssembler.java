package com.elasticpath.importexport.common.adapters.tag;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.importexport.common.dto.tag.ConditionalExpressionDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.TagDictionaryService;

/**
 * Assembler used to transform saved <code>ConditionalExpression</code>
 * to saved <code>ConditionalExpressionDTO</code>and vice versa.
 */
public class SavedConditionAssembler extends AbstractDtoAssembler<ConditionalExpressionDTO, ConditionalExpression> {

	private BeanFactory beanFactory;
	private TagDictionaryService tagDictionaryService;
	private ConditionalExpressionAdapter conditionalExpressionAdapter;

	@Override
	public ConditionalExpression getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.CONDITIONAL_EXPRESSION);
	}

	@Override
	public ConditionalExpressionDTO getDtoInstance() {
		return new ConditionalExpressionDTO();
	}

	@Override
	public void assembleDto(final ConditionalExpression source, final ConditionalExpressionDTO target) {
		
		conditionalExpressionAdapter.populateDTO(source, target);
	}

	/**
	 * {@inheritDoc} Note: Tag Dictionary is assumed to exist.
	 */
	@Override
	public void assembleDomain(final ConditionalExpressionDTO source, final ConditionalExpression target) {
		
		String guid = source.getGuid();
		String dictionaryGuid = source.getDictionaryGuid();

		// check each condition's dictionary_guid if exists
		TagDictionary foundTagDictionary = tagDictionaryService.findByGuid(dictionaryGuid);
		if (foundTagDictionary == null) {
			throw new PopulationRuntimeException("IE-40502", source.getGuid(), guid, dictionaryGuid);
		}
		source.setDictionaryGuid(foundTagDictionary.getGuid());
		source.setNamed(true);
		conditionalExpressionAdapter.populateDomain(source, target);
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * Sets the tag dictionary service.
	 * 
	 * @param tagDictionaryService value to set
	 */
	public void setTagDictionaryService(final TagDictionaryService tagDictionaryService) {
		this.tagDictionaryService = tagDictionaryService;
	}

	/**
	 * Sets the conditional expression adapter.
	 * 
	 * @param conditionalExpressionAdapter value to set
	 */
	public void setConditionalExpressionAdapter(final ConditionalExpressionAdapter conditionalExpressionAdapter) {
		this.conditionalExpressionAdapter = conditionalExpressionAdapter;
	}
}
