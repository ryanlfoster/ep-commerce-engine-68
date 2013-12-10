package com.elasticpath.sfweb.controller.impl;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.pricing.impl.PriceAdjustmentImpl;
import com.elasticpath.sfweb.ajax.bean.JsonBundleItemBean;
import com.elasticpath.sfweb.ajax.bean.impl.JsonBundleItemBeanImpl;
import com.elasticpath.sfweb.ajax.service.impl.JsonBundleFactoryImpl;
import com.elasticpath.sfweb.ajax.service.impl.PriceBuilder;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.impl.ShoppingItemFormBeanImpl;
import com.elasticpath.sfweb.test.BeanFactoryExpectationsFactory;

/**
 * Suite of tests for the json bundle class.
 *
 * @author shallinan
 */
@SuppressWarnings({"PMD.ExcessiveMethodLength"})
public class JsonBundleFactoryTest {
	private static final Logger LOG = Logger.getLogger(JsonBundleFactoryTest.class);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final int LEVEL_3 = 3;
	private static final String USD = "USD";
	private static final String CONSTITUENT_1_CONSTITUENT_0 = "constituent[1].constituent[0]";
	private static final String BUN_B_SKU_CODE = "BUN_B_SKU_CODE";
	private static final String B_BUNDLE = "B_BUNDLE";
	private static final String CONSTITUENT_1 = "constituent[1]";
	private static final String CONSTITUENT_0 = "constituent[0]";
	private static final String BUN_A_SKU_CODE = "BUN_A_SKU_CODE";
	private static final String B_SKU_CODE = "B_SKU_CODE";
	private static final String B_PRODUCT = "B_PRODUCT";
	private static final String A_PRODUCT = "A_PRODUCT";
	private static final String A_SKU_CODE = "A_SKU_CODE";
	private static final String A_BUNDLE = "A_BUNDLE";
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private final JsonBundleFactoryImpl jsonBundlefactory = new JsonBundleFactoryImpl();

	private final Properties jsonValues = new Properties();

