package com.elasticpath.importexport.common.adapters.products;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.adapters.products.data.AttributeGroupAdapter;
import com.elasticpath.importexport.common.adapters.products.data.ProductAvailabilityAdapter;
import com.elasticpath.importexport.common.adapters.products.data.ProductSkuAdapter;
import com.elasticpath.importexport.common.adapters.products.data.SeoAdapter;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.general.PricingMechanismValues;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.ProductAvailabilityDTO;
import com.elasticpath.importexport.common.dto.products.ProductDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuDTO;
import com.elasticpath.importexport.common.dto.products.SeoDTO;
import com.elasticpath.importexport.common.dto.products.SkuOptionDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.importexport.common.policies.ImportExportLocaleFallbackPolicyFactory;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.misc.TimeService;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between <code>Product</code> and
 * <code>ProductDTO</code> objects.
 */
public class ProductAdapter extends AbstractDomainAdapterImpl<Product, ProductDTO> {

	private SeoAdapter seoAdapter;

	private ProductAvailabilityAdapter productAvailabilityAdapter;

	private ProductSkuAdapter productSkuAdapter;

	private AttributeGroupAdapter attributeGroupAdapter;

	private ProductSkuService productSkuService;

	private TimeService timeService;

	private static final Logger LOG = Logger.getLogger(ProductAdapter.class);

	/**
	 * {@inheritDoc}
	 *
	 * @throws PopulationRuntimeException in case of incorrect productDto
	 */
	public void populateDomain(final ProductDTO productDto, final Product productDomain) {
		checkProductCode(productDto);
		checkBundleSign(productDto, productDomain);

		final CachingService cachingService = getCachingService();

		productDomain.setCode(productDto.getCode());
		productDomain.setImage(productDto.getImage());
		productDomain.setLastModifiedDate(timeService.getCurrentTime());

		populateProductType(productDto, productDomain, cachingService);
		populateTaxCode(productDto, productDomain, cachingService);
		populateBrand(productDto, productDomain, cachingService);
		populateDomainNameValues(productDto, productDomain);
		populateExtra(productDto, productDomain);

		validateProductAttributes(productDomain);
		populatePricingMechanismForBundle(productDto, productDomain);
	}


	/** Populate the product with seo, product availability, skus, attributes.
	 * @param productDto the dto resulted from xml
	 * @param productDomain the domain object we are trying to recreate from dto
	 * @throws PopulationRuntimeException in case of incorrect productDto
	 */
	protected void populateExtra(final ProductDTO productDto, final Product productDomain) {
		SeoDTO seoDTO = productDto.getSeoDTO();
		try {
			seoAdapter.populateDomain(seoDTO, productDomain);
			productAvailabilityAdapter.populateDomain(productDto.getProductAvailability(), productDomain);
			populateDomainProductSkus(productDto, productDomain);
			attributeGroupAdapter.populateDomain(productDto.getProductAttributes(), productDomain.getAttributeValueGroup());
		} catch (PopulationRollbackException exception) {
			// Report that the product failed to import
			LOG.error(new Message("IE-10320", productDomain.getCode()));
			throw exception;
		} catch (PopulationRuntimeException exception) {
			// Report that the product failed to import
			LOG.error(new Message("IE-10320", productDomain.getCode()));
			throw exception;
		}
	}


	/** Populate the product with the TaxCode.
	 * @param productDto the dto resulted from xml
	 * @param productDomain the domain object we are trying to recreate from dto
	 * @param cachingService cachingService used to retrieve the data from DB
	 * @throws PopulationRuntimeException in case of incorrect productDto
	 */
	protected void populateTaxCode(final ProductDTO productDto, final Product productDomain, final CachingService cachingService) {
		TaxCode taxCode = null;
		if (productDto.getTaxCode() != null) {
			taxCode = cachingService.findTaxCodeByCode(productDto.getTaxCode());
			if (taxCode == null) {
				throw new PopulationRuntimeException("IE-10302", productDto.getTaxCode());
			}
		}
		productDomain.setTaxCode(taxCode);
	}

	/** Populate the product with the ProductType.
	 * @param productDto the dto resulted from xml
	 * @param productDomain the domain object we are trying to recreate from dto
	 * @param cachingService cachingService used to retrieve the data from DB
	 * @throws PopulationRuntimeException in case of incorrect productDto
	 */
	protected void populateProductType(final ProductDTO productDto, final Product productDomain, final CachingService cachingService) {
		ProductType productType = cachingService.findProductTypeByName(productDto.getType());
		if (productType == null) {
			throw new PopulationRuntimeException("IE-10301", productDto.getType());
		}

		productDomain.setProductType(productType);
	}

