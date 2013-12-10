package com.elasticpath.common.pricing.service;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Interface of Service that is used as facade to {@link PriceListService} to minimise round trips to server by client. 
 */
public interface PriceListHelperService {

	/**
	 * Gets {@link BaseAmountDTO}s associated with {@link PriceListDescriptorDTO}s for given {@link Product}.
	 *  
	 * @param product the <code>Product</code> instance.
	 * @return <code>Map</code> between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO}
	 * @see #getPriceListMap(Product, boolean) when masterOnly is false
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final Product product);
	
	/**
	 * Gets {@link BaseAmountDTO}s associated with {@link PriceListDescriptorDTO}s for given {@link Product}.
	 * 
	 * @param product the <code>Product</code> instance.
	 * @param masterOnly if true the only Master Catalog's descriptor will be loaded and shipped with corresponding BaseAmounts, otherwise all. 
	 * @return <code>Map</code> between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO}
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final Product product, boolean masterOnly);

	/**
	 * Gets {@link BaseAmountDTO}s associated with {@link PriceListDescriptorDTO}s for given {@link Product}.
	 * 
	 * @param product the <code>Product</code> instance.
	 * @param priceListDescriptors price lists descriptors that are associated with the given product
	 * @return <code>Map</code> between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO}
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(Product product, List<PriceListDescriptorDTO> priceListDescriptors);

	
	/**
	 * Gets {@link BaseAmountDTO}s associated with {@link PriceListDescriptorDTO}s for {@link ProductSku} and {@link Product} of this SKU.
	 *  
	 * @param productSku the <code>ProductSku</code> instance.
	 * @return <code>Map</code> between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO}
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final ProductSku productSku);
	
	/**
	 * Gets {@link BaseAmountDTO}s associated with {@link PriceListDescriptorDTO}s for given bundle {@link ConstituentItem}.
	 * 
	 * @param constituentItem the <code>ConstituentItem</code> instance.
	 * @param priceListDescriptors price lists descriptors that are associated with the given product
	 * @return <code>Map</code> between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO}
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(ConstituentItem constituentItem, 
			List<PriceListDescriptorDTO> priceListDescriptors);
	
	/**
	 * Gets {@link BaseAmountDTO}s associated with {@link PriceListDescriptorDTO}s for {@link ConstituentItem}.
	 *  
	 * @param constituentItem the <code>ConstituentItem</code> instance.
	 * @return <code>Map</code> between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO}
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final ConstituentItem constituentItem);
	
	/**
	 * Gets {@link BaseAmountDTO}s associated with {@link PriceListDescriptorDTO}s for given {@link ProductSku}.
	 * 
	 * @param productSku the <code>ProductSku</code> instance.
	 * @param priceListDescriptors price lists descriptors that are associated with the given product
	 * @return <code>Map</code> between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO}
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(ProductSku productSku, List<PriceListDescriptorDTO> priceListDescriptors);
	
	/**
	 *  
	 * Gets the actual Price Information Map for given base amount filer.
	 * Will include only the price list descriptor for which its price list assignments are visible.
	 * 
	 * @param filter the {@link BaseAmountFilter}
	 * @param currencyCodes the currency codes to filter out by 
	 * @return <code>Map</code> between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO}
	 * 
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(BaseAmountFilter filter, String ... currencyCodes);	

	/**
	 * Processes the the collection of {@link BaseAmountChangeSet}s using <code>PriceListService</code>.
	 * 
	 * @param baseAmountChangeSetCollection collection of BaseAmountChangeSet.
	 * @throws com.elasticpath.domain.pricing.exceptions.DuplicateBaseAmountException if the given changeset
	 * includes instructions to add duplicate BaseAmounts or a BaseAmount that is already persisted
	 * @throws com.elasticpath.base.exception.EpServiceException on error
	 */
	void processBaseAmountChangeSets(final Collection<ChangeSetObjects<BaseAmountDTO>> baseAmountChangeSetCollection);
	
	/**
	 * Removes all {@code BaseAmount} entries for the given product, including
	 * its associated sku's {@code BaseAmount}s.
	 *  
	 * @param product the <code>Product</code> instance.
	 */
	void removePricesForProduct(final Product product);
	
	/**
	 * Removes all {@code BaseAmount} entries for the given collection of Skus.
	 * 
	 * @param productSkus the collection of <code>ProductSku</code>
	 */
	void removePricesForProductSkus(final Collection<ProductSku> productSkus);
	
	/**
	 * Finds all price list descriptors which belong to given product.
	 *
	 * @param product the product to find price list descriptors for
	 * @return list of price list descriptors
	 */
	List<PriceListDescriptorDTO> findAllDescriptors(final Product product);	
	
	/**
	 * Finds {@link PriceListDescriptorDTO}s for {@link Catalog}.
	 * 
	 * @param catalog the {@link Catalog} instance
	 * @return List of <code>PriceListDescriptorDTO</code>s
	 */
	List<PriceListDescriptorDTO> findAllDescriptors(final Catalog catalog);
	
	/**
	 * Finds all <code>PriceListDescriptorDTO</code>s for given parameters. DTOs contain only GUID.
	 *
	 * @param catalog Catalog to find descriptor GUIDs
	 * @param currency Currency to find descriptor GUIDs
	 * @return <code>List</code> encapsulating <code>PriceListDescriptor</code>
	 */
	List<PriceListDescriptorDTO> findAllDescriptors(final Catalog catalog, final Currency currency);

	/**
	 * Returns all supported currencies for catalog. 
	 * 
	 * @param catalog - catalog 
	 * @return set of supported currencies.
	 */
	Set<Currency> getAllCurrenciesFor(final Catalog catalog);
	
	/**
	 * Returns default currency for catalog.
	 * @param catalog - catalog
	 * @return default currency
	 */
	Currency getDefaultCurrencyFor(final Catalog catalog);

}