	/**
	 * Setup required before each test.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("jsonBundle", JsonBundleItemBeanImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("moneyFormatter", StandardMoneyFormatter.class);

		jsonBundlefactory.setBeanFactory(beanFactory);
		jsonBundlefactory.setPriceBuilder(new PriceBuilder());
		try {
			jsonValues.load(this.getClass().getResourceAsStream("/JsonValues.properties"));
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/** . **/
	@Test
	public void testGettingRootLevelSelectionRule() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		ShoppingItemFormBean shoppingItemFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle), 1, "", 0, true, false, false);

		// test
		JsonBundleItemBean jsonBundle = jsonBundlefactory.createJsonBundleFromShoppingItemFormBean(shoppingItemFormBean);
		String jsonString = jsonBundlefactory.serialize(jsonBundle);
		Assert.assertEquals(jsonValues.getProperty("json1"), jsonString);

		// to verify consistent with expectation from JsonBundleTest.
		// because the original serialized string has less fields, we can't compare as string directly.
		// We need to deserialize the original string expectation and jsonString, compare the deserialized java obejcts.
		String expectation = "{'selectionRule':1,'isCalculatedBundle':false,'constituents':[]}";
		verify(jsonBundlefactory.deserialize(expectation, JsonBundleItemBeanImpl.class),
				jsonBundlefactory.deserialize(jsonString, JsonBundleItemBeanImpl.class));
	}

	/**
	 * . *
	 */
	@Test
	public void testGettingConstituentPathReferencesForSingleConstituentBundle() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));

		ShoppingItemFormBean rootFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle), 1, "", 0, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean = createShoppingItemFormBean(
				new StoreProductImpl(product1), 1, CONSTITUENT_0, 1, true, false, false);

		rootFormBean.addConstituent(shoppingItemFormBean);

		// test
		JsonBundleItemBean jsonBundle = jsonBundlefactory.createJsonBundleFromShoppingItemFormBean(rootFormBean);
		String jsonString = jsonBundlefactory.serialize(jsonBundle);
		Assert.assertEquals(jsonValues.get("json2"), jsonString);

		String expectation = "{'selectionRule':1,'isCalculatedBundle':false,'constituents':["// NOPMD
				+ "{'path':'constituent[0]','selected':true,'constituents':[]}]}";
		verify(jsonBundlefactory.deserialize(expectation, JsonBundleItemBeanImpl.class),
				jsonBundlefactory.deserialize(jsonString, JsonBundleItemBeanImpl.class));

	}


	/**
	 * Test that the constituent price is included for a calculated bundle.
	 *
	 * *
	 */
	@Test
	public void testGettingConstituentPathReferencesForSingleConstituentCalculatedBundleNullPrice() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));

		bundle.setCalculated(true);

		ShoppingItemFormBean rootFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle), 1, "", 0, true, true, false);

		ShoppingItemFormBean shoppingItemFormBean = createShoppingItemFormBean(
				new StoreProductImpl(product1), 1, CONSTITUENT_0, 1, true, false, true);

		Price price = null;
		shoppingItemFormBean.setPrice(price);

		rootFormBean.addConstituent(shoppingItemFormBean);

		// test
		JsonBundleItemBean jsonBundle = jsonBundlefactory.createJsonBundleFromShoppingItemFormBean(rootFormBean);
		String jsonString = jsonBundlefactory.serialize(jsonBundle);
		Assert.assertEquals(jsonValues.get("json3"), jsonString);

		String expectation = "{'selectionRule':1,'calculatedBundle':true,'constituents':["
				+ "{\"calculatedBundleItem\":true,'path':'constituent[0]','selected':true,'quantity':0,'constituents':[]}]}";
		verify(jsonBundlefactory.deserialize(expectation, JsonBundleItemBeanImpl.class),
				jsonBundlefactory.deserialize(jsonString, JsonBundleItemBeanImpl.class));
	}


	/**
	 * Test that the constituent price is included for a calculated bundle.
	 *
	 * *
	 */
	@Test
	public void testGettingConstituentPathReferencesForSingleConstituentCalculatedBundleZeroPrice() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));

		bundle.setCalculated(true);

		ShoppingItemFormBean rootFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle), 1, "", 0, true, true, false);

		ShoppingItemFormBean shoppingItemFormBean = createShoppingItemFormBean(
				new StoreProductImpl(product1), 1, CONSTITUENT_0, 1, true, false, true);

		Price itemPrice = new PriceImpl();

		PriceTierImpl priceTier = new PriceTierImpl();
		priceTier.setSalePrice(BigDecimal.ZERO);
		priceTier.setListPrice(BigDecimal.ZERO);

		itemPrice.addOrUpdatePriceTier(priceTier);

		itemPrice.setCurrency(Currency.getInstance(USD));

		shoppingItemFormBean.setPrice(itemPrice);

		rootFormBean.addConstituent(shoppingItemFormBean);

		// test
		JsonBundleItemBean jsonBundle = jsonBundlefactory.createJsonBundleFromShoppingItemFormBean(rootFormBean);
		String jsonString = jsonBundlefactory.serialize(jsonBundle);

		Assert.assertEquals(jsonValues.get("json4"), jsonString);
		String expectation = "{'selectionRule':1,'calculatedBundle':true,'constituents':["
				+ "{\"calculatedBundleItem\":true,'path':'constituent[0]','selected':true,'price':0,'priceTier0':0,'quantity':0,'constituents':[]}]}";
		verify(jsonBundlefactory.deserialize(expectation, JsonBundleItemBeanImpl.class),
				jsonBundlefactory.deserialize(jsonString, JsonBundleItemBeanImpl.class));
	}


	/**
	 * Test that the constituent price is included for a calculated bundle.
	 *
	 * *
	 */
	@Test
	public void testGettingConstituentPathReferencesForSingleConstituentCalculatedBundleNonZeroPrice() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));

		bundle.setCalculated(true);

		ShoppingItemFormBean rootFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle), 1, "", 0, true, true, false);

		ShoppingItemFormBean shoppingItemFormBean = createShoppingItemFormBean(
				new StoreProductImpl(product1), 1, CONSTITUENT_0, 1, true, false, true);

		Price itemPrice = new PriceImpl();

		PriceTierImpl priceTier = new PriceTierImpl();
		priceTier.setSalePrice(new BigDecimal("23.55"));
		priceTier.setListPrice(new BigDecimal("23.55"));

		itemPrice.addOrUpdatePriceTier(priceTier);

		itemPrice.setCurrency(Currency.getInstance(USD));

		shoppingItemFormBean.setPrice(itemPrice);

		rootFormBean.addConstituent(shoppingItemFormBean);

		// test
		JsonBundleItemBean jsonBundle = jsonBundlefactory.createJsonBundleFromShoppingItemFormBean(rootFormBean);
		String jsonString = jsonBundlefactory.serialize(jsonBundle);

		Assert.assertEquals(jsonValues.get("json5"), jsonString);

		String expectation = "{'selectionRule':1,'calculatedBundle':true,'constituents':["
				+ "{\"calculatedBundleItem\":true,'path':'constituent[0]','selected':true,'price':23.55,"
				+ "'priceTier0':23.55,'quantity':0,'constituents':[]}]}";
		verify(jsonBundlefactory.deserialize(expectation, JsonBundleItemBeanImpl.class),
				jsonBundlefactory.deserialize(jsonString, JsonBundleItemBeanImpl.class));
	}


	/**
	 * . *
	 */
	@Test
	public void testGettingConstituentPathReferencesForMultiConstituentBundle() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		Product product2 = createProductWithSkuCode(B_PRODUCT, B_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));
		bundle.addConstituent(createBundleConstituent(product2));

		ShoppingItemFormBean rootFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle), 1, "", 0, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean = createShoppingItemFormBean(
				new StoreProductImpl(product1), 1, CONSTITUENT_0, 1, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean2 = createShoppingItemFormBean(
				new StoreProductImpl(product2), 1, CONSTITUENT_1, 1, false, false, false);

		rootFormBean.addConstituent(shoppingItemFormBean);
		rootFormBean.addConstituent(shoppingItemFormBean2);

		// test
		JsonBundleItemBean jsonBundle = jsonBundlefactory.createJsonBundleFromShoppingItemFormBean(rootFormBean);
		String jsonString = jsonBundlefactory.serialize(jsonBundle);
		Assert.assertEquals(jsonValues.get("json6"), jsonString);

		String expectation = "{'selectionRule':1,'calculatedBundle':false,'constituents':"
				+ "[{'path':'constituent[0]','selected':true,'constituents':[]},"
				+ "{'path':'constituent[1]','selected':false,'constituents':[]}]}";
		verify(jsonBundlefactory.deserialize(expectation, JsonBundleItemBeanImpl.class),
				jsonBundlefactory.deserialize(jsonString, JsonBundleItemBeanImpl.class));
	}

	/**
	 * . *
	 */
	@Test
	public void testGettingConstituentPathReferencesForNestedConstituentBundle() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		Product product2 = createProductWithSkuCode(B_PRODUCT, B_SKU_CODE);
		ProductBundle bundle2 = createBundleWithSkuCode(B_BUNDLE, BUN_B_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));
		bundle.addConstituent(createBundleConstituent(bundle2));
		bundle2.addConstituent(createBundleConstituent(product2));

		ShoppingItemFormBean rootFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle), 1, "", 0, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean = createShoppingItemFormBean(
				new StoreProductImpl(product1), 1, CONSTITUENT_0, 1, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean2 = createShoppingItemFormBean(
				new StoreProductImpl(bundle2), 1, CONSTITUENT_1, 1, false, false, false);

		ShoppingItemFormBean shoppingItemFormBean3 = createShoppingItemFormBean(
				new StoreProductImpl(product2), 1, CONSTITUENT_1_CONSTITUENT_0, 2, true, false, false);

		rootFormBean.addConstituent(shoppingItemFormBean);
		rootFormBean.addConstituent(shoppingItemFormBean2);
		rootFormBean.addConstituent(shoppingItemFormBean3);

		// test
		JsonBundleItemBean jsonBundle = jsonBundlefactory.createJsonBundleFromShoppingItemFormBean(rootFormBean);
		String jsonString = jsonBundlefactory.serialize(jsonBundle);
		Assert.assertEquals(jsonValues.get("json7"), jsonString);

		String expectation = "{'selectionRule':1,'calculatedBundle':false,'constituents':"
				+ "[{'path':'constituent[0]','selected':true,'constituents':[]},"
				+ "{'path':'constituent[1]','selected':false,'selectionRule':0,'constituents':"
				+ "[{'path':'constituent[1].constituent[0]','selected':true,'constituents':[]}]}]}";
		verify(jsonBundlefactory.deserialize(expectation, JsonBundleItemBeanImpl.class),
				jsonBundlefactory.deserialize(jsonString, JsonBundleItemBeanImpl.class));
	}

	/**
	 * . *
	 */
	@Test
	public void testGettingConstituentPathReferencesForManyNestedConstituentBundle() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		Product product3 = createProductWithSkuCode("C_PRODUCT", "C_SKU_CODE");
		Product product4 = createProductWithSkuCode("D_PRODUCT", "D_SKU_CODE");
		Product product5 = createProductWithSkuCode("E_PRODUCT", "E_SKU_CODE");

		ProductBundle bundle2 = createBundleWithSkuCode(B_BUNDLE, BUN_B_SKU_CODE);
		bundle2.setSelectionRule(new SelectionRuleImpl(0));
		ProductBundle bundle3 = createBundleWithSkuCode("C_BUNDLE", "BUN_C_SKU_CODE");
		bundle3.setSelectionRule(new SelectionRuleImpl(1));

		bundle.addConstituent(createBundleConstituent(product1));
		bundle.addConstituent(createBundleConstituent(bundle2));
		bundle.addConstituent(createBundleConstituent(product5));
		bundle2.addConstituent(createBundleConstituent(bundle3));
		bundle2.addConstituent(createBundleConstituent(product5));
		bundle3.addConstituent(createBundleConstituent(product3));
		bundle3.addConstituent(createBundleConstituent(product4));

		ShoppingItemFormBean rootFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle), 1, "", 0, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean = createShoppingItemFormBean(
				new StoreProductImpl(product1), 1, CONSTITUENT_0, 1, true, false, false);
		shoppingItemFormBean.setSelected(true);

		ShoppingItemFormBean shoppingItemFormBean2 = createShoppingItemFormBean(
				new StoreProductImpl(bundle2), 1, CONSTITUENT_1, 1, false, false, false);

		ShoppingItemFormBean shoppingItemFormBean3 = createShoppingItemFormBean(
				new StoreProductImpl(bundle3), 1, CONSTITUENT_1_CONSTITUENT_0, 2, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean4 = createShoppingItemFormBean(
				new StoreProductImpl(product3), 1, "constituent[1].constituent[0].constituent[0]", LEVEL_3, false, false, false);

		ShoppingItemFormBean shoppingItemFormBean5 = createShoppingItemFormBean(
				new StoreProductImpl(product4), 1, "constituent[1].constituent[0].constituent[1]", LEVEL_3, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean6 = createShoppingItemFormBean(
				new StoreProductImpl(product4), 1, "constituent[1].constituent[1]", 2, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean7 = createShoppingItemFormBean(
				new StoreProductImpl(product5), 1, "constituent[2]", 1, false, false, false);

		rootFormBean.addConstituent(shoppingItemFormBean);
		rootFormBean.addConstituent(shoppingItemFormBean2);
		rootFormBean.addConstituent(shoppingItemFormBean3);
		rootFormBean.addConstituent(shoppingItemFormBean4);
		rootFormBean.addConstituent(shoppingItemFormBean5);
		rootFormBean.addConstituent(shoppingItemFormBean6);
		rootFormBean.addConstituent(shoppingItemFormBean7);

		// test
		JsonBundleItemBean jsonBundle = jsonBundlefactory.createJsonBundleFromShoppingItemFormBean(rootFormBean);
		String jsonString = jsonBundlefactory.serialize(jsonBundle);
		Assert.assertEquals(jsonValues.get("json8"), jsonString);

		String expectation = "{\"selectionRule\": 1,\"calculatedBundle\":false,"
			+ "\"constituents\": [{\"path\": \"constituent[0]\",\"selected\": true,\"constituents\": []},"
			+ "{\"path\": \"constituent[1]\",\"selected\": false,\"selectionRule\": 0,\"constituents\": [{\"path\": \"constituent[1].constituent[0]\""
			+ ",\"selected\": true,\"selectionRule\": 1,\"constituents\": ["
			+ "{\"path\": \"constituent[1].constituent[0].constituent[0]\",\"selected\": false,\"constituents\": []},"
			+ "{\"path\": \"constituent[1].constituent[0].constituent[1]\",\"selected\": true,\"constituents\": []}]},"
			+ "{\"path\": \"constituent[1].constituent[1]\",\"selected\": true,\"constituents\": []}]},"
			+ "{\"path\": \"constituent[2]\",\"selected\": false,\"constituents\": []}]}";

		JsonBundleItemBean expected = jsonBundlefactory.deserialize(expectation, JsonBundleItemBeanImpl.class);
		verify(expected, jsonBundlefactory.deserialize(jsonString, JsonBundleItemBeanImpl.class));
	}


	/**
	 * . *
	 */
	@Test
	public void testGettingConstituentPathReferencesForManyNestedConstituentBundleDrop2() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		Product product3 = createProductWithSkuCode("C_PRODUCT", "C_SKU_CODE");
		Product product4 = createProductWithSkuCode("D_PRODUCT", "D_SKU_CODE");
		Product product5 = createProductWithSkuCode("E_PRODUCT", "E_SKU_CODE");

		ProductBundle bundle2 = createBundleWithSkuCode(B_BUNDLE, BUN_B_SKU_CODE);
		bundle2.setSelectionRule(new SelectionRuleImpl(0));
		ProductBundle bundle3 = createBundleWithSkuCode("C_BUNDLE", "BUN_C_SKU_CODE");
		bundle3.setSelectionRule(new SelectionRuleImpl(1));

		bundle.addConstituent(createBundleConstituent(product1));
		bundle.addConstituent(createBundleConstituent(bundle2));
		bundle.addConstituent(createBundleConstituent(product5));
		bundle2.addConstituent(createBundleConstituent(bundle3));
		bundle2.addConstituent(createBundleConstituent(product5));
		bundle3.addConstituent(createBundleConstituent(product3));

		ShoppingItemFormBean rootFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle), 1, "", 0, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean = createShoppingItemFormBean(
				new StoreProductImpl(product1), 1, CONSTITUENT_0, 1, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean2 = createShoppingItemFormBean(
				new StoreProductImpl(bundle2), 1, CONSTITUENT_1, 1, false, false, false);

		ShoppingItemFormBean shoppingItemFormBean3 = createShoppingItemFormBean(
				new StoreProductImpl(bundle3), 1, CONSTITUENT_1_CONSTITUENT_0, 2, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean4 = createShoppingItemFormBean(
				new StoreProductImpl(product3), 1, "constituent[1].constituent[0].constituent[0]", LEVEL_3, false, false, false);
		shoppingItemFormBean4.setPriceAdjustment(new PriceAdjustmentImpl() {
			private static final long serialVersionUID = -912791117691243582L;

			@Override
			public BigDecimal getAdjustmentAmount() {
				return new BigDecimal("1.234");
			}
		});

		ShoppingItemFormBean shoppingItemFormBean5 = createShoppingItemFormBean(
				new StoreProductImpl(product4), 1, "constituent[1].constituent[0].constituent[1]", LEVEL_3, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean7 = createShoppingItemFormBean(
				new StoreProductImpl(product5), 1, "constituent[2]", 1, false, false, false);

		rootFormBean.addConstituent(shoppingItemFormBean);
		rootFormBean.addConstituent(shoppingItemFormBean2);
		rootFormBean.addConstituent(shoppingItemFormBean3);
		rootFormBean.addConstituent(shoppingItemFormBean4);
		rootFormBean.addConstituent(shoppingItemFormBean5);
		rootFormBean.addConstituent(shoppingItemFormBean7);

		// test
		JsonBundleItemBean jsonBundle = jsonBundlefactory.createJsonBundleFromShoppingItemFormBean(rootFormBean);
		String jsonString = jsonBundlefactory.serialize(jsonBundle);

		Assert.assertEquals(jsonValues.get("json9"), jsonString);

		String expectation = "{\"selectionRule\": 1,\"calculatedBundle\":false,"
				+ "\"constituents\": [{\"path\": \"constituent[0]\",\"selected\": true,\"constituents\": []},"
				+ "{\"path\": \"constituent[1]\",\"selected\": false,\"selectionRule\": 0,\"constituents\": ["
				+ "{\"path\": \"constituent[1].constituent[0]\",\"selected\": true,\"selectionRule\": 1,\"constituents\": ["
				+ "{\"path\": \"constituent[1].constituent[0].constituent[0]\",\"selected\": false,\"priceAdjustment\":1.234,\"constituents\": []},"
				+ "{\"path\": \"constituent[1].constituent[0].constituent[1]\",\"selected\": true,\"constituents\": []}]}]},"
				+ "{\"path\": \"constituent[2]\",\"selected\": false,\"constituents\": []}]}";

		JsonBundleItemBean expected = jsonBundlefactory.deserialize(expectation, JsonBundleItemBeanImpl.class);
		JsonBundleItemBean actual = jsonBundlefactory.deserialize(jsonString, JsonBundleItemBeanImpl.class);
		verify(expected, actual);

	}

	/**
	 * .
	 */
	@Test
	public void testJsoningBundleWithTwoNestedBundle() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		Product product3 = createProductWithSkuCode("C_PRODUCT", "C_SKU_CODE");

		ProductBundle bundle2 = createBundleWithSkuCode(B_BUNDLE, BUN_B_SKU_CODE);
		bundle2.setSelectionRule(new SelectionRuleImpl(0));
		ProductBundle bundle3 = createBundleWithSkuCode("C_BUNDLE", "BUN_C_SKU_CODE");
		bundle3.setSelectionRule(new SelectionRuleImpl(1));

		bundle.addConstituent(createBundleConstituent(bundle2));
		bundle.addConstituent(createBundleConstituent(bundle3));
		bundle2.addConstituent(createBundleConstituent(product1));
		bundle3.addConstituent(createBundleConstituent(product3));

		ShoppingItemFormBean rootFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle), 1, "", 0, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean = createShoppingItemFormBean(
				new StoreProductImpl(bundle2), 1, CONSTITUENT_0, 1, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean3 = createShoppingItemFormBean(
				new StoreProductImpl(bundle3), 1, "constituent[0].constituent[0]", 2,
				true, false, false);

		ShoppingItemFormBean shoppingItemFormBean2 = createShoppingItemFormBean(
				new StoreProductImpl(product1), 1, CONSTITUENT_1, 1, true, false, false);

		ShoppingItemFormBean shoppingItemFormBean4 = createShoppingItemFormBean(
				new StoreProductImpl(product3), 1, CONSTITUENT_1_CONSTITUENT_0, 2, true, false, false);

		rootFormBean.addConstituent(shoppingItemFormBean);
		rootFormBean.addConstituent(shoppingItemFormBean3);
		rootFormBean.addConstituent(shoppingItemFormBean2);
		rootFormBean.addConstituent(shoppingItemFormBean4);

		// test
		JsonBundleItemBean jsonBundle = jsonBundlefactory.createJsonBundleFromShoppingItemFormBean(rootFormBean);
		String jsonString = jsonBundlefactory.serialize(jsonBundle);

		Assert.assertEquals(jsonValues.get("json10"), jsonString);

		String expectation = "{\"selectionRule\": 1,\"calculatedBundle\":false,\"constituents\": [{\"path\": \"constituent[0]\",\"selected\": true,"
				+ "\"selectionRule\":0,\"constituents\": [{\"path\": \"constituent[0].constituent[0]\",\"selected\": true,\"selectionRule\": 1,"
				+ "\"constituents\": []}]},{\"path\": \"constituent[1]\",\"selected\": true,\"selectionRule\":0,\"constituents\": [{\"path\": "
				+ "\"constituent[1].constituent[0]\",\"selected\": true,\"constituents\": []}]}]}";

		JsonBundleItemBean expected = jsonBundlefactory.deserialize(expectation, JsonBundleItemBeanImpl.class);
		verify(expected, jsonBundlefactory.deserialize(jsonString, JsonBundleItemBeanImpl.class));
	}

	private BundleConstituent createBundleConstituent(final Product product) {
		BundleConstituent bundleConstituent = new BundleConstituentImpl();
		bundleConstituent.setConstituent(product);
		bundleConstituent.setQuantity(1);
		return bundleConstituent;
	}

	private Product createProductWithSkuCode(final String productCode, final String skuCode) {
		ProductImpl product = new ProductImpl();
		product.setCode(productCode);

		ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode(skuCode);
		product.addOrUpdateSku(sku);

		return product;
	}

	private ProductBundle createBundleWithSkuCode(final String productCode, final String skuCode) {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setCode(productCode);

		ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode(skuCode);
		bundle.addOrUpdateSku(sku);

		return bundle;
	}

	/**
	 * @param storeProduct product for this item.
	 * @param quantity the quantity.
	 * @param path the bundle path.
	 * @param level tree level.
	 * @param selected whether this item is selected.
	 * @param calculatedBundle whether this item itself is a calculated bundle.
	 * @param calculatedBundleItem whether this item is an item of a calculated bundle.
	 * @return a ShoppingItemFormBean object.
	 */
	private ShoppingItemFormBean createShoppingItemFormBean(
			final StoreProduct storeProduct, final int quantity,
			final String path, final int level,
			final boolean selected,
			final boolean calculatedBundle,
			final boolean calculatedBundleItem) {
		ShoppingItemFormBean formBean = new ShoppingItemFormBeanImpl();
		formBean.setProduct(storeProduct);
		formBean.setSkuCode(storeProduct.getWrappedProduct().getDefaultSku().getSkuCode());
		formBean.setQuantity(quantity);
		formBean.setPath(path);
		formBean.setLevel(level);
		Product product = storeProduct.getWrappedProduct();
		if (product instanceof ProductBundle) {
			SelectionRule selRule = ((ProductBundle) product).getSelectionRule();
			if (selRule != null) {
				formBean.setSelectionRule(selRule.getParameter());
			}
		}
		formBean.setSelected(selected);
		formBean.setCalculatedBundle(calculatedBundle);
		formBean.setCalculatedBundleItem(calculatedBundleItem);
		return formBean;
	}

	/**
	 * @param expected expected json bundle, which is deserialzed from expected json text.
	 * @param jsonBundle json bundle object, deserialzed from seriazed string from our original json bundle object.
	 */
	private void verify(final JsonBundleItemBean expected,
			final JsonBundleItemBean jsonBundle) {
		PropertyDescriptor[] propertyDescriptors1 = PropertyUtils.getPropertyDescriptors(expected);
		for (int i = 0; i < propertyDescriptors1.length; i++) {
			PropertyDescriptor propertyDescriptor1 = propertyDescriptors1[i];
			Method read1 = propertyDescriptor1.getReadMethod();
			Object value1;
			Object value2;
			try {
				value1 = read1.invoke(expected);
				value2 = read1.invoke(jsonBundle);
				if ((value1 instanceof String) && (value2 instanceof String)) {
					String strVal1 = (String) value1;
					String strVal2 = (String) value2;
					if (strVal1.length() > 0) {
						Assert.assertEquals(strVal1, strVal2);
					}
				} else if ((value1 instanceof List<?>) && (value2 instanceof List<?>)) {
					@SuppressWarnings("unchecked")
					List<JsonBundleItemBean> listVal1 = (List<JsonBundleItemBean>) value1;

					@SuppressWarnings("unchecked")
					List<JsonBundleItemBean> listVal2 = (List<JsonBundleItemBean>) value2;

					for (int j = 0; j < listVal1.size(); j++) {
						verify(listVal1.get(j), listVal2.get(j));
					}
				} else if (read1.getName().equals("getQuantity")) {
					if (((Integer) value1).intValue() > 0) {
						Assert.assertEquals(read1.getName() + " not equal: ", value1, value2);
					}
				} else {
					Assert.assertEquals(read1.getName() + " not equal: ", value1, value2);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

		}

	}
}
