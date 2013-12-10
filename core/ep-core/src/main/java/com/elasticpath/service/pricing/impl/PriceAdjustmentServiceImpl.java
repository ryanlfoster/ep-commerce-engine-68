package com.elasticpath.service.pricing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.pricing.PriceAdjustmentService;
import com.elasticpath.service.pricing.dao.PriceAdjustmentDao;

/**
 * Implementation for service.
 */
public class PriceAdjustmentServiceImpl extends AbstractEpServiceImpl implements PriceAdjustmentService {
	private PriceAdjustmentDao dao;

	@Override
	public void delete(final PriceAdjustment priceAdjustment) throws EpServiceException {
		dao.delete(priceAdjustment);
	}
	
	/**
	 * @param dao the dao to use
	 */
	public void setPriceAdjustmentDao(final PriceAdjustmentDao dao) {
		this.dao = dao;
	}

	@Override
	@Deprecated
	public Collection<PriceAdjustment> findAllAdjustmentsOnBundle(final String plGuid, final ProductBundle bundle) {
		List <String> constituentGuids = getListOfConstituentGuids(bundle);
		return dao.findByPriceListBundleConstituents(plGuid, constituentGuids);
	}

	@Override
	public Map<String, PriceAdjustment> findByPriceListAndBundleAsMap(final String priceListGuid, final ProductBundle bundle) {
		return dao.findByPriceListAndBundleConstituentsAsMap(priceListGuid, getListOfConstituentGuids(bundle));
	}
	
	private List<String> getListOfConstituentGuids(final ProductBundle bundle) {
		List <String> list = new ArrayList<String>();
		processNode(bundle, list);
		return list;
	}

	private void processNode(final ProductBundle bundle, final List<String> list) {
		for (BundleConstituent constituent : bundle.getConstituents()) {
			list.add(constituent.getGuid());
			if (constituent.getConstituent().isBundle()) {
				processNode((ProductBundle) constituent.getConstituent().getProduct(), list);
			}
		}
	}
	
	@Override
	public List<PriceAdjustment> findByPriceList(final String plGuid) {
		return dao.findByPriceList(plGuid);
	}

}
