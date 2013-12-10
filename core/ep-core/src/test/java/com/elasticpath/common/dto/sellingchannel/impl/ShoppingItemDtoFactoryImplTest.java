package com.elasticpath.common.dto.sellingchannel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.ItemConfigurationImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests for ShoppingItemDtoFactoryImpl.
 */
public class ShoppingItemDtoFactoryImplTest {
	private static final String PROD_A = "ProdA";
	private static final String AAA = "AAA";
	private static final String BBB = "BBB";
	private static final String SKU_A = "SkuA";
	private static final String SKU_B = "SkuB";
	private static final String SKU_C = "SkuC";
	private static final String SKU_D = "SkuD";
	private static final String CURRENCY_CAD = "CAD";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that when a ShoppingItemDto is created from a ProductBundle, the
	 * DTO has the same number of constituents at all levels and they are in the same order.
	 * All skus must be marked as not selected.
	 */
	@Test
	public void testCreateShoppingItemDtoAddsMissingConstituents() {
		//Create a bundle with two items and a select 1 rule
		final String selectedSkuGuid = "selectedSku";
		final String nonSelectedSkuGuid = "nonSelectedSku";
		final String bundleSkuGuid = "bundleSku";
		final Product selectedProduct = new ProductImpl();
		final ProductSku selectedSku = new ProductSkuImpl();
		selectedSku.setSkuCode(selectedSkuGuid);
		selectedProduct.addOrUpdateSku(selectedSku);

		final Product nonSelectedProduct = new ProductImpl();
		final ProductSku nonSelectedSku = new ProductSkuImpl();
		nonSelectedSku.setSkuCode(nonSelectedSkuGuid);
		nonSelectedProduct.addOrUpdateSku(nonSelectedSku);
		nonSelectedSku.setProduct(nonSelectedProduct);

		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("productSkuConstituent", ProductSkuConstituentImpl.class);

		final BundleConstituent selectedBundleItem = new BundleConstituentImpl();
		selectedBundleItem.setQuantity(1);
		selectedBundleItem.setConstituent(selectedProduct);
		final BundleConstituent nonSelectedBundleItem = new BundleConstituentImpl();
		nonSelectedBundleItem.setQuantity(1);
		nonSelectedBundleItem.setConstituent(nonSelectedSku);

		final ProductBundle bundle = new ProductBundleImpl();
		bundle.setSelectionRule(new SelectionRuleImpl(1));
		final ProductSku bundleSku = new ProductSkuImpl();
		bundleSku.setSkuCode(bundleSkuGuid);
		bundle.addOrUpdateSku(bundleSku);
		bundle.addConstituent(selectedBundleItem);
		bundle.addConstituent(nonSelectedBundleItem);

		//Assemble the ShoppingItemDto and check it.
		ShoppingItemDtoFactoryImpl factory = new ShoppingItemDtoFactoryImpl();
		ShoppingItemDto dto = factory.createDto(bundle, 1);
		assertEquals("DTO should have the same number of constituents as the Bundle",
				dto.getConstituents().size(), bundle.getConstituents().size());
		assertEquals("DTO's first sku should be the same as the bundle's first sku.",
				selectedSkuGuid, dto.getConstituents().get(0).getSkuCode());
		assertEquals("DTO's second sku should be the same as the bundle's second sku.",
				nonSelectedSkuGuid, dto.getConstituents().get(1).getSkuCode());
		assertFalse("DTO's first sku should not be selected",
				dto.getConstituents().get(0).isSelected());
		assertFalse("DTO's second sku should not be selected",
				dto.getConstituents().get(1).isSelected());
		assertFalse(dto.getConstituents().get(0).isProductSkuConstituent());
		assertTrue(dto.getConstituents().get(1).isProductSkuConstituent());

		// Assert that a second dto created from the same bundle is equal to the first.
		ShoppingItemDto dto2 = factory.createDto(bundle, 1);
		assertEquals(dto, dto2);
		assertEquals(dto.hashCode(), dto2.hashCode());
	}

	/** Test shopping item object overrides getPrice and makeMoney. */
	class TestShoppingItemImpl extends ShoppingItemImpl {
		private static final long serialVersionUID = -7939481597705476320L;

		@Override
		protected Money makeMoney(final BigDecimal amount) {
			if (amount == null) {
				return null;
			}
			return MoneyFactory.createMoney(amount, getCurrency());
		}

