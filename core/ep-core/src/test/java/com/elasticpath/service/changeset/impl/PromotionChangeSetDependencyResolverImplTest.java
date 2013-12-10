package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.impl.CartSkuPercentDiscountActionImpl;
import com.elasticpath.domain.rules.impl.ProductInCartConditionImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.rules.RuleService;


/**
 *	The unit tests class for promotion change set resolver. 
 */
public class PromotionChangeSetDependencyResolverImplTest {

	private static final String PRODUCT_DEPENDENCY = "Only 1 product should be dependent";
	private static final String CATEGORY_UID = "CATEGORY_UID";
	private static final String BRAND_GUID = "BRAND_GUID";
	private static final String PRODUCT_GUID = "PRODUCT_GUID";
	private static final String SKU_GUID_WITH_MULTI_SKU_PARENT = "SKU_GUID_A";
	private static final String SKU_GUID_WITH_SINGLE_SKU_PARENT = "SKU_GUID_B";
	
	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final PromotionChangeSetDependencyResolverImpl resolver  = new PromotionChangeSetDependencyResolverImpl();
	private ProductService productService;
	private CategoryService categoryService;
	private ProductSkuService productSkuService;
	private RuleService ruleService;
	private Rule rule;
	private BrandService brandService;
	private final Set<RuleParameter> parameters = new LinkedHashSet<RuleParameter>();
	
	
	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		productService = context.mock(ProductService.class);
		categoryService = context.mock(CategoryService.class); 
		productSkuService = context.mock(ProductSkuService.class);
		ruleService = context.mock(RuleService.class);
		brandService = context.mock(BrandService.class);
		
		final RuleCondition condition = new ProductInCartConditionImpl();
		parameters.clear();
		condition.setParameters(parameters);
		rule = new PromotionRuleImpl() {
			private static final long serialVersionUID = -8755731174289770918L;

			public Set<RuleCondition> getConditions() {
				Set<RuleCondition> conditions = new LinkedHashSet<RuleCondition>();
				conditions.add(condition);
				return conditions;
			}
		};
		
