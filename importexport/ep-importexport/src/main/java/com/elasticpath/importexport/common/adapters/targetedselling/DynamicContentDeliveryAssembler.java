package com.elasticpath.importexport.common.adapters.targetedselling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.importexport.common.adapters.tag.SellingContextAdapter;
import com.elasticpath.importexport.common.dto.tag.ConditionalExpressionDTO;
import com.elasticpath.importexport.common.dto.tag.SellingContextDTO;
import com.elasticpath.importexport.common.dto.targetedselling.DynamicContentDeliveryDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.service.contentspace.ContentSpaceService;
import com.elasticpath.service.contentspace.DynamicContentService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.TagConditionService;
import com.elasticpath.tags.service.TagDictionaryService;

/**
 * Assembler for {@linkplain DynamicContentDelivery}.
 */
public class DynamicContentDeliveryAssembler extends AbstractDtoAssembler<DynamicContentDeliveryDTO, DynamicContentDelivery> {

	private BeanFactory beanFactory;
	private ContentSpaceService csService;
	private DynamicContentService dcService;
	private TagDictionaryService tagDictionaryService;
	private TagConditionService tagConditionService;
	private SellingContextAdapter sellingContextAdapter;

	@Override
	public DynamicContentDelivery getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.DYNAMIC_CONTENT_DELIVERY);
	}

	@Override
	public DynamicContentDeliveryDTO getDtoInstance() {
		return new DynamicContentDeliveryDTO();
	}

	@Override
	public void assembleDto(final DynamicContentDelivery source, final DynamicContentDeliveryDTO target) {
		List<String> contentSpaceGuids = new ArrayList<String>();
		for (ContentSpace cs : source.getContentspaces()) {
			contentSpaceGuids.add(cs.getGuid());
		}
		target.setContentSpaceGuids(contentSpaceGuids);
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setDynamicContentGuid(source.getDynamicContent().getGuid());
		target.setPriority(source.getPriority());
		target.setGuid(source.getGuid());

		SellingContext sellingContext = source.getSellingContext();
		if (sellingContext != null) {
			SellingContextDTO scDto = sellingContextAdapter.createDtoObject();
			sellingContextAdapter.populateDTO(sellingContext, scDto);
			target.setSellingContext(scDto);
		}
	}

	/**
	 * {@inheritDoc} Dynamic Content, Content Spaces, and Selling context are all assumed to exist, since
	 * they are required fields in the schema.
	 */
	@Override
	public void assembleDomain(final DynamicContentDeliveryDTO source, final DynamicContentDelivery target) {
		target.setName(source.getName());
		target.setGuid(source.getGuid());
		target.setPriority(source.getPriority());
		target.setDescription(source.getDescription());

		target.setContentspaces(getContentSpaces(source.getContentSpaceGuids()));
		target.setDynamicContent(getDynamicContent(source.getDynamicContentGuid()));

		validateConditions(source.getGuid(), source.getSellingContext());
		target.setSellingContext(getSellingContext(source.getSellingContext()));
	}

	/**
	 * Returns set of content spaces that match the list of <code>contentSpaceGuids</code> passed in.
	 * If the content space is not found then an exception is thrown.
	 *
	 * @param contentSpaceGuids list of guids
	 */
	private Set<ContentSpace> getContentSpaces(final List<String> contentSpaceGuids) {

		Set<ContentSpace> contentSpaces = new HashSet<ContentSpace>();
		for (String cs : contentSpaceGuids) {
			ContentSpace foundContentSpace = csService.findByGuid(cs);
			if (foundContentSpace == null) {
				throw new PopulationRuntimeException("IE-40500", cs);
			}
			contentSpaces.add(foundContentSpace);
		}
		return contentSpaces;
	}

	/**
	 * Returns Dynamic content domain object that matches the <code>dynamicContentGuid</code> passed in.
	 *
	 * @param dynamicContentGuid unique id for Dynamic Content domain object
	 */
	private DynamicContent getDynamicContent(final String dynamicContentGuid)  {
		DynamicContent dynamicContent = dcService.findByGuid(dynamicContentGuid);
		if (dynamicContent == null) {
			throw new PopulationRuntimeException("IE-40501", dynamicContentGuid);
		}
		return dynamicContent;
	}

	/**
	 * Returns Selling Context domain object that matches the guid of the <code>sellingContextDTO</code>
	 * or a new one if it does not exist and sets the values from the DTO onto it. The map of conditions on
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
	 * Validates that the conditions have valid dictionary guids and any saved conditions exist in the target database.
	 * If list of conditionDtos is null, then an empty list is created and set onto sellingContextDTO.
	 * If list of savedConditionGuids is null, then an empty list is created and set onto sellingContextDTO.
	 *
	 * @param dynamicContentDeliveryGuid Guid of the dynamic content delivery that has the given selling context
	 * @param sellingContextDTO selling context dto with conditions to be validated
	 */
	private void validateConditions(final String dynamicContentDeliveryGuid, final SellingContextDTO sellingContextDTO) {
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
					String guid = conditionDto.getGuid();
					throw new PopulationRuntimeException("IE-40502", dynamicContentDeliveryGuid, guid, dictionaryGuid);
				}
			}
		}

		List<String> savedConditionDtoGuids = sellingContextDTO.getSavedConditionGuids();

		if (savedConditionDtoGuids == null) {
			// Setup empty list of DTOs if none exist. This is needed by the populate.
			savedConditionDtoGuids = new ArrayList<String>();
			sellingContextDTO.setSavedConditionGuids(savedConditionDtoGuids);
		} else {
			// Validate that all saved conditions exist with given guids
			for (String guid : savedConditionDtoGuids) {
				ConditionalExpression condition = tagConditionService.findByGuid(guid);
				if (condition == null) {
						throw new PopulationRuntimeException("IE-40503", dynamicContentDeliveryGuid, guid);
				}
			}
		}
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	/**
	 * Sets the content space service.
	 *
	 * @param csService value to set
	 */
	public void setContentSpaceService(final ContentSpaceService csService) {
		this.csService = csService;
	}

	/**
	 * Sets the dynamic content service.
	 *
	 * @param dcService value to set
	 */
	public void setDynamicContentService(final DynamicContentService dcService) {
		this.dcService = dcService;
	}

	/**
	 * Sets the tag dictionary service.
	 *
	 * @param tdService value to set
	 */
	public void setTagDictionaryService(final TagDictionaryService tdService) {
		tagDictionaryService = tdService;
	}

	/**
	 * Sets the tag condition service.
	 *
	 * @param tcService value to set
	 */
	public void setTagConditionService(final TagConditionService tcService) {
		tagConditionService = tcService;
	}

	/**
	 * Sets the selling context adapter.
	 *
	 * @param sellingContextAdapter value to set
	 */
	public void setSellingContextAdapter(final SellingContextAdapter sellingContextAdapter) {
		this.sellingContextAdapter = sellingContextAdapter;
	}
}
