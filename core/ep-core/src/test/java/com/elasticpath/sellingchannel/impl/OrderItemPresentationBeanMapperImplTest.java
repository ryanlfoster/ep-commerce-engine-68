package com.elasticpath.sellingchannel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Currency;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.commons.tree.impl.TreeNodeMemento;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.sellingchannel.presentation.OrderItemPresentationBean;
import com.elasticpath.sellingchannel.presentation.impl.OrderItemPresentationBeanMapperImpl;
import com.elasticpath.sellingchannel.presentation.impl.OrderItemPresentationBeanMapperImpl.CopyFunctor;

/**
 * Tests {@link OrderItemPresentationBeanMapperImpl}.
 */
public class OrderItemPresentationBeanMapperImplTest {
	
	/** Required for testing. */
	private class CopyFunctorTestable extends CopyFunctor {
		@Override
		protected boolean isGC(final OrderItemDto sourceNode) {
			return true;
		}
	}
	
	/** Required for testing. */
	private class OrderItemPresentationBeanMapperTestable extends OrderItemPresentationBeanMapperImpl {
		public OrderItemPresentationBeanMapperTestable() {
			super();
			setFunctor(new CopyFunctorTestable());
		}
	}
	
	private static final int THIRTY = 30;

	private static final int TEN = 10;
	// Verifying that the PreOrderTraverser is used is done by inspection - it's too simple to test.
	
	private static final int FIVE = 5;
	private static final int THREE = 3;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Tests that an {@code OrderItemFormBean} can be created from an {@code OrderItemDto}.
	 */
	@Test
	public void testMapToDtoNoChild() {
		CopyFunctorTestable functor = new CopyFunctorTestable();
		
		final OrderItemDto orderItemDto = new OrderItemDto();
		
		TreeNodeMemento<OrderItemPresentationBean> formBean = functor.processNode(orderItemDto, null, null, 0);
		
		assertNotNull("A formBean should be returned", formBean);
	}
	
	/**
	 * Tests that a three level tree (parent->child->grandchildren) is mapped to a flattened structure with level information.
	 */
	@Test
	public void testMapThreeLevelTree() {
		OrderItemPresentationBeanMapperTestable mapper = new OrderItemPresentationBeanMapperTestable();
		
		final OrderItemDto orderItemDto = new OrderItemDto();
		orderItemDto.setQuantity(1);
		final OrderItemDto childDto = new OrderItemDto();
		childDto.setQuantity(1);
		final OrderItemDto grandchildDto = new OrderItemDto();
		grandchildDto.setQuantity(1);
		orderItemDto.addChild(childDto);
		childDto.addChild(grandchildDto);
		
		OrderItemPresentationBean presentationBean = mapper.mapFrom(orderItemDto);
		
		assertNotNull("A formBean should be returned", presentationBean);
		assertEquals("Should have 2 children", 2, presentationBean.getChildren().size());
		assertEquals("Parent is at level 0", 0, presentationBean.getLevel());
		OrderItemPresentationBean childFormBean = presentationBean.getChildren().get(0);
		assertEquals("Child is at level 1", 1, childFormBean.getLevel());
		OrderItemPresentationBean grandchildFormBean = presentationBean.getChildren().get(1);
		assertEquals("Grandchild is at level 2", 2, grandchildFormBean.getLevel());
	}
	