	/** Populate the product with the Brand.
	 * @param productDto the dto resulted from xml
	 * @param productDomain the domain object we are trying to recreate from dto
	 * @param cachingService cachingService used to retrieve the data from DB
	 * @throws PopulationRuntimeException in case of incorrect productDto
	 */
	protected void populateBrand(final ProductDTO productDto, final Product productDomain, final CachingService cachingService) {
		Brand brand = null;
		if (productDto.getBrand() != null) {
			brand = cachingService.findBrandByCode(productDto.getBrand());
			if (brand == null) {
				throw new PopulationRuntimeException("IE-10303", productDto.getBrand());
			}
		}
		productDomain.setBrand(brand);
	}

	/** Populates the pricing mechanism of a bundle product. Value is retrieved from attribute from <code>product</code> element.
	 * @param productDto the dto resulted from xml
	 * @param productDomain the domain object we are trying to recreate from dto
	 */
	protected void populatePricingMechanismForBundle(final ProductDTO productDto, final Product productDomain) {
		// Set the pricing mechanism for bundles. We are ignoring the pricingMechanism attribute if attribute bundle = "false"
		// If an attribute of pricingMechanism doesn't exist will default to "assigned"
		if (productDomain instanceof ProductBundle) {
			if (PricingMechanismValues.CALCULATED == productDto.getPricingMechanism()) {
				((ProductBundle) productDomain).setCalculated(true);
			} else {
				((ProductBundle) productDomain).setCalculated(false);
			}
		}
	}

	/**
	 * Validates that all the attributes on the product are associated with the product type.
	 * @param productDomain The product to validate.
	 * @throws PopulationRollbackException If the product has an attribute which is not in the product type.
	 */
	protected void validateProductAttributes(final Product productDomain) {
		ProductType productType = productDomain.getProductType();
		Set<AttributeGroupAttribute> typeAttributeGroupAttributes = productType.getProductAttributeGroupAttributes();

		// Need to make a set of the attribute keys for comparison at the next step.
		Set<String> typeAttributeKeys = new HashSet<String>(typeAttributeGroupAttributes.size());
		for (AttributeGroupAttribute attributeGroupAttribute : typeAttributeGroupAttributes) {
			typeAttributeKeys.add(attributeGroupAttribute.getAttribute().getKey());
		}

		Map<String, AttributeValue> attributeValueMap = productDomain.getAttributeValueMap();
		for (AttributeValue value : attributeValueMap.values()) {
			String attributeKey = value.getAttribute().getKey();
			if (!typeAttributeKeys.contains(attributeKey)) {
				throw new PopulationRollbackException("IE-10328", productDomain.getCode(), attributeKey, productType.getName());
			}
		}

	}

	private void checkProductCode(final ProductDTO productDto) {
		if ("".equals(productDto.getCode())) {
			throw new PopulationRuntimeException("IE-10300");
		}
	}

	private void checkBundleSign(final ProductDTO productDto,
			final Product productDomain) {
		if (productDto.isBundle() != productDomain instanceof ProductBundle) {
			throw new PopulationRuntimeException("IE-10324", productDto.getCode());
		}
	}

	private void populateDomainProductSkus(final ProductDTO productDto, final Product productDomain) {
		final List<Map<String, String>> productSkuOptions = new ArrayList<Map<String, String>>();

		for (ProductSkuDTO productSkuDto : productDto.getProductSkus()) {
			
			ProductSku productSku = productDomain.getSkuByGuid(productSkuDto.getGuid());
			
			// If this is a new sku for the product, validate that another sku does not already exist.
			// If all is good, then create a new instance of ProductSku for it.
			if (productSku == null) {
				
				// Throw an exception if another sku with same guid exists
				String productSkuDtoGuid = productSkuDto.getGuid();
				if (productSkuService.findByGuid(productSkuDtoGuid) != null) {
					throw new PopulationRollbackException("IE-10304", productSkuDtoGuid);
				}
			
				// Throw an exception if another sku with same code exists
				String productSkuDtoCode = productSkuDto.getSkuCode();  
				if (productSkuService.findBySkuCode(productSkuDtoCode) != null) {
					throw new PopulationRollbackException("IE-10333", productSkuDtoCode);
				}

				productSku = getBean(ContextIdNames.PRODUCT_SKU);
			}

			productSkuAdapter.populateDomain(productSkuDto, productSku);

			productDomain.addOrUpdateSku(productSku);

			checkSkuOptionValues(productSkuOptions, productSkuDto.getSkuOptionList());
		}
	}


