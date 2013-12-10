package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertEquals;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.InvalidBundleTreeStructureException;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.BundleConstituentFactory;
import com.elasticpath.service.catalog.impl.BundleConstituentFactoryImpl;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.catalogview.impl.StoreProductServiceImpl;
import com.elasticpath.sfweb.controller.impl.ShoppingItemFormBeanContainerFactoryImpl.SelectedItemsBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBeanContainer;
import com.elasticpath.sfweb.formbean.impl.CartFormBeanImpl;
import com.elasticpath.sfweb.formbean.impl.ShoppingItemFormBeanImpl;
import com.elasticpath.sfweb.test.BeanFactoryExpectationsFactory;
import com.elasticpath.sfweb.util.SfRequestHelper;
/**
 * Tests the {@code CartUpdateFormBeanFactoryImpl}.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
public class ShoppingItemFormBeanContainerFactoryImplTest {

	private static final String AAA = "AAA";
	private static final int THREE = 3;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ShoppingItemFormBeanContainerFactoryImpl formBeanFactory;
	private BundleConstituentFactory constituentFactory;
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private MockHttpServletRequest request;


	/** */
	@Before
	public void setUp() {
		formBeanFactory = new ShoppingItemFormBeanContainerFactoryImpl();
		constituentFactory = new BundleConstituentFactoryImpl() {
			@Override
			protected BundleConstituent createBundleConstituentInternal() {
				return new BundleConstituentImpl();
			}
		};
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);

		request = new MockHttpServletRequest();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that, for a normal non-bundle product, we get a CartUpdateFormBean with a single ShoppingItemFormBean.
	 */
	@Test
	public void testNormalProduct() {
		final ShoppingCartImpl shoppingCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = -1285000158367847493L;

			@Override
			public Store getStore() {
				return new StoreImpl();
			}
		};
		final ProductSku productSku = new ProductSkuImpl();
		final StoreProduct product = new StoreProductImpl(new ProductImpl()) {
			private static final long serialVersionUID = 7238859844818517128L;

			@Override
			public ProductSku getDefaultSku() {
				return productSku;
			}
		};

		context.checking(new Expectations() { {
			allowing(beanFactory).getBean(ContextIdNames.SHOPPING_ITEM_FORM_BEAN); will(returnValue(new ShoppingItemFormBeanImpl()));
		} });

		ShoppingItemFormBeanContainerFactoryImpl formBeanFactory = new ShoppingItemFormBeanContainerFactoryImpl() {
			@Override
			protected StoreProduct findProductByGuid(final String productCode, final Store store) {
				return product;
			}

			@Override
			protected int calculateMinQty(final ShoppingCart shoppingCart, final ShoppingItemFormBean rootFormBean) {
				return 1;
			}
		};

		formBeanFactory.setBeanFactory(beanFactory);

		productSku.setSkuCode(AAA);

		ShoppingItemFormBeanContainer cartBean = formBeanFactory.createCartFormBean(product, THREE, shoppingCart);

		List<ShoppingItemFormBean> itemBeanList = cartBean.getCartItems();
		assertEquals("Expect 1 item for the product", 1, itemBeanList.size());

		ShoppingItemFormBean itemBean = itemBeanList.get(0);
		assertEquals("Product should match", product, itemBean.getProduct());
		assertEquals("Quantity should match", THREE, itemBean.getQuantity());
	}

	/** */
	@Test
	public void testAddConstituentShoppingItemFormBeansFromProductIsNotBundle() {
		StoreProduct product = new StoreProductImpl(new ProductImpl());
		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		Store store = context.mock(Store.class);
		final ShoppingCart cart = new ShoppingCartImpl();
		cart.setStore(store);
		formBeanFactory.addConstituentShoppingItemFormBeansFromProduct(cart, rootFormBean, product, "", 0, null);

		assertEquals(0, rootFormBean.getConstituents().size());
	}

	/** */
	@Test
	public void testAddConstituentShoppingItemFormBeansFromProductNoBundleConstituent() {
		StoreProduct bundle = new StoreProductImpl(new ProductBundleImpl());
		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		Store store = new StoreImpl();
		final ShoppingCart cart = new ShoppingCartImpl();
		cart.setStore(store);
		formBeanFactory.addConstituentShoppingItemFormBeansFromProduct(cart,  rootFormBean, bundle, "", 0, null);

		assertEquals(0, rootFormBean.getConstituents().size());
	}

	/**
	 *
	 *
	 * */
	@Test
	public void testAddConstituentShoppingItemFormBeansFromShoppingItemDtoIsNotBundle() {
		StoreProduct product = new StoreProductImpl(new ProductImpl());
		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		//Store store = new StoreImpl();
		ShoppingCart shoppingCart = new ShoppingCartImpl();
		formBeanFactory.
		addConstituentShoppingItemFormBeansFromShoppingItemDto(shoppingCart,
				rootFormBean, null, product, "", 0, Collections.<String, PriceAdjustment>emptyMap());

		assertEquals(0, rootFormBean.getConstituents().size());
	}

	/** */
	@Test
	public void testAddConstituentShoppingItemFormBeansFromShoppingItemDtoNoBundleConstituent() {
		StoreProduct bundle = new StoreProductImpl(new ProductBundleImpl());
		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto("", 0);

		ShoppingCart shoppingCart = new ShoppingCartImpl();
		Store store = new StoreImpl();

		shoppingCart.setStore(store);
		formBeanFactory.addConstituentShoppingItemFormBeansFromShoppingItemDto(shoppingCart,
				rootFormBean, shoppingItemDto, bundle, "", 0, Collections.<String, PriceAdjustment>emptyMap());

		assertEquals(0, rootFormBean.getConstituents().size());
	}

	/** Tests that passing null for the form bean list to addShoppingItemFormBeans returns a form bean with an empty cart item list.*/
	@Test
	public void testAddingNullListOfShoppingItemFormBeans() {
		ShoppingItemFormBeanContainerFactoryImpl factory = new ShoppingItemFormBeanContainerFactoryImpl();
		ShoppingItemFormBeanContainer formBean = new CartFormBeanImpl();

		factory.addShoppingItemFormBeans(formBean, null);
		Assert.assertEquals(0, formBean.getCartItems().size());
	}

	/** Tests that passing 2 item form beans gets copied to the form bean..*/
	@Test
	public void testAddingListOfShoppingItemFormBeans() {
		ShoppingItemFormBeanContainerFactoryImpl factory = new ShoppingItemFormBeanContainerFactoryImpl();
		ShoppingItemFormBeanContainer formBean = new CartFormBeanImpl();

		// add two shopping item form bean
		List<ShoppingItemFormBean> shoppingItemFormBeans = new ArrayList<ShoppingItemFormBean>();
		shoppingItemFormBeans.add(new ShoppingItemFormBeanImpl());
		shoppingItemFormBeans.add(new ShoppingItemFormBeanImpl());

		factory.addShoppingItemFormBeans(formBean, shoppingItemFormBeans);
		Assert.assertEquals(2, formBean.getCartItems().size());
	}

	/** */
	@Test
	public void testAddConstituentShoppingItemFormBeansFromProductWithNestedBundleConstituent() {
		ShoppingItemFormBeanContainerFactoryImpl factory = new ShoppingItemFormBeanContainerFactoryImpl() {
			@Override
			protected ShoppingItemFormBean createEmptyShoppingItemFormBean() {
				return new ShoppingItemFormBeanImpl();
			}
		};

		final StoreProductService storeProductService = new StoreProductServiceImpl() {
			@Override public StoreProduct wrapProduct(final Product product, final Store store) {
				return new StoreProductImpl(product);
			}
		};

		factory.setStoreProductService(storeProductService);

		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
		final StoreProduct bundle = createNestedBundle();


		final ShoppingItemFormBean root = new ShoppingItemFormBeanImpl();
		final Store store = new StoreImpl();
		ShoppingCart cart = new ShoppingCartImpl();
		cart.setStore(store);

		factory.addConstituentShoppingItemFormBeansFromProduct(cart, root,  bundle, "", 1, Collections.<String, PriceAdjustment>emptyMap());

		assertEquals(THREE, root.getConstituents().size());

		ShoppingItemFormBean child1 = root.getConstituents().get(0);
		assertEquals("constituents[0]", child1.getPath());
		assertEquals(1, child1.getLevel());
		assertEquals(0, child1.getConstituents().size());

		ShoppingItemFormBean child2 = root.getConstituents().get(1);
		assertEquals("constituents[1]", child2.getPath());
		assertEquals(1, child2.getLevel());
		assertEquals(0, child2.getConstituents().size());

		ShoppingItemFormBean child21 = root.getConstituents().get(2);
		assertEquals("constituents[1].constituents[0]", child21.getPath());
		assertEquals(2, child21.getLevel());
		assertEquals(0, child21.getConstituents().size());
	}

	private StoreProduct createNestedBundle() {
		int anyQuantity = 1;

		ProductBundle bundle = createSampleBundle();
		bundle.setCalculated(false);
		bundle.addConstituent(constituentFactory.createBundleConstituent(createSampleProduct(), anyQuantity));

		ProductBundle nestedBundle = createSampleBundle();
		nestedBundle.setCalculated(false);
		bundle.addConstituent(constituentFactory.createBundleConstituent(nestedBundle, anyQuantity));
		nestedBundle.addConstituent(constituentFactory.createBundleConstituent(createSampleProduct(), anyQuantity));

		return new StoreProductImpl(bundle);
	}

	private ProductBundle createSampleBundle() {
		ProductBundle product = new ProductBundleImpl();
		ProductSku sku = createSampleSku();
		product.setProductSkus(Collections.singletonMap(sku.getSkuCode(), sku));

		return product;
	}

	private Product createSampleProduct() {
		Product product = new ProductImpl();
		ProductSku sku = createSampleSku();
		product.setProductSkus(Collections.singletonMap(sku.getSkuCode(), sku));

		return product;
	}

	private ProductSku createSampleSku() {
		ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode("sku");

		return sku;
	}

	/** */
	@Test
	public void testCreateShoppingItemFormBean() {

		TestShoppingItemFormBeanContainerFactoryImpl testShoppingItemFormBeanContainerFactoryImpl =
			new TestShoppingItemFormBeanContainerFactoryImpl();

		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
		final StoreProduct bundle = createNestedBundle();

		Store store = new StoreImpl();


		ShoppingItemFormBean formBean =
			testShoppingItemFormBeanContainerFactoryImpl.createShoppingItemFormBean(store, bundle, "AAA", 1, "somepath", 0, 1);

		Assert.assertFalse("the returned form bean should have calculated bundle set to false, for this mock bundle", formBean.isCalculatedBundle());

	}




	/**
	 * Test that the child form bean is populated correctly from the shopping item dto.
	 *
	 */
	@Test
	public void testPopulateChildFormBeanFromShoppingItemDto() {

		// override the container factory to isolate the method under test
		ShoppingItemFormBeanContainerFactoryImpl formBeanContainerFactory =
			new ShoppingItemFormBeanContainerFactoryImpl() {


			@Override
			protected void setChildFormBeanPriceForCalcBundle(final ShoppingCart shoppingCart,
					final BundleConstituent constituent,
					final ShoppingItemFormBean childFormBean) {
				// do nothing
			}


		};

		// use a calculated bundle
		ProductBundle bundle = createSampleBundle();
		bundle.setCalculated(true);

		ShoppingCart shoppingCart = new ShoppingCartImpl();
		ShoppingItemDto dto = new ShoppingItemDto("", 0);
		dto.setPrice(new PriceImpl());
		ShoppingItemFormBean childFormBean = new ShoppingItemFormBeanImpl();



		Map<String, PriceAdjustment> adjustments = new HashMap<String, PriceAdjustment>();
		// populate the map with a mock guid and adjustment
		PriceAdjustment priceAdjustment = context.mock(PriceAdjustment.class);
		String bundleConstituentGuid = "someGuid";
		adjustments.put(bundleConstituentGuid, priceAdjustment);


		final ConstituentItem constituentItem = context.mock(ConstituentItem.class);

		final BundleConstituent constituent = context.mock(BundleConstituent.class);

		final ProductSku productSku = context.mock(ProductSku.class);

		context.checking(new Expectations() { {

			allowing(constituentItem).isProductSku();
			will(returnValue(true));

			allowing(constituentItem).getProductSku();
			will(returnValue(productSku));

			allowing(constituent).getConstituent();
			will(returnValue(constituentItem));

			allowing(constituentItem).isBundle();
			will(returnValue(false));

		} });


		formBeanContainerFactory.populateChildFormBeanFromShoppingItemDto(childFormBean,
				shoppingCart, adjustments, bundle, dto, bundleConstituentGuid, constituent);


		// assert the child form bean has the product sku set
		Assert.assertEquals(productSku, childFormBean.getProductSku());

		// assert the price adjustment is set properly
		Assert.assertEquals(priceAdjustment, childFormBean.getPriceAdjustment());

	}

	/**
	 * Test the population of the chidl form bean from the product.
	 */
	@Test
	public void testPopulateChildFormBeanFromProduct() {

		ShoppingItemFormBeanContainerFactoryImpl formBeanContainerFactory
		= new ShoppingItemFormBeanContainerFactoryImpl() {

			/**
			 * Overridden to return true by default for test purposes
			 */
			@Override
			protected boolean isParentSelected(
					final ShoppingItemFormBean rootFormBean,
					final ShoppingItemFormBean currentFormBean) {

				return true;
			}


		};


		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);

		ShoppingItemFormBean childFormBean = new ShoppingItemFormBeanImpl();

		Map<String, PriceAdjustment> adjustments = new HashMap<String, PriceAdjustment>();
		// populate the map with a mock guid and adjustment
		PriceAdjustment priceAdjustment = context.mock(PriceAdjustment.class);
		final String bundleConstituentGuid = "someGuid";
		adjustments.put(bundleConstituentGuid, priceAdjustment);

		final BundleConstituent bundleConstituent = context.mock(BundleConstituent.class);

		final ConstituentItem constituentItem = context.mock(ConstituentItem.class);

		// use a calculated bundle
		final ProductBundle bundle = createSampleBundle();
		bundle.setCalculated(true);

		final ProductBundle mockProductBundle = context.mock(ProductBundle.class);


		ShoppingItemFormBean rootFormBean = new ShoppingItemFormBeanImpl();



		context.checking(new Expectations() { {

			allowing(bundleConstituent).getConstituent();
			will(returnValue(constituentItem));

			allowing(constituentItem).isProductSku();
			will(returnValue(false));


			allowing(bundleConstituent).getGuid();
			will(returnValue("someGuid"));

			allowing(constituentItem).getProduct();
			will(returnValue(mockProductBundle));

			allowing(constituentItem).isBundle();
			will(returnValue(true));

		} });



		int selectionRule = 0;

		int selectedItems = 2;

		SelectedItemsBean selectedItemBean = formBeanContainerFactory.new SelectedItemsBean(selectedItems, selectionRule);


		formBeanContainerFactory.populateChildFormBeanFromProduct(shoppingCart, rootFormBean, adjustments, bundle,
				selectedItemBean, bundleConstituent, childFormBean);


	}
	/**
	 * Test the mapCartItemsToFormBeansNoDeletedItems will not delete items from the cart.
	 */
	@Test
	public void testMapCartItemsToFormBeansNoDeletedItems() {
		final SfRequestHelper requestHelper = context.mock(SfRequestHelper.class);
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		final ShoppingCart cart =  context.mock(ShoppingCart.class);
		final ShoppingItemFormBeanContainer formBean = new CartFormBeanImpl();
		final List<ShoppingItem> items = new LinkedList<ShoppingItem>();
		final ShoppingItemAssembler shoppingItemAssembler = context.mock(ShoppingItemAssembler.class);
		final  CartDirector  cartDirector = context.mock(CartDirector.class);
		final Shopper shopper = context.mock(Shopper.class);
		items.add(new ShoppingItemImpl());

		context.checking(new Expectations() { {
			allowing(requestHelper).getCustomerSession(request); will(returnValue(customerSession));
			allowing(customerSession).getShopper(); will(returnValue(shopper));
			allowing(shopper).getCurrentShoppingCart(); will(returnValue(cart));
			allowing(cart).getCartItems(); will(returnValue(items));
			never(cart).removeCartItem(with(any(Long.class)));
			ignoring(shoppingItemAssembler);
			ignoring(cartDirector);

		} });

		ShoppingItemFormBeanContainerFactoryImpl formBeanFactory = new ShoppingItemFormBeanContainerFactoryImpl() {
			@Override
			void addShoppingItemFormBeans(final ShoppingItemFormBeanContainer cartFormBean, final List<ShoppingItemFormBean> shoppingItemFormBeans) {
				//No-op
			}
			@Override
			public ShoppingItemFormBeanContainer createCartFormBean(final ShoppingItemDto existingShoppingItemDto, final ShoppingCart shoppingCart,
					final boolean dependent) {
				return new CartFormBeanImpl();
			}

		};

		formBeanFactory.setBeanFactory(beanFactory);
		formBeanFactory.setRequestHelper(requestHelper);
		formBeanFactory.setShoppingItemAssembler(shoppingItemAssembler);
		formBeanFactory.setCartDirector(cartDirector);
		formBeanFactory.mapCartItemsToFormBeans(request, formBean);


	}

	/**
	 * Test the mapCartItemsToFormBeansNoDeletedItems will not delete items from the cart.
	 */
	@Test
	public void testMapCartItemsToFormBeansWillDeletedItems() {
		final SfRequestHelper requestHelper = context.mock(SfRequestHelper.class);
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		final ShoppingCart cart =  context.mock(ShoppingCart.class);
		final ShoppingItemFormBeanContainer formBean = new CartFormBeanImpl();
		final List<ShoppingItem> items = new LinkedList<ShoppingItem>();
		final ShoppingItemAssembler shoppingItemAssembler = context.mock(ShoppingItemAssembler.class);
		final  CartDirector  cartDirector = context.mock(CartDirector.class);
		final ShoppingCart updatedCart = new ShoppingCartImpl();
		final Shopper shopper = context.mock(Shopper.class);
		items.add(new ShoppingItemImpl());

		context.checking(new Expectations() { {
			allowing(requestHelper).getCustomerSession(request); will(returnValue(customerSession));
			allowing(customerSession).getShopper(); will(returnValue(shopper));
			allowing(shopper).getCurrentShoppingCart(); will(returnValue(cart));
			allowing(cart).getCartItems(); will(returnValue(items));
			oneOf(cart).removeCartItem(with(any(Long.class)));
			ignoring(shoppingItemAssembler);
			oneOf(cartDirector).saveShoppingCart(cart); will(returnValue(updatedCart));
			oneOf(cartDirector).isDependent(with(items), with((any(ShoppingItem.class))));
			oneOf(shopper).setCurrentShoppingCart(updatedCart);

		} });

		ShoppingItemFormBeanContainerFactoryImpl formBeanFactory = new ShoppingItemFormBeanContainerFactoryImpl() {
			@Override
			void addShoppingItemFormBeans(final ShoppingItemFormBeanContainer cartFormBean, final List<ShoppingItemFormBean> shoppingItemFormBeans) {
				//no-op
			}
			@Override
			public ShoppingItemFormBeanContainer createCartFormBean(final ShoppingItemDto existingShoppingItemDto, final ShoppingCart shoppingCart,
					final boolean dependent) {
				throw new InvalidBundleTreeStructureException("message");
			}
		};

		formBeanFactory.setBeanFactory(beanFactory);
		formBeanFactory.setRequestHelper(requestHelper);
		formBeanFactory.setShoppingItemAssembler(shoppingItemAssembler);
		formBeanFactory.setCartDirector(cartDirector);
		formBeanFactory.mapCartItemsToFormBeans(request, formBean);
	}

	/**
	 *
	 * Extension class for test purposes.
	 *
	 */
	private class TestShoppingItemFormBeanContainerFactoryImpl extends ShoppingItemFormBeanContainerFactoryImpl {


		/**
		 * Creates an empty {@link ShoppingItemFormBean}.
		 * @return {@link ShoppingItemFormBean}.
		 */
		@Override
		protected ShoppingItemFormBean createEmptyShoppingItemFormBean() {
			return new ShoppingItemFormBeanImpl();
		}


	}

}

