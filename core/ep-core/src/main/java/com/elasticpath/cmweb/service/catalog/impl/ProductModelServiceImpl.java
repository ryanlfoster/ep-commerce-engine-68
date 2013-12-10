package com.elasticpath.cmweb.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.elasticpath.cmweb.dto.catalog.PriceListEditorModel;
import com.elasticpath.cmweb.dto.catalog.PriceListSectionModel;
import com.elasticpath.cmweb.dto.catalog.ProductModel;
import com.elasticpath.cmweb.dto.catalog.ProductSkuModel;
import com.elasticpath.cmweb.dto.catalog.impl.PriceListEditorModelImpl;
import com.elasticpath.cmweb.dto.catalog.impl.ProductSkuPriceListModelImpl;
import com.elasticpath.cmweb.service.catalog.ProductModelService;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Server-side facade for the product editor in the CM Client.
 */
public class ProductModelServiceImpl extends AbstractEpServiceImpl implements ProductModelService {

	private PriceListService priceListService;

	private PriceListHelperService priceListHelperService;
	
	private ProductService productService;

	private ProductSkuService productSkuService;
	
	/**
	 * Builds a product model for the product editor.
	 * @param products the products to build the model for.
	 * @return the models for the specified products.
	 */
	public ProductModel[] buildLiteProductModels(final List<Product> products) {
		final ProductModel[] productModels = new ProductModel[products.size()];

		int index = 0;
		for (final Product currentProduct : products) {
			productModels[index] = new ProductModel(currentProduct, buildProductPriceModelList(currentProduct, true),
					getPriceListSectionModels(currentProduct.getCatalogs()));
			index++;
		}

		return productModels;
	}

	/**
	 * Builds an array of lightweight productSkuModels for a list of productSkus.
	 *
	 * @param productSkus the productSkus to build the models for
	 * @return the models for the specified productSkus
	 */
	public ProductSkuModel[] buildLiteProductSkuModels(final List<ProductSku> productSkus) {
		final ProductSkuModel[] productSkuModels = new ProductSkuModel[productSkus.size()];

		int index = 0;
		for (final ProductSku currentProductSku : productSkus) {
			productSkuModels[index] = new ProductSkuModel(currentProductSku, 
					buildProductPriceModelList(currentProductSku.getProduct(), true),
					getPriceListSectionModels(currentProductSku.getProduct().getCatalogs()));
			index++;
		}

		return productSkuModels;
	}
	
	/**
	 * @return <code>ProductLoadTuner</code> customized to load a
	 *         <code>Product</code> displayable in product editor
	 */
	ProductLoadTuner createProductLoadTuner() {
		final ProductLoadTuner productLoadTuner = getBean(ContextIdNames.PRODUCT_LOAD_TUNER);

		productLoadTuner.setLoadingProductType(true);
		productLoadTuner.setLoadingAttributeValue(true);
		productLoadTuner.setLoadingSkus(true);
		productLoadTuner.setLoadingCategories(true);

		return productLoadTuner;
	}
	
	@Override
	public ProductModel buildProductEditorModel(final String productGuid) {
		final Product product = productService.findByGuid(productGuid, createProductLoadTuner());

		if (product == null) {
			return null;
		}
		
		Map<String, List<PriceListSectionModel>> priceListSectionModels = getPriceListSectionModels(product.getCatalogs());
		List<PriceListDescriptorDTO> priceLists = new ArrayList<PriceListDescriptorDTO>();

		for (List<PriceListSectionModel> sectionModels : priceListSectionModels.values()) {
			for (PriceListSectionModel sectionModel : sectionModels) {
				priceLists.add(sectionModel.getPriceListDescriptorDTO());
			}
		}

		return new ProductModel(product, buildProductPriceModelList(product,
				priceLists), priceListSectionModels);
	}

	/**
	 * Build the price editor model list for product with given guid and
	 * existent price list descriptors.
	 * 
	 * @param product
	 *            the {@link Product} instance
	 * @param price
	 *            lists descriptors that are associated with the given product
	 * @return list of price editor models
	 */
	private List<PriceListEditorModel> buildProductPriceModelList(
			final Product product, final List<PriceListDescriptorDTO> priceLists) {
		
		final List<PriceListEditorModel> priceModelList = new ArrayList<PriceListEditorModel>();
		for (Entry<PriceListDescriptorDTO, List<BaseAmountDTO>> entry : priceListHelperService.getPriceListMap(product, priceLists).entrySet()) {
			priceModelList.add(new PriceListEditorModelImpl(entry.getKey(), entry.getValue()));
		}
		return priceModelList;
	}	
	
	@Override
	public ProductSkuModel buildProductSkuEditorModel(final ProductSku productSku) {
		Map<String, List<PriceListSectionModel>> priceListModelSections = getPriceListSectionModels(productSku.getProduct().getCatalogs());
		List<PriceListDescriptorDTO> priceLists = new ArrayList<PriceListDescriptorDTO>();

		for (List<PriceListSectionModel> sectionModels : priceListModelSections.values()) {
			for (PriceListSectionModel sectionModel : sectionModels) {
				priceLists.add(sectionModel.getPriceListDescriptorDTO());
			}
		}
		return new ProductSkuModel(
				productSku,
				buildProductSkuPriceModelList(productSku, priceLists),
				priceListModelSections);

	}

	
	/**
	 * Build the price editor model list for product and productSku.
	 * 
	 * @param productSku
	 *            the {@link ProductSku} instance
	 * @return list of price editor models
	 */
	private List<PriceListEditorModel> buildProductSkuPriceModelList(
			final ProductSku productSku,
			final List<PriceListDescriptorDTO> priceLists) {
		final List<PriceListEditorModel> priceModelList = new ArrayList<PriceListEditorModel>();
		for (Entry<PriceListDescriptorDTO, List<BaseAmountDTO>> entry : priceListHelperService.getPriceListMap(productSku, priceLists).entrySet()) {
			priceModelList.add(new ProductSkuPriceListModelImpl(entry.getKey(), entry.getValue()));
		}
		return priceModelList;
	}	
	