	private void checkSkuOptionValues(final List<Map<String, String>> productSkuOptions, final List<SkuOptionDTO> skuOptionList) {
		final Map<String, String> skuOptions = new HashMap<String, String>();

		for (SkuOptionDTO skuOptionDTO : skuOptionList) {
			skuOptions.put(skuOptionDTO.getCode(), skuOptionDTO.getSkuOptionValue());
		}

		for (Map<String, String> skuOptionMap : productSkuOptions) {
			if (!skuOptionMap.isEmpty() && skuOptionMap.equals(skuOptions)) {
				throw new PopulationRuntimeException("IE-10305");
			}
		}

		productSkuOptions.add(skuOptions);
	}

	private void populateDomainNameValues(final ProductDTO productDto, final Product productDomain) {
		List<DisplayValue> nameValues = productDto.getNameValues();
		for (DisplayValue displayValue : nameValues) {
			try {
				Locale locale = LocaleUtils.toLocale(displayValue.getLanguage());
				if (!LocaleUtils.isAvailableLocale(locale)) {
					throw new IllegalArgumentException();
				}
				checkLocaleSupportedByCatalog(locale, productDomain);
				ImportExportLocaleFallbackPolicyFactory factory = getBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY);
				LocaleDependantFields dependantFields  = productDomain.getLocaleDependantFields(
						factory.createProductLocaleFallbackPolicy(locale, false, productDomain));
				dependantFields.setDisplayName(displayValue.getValue());
				productDomain.addOrUpdateLocaleDependantFields(dependantFields);
			} catch (IllegalArgumentException exception) {
				throw new PopulationRuntimeException("IE-10306", exception, displayValue.getLanguage(), displayValue.getValue());
			}
		}
	}

	/**
	 * @throws PopulationRuntimeException if Catalog is not null and Locale is not supported.
	 */
	private void checkLocaleSupportedByCatalog(final Locale locale, final Product product) {
		for (Category category : product.getCategories()) {
			final Catalog catalog = category.getCatalog();
			if (catalog != null && !catalog.getSupportedLocales().contains(locale)) {
				throw new PopulationRuntimeException("IE-10000", locale.toString());
			}
		}
	}

	@Override
	public void populateDTO(final Product product, final ProductDTO productDTO) {
		productDTO.setCode(product.getCode());
		productDTO.setImage(product.getImage());

		final Catalog catalog = product.getMasterCatalog();

		Collection<Locale> supportedLocales = Collections.emptyList();
		if (catalog != null) {
			supportedLocales = catalog.getSupportedLocales();
		}

		populateDtoNameValues(product, productDTO, supportedLocales);

		seoAdapter.setSupportedLocales(supportedLocales);
		SeoDTO seoDto = new SeoDTO();
		seoAdapter.populateDTO(product, seoDto);
		productDTO.setSeoDto(seoDto);

		productDTO.setType(product.getProductType().getName());

		final TaxCode taxCode = product.getTaxCode();
		if (taxCode != null) {
			productDTO.setTaxCode(taxCode.getCode());
		}

		final Brand brand = product.getBrand();
		if (brand != null) {
			productDTO.setBrand(brand.getCode());
		}

		ProductAvailabilityDTO productAvailabilityDTO = new ProductAvailabilityDTO();
		productAvailabilityAdapter.populateDTO(product, productAvailabilityDTO);
		productDTO.setProductAvailability(productAvailabilityDTO);

		AttributeGroupDTO attributeGroupDto = new AttributeGroupDTO();
		attributeGroupAdapter.populateDTO(product.getAttributeValueGroup(), attributeGroupDto);
		productDTO.setProductAttributes(attributeGroupDto);

		populateDtoProductSkusList(product, productDTO);

		populateBundleSign(product, productDTO);
		populateBundlePricingMechanism(product, productDTO);

	}

	private void populateBundleSign(final Product product, final ProductDTO productDTO) {
		if (product instanceof ProductBundle) {
			productDTO.setBundle(Boolean.TRUE);
		}
	}

	private void populateBundlePricingMechanism(final Product product, final ProductDTO productDTO) {
		if (product instanceof ProductBundle) {
			if (((ProductBundle) product).isCalculated()) {
				productDTO.setPricingMechanism(PricingMechanismValues.CALCULATED);
			} else {
				productDTO.setPricingMechanism(PricingMechanismValues.ASSIGNED);
			}
		}
	}

	private void populateDtoProductSkusList(final Product product, final ProductDTO productDTO) {
		Map<String, ProductSku> productSkuMap = product.getProductSkus();
		List<ProductSkuDTO> productSkus = new ArrayList<ProductSkuDTO>();
		for (Entry<String, ProductSku> entry : productSkuMap.entrySet()) {
			ProductSkuDTO productSkuDTO = new ProductSkuDTO();
			productSkuAdapter.populateDTO(entry.getValue(), productSkuDTO);
			productSkus.add(productSkuDTO);
		}
		productDTO.setProductSkus(productSkus);
	}

	private void populateDtoNameValues(final Product product, final ProductDTO productDTO, final Collection<Locale> supportedLocales) {
		final List<DisplayValue> nameValues = new ArrayList<DisplayValue>();
		for (Locale locale : supportedLocales) {
			LocaleDependantFields fields = product.getLocaleDependantFieldsWithoutFallBack(locale);
			DisplayValue value = new DisplayValue(locale.toString(), fields.getDisplayName());
			nameValues.add(value);
		}
		productDTO.setNameValues(nameValues);
	}

	@Override
	public Product buildDomain(final ProductDTO source, final Product target) {
		Product chosenDomain = chooseDomain(source, target);

		populateDomain(source, chosenDomain);

		return chosenDomain;
	}

	private Product chooseDomain(final ProductDTO source, final Product target) {
		if (target == null) {
			if (source.isBundle()) {
				return getBean(ContextIdNames.PRODUCT_BUNDLE);
			}

			return getBean(ContextIdNames.PRODUCT);
		}
		return target;
	}

	/**
	 * Creates empty <code>Product</code>.
	 *
	 * @return new product
	 */
	@Override
	public Product createDomainObject() {
		return null;
	}

	/**
	 * Creates empty <code>ProductDTO</code>.
	 *
	 * @return new product DTO
	 */
	@Override
	public ProductDTO createDtoObject() {
		return new ProductDTO();
	}

	/**
	 * Gets the seoAdapter.
	 *
	 * @return the seoAdapter
	 * @see SeoAdapter
	 */
	public SeoAdapter getSeoAdapter() {
		return seoAdapter;
	}

	/**
	 * Sets the seoAdapter.
	 *
	 * @param seoAdapter the seoAdapter to set
	 * @see SeoAdapter
	 */
	public void setSeoAdapter(final SeoAdapter seoAdapter) {
		this.seoAdapter = seoAdapter;
	}

	/**
	 * Gets the productAvailabilityAdapter.
	 *
	 * @return the productAvailabilityAdapter
	 * @see ProductAvailabilityAdapter
	 */
	public ProductAvailabilityAdapter getProductAvailabilityAdapter() {
		return productAvailabilityAdapter;
	}

	/**
	 * Sets the productAvailabilityAdapter.
	 *
	 * @param productAvailabilityAdapter the productAvailabilityAdapter to set
	 * @see ProductAvailabilityAdapter
	 */
	public void setProductAvailabilityAdapter(final ProductAvailabilityAdapter productAvailabilityAdapter) {
		this.productAvailabilityAdapter = productAvailabilityAdapter;
	}

	/**
	 * Gets the productSkuAdapter.
	 *
	 * @return the productSkuAdapter
	 * @see ProductSkuAdapter
	 */
	public ProductSkuAdapter getProductSkuAdapter() {
		return productSkuAdapter;
	}

	/**
	 * Sets the productSkuAdapter.
	 *
	 * @param productSkuAdapter the productSkuAdapter to set
	 * @see ProductSkuAdapter
	 */
	public void setProductSkuAdapter(final ProductSkuAdapter productSkuAdapter) {
		this.productSkuAdapter = productSkuAdapter;
	}

	/**
	 * Gets the attributeGroupAdapter.
	 *
	 * @return the attributeGroupAdapter
	 */
	public AttributeGroupAdapter getAttributeGroupAdapter() {
		return attributeGroupAdapter;
	}

	/**
	 * Sets the attributeGroupAdapter.
	 *
	 * @param attributeGroupAdapter the attributeGroupAdapter to set
	 * @see AttributeGroupAdapter
	 */
	public void setAttributeGroupAdapter(final AttributeGroupAdapter attributeGroupAdapter) {
		this.attributeGroupAdapter = attributeGroupAdapter;
	}

	/**
	 * Gets the ProductSkuService.
	 *
	 * @return the productSkuService
	 */
	public final ProductSkuService getProductSkuService() {
		return productSkuService;
	}

	/**
	 * Sets the ProductSkuService.
	 *
	 * @param productSkuService the ProductSkuService instance
	 * @see ProductSkuService
	 */
	public final void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	/**
	 * Gets the timeService.
	 *
	 * @return the timeService
	 */
	public final TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Sets the TimeService.	 *
	 * @param timeService the TimeService instance
	 * @see TimeService
	 */
	public final void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}
}