	/**
	 * Tests that the functor copies the information required. 
	 */
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	@Test
	public void testFunctor() {
		CopyFunctorTestable functor = new CopyFunctorTestable();
		
		OrderItemDto dto = new OrderItemDto();
		
		dto.setDisplayName("display");
		dto.setImage("image.jpg");
		dto.setDisplaySkuOptions("skuoptions");
		dto.setSkuCode("sku-A");
		dto.setAllocated(true);
		ProductSku productSku = new ProductSkuImpl();
		dto.setProductSku(productSku);
		DigitalAsset digitalAsset = context.mock(DigitalAsset.class);
		dto.setDigitalAsset(digitalAsset);	
		dto.setEncryptedUidPk("54321");
		dto.setListPrice(MoneyFactory.createMoney(new BigDecimal("12.34"), Currency.getInstance("CAD")));
		dto.setUnitPrice(MoneyFactory.createMoney(new BigDecimal("23.45"), Currency.getInstance("CAD")));
		dto.setUnitLessThanList(false);
		dto.setDollarSavings(MoneyFactory.createMoney(new BigDecimal("11.11"), Currency.getInstance("CAD")));
		dto.setQuantity(THREE);
		dto.setTotal(MoneyFactory.createMoney(new BigDecimal("37.02"), Currency.getInstance("CAD")));
				
		TreeNodeMemento<OrderItemPresentationBean> presentationBeanMemento = functor.processNode(dto, null, null, 0);
		OrderItemPresentationBean presentationBean = presentationBeanMemento.getTreeNode();
		
		assertEquals("Output should match input", digitalAsset, presentationBean.getDigitalAsset());
		assertEquals("Output should match input", "display", presentationBean.getDisplayName());
		assertEquals("Output should match input", "54321", presentationBean.getEncryptedUidPk());
		assertEquals("Output should match input", "image.jpg", presentationBean.getImage());
		assertEquals("Output should match input", "skuoptions", presentationBean.getDisplaySkuOptions());
		assertEquals("Output should match input", true, presentationBean.isAllocated());
		assertEquals("Output should match input", 
				MoneyFactory.createMoney(new BigDecimal("12.34"), Currency.getInstance("CAD")), presentationBean.getListPriceMoney());
		assertEquals("Output should match input", 
				MoneyFactory.createMoney(new BigDecimal("23.45"), Currency.getInstance("CAD")), presentationBean.getUnitPriceMoney());
		assertEquals("Output should match input", false, presentationBean.isUnitLessThanList());
		assertEquals("Output should match input", MoneyFactory.createMoney(new BigDecimal(
				"11.11"), Currency.getInstance("CAD")), presentationBean
				.getDollarSavingsMoney());
		assertEquals("Output should match input", "sku-A", presentationBean.getSkuCode());
		assertEquals("Output should match input", THREE, presentationBean.getQuantity());
		assertEquals("Output should match input", "37.02 CAD", presentationBean.getTotalMoney().toString());
	}
	
	/** Tests that the quantity on the formBeans is the per bundle quantity instead of the shippable quantity. */
	@Test
	public void testMapFromHavingPerBundleQuantity() {		
		OrderItemPresentationBeanMapperTestable mapper = new OrderItemPresentationBeanMapperTestable();
		OrderItemDto root = createFakeOrderItemDto();

		OrderItemPresentationBean actualPresentationBeanRoot = mapper.mapFrom(root);
		assertEquals(FIVE, actualPresentationBeanRoot.getQuantity());
		assertEquals("Both descendants should be direct children of the root", 2, actualPresentationBeanRoot.getChildren().size());

		OrderItemPresentationBean actualFormBeanChild1 = actualPresentationBeanRoot.getChildren().get(0);
		assertEquals(2, actualFormBeanChild1.getQuantity());
		assertEquals(0, actualFormBeanChild1.getChildren().size());

		OrderItemPresentationBean actualFormBeanChild2 = actualPresentationBeanRoot.getChildren().get(1);
		assertEquals(THREE, actualFormBeanChild2.getQuantity());
		assertEquals(0, actualFormBeanChild2.getChildren().size());
	}

	/**
	 * The definition for the product bundle is:
	 * 
	 *  -root
	 *  	-child1 X 2
	 *  		-child11 X 3
	 *  
	 *  If the quantity of root is 5 then the order item has the following structure:
	 *  
	 *  -root X 5
	 *  	-child1 X 10
	 *  		-child11 X 30
	 *  .
	 */
	private OrderItemDto createFakeOrderItemDto() {
		OrderItemDto root = new OrderItemDto();
		root.setQuantity(FIVE);

		OrderItemDto child1 = new OrderItemDto();
		child1.setQuantity(TEN);
		root.addChild(child1);

		OrderItemDto child11 = new OrderItemDto();
		child11.setQuantity(THIRTY);
		child1.addChild(child11);
		
		return root;
	}
}
