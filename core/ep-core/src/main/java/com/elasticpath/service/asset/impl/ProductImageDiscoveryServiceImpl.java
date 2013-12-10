package com.elasticpath.service.asset.impl;

import java.util.Map;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.impl.ProductLoadTunerImpl;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ProductRelation;

/**
 * Image Discover service for products.
 */
public class ProductImageDiscoveryServiceImpl extends AbstractImageDiscoveryServiceImpl<Product> {

	private QueryService<Product> productQueryService;
	
	@Override
	protected String getDefaultImageKey() {
		return "defaultImage";
	}

	@Override
	protected Map<String, AttributeValue> getAttributeValueMap(final Product product) {
		return product.getAttributeValueMap();
	}

	@Override
	protected String getDefaultImage(final Product product) {
		return product.getImage();
	}

	@Override
	protected Product loadByCode(final String productCode) {
		ProductLoadTuner loadTuner = new ProductLoadTunerImpl();
		loadTuner.setLoadingAttributeValue(true);
		QueryResult<Product> result = getProductQueryService().query(CriteriaBuilder.criteriaFor(Product.class)
				.with(ProductRelation.having().codes(productCode))
				.usingLoadTuner(loadTuner)
				.returning(ResultType.ENTITY));
		return result.getSingleResult();
	}

	protected QueryService<Product> getProductQueryService() {
		return productQueryService;
	}

	public void setProductQueryService(final QueryService<Product> productQueryService) {
		this.productQueryService = productQueryService;
	}

}
