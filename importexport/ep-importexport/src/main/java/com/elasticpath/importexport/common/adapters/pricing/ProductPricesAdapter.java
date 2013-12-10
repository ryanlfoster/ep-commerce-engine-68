package com.elasticpath.importexport.common.adapters.pricing;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.pricing.ProductPricesDTO;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between <code>Product</code> and
 * <code>CatalogPricesDTO</code> objects.
 */
public class ProductPricesAdapter extends AbstractDomainAdapterImpl<Product, ProductPricesDTO> {

	// private static final Logger LOG = Logger.getLogger(ProductPricesAdapter.class);

	@Override
	public void populateDTO(final Product product, final ProductPricesDTO productPrices) {
		// productPrices.setProductCode(product.getCode());
		
		// TODO : implement DTO Population from PriceLists to ProductPricesDTO
	}

	@Override
	public void populateDomain(final ProductPricesDTO catalogPricesDto, final Product product) {
		// TODO : implement Domain Population from ProductPricesDTO to PriceLists
	}

	@Override
	public ProductPricesDTO createDtoObject() {
		return new ProductPricesDTO();
	}

}
