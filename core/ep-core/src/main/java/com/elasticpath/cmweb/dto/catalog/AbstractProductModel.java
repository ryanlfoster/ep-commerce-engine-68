package com.elasticpath.cmweb.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * The <code>AbstractProductModel</code> contains common functionality for ProductModel and ProductSkuModel.
 */
public abstract class AbstractProductModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Map<String, PriceListEditorModel> priceEditorModels;

	private final Map<String, List<PriceListSectionModel>> priceListSectionModels;

	/**
	 * Creates the product/productSku editor model based on price list model.
	 * 
	 * @param priceModelList price model list
	 * @param priceListSectionModels pricelist section models
	 */
	public AbstractProductModel(
			final List<PriceListEditorModel> priceModelList, 
			final Map<String, List<PriceListSectionModel>> priceListSectionModels) {
		this.priceEditorModels = buildPriceModelMap(priceModelList);
		this.priceListSectionModels = priceListSectionModels;
	}

	/**
	 * @return a Map with {@link PriceListSectionModel} objects that are grouped by their currency codes. 
	 * The currency codes serve as the Map keys.
	 */
	public Map<String, List<PriceListSectionModel>> getPriceListSectionModels() {
		return priceListSectionModels;
	}

	private Map<String, PriceListEditorModel> buildPriceModelMap(final List<PriceListEditorModel> priceModelList) {
		final Map<String, PriceListEditorModel> priceEditorModels = new HashMap<String, PriceListEditorModel>();
		for (PriceListEditorModel priceModel : priceModelList) {
			priceEditorModels.put(priceModel.getPriceListDescriptor().getGuid(), priceModel);
		}
		return priceEditorModels;
	}

	/**
	 * Convert price list editor models list from productModel to list of {@link BaseAmountChangeSet}s.
	 *  
	 * @return List of <code>BaseAmountChangeSet</code>.
	 */
	public List<ChangeSetObjects<BaseAmountDTO>> getBaseAmountChangeSets() {
		final List<ChangeSetObjects<BaseAmountDTO>> baseAmountChangeSets = new ArrayList<ChangeSetObjects<BaseAmountDTO>>();
		for (PriceListEditorModel priceListEditorModel : getPriceListEditorModels()) {
			baseAmountChangeSets.add(priceListEditorModel.getChangeSet());
		}		
		return baseAmountChangeSets;
	}

	/**
	 * Gets the price editor model for given price list guid.
	 * 
	 * @param priceListDescriptorGuid the guid
	 * @return price editor model or null if it does not exist
	 */
	public PriceListEditorModel getPriceEditorModel(final String priceListDescriptorGuid) {
		return priceEditorModels.get(priceListDescriptorGuid);
	}
	
	/**
	 * Gets the price list editor models.
	 * 
	 * @return collection of price list editor models
	 */
	Collection<PriceListEditorModel> getPriceListEditorModels() {
		return priceEditorModels.values();
	}

	/**
	 * @return master catalog of concrete implementation of product editor
	 */
	public abstract Catalog getProductMasterCatalog();

	/**
	 * Concrete implementation may return either PRODUCT or SKU.
	 * 
	 * @return object type of this editor
	 */
	public abstract String getModelType();

	/**
	 * @return object guid of this model
	 */
	public abstract String getObjectGuid();

	/**
	 * @return Product Sku associated with model.
	 */
	public abstract ProductSku getProductSku();
	
	/**
	 * Get the catalog set of the product.
	 * @return the set of catalog
	 */
	public abstract Set<Catalog> getCatalogs();

}
