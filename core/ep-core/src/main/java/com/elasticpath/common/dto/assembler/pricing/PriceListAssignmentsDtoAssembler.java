package com.elasticpath.common.dto.assembler.pricing;

import java.util.Currency;
import java.util.Date;
import java.util.Set;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.ConditionDSLBuilder;

/**
 * Assembler/disassembler for PriceListAssignments DTOs.
 */
public class PriceListAssignmentsDtoAssembler extends AbstractDtoAssembler<PriceListAssignmentsDTO, PriceListAssignment>  {
	
	// Info: lessThan and greaterThan operators come from OperatorDelegate.groovy
	private static final String LESS_THAN = "lessThan";
	
	private static final String GREATER_THAN = "greaterThan";	
	
	private CatalogService catalogService;
	
	private PriceListDescriptorService priceListDescriptorService;
	
	private BeanFactory beanFactory;

	/**
	 * Used for get values for DTO, that stored in selling context.
	 */
	private ConditionDSLBuilder conditionDSLBuilder;

	/**
	 * Assemble a PriceListAssignment from a PriceListAssignmentsDTO. 
	 * 
	 * @param source the source DTO to get data from
	 * @param target the domain object to copy the DTO data to
	 */
	@Override
	public void assembleDomain(final PriceListAssignmentsDTO source,
			final PriceListAssignment target) {
		if (source.getGuid() != null) {
			target.setGuid(source.getGuid());
		}
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setPriority(source.getPriority());
		target.setCatalog(catalogService.findByCode(source.getCatalogGuid()));
		target.setPriceListDescriptor(priceListDescriptorService.findByGuid(source.getPriceListGuid()));
		target.setHidden(source.isHidden());
	}

	/**
	 * Assemble a PriceListAssignmentsDTO from a PriceListAssignment. 
	 * 
	 * @param source the source object to copy the DTO 
	 * @param target the domain DTO to get data to
	 */	
	@Override
	public void assembleDto(final PriceListAssignment source,
			final PriceListAssignmentsDTO target) {
		fillDates(source, target);
		target.setGuid(source.getGuid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setPriority(source.getPriority());
		target.setPriceListGuid(source.getPriceListDescriptor().getGuid());
		target.setPriceListName(source.getPriceListDescriptor().getName());
		target.setCatalogGuid(source.getCatalog().getGuid());
		target.setCatalogName(source.getCatalog().getName());
		target.setPriceListCurrency(Currency.getInstance(source.getPriceListDescriptor().getCurrencyCode()));
		target.setHidden(source.isHidden());
	}

	/**
	 * Fill start / end dates from selling context.
	 * @param source the source object to copy the DTO 
	 * @param target the domain DTO to get data to
	 */
	private void fillDates(final PriceListAssignment source,
			final PriceListAssignmentsDTO target) {
		SellingContext sellingContext = source.getSellingContext();
		
		if (sellingContext != null) {
			ConditionalExpression timeConditionalExpression = sellingContext.getCondition(TagDictionary.DICTIONARY_TIME_GUID);
			if (timeConditionalExpression != null) {
				fillInTimeConditionData(target, timeConditionalExpression);
				
			}
		}
	}
	
	

	private void fillInTimeConditionData(final PriceListAssignmentsDTO target,
			final ConditionalExpression timeConditionalExpression) {
		LogicalOperator logicalOperator = conditionDSLBuilder.getLogicalOperationTree(
				timeConditionalExpression.getConditionString()
				); 
		Set<Condition> conditions = logicalOperator.getConditions();
		for (Condition condition : conditions) {
			String operator = condition.getOperator();
			
			Date conditionDate = new Date();
			conditionDate.setTime((Long) condition.getTagValue());

			if (GREATER_THAN.equals(operator)) {
				target.setStartDate(conditionDate);
			}
			
			if (LESS_THAN.equals(operator)) {				
				target.setEndDate(conditionDate);
			}
		}
	}

	@Override
	public PriceListAssignment getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.PRICE_LIST_ASSIGNMENT);
	}

	@Override
	public PriceListAssignmentsDTO getDtoInstance() {
		return new PriceListAssignmentsDTO();
	}

	/**
	 * Set the catalog service factory to use.
	 * @param catalogService catalog service instance
	 */	
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	/**
	 * Set price list descriptor service to use.
	 * @param priceListDescriptorService  price list descriptor service to set
	 */
	public void setPriceListDescriptorService(
			final PriceListDescriptorService priceListDescriptorService) {
		this.priceListDescriptorService = priceListDescriptorService;
	}

	/**
	 * Set the spring bean factory to use.
	 * @param beanFactory instance
	 */	
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Set conditional dsl builder. 
	 * @param conditionDSLBuilder to set.
	 */
	public void setConditionDSLBuilder(final ConditionDSLBuilder conditionDSLBuilder) {
		this.conditionDSLBuilder = conditionDSLBuilder;
	}
	
	

}
