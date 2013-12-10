package com.elasticpath.cmweb.service.catalog;

import java.util.List;

import com.elasticpath.cmweb.dto.catalog.ProductModel;
import com.elasticpath.cmweb.dto.catalog.ProductSkuModel;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.EpService;

/**
 * Provides product-related business services to the CM Client.
 */
public interface ProductModelService extends EpService {
	
	/**
	 * Builds a product model for the product editor.
	 * @param products the products to build the model for.
	 * @return the models for the specified products.
	 */
	ProductModel[] buildLiteProductModels(final List<Product> products);

	/**
	 * Builds an array of lightweight productSkuModels for a list of productSkus.
	 *
	 * @param productSkus the productSkus to build the models for
	 * @return the models for the specified productSkus
	 */
	ProductSkuModel[] buildLiteProductSkuModels(final List<ProductSku> productSkus);
	
	/**
	 * Builds product wizard model.
	 * 
	 * @param product the blank product
	 * @return product wizard model
	 */
	ProductModel buildProductWizardModel(final Product product);

	/**
	 * Builds product editor model.
	 * 
	 * @param productGuid product GUID
	 * @return product editor model
	 */
	ProductModel buildProductEditorModel(final String productGuid);

	/**
	 * Builds product sku editor model.
	 * 
	 * @param productSku product sku
	 * @return product sku editor model
	 */
	ProductSkuModel buildProductSkuEditorModel(final ProductSku productSku);

	/**
	 * Builds product sku editor model.
	 * 
	 * @param productSkuGuid the guid for the sku to build the editor for.
	 * @return product sku editor model
	 */
	ProductSkuModel buildProductSkuEditorModel(final String productSkuGuid);
}
