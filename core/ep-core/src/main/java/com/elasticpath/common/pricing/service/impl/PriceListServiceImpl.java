package com.elasticpath.common.pricing.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.assembler.pricing.BaseAmountDtoAssembler;
import com.elasticpath.common.dto.assembler.pricing.PriceListDescriptorDtoAssembler;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.common.pricing.service.BaseAmountUpdateStrategy;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpCurrencyBindException;
import com.elasticpath.commons.util.PairInsensitiveString;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * Default implementation of PriceListService. Client facing service to work with
 * price related DTOs and domain objects.
 * This service should not be aware of any persistence related logic, such as working with UIDs.
 */
public class PriceListServiceImpl extends AbstractEpServiceImpl implements PriceListService {
	
	private static final int PRODUCT_CODE_IDX = 0;
	private static final int PRODUCT_NAME_IDX = 1;
	private static final int PRODUCT_MULTUSKU_IDX = 2;
	private static final int PRODUCT_DEF_SKU_IDX = 3;	
	private static final int SKU_CODE_IDX = 3;
	private static final int SKU_OPTIONVALUE_IDX = 1;
	private static final int SKU_OPTIONKEY_IDX = 2;
	private static final int SKU_CODE_OPTIONVALUE_IDX = 0;
	
	
	private PriceListDescriptorService pldService;
	private BaseAmountService baService;
	private BaseAmountDtoAssembler baDtoAssembler;
	private PriceListDescriptorDtoAssembler pldDtoAssembler;
	private BaseAmountUpdateStrategy baStrategy;
	private PriceListAssignmentService priceListAssignmentService;
	private ProductService productService;
 
	
	/**
	 * Use the update strategy for applying the changes.
	 * 
	 * @param changeSet the DtoChangeSet containing BaseAmountDTOs
	 * @throws EpServiceException on errors applying changes
	 */
	public void modifyBaseAmountChangeSet(final ChangeSetObjects <BaseAmountDTO> changeSet) throws EpServiceException {
		this.baStrategy.modifyBaseAmounts(changeSet);
	}
	
	@Override
	public void delete(final PriceListDescriptorDTO pldDTO) {
		checkIfICanDelete(pldDTO);
		
		final String priceListDescriptiptorGuid = pldDTO.getGuid();
		PriceListDescriptor priceListDescriptor = pldService.findByGuid(priceListDescriptiptorGuid);
		pldService.delete(priceListDescriptor);			
	}

	/**
	 * Check if the price list descriptor can be deleted i.e. it has no PLAs assigned to it.
	 * Called by {@code PriceListService.delete(PriceListDescriptorDTO pldDTO)}.
	 * @param pldDTO the price list descriptor
	 * */
	protected void checkIfICanDelete(final PriceListDescriptorDTO pldDTO) {
		final String priceListDescriptiptorGuid = pldDTO.getGuid();
		
		final List<PriceListAssignment> assignmentList = 
			priceListAssignmentService.listByPriceList(priceListDescriptiptorGuid);
		
		if (!assignmentList.isEmpty()) {
			throw new PriceListInUseByPLAException("The price list is in use by price list assignments and cannot be deleted."); 
		}
	}
	
	@Override
	public Collection<BaseAmountDTO> getBaseAmounts(final BaseAmountFilter filter) {
		Collection <BaseAmount> baseAmounts = baService.findBaseAmounts(filter);
		return baDtoAssembler.assembleDto(baseAmounts);		
	}
	