		@Override
		public Price getPrice() {
			Price price = new PriceImpl();
			price.setCurrency(getCurrency());
			return price;
		}
	}

	/**
	 * Tests that a {@code ShoppingItemDto} can be created from a {@code ProductBundle}.
	 * A simple parent->child->grandchild is tested to check recursion.
	 */
	@Test
	public void testCreateShoppingItemDto() {
		//Create grandChild product/sku
		ProductSku grandChildProductSku = new ProductSkuImpl();
		Product grandChildProduct = new ProductImpl();
		grandChildProduct.setCode("ProdC");
		grandChildProduct.addOrUpdateSku(grandChildProductSku);
		grandChildProductSku.setSkuCode(BBB);
		//Create child product(Bundle)/sku
		ProductSku childProductSku = new ProductSkuImpl();
		ProductBundle childProduct = new ProductBundleImpl();
		childProduct.setCode("ProdB");
		childProduct.addOrUpdateSku(childProductSku);
		childProductSku.setSkuCode(AAA);

		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);

		BundleConstituent childConstituent = createBundleConstituentFrom(grandChildProduct);
		childProduct.addConstituent(childConstituent);
		//Create root bundle/sku
		ProductSku rootSku = new ProductSkuImpl();
		rootSku.setSkuCode(SKU_B);
		ProductBundle productBundle = new ProductBundleImpl();
		productBundle.setCode(PROD_A);
		productBundle.addOrUpdateSku(rootSku);
		productBundle.addConstituent(createBundleConstituentFrom(childProduct));

		final Price price = context.mock(Price.class);
		final Money money = MoneyFactory.createMoney(BigDecimal.ONE, Currency.getInstance(CURRENCY_CAD));

		context.checking(new Expectations() { {
			allowing(price).getCurrency(); will(returnValue(Currency.getInstance(CURRENCY_CAD)));
			allowing(price).getListPrice(1); will(returnValue(money));
			allowing(price).getSalePrice(1); will(returnValue(money));
			allowing(price).getComputedPrice(1); will(returnValue(money));
		} });

		ShoppingItemDtoFactoryImpl factory = new ShoppingItemDtoFactoryImpl();
		ShoppingItemDto shoppingItemDto = factory.createDto(productBundle, 1);
		assertEquals(SKU_B, shoppingItemDto.getSkuCode());
		assertEquals(PROD_A, shoppingItemDto.getProductCode());
		assertEquals(1, shoppingItemDto.getQuantity());
		assertEquals(0, shoppingItemDto.getShoppingItemUidPk());

		assertEquals(1, shoppingItemDto.getConstituents().size());
		ShoppingItemDto actualChild = shoppingItemDto.getConstituents().get(0);
		assertEquals(AAA, actualChild.getSkuCode());
		assertEquals(1, actualChild.getConstituents().size());
		assertEquals(BBB, actualChild.getConstituents().get(0).getSkuCode());

