/**
 * 
 */
package com.elasticpath.sfweb.ajax.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.exception.InvalidBundleTreeStructureException;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.pricing.PriceProvider;
import com.elasticpath.service.pricing.impl.PricedCalculatedBundle;
import com.elasticpath.sfweb.ajax.bean.JsonBundleItemBean;

/**
 *
 */
public class PricedCalculatedBundleForJson extends PricedCalculatedBundle {
	private final JsonBundleItemBean jsonBundle;
	private final ProductSkuService productSkuService;
	
	/**
	 * 
	 * @param jsonBundle json bundle.
	 * @param bundle product bundle.
	 * @param beanFactory bean factory.
	 * @param priceProvider price provider.
	 * @param productSkuService sku service.
	 */
	public PricedCalculatedBundleForJson(
			final JsonBundleItemBean jsonBundle, 
			final ProductBundle bundle,
			final BeanFactory beanFactory, 
			final PriceProvider priceProvider,
			final ProductSkuService productSkuService) {
		
		super(bundle, beanFactory, priceProvider);
		this.jsonBundle = jsonBundle;
		this.productSkuService = productSkuService;
	}

	@Override
	protected List<Pair<BundleConstituent, Price>> getSelectedConstituentsAndPrices(
			final List<BundleConstituent> constituents, 
			final int itemsToSelect) {
		
		List<Pair<BundleConstituent, Price>> prices = new ArrayList<Pair<BundleConstituent, Price>>(itemsToSelect);
		Collection<BundleConstituent> selectedConstituents = new ArrayList<BundleConstituent>();
		
		if (jsonBundle.getConstituents().size() < constituents.size()) {
			throw new InvalidBundleTreeStructureException("The bundle has more constituents than the JSON bundle");
		}
		
		for (int i = 0; i < constituents.size(); ++i) {
			if (jsonBundle.getConstituents().get(i).isSelected()) {
				selectedConstituents.add(constituents.get(i));
			}
		}
		
		for (BundleConstituent item : selectedConstituents) {
			Price price = getConstituentPrice(item);
			if (price == null) {
				return null;
			}
			prices.add(new Pair<BundleConstituent, Price>(item, price));
		}
		return prices;
	}

	@Override
	protected Price getConstituentPrice(final BundleConstituent constituent) {
		
		for (JsonBundleItemBean item : jsonBundle.getConstituents()) {
			if (constituent.getConstituent().getCode().equals(item.getProductCode())
			|| constituent.getConstituent().getCode().equals(item.getSkuCode())) {
				ProductSku sku = productSkuService.findBySkuCode(item.getSkuCode());
				return getPriceProvider().getProductSkuPrice(sku);
			}
		}
		
		return null;
	}

	
}
