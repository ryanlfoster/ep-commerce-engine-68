package com.elasticpath.sellingchannel.director.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.common.dto.sellingchannel.impl.ShoppingItemDtoFactoryImpl;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PricingSchemeImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.sellingchannel.impl.ShoppingItemFactoryImpl;
import com.elasticpath.sellingchannel.impl.ShoppingItemRecurringPriceAssemblerImpl;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Verifies the behaviour of the ShoppingItemAssembler.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveClassLength", "PMD.ExcessiveImports" })
public class ShoppingItemAssemblerImplTest {

	private static final String SELECTED_SKU = "selectedSku";

	private static final int SHOPPING_ITEM_UIDPK = 12334;

	private static final String BBB = "BBB";

	private static final String AAA = "AAA";

	private static final String CCC = "CCC";

	private static final String A_BUNDLE_CODE = "A_BUNDLE_CODE";

	private static final String A_BUNDLE_SKU_CODE = "A_BUNDLE_SKU";

	private static final String A_PRODUCT_CODE = "A_PRODUCT_CODE";

	private static final String A_PRODUCT_SKU_CODE = "A_PRODUCT_SKU_CODE";

	private static final String ANOTHER_PRODUCT_SKU_CODE = "ANOTHER_PRODUCT_SKU_CODE";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String SKU_B = "SkuB";

	private static final String CURRENCY_CAD = "CAD";

	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("productSkuConstituent", ProductSkuConstituentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("shoppingItem", ShoppingItemImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("Price", PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		final ShoppingItemRecurringPriceAssemblerImpl recurringPriceAssembler = new ShoppingItemRecurringPriceAssemblerImpl();
		recurringPriceAssembler.setBeanFactory(beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE_ASSEMBLER, recurringPriceAssembler);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that called createShoppingItem will find the sku, find the price and create the shopping item.
	 */
	@Test
	public void testCreateShoppingItem() {
		final ProductSku sku = new ProductSkuImpl();
		final Product product = new ProductImpl();
		sku.setProduct(product);

		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {

			@Override
			protected boolean verifySelectionRulesFollowed(final Product product, final ShoppingItemDto shoppingItemDto) {
				return true;
			}

			@Override
			protected boolean verifyDtoStructureEqualsBundleStructure(final Product product, final ShoppingItemDto dtoNode) {
				return true;
			}

			@Override
			ProductSku getProductSku(final String currentSkuGuid) {
				// not testing this part
				return sku;
			}
		};

		final ShoppingItemFactory cartItemFactory = context.mock(ShoppingItemFactory.class);
		assembler.setShoppingItemFactory(cartItemFactory);

		final Catalog catalog = new CatalogImpl();
		final Store store = new StoreImpl();
		store.setCatalog(catalog);

		final ShoppingItem shoppingItem = new ShoppingItemImpl();
		context.checking(new Expectations() {
			{
				oneOf(cartItemFactory).createShoppingItem(sku, null, 2, 0, Collections.<String, String>emptyMap());
				will(returnValue(shoppingItem));
			}
		});

		final ShoppingItemDto shoppingItemDto = new ShoppingItemDto(SKU_B, 2);
		shoppingItemDto.setSelected(true);

		final ShoppingItem actualShoppingItem = assembler.createShoppingItem(shoppingItemDto);

		assertEquals("The cartItem from the delegate should equal the cart item from the factory", shoppingItem, actualShoppingItem);

	}

	/**
	 * Test verification of bundle selection rules succeeds with a nested bundle. Root bundle is select 1, and nested bundle is select 1, and nested
	 * constituent (non bundle) is selected.
	 */
	@Test
	public void testValidationOfNestedBundleSelectOnlyConstituentSucceed() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(1));

		final ProductBundle nestedProductBundle = createBundleWithThreeConstituents();
		nestedProductBundle.setSelectionRule(new SelectionRuleImpl(1));

		productBundle.addConstituent(createBundleConstituentFrom(nestedProductBundle));

		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.getConstituents().get(0).setSelected(false);
		shoppingItemDto.getConstituents().get(1).setSelected(false);
		shoppingItemDto.getConstituents().get(2).setSelected(false);

		final ShoppingItemDto nestedShoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		nestedShoppingItemDto.getConstituents().get(0).setSelected(false);
		nestedShoppingItemDto.getConstituents().get(1).setSelected(false);
		shoppingItemDto.addConstituent(nestedShoppingItemDto);

		final ShoppingItemAssemblerImpl assembler = getAssemblerForSelectionRuleTesting();
		assertTrue(assembler.verifySelectionRulesFollowed(productBundle, shoppingItemDto));
	}

	/**
	 * Test verification of bundle selection rules succeeds with a nested bundle. Root bundle is select 1, root-level constituent is selected and
	 * nested bundle constituents are not selected.
	 */
	@Test
	public void testValidationOfNestedBundleSelectOne() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(1));

		final ProductBundle nestedProductBundle = createBundleWithThreeConstituents();
		nestedProductBundle.setSelectionRule(new SelectionRuleImpl(1));

		productBundle.addConstituent(createBundleConstituentFrom(nestedProductBundle));

		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.getConstituents().get(0).setSelected(false);
		shoppingItemDto.getConstituents().get(1).setSelected(false);
		shoppingItemDto.getConstituents().get(2).setSelected(true);

		final ShoppingItemDto nestedShoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		nestedShoppingItemDto.setSelected(false);
		nestedShoppingItemDto.getConstituents().get(0).setSelected(false);
		nestedShoppingItemDto.getConstituents().get(1).setSelected(false);
		nestedShoppingItemDto.getConstituents().get(2).setSelected(false);
		shoppingItemDto.addConstituent(nestedShoppingItemDto);