		// Assert that a second shoppingItemDto created from the same bundle is equal to the first.
		ShoppingItemDto shoppingItemDto2 = factory.createDto(productBundle, 1);
		assertEquals(shoppingItemDto, shoppingItemDto2);
		assertEquals(shoppingItemDto.hashCode(), shoppingItemDto2.hashCode());
	}

	private String createBundleConstituentGuid(final Product product) {
		return product.getCode() + "_CG";
	}

	private BundleConstituent createBundleConstituentFrom(final Product product) {
		final BundleConstituentImpl constituentBundle = new BundleConstituentImpl();
		constituentBundle.setConstituent(product);
		constituentBundle.setQuantity(1);
		constituentBundle.setGuid(createBundleConstituentGuid(product));
		return constituentBundle;
	}

	/**
	 * Tests that an ItemConfiguration for a simple Product creates a ShoppingItemDto that is equal to the existing method.
	 */
	@Test
	public void testItemConfigurationForProduct() {
		final String selectedSkuGuid = "selectedSku";
		final Product selectedProduct = new ProductImpl();
		final ProductSku selectedSku = new ProductSkuImpl();
		selectedSku.setSkuCode(selectedSkuGuid);
		selectedProduct.addOrUpdateSku(selectedSku);

		ItemConfigurationImpl itemConfiguration =
			new ItemConfigurationImpl(selectedSkuGuid, new HashMap<String, ItemConfiguration>(), true, null);

		ShoppingItemDtoFactoryImpl factory = new ShoppingItemDtoFactoryImpl();
		ShoppingItemDto dtoFromProduct = factory.createDto(selectedProduct, 1);
		ShoppingItemDto dtoFromItemConfiguration = factory.createDto(selectedProduct, 1, itemConfiguration);

		assertTrue(dtoFromItemConfiguration.isSelected());
		assertEquals(dtoFromProduct, dtoFromItemConfiguration);
		assertEquals(dtoFromProduct.hashCode(), dtoFromItemConfiguration.hashCode());
	}

	/**
	 * Tests that an ItemConfiguration for a simple ProductBundle creates a ShoppingItemDto.
	 */
	@Test
	public void testItemConfigurationForProductBundle() {
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);

		// childProductSkuC and childProductSkuD assembly.
		ProductSku childProductSkuC = new ProductSkuImpl();
		childProductSkuC.setSkuCode(SKU_C);
		ProductSku childProductSkuD = new ProductSkuImpl();
		childProductSkuD.setSkuCode(SKU_D);
		Product childProductCD = new ProductImpl();
		childProductCD.setCode("ProdCD");
		childProductCD.addOrUpdateSku(childProductSkuC);
		childProductCD.addOrUpdateSku(childProductSkuD);

		// childProductSkuB assembly.
		ProductSku childProductSkuB = new ProductSkuImpl();
		childProductSkuB.setSkuCode(SKU_B);
		ProductBundle childProductB = new ProductBundleImpl();
		childProductB.setCode("ProdB");
		childProductB.addOrUpdateSku(childProductSkuB);

		// rootSku assembly.
		ProductSku rootSku = new ProductSkuImpl();
		rootSku.setSkuCode(SKU_A);
		ProductBundle rootBundle = new ProductBundleImpl();
		rootBundle.setCode(PROD_A);
		rootBundle.addOrUpdateSku(rootSku);
		rootBundle.addConstituent(createBundleConstituentFrom(childProductB));
		rootBundle.addConstituent(createBundleConstituentFrom(childProductCD));

		// expected ShoppingItemDto assembly.
		ShoppingItemDto expectedDtoC = new ShoppingItemDto(SKU_D, 1);
		expectedDtoC.setProductCode("ProdCD");
		expectedDtoC.setSelected(false);

		ShoppingItemDto expectedDtoB = new ShoppingItemDto(SKU_B, 1);
		expectedDtoB.setProductCode("ProdB");
		expectedDtoB.setSelected(true);

		ShoppingItemDto expectedDtoA = new ShoppingItemDto(SKU_A, 1);
		expectedDtoA.setProductCode(PROD_A);
		expectedDtoA.setSelected(true);
		expectedDtoA.addConstituent(expectedDtoB);
		expectedDtoA.addConstituent(expectedDtoC);

		// ItemConfiguration assembly.
		ItemConfiguration itemConfigurationCD =
			new ItemConfigurationImpl(SKU_D, new HashMap<String, ItemConfiguration>(), false, createBundleConstituentGuid(childProductCD));
		ItemConfiguration itemConfigurationB =
			new ItemConfigurationImpl(SKU_B, new HashMap<String, ItemConfiguration>(), true, createBundleConstituentGuid(childProductB));

		Map<String, ItemConfiguration> childrenMap = new HashMap<String, ItemConfiguration>();
		childrenMap.put(createBundleConstituentGuid(childProductCD), itemConfigurationCD);
		childrenMap.put(createBundleConstituentGuid(childProductB), itemConfigurationB);
		ItemConfigurationImpl itemConfigurationA = new ItemConfigurationImpl(SKU_A, childrenMap, true, createBundleConstituentGuid(rootBundle));

		// Give the ItemConfiguration to the factory and get back the generated ShoppingItemDto.
		ShoppingItemDtoFactoryImpl factory = new ShoppingItemDtoFactoryImpl();
		ShoppingItemDto dtoFromItemConfiguration = factory.createDto(rootBundle, 1, itemConfigurationA);
		assertEquals(expectedDtoA, dtoFromItemConfiguration);
		assertEquals(expectedDtoA.hashCode(), dtoFromItemConfiguration.hashCode());
	}
}
