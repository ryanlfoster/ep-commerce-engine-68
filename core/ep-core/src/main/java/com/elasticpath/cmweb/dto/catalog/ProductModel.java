package com.elasticpath.cmweb.dto.catalog;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SelectionRule;

/**
 * Represents the model of the product being edited with the product price model.
 */
public class ProductModel extends AbstractProductModel {

	private static final long serialVersionUID = 1L;

	private Product product;

	private final Set<ProductSku> removedSkus = new HashSet<ProductSku>();

	/**
	 * Creates the product model based on product object and price list model.
	 * 
	 * @param product the product object
	 * @param priceModelList price model list
	 * @param priceListSectionModels price list section backing objects
	 */
	public ProductModel(final Product product, final List<PriceListEditorModel> priceModelList,
			final Map<String, List<PriceListSectionModel>> priceListSectionModels) {
		super(priceModelList, priceListSectionModels);
		this.product = product;
	}

	/**
	 * Get the catalog set of the product.
	 * @return the set of catalog
	 */
	@Override
	public Set<Catalog> getCatalogs() {
		return getProduct().getCatalogs();
	}

	@Override
	public Catalog getProductMasterCatalog() {
		return getProduct().getMasterCatalog();
	}

	/**
	 * Returns product type model name.
	 * 
	 * @return product type model name.
	 */
	@Override
	public String getModelType() {
		return "PRODUCT";
	}

	/**
	 * @return the product associated with model
	 * */
	public Product getProduct() {
		return product;
	}

	@Override
	public ProductSku getProductSku() {
		return product.getDefaultSku();
	}

	@Override
	public String getObjectGuid() {
		return getProduct().getGuid();
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(final Product product) {
		this.product = product;
	}

	/**
	 * Adds a sku to removed-sku list.
	 * 
	 * @param productSku sku to remove
	 */
	public void removeSku(final ProductSku productSku) {
		removedSkus.add(productSku);
	}
	/**
	 * Returns all skus which were removed from this model. Applicable only for multi-sku product.
	 * 
	 * @return removed skus
	 */
	public Set<ProductSku> getRemovedSkus() {
		return removedSkus;
	}

	/**
	 * @return the selection rule of the product if its a bundle, or null if not a bundle
	 */
	public SelectionRule getSelectionRule() {
		if (product instanceof ProductBundle) {
			return ((ProductBundle) product).getSelectionRule();
		}
		return null;
	}

}
