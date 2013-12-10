package com.elasticpath.common.pricing.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.category.ChangeSetObjectsImpl;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.common.pricing.service.PriceListLookupService;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/** 
 * Implementation of {@link PriceListHelperService} interface. 
 */
public class PriceListHelperServiceImpl extends AbstractEpServiceImpl implements PriceListHelperService {
	
	private static final String PRODUCT_TYPE = "PRODUCT"; //$NON-NLS-1$

	private static final String PRODUCT_SKU_TYPE = "SKU"; //$NON-NLS-1$

	private PriceListLookupService priceListLookupService;

	private PriceListService priceListService;
	
	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final Product product) {
		return getPriceListMap(product, false);
	}
	
	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final Product product, final boolean masterOnly) {
		return getPriceInfoInternal(product.getCode(), PRODUCT_TYPE, prepareDescriptorsList(product, masterOnly));
	}

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final Product product, 
			final List<PriceListDescriptorDTO> priceListDescriptors) {
		return getPriceInfoInternal(product.getCode(), PRODUCT_TYPE, priceListDescriptors);
	}	
	
	
	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final ProductSku productSku) {
		final Product product = productSku.getProduct();
		final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> map = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		for (PriceListDescriptorDTO priceListDescriptorDTO : findAllDescriptors(product)) {
			
			List<BaseAmountDTO> list = findBaseAmounts(priceListDescriptorDTO.getGuid(), product.getCode(), PRODUCT_TYPE);
			list.addAll(findBaseAmounts(priceListDescriptorDTO.getGuid(), productSku.getSkuCode(), PRODUCT_SKU_TYPE));
			
			map.put(priceListDescriptorDTO, list);
		}
		return map;
	}

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(
			final ProductSku productSku, 
			final List<PriceListDescriptorDTO> priceListDescriptors) {

		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> productMap = 
			getPriceInfoInternal(productSku.getProduct().getCode(), PRODUCT_TYPE, priceListDescriptors);
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> skuMap = 
			getPriceInfoInternal(productSku.getSkuCode(), PRODUCT_SKU_TYPE, priceListDescriptors);
		
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> resultMap = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>(productMap);

		for (PriceListDescriptorDTO priceListDescriptorDTO : skuMap.keySet()) {
			if (skuMap.get(priceListDescriptorDTO) == null || skuMap.get(priceListDescriptorDTO).isEmpty()) { continue; } 
			resultMap.get(priceListDescriptorDTO).addAll(skuMap.get(priceListDescriptorDTO));
		}
		return resultMap;
	}
	
	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final BaseAmountFilter filter, final String ... currencyCodes) {
		final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> descriptorAmountsMap = 
			new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		List<BaseAmountDTO> baseAmounts = findBaseAmounts(filter);
		
		final Map<String, PriceListDescriptorDTO> plDescriptors = getPriceListDescriptorDTOs(baseAmounts);
		removeUnwantedCurrencies(plDescriptors, currencyCodes);
		
		for (BaseAmountDTO baseAmount : baseAmounts) {
			PriceListDescriptorDTO priceListDescriptorDTO = plDescriptors.get(baseAmount.getPriceListDescriptorGuid());
			//only find the non-hidden price lists
			if (priceListDescriptorDTO != null && !priceListDescriptorDTO.isHidden()) {
				List<BaseAmountDTO> amounts = getBaseAmounts(descriptorAmountsMap, priceListDescriptorDTO);
				amounts.add(baseAmount);
			}
		}
		return descriptorAmountsMap;
	}	
	
	private void removeUnwantedCurrencies(final Map<String, PriceListDescriptorDTO> plDescriptors, final String ... currencyCodes) {
		if (ArrayUtils.isEmpty(currencyCodes)) {
			return;
		}
		for (Iterator<PriceListDescriptorDTO> iter = plDescriptors.values().iterator(); iter.hasNext();) {
			boolean remove = true;
			String currencyCode =  iter.next().getCurrencyCode();
			for (String currency : currencyCodes) {
				if (StringUtils.equalsIgnoreCase(currency, currencyCode) || StringUtils.isEmpty(currency)) {
					remove = false;
					break;
				}
			}
			if (remove) {
				iter.remove();
			}
		}
	}

	/**
	 * Gets the actual Price Information Map for code, type and pre-loaded descriptors.
	 * 
	 * @param code the Product or SKU code
	 * @param objectType the PRODUCT, or SKU string.
	 * @param priceListDescriptors the list of PriceListDescriptorDTOs
	 * @return <code>Map</code> between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO}
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceInfoInternal(final String code, 
			final String objectType, final List<PriceListDescriptorDTO> priceListDescriptors) {
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> map = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		for (PriceListDescriptorDTO priceListDescriptorDTO : priceListDescriptors) {
			map.put(priceListDescriptorDTO, findBaseAmounts(priceListDescriptorDTO.getGuid(), code, objectType));
		}
		return map;
	}	

	/**
	 * Creates a list of <code>PriceListDescriptorDTO</code>s for <code>Product</code>.
	 *   
	 * @param product the {@link Product} instance
	 * @param masterOnly if true the only Master Catalog's descriptor will be loaded and shipped with corresponding BaseAmounts, otherwise all.
	 * @return List of <code>PriceListDescriptorDTO</code>
	 */
	List<PriceListDescriptorDTO> prepareDescriptorsList(final Product product, final boolean masterOnly) {
		if (masterOnly) {
			final Catalog masterCatalog = product.getMasterCatalog();
			return findAllDescriptors(masterCatalog, getDefaultCurrencyFor(masterCatalog));
		}
		
		return findAllDescriptors(product);
	}
	
	@Override
	public void processBaseAmountChangeSets(final Collection<ChangeSetObjects<BaseAmountDTO>> baseAmountChangeSetCollection) {
		for (ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet : baseAmountChangeSetCollection) {
			priceListService.modifyBaseAmountChangeSet(baseAmountChangeSet);
		}
	}
	
	/**
	 * Finds {@link PriceListDescriptorDTO}s for {@link Product} using its Catalogs and SupportedCurrencies.
	 * 
	 * @param product the {@link Product} instance
	 * @return List of <code>PriceListDescriptorDTO</code>s
	 */
	public List<PriceListDescriptorDTO> findAllDescriptors(final Product product) {
		final List<PriceListDescriptorDTO> result = new ArrayList<PriceListDescriptorDTO>();
		for (Catalog catalog : product.getCatalogs()) {
			for (Currency currency : getAllCurrenciesFor(catalog)) {
				result.addAll(findAllDescriptors(catalog, currency));
			}
		}
		// need to remove duplicates that are created in findAllDescriptors(catalog, currency)
		for (int index = 0; index < result.size(); index++) {
			final PriceListDescriptorDTO currentPld = result.get(index);
			for (int subIndex = index + 1; subIndex < result.size(); subIndex++) {
				final PriceListDescriptorDTO checkPld = result.get(subIndex);
				if (currentPld.getGuid().equals(checkPld.getGuid())) {
					result.remove(subIndex);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Finds {@link PriceListDescriptorDTO}s for {@link Catalog}.
	 * 
	 * @param catalog the {@link Catalog} instance
	 * @return List of <code>PriceListDescriptorDTO</code>s
	 */
	public List<PriceListDescriptorDTO> findAllDescriptors(final Catalog catalog) {
		return priceListService.listByCatalog(catalog);
	}

	@Override
	public void removePricesForProduct(final Product product) {
		priceListService.modifyBaseAmountChangeSet(prepareChangeSetForProduct(product));
	}

	/**
	 * Prepares the ChangeSet List.
	 * 
	 * @param product {@link Product} instance.
	 * @return List of ChangeSets
	 */
	ChangeSetObjects<BaseAmountDTO> prepareChangeSetForProduct(final Product product) {
		ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet = new ChangeSetObjectsImpl<BaseAmountDTO>();
		baseAmountChangeSet.getRemovalList().addAll(findProductBaseAmounts(null, product.getCode()));
		if (product.hasMultipleSkus()) {
			for (ProductSku productSku : product.getProductSkus().values()) {
				baseAmountChangeSet.getRemovalList().addAll(findProductSkuBaseAmounts(null, productSku.getSkuCode()));
			}
		}
		return baseAmountChangeSet;
	}
	
	@Override
	public void removePricesForProductSkus(final Collection<ProductSku> productSkus) {
		ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet = new ChangeSetObjectsImpl<BaseAmountDTO>();
		for (ProductSku productSku : productSkus) {
			baseAmountChangeSet.getRemovalList().addAll(findProductSkuBaseAmounts(null, productSku.getSkuCode()));
		}
		priceListService.modifyBaseAmountChangeSet(baseAmountChangeSet);
	}
	
	@Override
	public List<PriceListDescriptorDTO> findAllDescriptors(final Catalog catalog, final Currency currency) {
		PriceListStack stack = null;
		if (catalog != null && currency != null) {
			stack = priceListLookupService.getPriceListStack(catalog.getCode(), currency, null); 
		}
		if (stack != null)  {
			return createPldDtos(currency, stack.getPriceListStack());
		}
		return Collections.emptyList();
	}

	private List<PriceListDescriptorDTO> createPldDtos(final Currency currency, final List<String> pldGuids) {
		List<PriceListDescriptorDTO> descriptors = new ArrayList<PriceListDescriptorDTO>();
		for (String pldGuid : pldGuids) {
			PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
			priceListDescriptorDTO.setGuid(pldGuid);
			priceListDescriptorDTO.setCurrencyCode(currency.getCurrencyCode());
			descriptors.add(priceListDescriptorDTO);
		}
		return descriptors;
	}
	
	/**
	 * Finds base amounts by given price list guid and product guid.
	 *
	 * @param priceListGuid price list guid
	 * @param productGuid product guid
	 * @param objectType the object type of the base amount being searched
	 * @return list of base amounts
	 */
	List<BaseAmountDTO> findBaseAmounts(final String priceListGuid, final String productGuid, final String objectType) {
		final BaseAmountFilter baseAmountFilter = getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		baseAmountFilter.setPriceListDescriptorGuid(priceListGuid);
		baseAmountFilter.setObjectGuid(productGuid);
		baseAmountFilter.setObjectType(objectType);
		return findBaseAmounts(baseAmountFilter);
	}
	
	/**
	 * Finds base amounts by given {@link BaseAmountFilter}.
	 * @param baseAmountFilter the {@link BaseAmountFilter} 
	 * @return list of base amounts
	 */
	List<BaseAmountDTO> findBaseAmounts(final BaseAmountFilter baseAmountFilter) {
		return new ArrayList<BaseAmountDTO>(priceListService.getBaseAmounts(baseAmountFilter));		
	}
	
	/**
	 * Get or create new base amount dto collection for given price list descriptor dto.
	 * @param descriptorAmountsMap between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO} collection. 
	 * @param priceListDescriptorDTO given price list descriptor dto.
	 * @return base amount dto collection.
	 */
	private List<BaseAmountDTO> getBaseAmounts(
			final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> descriptorAmountsMap,
			final PriceListDescriptorDTO priceListDescriptorDTO) {
		List<BaseAmountDTO> amounts = descriptorAmountsMap.get(priceListDescriptorDTO);
		if (amounts == null) {
			amounts = new ArrayList<BaseAmountDTO>();
			descriptorAmountsMap.put(priceListDescriptorDTO, amounts);
		}
		return amounts;
	}

	/**
	 * Get all the {@link PriceListDescriptorDTO}s for the <code>baseAmounts</code>.
	 * @param baseAmounts the baseAmounts to get the PriceListDescriptorDTO's for. 
	 * @return map of guid to price list descriptor DTOs.
	 */
	private Map<String, PriceListDescriptorDTO> getPriceListDescriptorDTOs(final List<BaseAmountDTO> baseAmounts) {
		List<String> guids = new ArrayList<String>();
		for (BaseAmountDTO dto : baseAmounts) {
			guids.add(dto.getPriceListDescriptorGuid());
		}
		Map<String, PriceListDescriptorDTO> resultMap = new HashMap<String, PriceListDescriptorDTO>();
		for (PriceListDescriptorDTO dto : priceListService.getPriceListDescriptors(guids)) {
			resultMap.put(dto.getGuid(), dto);
		}
		return resultMap;
	}	
	
	
	/** Calls {@link #findBaseAmounts(String, String, PRODUCT_TYPE)}. */
	private List<BaseAmountDTO> findProductBaseAmounts(final String priceListGuid, final String productGuid) {
		return findBaseAmounts(priceListGuid, productGuid, PRODUCT_TYPE);
	}
	
	/** Calls {@link #findBaseAmounts(String, String, PRODUCT_SKU_TYPE)}. */
	private List<BaseAmountDTO> findProductSkuBaseAmounts(final String priceListGuid, final String productGuid) {
		return findBaseAmounts(priceListGuid, productGuid, PRODUCT_SKU_TYPE);
	}

	/**
	 * @param priceListLookupService the priceListLookupService to set
	 */
	public void setPriceListLookupService(final PriceListLookupService priceListLookupService) {
		this.priceListLookupService = priceListLookupService;
	}

	/**
	 * @param priceListService the priceListService to set
	 */
	public void setPriceListService(final PriceListService priceListService) {
		this.priceListService = priceListService;
	}
	
	/**
	 * Returns all supported currencies for catalog. 
	 * 
	 * @param catalog - catalog 
	 * @return set of supported currencies.
	 */
	public Set<Currency> getAllCurrenciesFor(final Catalog catalog) {
		if (null == catalog) {
			return null;
		}
		List<PriceListDescriptorDTO> allPriceListDescriptors = priceListService.listByCatalog(catalog);
		if (CollectionUtils.isEmpty(allPriceListDescriptors)) {
			return Collections.emptySet();
		}
		Set<Currency> allCatalogCurrencies = new LinkedHashSet<Currency>();
			for (int i = 0; i < allPriceListDescriptors.size(); i++) {
				PriceListDescriptorDTO priceListAssignment = allPriceListDescriptors.get(i);
				String currencyCode = priceListAssignment.getCurrencyCode();
				Currency currency = Currency.getInstance(currencyCode);
				allCatalogCurrencies.add(currency);
			}
		return allCatalogCurrencies;
	}	
	
	
	/**
	 * Returns default currency for catalog.
	 * @param catalog - catalog
	 * @return default currency
	 */
	public Currency getDefaultCurrencyFor(final Catalog catalog) {
		if (null == catalog) {
			return null;
		}
		Set<Currency> currencies = getAllCurrenciesFor(catalog);
		if (currencies.isEmpty()) {
			return null;
		}
		return currencies.iterator().next();
	}

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final ConstituentItem constituentItem,
			final List<PriceListDescriptorDTO> priceListDescriptors) {
		if (constituentItem.isProductSku()) {
			return getPriceListMap(constituentItem.getProductSku(), priceListDescriptors);
		}
		return getPriceListMap(constituentItem.getProduct(), priceListDescriptors);
	}

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final ConstituentItem constituentItem) {
		if (constituentItem.isProductSku()) {
			return getPriceListMap(constituentItem.getProductSku());
		}
		return getPriceListMap(constituentItem.getProduct());
	}

}
