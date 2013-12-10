/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.domain.builder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A builder that builds {@link ProductSku}s for testing purposes.
 */
public final class ProductSkuBuilder implements DomainObjectBuilder<ProductSku> {
	@Autowired
	private BeanFactory beanFactory;

	private Date startDate;

	private Product product;

	private AttributeValueGroup attributeValueGroup;

	private Map<String, SkuOptionValue> optionValueMap = new HashMap<String, SkuOptionValue>();

	private String image;

	private boolean digitalProduct = false;

	private boolean shippable = true;

	private Map<String, AttributeValue> attributeValueMap;

	private BigDecimal height;

	private BigDecimal width;

	private BigDecimal length;

	private BigDecimal weight;

	private long uidPk;

	private int preOrBackOrderedQuantity;

	private Date endDate;

	private String skuCode;

	public ProductSkuBuilder setStartDate(final Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public ProductSkuBuilder setEndDate(final Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public ProductSkuBuilder setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
		return this;
	}

	public ProductSkuBuilder setProduct(final Product product) {
		this.product = product;
		return this;
	}

	public ProductSkuBuilder setAttributeValueGroup(final AttributeValueGroup attributeValueGroup) {
		this.attributeValueGroup = attributeValueGroup;
		return this;
	}

	public ProductSkuBuilder setImage(final String image) {
		this.image = image;
		return this;
	}

	public ProductSkuBuilder setOptionValueMap(final Map<String, SkuOptionValue> optionValueMap) {
		this.optionValueMap = optionValueMap;
		return this;
	}
	
	public ProductSkuBuilder setDigitalProduct(final boolean digitalProduct) {
		this.digitalProduct = digitalProduct;
		return this;
	}
	
	public ProductSkuBuilder setShippable(final boolean shippable) {
		this.shippable = shippable;
		return this;
	}

	public ProductSkuBuilder setDigitalAsset(final DigitalAsset digitalAsset) {
		return this;
	}

	public ProductSkuBuilder setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap) {
		this.attributeValueMap = attributeValueMap;
		return this;
	}

	public ProductSkuBuilder setHeight(final BigDecimal height) {
		this.height = height;
		return this;
	}

	public ProductSkuBuilder setWidth(final BigDecimal width) {
		this.width = width;
		return this;
	}

	public ProductSkuBuilder setLength(final BigDecimal length) {
		this.length = length;
		return this;
	}

	public ProductSkuBuilder setWeight(final BigDecimal weight) {
		this.weight = weight;
		return this;
	}

	public ProductSkuBuilder setUidPk(final long uidPk) {
		this.uidPk = uidPk;
		return this;
	}

	public ProductSkuBuilder setPreOrBackOrderedQuantity(final int preOrBackOrderedQuantity) {
		this.preOrBackOrderedQuantity = preOrBackOrderedQuantity;
		return this;
	}

	@Override
	public ProductSku build() {
		ProductSku productSku = beanFactory.getBean(ContextIdNames.PRODUCT_SKU);
		
		productSku.setStartDate(startDate);
		productSku.setEndDate(endDate);
		productSku.setProduct(product);
		productSku.setAttributeValueGroup(attributeValueGroup);
		productSku.setOptionValueMap(optionValueMap);
		productSku.setImage(image);
		productSku.setDigital(digitalProduct);
		productSku.setShippable(shippable);
		productSku.setAttributeValueMap(attributeValueMap);
		productSku.setHeight(height);
		productSku.setWidth(width);
		productSku.setLength(length);
		productSku.setWeight(weight);
		productSku.setUidPk(uidPk);
		productSku.setPreOrBackOrderedQuantity(preOrBackOrderedQuantity);
		productSku.setSkuCode(skuCode);
		
		return productSku;
	}
}
