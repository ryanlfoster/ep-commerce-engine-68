package com.elasticpath.importexport.common.adapters.pricing;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.importexport.common.adapters.tag.SellingContextAdapter;
import com.elasticpath.importexport.common.dto.pricing.PriceListAssignmentDTO;
import com.elasticpath.importexport.common.dto.tag.ConditionalExpressionDTO;
import com.elasticpath.importexport.common.dto.tag.SellingContextDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.TagDictionaryService;

/**
 * Assembler for <code>PriceListAssignment</code>s.
 */
public class PriceListAssignmentAssembler extends AbstractDtoAssembler<PriceListAssignmentDTO, PriceListAssignment>  {
	
	private BeanFactory beanFactory;
	private CatalogService catalogService;
	private PriceListDescriptorService priceListDescriptorService;
	private SellingContextAdapter sellingContextAdapter;
	private TagDictionaryService tagDictionaryService;

	
	/**
	 * Assemble a PriceListAssignmentsDTO from a PriceListAssignment. 
	 * 
	 * @param source the source object to copy the DTO 
	 * @param target the domain DTO to get data to
	 */	
	@Override
	public void assembleDto(final PriceListAssignment source, final PriceListAssignmentDTO target) {
		target.setGuid(source.getGuid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setPriority(source.getPriority());
		target.setPriceListGuid(source.getPriceListDescriptor().getGuid());
		target.setCatalogGuid(source.getCatalog().getGuid());
		
		// Copied from DynamicContentDeliveryAssembler.java
		SellingContext sellingContext = source.getSellingContext();
		if (sellingContext != null) {
			SellingContextDTO scDto = sellingContextAdapter.createDtoObject();
			sellingContextAdapter.populateDTO(sellingContext, scDto);
			target.setSellingContext(scDto);
		}
	}
	
	
	/**
	 * Assemble a PriceListAssignment from a PriceListAssignmentsDTO. 
	 * 
	 * @param source the source DTO to get data from
	 * @param target the domain object to copy the DTO data to
	 */
	@Override
	public void assembleDomain(final PriceListAssignmentDTO source, final PriceListAssignment target) {
		if (source.getGuid() != null) {
			target.setGuid(source.getGuid());
		}
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setPriority(source.getPriority());
		target.setCatalog(getCatalog(source.getCatalogGuid()));
		target.setPriceListDescriptor(getPriceListDescriptor(source.getPriceListGuid()));
		
		// Copied from DynamicContentDeliveryAssembler.java
		SellingContextDTO sellingContextDTO = source.getSellingContext();
		if (sellingContextDTO != null) {
			validateConditions(source.getGuid(), sellingContextDTO);
			target.setSellingContext(getSellingContext(sellingContextDTO));
		}
	}
	
	
	/**
	 * Returns Catalog domain object that matches the <code>catalogCode</code> passed in.
	 * 
	 * @param catalogCode unique id for Catalog domain object
	 */
	private Catalog getCatalog(final String catalogCode)  {
		Catalog catalog = catalogService.findByCode(catalogCode);
		if (catalog == null) {
			throw new PopulationRuntimeException("IE-10103", catalogCode);
		}
		return catalog;
	}
	
	/**
	 * Returns Price List Descriptor domain object that matches the <code>priceListDescriptorGuid</code> passed in.
	 * 
	 * @param priceListDescriptorGuid unique id for Price List Descriptor domain object
	 */
	private PriceListDescriptor getPriceListDescriptor(final String priceListDescriptorGuid)  {
		PriceListDescriptor priceListDescriptor = priceListDescriptorService.findByGuid(priceListDescriptorGuid);
		if (priceListDescriptor == null) {
			throw new PopulationRuntimeException("IE-10614", priceListDescriptorGuid);
		}
		return priceListDescriptor;
	}
	
	
	/**
	 * Returns Selling Context domain object that matches the guid of the <code>sellingContextDTO</code>
	 * and sets the values from the DTO onto it. The map of conditions on
	 * the selling context domain is first cleared.
	 *
	 * @param sellingContextDTO Selling Context DTO object
	 * @return selling context domain object with DTO values populated in it 
	 */
	private SellingContext getSellingContext(final SellingContextDTO sellingContextDTO) {
		
		SellingContext sellingContext = sellingContextAdapter.getDomainObject(sellingContextDTO.getGuid());
		sellingContext.getConditions().clear();
		sellingContextAdapter.populateDomain(sellingContextDTO, sellingContext);
		
		return sellingContext;
	}
	
	/**
	 * Validates that the price list assignment has no any saved conditions and its no-saved conditions have valid dictionary guids.
	 * If list of savedConditionGuids is not null, then throw <code>PopulationRuntimeException</code>.
	 * If list of conditionDtos is null, then an empty list is created and set onto sellingContextDTO.
	 * 
	 * @param priceListAssignmentGuid Guid of the price list assignment that has the given selling context
	 * @param sellingContextDTO selling context dto with conditions to be validated
	 */
	private void validateConditions(final String priceListAssignmentGuid, final SellingContextDTO sellingContextDTO) {
		
		
		if (sellingContextDTO.getSavedConditionGuids() != null && sellingContextDTO.getSavedConditionGuids().size() > 0) {
			throw new PopulationRuntimeException("IE-10612", priceListAssignmentGuid);
		}
		
		List<ConditionalExpressionDTO> conditionDtos = sellingContextDTO.getConditions();
		
		if (conditionDtos == null) {
			// Setup empty list of DTOs if none exist. This is needed by the populate.
			conditionDtos = new ArrayList<ConditionalExpressionDTO>();
			sellingContextDTO.setConditions(conditionDtos);
		} else {
			// Validate that the all tag dictionaries exist with the given dictionary guids
			for (ConditionalExpressionDTO conditionDto : conditionDtos) {
				String dictionaryGuid = conditionDto.getDictionaryGuid();
				TagDictionary foundTagDictionary = tagDictionaryService.findByGuid(dictionaryGuid);
				if (foundTagDictionary == null) {
					throw new PopulationRuntimeException(
							"IE-10613", priceListAssignmentGuid, conditionDto.getGuid(), dictionaryGuid);
				}
			}
		}
	}
	
	@Override
	public PriceListAssignment getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.PRICE_LIST_ASSIGNMENT);
	}

	@Override
	public PriceListAssignmentDTO getDtoInstance() {
		return new PriceListAssignmentDTO();
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
	
	public void setSellingContextAdapter(final SellingContextAdapter sellingContextAdapter) {
		this.sellingContextAdapter = sellingContextAdapter;
	}
	
	/**
	 * Sets the tag dictionary service.
	 * 
	 * @param tagDictionaryService value to set
	 */
	public void setTagDictionaryService(final TagDictionaryService tagDictionaryService) {
		this.tagDictionaryService = tagDictionaryService;
	}
}
