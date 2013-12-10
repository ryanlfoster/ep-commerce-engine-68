package com.elasticpath.sfweb.controller.impl;

import java.math.BigDecimal;
import java.util.Currency;

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
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.pricing.impl.PriceAdjustmentImpl;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.impl.ShoppingItemFormBeanImpl;
import com.elasticpath.sfweb.test.BeanFactoryExpectationsFactory;

/**
 *
 * Suite of tests for the json bundle class.
 *
 * @author shallinan
 *
 */
public class JsonBundleTest {

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

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Setup required before each test.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("moneyFormatter", StandardMoneyFormatter.class);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/** . **/
	@Test
	public void testGettingRootLevelSelectionRule() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		ShoppingItemFormBean shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setProduct(new StoreProductImpl(bundle));

		// test
		JsonBundle jsonizer = new JsonBundle();
		jsonizer.setBundle(bundle);
		jsonizer.setFormBean(shoppingItemFormBean);

		Assert.assertEquals("{'selectionRule':1,'isCalculatedBundle':false,'constituents':[]}", jsonizer.toJsonString());
	}

	/** . **/
	@Test
	public void testGettingConstituentPathReferencesForSingleConstituentBundle() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));

		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		rootFormBean.setProduct(new StoreProductImpl(bundle));

		ShoppingItemFormBean shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setSelected(true);
		shoppingItemFormBean.setProduct(new StoreProductImpl(product1));
		shoppingItemFormBean.setPath(CONSTITUENT_0);
		rootFormBean.addConstituent(shoppingItemFormBean);

		// test
		JsonBundle jsonizer = new JsonBundle();
		jsonizer.setBundle(bundle);
		jsonizer.setFormBean(rootFormBean);
		Assert.assertEquals(("{'selectionRule':1,'isCalculatedBundle':false,'constituents':["// NOPMD
				+ "{'path':'constituent[0]','selected':true,'constituents':[]}]}"), jsonizer.toJsonString());
	}


	/**
	 * Test that the constituent price is included for a calculated bundle.
	 *
	 *  **/
	@Test
	public void testGettingConstituentPathReferencesForSingleConstituentCalculatedBundleNullPrice() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));

		bundle.setCalculated(true);

		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		rootFormBean.setProduct(new StoreProductImpl(bundle));

		ShoppingItemFormBean shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setSelected(true);
		shoppingItemFormBean.setProduct(new StoreProductImpl(product1));
		shoppingItemFormBean.setPath(CONSTITUENT_0);
		shoppingItemFormBean.setCalculatedBundleItem(true);

		Price price = null;
		shoppingItemFormBean.setPrice(price);

		rootFormBean.addConstituent(shoppingItemFormBean);

		// test
		JsonBundle jsonizer = new JsonBundle();
		jsonizer.setBundle(bundle);
		jsonizer.setFormBean(rootFormBean);
		Assert.assertEquals(("{'selectionRule':1,'isCalculatedBundle':true,'constituents':["
				+ "{'path':'constituent[0]','selected':true,'quantity':0,'constituents':[]}]}"), jsonizer.toJsonString());
	}


	/**
	 * Test that the constituent price is included for a calculated bundle.
	 *
	 *  **/
	@Test
	public void testGettingConstituentPathReferencesForSingleConstituentCalculatedBundleZeroPrice() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));

		bundle.setCalculated(true);

		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		rootFormBean.setProduct(new StoreProductImpl(bundle));

		ShoppingItemFormBean shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setSelected(true);
		shoppingItemFormBean.setProduct(new StoreProductImpl(product1));
		shoppingItemFormBean.setPath(CONSTITUENT_0);
		shoppingItemFormBean.setCalculatedBundleItem(true);

		Price itemPrice = new PriceImpl();

		PriceTierImpl priceTier = new PriceTierImpl();
		priceTier.setSalePrice(BigDecimal.ZERO);
		priceTier.setListPrice(BigDecimal.ZERO);

		itemPrice.addOrUpdatePriceTier(priceTier);

		itemPrice.setCurrency(Currency.getInstance(USD));

		shoppingItemFormBean.setPrice(itemPrice);

		rootFormBean.addConstituent(shoppingItemFormBean);

		// test
		JsonBundle jsonizer = new JsonBundle();
		jsonizer.setBundle(bundle);
		jsonizer.setFormBean(rootFormBean);

		String jsonString = jsonizer.toJsonString();

		Assert.assertEquals(("{'selectionRule':1,'isCalculatedBundle':true,'constituents':["
				+ "{'path':'constituent[0]','selected':true,'price':0.00,'priceTier0':0,'quantity':0,'constituents':[]}]}"), jsonString);
	}


	/**
	 * Test that the constituent price is included for a calculated bundle.
	 *
	 *  **/
	@Test
	public void testGettingConstituentPathReferencesForSingleConstituentCalculatedBundleNonZeroPrice() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));

		bundle.setCalculated(true);

		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		rootFormBean.setProduct(new StoreProductImpl(bundle));

		ShoppingItemFormBean shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setSelected(true);
		shoppingItemFormBean.setProduct(new StoreProductImpl(product1));
		shoppingItemFormBean.setPath(CONSTITUENT_0);
		shoppingItemFormBean.setCalculatedBundleItem(true);

		Price itemPrice = new PriceImpl();

		PriceTierImpl priceTier = new PriceTierImpl();
		priceTier.setSalePrice(new BigDecimal("23.55"));
		priceTier.setListPrice(new BigDecimal("23.55"));

		itemPrice.addOrUpdatePriceTier(priceTier);

		itemPrice.setCurrency(Currency.getInstance(USD));

		shoppingItemFormBean.setPrice(itemPrice);

		rootFormBean.addConstituent(shoppingItemFormBean);

		// test
		JsonBundle jsonizer = new JsonBundle();
		jsonizer.setBundle(bundle);
		jsonizer.setFormBean(rootFormBean);

		String jsonString = jsonizer.toJsonString();

		Assert.assertEquals(("{'selectionRule':1,'isCalculatedBundle':true,'constituents':["
				+ "{'path':'constituent[0]','selected':true,'price':23.55,'priceTier0':23.55,'quantity':0,'constituents':[]}]}"), jsonString);
	}


	/** . **/
	@Test
	public void testGettingConstituentPathReferencesForMultiConstituentBundle() {
		// given
		ProductBundle bundle = createBundleWithSkuCode(A_BUNDLE, BUN_A_SKU_CODE);
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		Product product1 = createProductWithSkuCode(A_PRODUCT, A_SKU_CODE);
		Product product2 = createProductWithSkuCode(B_PRODUCT, B_SKU_CODE);
		bundle.addConstituent(createBundleConstituent(product1));
		bundle.addConstituent(createBundleConstituent(product2));

		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		rootFormBean.setProduct(new StoreProductImpl(bundle));

		ShoppingItemFormBean shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setSelected(true);
		shoppingItemFormBean.setProduct(new StoreProductImpl(product1));
		shoppingItemFormBean.setPath(CONSTITUENT_0);
		ShoppingItemFormBean shoppingItemFormBean2 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean2.setSelected(false);
		shoppingItemFormBean2.setProduct(new StoreProductImpl(product2));
		shoppingItemFormBean2.setPath(CONSTITUENT_1);
		rootFormBean.addConstituent(shoppingItemFormBean);
		rootFormBean.addConstituent(shoppingItemFormBean2);

		// test
		JsonBundle jsonizer = new JsonBundle();
		jsonizer.setBundle(bundle);
		jsonizer.setFormBean(rootFormBean);
		Assert.assertEquals("{'selectionRule':1,'isCalculatedBundle':false,'constituents':"
				+ "[{'path':'constituent[0]','selected':true,'constituents':[]},"
				+ "{'path':'constituent[1]','selected':false,'constituents':[]}]}", jsonizer.toJsonString());
	}

	/** . **/
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

		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		rootFormBean.setProduct(new StoreProductImpl(bundle));

		ShoppingItemFormBean shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setSelected(true);
		shoppingItemFormBean.setProduct(new StoreProductImpl(product1));
		shoppingItemFormBean.setPath(CONSTITUENT_0);
		ShoppingItemFormBean shoppingItemFormBean2 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean2.setSelected(false);
		shoppingItemFormBean2.setProduct(new StoreProductImpl(bundle2));
		shoppingItemFormBean2.setPath(CONSTITUENT_1);

		ShoppingItemFormBean shoppingItemFormBean3 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean3.setSelected(true);
		shoppingItemFormBean3.setProduct(new StoreProductImpl(product2));
		shoppingItemFormBean3.setPath(CONSTITUENT_1_CONSTITUENT_0);

		rootFormBean.addConstituent(shoppingItemFormBean);
		rootFormBean.addConstituent(shoppingItemFormBean2);
		rootFormBean.addConstituent(shoppingItemFormBean3);

		// test
		JsonBundle jsonizer = new JsonBundle();
		jsonizer.setBundle(bundle);
		jsonizer.setFormBean(rootFormBean);
		Assert.assertEquals("{'selectionRule':1,'isCalculatedBundle':false,'constituents':"
				+ "[{'path':'constituent[0]','selected':true,'constituents':[]},"
				+ "{'path':'constituent[1]','selected':false,'selectionRule':0,'constituents':"
				+ "[{'path':'constituent[1].constituent[0]','selected':true,'constituents':[]}]}]}", jsonizer.toJsonString());
	}

	/** . **/
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

		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		rootFormBean.setProduct(new StoreProductImpl(bundle));

		ShoppingItemFormBean shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setSelected(true);
		shoppingItemFormBean.setProduct(new StoreProductImpl(product1));
		shoppingItemFormBean.setPath(CONSTITUENT_0);

		ShoppingItemFormBean shoppingItemFormBean2 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean2.setSelected(false);
		shoppingItemFormBean2.setProduct(new StoreProductImpl(bundle2));
		shoppingItemFormBean2.setPath(CONSTITUENT_1);

		ShoppingItemFormBean shoppingItemFormBean3 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean3.setSelected(true);
		shoppingItemFormBean3.setProduct(new StoreProductImpl(bundle3));
		shoppingItemFormBean3.setPath(CONSTITUENT_1_CONSTITUENT_0);

		ShoppingItemFormBean shoppingItemFormBean4 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean4.setSelected(false);
		shoppingItemFormBean4.setProduct(new StoreProductImpl(product3));
		shoppingItemFormBean4.setPath("constituent[1].constituent[0].constituent[0]");

		ShoppingItemFormBean shoppingItemFormBean5 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean5.setSelected(true);
		shoppingItemFormBean5.setProduct(new StoreProductImpl(product4));
		shoppingItemFormBean5.setPath("constituent[1].constituent[0].constituent[1]");

		ShoppingItemFormBean shoppingItemFormBean6 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean6.setSelected(true);
		shoppingItemFormBean6.setProduct(new StoreProductImpl(product4));
		shoppingItemFormBean6.setPath("constituent[1].constituent[1]");

		ShoppingItemFormBean shoppingItemFormBean7 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean7.setSelected(false);
		shoppingItemFormBean7.setProduct(new StoreProductImpl(product5));
		shoppingItemFormBean7.setPath("constituent[2]");

		rootFormBean.addConstituent(shoppingItemFormBean);
		rootFormBean.addConstituent(shoppingItemFormBean2);
		rootFormBean.addConstituent(shoppingItemFormBean3);
		rootFormBean.addConstituent(shoppingItemFormBean4);
		rootFormBean.addConstituent(shoppingItemFormBean5);
		rootFormBean.addConstituent(shoppingItemFormBean6);
		rootFormBean.addConstituent(shoppingItemFormBean7);

		// test
		JsonBundle jsonizer = new JsonBundle();
		jsonizer.setBundle(bundle);
		jsonizer.setFormBean(rootFormBean);
		String expectation = "{'selectionRule': 1,'isCalculatedBundle':false,"
				+ "'constituents': [{'path': 'constituent[0]','selected': true,'constituents': []},"
				+ "{'path': 'constituent[1]','selected': false,'selectionRule': 0,'constituents': [{'path': 'constituent[1].constituent[0]'"
				+ ",'selected': true,'selectionRule': 1,'constituents': ["
				+ "{'path': 'constituent[1].constituent[0].constituent[0]','selected': false,'constituents': []},"
				+ "{'path': 'constituent[1].constituent[0].constituent[1]','selected': true,'constituents': []}]},"
				+ "{'path': 'constituent[1].constituent[1]','selected': true,'constituents': []}]},"
				+ "{'path': 'constituent[2]','selected': false,'constituents': []}]}";
		expectation = expectation.replace(" ", "");
		Assert.assertEquals(expectation, jsonizer.toJsonString().replace(" ", ""));
	}

	/** . **/
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

		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		rootFormBean.setProduct(new StoreProductImpl(bundle));

		ShoppingItemFormBean shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setSelected(true);
		shoppingItemFormBean.setProduct(new StoreProductImpl(product1));
		shoppingItemFormBean.setPath(CONSTITUENT_0);

		ShoppingItemFormBean shoppingItemFormBean2 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean2.setSelected(false);
		shoppingItemFormBean2.setProduct(new StoreProductImpl(bundle2));
		shoppingItemFormBean2.setPath(CONSTITUENT_1);

		ShoppingItemFormBean shoppingItemFormBean3 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean3.setSelected(true);
		shoppingItemFormBean3.setProduct(new StoreProductImpl(bundle3));
		shoppingItemFormBean3.setPath(CONSTITUENT_1_CONSTITUENT_0);

		ShoppingItemFormBean shoppingItemFormBean4 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean4.setSelected(false);
		shoppingItemFormBean4.setPriceAdjustment(new PriceAdjustmentImpl() {
			private static final long serialVersionUID = -8524157448368414699L;

			@Override
			public BigDecimal getAdjustmentAmount() {
				return new BigDecimal("1.234");
			}
		});
		shoppingItemFormBean4.setProduct(new StoreProductImpl(product3));
		shoppingItemFormBean4.setPath("constituent[1].constituent[0].constituent[0]");

		ShoppingItemFormBean shoppingItemFormBean5 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean5.setSelected(true);
		shoppingItemFormBean5.setProduct(new StoreProductImpl(product4));
		shoppingItemFormBean5.setPath("constituent[1].constituent[0].constituent[1]");

		ShoppingItemFormBean shoppingItemFormBean7 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean7.setSelected(false);
		shoppingItemFormBean7.setProduct(new StoreProductImpl(product5));
		shoppingItemFormBean7.setPath("constituent[2]");

		rootFormBean.addConstituent(shoppingItemFormBean);
		rootFormBean.addConstituent(shoppingItemFormBean2);
		rootFormBean.addConstituent(shoppingItemFormBean3);
		rootFormBean.addConstituent(shoppingItemFormBean4);
		rootFormBean.addConstituent(shoppingItemFormBean5);
		rootFormBean.addConstituent(shoppingItemFormBean7);

		// test
		JsonBundle jsonizer = new JsonBundle();
		jsonizer.setBundle(bundle);
		jsonizer.setFormBean(rootFormBean);
		String expectation = "{'selectionRule': 1,'isCalculatedBundle':false,"
				+ "'constituents': [{'path': 'constituent[0]','selected': true,'constituents': []},"
				+ "{'path': 'constituent[1]','selected': false,'selectionRule': 0,'constituents': ["
				+ "{'path': 'constituent[1].constituent[0]','selected': true,'selectionRule': 1,'constituents': ["
				+ "{'path': 'constituent[1].constituent[0].constituent[0]','selected': false,'adjustment':1.234,'constituents': []},"
				+ "{'path': 'constituent[1].constituent[0].constituent[1]','selected': true,'constituents': []}]}]},"
				+ "{'path': 'constituent[2]','selected': false,'constituents': []}]}";
		expectation = expectation.replace(" ", "");
		Assert.assertEquals(expectation, jsonizer.toJsonString().replace(" ", ""));
	}

	/** . */
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

		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		rootFormBean.setProduct(new StoreProductImpl(bundle));

		ShoppingItemFormBean shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setSelected(true);
		shoppingItemFormBean.setProduct(new StoreProductImpl(bundle2));
		shoppingItemFormBean.setPath(CONSTITUENT_0);

		ShoppingItemFormBean shoppingItemFormBean2 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean2.setSelected(true);
		shoppingItemFormBean2.setProduct(new StoreProductImpl(product1));
		shoppingItemFormBean2.setPath(CONSTITUENT_1);

		ShoppingItemFormBean shoppingItemFormBean3 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean3.setSelected(true);
		shoppingItemFormBean3.setProduct(new StoreProductImpl(bundle3));
		shoppingItemFormBean3.setPath("constituent[0].constituent[0]");

		ShoppingItemFormBean shoppingItemFormBean4 = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean4.setSelected(true);
		shoppingItemFormBean4.setProduct(new StoreProductImpl(product3));
		shoppingItemFormBean4.setPath(CONSTITUENT_1_CONSTITUENT_0);

		rootFormBean.addConstituent(shoppingItemFormBean);
		rootFormBean.addConstituent(shoppingItemFormBean3);
		rootFormBean.addConstituent(shoppingItemFormBean2);
		rootFormBean.addConstituent(shoppingItemFormBean4);

		// test
		JsonBundle jsonizer = new JsonBundle();
		jsonizer.setBundle(bundle);
		jsonizer.setFormBean(rootFormBean);
		String expectation = "{'selectionRule': 1,'isCalculatedBundle':false,'constituents': [{'path': 'constituent[0]','selected': true,"
				+ "'selectionRule':0,'constituents': [{'path': 'constituent[0].constituent[0]','selected': true,"
				+ "'constituents': []}]},{'path': 'constituent[1]','selected': true,'selectionRule':1,'constituents': [{'path': "
				+ "'constituent[1].constituent[0]','selected': true,'constituents': []}]}]}";
		expectation = expectation.replace(" ", "");
		Assert.assertEquals(expectation, jsonizer.toJsonString().replace(" ", ""));
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

}
