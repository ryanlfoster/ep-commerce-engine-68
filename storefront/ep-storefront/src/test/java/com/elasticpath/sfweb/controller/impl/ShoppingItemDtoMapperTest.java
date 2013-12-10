package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.sfweb.controller.ShoppingItemDtoMapper;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.impl.ShoppingItemFormBeanImpl;

/**
 * Tests the {@link ShoppingItemDtoMapperImpl}.
 */
public class ShoppingItemDtoMapperTest {

	private static final String SKU1 = "SKU1";
	
	private static final String SKU2 = "SKU2";
	
	private static final String SKU3 = "SKU3";
	
	/**
	 * Tests that a single ShoppingItemFormBean, with no children, will return a single ShoppingItemDto.
	 */
	@Test
	public void testSingleItem() {
		ShoppingItemDtoMapper mapper = new ShoppingItemDtoMapperImpl();
		
		ShoppingItemFormBean formBean = new ShoppingItemFormBeanImpl();
		formBean.setSkuCode(SKU1);
		formBean.setQuantity(2);
		
		ShoppingItemDto result = mapper.mapFrom(formBean);
		
		assertEquals("Result sku should be from the formBean.", SKU1, result.getSkuCode());
		assertEquals("Result quantity should be from the formBean.", 2, result.getQuantity());
		
		// Note that there's no way to confirm that no constituents have been added.
	}
	
	/**
	 * Test that a ShoppingItemFormBean, with exactly 1 child, will return a ShoppingItemDto with 1 child.
	 */
	@Test
	public void testSingleChild() {
		ShoppingItemDtoMapper mapper = new ShoppingItemDtoMapperImpl();
		
		ShoppingItemFormBean formBean = new ShoppingItemFormBeanImpl();
		formBean.setSkuCode(SKU1);
		formBean.setQuantity(2);
		
		ShoppingItemFormBean formBean2 = createShoppingItemFormBean(null, SKU2, 0, "", 1, 0);
		formBean.addConstituent(formBean2);
		
		ShoppingItemDto result = mapper.mapFrom(formBean);
		
		assertEquals("Result sku should be from the formBean.", SKU1, result.getSkuCode());
		assertEquals("Result quantity should be from the formBean.", 2, result.getQuantity());
		assertEquals("Should be only 1 child", 1, result.getConstituents().size());
		ShoppingItemDto childDto = result.getConstituents().get(0);
		assertEquals("Should match the first child", SKU2, childDto.getSkuCode());
	}
	
	/**
	 * Test that a ShoppingItemFormBean, with exactly 2 children, will return a ShoppingItemDto with 2 children.
	 */
	@Test
	public void testTwoChildren() {
		ShoppingItemDtoMapper mapper = new ShoppingItemDtoMapperImpl();
		
		ShoppingItemFormBean formBean = new ShoppingItemFormBeanImpl();
		formBean.setSkuCode(SKU1);
		formBean.setQuantity(2);
		
		ShoppingItemFormBean formBean2 = createShoppingItemFormBean(null, SKU2, 0, "", 1, 0);
		formBean.addConstituent(formBean2);
		
		ShoppingItemFormBean formBean3 = createShoppingItemFormBean(null, SKU3, 0, "", 1, 0);
		formBean.addConstituent(formBean3);
		
		ShoppingItemDto result = mapper.mapFrom(formBean);
		
		assertEquals("Result sku should be from the formBean.", SKU1, result.getSkuCode());
		assertEquals("Result quantity should be from the formBean.", 2, result.getQuantity());
		
		
		assertEquals("Should be only 2 children", 2, result.getConstituents().size());
		ShoppingItemDto childDto = result.getConstituents().get(0);
		assertEquals("Should match the first child", SKU2, childDto.getSkuCode());
		
		ShoppingItemDto childDto2 = result.getConstituents().get(1);
		assertEquals("Should match the first child", SKU3, childDto2.getSkuCode());
	}
	
	
	private ShoppingItemFormBean createShoppingItemFormBean(final StoreProduct storeProduct, final String skuCode, final int quantity,
			final String path, final int level, final int shoppingItemUid) {
		ShoppingItemFormBean formBean = new ShoppingItemFormBeanImpl();
		formBean.setProduct(storeProduct);
		formBean.setSkuCode(skuCode);
		formBean.setQuantity(quantity);
		formBean.setPath(path);
		formBean.setLevel(level);
		formBean.setUpdateShoppingItemUid(shoppingItemUid);
		
		return formBean;
	}
	
	/** */
	@Test
	public void testNestedFormBean() {
		ShoppingItemFormBean root = createShoppingItemFormBean(null, SKU1, 0, "", 1, 0);
		
		ShoppingItemFormBean child1 = createShoppingItemFormBean(null, SKU2, 0, "", 1, 0);
		root.addConstituent(child1);
		
		ShoppingItemFormBean child11 = createShoppingItemFormBean(null, SKU3, 0, "", 1, 0);
		child1.addConstituent(child11);
		
		ShoppingItemDtoMapperImpl mapper = new ShoppingItemDtoMapperImpl();
		
		ShoppingItemDto actualRoot = mapper.mapFrom(root);		
		assertEquals(1, actualRoot.getConstituents().size());
		assertEquals(SKU1, actualRoot.getSkuCode());
		
		
		ShoppingItemDto actualChild1 = actualRoot.getConstituents().get(0);
		assertEquals(1, actualChild1.getConstituents().size());
		assertEquals(SKU2, actualChild1.getSkuCode());
		
		ShoppingItemDto actualChild11 = actualChild1.getConstituents().get(0);
		assertEquals(0, actualChild11.getConstituents().size());
		assertEquals(SKU3, actualChild11.getSkuCode());
	}
}
