package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.common.dto.sellingchannel.impl.ShoppingItemDtoFactoryImpl;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShoppingCartFactoryForTestApplication;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.sellingchannel.director.impl.ShoppingItemAssemblerImpl;
import com.elasticpath.sfweb.formbean.ShoppingCartFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBeanContainer;
import com.elasticpath.sfweb.formbean.impl.ShoppingCartFormBeanImpl;
import com.elasticpath.sfweb.formbean.impl.ShoppingItemFormBeanImpl;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Unit test for {@link ShoppingCartFormBeanFactoryImpl}.
 */
public class ShoppingCartFormBeanFactoryImplTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private MockHttpServletRequest request;


	/**.*/
	@Before
	public void setUp() {
		request = new MockHttpServletRequest();
	}

	/**.*/
	@Test
	public void testCreatingShoppingCartFormBeanWhenShoppingCartIsNull() {
		final ShoppingCartFormBeanFactoryImpl factory = new ShoppingCartFormBeanFactoryImpl();
		final SfRequestHelper requestHelper = context.mock(SfRequestHelper.class);
		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSession();
		factory.setRequestHelper(requestHelper);

		context.checking(new Expectations() { {
			allowing(requestHelper).getCustomerSession(request); will(returnValue(customerSession));
		} });
		Assert.assertEquals(null, factory.createShoppingCartFormBean(request));
	}

	/**.*/
	@Test
	public void testCreatingShoppingCartFormBean() {
		final ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode("A_SKU_CODE");
		final Product product = TestProductBuilder.create()
									.withProductCode("A_CODE")
									.withDefaultSku(sku).build();

		ShoppingCartFormBeanFactoryImpl underTest = new ShoppingCartFormBeanFactoryImpl() {
			@Override ShoppingCartFormBean createFrom(final ShoppingCart shoppingCart) {
				return new ShoppingCartFormBeanImpl();
			}
			@Override protected StoreProduct findProductByGuid(final String productCode, final Store store) {
				return new StoreProductImpl(product);
			}
			@Override
			protected int calculateMinQty(final ShoppingCart shoppingCart, final ShoppingItemFormBean rootFormBean) {
				return 1;
			}
		};

		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		final CartDirector cartDirector = context.mock(CartDirector.class);
		final SfRequestHelper requestHelper = context.mock(SfRequestHelper.class);
		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSession();
		final ShoppingCartImpl cart = TestShoppingCartFactoryForTestApplication.getInstance().createNewCartWithMemento(
				customerSession.getShopper(), new StoreImpl());
		cart.getShoppingCartMemento().getAllItems().add(shoppingItem);
		cart.getShoppingCartMemento().getAllItems().add(shoppingItem);

		context.checking(new Expectations() { {
			ignoring(shoppingItem).getProductSku();
			ignoring(shoppingItem).getQuantity();
			ignoring(shoppingItem).getUidPk();
			ignoring(shoppingItem).getBundleItems();
			ignoring(shoppingItem).getPrice();
			allowing(shoppingItem).getFields(); will(returnValue(new HashMap<String, String>()));
			allowing(beanFactory).getBean(ContextIdNames.SHOPPING_CART_FORM_BEAN); will(returnValue(new ShoppingCartFormBeanImpl()));
			allowing(beanFactory).getBean(ContextIdNames.SHOPPING_ITEM_FORM_BEAN); will(returnValue(new ShoppingItemFormBeanImpl()));
			allowing(cartDirector).isDependent(
					with(cart.getShoppingCartMemento().getAllItems()),
					with(any(ShoppingItem.class)));
					will(returnValue(false));
			allowing(requestHelper).getCustomerSession(request); will(returnValue(customerSession));
		} });

		underTest.setCartDirector(cartDirector);
		ShoppingItemAssembler siAssembler = new ShoppingItemAssemblerImpl() {
			@Override public ShoppingItemDtoFactory getShoppingItemDtoFactory() {
				return new ShoppingItemDtoFactoryImpl();
			}
		};
		underTest.setShoppingItemAssembler(siAssembler);
		underTest.setBeanFactory(beanFactory);
		underTest.setRequestHelper(requestHelper);

		ShoppingItemFormBeanContainer actual = underTest.createShoppingCartFormBean(request);

		Assert.assertEquals(2, actual.getCartItems().size());
	}

	/**.*/
	@Test
	public void testCreateFrom() {
		ShoppingCartFormBeanFactoryImpl factory = new ShoppingCartFormBeanFactoryImpl() {
			@Override
			protected void setPromotionCodesState(final ShoppingCart shoppingCart, final ShoppingCartFormBean formBean) {
				// do nothing
			}
		};

		final ShoppingCart cart = context.mock(ShoppingCart.class);
		final BeanFactory beanFactory = context.mock(BeanFactory.class);

		// this checkings needs to be verified since we need all of them
		context.checking(new Expectations() { {
			allowing(beanFactory).getBean(ContextIdNames.SHOPPING_CART_FORM_BEAN); will(returnValue(new ShoppingCartFormBeanImpl()));
			oneOf(cart).isCodeValid();
			oneOf(cart).isCodeValid();
			oneOf(cart).setCodeValid(true);
			oneOf(cart).getShippingServiceLevelList();
			oneOf(cart).requiresShipping();
			atLeast(1).of(cart).getShippingAddress();
			oneOf(cart).getSubtotalMoney();
			oneOf(cart).getSubtotalDiscountMoney();
			oneOf(cart).isInclusiveTaxCalculationInUse();
			oneOf(cart).isEstimateMode();
			oneOf(cart).getShippingCost();
			oneOf(cart).getBeforeTaxShippingCost();
			oneOf(cart).getBeforeTaxTotal();
			oneOf(cart).getTaxCalculationResult();
			oneOf(cart).getAppliedGiftCertificates();
			oneOf(cart).getGiftCertificateDiscountMoney();
			oneOf(cart).getTotalMoney();
			oneOf(cart).getViewHistory();
			oneOf(cart).getSelectedShippingServiceLevel();
			oneOf(cart).getSelectedShippingServiceLevel();
			oneOf(cart).getLocale();
			oneOf(cart).getCartItems();

		} });

		factory.setBeanFactory(beanFactory);

		factory.createFrom(cart);

	}

	/** */
	public static class TestProductBuilder {
		private final Product product = new ProductImpl();
		private ProductSku defaultSku = new ProductSkuImpl();
		private static final String PRODUCT_CODE = "A_CODE";

		/**
		 * .
		 * @param code .
		 * @return .
		 */
		public TestProductBuilder withProductCode(final String code) {
			this.product.setCode(code);
			return this;
		}

		/**
		 * .
		 * @param sku .
		 * @return .
		 */
		public TestProductBuilder withDefaultSku(final ProductSku sku) {
			this.defaultSku = sku;
			return this;
		}

		/**
		 * .
		 * @return .
		 */
		public Product build() {
			product.setCode(PRODUCT_CODE);
			product.setDefaultSku(defaultSku);
			return product;
		}

		/**
		 * .
		 * @return .
		 */
		public static TestProductBuilder create() {
			return new TestProductBuilder();
		}
	}
}