		resolver.setCategoryService(categoryService);
		resolver.setProductService(productService);
		resolver.setProductSkuService(productSkuService);
		resolver.setRuleService(ruleService);
		resolver.setBrandService(brandService);
	}

	
	
	/**
	 * Test for multi sku product's product Sku.
	 */
	@Test
	public void testChangeSetDependencyForMultiProductWSku() {

		final ProductType productType = new ProductTypeImpl();
		productType.setWithMultipleSkus(true);

		final Product productWithMultiSku = new ProductImpl();
		productWithMultiSku.setProductType(productType);

		final ProductSku productSkuA = new ProductSkuImpl();
		productSkuA.setProduct(productWithMultiSku);

		context.checking(new Expectations() { {
			oneOf(productSkuService).findBySkuCode(SKU_GUID_WITH_MULTI_SKU_PARENT); will(returnValue(productSkuA));
		} });

		Set<RuleParameter> paramsForElem = createRuleElements();
		addToParam(paramsForElem, RuleParameter.SKU_CODE_KEY, SKU_GUID_WITH_MULTI_SKU_PARENT);
		//run the resolver
		Set< ? > dependencies = resolver.getChangeSetDependency(rule);
		//check results	
		// the dependent item is the productSku
		assertSame(PRODUCT_DEPENDENCY, 1, dependencies.size());
		Object  dependent = dependencies.iterator().next();
		assertEquals("object returned should be productSku", productSkuA, dependent);
	}
	
	/**
	 * Test for empty object.
	 */
	@Test
	public void testEmptyChangeSetDependency() {
		Object object = new Object();
		Set< ? >  dependencies = resolver.getChangeSetDependency(object);
		assertTrue("Non-Promotion object should not be processed", dependencies.isEmpty());
	}
	
	/**
	 * Test for brand dependency.
	 */
	@Test
	public void testChangeSetDependencyForBrand() {

	final Brand brand = context.mock(Brand.class);
		

		context.checking(new Expectations() { {
			oneOf(brandService).findByCode(BRAND_GUID); will(returnValue(brand));
		} });
		
		
		addToParam(parameters, RuleParameter.BRAND_CODE_KEY, BRAND_GUID);
		
		Set< ? >  dependencies = resolver.getChangeSetDependency(rule);
		assertSame(PRODUCT_DEPENDENCY, 1, dependencies.size());
		// the dependent item is the brand.
		Object	dependent = dependencies.iterator().next();
		assertEquals("object returned should be brand", brand, dependent);
	}
	/**
	 * Test productSku for changesetDependency.
	 * Uses RuleElements instead of conditions.
	 */
	@Test
	public void testChangeSetDependencyForProductSku() {

		final ProductType productType = new ProductTypeImpl();
		productType.setWithMultipleSkus(false);

		final Product productWithoutMultiSku = new ProductImpl();
		productWithoutMultiSku.setProductType(productType);

		final ProductSku productSkuB = new ProductSkuImpl();
		productSkuB.setProduct(productWithoutMultiSku);

		context.checking(new Expectations() { {
			oneOf(productSkuService).findBySkuCode(SKU_GUID_WITH_SINGLE_SKU_PARENT); will(returnValue(productSkuB));
		} });

		Set<RuleParameter> paramsForElem = createRuleElements();
		// check for single sku parent:
		addToParam(paramsForElem, RuleParameter.SKU_CODE_KEY, SKU_GUID_WITH_SINGLE_SKU_PARENT);
		Set< ? >	dependencies = resolver.getChangeSetDependency(rule);
		assertSame(PRODUCT_DEPENDENCY, 1, dependencies.size());
		// the dependent item is the parent product of the productSku (since the product is not multisku)
		Object	dependent = dependencies.iterator().next();
		assertEquals("object returned should be product", productWithoutMultiSku, dependent);


	}

	
	
	/**
	 * Tests that if the the resolver will return the a product based on the parameters key/values.
	 */
	@Test
	public void testChangeSetDependencyForProduct() {

		final Product product = new ProductImpl();
		addProductCodeToParam();
		
		context.checking(new Expectations() { {
			allowing(productService).findByGuid(PRODUCT_GUID);	 will(returnValue(product));
		} });
		//run the resolver
		Set< ? > dependencies = resolver.getChangeSetDependency(rule);
		assertSame(PRODUCT_DEPENDENCY, 1, dependencies.size());
		Object  dep = dependencies.iterator().next();
		assertEquals("dependent object should be product", product, dep);		
	}
	/**
	 * 	Test category dependency.
	 */
	@Test
	public void testCategory() {
		final Category category = new CategoryImpl();
		addCategoryCodeToParam();
		context.checking(new Expectations() { {
			allowing(categoryService).findByGuid(CATEGORY_UID); will(returnValue(category));
		} });
		
		Set< ? > dependencies = resolver.getChangeSetDependency(rule);
		Object dep = dependencies.iterator().next();
		assertSame("Only 1 category should be dependent", 1, dependencies.size());
		assertEquals("dependent object should be product", category, dep);
		
	}
	/**
	 * Test both product and category dependency.
	 */
	@Test
	public void testCategoryAndProduct() {
		addCategoryCodeToParam();
		addProductCodeToParam();
		
		final Category category = new CategoryImpl();
		final Product product = new ProductImpl();
		context.checking(new Expectations() { {
			allowing(productService).findByGuid(PRODUCT_GUID);	 will(returnValue(product));
			allowing(categoryService).findByGuid(CATEGORY_UID); will(returnValue(category));
		} });
		Set<?> dependencies = resolver.getChangeSetDependency(rule);
		assertSame("Only 2 products should be dependent", 2, dependencies.size());
	}
	
	/** test for get object where object descriptor wrong. **/
	@Test
	public void testWrongObject() {
		
		BusinessObjectDescriptor descriptor = new BusinessObjectDescriptorImpl();
		descriptor.setObjectIdentifier(CATEGORY_UID);
		// use class that isn't a Rule
		Class<?> objectClass = descriptor.getClass();
		Object object = resolver.getObject(descriptor, objectClass);
		assertSame("Object returned should be null", null, object);
	}
	/** test for get object where object descriptor  is correct. **/
	@Test
	public void testCorrectObject() {
		
		BusinessObjectDescriptor descriptor = new BusinessObjectDescriptorImpl();
		descriptor.setObjectIdentifier(CATEGORY_UID);
		Class<?> objectClass = rule.getClass();
		final Rule testObject = new PromotionRuleImpl();
		context.checking(new Expectations() { {
			oneOf(ruleService).findByRuleCode(CATEGORY_UID); will(returnValue(testObject));
		} });
		Object object = resolver.getObject(descriptor, objectClass);
		
		assertSame("Object returned should be 'test object'", testObject, object);
	}
	private void addCategoryCodeToParam() {
		addToParam(parameters, RuleParameter.CATEGORY_CODE_KEY, CATEGORY_UID);
	}
	
	private void addProductCodeToParam() {
		addToParam(parameters, RuleParameter.PRODUCT_CODE_KEY, PRODUCT_GUID);
	}
	private void addToParam(final Set<RuleParameter> parameters, final String key, final String value) {
		RuleParameterImpl productSkuParam = new RuleParameterImpl();
		productSkuParam.setKey(key);
		productSkuParam.setValue(value);
		parameters.add(productSkuParam);
	}
	
	private Set<RuleParameter> createRuleElements() {
		Set<RuleElement> ruleElements =  new LinkedHashSet<RuleElement>();
		Set<RuleParameter> paramsForElem = new LinkedHashSet<RuleParameter>();

		final RuleAction action = new CartSkuPercentDiscountActionImpl();
		action.setParameters(paramsForElem);
		ruleElements.add(action);
		rule.setRuleElements(ruleElements);
		return paramsForElem;
	}
	
}
