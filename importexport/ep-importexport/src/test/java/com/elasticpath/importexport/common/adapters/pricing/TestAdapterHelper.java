package com.elasticpath.importexport.common.adapters.pricing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.importexport.common.dto.catalogs.AttributeDTO;
import com.elasticpath.importexport.common.dto.catalogs.BrandDTO;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.CategoryTypeDTO;
import com.elasticpath.importexport.common.dto.pricing.CurrencyPriceDTO;
import com.elasticpath.importexport.common.dto.pricing.PriceAlias;
import com.elasticpath.importexport.common.dto.pricing.PriceDTO;
import com.elasticpath.importexport.common.dto.pricing.PriceTierDTO;
import com.elasticpath.importexport.common.dto.products.ProductDTO;

/**
 * Helper class for adapters unit tests.
 */
public class TestAdapterHelper {
	
	private static final String PRODUCT_CODE = "product_code";

	/**
	 * Creates catalog with code.
	 * 
	 * @param code the catalog code
	 * @return new catalog
	 */
	public Catalog createCatalog(final String code) {
		Catalog catalog = new CatalogImpl();
		catalog.setCode(code);		
		return catalog;
	}
	
	/**
	 * Creates category with code.
	 * 
	 * @param code the category code
	 * @return new category
	 */
	public Category createCategory(final String code) {
		Category category = new CategoryImpl();
		category.setCode(code);
		return category;
	}
	
	/**
	 * Creates product price tier.
	 * 
	 * @return the PriceTier
	 */
	public PriceTier createProductPriceTier() {
		PriceTier priceTier = new PriceTierImpl();
		priceTier.setMinQty(1);
		priceTier.setListPrice(new BigDecimal("2.0"));
		priceTier.setSalePrice(new BigDecimal("3.0"));
		return priceTier;
	}
	
	/**
	 * Creates product price tier DTO.
	 * 
	 * @return the PriceTierDTO
	 */
	public PriceTierDTO createProductPriceTierDTO() {
		PriceTierDTO priceTierDto = new PriceTierDTO();
		priceTierDto.setQty(1);
		
		PriceDTO listPriceDTO = new PriceDTO();
		listPriceDTO.setAlias(PriceAlias.list);
		listPriceDTO.setValue(new BigDecimal("3.0"));
		
		PriceDTO salePriceDTO = new PriceDTO();
		salePriceDTO.setAlias(PriceAlias.sale);
		salePriceDTO.setValue(new BigDecimal("2.0"));
		
		List<PriceDTO> priceDtoList = new ArrayList<PriceDTO>();
		priceDtoList.add(listPriceDTO);
		priceDtoList.add(salePriceDTO);
		priceTierDto.setPriceDtoList(priceDtoList);
		return priceTierDto;
	}

	/**
	 * Creates product price.
	 * 
	 * @return the product price
	 */
	public Price createProductPrice() {
		Price price = new PriceImpl();
		Map<Integer, PriceTier> priceTiers = new HashMap<Integer, PriceTier>();
		PriceTier priceTier = createProductPriceTier();

		priceTiers.put(1, priceTier);
		price.setPersistentPriceTiers(priceTiers);
		price.setCurrency(Currency.getInstance("CAD"));
		return price;
	}
	
	/**
	 * Creates product price DTO.
	 * 
	 * @return the product price DTO
	 */
	public CurrencyPriceDTO createProductPriceDTO() {
		CurrencyPriceDTO priceDTO = new CurrencyPriceDTO();
		List<PriceTierDTO> priceTierDTOs = new ArrayList<PriceTierDTO>();
		PriceTierDTO priceTierDTO = createProductPriceTierDTO();

		priceTierDTOs.add(priceTierDTO);
		priceDTO.setTierList(priceTierDTOs);
		priceDTO.setCurrencyCode("CAD");
		return priceDTO;
	}

	/**
	 * Create prices.
	 * 
	 * @return Map between Currency and Price
	 */
	public Map<Currency, Price> createPrices() {
		Map<Currency, Price> prices = new HashMap<Currency, Price>();
		Price price = createProductPrice();
		prices.put(price.getCurrency(), price);
		return prices;
	}

	/**
	 * Create price DTOs.
	 * 
	 * @return List of ProductPriceDTO
	 */
	public List<CurrencyPriceDTO> createPriceDTOs() {
		List<CurrencyPriceDTO> priceDTOs = new ArrayList<CurrencyPriceDTO>();
		CurrencyPriceDTO priceDTO = createProductPriceDTO();
		priceDTOs.add(priceDTO);
		return priceDTOs;
	}
		
	/**
	 * Creates product DTO.
	 * @return the product DTO
	 */
	public ProductDTO createProductDTO() {
		ProductDTO productDTO = new ProductDTO();
		productDTO.setCode(PRODUCT_CODE);
		return productDTO; 
	}
	
	/**
	 * Creates catalog DTO.
	 * 
	 * @param code the catalog code
	 * @return the catalog DTO
	 */
	public CatalogDTO createCatalogDTO(final String code) {
		CatalogDTO catalogDTO = new CatalogDTO();
		catalogDTO.setCode(code);
		
		BrandDTO brandDTO = new BrandDTO();
		brandDTO.setCode("b_code");
		List<BrandDTO> brandList = new ArrayList<BrandDTO>();
		brandList.add(brandDTO);
		catalogDTO.setBrands(brandList);
		
		AttributeDTO attributeDTO = new AttributeDTO();
		attributeDTO.setKey("a_key");
		List<AttributeDTO> attributeList = new ArrayList<AttributeDTO>();
		attributeList.add(attributeDTO);
		catalogDTO.setAttributes(attributeList);
		
		CategoryTypeDTO categoryTypeDTO = new CategoryTypeDTO();
		categoryTypeDTO.setName("cat_name");
		List<CategoryTypeDTO> categoryTypeList = new ArrayList<CategoryTypeDTO>();
		categoryTypeList.add(categoryTypeDTO);
		catalogDTO.setCategoryTypes(categoryTypeList);
		
		return catalogDTO; 
	}
	
	/**
	 * Creates product.
	 * @return the product
	 */
	public Product createProduct() {
		Product product = new ProductImpl();
		product.setCode(PRODUCT_CODE);
		return product; 
	}
}