	@Override
	public ProductSkuModel buildProductSkuEditorModel(final String productSkuGuid) {
		ProductSku sku = productSkuService.findByGuid(productSkuGuid);
		if (sku == null) {
			return null;
		}
		return buildProductSkuEditorModel(sku);
	}
	

	/**
	 * Builds product wizard model.
	 * 
	 * @param product
	 *            the blank product
	 * @return product wizard model
	 */
	public ProductModel buildProductWizardModel(final Product product) {
		return new ProductModel(product,
				buildProductWizardPriceModelList(product),
				getPriceListSectionModels(product.getCatalogs()));
	}
	
	/**
	 * Build the price editor model list for product wizard. Retrieves price
	 * list descriptors, while keeping base amount lists empty.
	 * 
	 * @param product
	 *            the {@link Product} instance
	 * @return list of price editor models
	 */
	protected List<PriceListEditorModel> buildProductWizardPriceModelList(final Product product) {
		final List<PriceListEditorModel> priceModelList = new ArrayList<PriceListEditorModel>();
		for (PriceListDescriptorDTO priceListDescriptorDTO : priceListHelperService.findAllDescriptors(product)) {
			priceModelList.add(new PriceListEditorModelImpl(priceListDescriptorDTO, Collections.<BaseAmountDTO>emptyList()));
		}
		return priceModelList;
	}

	/**
	 * Build the price editor model list for product with given guid and existent price list descriptors.
	 * 
	 * @param product the {@link Product} instance
	 * @return list of price editor models
	 */
	private List<PriceListEditorModel> buildProductPriceModelList(final Product product, final boolean masterOnly) {
		final List<PriceListEditorModel> priceModelList = new ArrayList<PriceListEditorModel>();
		for (Entry<PriceListDescriptorDTO, List<BaseAmountDTO>> entry : priceListHelperService.getPriceListMap(product, masterOnly).entrySet()) {
			priceModelList.add(new PriceListEditorModelImpl(entry.getKey(), entry.getValue()));
		}
		return priceModelList;
	}

	/**
	 * Constructs a a Map with {@link PriceListSectionModel} objects that are grouped by their currency codes. The currency codes
	 * serve as the Map keys. The price list objects that are contained in this Map are assigned to the catalogs that are passed
	 * as a parameter.
	 * 
	 * @param catalogs the catalogs that should be looked through for assigned price lists.
	 * @return a Map of {@link PriceListSectionModel} objects
	 */
	private Map<String, List<PriceListSectionModel>> getPriceListSectionModels(final Set<Catalog> catalogs) {
		Map<String, List<PriceListSectionModel>> sections = new TreeMap<String, List<PriceListSectionModel>>(String.CASE_INSENSITIVE_ORDER);
		for (Catalog catalog : catalogs) {
			List<PriceListDescriptorDTO> descriptors = priceListService.listByCatalog(catalog);
			for (PriceListDescriptorDTO descriptor : descriptors) {
				String currencyCode = descriptor.getCurrencyCode();
				if (sections.containsKey(currencyCode)) {
					PriceListSectionModel model = lookupByDescriptor(sections.get(currencyCode), descriptor);
					if (model != null) {
						model.getCatalogs().add(catalog);
						continue;
					}
					sections.get(currencyCode).add(new PriceListSectionModel(descriptor, catalog));
				} else {
					List<PriceListSectionModel> sectionModels = new ArrayList<PriceListSectionModel>();
					sectionModels.add(new PriceListSectionModel(descriptor, catalog));
					sections.put(currencyCode, sectionModels);
				}
			}
		}
		for (List<PriceListSectionModel> sectionModel : sections.values()) {
			Collections.sort(sectionModel, PriceListSectionModel.getDescriptorNameComparator());
		}
		return sections;
	}

	private PriceListSectionModel lookupByDescriptor(final List<PriceListSectionModel> sectionModels, final PriceListDescriptorDTO descriptor) {
		for (PriceListSectionModel model : sectionModels) {
			if (model.getPriceListDescriptorDTO().getGuid().equalsIgnoreCase(descriptor.getGuid())) {
				return model;
			}
		}
		return null;
	}

	/**
	 * @param priceListService the price list service to use.
	 */
	public void setPriceListService(final PriceListService priceListService) {
		this.priceListService = priceListService;
	}

	/**
	 * @param priceListHelperService the price list helper service to use.
	 */
	public void setPriceListHelperService(final PriceListHelperService priceListHelperService) {
		this.priceListHelperService = priceListHelperService;
	}
	
	/**
	 * @param productService the product service to use.
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}
	
	/**
	 * @param productSkuService the product sku service to use.
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}
}
