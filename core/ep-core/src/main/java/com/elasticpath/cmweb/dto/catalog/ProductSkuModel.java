package com.elasticpath.cmweb.dto.catalog;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Represents the model of the product sku being edited with the product price model.
 */
public class ProductSkuModel extends AbstractProductModel {

	private static final long serialVersionUID = 1L;

	private final ProductSku productSku;

	/**
	 * Creates the product sku editor model based on product sku object and price list model.
	 * 
	 * @param productSku the product slu object
	 * @param priceModelList price model list
	 * @param priceListSectionModels price list section backing objects
	 */
	public ProductSkuModel(
			final ProductSku productSku,
			final List<PriceListEditorModel> priceModelList,
			final Map<String, List<PriceListSectionModel>> priceListSectionModels) {
		super(priceModelList, priceListSectionModels);
		this.productSku = productSku;
	}

	/**
	 * Get the catalog set of the product.
	 * @return the set of catalog
	 */
	@Override
	public Set<Catalog> getCatalogs() {
		return getProductSku().getProduct().getCatalogs();
	}

	@Override
	public Catalog getProductMasterCatalog() {
		return getProductSku().getProduct().getMasterCatalog();
	}

	/**
	 * Returns product sku type model name.
	 * 
	 * @return returns product sku type model name.
	 */
	@Override
	public String getModelType() {
		return "SKU";
	}

	@Override
	public ProductSku getProductSku() {
		return productSku;
	}

	@Override
	public String getObjectGuid() {
		return getProductSku().getSkuCode();
	}
}