	@Override
	public Collection<BaseAmountDTO> getBaseAmountsExt(
			final BaseAmountFilterExt baseAmountFilterExt, final boolean exactMatch) {
		final String objectGuid;
		final String namedQuery;
		if (exactMatch) {
			objectGuid = adaptToSearchExact(baseAmountFilterExt.getObjectGuid());
			//The similar named query here is just because Derby does not support Null in sql.
			namedQuery = getQueryNameForExactMatch(baseAmountFilterExt);
		} else {
			objectGuid = adaptToSearch(baseAmountFilterExt.getObjectGuid(), baseAmountFilterExt.getLocale());
			namedQuery = getQueryName(baseAmountFilterExt);
		}
		
		
		final Object [] searchCriteria = new Object [] {
				baseAmountFilterExt.getPriceListDescriptorGuid(),
				baseAmountFilterExt.getObjectType(),
				objectGuid,
				baseAmountFilterExt.getLowestPrice(),
				baseAmountFilterExt.getHighestPrice(),
				baseAmountFilterExt.getQuantity()				
		};
		
		List<String> guids = productService.getProductSkuGuids(objectGuid);

		Collection <BaseAmount> baseAmounts = baService.findBaseAmounts(
				namedQuery,
				searchCriteria,
				baseAmountFilterExt.getStartIndex(),
				baseAmountFilterExt.getLimit(),
				guids);
		
		Collection <BaseAmountDTO> baseAmountDTOs = baDtoAssembler.assembleDto(baseAmounts);
		enrichBaseAmountDTOs(baseAmountFilterExt.getLocale(), baseAmountDTOs);
		attachSkusForMultiSkuProduct(baseAmountFilterExt, baseAmountDTOs);
		return baseAmountDTOs;
	}

	private String getQueryName(final BaseAmountFilterExt baseAmountFilterExt) {
		final String namedQuery;
		if (baseAmountFilterExt.getLowestPrice() == null && baseAmountFilterExt.getHighestPrice() == null) {
			namedQuery = "BASE_AMOUNTS_BY_EXT_FILTER";
		} else if (baseAmountFilterExt.getLowestPrice() != null && baseAmountFilterExt.getHighestPrice() != null) { //NOPMD
			namedQuery = "BASE_AMOUNTS_BY_EXT_FILTER_LOWVALUE_UPVALUE_PROVIDED";
		} else if (baseAmountFilterExt.getLowestPrice() != null) {	//NOPMD
			namedQuery = "BASE_AMOUNTS_BY_EXT_FILTER_LOWVALUE_PROVIDED";
		} else {
			namedQuery = "BASE_AMOUNTS_BY_EXT_FILTER_UPVALUE_PROVIDED";
		}
		return namedQuery;
	}

	private String getQueryNameForExactMatch(final BaseAmountFilterExt baseAmountFilterExt) {
		final String namedQuery;
		if (baseAmountFilterExt.getLowestPrice() == null && baseAmountFilterExt.getHighestPrice() == null) {
			namedQuery = "BASE_AMOUNTS_BY_EXT_FILTER_CASE_SENSITIVE";
		} else if (baseAmountFilterExt.getLowestPrice() != null && baseAmountFilterExt.getHighestPrice() != null) {  //NOPMD
			namedQuery = "BASE_AMOUNTS_BY_EXT_FILTER_CASE_SENSITIVE_LOWVALUE_UPVALUE_PROVIDED";
		} else if (baseAmountFilterExt.getLowestPrice() != null) {	//NOPMD
			namedQuery = "BASE_AMOUNTS_BY_EXT_FILTER_CASE_SENSITIVE_LOWVALUE_PROVIDED";
		} else {
			namedQuery = "BASE_AMOUNTS_BY_EXT_FILTER_CASE_SENSITIVE_UPVALUE_PROVIDED";
		}
		return namedQuery;
	}
	
	/**
	 * Attach skus for multiple skus product, if collection 
	 * not rich the limit from filter and object guid not blank.
	 * The size of target collection will be not more than specified 
	 * by filter limit.  
	 * @param baseAmountFilterExt criteria for searching BaseAmounts. 
	 * @param baseAmountDTOs collection of retrieved DTO by given filter
	 */
	private void attachSkusForMultiSkuProduct(final BaseAmountFilterExt baseAmountFilterExt, 
			final Collection <BaseAmountDTO> baseAmountDTOs) {
		final Collection <BaseAmountDTO> attachments = new ArrayList<BaseAmountDTO>();
		for (BaseAmountDTO baseAmountDTO : baseAmountDTOs) {
			if (baseAmountDTOs.size() < baseAmountFilterExt.getLimit() && StringUtils.isNotBlank(baseAmountFilterExt.getObjectGuid())) {
				if (baseAmountDTO.isMultiSku() 
						&& 
						baseAmountDTO.getObjectType().equals(BaseAmountObjectType.PRODUCT.toString())) {
					final int attachmentsLimit = baseAmountFilterExt.getLimit() - attachments.size() - baseAmountDTOs.size(); 
					attachEnrichedSkus(attachments, 
							baseAmountFilterExt.getPriceListDescriptorGuid(),
							baseAmountFilterExt.getLocale(), baseAmountDTO.getObjectGuid(),
							attachmentsLimit
							);
					
				}
			} else {
				break;
			}
		}
		
		for (BaseAmountDTO attachedDto : attachments) {
			if (!baseAmountDTOs.contains(attachedDto)) {
				baseAmountDTOs.add(attachedDto);
			}
		}				
		
	}
	
	
	