		final ShoppingItemAssemblerImpl assembler = getAssemblerForSelectionRuleTesting();
		assertTrue(assembler.verifySelectionRulesFollowed(productBundle, shoppingItemDto));
	}

	/**
	 * Test verification of bundle selection rules succeeds with a nested bundle. Root bundle is select 1, and nested bundle is select 1, no root
	 * bundle constituents are selected and ONE nested bundle constituent is selected.
	 */
	@Test
	public void testValidationOfNestedBundleSelectOneSucceeds() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(1));

		final ProductBundle nestedProductBundle = createBundleWithThreeConstituents();
		nestedProductBundle.setSelectionRule(new SelectionRuleImpl(1));

		productBundle.addConstituent(createBundleConstituentFrom(nestedProductBundle));

		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.getConstituents().get(2).setSelected(false);
		shoppingItemDto.getConstituents().get(1).setSelected(false);
		shoppingItemDto.getConstituents().get(0).setSelected(false);

		final ShoppingItemDto nestedShoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		nestedShoppingItemDto.getConstituents().get(0).setSelected(false);
		nestedShoppingItemDto.getConstituents().get(1).setSelected(false);
		shoppingItemDto.addConstituent(nestedShoppingItemDto);

		final ShoppingItemAssemblerImpl assembler = getAssemblerForSelectionRuleTesting();
		assertTrue(assembler.verifySelectionRulesFollowed(productBundle, shoppingItemDto));
	}

	/**
	 * Test verification of bundle selection rules fails with a nested bundle. Root bundle is select 1, and nested bundle is select 1, no root bundle
	 * constituents are selected and ALL nested bundle constituents are selected.
	 */
	@Test
	public void testValidationOfNestedBundleSelectOneFail() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(1));

		final ProductBundle nestedProductBundle = createBundleWithThreeConstituents();
		nestedProductBundle.setSelectionRule(new SelectionRuleImpl(1));

		productBundle.addConstituent(createBundleConstituentFrom(nestedProductBundle));

		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.getConstituents().get(2).setSelected(false);
		shoppingItemDto.getConstituents().get(1).setSelected(false);
		shoppingItemDto.getConstituents().get(0).setSelected(false);

		final ShoppingItemDto nestedShoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.addConstituent(nestedShoppingItemDto);

		final ShoppingItemAssemblerImpl assembler = getAssemblerForSelectionRuleTesting();
		assertFalse(assembler.verifySelectionRulesFollowed(productBundle, shoppingItemDto));
	}

	/**
	 * Test that if a bundle is not selected, none of its children should be selected.
	 */
	@Test
	public void testSelectedChildrenInNonSelectedParentFails() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(1));

		final ProductBundle nestedProductBundle = createBundleWithThreeConstituents();
		nestedProductBundle.setSelectionRule(new SelectionRuleImpl(1));

		productBundle.addConstituent(createBundleConstituentFrom(nestedProductBundle));

		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.getConstituents().get(1).setSelected(false);
		shoppingItemDto.getConstituents().get(0).setSelected(false);

		final ShoppingItemDto nestedShoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		nestedShoppingItemDto.setSelected(false);

		shoppingItemDto.addConstituent(nestedShoppingItemDto);

		final ShoppingItemAssemblerImpl assembler = getAssemblerForSelectionRuleTesting();
		assertFalse(assembler.verifySelectionRulesFollowed(productBundle, shoppingItemDto));
	}

	/**
	 * Test verification of bundle selection rules fails with no nesting, select all.
	 */
	@Test
	public void testValidationForBundleWithNoNestingSelectAllFail() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(0));
		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.getConstituents().get(1).setSelected(false);

		final ShoppingItemAssemblerImpl assembler = getAssemblerForSelectionRuleTesting();
		assertFalse(assembler.verifySelectionRulesFollowed(productBundle, shoppingItemDto));
	}

	/**
	 * Test verification of bundle selection rules fails with no nesting, select 1.
	 */
	@Test
	public void testValidationForBundleWithNoNestingSelectOneFail() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(1));
		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.getConstituents().get(1).setSelected(false);

		final ShoppingItemAssemblerImpl assembler = getAssemblerForSelectionRuleTesting();
		assertFalse(assembler.verifySelectionRulesFollowed(productBundle, shoppingItemDto));
	}

	/**
	 * Test verification of bundle selection rules succeeds with no nesting, select 1.
	 */
	@Test
	public void testValidationForBundleWithNoNestingSelectOne() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(1));
		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.getConstituents().get(1).setSelected(false);
		shoppingItemDto.getConstituents().get(2).setSelected(false);

		final ShoppingItemAssemblerImpl assembler = getAssemblerForSelectionRuleTesting();
		assertTrue(assembler.verifySelectionRulesFollowed(productBundle, shoppingItemDto));
	}

	/**
	 * Tests verification of bundle selection rules succeeds with no nesting, select all.
	 */
	@Test
	public void testCreateShoppingItemVerifiesBundleSelectionRules() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(0));
		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		final ShoppingItemAssemblerImpl assembler = getAssemblerForSelectionRuleTesting();
		assertTrue(assembler.verifySelectionRulesFollowed(productBundle, shoppingItemDto));
	}

	private ShoppingItemDto createDtoWithThreeConstituentsAllSelected() {
		final ShoppingItemDto rootDto = new ShoppingItemDto("productCode1", 1);
		rootDto.addConstituent(createConstituentDto(true));
		rootDto.addConstituent(createConstituentDto(true));
		rootDto.addConstituent(createConstituentDto(true));
		return rootDto;
	}

	private ShoppingItemDto createConstituentDto(final boolean selected) {
		final ShoppingItemDto dto = new ShoppingItemDto("skuCode", 1);
		dto.setSelected(selected);
		return dto;
	}

	private BundleConstituent createBundleConstituentFrom(final Product product) {
		final BundleConstituentImpl constituentBundle = new BundleConstituentImpl();
		constituentBundle.setConstituent(product);
		constituentBundle.setQuantity(1);
		return constituentBundle;
	}

	private BundleConstituent createBundleConstituentFromSku(final ProductSku productSku) {
		final BundleConstituentImpl constituentBundle = new BundleConstituentImpl();
		constituentBundle.setConstituent(productSku);
		constituentBundle.setQuantity(1);
		return constituentBundle;
	}

	private ProductBundle createBundleWithThreeConstituents() {
		final ProductBundle rootBundle = new ProductBundleImpl();
		rootBundle.addConstituent(createBundleConstituentFrom(null));
		rootBundle.addConstituent(createBundleConstituentFrom(null));
		rootBundle.addConstituent(createBundleConstituentFrom(null));
		return rootBundle;
	}

	/**
	 * @return
	 */
	private ShoppingItemAssemblerImpl getAssemblerForSelectionRuleTesting() {
		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			ProductSku retrieveSkuForShoppingItem(final BundleConstituent bundleItem, final ShoppingItemDto thisShoppingItemDto) {
				// not testing this part
				return new ProductSkuImpl();
			}

			@Override
			protected ProductSku getSkuFromProduct(final Product product, final String skuCode) {
				return new ProductSkuImpl();
			}

			@Override
			public ShoppingItemFactory getShoppingItemFactory() {
				return new ShoppingItemFactoryImpl() {
					@Override
					protected ShoppingItem getShoppingItemBean() {
						return new ShoppingItemImpl();
					}

					@Override
					protected void sanityCheck(final ProductSku sku, final Price price) {
						return;
					}

					@Override
					protected int getMinQuantity(final ProductSku sku) {
						return 1;
					}
				};
			}

		};
		return assembler;
	}

	/**
	 * Tests that a {@code ShoppingItemDto} with 3 direct children will have a ShoppingItem created for each child, with a single ShoppingItem
	 * parent.
	 */
	@Test
	public void testTraverseAllChildren() {

		final ShoppingItemDto rootShoppingItemDto = new ShoppingItemDto("sku", 1);
		final ShoppingItemDto childDto1 = new ShoppingItemDto("childDto1", 2);
		final ShoppingItemDto childDto2 = new ShoppingItemDto("childDto2", 3);
		final ShoppingItemDto childDto3 = new ShoppingItemDto("childDto3", 4);

		rootShoppingItemDto.addConstituent(childDto1);
		rootShoppingItemDto.addConstituent(childDto2);
		rootShoppingItemDto.addConstituent(childDto3);

		final ProductBundle bundle = context.mock(ProductBundle.class);

		final List<BundleConstituent> bundleConstituents = new ArrayList<BundleConstituent>();

		final BundleConstituent const1 = new BundleConstituentImpl();
		final Product productA = new ProductImpl();
		const1.setConstituent(productA);
		const1.setQuantity(1);
		bundleConstituents.add(const1);

		final BundleConstituent const2 = new BundleConstituentImpl();
		final Product productB = new ProductImpl();
		const2.setConstituent(productB);
		const2.setQuantity(1);
		bundleConstituents.add(const2);

		final BundleConstituent const3 = new BundleConstituentImpl();
		final Product productC = new ProductImpl();
		const3.setConstituent(productC);
		const3.setQuantity(Integer.valueOf(1));
		bundleConstituents.add(const3);

		final ProductSku productSku = context.mock(ProductSku.class);
		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			ProductSku retrieveSkuForShoppingItem(final BundleConstituent bundleItem, final ShoppingItemDto thisShoppingItemDto) {
				return productSku;
			}
		};
		final ShoppingItemFactory shoppingItemFactory = context.mock(ShoppingItemFactory.class);
		assembler.setShoppingItemFactory(shoppingItemFactory);

		final Sequence visitSequence = context.sequence("visitSequence");
		final SelectionRule selectionRule = context.mock(SelectionRule.class);

		context.checking(new Expectations() {
			{
				allowing(bundle).getConstituents();
				will(returnValue(bundleConstituents));
				allowing(bundle).getSelectionRule();
				will(returnValue(selectionRule));
				allowing(selectionRule).getParameter();
				will(returnValue(0));
				oneOf(shoppingItemFactory).createShoppingItem(productSku, null, 1, 0, Collections.<String, String>emptyMap());
				inSequence(visitSequence);
				oneOf(shoppingItemFactory).createShoppingItem(productSku, null, 1, 1, Collections.<String, String>emptyMap());
				inSequence(visitSequence);
				oneOf(shoppingItemFactory).createShoppingItem(productSku, null, 1, 2, Collections.<String, String>emptyMap());
				inSequence(visitSequence);

			}
		});
		final ShoppingItem parent = new ShoppingItemImpl();
		assembler.createShoppingItemTree(bundle, rootShoppingItemDto, parent, 1);

	}

	/**
	 * Tests that a {@code ShoppingItem} with a child and a grandchild will have a ShoppingItem for both the child and the grandchild when the
	 * children are created.
	 */
	@Test
	public void testTraverseGrandChildren() {
		//
		// Create ShoppingItemDto Tree
		//
		final ShoppingItemDto rootShoppingItemDto = new ShoppingItemDto("sku", 1);

		final ShoppingItemDto childShoppingItemDto = new ShoppingItemDto("child", 2);
		childShoppingItemDto.setSelected(true);
		rootShoppingItemDto.addConstituent(childShoppingItemDto);

		final ShoppingItemDto grandChildShoppingItemDto = new ShoppingItemDto("grandchild", 3);
		grandChildShoppingItemDto.setSelected(true);
		childShoppingItemDto.addConstituent(grandChildShoppingItemDto);
		//
		// Create ProductBundle Tree
		//
		final ProductBundle rootBundle = new ProductBundleImpl();
		rootBundle.setSelectionRule(new SelectionRuleImpl(0));
		final ProductBundle childBundle = new ProductBundleImpl();
		childBundle.setSelectionRule(new SelectionRuleImpl(0));
		final Product grandChildProduct = new ProductImpl();
		rootBundle.addConstituent(createBundleConstituentFrom(childBundle));
		childBundle.addConstituent(createBundleConstituentFrom(grandChildProduct));

		//
		// Create Assembler implementation for testing
		//
		final ProductSku productSku = context.mock(ProductSku.class);
		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			ProductSku retrieveSkuForShoppingItem(final BundleConstituent bundleItem, final ShoppingItemDto thisShoppingItemDto) {
				return productSku;
			}
		};

		final ShoppingItemFactory shoppingItemFactory = context.mock(ShoppingItemFactory.class);
		assembler.setShoppingItemFactory(shoppingItemFactory);

		final Sequence visitSequence = context.sequence("visitSequence");

		context.checking(new Expectations() {
			{
				// The root ShoppingItem should be passed in, so it won't have to be created
				oneOf(shoppingItemFactory).createShoppingItem(productSku, null, 1, 0, Collections.<String, String>emptyMap());
				will(returnValue(new ShoppingItemImpl()));
				inSequence(visitSequence);
				oneOf(shoppingItemFactory).createShoppingItem(productSku, null, 1, 0, Collections.<String, String>emptyMap());
				will(returnValue(new ShoppingItemImpl()));
				inSequence(visitSequence);
			}
		});
		final ShoppingItem parent = new ShoppingItemImpl();
		assembler.createShoppingItemTree(rootBundle, rootShoppingItemDto, parent, 1);

	}

	/**
	 * Tests that if a product has multiple skus, the product doesn't have its skus loaded, and a sku code is specified, then the product will be
	 * loaded with its skus from the product service.
	 */
	@Test
	public void testProcessNodeMultipleSku() {
		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl();

		final ProductSku productSku = new ProductSkuImpl();
		productSku.setGuid(AAA);

		final ProductType productType = new ProductTypeImpl();
		productType.setWithMultipleSkus(true);

		final String productCode = "PRODUCT";
		final Product multiSkuProductWithoutSkus = new ProductImpl();
		multiSkuProductWithoutSkus.setCode(productCode);
		multiSkuProductWithoutSkus.setProductType(productType);

		final Product multiSkuProductWithSkus = new ProductImpl();
		multiSkuProductWithSkus.setCode(productCode);
		productSku.setProduct(multiSkuProductWithSkus);
		multiSkuProductWithSkus.setProductType(productType);

		final ProductService productService = context.mock(ProductService.class);
		assembler.setProductService(productService);
		final ShoppingItemFactory shoppingItemFactory = context.mock(ShoppingItemFactory.class);
		assembler.setShoppingItemFactory(shoppingItemFactory);

		context.checking(new Expectations() {
			{
				oneOf(productService).getProductWithSkus(multiSkuProductWithoutSkus);
				will(returnValue(multiSkuProductWithSkus));
			}
		});

		assembler.getSkuFromProduct(multiSkuProductWithoutSkus, AAA);

	}

	/**
	 * Test that if a DTO for a bundle product contains reference to a sku that is not in the bundle, then the structure is deemed to be invalid.
	 */
	@Test
	public void testDtoConstituentNotInBundleInvalid() {
		// Create a bundle with a bundle with a product - each has a sku
		final ProductBundle rootBundle = new ProductBundleImpl();
		rootBundle.setCode(AAA);
		final ProductBundle nestedBundle = new ProductBundleImpl();
		nestedBundle.setCode(BBB);
		final Product leafProduct = new ProductImpl();
		leafProduct.setCode(CCC);
		final BundleConstituent rootBundleConstituent = new BundleConstituentImpl();
		rootBundleConstituent.setConstituent(nestedBundle);
		rootBundle.addConstituent(rootBundleConstituent);
		final BundleConstituent nestedBundleConstituent = new BundleConstituentImpl();
		nestedBundleConstituent.setConstituent(leafProduct);
		nestedBundle.addConstituent(nestedBundleConstituent);
		final ProductSku rootSku = new ProductSkuImpl();
		rootSku.setGuid(AAA);
		rootBundle.addOrUpdateSku(rootSku);
		final ProductSku nestedSku = new ProductSkuImpl();
		nestedSku.setGuid(BBB);
		nestedBundle.addOrUpdateSku(nestedSku);
		final ProductSku leafSku = new ProductSkuImpl();
		leafSku.setGuid(CCC);
		leafProduct.addOrUpdateSku(leafSku);
		// Create a ShoppingItemDto that mirrors the bundle tree, with a different skucode for the leaf
		final ShoppingItemDto rootDto = new ShoppingItemDto(AAA, 1);
		final ShoppingItemDto nestedDto = new ShoppingItemDto(BBB, 1);
		final ShoppingItemDto leafDto = new ShoppingItemDto("SOMETHING_ELSE", 1);
		rootDto.addConstituent(nestedDto);
		nestedDto.addConstituent(leafDto);

		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl();
		assertFalse(assembler.verifyDtoStructureEqualsBundleStructure(rootBundle, rootDto));
	}

	/**
	 * Test that if a DTO for a non-bundle product specifies a skucode for a sku that doesn't exist in the product, then structure is deemed to be
	 * invalid.
	 */
	@Test
	public void testDtoSkuNotInBundleInvalid() {
		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			protected ProductSku getSkuFromProduct(final Product product, final String skuCode) {
				return null;
			}
		};

		final Product product = new ProductImpl();
		product.setCode(AAA);

		final ShoppingItemDto dto = new ShoppingItemDto(BBB, 1);
		dto.setProductCode(AAA);

		assertFalse(assembler.verifyDtoStructureEqualsBundleStructure(product, dto));
	}

	/**
	 * Tests that if the DTO structure is deemed to be invalid then an exception is thrown.
	 */
	@Test(expected = EpSystemException.class)
	public void testDtoStructureInvalidThrowsException() {
		final ProductSku productSku = new ProductSkuImpl();
		final Product product = new ProductImpl();
		product.addOrUpdateSku(productSku);

		final ShoppingItemDto dto = new ShoppingItemDto(AAA, 1);

		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			ProductSku getProductSku(final String currentSkuGuid) {
				return productSku;
			}

			@Override
			protected boolean verifySelectionRulesFollowed(final Product product, final ShoppingItemDto shoppingItemDto) {
				return true;
			}

			@Override
			protected boolean verifyDtoStructureEqualsBundleStructure(final Product product, final ShoppingItemDto dtoNode) {
				return false;
			}
		};
		assembler.createShoppingItem(dto);
	}

	/**
	 * Test that when a ShoppingItemDto is created from a ShoppingItem, any applicable Bundle items that are not in the ShoppingItem are added to the
	 * ShoppingItemDto so that the DTO's structure mirrors the Bundle's structure.
	 */
	@Test
	public void testBundleWithSkuConstituent() {
		// Create a bundle with two items and a select 1 rule
		final String selectedSkuGuid = SELECTED_SKU;
		final String nonSelectedSkuGuid = "nonSelectedSku";
		final String bundleSkuGuid = "bundleSku";
		final Product selectedProduct = new ProductImpl();
		final ProductSku selectedSku = new ProductSkuImpl();
		selectedSku.setGuid(selectedSkuGuid);
		selectedSku.setSkuCode(selectedSkuGuid);
		selectedProduct.addOrUpdateSku(selectedSku);

		final Product nonSelectedProduct = new ProductImpl();
		final ProductSku nonSelectedSku = new ProductSkuImpl();
		nonSelectedSku.setGuid(nonSelectedSkuGuid);
		nonSelectedSku.setSkuCode(nonSelectedSkuGuid);
		nonSelectedProduct.addOrUpdateSku(nonSelectedSku);

		final BundleConstituent selectedBundleItem = new BundleConstituentImpl();
		selectedBundleItem.setQuantity(1);
		selectedBundleItem.setConstituent(selectedProduct);
		final BundleConstituent nonSelectedBundleItem = new BundleConstituentImpl();
		nonSelectedBundleItem.setQuantity(1);
		nonSelectedBundleItem.setConstituent(nonSelectedProduct);

		final ProductBundle bundle = new ProductBundleImpl();
		bundle.setSelectionRule(new SelectionRuleImpl(1));
		final ProductSku bundleSku = new ProductSkuImpl();
		bundleSku.setSkuCode(bundleSkuGuid);
		bundleSku.setGuid(bundleSkuGuid);
		bundle.addOrUpdateSku(bundleSku);
		bundle.addConstituent(selectedBundleItem);
		bundle.addConstituent(nonSelectedBundleItem);
		// Create a corresponding ShoppingItem
		final ShoppingItem shoppingItem = new TestShoppingItemImpl();
		shoppingItem.setProductSku(bundleSku);
		final ShoppingItem nestedShoppingItem = new TestShoppingItemImpl();
		nestedShoppingItem.setProductSku(selectedSku);
		shoppingItem.addChild(nestedShoppingItem);
		// Assemble the ShoppingItemDto and check it.
		final ShoppingItemAssembler assembler = new ShoppingItemAssemblerImpl() {
			@Override
			public ShoppingItemDtoFactory getShoppingItemDtoFactory() {
				return new ShoppingItemDtoFactoryImpl();
			}
		};
		final ShoppingItemDto dto = assembler.assembleShoppingItemDtoFrom(shoppingItem);
		assertEquals("DTO should have the same number of constituents as the Bundle", dto.getConstituents().size(), bundle.getConstituents().size());
		assertEquals("DTO's first sku should be the same as the bundle's first sku.", selectedSkuGuid, dto.getConstituents().get(0).getSkuCode());
		assertEquals("DTO's second sku should be the same as the bundle's second sku.", nonSelectedSkuGuid, dto.getConstituents().get(1)
				.getSkuCode());
		assertTrue("DTO's first sku should be selected", dto.getConstituents().get(0).isSelected());
		assertFalse("DTO's second sku should not be selected", dto.getConstituents().get(1).isSelected());
	}

	/** Test shopping item object overrides getPrice and makeMoney. */
	class TestShoppingItemImpl extends ShoppingItemImpl {
		private static final long serialVersionUID = 1L;

		@Override
		protected Money makeMoney(final BigDecimal amount) {
			if (amount == null) {
				return null;
			}
			return MoneyFactory.createMoney(amount, getCurrency());
		};

		@Override
		public Price getPrice() {
			final Price price = new PriceImpl();
			price.setCurrency(getCurrency());
			return price;
		}
	}

	/**
	 * Test that when a ShoppingItemDto is configured from a ShoppingItem, the prices and the UIDPK are copied over (recursively through the tree).
	 * Also check that any items in the ShoppingItem are marked as "selected" on the ShoppingItemDto. A simple parent->child->grandchild is tested to
	 * check recursion.
	 */
	@Test
	public void testConfigureShoppingItemDto() {
		// ShoppingItemDto rootDto = new ShoppingItemDto(AAA, 1);

		// Create grandChild product/sku
		final ProductSku grandChildProductSku = new ProductSkuImpl();
		final Product grandChildProduct = new ProductImpl();
		grandChildProduct.setCode("ProdC");
		grandChildProductSku.setSkuCode(BBB);
		grandChildProductSku.setGuid(BBB);
		grandChildProduct.addOrUpdateSku(grandChildProductSku);
		// Create child product(Bundle)/sku
		final ProductSku childProductSku = new ProductSkuImpl();
		final ProductBundle childProduct = new ProductBundleImpl();
		childProduct.setCode("ProdB");
		childProductSku.setSkuCode(AAA);
		childProductSku.setGuid(AAA);
		childProduct.addOrUpdateSku(childProductSku);
		final BundleConstituent childConstituent = createBundleConstituentFrom(grandChildProduct);
		childProduct.addConstituent(childConstituent);
		// Create root bundle/sku
		final ProductSku rootSku = new ProductSkuImpl();
		rootSku.setSkuCode(SKU_B);
		rootSku.setGuid(SKU_B);
		final ProductBundle productBundle = new ProductBundleImpl();
		productBundle.setCode("ProdA");
		productBundle.addOrUpdateSku(rootSku);
		productBundle.addConstituent(createBundleConstituentFrom(childProduct));

		// Create shopping item tree
		final ShoppingItem rootItem = new TestShoppingItemImpl();
		rootItem.setProductSku(rootSku);
		rootItem.setUidPk(SHOPPING_ITEM_UIDPK);

		final ShoppingItem childItem = new TestShoppingItemImpl();
		childItem.setProductSku(childProductSku);
		childItem.setUidPk(SHOPPING_ITEM_UIDPK + 1);

		final ShoppingItem grandChildItem = new TestShoppingItemImpl();
		grandChildItem.setProductSku(grandChildProductSku);
		grandChildItem.setUidPk(SHOPPING_ITEM_UIDPK + 2);
		childItem.addChildItem(grandChildItem);

		rootItem.addChildItem(childItem);

		final Price price = context.mock(Price.class);
		final Money money = MoneyFactory.createMoney(BigDecimal.ONE, Currency.getInstance(CURRENCY_CAD));

		context.checking(new Expectations() {
			{
				allowing(price).getCurrency();
				will(returnValue(Currency.getInstance(CURRENCY_CAD)));
				allowing(price).getListPrice(1);
				will(returnValue(money));
				allowing(price).getSalePrice(1);
				will(returnValue(money));
				allowing(price).getComputedPrice(1);
				will(returnValue(money));

				allowing(price).getPricingScheme(); will(returnValue(null));

			}
		});

		rootItem.setPrice(1, price);

		final ShoppingItemAssembler assembler = new ShoppingItemAssemblerImpl() {
			@Override
			public ShoppingItemDtoFactory getShoppingItemDtoFactory() {
				return new ShoppingItemDtoFactoryImpl();
			}
		};
		final ShoppingItemDto shoppingItemDto = assembler.assembleShoppingItemDtoFrom(rootItem);
		assertEquals(SKU_B, shoppingItemDto.getSkuCode());
		assertEquals("ProdA", shoppingItemDto.getProductCode());
		assertEquals(1, shoppingItemDto.getQuantity());
		assertEquals(SHOPPING_ITEM_UIDPK, shoppingItemDto.getShoppingItemUidPk());

		assertEquals(1, shoppingItemDto.getConstituents().size());
		final ShoppingItemDto actualChild = shoppingItemDto.getConstituents().get(0);
		assertEquals(AAA, actualChild.getSkuCode());
		assertEquals(1, actualChild.getConstituents().size());
		assertEquals(BBB, actualChild.getConstituents().get(0).getSkuCode());

	}

	/**
	 * Tests that a {@code ShoppingItem} can be created from a {@code ShoppingItemDto} which has only the root sku code and not the children. This
	 * code is used when an accessory that is a bundle is added to the cart. <br/>
	 */
	@Test
	public void testCreateChildrenForAccessoryBundle() {
		final ShoppingItemDto parentShoppingItemDto = new ShoppingItemDto("", 0);

		final ProductBundle bundle = context.mock(ProductBundle.class);

		final List<BundleConstituent> bundleConstituents = new ArrayList<BundleConstituent>();

		final BundleConstituent const1 = new BundleConstituentImpl();
		final Product productA = new ProductImpl();
		const1.setConstituent(productA);
		const1.setQuantity(1);
		bundleConstituents.add(const1);

		final ProductSku productSku = context.mock(ProductSku.class);
		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			ProductSku retrieveSkuForShoppingItem(final BundleConstituent bundleItem, final ShoppingItemDto thisShoppingItemDto) {
				return productSku;
			}
		};

		final ShoppingItemFactory shoppingItemFactory = context.mock(ShoppingItemFactory.class);
		assembler.setShoppingItemFactory(shoppingItemFactory);
		final SelectionRule selectionRule = context.mock(SelectionRule.class);
		context.checking(new Expectations() {
			{
				allowing(bundle).getConstituents();
				will(returnValue(bundleConstituents));
				allowing(bundle).getSelectionRule();
				will(returnValue(selectionRule));
				allowing(selectionRule).getParameter();
				will(returnValue(0));
				oneOf(shoppingItemFactory).createShoppingItem(productSku, null, 1, 0, Collections.<String, String>emptyMap());
			}
		});
		final ShoppingItem parent = new ShoppingItemImpl();
		assembler.createShoppingItemTree(bundle, parentShoppingItemDto, parent, 1);

	}

	/**
	 * Tests that no shoppingItems are created for the bundle if no constituents are needed. Top constituent at top level is selected. Nested bundle
	 * is not.
	 */
	@Test
	public void testCreateShoppingItemTreeWhenNestedBundleHasNoSelection() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(1));
		final ProductBundle nestedProductBundle = createBundleWithThreeConstituents();
		nestedProductBundle.setSelectionRule(new SelectionRuleImpl(1));

		productBundle.addConstituent(createBundleConstituentFrom(nestedProductBundle));

		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.getConstituents().get(2).setSelected(false);
		shoppingItemDto.getConstituents().get(1).setSelected(false);

		final ShoppingItemDto nestedShoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		nestedShoppingItemDto.setSelected(false);
		nestedShoppingItemDto.getConstituents().get(0).setSelected(false);
		nestedShoppingItemDto.getConstituents().get(1).setSelected(false);
		nestedShoppingItemDto.getConstituents().get(2).setSelected(false);
		shoppingItemDto.addConstituent(nestedShoppingItemDto);

		final ShoppingItemAssemblerImpl assembler = getAssemblerForSelectionRuleTesting();

		final ShoppingItemImpl parent = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isBundle() {
				return true;
			}
		};
		assembler.createShoppingItemTree(productBundle, shoppingItemDto, parent, 1);
		assertEquals(1, parent.getBundleItems().size());
	}

	/**
	 * Test that the {@code ShoppingItem} created from the {@code ShoppingItemDto} only contains the skus that are flagged as "selected" on the DTO.
	 */
	@Test
	public void testShoppingItemContainsOnlySelectedSkusFromDto() {
		// Create a bundle with two items and a select 1 rule
		final String selectedSkuGuid = SELECTED_SKU;
		final String nonSelectedSkuGuid = "nonSelectedSku";
		final Product selectedProduct = new ProductImpl();
		final ProductSku selectedSku = new ProductSkuImpl();
		selectedSku.setSkuCode(selectedSkuGuid);
		selectedProduct.addOrUpdateSku(selectedSku);

		final Product nonSelectedProduct = new ProductImpl();
		final ProductSku nonSelectedSku = new ProductSkuImpl();
		nonSelectedSku.setSkuCode(nonSelectedSkuGuid);
		nonSelectedProduct.addOrUpdateSku(nonSelectedSku);

		final BundleConstituent selectedBundleItem = new BundleConstituentImpl();
		selectedBundleItem.setQuantity(1);
		selectedBundleItem.setConstituent(selectedProduct);
		final BundleConstituent nonSelectedBundleItem = new BundleConstituentImpl();
		nonSelectedBundleItem.setQuantity(1);
		nonSelectedBundleItem.setConstituent(nonSelectedProduct);

		final ProductBundle bundle = new ProductBundleImpl();
		bundle.setSelectionRule(new SelectionRuleImpl(1));
		final ProductSku bundleSku = new ProductSkuImpl();
		bundleSku.setSkuCode("bundleSku");
		bundle.addOrUpdateSku(bundleSku);
		bundle.addConstituent(selectedBundleItem);
		bundle.addConstituent(nonSelectedBundleItem);

		// Create a DTO matching the bundle, with 1 item selected
		final ShoppingItemDto rootDto = new ShoppingItemDto("", 1);
		final ShoppingItemDto selectedDto = new ShoppingItemDto(selectedSkuGuid, 1);
		selectedDto.setSelected(true);
		final ShoppingItemDto nonSelectedDto = new ShoppingItemDto(nonSelectedSkuGuid, 1);
		nonSelectedDto.setSelected(false);
		rootDto.addConstituent(selectedDto);
		rootDto.addConstituent(nonSelectedDto);

		// assemble the ShoppingItem
		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			protected ProductSku getProductSku(final String skuCode) {
				if (skuCode.equals(selectedSkuGuid)) {
					return selectedSku;
				} else if (skuCode.equals(nonSelectedSkuGuid)) {
					return nonSelectedSku;
				}
				return bundleSku;
			}
		};
		final ShoppingItemFactory shoppingItemFactory = context.mock(ShoppingItemFactory.class);
		assembler.setShoppingItemFactory(shoppingItemFactory);
		context.checking(new Expectations() {
			{
				// ShoppingItemFactory should only be called TWICE!
				oneOf(shoppingItemFactory).createShoppingItem(bundleSku, null, 1, 0, Collections.<String, String>emptyMap());
				oneOf(shoppingItemFactory).createShoppingItem(selectedSku, null, 1, 0, Collections.<String, String>emptyMap());
			}
		});
		assembler.createShoppingItem(rootDto);
		// Verify that the ShoppingItem only has one item in it (the selected one).
	}

	/**
	 * Test that when a simple multi-sku product (non-bundle) is in a shoppingItem by itself, the ShoppingItemDto that was created with the product's
	 * default sku is updated to the sku specified in the shopping item.
	 */
	@Test
	public void testDtoUpdatedToShoppingItemSkuForMultiSkuProducts() {
		// Create a multisku product
		final ProductSku defaultSku = new ProductSkuImpl();
		final String defaultSkuCode = "defaultSku";
		defaultSku.setSkuCode(defaultSkuCode);
		final ProductSku selectedSku = new ProductSkuImpl();
		selectedSku.setSkuCode(SELECTED_SKU);
		final Product product = new ProductImpl();
		product.addOrUpdateSku(defaultSku);
		product.addOrUpdateSku(selectedSku);
		product.setDefaultSku(defaultSku);
		// Create a shoppingItem for the multisku product's default sku
		final ShoppingItem shoppingItem = new ShoppingItemImpl();
		shoppingItem.setProductSku(selectedSku);
		final ShoppingItemDto dto = new ShoppingItemDto(null, 1);
		dto.setSkuCode(defaultSkuCode);
		// configure the dto, and check that it's been updated.
		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			void configureShoppingItemDtoPricesFromShoppingItem(final ShoppingItemDto rootDto, final ShoppingItem rootItem) {
				// do nothing - not testing this part
			}
		};
		assertEquals("The DTO should have been updated to reflect the actual sku specified in the ShoppingItem, rather than the"
				+ "multi-sku product's default sku", selectedSku.getSkuCode(), assembler.configureShoppingItemDtoFromShoppingItem(dto, shoppingItem)
				.getSkuCode());
	}

	/**
	 * Test that when a simple multi-sku product (non-bundle) is in a shoppingItem by itself, the ShoppingItemDto that was created with the product's
	 * default sku is updated to the sku specified in the shopping item.
	 */
	@Test
	public void testDtoUpdatedToShoppingItemSkuForMultiSkuProducts2() {
		// Create a multisku product
		final ProductSku defaultSku = new ProductSkuImpl();
		final String defaultSkuCode = "defaultSku";
		defaultSku.setSkuCode(defaultSkuCode);
		defaultSku.setGuid(defaultSkuCode);
		final ProductSku selectedSku = new ProductSkuImpl();
		selectedSku.setSkuCode(SELECTED_SKU);
		selectedSku.setGuid(SELECTED_SKU);
		final Product product = new ProductImpl();
		product.addOrUpdateSku(defaultSku);
		product.addOrUpdateSku(selectedSku);
		product.setDefaultSku(defaultSku);
		product.setCode(A_PRODUCT_CODE);

		final ProductBundle bundle = new ProductBundleImpl();
		final ProductSku bundleSku = new ProductSkuImpl();
		final String bundleCode = "bundle";
		bundleSku.setSkuCode(bundleCode);
		bundleSku.setGuid(bundleCode);
		bundle.addOrUpdateSku(bundleSku);
		bundle.addConstituent(createBundleConstituentFromSku(defaultSku));
		bundle.addConstituent(createBundleConstituentFromSku(selectedSku));
		bundle.setSelectionRule(new SelectionRuleImpl(1));
		bundle.setCode(bundleCode);

		final ProductSkuService productSkuService = context.mock(ProductSkuService.class);

		context.checking(new Expectations() {
			{
				allowing(productSkuService).findBySkuCode(bundleCode);
				will(returnValue(bundleSku));
			}
		});
		// configure the dto, and check that it's been updated.
		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			void configureShoppingItemDtoPricesFromShoppingItem(final ShoppingItemDto rootDto, final ShoppingItem rootItem) {
				// do nothing - not testing this part
			}
		};
		assembler.setShoppingItemDtoFactory(new ShoppingItemDtoFactoryImpl());
		assembler.setProductSkuService(productSkuService);
		final ShoppingItemFactoryImpl shoppingItemFactory = new ShoppingItemFactoryImpl();
		shoppingItemFactory.setBeanFactory(beanFactory);
		assembler.setShoppingItemFactory(shoppingItemFactory);
		final ShoppingItemDto dto = assembler.createShoppingItemDto(bundle, 1);
		dto.getConstituents().get(1).setSelected(true);
		final ShoppingItem shoppingItem = assembler.createShoppingItem(dto);
		assembler.configureShoppingItemDtoFromShoppingItem(dto, shoppingItem);
		assertTrue(assembler.verifyDtoStructureEqualsBundleStructure(bundle, dto));
	}

	/**
	 * Tests if it can find the appropriate DTO from the DTO list. And make sure that only appropriate DTO is selected.
	 */
	@Test
	public void testAssemblingShoppingItemFormBeanWhenOneOfTheTwoSameMultiSkuConstituentsIsSelected() {
		// given
		final ProductBundle bundle = createProductBundleWithSkuCode(A_BUNDLE_CODE, A_BUNDLE_SKU_CODE);
		final Product product = createProductWithSkuCode(A_PRODUCT_CODE, A_PRODUCT_SKU_CODE);
		final ProductSku secondSku = new ProductSkuImpl();
		secondSku.setSkuCode(ANOTHER_PRODUCT_SKU_CODE);
		secondSku.setGuid(ANOTHER_PRODUCT_SKU_CODE);
		product.addOrUpdateSku(secondSku);

		bundle.addConstituent(createBundleConstituentFrom(product));
		bundle.addConstituent(createBundleConstituentFrom(product));

		final ShoppingItem root = createFakeShoppingItem();
		root.setProductSku(bundle.getDefaultSku());

		final ShoppingItem child1 = createFakeShoppingItem();
		child1.setProductSku(product.getSkuByCode(ANOTHER_PRODUCT_SKU_CODE));
		root.addChildItem(child1);

		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			public ShoppingItemDtoFactory getShoppingItemDtoFactory() {
				return new ShoppingItemDtoFactoryImpl();
			}
		};

		// test
		final ShoppingItemDto rootDto = assembler.assembleShoppingItemDtoFrom(root);
		Assert.assertEquals(2, rootDto.getConstituents().size());
		Assert.assertTrue(rootDto.getConstituents().get(0).isSelected());
		Assert.assertFalse(rootDto.getConstituents().get(1).isSelected());
	}

	/**
	 * Tests assembling ShoppingItemFormBeans from ShoppingItem when shopping item has same constituents.
	 */
	@Test
	public void testAssemblingShoppingItemFormBeanFromShoppingItemWithTwoSameConstituents() {
		// given
		final ProductBundle bundle = createProductBundleWithSkuCode(A_BUNDLE_CODE, A_BUNDLE_SKU_CODE);
		final Product product = createProductWithSkuCode(A_PRODUCT_CODE, A_PRODUCT_SKU_CODE);
		bundle.addConstituent(createBundleConstituentFrom(product));
		bundle.addConstituent(createBundleConstituentFrom(product));

		final ShoppingItem root = createFakeShoppingItem();
		root.setProductSku(bundle.getDefaultSku());

		final ShoppingItem child1 = createFakeShoppingItem();
		child1.setProductSku(product.getDefaultSku());
		child1.setOrdering(0);
		final ShoppingItem child2 = createFakeShoppingItem();
		child2.setProductSku(product.getDefaultSku());
		child2.setOrdering(1);

		root.addChildItem(child1);
		root.addChildItem(child2);

		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			public ShoppingItemDtoFactory getShoppingItemDtoFactory() {
				return new ShoppingItemDtoFactoryImpl();
			}
		};

		// test
		final ShoppingItemDto rootDto = assembler.assembleShoppingItemDtoFrom(root);
		Assert.assertEquals(2, rootDto.getConstituents().size());
		Assert.assertTrue(rootDto.getConstituents().get(0).isSelected());
		Assert.assertTrue(rootDto.getConstituents().get(1).isSelected());
	}

	private ShoppingItemImpl createFakeShoppingItem() {
		return new ShoppingItemImpl();
	}

	/**
	 * Tests creating shopping item from a bundle which has two of same constituents.
	 */
	@Test
	public void testCreatingShoppingItemFromBundleWithTwoSameConstituents() {
		// given
		final ProductBundle bundle = createProductBundleWithSkuCode(A_BUNDLE_CODE, A_BUNDLE_SKU_CODE);
		bundle.addConstituent(createBundleConstituentFrom(createProductWithSkuCode(A_PRODUCT_CODE, A_PRODUCT_SKU_CODE)));
		bundle.addConstituent(createBundleConstituentFrom(createProductWithSkuCode(A_PRODUCT_CODE, A_PRODUCT_SKU_CODE)));

		final ShoppingItemDto bundleDto = new ShoppingItemDto(A_BUNDLE_SKU_CODE, 1);
		final ShoppingItemDto childDto1 = new ShoppingItemDto(A_PRODUCT_SKU_CODE, 1);
		final ShoppingItemDto childDto2 = new ShoppingItemDto(A_PRODUCT_SKU_CODE, 1);

		bundleDto.addConstituent(childDto1);
		bundleDto.addConstituent(childDto2);

		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			public ShoppingItemFactory getShoppingItemFactory() {
				return new ShoppingItemFactoryImpl() {
					@Override
					protected ShoppingItem getShoppingItemBean() {
						return new ShoppingItemImpl();
					}
				};
			}
		};

		// test
		final ShoppingItemImpl shoppingItem = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isBundle() {
				return true;
			}
		};
		assembler.createShoppingItemTree(bundle, bundleDto, shoppingItem, 1);
		Assert.assertEquals(2, shoppingItem.getBundleItems().size());
		Assert.assertEquals(A_PRODUCT_SKU_CODE, shoppingItem.getBundleItems().get(0).getProductSku().getSkuCode());
		Assert.assertEquals(A_PRODUCT_SKU_CODE, shoppingItem.getBundleItems().get(1).getProductSku().getSkuCode());
	}

	private Product createProductWithSkuCode(final String productCode, final String skuCode) {
		final Product product = new ProductImpl();
		product.setCode(productCode);

		final ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode(skuCode);
		sku.setGuid(skuCode);
		product.addOrUpdateSku(sku);
		return product;
	}

	private ProductBundle createProductBundleWithSkuCode(final String bundleCode, final String skuCode) {
		final ProductBundle bundle = new ProductBundleImpl();
		bundle.setCode(bundleCode);

		final ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode(skuCode);

		bundle.addOrUpdateSku(sku);
		return bundle;
	}

}
