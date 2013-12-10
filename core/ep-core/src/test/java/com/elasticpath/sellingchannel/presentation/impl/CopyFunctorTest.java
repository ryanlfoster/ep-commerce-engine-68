package com.elasticpath.sellingchannel.presentation.impl;

import org.junit.Assert;

import org.junit.Test;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.sellingchannel.presentation.OrderItemPresentationBean;
import com.elasticpath.sellingchannel.presentation.impl.OrderItemPresentationBeanMapperImpl.CopyFunctor;

/**
 * Tests that the 'emailable' flag is set on the order item presentation form bean by copy functor when necessary: 
 * only if the product type of the item is GC. 
 */
public class CopyFunctorTest {

	private static final String GIFT_CERTIFICATES = "Gift Certificates";

	/**
	 * Tests if the flag is set.
	 */
	@Test
	public void testEmailableFlagSetWhenGiftCertificate() {
		CopyFunctor functor = new CopyFunctor();
		OrderItemPresentationBean bean = new OrderItemPresentationBeanImpl();
		functor.setViewFlags(createDtoWithProductTypeName(GIFT_CERTIFICATES), bean);
		Assert.assertTrue("Emailable flag wasn't set", bean.isViewFlagOn("gc"));
	}

	/**
	 * Tests if the flag is set.
	 */
	@Test
	public void testEmailableFlagNotSetWhenNotGiftCertificate() {
		CopyFunctor functor = new CopyFunctor();
		OrderItemPresentationBean bean = new OrderItemPresentationBeanImpl();
		functor.setViewFlags(createDtoWithProductTypeName("Random Product Type"), bean);
		Assert.assertFalse("Emailable flag shouldn't be set", bean.isViewFlagOn("gc"));
	}
	
	
	/**
	 * @return OrderItemDto instance that has GC product type
	 * 
	 * @param productTypeName the product type name
	 */
	public OrderItemDto createDtoWithProductTypeName(final String productTypeName) {
		OrderItemDto dto = new OrderItemDto();
		Product prod = new ProductImpl();
		ProductType pType = new ProductTypeImpl();
		ProductSku pSku = new ProductSkuImpl();
		pType.setName(productTypeName);
		prod.setProductType(pType);
		pSku.setProduct(prod);
		dto.setProductSku(pSku);
		return dto;
	}
	
}
