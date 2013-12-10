package com.elasticpath.importexport.common.adapters.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.tag.ConditionalExpressionDTO;
import com.elasticpath.importexport.common.dto.tag.SellingContextDTO;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;

/**
 * Adapter used to transform <code>SellingContext</code>
 * to  <code>SellingContextDTO</code> and vice versa.
 */
public class SellingContextAdapter extends AbstractDomainAdapterImpl<SellingContext, SellingContextDTO> {

	private SellingContextService sellingContextService;
	private TagConditionService tagConditionService;
	private ConditionalExpressionAdapter conditionalExpressionAdapter;

	@Override
	public void populateDTO(final SellingContext source, final SellingContextDTO target) {
		target.setGuid(source.getGuid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setPriority(source.getPriority());
		populateChildConditionDtos(source, target);

	}

	private void populateChildConditionDtos(final SellingContext sellingContext, final SellingContextDTO target) {
		Map<String, ConditionalExpression> conditions = sellingContext.getConditions();
		List<ConditionalExpressionDTO> conditionDtos = new ArrayList<ConditionalExpressionDTO>();
		List<String> savedConditionDtoGuids = new ArrayList<String>();
		
		for (ConditionalExpression condition : conditions.values()) {
			if (condition.isNamed()) {
				savedConditionDtoGuids.add(condition.getGuid());
			} else {
				ConditionalExpressionDTO conditionalExpressionDto = new ConditionalExpressionDTO();
				conditionalExpressionAdapter.populateDTO(condition, conditionalExpressionDto);
				conditionDtos.add(conditionalExpressionDto);				
			}
		}
		
		if (!conditionDtos.isEmpty()) {
			target.setConditions(conditionDtos);
		}
		if (!savedConditionDtoGuids.isEmpty()) {
			target.setSavedConditionGuids(savedConditionDtoGuids);
		}
	}

	@Override
	public void populateDomain(final SellingContextDTO source, final SellingContext target) {
		target.setGuid(source.getGuid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setPriority(source.getPriority());
		populateChildConditions(source, target);
	}
	
	private void populateChildConditions(final SellingContextDTO source, final SellingContext target) {
		if (source.getConditions() != null &&  !source.getConditions().isEmpty()) {
			for (ConditionalExpressionDTO conditionalExpressionDto : source.getConditions()) {
				ConditionalExpression condition = getConditionalExpression(conditionalExpressionDto.getGuid());
				conditionalExpressionDto.setNamed(false);
				conditionalExpressionAdapter.populateDomain(conditionalExpressionDto, condition);
				target.setCondition(condition.getTagDictionaryGuid(), condition);
			}
		}
		
		if (source.getSavedConditionGuids() != null &&  !source.getSavedConditionGuids().isEmpty()) {
			for (String savedConditionGuid : source.getSavedConditionGuids()) {
				ConditionalExpression condition = getConditionalExpression(savedConditionGuid);
				target.setCondition(condition.getTagDictionaryGuid(), condition);
			}
		}
	}
	
	
	/**
	 * Returns domain object.
	 * @param guid - guid of the selling context to be returned
	 * @return returned selling context.
	 */
	public SellingContext getDomainObject(final String guid) {
		SellingContext sellingContext = sellingContextService.getByGuid(guid);
		if (sellingContext == null) {
			sellingContext = getBean(ContextIdNames.SELLING_CONTEXT);
		}
		return sellingContext;
	}
		
	@Override
	public SellingContextDTO createDtoObject() {
		return new SellingContextDTO();
	}
	
	/**
	 * Injects selling context service.
	 * 
	 * @param sellingContextService  selling context service to be set.
	 */
	public void setSellingContextService(final SellingContextService sellingContextService) {
		this.sellingContextService = sellingContextService;
	}

	/**
	 * Injects conditional ExpressionAdapter adapter. 
	 * 
	 * @param conditionalExpressionAdapter conditionalExpressionAdapter to be set.
	 */
	public void setConditionalExpressionAdapter(final ConditionalExpressionAdapter conditionalExpressionAdapter) {
		this.conditionalExpressionAdapter = conditionalExpressionAdapter;
	}
	
	/**
	 * Returns domain object. If object with specified guid does not exist, new one is created.
	 * 
	 * @param guid - guid of the object to be returned.
	 *
	 * @return - domain object.
	 */
	private ConditionalExpression getConditionalExpression(final String guid) {
		ConditionalExpression conditionalExpression = tagConditionService.findByGuid(guid);
		if (conditionalExpression == null) {
			conditionalExpression = getBean(ContextIdNames.CONDITIONAL_EXPRESSION);
		}
		return conditionalExpression;
	}
	
	/**
	 * Injects tag condition service.
	 * 
	 * @param tagConditionService - tag condition service to be injected.
	 */
	public void setTagConditionService(final TagConditionService tagConditionService) {
		this.tagConditionService = tagConditionService;
	}
}