	@Override
	public Collection<BaseAmountDTO> getBaseAmountsExt(
			final BaseAmountFilterExt baseAmountFilterExt) {
		return getBaseAmountsExt(baseAmountFilterExt, false);
	}

	private String adaptToSearchExact(final String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		return str;
	}

	
	private String adaptToSearch(final String str, final Locale locale) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		return "%" + str.toLowerCase(locale) + "%";
	}
	
	@Override
	public Collection<BaseAmountDTO> getBaseAmountsExtWithSkus(
			final BaseAmountFilterExt baseAmountFilterExt) {
		
		Collection<BaseAmountDTO> enrichedDtos = new ArrayList<BaseAmountDTO>();
		
		final Collection<BaseAmountDTO> baseAmountsDTOProducts = getBaseAmountsExt(baseAmountFilterExt, true);
		
		if (baseAmountFilterExt.getObjectType() == null 
				|| !BaseAmountObjectType.PRODUCT.toString().equals(baseAmountFilterExt.getObjectType())) {
			
			enrichedDtos = baseAmountsDTOProducts;
		
		} else { // if product then need to retrieve associated skus
			
			enrichedDtos.addAll(baseAmountsDTOProducts);
			
			attachEnrichedSkus(enrichedDtos, 
					baseAmountFilterExt.getPriceListDescriptorGuid(), 
					baseAmountFilterExt.getLocale(),
					baseAmountFilterExt.getObjectGuid(),
					Integer.MAX_VALUE);
			
		}
		
		return enrichedDtos;
	}

	/**
	 * 
	 * @param enrichedDtos collection to attach to 
	 * @param priceListDescriptorGuid price list descriptor guid
	 * @param locale locale
	 * @param productGuid guid of product for which we find skus
	 * @oaram limit size limit for enrichedDtos 
	 */
	private void attachEnrichedSkus(
			final Collection<BaseAmountDTO> enrichedDtos,
			final String priceListDescriptorGuid, final Locale locale,
			final String productGuid,
			final int limit) {
		final String[] skuGuids = extractProductGuids(productGuid);
		
		if (!ArrayUtils.isEmpty(skuGuids)) {
		
			Collection <BaseAmount> baseAmountsSkus = baService.findBaseAmounts(
					priceListDescriptorGuid, 
					BaseAmountObjectType.SKU.toString(), skuGuids);
			
			Collection <BaseAmountDTO> baseAmountDTOSkus = baDtoAssembler.assembleDto(baseAmountsSkus);
			enrichBaseAmountDTOs(locale, baseAmountDTOSkus);
			
			for (BaseAmountDTO dto : baseAmountDTOSkus) {
				if (enrichedDtos.size() < limit && !enrichedDtos.contains(dto)) {
					enrichedDtos.add(dto);					
				} else {
					break;
				}
				
			}
			
		}
	}
	
	private String[] extractProductGuids(final String baseAmountGuid) {
		final List<String> guids = productService.getProductSkuGuids(baseAmountGuid);
		if (guids != null) {
			return guids.toArray(new String[guids.size()]);
		}
		return null;
	}

	@Override
	public Collection<BaseAmountDTO> getBaseAmounts(final String priceListGuid, final Locale locale) {
		final BaseAmountFilter baseAmountFilter = getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		baseAmountFilter.setPriceListDescriptorGuid(priceListGuid);
		
		final Collection<BaseAmountDTO> allDTOs = getBaseAmounts(baseAmountFilter);
		enrichBaseAmountDTOs(locale, allDTOs);
		return allDTOs;
	}
	
	/**
	 * Create map of dtos, where key is object guid , object type
	 * value list of dto. Distinguish between dot in collection is
	 * quantity, because of different price tiers  
	 * @param dtos collection of dto
	 * @return map of dtos
	 */
	private Map<PairInsensitiveString, List<BaseAmountDTO>> createBaseAmountMap(final Collection<BaseAmountDTO> dtos, final Locale locale) {
		Map<PairInsensitiveString, List<BaseAmountDTO>> map = 
			new HashMap<PairInsensitiveString, List<BaseAmountDTO>>(dtos.size());
		for (BaseAmountDTO dto : dtos) {
			final PairInsensitiveString key = new PairInsensitiveString(dto.getObjectGuid(), dto.getObjectType(), locale); 
			List<BaseAmountDTO> dtoTuple = map.get(key);
			if (dtoTuple == null) {
				dtoTuple = new ArrayList<BaseAmountDTO>();
			}
			dtoTuple.add(dto);
			map.put(key, dtoTuple);			
		}		
		return map;
	}

	
	private Map<PairInsensitiveString, List<BaseAmountDTO>> enrichBaseAmountDTOs(final Locale locale, final Collection<BaseAmountDTO> dtos) {
		final Map<PairInsensitiveString, List<BaseAmountDTO>> dtoMap = createBaseAmountMap(dtos, locale);
		
		enrichBaseAmountDTOsForProducts(locale, dtoMap, "PRODUCT_ENRICH_DTO_BY_GUIDS");
		enrichBaseAmountDTOsForProducts(locale, dtoMap, "BUNDLE_ENRICH_DTO_BY_GUIDS");	
		
		enrichBaseAmountDTOsForSkus(locale, dtoMap, "PRODUCT_SKU_ENRICH_DTO_BY_GUIDS");
		enrichBaseAmountDTOForSkuOptions(locale, dtoMap, "PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS");
		
		return dtoMap;
	}
	
	
	//private 
	
	private void enrichBaseAmountDTOsForSkus(
			final Locale locale, 
			final Map<PairInsensitiveString, List<BaseAmountDTO>> dtos, 
			final String queryName) {
		
		final List<Object[]> enrichingData = findEnrichingData(locale, dtos.keySet(), queryName, BaseAmountObjectType.SKU.toString());
		
		for (Object[] data : enrichingData) {
			List<BaseAmountDTO> dtoTuple = dtos.get(
					new PairInsensitiveString(
							(String) data[SKU_CODE_IDX], 
							BaseAmountObjectType.SKU.toString(),
							locale));
			if (dtoTuple != null) {
				for (BaseAmountDTO dto : dtoTuple) {
					dto.setProductCode((String) data[PRODUCT_CODE_IDX]);
					dto.setProductName((String) data[PRODUCT_NAME_IDX]);
					dto.setMultiSku((Boolean) data[PRODUCT_MULTUSKU_IDX]);			
					dto.setSkuCode((String) data[SKU_CODE_IDX]);
				}
			}
		}		
	}	
	
	
	private void enrichBaseAmountDTOsForProducts(final Locale locale, 
			final Map<PairInsensitiveString, List<BaseAmountDTO>> dtos, 
			final String queryName) {
		
		final List<Object[]> enrichingData = findEnrichingData(locale, dtos.keySet(), queryName, BaseAmountObjectType.PRODUCT.toString());

		for (Object[] data : enrichingData) {
			List<BaseAmountDTO> dtoTuple = dtos.get(
					new PairInsensitiveString(
							(String) data[PRODUCT_CODE_IDX], 
							BaseAmountObjectType.PRODUCT.toString(),
							locale));
			if (dtoTuple != null) {
				for (BaseAmountDTO dto : dtoTuple) {
					dto.setProductCode((String) data[PRODUCT_CODE_IDX]);
					dto.setProductName((String) data[PRODUCT_NAME_IDX]);
					dto.setMultiSku((Boolean) data[PRODUCT_MULTUSKU_IDX]);
					if (!dto.isMultiSku()) {
						dto.setSkuCode((String) data[PRODUCT_DEF_SKU_IDX]);
					}
				}
			}
		}
		
	}
	
	private void enrichBaseAmountDTOForSkuOptions(final Locale locale,
			final Map<PairInsensitiveString, List<BaseAmountDTO>> dtos, 
			final String queryName) {
		
		final List<Object[]> enrichingData = findEnrichingData(locale, dtos.keySet(), queryName, BaseAmountObjectType.SKU.toString());
		for (Object[] data : enrichingData) {
			List<BaseAmountDTO> dtoTuple = dtos.get(
					new PairInsensitiveString(
							(String) data[SKU_CODE_OPTIONVALUE_IDX], 
							BaseAmountObjectType.SKU.toString(),
							locale
							));
			if (dtoTuple != null) {
				for (BaseAmountDTO dto : dtoTuple) {
					final String optionValue = dto.getSkuConfiguration();
					if (StringUtils.isEmpty(optionValue)) {
						dto.setSkuConfiguration(((String) data[SKU_OPTIONVALUE_IDX]));					
					} else {
						dto.setSkuConfiguration(optionValue + "," + data[SKU_OPTIONVALUE_IDX]);
					}
					
					if (StringUtils.equalsIgnoreCase("frequency", (String) data[SKU_OPTIONKEY_IDX])) {
					    dto.setPaymentScheduleName((String) data[SKU_OPTIONVALUE_IDX]);
					}
				}
			}		
		}
		enrichingData.clear();
	}
	
	
	private List<Object[]> findEnrichingData(
			final Locale locale,
			final Set<PairInsensitiveString> keySet, 
			final String queryName,
			final String objectTypeFilter) {
		
		return productService.findEnrichingData(queryName, extractGuids(keySet, objectTypeFilter), locale);
	}

	private Collection<String> extractGuids(
			final Set<PairInsensitiveString> keySet,
			final String objectTypeFilter) {
		Collection<String> guids = new ArrayList<String>(keySet.size());
		for (PairInsensitiveString guidTypePair : keySet) {
			if (objectTypeFilter.equalsIgnoreCase(guidTypePair.getSecond())) {
				guids.add(guidTypePair.getFirst());				
			}
		}
		return guids;
	}


	

	@Override
	public PriceListDescriptorDTO getPriceListDescriptor(final String pldGuid) {
		PriceListDescriptor pld = pldService.findByGuid(pldGuid);
		if (pld == null) {
			return null;
		}
		return pldDtoAssembler.assembleDto(pld);
	}

	@Override
	public BaseAmountDTO getBaseAmount(final String baGuid) {
		BaseAmount baseAmount = baService.findByGuid(baGuid);
		if (baseAmount == null) {
			return null;
		}
		return baDtoAssembler.assembleDto(baseAmount);
	}
	
	@Override
	public Collection<PriceListDescriptorDTO> getPriceListDescriptors(final boolean includeHidden) {
		Collection<PriceListDescriptor> plds = pldService.getPriceListDescriptors(includeHidden);
		return pldDtoAssembler.assembleDto(plds);
	}

	@Override
	public List<PriceListDescriptorDTO> listByCatalog(final Catalog catalog) {
		return listByCatalog(catalog, false);
	}
	
	@Override
	public List<PriceListDescriptorDTO> listByCatalog(final Catalog catalog, final boolean includeHidden) {
		List<PriceListAssignment> plas = priceListAssignmentService
				.listByCatalog(catalog, includeHidden);
		Set<PriceListDescriptor> unique = new HashSet<PriceListDescriptor>();
		for (PriceListAssignment assignment : plas) {
			unique.add(assignment.getPriceListDescriptor());
		}
		
		List<PriceListDescriptorDTO> result = new ArrayList<PriceListDescriptorDTO>();
		for (PriceListDescriptor descriptor : unique) {
			result.add(pldDtoAssembler.assembleDto(descriptor));
		}
		return result;
	}
	
	/**
	 * Save a new PriceListDescriptorDTO that isn't already in the database, or if
	 * it is an existing descriptor do an update.  
	 * Input DTO object will be checked for valid currency code.
	 * 
	 * @param pldDTO new or updated price list descriptor
	 * @return updated instance of the price list descriptor
	 */
	public PriceListDescriptorDTO saveOrUpdate(final PriceListDescriptorDTO pldDTO) {
		// check for valid currency code
		try {
			Currency.getInstance(pldDTO.getCurrencyCode());
		} catch (final Exception e) {
			throw new EpCurrencyBindException("Unsupported currency code: [" + pldDTO.getCurrencyCode() + "]", e);
		}
		
		PriceListDescriptor descriptor = pldDtoAssembler.assembleDomain(pldDTO);
		PriceListDescriptor merged = null;
		if (isDtoPersistent(pldDTO)) {
			merged = this.pldService.update(descriptor);
		} else {
			merged = this.pldService.add(descriptor);
		}
		return pldDtoAssembler.assembleDto(merged);
	}
	
	/**
	 * @param pldDto to check if persisted
	 * @return true if DTO is representing a PriceListDescriptor that is already persisted.
	 */
	protected boolean isDtoPersistent(final PriceListDescriptorDTO pldDto) {
		return pldService.findByGuid(pldDto.getGuid()) != null;
	}

	/**
	 * Set the PriceListDescriptorService.
	 * @param pldService the service
	 */
	public void setPriceListDescriptorService(final PriceListDescriptorService pldService) {
		this.pldService = pldService;
	}

	/**
	 * Set the BaseAmountService.
	 * @param baService the service
	 */
	public void setBaseAmountService(final BaseAmountService baService) {
		this.baService = baService;
	}

	/**
	 * Set the DTO assembler for BaseAmounts.
	 * 
	 * @param baDtoAssembler the assembler
	 */
	public void setBaseAmountDtoAssembler(final BaseAmountDtoAssembler baDtoAssembler) {
		this.baDtoAssembler = baDtoAssembler;
	}
	

	/**
	 * Set the DTO assembler for PriceListDescriptors.
	 * 
	 * @param pldDtoAssembler the assembler
	 */
	public void setPriceListDescriptorDtoAssembler(final PriceListDescriptorDtoAssembler pldDtoAssembler) {
		this.pldDtoAssembler = pldDtoAssembler;
	}

	/**
	 * @param baStrategy the strategy to use to apply BaseAmount changes
	 */
	public void setBaseAmountUpdateStrategy(final BaseAmountUpdateStrategy baStrategy) {
		this.baStrategy = baStrategy;
	}
	
	/**
	 * @param priceListAssignmentService <code>PriceListAssignmentService</code> to use
	 */
	public void setPriceListAssignmentService(
			final PriceListAssignmentService priceListAssignmentService) {
		this.priceListAssignmentService = priceListAssignmentService;
	}
	
	@Override
	public List<PriceListDescriptorDTO> getPriceListDescriptors(final Collection<String> priceListDescriptorsGuids) {
		Collection<PriceListDescriptor> plds = pldService.getPriceListDescriptors(priceListDescriptorsGuids);
		return pldDtoAssembler.assembleDto(plds);
	}
	
	@Override
	public boolean isPriceListNameUnique(final String guid, final String name) {
		return pldService.isPriceListNameUnique(guid, name);
	}

	/**
	 * 
	 * @param productService {@link ProductService} service to use
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	@Override
	public PriceListDescriptorDTO getPriceListDescriptorByName(final String priceListDescriptorName) {
		PriceListDescriptor pld = pldService.findByName(priceListDescriptorName);
		if (pld == null) {
			return null;
		}
		return pldDtoAssembler.assembleDto(pld);
	}

	@Override
	public PriceListDescriptorService getPriceListDescriptorService() {
		return pldService;
	}

	@Override
	public PriceListAssignmentService getPriceListAssignmentService() {
		return priceListAssignmentService;
	}
	
}
